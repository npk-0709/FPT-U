#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"
#include "hal/brownout_ll.h"  // Thư viện can thiệp Brownout chuẩn cho ESP32-S3
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <SPI.h>
#include <LoRa.h>
#include <ArduinoJson.h>

// Thông tin cấu hình WiFi, Server PHP và Telegram Bot
const char* WIFI_SSID = "KhuongSoSad";
const char* WIFI_PASS = "phukhuong";
const char* PHP_API_URL = "https://iot102-group3-weather-station.phukhuong79.com/api.php";
const char* TG_BOT_TOKEN = "8568608071:AAHDaQBp_nBVdHdrFJVl7wUahyxAO4j19yY";
const char* TG_CHAT_ID = "1279015740";

// Định nghĩa chân SPI LoRa trên ESP32-S3-WROOM
#define LORA_SCK 12
#define LORA_MISO 13
#define LORA_MOSI 11
#define LORA_SS 10
#define LORA_RST 9
#define LORA_DIO0 4

// Cấu hình chân LED & Còi cảnh báo tại Gateway
#define LED_PIN 7
#define LED_RAIN 7        
#define BUZZER_GATEWAY 8  // Chân còi báo động tại Gateway

struct NodeData {
  float temp = 0;
  float hum = 0;
  float pres = 1008;
  int water = 0;
  int soil = 0;
  int rain = 0;
  int pumpA = 0; // Máy bơm 2
  int pumpB = 0; // Máy bơm 3
  int buzz = 0;
  int mode = 1;  // 1 = AUTO, 0 = MANUAL
  int rssi = 0;
  unsigned long lastSeen = 0;
  bool online = false;
  bool offlineAlertSent = false;
};

NodeData node2; // Chỉ giữ lại Node 2

// Biến lưu lệnh điều khiển hiện tại cho Node 2
int ctrlMode2 = 1, ctrlPump2 = 0, ctrlPump3 = 0, ctrlBuzz2 = 0;

unsigned long lastServerSync = 0;
unsigned long lastTelegramTime = 0;
const unsigned long NODE_TIMEOUT_MS = 60000;  // 60 giây timeout mất kết nối

String pendingTelegramMsg = "";

// Hàm đẩy tin nhắn vào hàng đợi
void queueTelegramAlert(String message) {
  if (millis() - lastTelegramTime < 15000 && lastTelegramTime != 0) return;
  pendingTelegramMsg = message;
}

// Cập nhật LED báo mưa dựa trên Node 2
void updateRainLED() {
  bool isRaining = (node2.online && node2.rain == 1);
  digitalWrite(LED_RAIN, isRaining ? HIGH : LOW);
}

// Cập nhật Còi tại Gateway dựa trên trạng thái Node 2
void updateGatewayBuzzer() {
  bool needBuzz = false;
  if (node2.online && node2.water <= 20 && node2.soil <= 35) {
    needBuzz = true; // Cạn nước bồn + đất khô khẩn cấp
  }
  digitalWrite(BUZZER_GATEWAY, needBuzz ? HIGH : LOW);
}

// Xử lý gửi tin nhắn Telegram
void processTelegramQueue() {
  if (pendingTelegramMsg == "") return;
  if (WiFi.status() != WL_CONNECTED) return;

  String message = pendingTelegramMsg;
  pendingTelegramMsg = "";
  lastTelegramTime = millis();

  LoRa.idle();
  delay(20);

  WiFiClientSecure client;
  client.setInsecure();
  client.setTimeout(5000);
  HTTPClient https;

  String url = "https://api.telegram.org/bot" + String(TG_BOT_TOKEN) + "/sendMessage";
  if (https.begin(client, url)) {
    https.addHeader("Content-Type", "application/json");
    StaticJsonDocument<512> doc;
    doc["chat_id"] = TG_CHAT_ID;
    doc["text"] = message;
    String body;
    serializeJson(doc, body);
    int httpCode = https.POST(body);
    Serial.printf("[TELEGRAM] HTTP %d: %s\n", httpCode, message.c_str());
    https.end();
  }

  delay(20);
  LoRa.receive();
}

