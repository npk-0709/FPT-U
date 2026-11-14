#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"
#include "hal/brownout_ll.h"
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <SPI.h>
#include <LoRa.h>
#include <ArduinoJson.h>

// ================= CẤU HÌNH MẠNG & TELEGRAM =================
const char* WIFI_SSID   = "KhuongSoSad2";
const char* WIFI_PASS   = "phukhuong";
const char* PHP_API_URL = "https://iot102-group3-weather-station.phukhuong79.com/api.php";
const char* TG_BOT_TOKEN = "8568608071:AAHDaQBp_nBVdHdrFJVl7wUahyxAO4j19yY";
const char* TG_CHAT_ID   = "1279015740";

// ================= CẤU HÌNH CHÂN PHẦN CỨNG ESP32-S3 =================
#define LORA_SCK        12
#define LORA_MISO       13
#define LORA_MOSI       11
#define LORA_SS         10
#define LORA_RST        9
#define LORA_DIO0       4

#define LED_WATER_LOW   6              // Đèn báo cạn nước bồn chứa
#define LED_RAIN        7              // Đèn báo trời mưa
#define BUZZER_GATEWAY  8              // Còi báo động tại trạm Gateway

// ================= CẤU TRÚC DỮ LIỆU TRẠM NODE =================
struct NodeData {
  float temp = 0;
  float hum = 0;
  float pres = 1008;
  int water = 85;
  int soil = 60;
  int rain = 0;
  int pump1 = 0;                       
  int pump2 = 0;                       
  int buzz = 0;                        
  int mode = 1;                        
  String statusCode = "NORMAL";        
  int soilStart = 0;                   
  int rssi = 0;
  unsigned long lastSeen = 0;
  bool online = false;
  bool offlineAlertSent = false;
};

NodeData node2;

// Biến lưu trạng thái của gói tin trước đó
bool firstPacketReceived = false;
int prevPump1 = 0;
int prevPump2 = 0;
int prevRain = 0;
String prevStatusCode = "NORMAL";
bool isDryRunLockedState = false;      
bool isStallAlertSent = false;         

// Biến đồng bộ điều khiển với Web Server
int ctrlMode2 = 1, ctrlPump2 = 0, ctrlPump3 = 0, ctrlBuzz2 = 0;

// Biến lưu cấu hình gần nhất (để không phải gửi LoRa liên tục nếu không đổi)
int last_cfg_tnk = -1, last_cfg_s_st = -1, last_cfg_s_sp = -1, last_cfg_timeout = -1;
float last_cfg_t_st = -1.0, last_cfg_t_sp = -1.0;

unsigned long lastServerSync = 0;
const unsigned long NODE_TIMEOUT_MS = 60000; 

// ================= HÀNG ĐỢI TIN NHẮN TELEGRAM =================
#define MAX_TG_QUEUE 10
String tgQueue[MAX_TG_QUEUE];
int tgQueueHead = 0;
int tgQueueTail = 0;
int tgQueueCount = 0;
unsigned long lastTelegramSendTime = 0;

void enqueueTelegramAlert(String message) {
  if (tgQueueCount < MAX_TG_QUEUE) {
    tgQueue[tgQueueTail] = message;
    tgQueueTail = (tgQueueTail + 1) % MAX_TG_QUEUE;
    tgQueueCount++;
  }
}

void processTelegramQueue() {
  if (tgQueueCount == 0) return;
  if (WiFi.status() != WL_CONNECTED) return;
  if (millis() - lastTelegramSendTime < 1500) return;

  String message = tgQueue[tgQueueHead];
  tgQueueHead = (tgQueueHead + 1) % MAX_TG_QUEUE;
  tgQueueCount--;
  lastTelegramSendTime = millis();

  LoRa.idle();
  delay(20);

  WiFiClientSecure client;
  client.setInsecure();
  client.setTimeout(5000);
  HTTPClient https;

  String url = "https://api.telegram.org/bot" + String(TG_BOT_TOKEN) + "/sendMessage";
  if (https.begin(client, url)) {
    https.addHeader("Content-Type", "application/json");
    StaticJsonDocument<1024> doc;
    doc["chat_id"] = TG_CHAT_ID;
    doc["text"] = message;
    String body;
    serializeJson(doc, body);
    https.POST(body);
    https.end();
  }

  delay(20);
  LoRa.receive();
}

