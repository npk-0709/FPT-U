#include <ESP8266WiFi.h>
#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

// ================= CẤU HÌNH ĐỊNH DANH & PHẦN CỨNG =================
#define NODE_ID             2          

#define RELAY_ON            HIGH       
#define RELAY_OFF           LOW        

#define SOIL_ADC_DRY        1023       
#define SOIL_ADC_WET        350        

// DYNAMIC CONFIG VARIABLES (Lấy từ Server, Không dùng #define cứng nữa)
int TANK_EMPTY_LOCK_PCT = 20;
int SOIL_START_IRRIG = 55;
int SOIL_STOP_IRRIG = 70;
float TEMP_START_MIST = 33.0;
float TEMP_STOP_MIST = 30.0;
unsigned long PUMP_STALL_TIMEOUT = 180000;

// Cấu hình chân giao tiếp LoRa (SX1278 433MHz)
#define LORA_SS             D8
#define LORA_RST            -1
#define LORA_DIO0           -1
#define LORA_FREQ           433E6

// Cấu hình chân Cảm biến & Relay 
#define PIN_I2C_SCL         D1         
#define PIN_I2C_SDA         D2         
#define PIN_SOIL_AO         A0         
#define PIN_WATER_DO        3          
#define PIN_RAIN_DO         D3         
#define PIN_RELAY_PUMP1     D0         
#define PIN_RELAY_PUMP2     D4         

// ================= BIẾN TOÀN CỤC =================
Adafruit_BME280 bme;
bool bmeAvailable = false;
bool isSensorError = false;            // Cờ phát hiện lỗi hỏng/đứt dây cảm biến

float temp = 30.0;
float hum = 65.0;
float press = 1010.0;
int soilMoisturePct = 62;
int waterLevelPct = 85;
bool isRaining = false;

bool pump1State = false;               
bool pump2State = false;               

bool dryRunLocked = false;             
bool stormPreWarning = false;          
bool pumpFaultLock = false;            

String remoteOverride = "AUTO";        
bool manualPump1 = false;
bool manualPump2 = false;
bool manualAlarm = false;              

unsigned long pump1StartTime = 0;      
int soilAtPump1Start = 0;              

String statusCode = "NORMAL";
String lastSentStatus = "NORMAL";

unsigned long lastSensorEvalTime = 0;
unsigned long lastEmergencyTxTime = 0;
unsigned long lastPeriodicTxTime = 0;

// ================= HÀM ĐỌC CẢM BIẾN =================
int readSoilMoisturePercentage() {
  long sum = 0;
  for (int i = 0; i < 10; i++) {
    sum += analogRead(PIN_SOIL_AO);
    delay(2);
  }
  int avgRaw = sum / 10;
  int pct = map(avgRaw, SOIL_ADC_DRY, SOIL_ADC_WET, 0, 100);
  return constrain(pct, 0, 100);
}

void readAllSensors() {
  soilMoisturePct = readSoilMoisturePercentage();
  
  isSensorError = !bmeAvailable;
  
  if (bmeAvailable) {
    float t = bme.readTemperature();
    float p = bme.readPressure() / 100.0F;
    
    // Nếu cảm biến hỏng/cháy mạch trong quá trình chạy sẽ trả về NaN
    if (isnan(t) || isnan(p)) {
      isSensorError = true;
    } else {
      temp = t;
      if (p > 800.0F) press = p;
    }
  }

  hum = (float)soilMoisturePct;

  if (digitalRead(PIN_WATER_DO) == HIGH) {
    waterLevelPct = 85;
  } else {
    waterLevelPct = 15;
  }
  isRaining = (digitalRead(PIN_RAIN_DO) == LOW);
}

// ================= HÀM ĐIỀU KHIỂN MÁY BƠM =================
void setPumps(bool turnOnPump1, bool turnOnPump2) {
  if (dryRunLocked || isSensorError) {
    pump1State = false;
    pump2State = false;
    digitalWrite(PIN_RELAY_PUMP1, RELAY_OFF);
    digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);
    return;
  }

  if (turnOnPump1 && !pump1State) {
    pump1StartTime = millis();
    soilAtPump1Start = soilMoisturePct;
  }

  pump1State = turnOnPump1;
  pump2State = turnOnPump2;

  digitalWrite(PIN_RELAY_PUMP1, pump1State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
}

