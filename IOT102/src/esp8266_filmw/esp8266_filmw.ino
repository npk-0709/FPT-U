/*
 * ============================================================================
 * DỰ ÁN IOT LORA 433MHZ - CODE HOÀN CHỈNH CHO NODE 2 (TRẠM TƯỚI TIÊU & BỒN CHỨA)
 * Vi điều khiển : ESP8266 (NodeMCU ESP-12E)
 * Giao tiếp     : LoRa Ra-01 SX1278 (433MHz) <-> Gateway ESP32-S3-WROOM-1
 * Cập nhật      : Thay thế độ ẩm không khí bằng Cảm biến độ ẩm đất (A0),
 *                 Bổ sung Relay 3 (Bơm phun sương - D4), CB Mực nước (RX), Còi (TX)
 * ============================================================================
 */

#include <ESP8266WiFi.h>
#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

// ================= 1. ĐỊNH DANH NODE & CẤU HÌNH PHẦN CỨNG =================
#define NODE_ID             2       // Cố định là 2 cho Trạm Tưới Tiêu & Bồn Chứa

// Cấu hình mức kích của Module Relay và Còi (Đổi HIGH/LOW nếu dùng mạch kích âm)
#define RELAY_ON            HIGH
#define RELAY_OFF           LOW
#define BUZZER_ON           HIGH
#define BUZZER_OFF          LOW

// Hiệu chuẩn Cảm biến độ ẩm đất (Chân A0 - ADC0)
// Thông thường cảm biến độ ẩm đất: Khô hoàn toàn trả về ~1024, Ngập nước trả về ~350
#define SOIL_ADC_DRY        1023    // Giá trị A0 khi đất khô cằn (0%)
#define SOIL_ADC_WET        350     // Giá trị A0 khi đất ướt đẫm (100%)

// Các ngưỡng vận hành thực tế của Node 2 (Sử dụng Độ ẩm đất thay cho Độ ẩm không khí)
#define TANK_EMPTY_LOCK_PCT 20      // Mực nước bồn < 20% (RX = LOW -> 15%): Khóa cứng máy bơm chống cháy
#define SOIL_START_IRRIG    55      // Độ ẩm đất < 55%: Đất khô -> Bật bơm tưới gốc (Bơm 2)
#define SOIL_STOP_IRRIG     70      // Độ ẩm đất >= 70%: Đất đủ ẩm -> Tắt bơm tưới gốc (Bơm 2)

#define TEMP_START_MIST     33.0F   // Nhiệt độ > 33.0°C (Trời nắng nóng) -> Bật bơm phun sương (Bơm 3)
#define TEMP_STOP_MIST      30.0F   // Nhiệt độ <= 30.0°C (Trời mát) -> Tắt bơm phun sương (Bơm 3)

// Thời gian kiểm tra kẹt bơm / vỡ ống (Trường hợp 5): 3 phút (180000ms)
// Kiểm tra nếu Bơm 2 chạy 3 phút mà độ ẩm đất không tăng thêm ít nhất 3%
#define PUMP_STALL_TIMEOUT  180000UL 

// ================= 2. ĐỊNH NGHĨA CHÂN GPIO TRÊN ESP8266 =================
#define LORA_SS             D8      // GPIO15 (NSS/CS)
#define LORA_RST            -1      // Nối chung chân RST của ESP8266
#define LORA_DIO0           -1      // Dùng chế độ Polling LoRa.parsePacket()
#define LORA_FREQ           433E6   // Tần số 433MHz

#define PIN_I2C_SCL         D1      // GPIO5  - BME280 SCL
#define PIN_I2C_SDA         D2      // GPIO4  - BME280 SDA
#define PIN_SOIL_AO         A0      // ADC0   - Cảm biến độ ẩm đất (Analog)
#define PIN_WATER_DO        3       // RX (GPIO3) - Cảm biến mực nước (HIGH = ~85%, LOW = ~15%)
#define PIN_RAIN_DO         D3      // GPIO0  - Cảm biến mưa (LOW = Có mưa, HIGH = Khô ráo)
#define PIN_RELAY_PUMP2     D0      // GPIO16 - Relay 2: Máy bơm 2 (12V Bơm tưới gốc)
#define PIN_RELAY_PUMP3     D4      // GPIO2  - Relay 3: Máy bơ m 3 (12V Bơm phun sương)
#define PIN_BUZZER          1       // TX (GPIO1 - FUNCTION_3) - Còi báo động 2

// ================= 3. BIẾN TOÀN CỤC QUẢN LÝ TRẠNG THÁI =================
Adafruit_BME280 bme;
bool bmeAvailable = false;