// ================= CẬP NHẬT ĐÈN LED & CÒI TRÊN GATEWAY =================
void updateGatewayAlerts() {
  bool isRaining = (node2.online && node2.rain == 1);
  digitalWrite(LED_RAIN, isRaining ? HIGH : LOW);

  bool isWaterLow = (node2.online && node2.water < 20);
  digitalWrite(LED_WATER_LOW, isWaterLow ? HIGH : LOW);

  bool needBuzz = false;
  if (node2.online && (node2.statusCode == "ERR_DRY_RUN_LOCK" || 
                       node2.statusCode == "CRITICAL_PUMP_STALL" || 
                       node2.statusCode == "ERR_SENSOR" ||
                       node2.buzz == 1)) {
    needBuzz = true;
  }
  digitalWrite(BUZZER_GATEWAY, needBuzz ? HIGH : LOW);
}

// ================= GỬI LỆNH ĐIỀU KHIỂN XUỐNG NODE =================
void sendLoRaCommand() {
  String cmd = "CMD,2," + String(ctrlMode2) + "," + String(ctrlPump2) + "," + String(ctrlPump3) + "," + String(ctrlBuzz2);
  delay(15);
  LoRa.beginPacket();
  LoRa.print(cmd);
  LoRa.endPacket();
  delay(15);
}