// ================= HÀM ĐÁNH GIÁ KỊCH BẢN =================
void evaluateNodeCases() {
  readAllSensors();

  // 0. KIỂM TRA LỖI PHẦN CỨNG CẢM BIẾN (MỚI THÊM)
  if (isSensorError) {
    setPumps(false, false);
    statusCode = "ERR_SENSOR";
    return;
  }

  // 1. KIỂM TRA LỖI KẸT BƠM / VỠ ỐNG DẪN (CASE 5)
  if (pump1State && (millis() - pump1StartTime > PUMP_STALL_TIMEOUT)) {
    if ((soilMoisturePct - soilAtPump1Start) < 3) {
      setPumps(false, false);
      pumpFaultLock = true;            
      statusCode = "CRITICAL_PUMP_STALL";
      return;
    } else {
      pump1StartTime = millis();
      soilAtPump1Start = soilMoisturePct;
    }
  }

  if (pumpFaultLock) {
    setPumps(false, false);
    statusCode = "CRITICAL_PUMP_STALL";
    return;
  }

  // 2. KIỂM TRA MỰC NƯỚC BỒN CHỨA (CASE 3)
  if (waterLevelPct < TANK_EMPTY_LOCK_PCT) {
    dryRunLocked = true;
    setPumps(false, false);
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  } else {
    dryRunLocked = false;
  }

  // 3. ĐIỀU KHIỂN THỦ CÔNG (MANUAL)
  if (remoteOverride == "MANUAL") {
    setPumps(manualPump1, manualPump2);
    statusCode = (manualPump1 || manualPump2) ? "AUTO_IRRIGATING" : "NORMAL";
    return;
  }

  // 4. KIỂM TRA THỜI TIẾT MƯA / BÃO (CASE 4)
  if (isRaining) {
    setPumps(false, false);
    statusCode = "STOP_BY_RAIN";
  } 
  else if (stormPreWarning) {
    setPumps(false, false);
    statusCode = "HOLD_FOR_STORM";
  } 
  // 5. TƯỚI VÀ LÀM MÁT TỰ ĐỘNG (CASE 1 & CASE 2)
  else {
    bool nextPump1 = pump1State;
    bool nextPump2 = pump2State;

    if (soilMoisturePct < SOIL_START_IRRIG) {
      nextPump1 = true;
    } else if (soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump1 = false;
    }

    if (temp > TEMP_START_MIST && soilMoisturePct < SOIL_STOP_IRRIG) {
      nextPump2 = true;
    } else if (temp <= TEMP_STOP_MIST || soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump2 = false;
    }

    setPumps(nextPump1, nextPump2);

    if (pump1State || pump2State) {
      statusCode = "AUTO_IRRIGATING";
    } else {
      statusCode = "NORMAL";
    }
  }
}

