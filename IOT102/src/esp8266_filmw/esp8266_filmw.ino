/*
 * ============================================================================
 * DỰ ÁN IOT LORA 433MHZ - CODE HOÀN CHỈNH CHO NODE 2 (TRẠM TƯỚI TIÊU & BỒN CHỨA)
 * Vi điều khiển : ESP8266 (NodeMCU ESP-12E)
 * Giao tiếp     : LoRa Ra-01 SX1278 (433MHz) <-> Gateway ESP32-S3-WROOM-1
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

// Hiệu chuẩn Cảm biến mực nước v2 đặt trong Bồn dự trữ nước tưới (Chân A0)
#define WATER_ADC_DRY       20      // Giá trị A0 khi bồn cạn khô (0%)
#define WATER_ADC_MAX       820     // Giá trị A0 khi bồn đầy 100%

// Các ngưỡng vận hành thực tế của Node 2
#define TANK_EMPTY_LOCK_PCT 15      // Mực nước bồn < 15%: Khóa cứng máy bơm chống cháy (Case 3)
#define TANK_RECOVER_PCT    22      // Mực nước bồn >= 22%: Mở khóa cho phép bơm hoạt động lại
#define TEMP_START_IRRIG    33.0F   // Nhiệt độ > 33.0°C (Trời nắng nóng)
#define HUM_START_IRRIG     55.0F   // Độ ẩm không khí < 55.0% (Trời hanh khô) -> Bật bơm tưới
#define HUM_STOP_IRRIG      70.0F   // Độ ẩm đạt >= 70.0% hoặc Nhiệt độ < 30.0°C -> Đủ ẩm, tắt bơm
#define TEMP_STOP_IRRIG     30.0F

// Thời gian kiểm tra kẹt bơm / vỡ ống (Trường hợp 5): 3 phút (180000ms)
#define PUMP_STALL_TIMEOUT  180000UL 

// ================= 2. ĐỊNH NGHĨA CHÂN GPIO TRÊN ESP8266 =================
#define LORA_SS             D8      // GPIO15 (CS)
#define LORA_RST            -1      // Nối chung chân RST của ESP8266
#define LORA_DIO0           -1      // Dùng chế độ Polling LoRa.parsePacket()
#define LORA_FREQ           433E6   // Tần số 433MHz

#define PIN_I2C_SCL         D1      // GPIO5 - BME280 SCL
#define PIN_I2C_SDA         D2      // GPIO4 - BME280 SDA
#define PIN_WATER_LVL       A0      // ADC0  - Cảm biến mực nước bồn chứa
#define PIN_RAIN_DO         D3      // GPIO0 - Cảm biến mưa (LOW = Có mưa, HIGH = Khô)
#define PIN_RELAY_PUMP      D0      // GPIO16 - Relay Máy bơm tưới 12V #2
#define PIN_BUZZER          D4      // GPIO2 - Còi báo động #2

// ================= 3. BIẾN TOÀN CỤC QUẢN LÝ TRẠNG THÁI =================
Adafruit_BME280 bme;
bool bmeAvailable = false;

float temp = 30.0;
float hum = 65.0;
float press = 1010.0;
int waterLevelPct = 50;             // % Nước còn lại trong bồn dự trữ
bool isRaining = false;
bool pumpState = false;

bool dryRunLocked = false;          // Cờ khóa chống cháy bơm khi cạn bồn (Trường hợp 3)
bool stormPreWarning = false;       // Cờ dự báo bão sớm nhận từ ESP32-S3 (Trường hợp 4)
bool pumpFaultLock = false;         // Cờ khóa khi phát hiện kẹt bơm (Trường hợp 5)
String remoteOverride = "AUTO";

unsigned long pumpStartTime = 0;
int waterLevelAtPumpStart = 0;

String statusCode = "NORMAL";
String lastSentStatus = "NORMAL";

unsigned long lastSensorEvalTime = 0;
unsigned long lastEmergencyTxTime = 0;

// ================= 4. HÀM ĐỌC CẢM BIẾN CÓ LỌC NHIỄU =================
int readWaterLevelPercentage() {
  long sum = 0;
  for (int i = 0; i < 10; i++) {
    sum += analogRead(PIN_WATER_LVL);
    delay(2);
  }
  int avgRaw = sum / 10;
  int pct = map(avgRaw, WATER_ADC_DRY, WATER_ADC_MAX, 0, 100);
  return constrain(pct, 0, 100);
}

void readAllSensors() {
  if (bmeAvailable) {
    float t = bme.readTemperature();
    float h = bme.readHumidity();
    float p = bme.readPressure() / 100.0F;
    if (!isnan(t)) temp = t;
    if (!isnan(h)) hum = h;
    if (!isnan(p) && p > 800.0F) press = p;
  }
  waterLevelPct = readWaterLevelPercentage();
  isRaining = (digitalRead(PIN_RAIN_DO) == LOW);
}

// ================= 5. HÀM ĐIỀU KHIỂN MÁY BƠM TƯỚI TIÊU =================
void setPump(bool turnOn) {
  // Chốt chặn an toàn tuyệt đối ở tầng phần cứng: Không bao giờ bật bơm nếu đang cạn bồn!
  if (turnOn && dryRunLocked) {
    pumpState = false;
    digitalWrite(PIN_RELAY_PUMP, RELAY_OFF);
    return;
  }

  if (turnOn && !pumpState) {
    pumpStartTime = millis();
    waterLevelAtPumpStart = waterLevelPct;
  }
  pumpState = turnOn;
  digitalWrite(PIN_RELAY_PUMP, turnOn ? RELAY_ON : RELAY_OFF);
}

// ================= 6. ĐIỀU KHIỂN NHỊP CÒI BÁO ĐỘNG (NON-BLOCKING) =================
void updateBuzzerPattern() {
  unsigned long now = millis();

  if (statusCode == "CRITICAL_PUMP_STALL") {
    // Trường hợp 5 (Kẹt bơm / Tắc ống): Còi hú liên tục
    digitalWrite(PIN_BUZZER, BUZZER_ON);
  } 
  else if (statusCode == "ERR_DRY_RUN_LOCK") {
    // Trường hợp 3 (Cạn bồn chứa): Kêu "Bíp-Bíp" ngắn mỗi 5 giây để nhắc cấp thêm nước
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

  // --- TRƯỜNG HỢP 5: PHÁT HIỆN KẸT BƠM / KHÔNG BƠM ĐƯỢC NƯỚC ---
  if (pumpState && (millis() - pumpStartTime > PUMP_STALL_TIMEOUT)) {
    if (abs(waterLevelPct - waterLevelAtPumpStart) < 3) {
      setPump(false);
      pumpFaultLock = true;
      statusCode = "CRITICAL_PUMP_STALL";
      return;
    } else {
      pumpStartTime = millis();
      waterLevelAtPumpStart = waterLevelPct;
    }
  }

  if (pumpFaultLock) {
    setPump(false);
    statusCode = "CRITICAL_PUMP_STALL";
    return;
  }

  // --- TRƯỜNG HỢP 3: KHÓA LIÊN ĐỘNG CHỐNG CHÁY BƠM KHI CẠN BỒN (< 15%) ---
  if (waterLevelPct < TANK_EMPTY_LOCK_PCT) {
    dryRunLocked = true;
    setPump(false); // Ngắt cưỡng bức ngay lập tức bất kể trời nóng hay lệnh từ xa
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  } else if (dryRunLocked && waterLevelPct >= TANK_RECOVER_PCT) {
    // Chỉ mở khóa khi nước trong bồn đã được bơm cấp bù lên >= 22% (Chống nhảy Relay)
    dryRunLocked = false;
  }

  if (dryRunLocked) {
    setPump(false);
    statusCode = "ERR_DRY_RUN_LOCK";
    return;
  }

  // Hỗ trợ lệnh cưỡng bức từ xa (chỉ có tác dụng khi bồn đủ nước và không bị lỗi phần cứng)
  if (remoteOverride == "FORCE_ON") {
    setPump(true);
    statusCode = "AUTO_IRRIGATING";
    return;
  } else if (remoteOverride == "FORCE_OFF") {
    setPump(false);
    statusCode = "NORMAL";
    return;
  }

  // --- TRƯỜNG HỢP 2 & 4: TƯỚI TIÊU THÍCH ỨNG THỜI TIẾT & TỰ NGẮT KHI MƯA / SẮP BÃO ---
  if (isRaining) {
    // Nếu đang tưới hoặc chuẩn bị tưới mà phát hiện hạt mưa rơi -> Ngắt bơm ngay lập tức
    setPump(false);
    statusCode = "STOP_BY_RAIN";
  } 
  else if (stormPreWarning) {
    // Trường hợp 4 (Nhánh Node 2): Gateway báo áp suất khí quyển tụt giảm (sắp mưa dông) -> Hoãn tưới
    setPump(false);
    statusCode = "HOLD_FOR_STORM";
  } 
  else if (temp > TEMP_START_IRRIG && hum < HUM_START_IRRIG) {
    // Trời nắng nóng (>33°C), độ ẩm thấp (<55%), không mưa, bồn còn nước -> Bật bơm tưới tự động
    setPump(true);
    statusCode = "AUTO_IRRIGATING";
  } 
  else if (hum >= HUM_STOP_IRRIG || temp <= TEMP_STOP_IRRIG) {
    // Độ ẩm vườn cây đã đạt mức mát mẻ (>=70%) -> Tắt bơm hoàn tất chu kỳ tưới
    setPump(false);
    statusCode = "NORMAL";
  } 
  else if (!pumpState) {
    statusCode = "NORMAL";
  }
}

// ================= 8. ĐÓNG GÓI VÀ PHÁT GÓI TIN LORA LÊN ESP32-S3 =================
// Định dạng chuẩn khớp với ESP32-S3: N2,34.5,48.0,1009.2,70,0,1,AUTO_IRRIGATING
void sendLoRaPacket() {
  String payload = "N" + String(NODE_ID) + "," +
                   String(temp, 1) + "," +
                   String(hum, 1) + "," +
                   String(press, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(isRaining ? 1 : 0) + "," +
                   String(pumpState ? 1 : 0) + "," +
                   statusCode;

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();

  Serial.println("[Node 2 -> LoRa Uplink] " + payload);
}

// ================= 9. XỬ LÝ LỆNH DOWNLINK TỪ GATEWAY ESP32-S3 =================
void handleGatewayCommand(String cmd) {
  cmd.trim();
  String myPrefix = "CMD,N" + String(NODE_ID) + ",";
  if (!cmd.startsWith(myPrefix)) return; // Không phải lệnh gọi Node 2 -> Bỏ qua

  Serial.println("[Node 2 <- LoRa Downlink] " + cmd);

  // Nhận cờ cảnh báo bão sớm từ Gateway ESP32-S3 (Trường hợp 4)
  stormPreWarning = (cmd.indexOf("STORM_WARN") != -1);

  if (cmd.indexOf("RESET_FAULT") != -1) {
    pumpFaultLock = false;
    remoteOverride = "AUTO";
    statusCode = "NORMAL";
  } else if (cmd.indexOf("FORCE_ON") != -1) {
    remoteOverride = "FORCE_ON";
  } else if (cmd.indexOf("FORCE_OFF") != -1) {
    remoteOverride = "FORCE_OFF";
  } else {
    remoteOverride = "AUTO";
  }

  // Đánh giá lại trạng thái và chờ 40ms trước khi gửi báo cáo lên ESP32-S3
  evaluateNode2Cases();
  delay(40);
  sendLoRaPacket();
  lastSentStatus = statusCode;
}

// ================= 10. KHỞI TẠO HỆ THỐNG (SETUP) =================
void setup() {
  Serial.begin(115200);
  delay(200);
  Serial.println("\n=== KHOI DONG NODE 2: TRAM TUOI TIEU & BON CHUA (ESP8266) ===");

  // Tắt hoàn toàn WiFi trên ESP8266 để tiết kiệm pin năng lượng mặt trời
  WiFi.mode(WIFI_OFF);
  WiFi.forceSleepBegin();
  delay(1);

  // Cấu hình chân I/O
  pinMode(PIN_RAIN_DO, INPUT_PULLUP);
  pinMode(PIN_RELAY_PUMP, OUTPUT);
  pinMode(PIN_BUZZER, OUTPUT);
  digitalWrite(PIN_RELAY_PUMP, RELAY_OFF);
  digitalWrite(PIN_BUZZER, BUZZER_OFF);

  // Khởi tạo BME280 trên bus I2C (SDA = D2, SCL = D1)
  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76)) {
    bmeAvailable = true;
    Serial.println("[OK] Đã kết nối cảm biến BME280 (0x76).");
  } else if (bme.begin(0x77)) {
    bmeAvailable = true;
    Serial.println("[OK] Đã kết nối cảm biến BME280 (0x77).");
  } else {
    Serial.println("[CẢNH BÁO] Không tìm thấy BME280! Sử dụng giá trị dự phòng.");
  }

  // Khởi tạo LoRa Ra-01 SX1278 433MHz
  SPI.begin();
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(LORA_FREQ)) {
    Serial.println("[LỖI] Khởi tạo LoRa Ra-01 thất bại! Kiểm tra nguồn 3.3V và chân D5, D6, D7, D8.");
    while (1) {
      digitalWrite(PIN_BUZZER, !digitalRead(PIN_BUZZER));
      delay(300);
    }
  }

  // Đồng bộ thông số sóng LoRa khớp 100% với Gateway ESP32-S3
  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.enableCrc();
  Serial.println("[OK] Node 2 LoRa 433MHz đã sẵn sàng!");
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
      }
    }
  }

  // 3. Lắng nghe lệnh Polling & Cảnh báo bão từ Gateway ESP32-S3
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    handleGatewayCommand(incoming);
  }
}