// ================= ĐÁNH GIÁ & PHÁT THÔNG BÁO THEO KỊCH BẢN =================
void evaluateRealWorldAlerts() {
  if (!firstPacketReceived) {
    firstPacketReceived = true;
    prevPump1 = node2.pump1;
    prevPump2 = node2.pump2;
    prevRain = node2.rain;
    prevStatusCode = node2.statusCode;

    if (node2.statusCode == "NORMAL") {
      enqueueTelegramAlert("✅ [HỆ THỐNG BÌNH THƯỜNG - TRẠM NODE]\nMọi thông số môi trường đều trong ngưỡng an toàn (Độ ẩm đất: " + String(node2.soil) + "%, Nhiệt độ: " + String(node2.temp, 1) + "°C, Nước bồn: " + String(node2.water) + "%).\nTrạng thái: NORMAL.");
    }
    else if (node2.statusCode == "ERR_DRY_RUN_LOCK") {
      isDryRunLockedState = true;
      enqueueTelegramAlert("⚠️ [KHẨN CẤP - TRẠM CHÍNH]\nĐất đang rất khô (" + String(node2.soil) + "%) nhưng BỒN CHỨA CẠN NƯỚC (" + String(node2.water) + "% < 20%)!\nĐã KHÓA Máy bơm 1 & 2 (dryRunLocked = true) để chống cháy bơm và bật còi cảnh báo.");
    }
    else if (node2.statusCode == "STOP_BY_RAIN") {
      enqueueTelegramAlert("🌧 [HOÃN TƯỚI KHẨN CẤP DO MƯA - TRẠM CHÍNH]\nCảm biến mưa phát hiện có mưa (D3 = LOW).\nĐã lập tức NGẮT toàn bộ hoạt động bơm tưới tiêu nhằm tiết kiệm nước và bảo vệ cây trồng.");
    }
    else if (node2.statusCode == "ERR_SENSOR") {
      enqueueTelegramAlert("⚠️ [LỖI PHẦN CỨNG - TRẠM NODE]\nKhông thể giao tiếp với Cảm biến BME280 (hỏng hoặc tuột dây).\nHệ thống đã tự động NGẮT các máy bơm để đảm bảo an toàn. Vui lòng kiểm tra lại Node!");
    }
    return;
  }

  // LỖI CẢM BIẾN (ERR_SENSOR)
  if (node2.statusCode == "ERR_SENSOR" && prevStatusCode != "ERR_SENSOR") {
    enqueueTelegramAlert("⚠️ [LỖI PHẦN CỨNG - TRẠM NODE]\nKhông thể giao tiếp với Cảm biến BME280 (hỏng hoặc tuột dây).\nHệ thống đã tự động NGẮT các máy bơm để đảm bảo an toàn. Vui lòng kiểm tra lại Node!");
  }

  // CASE 3: CẠN BỒN CHỨA
  if (node2.statusCode == "ERR_DRY_RUN_LOCK" && !isDryRunLockedState) {
    isDryRunLockedState = true;
    enqueueTelegramAlert("⚠️ [KHẨN CẤP - TRẠM CHÍNH]\nĐất đang rất khô (" + String(node2.soil) + "%) nhưng BỒN CHỨA CẠN NƯỚC (" + String(node2.water) + "% < 20%)!\nĐã KHÓA Máy bơm 1 & 2 (dryRunLocked = true) để chống cháy bơm và bật còi cảnh báo.");
  } 
  else if (isDryRunLockedState && node2.water >= 20 && node2.statusCode != "ERR_DRY_RUN_LOCK") {
    isDryRunLockedState = false;
    enqueueTelegramAlert("💧 [ĐÃ BỔ SUNG NƯỚC BỒN - TRẠM CHÍNH]\nMực nước bồn chứa đã đạt " + String(node2.water) + "%.\nĐã mở khóa bảo vệ máy bơm và tắt còi cảnh báo.");
  }

  // CASE 4: PHÁT HIỆN MƯA & TẠNH MƯA
  if (node2.rain == 1 && prevRain == 0) {
    enqueueTelegramAlert("🌧 [HOÃN TƯỚI KHẨN CẤP DO MƯA - TRẠM CHÍNH]\nCảm biến mưa phát hiện có mưa (D3 = LOW).\nĐã lập tức NGẮT toàn bộ hoạt động bơm tưới tiêu nhằm tiết kiệm nước và bảo vệ cây trồng.");
  } 
  else if (node2.rain == 0 && prevRain == 1) {
    enqueueTelegramAlert("☀️ [ĐÃ TẠNH MƯA - TRẠM CHÍNH]\nCảm biến ghi nhận trời đã tạnh mưa (Độ ẩm đất: " + String(node2.soil) + "%).\nHệ thống trở lại chế độ giám sát tự động NORMAL.");
  }

  // CASE 5 (STALL): KẸT BƠM / VỠ ỐNG DẪN
  if (node2.statusCode == "CRITICAL_PUMP_STALL" && !isStallAlertSent) {
    isStallAlertSent = true;
    enqueueTelegramAlert("🚨 [BÁO ĐỘNG KHẨN CẤP - KẸT BƠM / VỠ ỐNG DẪN]\nMáy bơm 1 đã chạy liên tục quá lâu nhưng độ ẩm đất không tăng đủ 3% (chỉ từ " + String(node2.soilStart) + "% lên " + String(node2.soil) + "%)!\nHệ thống đã KHÓA VĨNH VIỄN cụm bơm (pumpFaultLock = true) và HÚ CÒI BÁO ĐỘNG KHẨN CẤP!");
  } 
  else if (node2.statusCode != "CRITICAL_PUMP_STALL") {
    isStallAlertSent = false;
  }

  // BẬT / TẮT MÁY BƠM 1
  if (node2.pump1 == 1 && prevPump1 == 0) {
    enqueueTelegramAlert("🌱 [TƯỚI TỰ ĐỘNG - TRẠM CHÍNH]\nĐộ ẩm đất xuống thấp (" + String(node2.soil) + "%).\nHệ thống đã tự động BẬT Máy bơm 1 (Tưới gốc).\nMã trạng thái: AUTO_IRRIGATING");
    enqueueTelegramAlert("🌱 [BẮT ĐẦU CHU KỲ TƯỚI - TRẠM CHÍNH]\nKích hoạt Bơm 1 tại mốc độ ẩm đất ban đầu: " + String(node2.soilStart) + "%. Đang giám sát lưu lượng đường ống...");
  } 
  else if (node2.pump1 == 0 && prevPump1 == 1) {
    if (node2.statusCode != "CRITICAL_PUMP_STALL" && node2.statusCode != "ERR_DRY_RUN_LOCK" && node2.statusCode != "ERR_SENSOR" && node2.rain == 0) {
      enqueueTelegramAlert("✅ [HOÀN TẤT TƯỚI GỐC - TRẠM CHÍNH]\nĐộ ẩm đất đã đạt ngưỡng bão hòa (" + String(node2.soil) + "%).\nHệ thống đã tự động NGẮT Máy bơm 1 (Tưới gốc).");
    }
  }

  // BẬT / TẮT MÁY BƠM 2
  if (node2.pump2 == 1 && prevPump2 == 0) {
    enqueueTelegramAlert("🌡 [CẢNH BÁO NẮNG NÓNG - TRẠM CHÍNH]\nNhiệt độ vườn đạt " + String(node2.temp, 1) + "°C, Độ ẩm đất " + String(node2.soil) + "%.\nĐã tự động BẬT Máy bơm 2 (Phun sương hạ nhiệt).");
  } 
  else if (node2.pump2 == 0 && prevPump2 == 1) {
    if (node2.statusCode != "CRITICAL_PUMP_STALL" && node2.statusCode != "ERR_DRY_RUN_LOCK" && node2.statusCode != "ERR_SENSOR" && node2.rain == 0) {
      enqueueTelegramAlert("❄️ [ĐÃ HẠ NHIỆT VƯỜN - TRẠM CHÍNH]\nNhiệt độ vườn đã giảm xuống mức an toàn (" + String(node2.temp, 1) + "°C).\nHệ thống đã tự động NGẮT Máy bơm 2 (Phun sương).");
    }
  }

  // TRỞ VỀ TRẠNG THÁI NORMAL
  if (node2.statusCode == "NORMAL" && prevStatusCode != "NORMAL") {
    if (!(prevRain == 1 && node2.rain == 0)) {
      enqueueTelegramAlert("✅ [HỆ THỐNG BÌNH THƯỜNG - TRẠM NODE]\nMọi thông số môi trường đều trong ngưỡng an toàn (Độ ẩm đất: " + String(node2.soil) + "%, Nhiệt độ: " + String(node2.temp, 1) + "°C, Nước bồn: " + String(node2.water) + "%).\nTrạng thái: NORMAL.");
    }
  }

  prevPump1 = node2.pump1;
  prevPump2 = node2.pump2;
  prevRain = node2.rain;
  prevStatusCode = node2.statusCode;
}

