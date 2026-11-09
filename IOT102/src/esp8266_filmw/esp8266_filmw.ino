#include <ESP8266WiFi.h>
#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

#define NODE_ID             1

#define RELAY_ON            HIGH
#define RELAY_OFF           LOW
#define BUZZER_ON           HIGH
#define BUZZER_OFF          LOW

#define SOIL_ADC_DRY        1023
#define SOIL_ADC_WET        350

#define TANK_EMPTY_LOCK_PCT 20
#define SOIL_START_IRRIG    55
#define SOIL_STOP_IRRIG     70

#define TEMP_START_MIST     33.0F
#define TEMP_STOP_MIST      30.0F

#define PUMP_STALL_TIMEOUT  180000UL 

#define LORA_SS             D8
#define LORA_RST            -1
#define LORA_DIO0           -1
#define LORA_FREQ           433E6

#define PIN_I2C_SCL         D1
#define PIN_I2C_SDA         D2
#define PIN_SOIL_AO         A0
#define PIN_WATER_DO        3
#define PIN_RAIN_DO         D3
#define PIN_RELAY_PUMP2     D0
#define PIN_RELAY_PUMP3     D4
#define PIN_BUZZER          1

Adafruit_BME280 bme;
bool bmeAvailable = false;

float temp = 30.0;
float hum = 65.0;
float press = 1010.0;
int soilMoisturePct = 60;
int waterLevelPct = 85;
bool isRaining = false;

bool pump2State = false;
bool pump3State = false;

bool dryRunLocked = false;
bool stormPreWarning = false;
bool pumpFaultLock = false;

String remoteOverride = "AUTO";
bool manualPump2 = false;
bool manualPump3 = false;
bool manualBuzzer = false;

unsigned long pump2StartTime = 0;
int soilAtPump2Start = 0;

String statusCode = "NORMAL";
String lastSentStatus = "NORMAL";

unsigned long lastSensorEvalTime = 0;
unsigned long lastEmergencyTxTime = 0;
unsigned long lastPeriodicTxTime = 0;

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

  if (bmeAvailable) {
    float t = bme.readTemperature();
    float p = bme.readPressure() / 100.0F;
    if (!isnan(t)) temp = t;
    if (!isnan(p) && p > 800.0F) press = p;
  }

  hum = (float)soilMoisturePct;

  if (digitalRead(PIN_WATER_DO) == HIGH) {
    waterLevelPct = 85;
  } else {
    waterLevelPct = 15;
  }

  isRaining = (digitalRead(PIN_RAIN_DO) == LOW);
}

void setPumps(bool turnOnPump2, bool turnOnPump3) {
  if (dryRunLocked) {
    pump2State = false;
    pump3State = false;
    digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);
    digitalWrite(PIN_RELAY_PUMP3, RELAY_OFF);
    return;
  }

  if (turnOnPump2 && !pump2State) {
    pump2StartTime = millis();
    soilAtPump2Start = soilMoisturePct;
  }

  pump2State = turnOnPump2;
  pump3State = turnOnPump3;

  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP3, pump3State ? RELAY_ON : RELAY_OFF);
}

void updateBuzzerPattern() {
  unsigned long now = millis();

  if (manualBuzzer || statusCode == "CRITICAL_PUMP_STALL") {
    digitalWrite(PIN_BUZZER, BUZZER_ON);
  } 
  else if (statusCode == "ERR_DRY_RUN_LOCK") {
    unsigned long cycle = now % 5000;
    bool doubleBeep = (cycle < 150) || (cycle >= 300 && cycle < 450);
    digitalWrite(PIN_BUZZER, doubleBeep ? BUZZER_ON : BUZZER_OFF);
  } 
  else {
    digitalWrite(PIN_BUZZER, BUZZER_OFF);
  }
}

