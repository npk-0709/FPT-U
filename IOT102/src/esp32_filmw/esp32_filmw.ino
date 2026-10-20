#include <SPI.h>
#include <LoRa.h>

// Định nghĩa chân SPI cho ESP32-S3-WROOM-1
#define LORA_SCK   12
#define LORA_MISO  13
#define LORA_MOSI  11
#define LORA_SS    10
#define LORA_RST   9
#define LORA_DIO0  2

int counter = 0;

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("--- Khoi dong LoRa Sender tren ESP32-S3 ---");

  // Khởi tạo bus SPI với đúng các chân của ESP32-S3
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);

  // Gán chân điều khiển cho thư viện LoRa
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);

  // Khởi động LoRa ở tần số 433MHz (chuẩn của module Ra-01)
  if (!LoRa.begin(433E6)) {
    Serial.println("Loi: Khong tim thay module LoRa Ra-01! Kiem tra lai day SPI va nguon 3.3V.");
    while (1) {
      delay(1000);
    }
  }

  // Đặt mã đồng bộ (Sync Word) là 0xF3 để chỉ giao tiếp với ESP8266 cùng mã
  LoRa.setSyncWord(0xF3);
  Serial.println("Khoi tao LoRa thanh cong! Bat dau gui du lieu...");
}

void loop() {
  Serial.print("Dang gui goi tin so: ");
  Serial.println(counter);

  // Đóng gói và phát dữ liệu qua sóng LoRa
  LoRa.beginPacket();
  LoRa.print("Xin chao tu ESP32-S3 #");
  LoRa.print(counter);
  LoRa.endPacket();

  counter++;
  delay(2000); // Gửi mỗi 2 giây
}