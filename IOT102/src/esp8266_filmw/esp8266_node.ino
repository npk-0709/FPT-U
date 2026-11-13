#include <ESP8266WiFi.h>
#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

// ================= CẤU HÌNH ĐỊNH DANH & PHẦN CỨNG =================
#define NODE_ID             2          // Đồng bộ ID = 2 với Gateway

#define RELAY_ON            HIGH       // Mức tín hiệu kích hoạt Relay
#define RELAY_OFF           LOW        // Mức tín hiệu ngắt Relay

// Ngưỡng đọc ADC của cảm biến độ ẩm đất
#define SOIL_ADC_DRY        1023       // Giá trị ADC khi đất khô hoàn toàn (0%)
#define SOIL_ADC_WET        350        // Giá trị ADC khi đất ướt đẫm (100%)

// Các ngưỡng điều khiển tự động theo kịch bản
#define TANK_EMPTY_LOCK_PCT 20         // Ngưỡng cạn bồn chứa (< 20% sẽ khóa bơm chống cháy)
#define SOIL_START_IRRIG    55         // Độ ẩm đất < 55% -> Bật Bơm 1 (Tưới gốc)
#define SOIL_STOP_IRRIG     70         // Độ ẩm đất >= 70% -> Tắt Bơm 1 (Đạt bão hòa)

#define TEMP_START_MIST     33.0F      // Nhiệt độ > 33.0°C -> Bật Bơm 2 (Phun sương)
#define TEMP_STOP_MIST      30.0F      // Nhiệt độ <= 30.0°C -> Tắt Bơm 2

#define PUMP_STALL_TIMEOUT  180000UL   // Thời gian kiểm tra kẹt bơm/vỡ ống: 3 phút (180.000ms)

// Cấu hình chân giao tiếp LoRa (SX1278 433MHz)
#define LORA_SS             D8
#define LORA_RST            -1
#define LORA_DIO0           -1
#define LORA_FREQ           433E6

// Cấu hình chân Cảm biến & Relay 
#define PIN_I2C_SCL         D1         // Chân SCL cho cảm biến BME280
#define PIN_I2C_SDA         D2         // Chân SDA cho cảm biến BME280
#define PIN_SOIL_AO         A0         // Chân Analog đọc độ ẩm đất
#define PIN_WATER_DO        3          // Chân RX (GPIO3) đọc phao nước bồn chứa
#define PIN_RAIN_DO         D3         // Chân đọc cảm biến mưa (LOW = Có mưa)
#define PIN_RELAY_PUMP1     D0         // Relay điều khiển Máy bơm 1 (Tưới gốc - cũ là Pump2)
#define PIN_RELAY_PUMP2     D4         // Relay điều khiển Máy bơm 2 (Phun sương - cũ là Pump3)

// ================= BIẾN TOÀN CỤC =================
Adafruit_BME280 bme;
bool bmeAvailable = false;

// Biến lưu giá trị cảm biến
float temp = 30.0;
float hum = 65.0;
float press = 1010.0;
int soilMoisturePct = 62;
int waterLevelPct = 85;
bool isRaining = false;

// Trạng thái hoạt động của 2 máy bơm
bool pump1State = false;               // Trạng thái Máy bơm 1 (Tưới gốc)
bool pump2State = false;               // Trạng thái Máy bơm 2 (Phun sương)

// Các cờ khóa bảo vệ an toàn
bool dryRunLocked = false;             // Khóa chống cháy bơm khi cạn nước bồn
bool stormPreWarning = false;          // Cờ cảnh báo bão sớm
bool pumpFaultLock = false;            // Khóa vĩnh viễn khi phát hiện kẹt bơm / vỡ ống

// Biến điều khiển từ xa qua Web/Gateway
String remoteOverride = "AUTO";        // Chế độ: "AUTO" hoặc "MANUAL"
bool manualPump1 = false;
bool manualPump2 = false;
bool manualAlarm = false;              // Cờ báo động gửi từ Web (thay cho còi vật lý)

// Biến phục vụ giám sát lưu lượng đường ống (Case 5)
unsigned long pump1StartTime = 0;      // Thời điểm bắt đầu bật Bơm 1
int soilAtPump1Start = 0;              // Độ ẩm đất tại thời điểm bắt đầu bật Bơm 1

// Mã trạng thái hệ thống
String statusCode = "NORMAL";
String lastSentStatus = "NORMAL";