float temp = 30.0;
float hum = 65.0;                   // Vẫn đọc từ BME280 (nếu có) nhưng dùng Độ ẩm đất làm chuẩn
float press = 1010.0;
int soilMoisturePct = 60;           // % Độ ẩm đất thực tế đọc từ chân A0
int waterLevelPct = 85;             // % Nước trong bồn (85% khi HIGH, 15% khi LOW)
bool isRaining = false;

bool pump2State = false;            // Trạng thái Bơm 2 (Tưới gốc - D0)
bool pump3State = false;            // Trạng thái Bơm 3 (Phun sương - D4)

bool dryRunLocked = false;          // Cờ khóa chống cháy bơm khi cạn bồn (Trường hợp 3)
bool stormPreWarning = false;       // Cờ dự báo bão sớm nhận từ ESP32-S3 (Trường hợp 4)
bool pumpFaultLock = false;         // Cờ khóa khi phát hiện kẹt bơm / khô vòi (Trường hợp 5)

String remoteOverride = "AUTO";     // "AUTO" hoặc "MANUAL"
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

// ================= 4. HÀM ĐỌC CẢM BIẾN CÓ LỌC NHIỄU =================
int readSoilMoisturePercentage() {
  long sum = 0;
  for (int i = 0; i < 10; i++) {
    sum += analogRead(PIN_SOIL_AO);
    delay(2);
  }
  int avgRaw = sum / 10;
  // Chuyển đổi từ ADC sang % (SOIL_ADC_DRY -> 0%, SOIL_ADC_WET -> 100%)
  int pct = map(avgRaw, SOIL_ADC_DRY, SOIL_ADC_WET, 0, 100);
  return constrain(pct, 0, 100);
}

void readAllSensors() {
  // 1. Đọc độ ẩm đất từ chân A0
  soilMoisturePct = readSoilMoisturePercentage();

  // 2. Đọc nhiệt độ & áp suất từ BME280
  if (bmeAvailable) {
    float t = bme.readTemperature();
    float p = bme.readPressure() / 100.0F;
    if (!isnan(t)) temp = t;
    if (!isnan(p) && p > 800.0F) press = p;
  }

  // Đồng bộ biến hum bằng độ ẩm đất để tương thích hiển thị nếu hệ thống cũ lấy trường hum
  hum = (float)soilMoisturePct;

  // 3. Đọc cảm biến mực nước tại chân RX (GPIO3): HIGH = Còn nước (~85%), LOW = Cạn bồn (~15%)
  if (digitalRead(PIN_WATER_DO) == HIGH) {
    waterLevelPct = 85;
  } else {
    waterLevelPct = 15;
  }

  // 4. Đọc cảm biến mưa tại chân D3 (GPIO0): LOW = Đang mưa, HIGH = Khô ráo
  isRaining = (digitalRead(PIN_RAIN_DO) == LOW);
}

// ================= 5. HÀM ĐIỀU KHIỂN MÁY BƠM TƯỚI TIÊU =================
void setPumps(bool turnOnPump2, bool turnOnPump3) {
  // Chốt chặn an toàn tuyệt đối ở tầng phần cứng: Không bao giờ bật bơm nếu đang cạn bồn!
  if (dryRunLocked) {
    pump2State = false;
    pump3State = false;
    digitalWrite(PIN_RELAY_PUMP2, RELAY_OFF);
    digitalWrite(PIN_RELAY_PUMP3, RELAY_OFF);
    return;
  }

  // Ghi nhận thời điểm bắt đầu bật Bơm 2 để theo dõi lỗi kẹt bơm / khô nước
  if (turnOnPump2 && !pump2State) {
    pump2StartTime = millis();
    soilAtPump2Start = soilMoisturePct;
  }

  pump2State = turnOnPump2;
  pump3State = turnOnPump3;

  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP3, pump3State ? RELAY_ON : RELAY_OFF);
}

// ================= 6. ĐIỀU KHIỂN NHỊP CÒI BÁO ĐỘNG (NON-BLOCKING) =================
void updateBuzzerPattern() {
  unsigned long now = millis();

  if (manualBuzzer || statusCode == "CRITICAL_PUMP_STALL") {
    // Trường hợp 5 (Kẹt bơm / Tắc ống) hoặc bật còi cưỡng bức từ xa: Còi hú liên tục
    digitalWrite(PIN_BUZZER, BUZZER_ON);
  } 
  else if (statusCode == "ERR_DRY_RUN_LOCK") {
    // Trường hợp 3 (Cạn bồn chứa - RX = LOW): Kêu "Bíp-Bíp" ngắn mỗi 5 giây để nhắc cấp thêm nước
    unsigned long cycle = now % 5000;
    bool doubleBeep = (cycle < 150) || (cycle >= 300 && cycle < 450);
    digitalWrite(PIN_BUZZER, doubleBeep ? BUZZER_ON : BUZZER_OFF);
  } 
  else {
    digitalWrite(PIN_BUZZER, BUZZER_OFF);
  }
}

