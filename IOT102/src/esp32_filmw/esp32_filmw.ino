#include <SPI.h>
#include <LoRa.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>

// ================= 1. CẤU HÌNH WIFI, CLOUD & TELEGRAM =================
const char* WIFI_SSID     = "KhuongSoSad";
const char* WIFI_PASSWORD = "phukhuong";

// URL Đám mây (Test nhanh bằng https://webhook.site hoặc điền API Backend của bạn)
const char* CLOUD_API_URL = "https://webhook.site/c4bf6867-500a-4199-91e6-5fe874b7b29f";

// Thông tin Telegram Bot
const char* BOT_TOKEN     = "8568608071:AAHDaQBp_nBVdHdrFJVl7wUahyxAO4j19yY";
const char* CHAT_ID       = "1279015740";

// ================= 2. CẤU HÌNH CHÂN LORA RA-01 CHO ESP32-S3-WROOM-1 =================
// Sử dụng cụm chân phần cứng FSPI (SPI2) chuẩn trên ESP32-S3 (nằm liền kề từ GPIO 9 đến GPIO 14)
#define LORA_DIO0   9       // Chân báo ngắt gói tin (sát GPIO 10)
#define LORA_SS     10      // FSPI CS0
#define LORA_MOSI   11      // FSPI MOSI (FSPID)
#define LORA_SCK    12      // FSPI CLK  (FSPICLK)
#define LORA_MISO   13      // FSPI MISO (FSPIQ)
#define LORA_RST    14      // Chân Reset LoRa (sát GPIO 13)
#define LORA_FREQ   433E6   // Tần số 433MHz

// ================= 3. CẤU TRÚC DỮ LIỆU QUẢN LÝ 2 NODE ESP8266 =================
struct NodeData {
  uint8_t nodeId;
  float temp;
  float hum;
  float press;
  int waterLevel;
  bool isRaining;
  bool pumpState;
  String statusCode;
  String lastAlertedStatus; // Lưu trạng thái cũ để chống spam Telegram
  unsigned long lastAlertTime;
  int rssi;
  float snr;
  unsigned long lastSeen;
  bool online;
};

NodeData node1 = {1, 0, 0, 1012.0, 0, false, false, "WAITING", "NORMAL", 0, 0, 0, 0, false};
NodeData node2 = {2, 0, 0, 1012.0, 0, false, false, "WAITING", "NORMAL", 0, 0, 0, 0, false};

// Biến phục vụ Trường hợp 4: Dự báo bão sớm bằng Áp suất khí quyển (BME280)
float baselinePressure = 0.0;
bool globalStormWarning = false;

// Quản lý thời gian Polling LoRa & Đẩy Cloud
unsigned long lastPollTime = 0;
uint8_t currentPollTarget = 1;
const unsigned long POLL_INTERVAL_MS = 3000;        // Mỗi 3 giây hỏi 1 Node (luân phiên Node 1 <-> Node 2)
const unsigned long CLOUD_SYNC_INTERVAL_MS = 10000; // Mỗi 10 giây đẩy dữ liệu JSON lên Cloud 1 lần
const unsigned long ALERT_COOLDOWN_MS = 180000;     // Chống spam: Lặp lại cảnh báo tối đa 3 phút/lần

// Hàng đợi tin nhắn Telegram giữa Core 1 (LoRa) và Core 0 (Network)
QueueHandle_t telegramQueue;
struct TelegramMsg {
  char text[512];
};

// ================= 4. HÀM ĐẨY TIN NHẮN VÀO HÀNG ĐỢI TELEGRAM =================
void queueTelegramAlert(const String& msg) {
  TelegramMsg item;
  msg.toCharArray(item.text, sizeof(item.text));
  xQueueSend(telegramQueue, &item, 0); // Không làm trễ luồng thu/phát LoRa
}

// ================= 5. HÀM GỬI LỆNH DOWNLINK LORA XUỐNG NODE =================
// Định dạng: CMD,N1,AUTO,STORM_WARN hoặc CMD,N2,AUTO,NO_STORM
void sendLoRaPollAndCommand(uint8_t targetNodeId, const String& pumpCmd) {
  String stormFlag = globalStormWarning ? "STORM_WARN" : "NO_STORM";
  String downlinkPayload = "CMD,N" + String(targetNodeId) + "," + pumpCmd + "," + stormFlag;
  
  LoRa.beginPacket();
  LoRa.print(downlinkPayload);
  LoRa.endPacket();
  
  Serial.println("[LoRa Downlink -> Node " + String(targetNodeId) + "] " + downlinkPayload);
}