// Biến định thời (Timer)
unsigned long lastSensorEvalTime = 0;
unsigned long lastEmergencyTxTime = 0;
unsigned long lastPeriodicTxTime = 0;

// ================= HÀM ĐỌC CẢM BIẾN =================

// Đọc trung bình 10 lần cảm biến độ ẩm đất và quy đổi ra phần trăm (0 - 100%)
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

// Thu thập toàn bộ dữ liệu từ các cảm biến
void readAllSensors() {
  soilMoisturePct = readSoilMoisturePercentage();

  if (bmeAvailable) {
    float t = bme.readTemperature();
    float p = bme.readPressure() / 100.0F;
    if (!isnan(t)) temp = t;
    if (!isnan(p) && p > 800.0F) press = p;
  }

  hum = (float)soilMoisturePct;

  // Đọc cảm biến phao nước: HIGH = Còn nước (85%), LOW = Cạn nước (15%)
  if (digitalRead(PIN_WATER_DO) == HIGH) {
    waterLevelPct = 85;
  } else {
    waterLevelPct = 15;
  }

  // Cảm biến mưa: Mức LOW nghĩa là có nước mưa trên bề mặt cảm biến
  isRaining = (digitalRead(PIN_RAIN_DO) == LOW);
}

// ================= HÀM ĐIỀU KHIỂN MÁY BƠM =================
void setPumps(bool turnOnPump1, bool turnOnPump2) {
  // Nếu đang bị khóa do cạn nước bồn, cưỡng bức tắt toàn bộ bơm
  if (dryRunLocked) {
    pump1State = false;
    pump2State = false;
    digitalWrite(PIN_RELAY_PUMP1, RELAY_OFF);
    digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);
    return;
  }

  // Ghi nhận mốc thời gian và độ ẩm đất ban đầu khi Bơm 1 chuyển từ TẮT sang BẬT (Phục vụ Case 5)
  if (turnOnPump1 && !pump1State) {
    pump1StartTime = millis();
    soilAtPump1Start = soilMoisturePct;
  }

  pump1State = turnOnPump1;
  pump2State = turnOnPump2;

  digitalWrite(PIN_RELAY_PUMP1, pump1State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
}