// ================= 7. ĐÁNH GIÁ CÁC TRƯỜNG HỢP THỰC TẾ TẠI NODE 2 =================
void evaluateNode2Cases() {
  readAllSensors();

  // --- TRƯỜNG HỢP 5: PHÁT HIỆN KẸT BƠM / TẮC ỐNG (BƠM CHẠY NHƯNG ĐẤT KHÔNG ẨM LÊN) ---
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

  // --- TRƯỜNG HỢP 3: KHÓA LIÊN ĐỘNG CHỐNG CHÁY BƠM KHI CẠN BỒN (RX = LOW -> 15%) ---
  if (waterLevelPct < TANK_EMPTY_LOCK_PCT) {
    dryRunLocked = true;
    setPumps(false, false); // Ngắt cưỡng bức cả 2 bơm ngay lập tức
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  } else {
    // Khi chân RX lên HIGH (~85%), mở khóa cho phép bơm hoạt động lại
    dryRunLocked = false;
  }

  // --- CHẾ ĐỘ ĐIỀU KHIỂN CƯỠNG BỨC TỪ XA (MANUAL) ---
  if (remoteOverride == "MANUAL") {
    setPumps(manualPump2, manualPump3);
    statusCode = (manualPump2 || manualPump3) ? "AUTO_IRRIGATING" : "NORMAL";
    return;
  }

  // --- TRƯỜNG HỢP 2 & 4: TƯỚI TIÊU THÍCH ỨNG THEO ĐỘ ẨM ĐẤT & NHIỆT ĐỘ ---
  if (isRaining) {
    // Đang mưa (D3 = LOW) -> Ngắt toàn bộ bơm tưới ngay lập tức
    setPumps(false, false);
    statusCode = "STOP_BY_RAIN";
  } 
  else if (stormPreWarning) {
    // Gateway báo sắp mưa dông -> Hoãn tưới
    setPumps(false, false);
    statusCode = "HOLD_FOR_STORM";
  } 
  else {
    bool nextPump2 = pump2State;
    bool nextPump3 = pump3State;

    // 1. Điều khiển Bơm 2 (Tưới gốc - D0) dựa trên ĐỘ ẨM ĐẤT (Thay cho độ ẩm không khí)
    if (soilMoisturePct < SOIL_START_IRRIG) {
      nextPump2 = true;  // Đất khô (< 55%) -> Bật bơm tưới gốc
    } else if (soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump2 = false; // Đất đủ ẩm (>= 70%) -> Tắt bơm tưới gốc
    }

    // 2. Điều khiển Bơm 3 (Phun sương - D4) dựa trên NHIỆT ĐỘ & ĐỘ ẨM ĐẤT
    if (temp > TEMP_START_MIST && soilMoisturePct < SOIL_STOP_IRRIG) {
      nextPump3 = true;  // Trời nắng nóng (> 33°C) và đất chưa ướt đẫm -> Bật phun sương làm mát
    } else if (temp <= TEMP_STOP_MIST || soilMoisturePct >= SOIL_STOP_IRRIG) {
      nextPump3 = false; // Nhiệt độ đã hạ (<= 30°C) hoặc đã đủ ẩm -> Tắt phun sương
    }

    setPumps(nextPump2, nextPump3);

    if (pump2State || pump3State) {
      statusCode = "AUTO_IRRIGATING";
    } else {
      statusCode = "NORMAL";
    }
  }
}