// ================= HÀM TRUYỀN THÔNG LORA =================
void sendLoRaPacket() {
  int alarmState = (manualAlarm || statusCode == "CRITICAL_PUMP_STALL" || statusCode == "ERR_DRY_RUN_LOCK" || statusCode == "ERR_SENSOR") ? 1 : 0;
  int modeInt = (remoteOverride == "AUTO") ? 1 : 0;

  String payload = "UP," + String(NODE_ID) + "," +
                   String(temp, 1) + "," +
                   String(soilMoisturePct, 1) + "," +
                   String(press, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(soilMoisturePct) + "," +
                   String(isRaining ? 1 : 0) + "," +
                   String(pump1State ? 1 : 0) + "," +
                   String(pump2State ? 1 : 0) + "," +
                   String(alarmState) + "," +
                   String(modeInt) + "," +
                   statusCode + "," +
                   String(soilAtPump1Start);

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();
}

void handleGatewayCommand(String cmd) {
  cmd.trim();
  
  // NẾU LÀ GÓI TIN ĐỒNG BỘ CẤU HÌNH (CFG) TỪ SERVER
  if (cmd.startsWith("CFG,2,")) {
    int p1 = cmd.indexOf(',');
    int p2 = cmd.indexOf(',', p1 + 1);
    int p3 = cmd.indexOf(',', p2 + 1);
    int p4 = cmd.indexOf(',', p3 + 1);
    int p5 = cmd.indexOf(',', p4 + 1);
    int p6 = cmd.indexOf(',', p5 + 1);
    int p7 = cmd.indexOf(',', p6 + 1);

    if (p7 != -1) {
      TANK_EMPTY_LOCK_PCT = cmd.substring(p2 + 1, p3).toInt();
      SOIL_START_IRRIG    = cmd.substring(p3 + 1, p4).toInt();
      SOIL_STOP_IRRIG     = cmd.substring(p4 + 1, p5).toInt();
      TEMP_START_MIST     = cmd.substring(p5 + 1, p6).toFloat();
      TEMP_STOP_MIST      = cmd.substring(p6 + 1, p7).toFloat();
      PUMP_STALL_TIMEOUT  = cmd.substring(p7 + 1).toInt();
    }
    return;
  }

  // NẾU LÀ LỆNH ĐIỀU KHIỂN (CMD) TỪ SERVER
  String myPrefix = "CMD," + String(NODE_ID) + ",";
  if (!cmd.startsWith(myPrefix)) return;

  int p1 = cmd.indexOf(',');
  int p2 = cmd.indexOf(',', p1 + 1);
  int p3 = cmd.indexOf(',', p2 + 1);
  int p4 = cmd.indexOf(',', p3 + 1);
  int p5 = cmd.indexOf(',', p4 + 1);

  if (p2 != -1 && p3 != -1 && p4 != -1 && p5 != -1) {
    int mode  = cmd.substring(p2 + 1, p3).toInt();
    int pump1 = cmd.substring(p3 + 1, p4).toInt();
    int pump2 = cmd.substring(p4 + 1, p5).toInt();
    int alarm = cmd.substring(p5 + 1).toInt();

    if (mode == 1) {
      remoteOverride = "AUTO";
      pumpFaultLock = false;           
      manualAlarm = (alarm == 1);
    } else {
      remoteOverride = "MANUAL";
      manualPump1 = (pump1 == 1);
      manualPump2 = (pump2 == 1);
      manualAlarm = (alarm == 1);
    }
  }

  evaluateNodeCases();
  delay(50);
  sendLoRaPacket();
  lastSentStatus = statusCode;
  lastPeriodicTxTime = millis();
}

// ================= KHỞI TẠO HỆ THỐNG =================
void setup() {
  WiFi.mode(WIFI_OFF);
  WiFi.forceSleepBegin();
  delay(1);

  pinMode(PIN_WATER_DO, FUNCTION_3);

  pinMode(PIN_WATER_DO, INPUT_PULLUP);
  pinMode(PIN_RAIN_DO, INPUT_PULLUP);
  
  pinMode(PIN_RELAY_PUMP1, OUTPUT);
  pinMode(PIN_RELAY_PUMP2, OUTPUT);

  digitalWrite(PIN_RELAY_PUMP1, RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);

  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76)) {
    bmeAvailable = true;
  } else if (bme.begin(0x77)) {
    bmeAvailable = true;
  }

  SPI.begin();
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(LORA_FREQ)) {
    while (1) {
      delay(500);
    }
  }

  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
}

// ================= VÒNG LẶP CHÍNH =================
void loop() {
  unsigned long now = millis();

  if (now - lastSensorEvalTime >= 1000) {
    lastSensorEvalTime = now;
    evaluateNodeCases();

    if (statusCode != lastSentStatus) {
      if (now - lastEmergencyTxTime > 1000) {
        sendLoRaPacket();
        lastSentStatus = statusCode;
        lastEmergencyTxTime = now;
        lastPeriodicTxTime = now;
      }
    }
  }

  if (now - lastPeriodicTxTime >= 5000) {
    lastPeriodicTxTime = now;
    sendLoRaPacket();
  }

  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    handleGatewayCommand(incoming);
  }
}