// ================= XỬ LÝ GÓI TIN LORA =================
void processLoRaPacket(String packet, int rssi) {
  packet.trim();
  if (!packet.startsWith("UP,")) return;

  String parts[14];
  int partIdx = 0, startPos = 0;
  for (int i = 0; i < (int)packet.length(); i++) {
    if (packet.charAt(i) == ',' || i == (int)packet.length() - 1) {
      int endPos = (i == (int)packet.length() - 1) ? i + 1 : i;
      if (partIdx < 14) {
        parts[partIdx++] = packet.substring(startPos, endPos);
      }
      startPos = i + 1;
    }
  }
  if (partIdx < 12) return;

  int id = parts[1].toInt();
  if (id != 2) return;                 

  node2.temp = parts[2].toFloat();
  node2.hum = parts[3].toFloat();
  node2.pres = parts[4].toFloat();
  node2.water = parts[5].toInt();
  node2.soil = parts[6].toInt();
  node2.rain = parts[7].toInt();
  node2.pump1 = parts[8].toInt();      
  node2.pump2 = parts[9].toInt();      
  node2.buzz = parts[10].toInt();      
  node2.mode = parts[11].toInt();

  if (partIdx >= 13) node2.statusCode = parts[12];
  if (partIdx >= 14) node2.soilStart = parts[13].toInt();
  else node2.soilStart = node2.soil;

  node2.rssi = rssi;
  node2.lastSeen = millis();

  if (!node2.online && node2.offlineAlertSent) {
    firstPacketReceived = false;
  }
  node2.online = true;
  node2.offlineAlertSent = false;

  if (ctrlMode2 == 1) {
    ctrlPump2 = node2.pump1;
    ctrlPump3 = node2.pump2;
    ctrlBuzz2 = node2.buzz;
  }

  updateGatewayAlerts();
  evaluateRealWorldAlerts();
}