// ================= 6. PHÂN TÍCH ÁP SUẤT KHÍ QUYỂN (TRƯỜNG HỢP 4) =================
void analyzeBarometricStorm() {
  float currentPress = 0;
  float currentHum = 0;
  int activeCount = 0;

  if (node1.online && node1.press > 800) { currentPress += node1.press; currentHum += node1.hum; activeCount++; }
  if (node2.online && node2.press > 800) { currentPress += node2.press; currentHum += node2.hum; activeCount++; }
  
  if (activeCount == 0) return;
  currentPress /= activeCount;
  currentHum /= activeCount;

  if (baselinePressure == 0.0) {
    baselinePressure = currentPress;
  }

  // Phát hiện áp suất giảm đột ngột >= 3.0 hPa kèm độ ẩm > 80% HOẶC Node 1 mưa lớn ở đầu hướng gió
  bool rapidPressureDrop = (baselinePressure - currentPress >= 3.0) && (currentHum > 80.0);
  bool upstreamRainDetected = (node1.online && node1.isRaining && node2.online && !node2.isRaining);

  if ((rapidPressureDrop || upstreamRainDetected) && !globalStormWarning) {
    globalStormWarning = true;
    String alert = "⛈ *[CẢNH BÁO DÔNG BÃO SỚM - CASE 4]*\n"
                   "• Áp suất khí quyển giảm xuống: `" + String(currentPress, 1) + " hPa`\n"
                   "• Độ ẩm trung bình: `" + String(currentHum, 1) + "%`\n"
                   "• *Hành động điều phối liên trạm:*\n"
                   "  1️⃣ Lệnh cho *Node 1* bơm hạ mực nước hồ chứa đón lũ.\n"
                   "  2️⃣ Lệnh cho *Node 2* tạm hoãn tưới tiêu chờ mưa tự nhiên.";
    queueTelegramAlert(alert);
  } 
  else if (!rapidPressureDrop && !upstreamRainDetected && globalStormWarning) {
    globalStormWarning = false;
    baselinePressure = currentPress;
    queueTelegramAlert("🌤 *[THỜI TIẾT ỔN ĐỊNH]* Áp suất khí quyển đã bình thường trở lại (`" + String(currentPress, 1) + " hPa`). Hủy chế độ đón bão.");
  }
}