// ================= HÀM ĐÁNH GIÁ KỊCH BẢN (LOGIC CHÍNH) =================
void evaluateNodeCases() {
  readAllSensors();

  // 1. KIỂM TRA LỖI KẸT BƠM / VỠ ỐNG DẪN (CASE 5)
  // Nếu Bơm 1 đã chạy quá 3 phút (180.000ms) mà độ ẩm đất tăng không đủ 3%
  if (pump1State && (millis() - pump1StartTime > PUMP_STALL_TIMEOUT)) {
    if ((soilMoisturePct - soilAtPump1Start) < 3) {
      setPumps(false, false);
      pumpFaultLock = true;            // Khóa vĩnh viễn cụm bơm
      statusCode = "CRITICAL_PUMP_STALL";
      return;
    } else {
      // Nếu độ ẩm vẫn tăng bình thường, làm mới lại bộ đếm 3 phút tiếp theo
      pump1StartTime = millis();
      soilAtPump1Start = soilMoisturePct;
    }
  }

  // Duy trì trạng thái khóa vĩnh viễn nếu đã xảy ra lỗi đường ống trước đó
  if (pumpFaultLock) {
    setPumps(false, false);
    statusCode = "CRITICAL_PUMP_STALL";
    return;
  }

  // 2. KIỂM TRA MỰC NƯỚC BỒN CHỨA (CASE 3: CHỐNG CHÁY BƠM)
  if (waterLevelPct < TANK_EMPTY_LOCK_PCT) {
    dryRunLocked = true;
    setPumps(false, false);
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  } else {
    dryRunLocked = false;
  }

  // 3. CHẾ ĐỘ ĐIỀU KHIỂN THỦ CÔNG TỪ XA (MANUAL)
  if (remoteOverride == "MANUAL") {
    setPumps(manualPump1, manualPump2);
    statusCode = (manualPump1 || manualPump2) ? "AUTO_IRRIGATING" : "NORMAL";
    return;
  }

  // 4. KIỂM TRA ĐIỀU KIỆN THỜI TIẾT MƯA / BÃO (CASE 4)
  if (isRaining) {
    setPumps(false, false);
    statusCode = "STOP_BY_RAIN";
  } 
  else if (stormPreWarning) {
    setPumps(false, false);
    statusCode = "HOLD_FOR_STORM";
  } 
  // 5. CHẾ ĐỘ TƯỚI VÀ LÀM MÁT TỰ ĐỘNG (CASE 1 & CASE 2)
  else {
    bool nextPump1 = pump1State;
    bool nextPump2 = pump2State;

    // Logic Bơm 1 (Tưới gốc - Case 1): Bật khi < 55%, Tắt khi >= 70%
    if (soilMoisturePct < SOIL_START_IRRIG) {
      nextPump1 = true;
    } else if (soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump1 = false;
    }

    // Logic Bơm 2 (Phun sương hạ nhiệt - Case 2): Bật khi > 33°C, Tắt khi <= 30°C
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

// Đóng gói và gửi dữ liệu lên Gateway (Gồm 14 trường dữ liệu)
void sendLoRaPacket() {
  // Cờ báo động ảo gửi về Gateway (để Gateway hú còi và báo Web)
  int alarmState = (manualAlarm || statusCode == "CRITICAL_PUMP_STALL" || statusCode == "ERR_DRY_RUN_LOCK") ? 1 : 0;
  int modeInt = (remoteOverride == "AUTO") ? 1 : 0;

  // Cấu trúc gói tin: UP,ID,Temp,Hum,Press,Water,Soil,Rain,Pump1,Pump2,Alarm,Mode,StatusCode,SoilStart
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

// Xử lý lệnh điều khiển từ Gateway gửi xuống (CMD,ID,Mode,Pump1,Pump2,Alarm)
void handleGatewayCommand(String cmd) {
  cmd.trim();
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
      pumpFaultLock = false;           // Chuyển sang AUTO trên Web sẽ giúp mở khóa lỗi kẹt bơm
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
  // Tắt WiFi trên ESP8266 để tiết kiệm năng lượng tối đa cho nguồn NLMT
  WiFi.mode(WIFI_OFF);
  WiFi.forceSleepBegin();
  delay(1);

  // Chuyển chức năng chân RX (GPIO3) sang GPIO thông thường để đọc phao nước
  pinMode(PIN_WATER_DO, FUNCTION_3);

  pinMode(PIN_WATER_DO, INPUT_PULLUP);
  pinMode(PIN_RAIN_DO, INPUT_PULLUP);
  
  pinMode(PIN_RELAY_PUMP1, OUTPUT);
  pinMode(PIN_RELAY_PUMP2, OUTPUT);

  // Đảm bảo tắt tất cả máy bơm khi mới khởi động
  digitalWrite(PIN_RELAY_PUMP1, RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);

  // Khởi tạo giao tiếp I2C và cảm biến BME280
  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76)) {
    bmeAvailable = true;
  } else if (bme.begin(0x77)) {
    bmeAvailable = true;
  }

  // Khởi tạo module LoRa SX1278
  SPI.begin();
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(LORA_FREQ)) {
    // Nếu lỗi phần cứng LoRa, treo vòng lặp chờ watchdog reset (đã bỏ nháy còi)
    while (1) {
      delay(500);
    }
  }

  // Cấu hình thông số thu phát LoRa đồng bộ với Gateway
  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
}

// ================= VÒNG LẶP CHÍNH =================
void loop() {
  unsigned long now = millis();

  // Định kỳ mỗi 1 giây: Đọc cảm biến và đánh giá kịch bản
  if (now - lastSensorEvalTime >= 1000) {
    lastSensorEvalTime = now;
    evaluateNodeCases();

    // Nếu có sự thay đổi trạng thái (kể cả về lại NORMAL), lập tức gửi gói tin LoRa ngay
    if (statusCode != lastSentStatus) {
      if (now - lastEmergencyTxTime > 1000) {
        sendLoRaPacket();
        lastSentStatus = statusCode;
        lastEmergencyTxTime = now;
        lastPeriodicTxTime = now;
      }
    }
  }

  // Định kỳ mỗi 5 giây: Gửi gói tin cập nhật thông số về Gateway
  if (now - lastPeriodicTxTime >= 5000) {
    lastPeriodicTxTime = now;
    sendLoRaPacket();
  }

  // Lắng nghe lệnh điều khiển từ Gateway gửi xuống
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    handleGatewayCommand(incoming);
  }
}