// ================= ĐỒNG BỘ WEB SERVER PHP =================
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
  n2["temp"]   = node2.temp;
  n2["hum"]    = node2.hum;
  n2["pres"]   = node2.pres;
  n2["water"]  = node2.water;
  n2["soil"]   = node2.soil;
  n2["rain"]   = node2.rain;
  n2["pump2"]  = node2.pump1;          
  n2["pump3"]  = node2.pump2;
  n2["buzz2"]  = node2.buzz;
  n2["mode"]   = node2.mode;
  n2["rssi"]   = node2.rssi;
  n2["online"] = node2.online ? 1 : 0;
  n2["status_code"] = node2.statusCode;

  String requestBody;
  serializeJson(doc, requestBody);

  int httpResponseCode = https.POST(requestBody);
  if (httpResponseCode == 200) {
    String response = https.getString();
    StaticJsonDocument<512> resDoc;
    if (deserializeJson(resDoc, response) == DeserializationError::Ok) {
      
      // Đọc và đồng bộ cấu hình từ trang config.php 
      if (resDoc.containsKey("config") && !resDoc["config"].isNull()) {
        JsonObject cfg = resDoc["config"];
        int tnk = cfg["TANK_EMPTY_LOCK_PCT"] | 20;
        int s_st = cfg["SOIL_START_IRRIG"] | 55;
        int s_sp = cfg["SOIL_STOP_IRRIG"] | 70;
        float t_st = cfg["TEMP_START_MIST"] | 33.0;
        float t_sp = cfg["TEMP_STOP_MIST"] | 30.0;
        long timeout = cfg["PUMP_STALL_TIMEOUT"] | 180000;

        // Nếu có sự thay đổi cấu hình, gửi ngay xuống Node qua gói tin CFG
        if (tnk != last_cfg_tnk || s_st != last_cfg_s_st || s_sp != last_cfg_s_sp || 
            t_st != last_cfg_t_st || t_sp != last_cfg_t_sp || timeout != last_cfg_timeout) {
          
          last_cfg_tnk = tnk; last_cfg_s_st = s_st; last_cfg_s_sp = s_sp;
          last_cfg_t_st = t_st; last_cfg_t_sp = t_sp; last_cfg_timeout = timeout;

          String configPayload = "CFG,2," + String(tnk) + "," + String(s_st) + "," + String(s_sp) + 
                                 "," + String(t_st, 1) + "," + String(t_sp, 1) + "," + String(timeout);
          
          delay(20);
          LoRa.beginPacket();
          LoRa.print(configPayload);
          LoRa.endPacket();
          delay(20);
        }
      }

      // Đọc và đồng bộ lệnh điều khiển
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

// ================= GIÁM SÁT MẤT KẾT NỐI LORA =================
void checkNodeWatchdog() {
  unsigned long now = millis();
  if ((node2.lastSeen > 0 && (now - node2.lastSeen > NODE_TIMEOUT_MS)) ||
      (node2.lastSeen == 0 && now > NODE_TIMEOUT_MS)) {
    node2.online = false;
    if (!node2.offlineAlertSent) {
      node2.offlineAlertSent = true;
      enqueueTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm chính (Nông nghiệp) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT.");
    }
  }
  updateGatewayAlerts();
}

// ================= KHỞI TẠO GATEWAY =================
void setup() {
  brownout_ll_intr_enable(false);
  brownout_ll_ana_reset_enable(false);
  CLEAR_PERI_REG_MASK(RTC_CNTL_BROWN_OUT_REG, RTC_CNTL_BROWN_OUT_ENA);

  pinMode(LED_WATER_LOW, OUTPUT);
  digitalWrite(LED_WATER_LOW, LOW);
  pinMode(LED_RAIN, OUTPUT);
  digitalWrite(LED_RAIN, LOW);
  pinMode(BUZZER_GATEWAY, OUTPUT);
  digitalWrite(BUZZER_GATEWAY, LOW);

  setCpuFrequencyMhz(160);

  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(433E6)) {
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
  enqueueTelegramAlert("✅ [HỆ THỐNG KHỞI ĐỘNG]\nGateway ESP32-S3 đã kết nối WiFi và sẵn sàng thu phát LoRa 433MHz!");
}

// ================= VÒNG LẶP CHÍNH =================
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