// ================= 7. KIỂM TRA SỰ KIỆN & BẮN CẢNH BÁO TELEGRAM =================
void evaluateTelegramNotifications(NodeData &node) {
  unsigned long now = millis();
  bool statusChanged = (node.statusCode != node.lastAlertedStatus);
  bool cooldownExpired = (now - node.lastAlertTime > ALERT_COOLDOWN_MS);

  // Báo phục hồi khi trạng thái quay lại NORMAL
  if (node.statusCode == "NORMAL" && node.lastAlertedStatus != "NORMAL") {
    String msg = "✅ *[ĐÃ BÌNH THƯỜNG - NODE " + String(node.nodeId) + "]*\n"
                 "Trạm " + String(node.nodeId) + " đã hoạt động ổn định trở lại.\n"
                 "• Mực nước: `" + String(node.waterLevel) + "%` | Bơm: `" + String(node.pumpState ? "BẬT" : "TẮT") + "`";
    queueTelegramAlert(msg);
    node.lastAlertedStatus = "NORMAL";
    return;
  }

  if (node.statusCode != "NORMAL" && (statusChanged || cooldownExpired)) {
    String msg = "";
    
    // TRƯỜNG HỢP 1: Chống ngập khẩn cấp tại Node 1
    if (node.statusCode == "FLOOD_DRAINING") {
      msg = "🚨 *[CẢNH BÁO NGẬP KHẨN CẤP - CASE 1]*\n"
            "• Trạm: *Node 1 (Hồ thu chống ngập)*\n"
            "• Mực nước dâng cao: `" + String(node.waterLevel) + "%`\n"
            "• Trạng thái mưa: `" + String(node.isRaining ? "Đang mưa" : "Không mưa") + "`\n"
            "• *Xử lý:* Đã tự động BẬT Máy bơm thoát nước #1 & Còi báo động tại chỗ!";
    }
    // TRƯỜNG HỢP 2: Tưới tiêu thông minh hoặc tự ngắt khi mưa tại Node 2
    else if (node.statusCode == "AUTO_IRRIGATING" && statusChanged) {
      msg = "🌱 *[TƯỚI CÂY TỰ ĐỘNG - CASE 2]*\n"
            "• Trạm: *Node 2 (Vườn canh tác)*\n"
            "• Vi khí hậu khô nóng: `" + String(node.temp, 1) + "°C` | Độ ẩm: `" + String(node.hum, 1) + "%`\n"
            "• Nước bồn dự trữ: `" + String(node.waterLevel) + "%`\n"
            "• *Xử lý:* Đang bật Máy bơm #2 tưới tiêu.";
    }
    else if (node.statusCode == "STOP_BY_RAIN" && statusChanged) {
      msg = "🌧 *[NGẮT TƯỚI DO TRỜI MƯA - CASE 2]*\n"
            "• Trạm: *Node 2* phát hiện trời bắt đầu mưa.\n"
            "• *Xử lý:* Đã ngắt Máy bơm #2 ngay lập tức để tiết kiệm điện NLMT và tránh úng cây.";
    }
    // TRƯỜNG HỢP 3: Khóa chống cháy bơm khi cạn bồn tại Node 2
    else if (node.statusCode == "ERR_DRY_RUN_LOCK") {
      msg = "⚠️ *[KHÓA BẢO VỆ CẠN BỒN - CASE 3]*\n"
            "• Trạm: *Node 2 (Bồn chứa nước tưới)*\n"
            "• Mực nước bồn cạn kiệt: `" + String(node.waterLevel) + "%` (Dưới ngưỡng an toàn 15%)\n"
            "• *Xử lý:* Đã KHÓA CƯỠNG BỨC Máy bơm #2 chống cháy motor và bật còi nhắc nhở!";
    }
    // TRƯỜNG HỢP 4: Bơm hạ mực nước đón bão tại Node 1
    else if (node.statusCode == "PRE_STORM_DRAIN" && statusChanged) {
      msg = "🌊 *[XẢ ĐỆM ĐÓN BÃO SỚM - CASE 4]*\n"
            "• Trạm: *Node 1* đang bơm rút bớt nước hồ (Hiện tại: `" + String(node.waterLevel) + "%`) để tạo dung tích trống đón mưa lớn.";
    }
    // TRƯỜNG HỢP 5: Lỗi phần cứng kẹt bơm / vỡ ống
    else if (node.statusCode == "CRITICAL_PUMP_STALL") {
      msg = "🔥 *[SỰ CỐ PHẦN CỨNG NGHIÊM TRỌNG - CASE 5]*\n"
            "• Trạm: *Node " + String(node.nodeId) + "*\n"
            "• Chi tiết: Máy bơm đã chạy quá 3 phút nhưng mực nước đứng yên ở `" + String(node.waterLevel) + "%` (Nghi ngờ kẹt cánh bơm, tuột ống hoặc đứt dây 12V).\n"
            "• *Xử lý:* Đã ngắt Relay bảo vệ nguồn NLMT và hú còi báo động liên tục! Yêu cầu kiểm tra thực địa!";
    }

    if (msg.length() > 0) {
      queueTelegramAlert(msg);
      node.lastAlertedStatus = node.statusCode;
      node.lastAlertTime = now;
    }
  }
}

// ================= 8. HÀM BÓC TÁCH GÓI TIN CSV TỪ LORA =================
// Định dạng gói tin nhận từ ESP8266: N1,29.5,78.2,1008.4,85,1,1,FLOOD_DRAINING
void parseLoRaPacket(String packet, int rssi, float snr) {
  packet.trim();
  Serial.println("[LoRa Uplink <-] " + packet + " | RSSI: " + String(rssi) + " dBm");

  int idx[7];
  idx[0] = packet.indexOf(',');
  for (int i = 1; i < 7; i++) {
    idx[i] = packet.indexOf(',', idx[i - 1] + 1);
    if (idx[i] == -1) return;
  }

  String idStr     = packet.substring(0, idx[0]);
  float temp       = packet.substring(idx[0] + 1, idx[1]).toFloat();
  float hum        = packet.substring(idx[1] + 1, idx[2]).toFloat();
  float press      = packet.substring(idx[2] + 1, idx[3]).toFloat();
  int waterLvl     = packet.substring(idx[3] + 1, idx[4]).toInt();
  bool rain        = (packet.substring(idx[4] + 1, idx[5]).toInt() == 1);
  bool pump        = (packet.substring(idx[5] + 1, idx[6]).toInt() == 1);
  String statusStr = packet.substring(idx[6] + 1);

  NodeData* target = nullptr;
  if (idStr == "N1") target = &node1;
  else if (idStr == "N2") target = &node2;
  else return;

  target->temp       = temp;
  target->hum        = hum;
  target->press      = press;
  target->waterLevel = waterLvl;
  target->isRaining  = rain;
  target->pumpState  = pump;
  target->statusCode = statusStr;
  target->rssi       = rssi;
  target->snr        = snr;
  target->lastSeen   = millis();
  target->online     = true;

  evaluateTelegramNotifications(*target);
  analyzeBarometricStorm();
}