// ================= 8. ĐÓNG GÓI VÀ PHÁT GÓI TIN LORA LÊN ESP32-S3 =================
// Chuẩn 12 trường khớp 100% với Gateway: UP,id,temp,hum,pres,water,soil,rain,pumpA,pumpB,buzz,mode
void sendLoRaPacket() {
  int buzzState = (manualBuzzer || statusCode == "CRITICAL_PUMP_STALL" || statusCode == "ERR_DRY_RUN_LOCK") ? 1 : 0;
  int modeInt = (remoteOverride == "AUTO") ? 1 : 0;

  String payload = "UP," + String(NODE_ID) + "," +
                   String(temp, 1) + "," +
                   String(soilMoisturePct, 1) + "," + // Gửi độ ẩm đất thay vào vị trí hum nếu hiển thị chung
                   String(press, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(soilMoisturePct) + "," +    // Trường số 6 (soil): Độ ẩm đất chính xác từ A0
                   String(isRaining ? 1 : 0) + "," +
                   String(pump2State ? 1 : 0) + "," + // pumpA: Bơm 2 (Tưới gốc - D0)
                   String(pump3State ? 1 : 0) + "," + // pumpB: Bơm 3 (Phun sương - D4)
                   String(buzzState) + "," +
                   String(modeInt);

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();
}

// ================= 9. XỬ LÝ LỆNH DOWNLINK TỪ GATEWAY ESP32-S3 =================
// Gateway gửi xuống: CMD,2,mode,pump2,pump3,buzz2 (Ví dụ: CMD,2,1,0,0,0)
void handleGatewayCommand(String cmd) {
  cmd.trim();
  String myPrefix = "CMD," + String(NODE_ID) + ",";
  if (!cmd.startsWith(myPrefix)) return; // Không phải lệnh cho Node 2 -> Bỏ qua

  // Tách các tham số điều khiển từ chuỗi CMD,2,mode,pump2,pump3,buzz2
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
      pumpFaultLock = false; // Mở khóa lỗi nếu chuyển lại về AUTO từ Web
      manualBuzzer = (buzz2 == 1);
    } else {
      remoteOverride = "MANUAL";
      manualPump2 = (pump2 == 1);
      manualPump3 = (pump3 == 1);
      manualBuzzer = (buzz2 == 1);
    }
  }

  // Đánh giá lại trạng thái và gửi gói tin cập nhật ngay về Gateway
  evaluateNode2Cases();
  updateBuzzerPattern();
  delay(50);
  sendLoRaPacket();
  lastSentStatus = statusCode;
  lastPeriodicTxTime = millis();
}

// ================= 10. KHỞI TẠO HỆ THỐNG (SETUP) =================
void setup() {
  // Tắt WiFi để tiết kiệm điện và giảm nhiễu ADC
  WiFi.mode(WIFI_OFF);
  WiFi.forceSleepBegin();
  delay(1);

  // Chuyển chân TX (GPIO1) và RX (GPIO3) sang chế độ GPIO thông thường (FUNCTION_3)
  // Lưu ý: Không gọi Serial.begin() vì chân TX/RX đang dùng cho Còi Báo Động 2 và CB Mực Nước
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

  // Khởi tạo BME280 trên D1 (SCL), D2 (SDA)
  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76)) {
    bmeAvailable = true;
  } else if (bme.begin(0x77)) {
    bmeAvailable = true;
  }

  // Khởi tạo LoRa Ra-01 SX1278
  SPI.begin();
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(LORA_FREQ)) {
    // Nếu lỗi LoRa: Nháy còi báo động liên tục để nhận biết
    while (1) {
      digitalWrite(PIN_BUZZER, !digitalRead(PIN_BUZZER));
      delay(300);
    }
  }

  // ĐỒNG BỘ 100% THÔNG SỐ VỚI GATEWAY ESP32-S3 (SyncWord 0xF3)
  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
}

// ================= 11. VÒNG LẶP CHÍNH (LOOP) =================
void loop() {
  unsigned long now = millis();

  // 1. Cập nhật nhịp còi báo động liên tục (Non-blocking)
  updateBuzzerPattern();

  // 2. Đọc cảm biến và xử lý các trường hợp thực tế mỗi 1 giây
  if (now - lastSensorEvalTime >= 1000) {
    lastSensorEvalTime = now;
    evaluateNode2Cases();

    // Nếu vừa chuyển sang trạng thái KHẨN CẤP hoặc SỰ KIỆN MỚI -> Bắn gói tin ngay lập tức
    if (statusCode != lastSentStatus && statusCode != "NORMAL") {
      if (now - lastEmergencyTxTime > 1500) {
        sendLoRaPacket();
        lastSentStatus = statusCode;
        lastEmergencyTxTime = now;
        lastPeriodicTxTime = now;
      }
    }
  }

  // 3. Gửi dữ liệu định kỳ mỗi 5 giây lên Gateway
  if (now - lastPeriodicTxTime >= 5000) {
    lastPeriodicTxTime = now;
    sendLoRaPacket();
  }

  // 4. Lắng nghe lệnh điều khiển Downlink từ Gateway ESP32-S3
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    handleGatewayCommand(incoming);
  }
}