void evaluateNodeCases() {
  readAllSensors();

  if (pump2State && (millis() - pump2StartTime > PUMP_STALL_TIMEOUT)) {
    if ((soilMoisturePct - soilAtPump2Start) < 3) {
      setPumps(false, false);
      pumpFaultLock = true;
      statusCode = "CRITICAL_PUMP_STALL";
      return;
    } else {
      pump2StartTime = millis();
      soilAtPump2Start = soilMoisturePct;
    }
  }

  if (pumpFaultLock) {
    setPumps(false, false);
    statusCode = "CRITICAL_PUMP_STALL";
    return;
  }

  if (waterLevelPct < TANK_EMPTY_LOCK_PCT) {
    dryRunLocked = true;
    setPumps(false, false);
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  } else {
    dryRunLocked = false;
  }

  if (remoteOverride == "MANUAL") {
    setPumps(manualPump2, manualPump3);
    statusCode = (manualPump2 || manualPump3) ? "AUTO_IRRIGATING" : "NORMAL";
    return;
  }

  if (isRaining) {
    setPumps(false, false);
    statusCode = "STOP_BY_RAIN";
  } 
  else if (stormPreWarning) {
    setPumps(false, false);
    statusCode = "HOLD_FOR_STORM";
  } 
  else {
    bool nextPump2 = pump2State;
    bool nextPump3 = pump3State;

    if (soilMoisturePct < SOIL_START_IRRIG) {
      nextPump2 = true;
    } else if (soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump2 = false;
    }

    if (temp > TEMP_START_MIST && soilMoisturePct < SOIL_STOP_IRRIG) {
      nextPump3 = true;
    } else if (temp <= TEMP_STOP_MIST || soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump3 = false;
    }

    setPumps(nextPump2, nextPump3);

    if (pump2State || pump3State) {
      statusCode = "AUTO_IRRIGATING";
    } else {
      statusCode = "NORMAL";
    }
  }
}

void sendLoRaPacket() {
  int buzzState = (manualBuzzer || statusCode == "CRITICAL_PUMP_STALL" || statusCode == "ERR_DRY_RUN_LOCK") ? 1 : 0;
  int modeInt = (remoteOverride == "AUTO") ? 1 : 0;

  String payload = "UP," + String(NODE_ID) + "," +
                   String(temp, 1) + "," +
                   String(soilMoisturePct, 1) + "," +
                   String(press, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(soilMoisturePct) + "," +
                   String(isRaining ? 1 : 0) + "," +
                   String(pump2State ? 1 : 0) + "," +
                   String(pump3State ? 1 : 0) + "," +
                   String(buzzState) + "," +
                   String(modeInt);

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();
}

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
    int pump2 = cmd.substring(p3 + 1, p4).toInt();
    int pump3 = cmd.substring(p4 + 1, p5).toInt();
    int buzz2 = cmd.substring(p5 + 1).toInt();

    if (mode == 1) {
      remoteOverride = "AUTO";
      pumpFaultLock = false;
      manualBuzzer = (buzz2 == 1);
    } else {
      remoteOverride = "MANUAL";
      manualPump2 = (pump2 == 1);
      manualPump3 = (pump3 == 1);
      manualBuzzer = (buzz2 == 1);
    }
  }

  evaluateNodeCases();
  updateBuzzerPattern();
  delay(50);
  sendLoRaPacket();
  lastSentStatus = statusCode;
  lastPeriodicTxTime = millis();
}

void setup() {
  WiFi.mode(WIFI_OFF);
  WiFi.forceSleepBegin();
  delay(1);

  pinMode(PIN_BUZZER, FUNCTION_3);
  pinMode(PIN_WATER_DO, FUNCTION_3);

  pinMode(PIN_WATER_DO, INPUT_PULLUP);
  pinMode(PIN_RAIN_DO, INPUT_PULLUP);
  
  pinMode(PIN_RELAY_PUMP2, OUTPUT);
  pinMode(PIN_RELAY_PUMP3, OUTPUT);
  pinMode(PIN_BUZZER, OUTPUT);

  digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP3, RELAY_OFF);
  digitalWrite(PIN_BUZZER, BUZZER_OFF);

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
      digitalWrite(PIN_BUZZER, !digitalRead(PIN_BUZZER));
      delay(300);
    }
  }

  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
}

void loop() {
  unsigned long now = millis();

  updateBuzzerPattern();

  if (now - lastSensorEvalTime >= 1000) {
    lastSensorEvalTime = now;
    evaluateNodeCases();

    if (statusCode != lastSentStatus && statusCode != "NORMAL") {
      if (now - lastEmergencyTxTime > 1500) {
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