// ================= 9. TÁC VỤ MẠNG CHẠY TRÊN CORE 0 (CLOUD + TELEGRAM) =================
void sendTelegramHTTPS(const String& text) {
  if (WiFi.status() != WL_CONNECTED) return;

  WiFiClientSecure client;
  client.setInsecure();
  HTTPClient https;

  String url = "https://api.telegram.org/bot" + String(BOT_TOKEN) + "/sendMessage";
  if (https.begin(client, url)) {
    https.addHeader("Content-Type", "application/json");
    
    String escapedText = text;
    escapedText.replace("\n", "\\n");
    
    String jsonBody = "{\"chat_id\":\"" + String(CHAT_ID) + "\","
                      "\"text\":\"" + escapedText + "\","
                      "\"parse_mode\":\"Markdown\"}";

    int httpCode = https.POST(jsonBody);
    Serial.printf("[Telegram HTTPS] Status Code: %d\n", httpCode);
    https.end();
  }
}

void pushDataToCloud() {
  if (WiFi.status() != WL_CONNECTED) return;

  WiFiClientSecure client;
  client.setInsecure();
  HTTPClient http;

  if (http.begin(client, CLOUD_API_URL)) {
    http.addHeader("Content-Type", "application/json");

    String jsonPayload = "{";
    jsonPayload += "\"device\":\"ESP32-S3-WROOM-1\",";
    jsonPayload += "\"gateway_uptime_s\":" + String(millis() / 1000) + ",";
    jsonPayload += "\"storm_pre_warning\":" + String(globalStormWarning ? "true" : "false") + ",";
    
    jsonPayload += "\"node_1\":{";
    jsonPayload += "\"online\":" + String(node1.online ? "true" : "false") + ",";
    jsonPayload += "\"temp\":" + String(node1.temp, 1) + ",";
    jsonPayload += "\"hum\":" + String(node1.hum, 1) + ",";
    jsonPayload += "\"press\":" + String(node1.press, 1) + ",";
    jsonPayload += "\"water_level\":" + String(node1.waterLevel) + ",";
    jsonPayload += "\"is_raining\":" + String(node1.isRaining ? "true" : "false") + ",";
    jsonPayload += "\"pump_state\":" + String(node1.pumpState ? "true" : "false") + ",";
    jsonPayload += "\"status\":\"" + node1.statusCode + "\",";
    jsonPayload += "\"rssi\":" + String(node1.rssi) + "},";

    jsonPayload += "\"node_2\":{";
    jsonPayload += "\"online\":" + String(node2.online ? "true" : "false") + ",";
    jsonPayload += "\"temp\":" + String(node2.temp, 1) + ",";
    jsonPayload += "\"hum\":" + String(node2.hum, 1) + ",";
    jsonPayload += "\"press\":" + String(node2.press, 1) + ",";
    jsonPayload += "\"water_level\":" + String(node2.waterLevel) + ",";
    jsonPayload += "\"is_raining\":" + String(node2.isRaining ? "true" : "false") + ",";
    jsonPayload += "\"pump_state\":" + String(node2.pumpState ? "true" : "false") + ",";
    jsonPayload += "\"status\":\"" + node2.statusCode + "\",";
    jsonPayload += "\"rssi\":" + String(node2.rssi) + "}";
    jsonPayload += "}";

    int httpCode = http.POST(jsonPayload);
    Serial.printf("[Cloud Sync] HTTP Code: %d\n", httpCode);
    http.end();
  }
}