// Gửi lệnh điều khiển Downlink xuống Node 2
void sendLoRaCommand() {
  String cmd = "CMD,2," + String(ctrlMode2) + "," + String(ctrlPump2) + "," + String(ctrlPump3) + "," + String(ctrlBuzz2);

  delay(15);
  LoRa.beginPacket();
  LoRa.print(cmd);
  LoRa.endPacket();
  delay(15);

  Serial.println("[GATEWAY -> LORA DOWNLINK] " + cmd);
}

// Đánh giá cảnh báo thực tế cho Node 2
void evaluateRealWorldAlerts() {
  if (node2.water <= 20) {
    if (node2.soil <= 35) {
      queueTelegramAlert("⚠️ [KHẨN CẤP - NODE 2]\nĐất đang rất khô (" + String(node2.soil) + "%) nhưng BỒN CHỨA CẠN NƯỚC (" + String(node2.water) + "%)!\nĐã KHÓA Máy bơm 2 & 3 để chống cháy bơm.");
    } else {
      queueTelegramAlert("⚠️ [CẢNH BÁO CẠN BỒN - NODE 2]\nMực nước bồn tưới chỉ còn " + String(node2.water) + "%!\nĐã ngắt bảo vệ Máy bơm 2 & Máy bơm 3.");
    }
  } else if (node2.soil <= 35 && node2.pumpA == 1) {
    queueTelegramAlert("🌱 [TƯỚI TỰ ĐỘNG - NODE 2]\nĐộ ẩm đất xuống thấp (" + String(node2.soil) + "%).\nHệ thống đã tự động BẬT Máy bơm 2 tưới cây.");
  }
  if (node2.temp >= 35.0 && node2.pumpB == 1) {
    queueTelegramAlert("🌡 [CẢNH BÁO NẮNG NÓNG - NODE 2]\nNhiệt độ vườn đạt " + String(node2.temp, 1) + "°C, Ẩm KK " + String(node2.hum, 1) + "%.\nĐã bật Máy bơm 3 phun sương hạ nhiệt.");
  }
}

// Giải mã gói tin LoRa từ Node 2
void processLoRaPacket(String packet, int rssi) {
  if (!packet.startsWith("UP,")) return;

  String parts[12];
  int partIdx = 0;
  int startPos = 0;
  for (int i = 0; i < (int)packet.length(); i++) {
    if (packet.charAt(i) == ',' || i == (int)packet.length() - 1) {
      int endPos = (i == (int)packet.length() - 1) ? i + 1 : i;
      if (partIdx < 12) {
        parts[partIdx++] = packet.substring(startPos, endPos);
      }
      startPos = i + 1;
    }
  }
  if (partIdx < 12) return;

  int id = parts[1].toInt();
  if (id != 2) return; // Chỉ chấp nhận gói tin từ Node 2

  node2.temp = parts[2].toFloat();
  node2.hum = parts[3].toFloat();
  node2.pres = parts[4].toFloat();
  node2.water = parts[5].toInt();
  node2.soil = parts[6].toInt();
  node2.rain = parts[7].toInt();
  node2.pumpA = parts[8].toInt();
  node2.pumpB = parts[9].toInt();
  node2.buzz = parts[10].toInt();
  node2.mode = parts[11].toInt();
  node2.rssi = rssi;
  node2.lastSeen = millis();
  node2.online = true;
  node2.offlineAlertSent = false;

  if (ctrlMode2 == 1) {
    ctrlPump2 = node2.pumpA;
    ctrlPump3 = node2.pumpB;
    ctrlBuzz2 = node2.buzz;
  }

  updateRainLED();
  updateGatewayBuzzer();
  evaluateRealWorldAlerts();
}

