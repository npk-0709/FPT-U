#include <SPI.h>
#include <LoRa.h>

// Định nghĩa chân cho ESP8266 (NodeMCU)
// Các chân SPI cứng (D5=SCK, D6=MISO, D7=MOSI) được thư viện SPI tự động nhận
#define LORA_SS    15  // Chân D8 (GPIO 15) - Rút dây này ra nếu lúc Upload bị lỗi, nạp xong cắm lại
#define LORA_RST   16  // Chân D0 (GPIO 16)
#define LORA_DIO0  5   // Chân D1 (GPIO 5)

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println();
  Serial.println("--- Khoi dong LoRa Receiver tren ESP8266 ---");

  // Gán chân điều khiển cho thư viện LoRa
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);

  // Khởi động LoRa ở tần số 433MHz
  if (!LoRa.begin(433E6)) {
    Serial.println("Loi: Khong tim thay module LoRa Ra-01! Kiem tra lai day dau noi.");
    while (1) {
      delay(1000);
    }
  }

  // Phải trùng Sync Word (0xF3) với bên phát ESP32-S3
  LoRa.setSyncWord(0xF3);
  Serial.println("Khoi tao LoRa thanh cong! Dang lang nghe goi tin...");
}

void loop() {
  // Kiểm tra xem có gói tin nào gửi tới không
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    Serial.print("Nhan duoc: '");

    // Đọc toàn bộ nội dung gói tin
    while (LoRa.available()) {
      String LoRaData = LoRa.readString();
      Serial.print(LoRaData);
    }

    // In ra cường độ tín hiệu (RSSI) và tỉ số tín hiệu trên nhiễu (SNR)
    Serial.print("' | RSSI: ");
    Serial.print(LoRa.packetRssi());
    Serial.print(" dBm | SNR: ");
    Serial.println(LoRa.packetSnr());
  }
}