void networkTask(void * parameter) {
  unsigned long lastCloudSync = 0;
  TelegramMsg incomingMsg;

  for (;;) {
    if (WiFi.status() != WL_CONNECTED) {
      Serial.println("[WiFi] Đang kết nối lại...");
      WiFi.disconnect();
      WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
      vTaskDelay(5000 / portTICK_PERIOD_MS);
      continue;
    }

    if (xQueueReceive(telegramQueue, &incomingMsg, 100 / portTICK_PERIOD_MS) == pdTRUE) {
      sendTelegramHTTPS(String(incomingMsg.text));
    }

    if (millis() - lastCloudSync > CLOUD_SYNC_INTERVAL_MS) {
      lastCloudSync = millis();
      pushDataToCloud();
    }

    vTaskDelay(50 / portTICK_PERIOD_MS);
  }
}

// ================= 10. KHỞI TẠO HỆ THỐNG TRÊN ESP32-S3-WROOM-1 =================
void setup() {
  Serial.begin(115200);
  // Đợi tối đa 2 giây để cổng USB-CDC trên ESP32-S3 kịp khởi tạo nhưng không treo nếu chạy nguồn rời
  unsigned long startWait = millis();
  while (!Serial && (millis() - startWait < 2000)) { delay(10); }
  
  Serial.println("\n=== KHOI DONG GATEWAY ESP32-S3-WROOM-1 LORA 433MHZ ===");

  telegramQueue = xQueueCreate(10, sizeof(TelegramMsg));

  // Kết nối WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Đang kết nối WiFi");
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    Serial.print(".");
    retry++;
  }
  Serial.println("\nWiFi Connected! IP: " + WiFi.localIP().toString());

  // Khởi tạo bus FSPI trên ESP32-S3 và gán rõ ràng vào thư viện LoRa
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setSPI(SPI);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);

  if (!LoRa.begin(LORA_FREQ)) {
    Serial.println("[LỖI] Không tìm thấy module LoRa Ra-01 SX1278! Kiểm tra các chân GPIO 9 -> 14 và nguồn 3.3V.");
    while (1) { delay(1000); }
  }

  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.enableCrc();
  Serial.println("[OK] LoRa SX1278 433MHz trên ESP32-S3 đã sẵn sàng!");

  // Chạy tác vụ mạng độc lập trên Core 0 của ESP32-S3
  xTaskCreatePinnedToCore(
    networkTask,
    "NetworkTask",
    12288, // Cấp 12KB Stack cho TLS/HTTPS trên kiến trúc Xtensa LX7 của ESP32-S3
    NULL,
    1,
    NULL,
    0      // Ghim vào Core 0
  );

  queueTelegramAlert("🟢 *[HỆ THỐNG KHỞI ĐỘNG]*\nGateway *ESP32-S3-WROOM-1* đã kết nối WiFi và sẵn sàng thu thập dữ liệu LoRa 433MHz từ 2 Node ESP8266.");
}

// ================= 11. VÒNG LẶP CHÍNH TRÊN CORE 1 (LORA REAL-TIME) =================
void loop() {
  // 1. Nhận gói tin Uplink từ 2 Node ESP8266
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    parseLoRaPacket(incoming, LoRa.packetRssi(), LoRa.packetSnr());
  }

  // 2. Điều phối hỏi vòng Master-Slave mỗi 3 giây chống xung đột sóng
  unsigned long now = millis();
  if (now - lastPollTime > POLL_INTERVAL_MS) {
    lastPollTime = now;
    sendLoRaPollAndCommand(currentPollTarget, "AUTO");
    currentPollTarget = (currentPollTarget == 1) ? 2 : 1;
  }

  // 3. Kiểm tra mất kết nối từng Node (Timeout 30 giây)
  if (node1.online && (now - node1.lastSeen > 30000UL)) {
    node1.online = false;
    queueTelegramAlert("⚠️ *[MẤT KẾT NỐI LORA]* Không nhận được tín hiệu từ *Node 1 (Trạm Chống Ngập)* quá 30 giây!");
  }
  if (node2.online && (now - node2.lastSeen > 30000UL)) {
    node2.online = false;
    queueTelegramAlert("⚠️ *[MẤT KẾT NỐI LORA]* Không nhận được tín hiệu từ *Node 2 (Trạm Tưới Tiêu)* quá 30 giây!");
  }
}