// Đồng bộ 1 chiều/2 chiều với Server PHP (Chỉ giữ lại cấu trúc cho Node 2)
void syncWithPHPServer() {
  if (WiFi.status() != WL_CONNECTED) return;

  WiFiClientSecure client;
  client.setInsecure();
  client.setTimeout(5000);

  HTTPClient https;
  if (!https.begin(client, PHP_API_URL)) return;
  https.addHeader("Content-Type", "application/json");

  StaticJsonDocument<512> doc;
  JsonObject n2 = doc.createNestedObject("node2");
  n2["temp"] = node2.temp;
  n2["hum"] = node2.hum;
  n2["pres"] = node2.pres;
  n2["water"] = node2.water;
  n2["soil"] = node2.soil;
  n2["rain"] = node2.rain;
  n2["pump2"] = node2.pumpA;
  n2["pump3"] = node2.pumpB;
  n2["buzz2"] = node2.buzz;
  n2["mode"] = node2.mode;
  n2["rssi"] = node2.rssi;
  n2["online"] = node2.online ? 1 : 0;

  String requestBody;
  serializeJson(doc, requestBody);

  int httpResponseCode = https.POST(requestBody);
  if (httpResponseCode == 200) {
    String response = https.getString();
    StaticJsonDocument<256> resDoc;
    if (deserializeJson(resDoc, response) == DeserializationError::Ok) {
      if (resDoc.containsKey("commands")) {
        JsonObject cmd = resDoc["commands"];
        int webMode2 = cmd["mode2"] | ctrlMode2;
        int webPump2 = cmd["pump2"] | ctrlPump2;
        int webPump3 = cmd["pump3"] | ctrlPump3;
        int webBuzz2 = cmd["buzz2"] | ctrlBuzz2;

        if (webMode2 != ctrlMode2 || webPump2 != ctrlPump2 || webPump3 != ctrlPump3 || webBuzz2 != ctrlBuzz2) {
          ctrlMode2 = webMode2;
          ctrlPump2 = webPump2;
          ctrlPump3 = webPump3;
          ctrlBuzz2 = webBuzz2;
          delay(30);
          sendLoRaCommand();
        }
      }
    }
  }
  https.end();
}

// Giám sát mất kết nối LoRa cho Node 2
void checkNodeWatchdog() {
  unsigned long now = millis();
  if (node2.lastSeen > 0 && (now - node2.lastSeen > NODE_TIMEOUT_MS)) {
    node2.online = false;
    if (!node2.offlineAlertSent) {
      node2.offlineAlertSent = true;
      queueTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm Node 2 (Nông nghiệp) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT.");
    }
  }

  updateRainLED();
  updateGatewayBuzzer();
}

void setup() {
  brownout_ll_intr_enable(false);
  brownout_ll_ana_reset_enable(false);
  CLEAR_PERI_REG_MASK(RTC_CNTL_BROWN_OUT_REG, RTC_CNTL_BROWN_OUT_ENA);

  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);
  pinMode(BUZZER_GATEWAY, OUTPUT);
  digitalWrite(BUZZER_GATEWAY, LOW);

  setCpuFrequencyMhz(160);

  Serial.begin(115200);
  delay(300);

  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(433E6)) {
    Serial.println("Lỗi khởi tạo LoRa trên Gateway ESP32-S3!");
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
  LoRa.setTxPower(14);

  delay(200);

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  WiFi.setTxPower(WIFI_POWER_11dBm);
  WiFi.setSleep(false);

  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    retry++;
  }

  delay(300);
  queueTelegramAlert("✅ [HỆ THỐNG KHỞI ĐỘNG]\nGateway ESP32-S3 (Node 2) đã kết nối WiFi và sẵn sàng thu phát LoRa 433MHz!");
}

void loop() {
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    int rssi = LoRa.packetRssi();
    processLoRaPacket(incoming, rssi);
  }

  processTelegramQueue();

  if (millis() - lastServerSync >= 3000) {
    lastServerSync = millis();
    checkNodeWatchdog();
    syncWithPHPServer();
  }

  delay(5);
}