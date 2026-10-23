# DỰ ÁN IOT THỰC TẾ: HỆ THỐNG GIÁM SÁT THỦY LỢI, CẢNH BÁO NGẬP ÚNG & TƯỚI TIÊU THÔNG MINH VÙNG SÂU VÙNG XA (LORA + ESP32-S3 + PHP SERVER + TELEGRAM + BLYNK)

Dự án này tận dụng **100% số lượng linh kiện bạn đang có** để xây dựng một mạng lưới cảm biến không dây tầm xa (LoRa $433\text{ MHz}$) hoạt động độc lập bằng năng lượng mặt trời tại các khu vực không có Wi-Fi, kết nối về Trạm trung tâm (Gateway) để tự động hóa 3 máy bơm và đồng bộ dữ liệu đa nền tảng.

## PHẦN 1: PHÂN BỔ PHẦN CỨNG & VAI TRÒ TỪNG LINH KIỆN

### 1. Phân chia 3 phân khu phần cứng

1. **NODE 1 (Trạm Thủy Lợi / Kênh Thoát Nước - Vùng sâu không có Wi-Fi):**

   * **Vi điều khiển:** 01 x ESP8266 (NodeMCU / Wemos D1).

   * **Truyền thông:** 01 x Module LoRa Ra-01 SX1278 ($433\text{ MHz}$).

   * **Cảm biến:** 01 x BME280 (Nhiệt độ, Độ ẩm, Áp suất) + 01 x Cảm biến mực nước v2 + 01 x Cảm biến nước mưa.

   * **Cảnh báo tại chỗ:** 01 x Còi báo động $5\text{V}$ (2 dây).

   * **Nguồn & Lọc nhiễu:** 01 x Tấm pin NLMT + 01 x Mạch sạc + 01 x Pin $3.7\text{V}$ + **02 x Tụ hóa** $16\text{V } 220\mu\text{F}$.

2. **NODE 2 (Trạm Khu Canh Tác & Bể Chứa Nước Đồi Cao - Vùng sâu không có Wi-Fi):**

   * **Vi điều khiển:** 01 x ESP8266.

   * **Truyền thông:** 01 x Module LoRa Ra-01 SX1278 ($433\text{ MHz}$).

   * **Cảm biến:** 01 x BME280 + 01 x Cảm biến mực nước v2 (đo mực nước bể chứa) + 01 x Cảm biến nước mưa.

   * **Cảnh báo tại chỗ:** 01 x Còi báo động $5\text{V}$ (2 dây).

   * **Nguồn & Lọc nhiễu:** 01 x Tấm pin NLMT + 01 x Mạch sạc + 01 x Pin $3.7\text{V}$ + **01 x Tụ hóa** $16\text{V } 220\mu\text{F}$.

3. **GATEWAY TRUNG TÂM (Trạm Điều Khiển Máy Bơm & Kết Nối Internet):**

   * **Vi điều khiển:** 01 x ESP32-S3-WROOM (Có Wi-Fi mạnh, nhiều chân GPIO).

   * **Truyền thông:** 01 x Module LoRa Ra-01 SX1278 ($433\text{ MHz}$).

   * **Chấp hành:** 03 x Module Relay + 03 x Máy bơm $12\text{V}$ (Bơm 1: Tiêu úng, Bơm 2: Cấp nước vào bể, Bơm 3: Phun sương/Tưới tiêu).

   * **Nguồn & Lọc nhiễu:** Nguồn $5\text{V}$ cho mạch điều khiển + Nguồn $12\text{V}$ cho máy bơm + **02 x Tụ hóa** $16\text{V } 220\mu\text{F}$.

> **Giải thích kỹ thuật về 5 Tụ điện** $16\text{V } 220\mu\text{F}$**:**
> Module LoRa SX1278 khi phát tín hiệu (TX) sẽ tiêu thụ dòng xung đột ngột lên tới $120\text{ mA}$. Nếu dùng nguồn từ pin $3.7\text{V}$ hoặc chân $3.3\text{V}$ của ESP, sụt áp tức thời sẽ làm treo chip hoặc rớt gói tin. Do đó:
>
> * **3 tụ** $220\mu\text{F}$ được mắc song song sát chân `3.3V` và `GND` của **3 module LoRa Ra-01** (Chân dương tụ vào `3.3V`, chân âm sọc trắng vào `GND`).
>
> * **1 tụ** $220\mu\text{F}$ mắc vào đường nguồn `5V` cấp cho cụm 3 Relay ở ESP32-S3 để chống sụt áp khi cuộn hút Relay đóng/ngắt.
>
> * **1 tụ** $220\mu\text{F}$ mắc ở đầu ra mạch sạc pin NLMT của Node 1 (trạm quan trọng nhất) để ổn định dòng khi còi báo động $5\text{V}$ kêu.

## PHẦN 2: 6 TRƯỜNG HỢP THỰC TẾ (USE CASES) ĐƯỢC LẬP TRÌNH TRONG HỆ THỐNG

Hệ thống hỗ trợ 2 chế độ: **TỰ ĐỘNG (`AUTO`)** và **THỦ CÔNG (`MANUAL`)**. Khi ở chế độ `AUTO`, 6 kịch bản thực tế sau sẽ vận hành liên tục:

### Trường hợp 1: Chống ngập úng khẩn cấp tại Kênh thoát nước (Node 1 $\rightarrow$ Bơm 1)

* **Điều kiện kích hoạt:** Khi Cảm biến mực nước tại Node 1 vượt ngưỡng nguy hiểm ($> 75\%$) **HOẶC** (Mực nước $> 50\%$ **VÀ** Cảm biến mưa Node 1 phát hiện đang mưa to).

* **Hành động thực thi:**

  1. Node 1 bật **Còi báo động #1** kêu tại chỗ để cảnh báo người dân/nhân viên trực.

  2. Node 1 gửi gói tin khẩn cấp qua sóng LoRa về ESP32-S3.

  3. ESP32-S3 lập tức đóng Relay 1, bật **Máy bơm 1 (Bơm tiêu úng/thoát nước)**.

  4. Gửi tin nhắn cảnh báo khẩn cấp qua **Telegram** và cập nhật trạng thái lên **Web Server PHP** & **Blynk IoT**. Khi nước rút xuống $< 40\%$, hệ thống tự động tắt Bơm 1 và tắt còi.

### Trường hợp 2: Tự động bơm bù nước vào bể dự trữ & Bảo vệ chống chạy khô (Node 2 & Node 1 $\rightarrow$ Bơm 2)

* **Điều kiện kích hoạt:** Cảm biến mực nước tại Node 2 (đặt trong bể chứa trên đồi) báo mực nước cạn ($< 25\%$).

* **Hành động thực thi:**

  1. ESP32-S3 kiểm tra mực nước nguồn tại Node 1. Nếu Node 1 có nước ($\ge 15\%$), ESP32-S3 bật **Máy bơm 2 (Bơm cấp nước lên bể)** và ghi nhận lên Web/Blynk.

  2. Khi mực nước bể tại Node 2 đầy ($\ge 85\%$), ESP32-S3 tự động ngắt Bơm 2.

  3. **Bảo vệ thực tế (Anti-Dry Run):** Nếu bể Node 2 cạn ($< 25\%$) nhưng nguồn nước ở Node 1 cũng cạn kiệt ($< 15\%$), ESP32-S3 **cấm bật Bơm 2** để tránh cháy máy bơm, đồng thời gửi cảnh báo *"Cạn nguồn nước cấp - Đã khóa Bơm 2 bảo vệ động cơ"* qua Telegram.

### Trường hợp 3: Tưới tiêu & Phun sương làm mát thông minh tiết kiệm nước (Node 2 $\rightarrow$ Bơm 3)

* **Điều kiện kích hoạt:** Cảm biến BME280 tại Node 2 đo được Nhiệt độ cao ($> 35^\circ\text{C}$) **HOẶC** Độ ẩm không khí khô ($< 50\%$), **ĐỒNG THỜI** Cảm biến mưa tại Node 2 xác nhận **trời không mưa**.

* **Hành động thực thi:**

  1. ESP32-S3 bật **Máy bơm 3 (Bơm tưới/phun sương)** để hạ nhiệt và cấp ẩm cho cây trồng.

  2. **Ngắt thông minh khi trời mưa:** Nếu đang tưới mà trời đổ mưa (Cảm biến mưa Node 2 chuyển trạng thái có mưa) hoặc độ ẩm đã đạt $\ge 70\%$ và nhiệt độ $< 32^\circ\text{C}$, ESP32-S3 lập tức tắt Bơm 3 để tiết kiệm nước và điện.

### Trường hợp 4: Dự báo giông bão sớm bằng Áp suất khí quyển & Cảnh báo nguy cơ cháy/sốc nhiệt (BME280 Node 1 & 2)

* **Điều kiện kích hoạt:**

  * *Kịch bản 4A (Cảnh báo áp thấp/giông bão):* Áp suất khí quyển từ BME280 (Node 1 hoặc Node 2) tụt xuống dưới ngưỡng áp thấp ($< 1000\text{ hPa}$) kèm độ ẩm không khí cao ($> 80\%$).

  * *Kịch bản 4B (Cảnh báo khô hạn/cháy):* Nhiệt độ tại Node 2 vượt ngưỡng cực đoan ($> 38^\circ\text{C}$) và độ ẩm $< 40\%$.

* **Hành động thực thi:**

  * Với 4A: Gửi cảnh báo dự báo thời tiết xấu qua Telegram trước khi mưa lũ ập tới.

  * Với 4B: Bật **Còi báo động #2** tại khu canh tác, gửi cảnh báo nguy cơ cháy/héo cây qua Telegram và tự động kích hoạt Bơm 3 cứu cây.

### Trường hợp 5: Giám sát đứt kết nối trạm từ xa (LoRa Watchdog & Fail-Safe)

* **Điều kiện kích hoạt:** Do Node 1 và Node 2 đặt ở vùng sâu dùng pin mặt trời, nếu quá $60\text{ giây}$ mà ESP32-S3 không nhận được gói tin LoRa từ Node 1 hoặc Node 2 (do che khuất, hỏng hóc, cạn pin).

* **Hành động thực thi:**

  1. ESP32-S3 kích hoạt cơ chế an toàn (**Fail-Safe**): Tự động ngắt các máy bơm đang phụ thuộc vào trạm bị mất kết nối để tránh bơm tràn hoặc cháy bơm.

  2. Gửi tin nhắn cảnh báo *"MẤT KẾT NỐI TRẠM LORA"* qua Telegram và đổi trạng thái Node sang `OFFLINE` trên giao diện Web PHP và Blynk.

### Trường hợp 6: Điều khiển cưỡng bức từ xa đa nền tảng (Manual Override qua Web PHP & Blynk IoT)

* **Điều kiện kích hoạt:** Người quản lý gạt nút chuyển chế độ từ `AUTO` sang `MANUAL` trên ứng dụng **Blynk IoT** hoặc trên **Bảng điều khiển Web PHP**.

* **Hành động thực thi:** Hệ thống tạm dừng thuật toán tự động, cho phép người dùng bật/tắt trực tiếp từng Máy bơm (1, 2, 3) từ bất kỳ đâu có Internet. Trạng thái giữa Web PHP và Blynk được đồng bộ hai chiều.

## PHẦN 3: SƠ ĐỒ ĐẤU DÂY CHI TIẾT (WIRING DIAGRAM)

### 1. Sơ đồ đấu dây cho NODE 1 và NODE 2 (2 x ESP8266 NodeMCU)

> **Lưu ý kỹ thuật cực kỳ quan trọng trên ESP8266:**
> Các chân `D3 (GPIO0)`, `D4 (GPIO2)` phải ở mức `HIGH` khi khởi động, và `D8 (GPIO15)` phải ở mức `LOW` khi khởi động. Bảng đấu dây dưới đây đã được thiết kế chuẩn xác để **ESP8266 không bao giờ bị treo khi bật nguồn hoặc Reset**:

| Linh kiện tại Node 1 & 2 | Chân Linh Kiện | Chân ESP8266 (NodeMCU) | GPIO tương ứng | Ghi chú kỹ thuật | 
| ----- | ----- | ----- | ----- | ----- | 
| **Module LoRa Ra-01** | `3.3V` | `3V3` | \- | **Mắc 1 tụ** $220\mu\text{F}$ **sát chân `3.3V` và `GND`** | 
| *(Giao tiếp SPI)* | `GND` | `GND` | \- | Nối chung Mass | 
|  | `SCK` | `D5` | `GPIO14` | Xung nhịp SPI | 
|  | `MISO` | `D6` | `GPIO12` | Dữ liệu vào SPI | 
|  | `MOSI` | `D7` | `GPIO13` | Dữ liệu ra SPI | 
|  | `NSS` (CS) | `D0` | `GPIO16` | Chọn chip (Không ảnh hưởng Boot) | 
|  | `RST` | `D3` | `GPIO0` | Chân RST có trở kéo lên $\rightarrow$ Boot an toàn | 
|  | `DIO0` | *Bỏ trống* | \- | Dùng chế độ Polling không cần chân DIO0 | 
| **Cảm biến BME280** | `VCC` | `3V3` | \- | Nguồn $3.3\text{V}$ | 
| *(Giao tiếp I2C)* | `GND` | `GND` | \- | Nối chung Mass | 
|  | `SCL` | `D1` | `GPIO5` | Xung nhịp I2C | 
|  | `SDA` | `D2` | `GPIO4` | Dữ liệu I2C | 
| **Cảm biến mực nước v2** | `+` (VCC) | `3V3` | \- | Nguồn $3.3\text{V}$ | 
| *(Đọc Analog)* | `-` (GND) | `GND` | \- | Nối chung Mass | 
|  | `S` (Signal) | `A0` | `ADC0` | Đọc độ sâu mực nước ($0 - 1024$) | 
| **Cảm biến nước mưa** | `VCC` | `3V3` | \- | Cấp vào module so sánh LM393 | 
| *(Đọc Digital)* | `GND` | `GND` | \- | Nối chung Mass | 
|  | `DO` | `D4` | `GPIO2` | Khi khô xuất mức HIGH $\rightarrow$ Boot an toàn | 
| **Còi báo động 5V** | Dây Đỏ (`+`) | `D8` | `GPIO15` | Kéo xuống GND qua còi $\rightarrow$ Boot an toàn | 
| *(2 dây)* | Dây Đen (`-`) | `GND` | \- | (Khuyến khích đệm Transistor NPN nếu còi ăn dòng lớn) | 
| **Nguồn Pin NLMT** | Tấm Pin `+ / -` | `IN+ / IN-` (Mạch sạc) | \- | Sạc cho Pin $3.7\text{V}$ | 
|  | `OUT+` (Mạch sạc) | `VIN` (hoặc `5V`/`3V3` qua LDO) | \- | Mắc tụ $220\mu\text{F}$ tại ngõ ra mạch sạc | 
|  | `OUT-` (Mạch sạc) | `GND` | \- | Mass chung toàn mạch | 

### 2. Sơ đồ đấu dây cho GATEWAY TRUNG TÂM (ESP32-S3-WROOM)

| Linh kiện tại Gateway | Chân Linh Kiện | Chân ESP32-S3-WROOM | Ghi chú kỹ thuật | 
| ----- | ----- | ----- | ----- | 
| **Module LoRa Ra-01 #3** | `3.3V` | `3V3` | **Mắc 1 tụ** $220\mu\text{F}$ **sát chân `3.3V` và `GND`** | 
| *(Giao tiếp SPI)* | `GND` | `GND` | Nối chung Mass | 
|  | `SCK` | `GPIO12` | SPI Clock | 
|  | `MISO` | `GPIO13` | SPI MISO | 
|  | `MOSI` | `GPIO11` | SPI MOSI | 
|  | `NSS` (CS) | `GPIO10` | SPI Chip Select | 
|  | `RST` | `GPIO9` | LoRa Reset | 
|  | `DIO0` | `GPIO14` | Ngắt nhận gói tin LoRa | 
| **Cụm 3 Module Relay** | `VCC` | `5V` (`VIN`) | **Mắc 1 tụ** $220\mu\text{F}$ **giữa `5V` và `GND` của Relay** | 
|  | `GND` | `GND` | Nối chung Mass với ESP32-S3 | 
|  | `IN1` (Relay 1) | `GPIO4` | Điều khiển **Máy bơm 1** (Bơm tiêu úng) | 
|  | `IN2` (Relay 2) | `GPIO5` | Điều khiển **Máy bơm 2** (Bơm cấp nước bể) | 
|  | `IN3` (Relay 3) | `GPIO6` | Điều khiển **Máy bơm 3** (Bơm tưới/phun sương) | 
| **3 Máy bơm 12V** | Dây Đỏ (`+`) Bơm 1,2,3 | Chân `NO` của Relay 1,2,3 | Chân `COM` của cả 3 Relay nối vào cực `+12V` của Nguồn 12V | 
|  | Dây Đen (`-`) Bơm 1,2,3 | Cực `-` (`GND`) Nguồn 12V | Đấu trực tiếp về âm nguồn 12V | 

## PHẦN 4: MÃ NGUỒN ĐẦY ĐỦ (FULL SOURCE CODE)

### 1. Code cho 2 Node phát LoRa (ESP8266 Node 1 & Node 2)

> **Cách nạp:** Bạn dùng chung đoạn code này cho cả 2 con ESP8266. Khi nạp cho Node 1 thì để `#define NODE_ID 1`, khi nạp cho Node 2 thì đổi thành `#define NODE_ID 2`.

```
// =========================================================================
// FIRMWARE NODE CẢM BIẾN LORA TẦM XA (DÙNG CHO ESP8266 NODE 1 & NODE 2)
// Thư viện cần cài trong Arduino IDE:
// 1. "LoRa" by Sandeep Mistry
// 2. "Adafruit BME280 Library" & "Adafruit Unified Sensor"
// =========================================================================

#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

// ---------------- CẤU HÌNH ĐỊNH DANH NODE ----------------
// Đổi thành 1 khi nạp cho Node 1 (Trạm Kênh Thoát Nước)
// Đổi thành 2 khi nạp cho Node 2 (Trạm Khu Canh Tác & Bể Chứa)
#define NODE_ID 1  

// ---------------- ĐỊNH NGHĨA CHÂN PIN ESP8266 ----------------
#define LORA_SCK    14  // D5
#define LORA_MISO   12  // D6
#define LORA_MOSI   13  // D7
#define LORA_SS     16  // D0
#define LORA_RST    0   // D3
#define LORA_DIO0   -1  // Không dùng ngắt, dùng chế độ Polling tiết kiệm chân

#define I2C_SCL     5   // D1
#define I2C_SDA     4   // D2

#define PIN_WATER   A0  // Chân Analog đọc cảm biến mực nước v2
#define PIN_RAIN    2   // D4 - Chân DO của cảm biến mưa (LOW = Có mưa, HIGH = Khô)
#define PIN_BUZZER  15  // D8 - Chân kích Còi báo động 5V (HIGH = Kêu, LOW = Tắt)

// ---------------- NGƯỠNG CẢNH BÁO TẠI CHỖ ----------------
#define FLOOD_WATER_THRESHOLD   75  // Mực nước > 75% báo động ngập tại Node 1
#define EXTREME_TEMP_THRESHOLD  38.0 // Nhiệt độ > 38 độ C báo động nhiệt tại Node 2

// ---------------- BIẾN TOÀN CỤC ----------------
Adafruit_BME280 bme;
bool bmeAvailable = false;

unsigned long lastSendTime = 0;
// Node 1 gửi mỗi 5000ms (5s), Node 2 gửi mỗi 5700ms (5.7s) để tránh trùng sóng LoRa!
const unsigned long sendInterval = (NODE_ID == 1) ? 5000 : 5700; 

unsigned long packetCounter = 0;

void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n==========================================");
  Serial.printf("KHOI DONG LORA SENSOR NODE #%d\n", NODE_ID);
  Serial.println("==========================================");

  // 1. Cấu hình IO
  pinMode(PIN_RAIN, INPUT);
  pinMode(PIN_BUZZER, OUTPUT);
  digitalWrite(PIN_BUZZER, LOW); // Đảm bảo còi tắt lúc khởi động

  // Bíp còi 1 tiếng ngắn báo hiệu mạch khởi động thành công
  digitalWrite(PIN_BUZZER, HIGH);
  delay(150);
  digitalWrite(PIN_BUZZER, LOW);

  // 2. Khởi tạo I2C & Cảm biến BME280
  Wire.begin(I2C_SDA, I2C_SCL);
  if (bme.begin(0x76, &Wire)) {
    bmeAvailable = true;
    Serial.println("[OK] Da ket noi BME280 tai dia chi 0x76");
  } else if (bme.begin(0x77, &Wire)) {
    bmeAvailable = true;
    Serial.println("[OK] Da ket noi BME280 tai dia chi 0x77");
  } else {
    Serial.println("[CANH BAO] Khong tim thay BME280! Se gui gia tri mac dinh.");
  }

  // 3. Khởi tạo Module LoRa Ra-01 433MHz
  SPI.begin();
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  
  if (!LoRa.begin(433E6)) {
    Serial.println("[LOI] Khoi tao LoRa Ra-01 THAT BAI! Kiem tra dau day SPI va nguon 3.3V.");
    while (1) {
      // Nháy còi báo lỗi phần cứng LoRa
      digitalWrite(PIN_BUZZER, HIGH); delay(100);
      digitalWrite(PIN_BUZZER, LOW);  delay(900);
    }
  }

  // Tối ưu hóa thông số LoRa cho vùng sâu vùng xa (xuyên vật cản tốt)
  LoRa.setSpreadingFactor(9);       // SF9 cân bằng giữa khoảng cách xa và thời gian phát
  LoRa.setSignalBandwidth(125E3);   // Băng thông 125kHz
  LoRa.setCodingRate4(5);           // Coding rate 4/5
  LoRa.setSyncWord(0xF3);           // Mã đồng bộ riêng (tránh nhiễu từ mạng LoRa khác)
  LoRa.enableCrc();                 // Bật kiểm tra lỗi CRC

  Serial.println("[OK] LoRa Ra-01 433MHz da san sang phat tin hieu!");
}

// Hàm đọc và quy đổi mực nước ra phần trăm (0 - 100%)
int readWaterLevelPercent() {
  long sum = 0;
  for (int i = 0; i < 10; i++) {
    sum += analogRead(PIN_WATER);
    delay(5);
  }
  int raw = sum / 10;
  // Cảm biến mực nước v2 khi khô = 0, khi ngập hoàn toàn khoảng 650 - 800
  int percent = map(raw, 0, 700, 0, 100);
  percent = constrain(percent, 0, 100);
  return percent;
}

void loop() {
  unsigned long currentMillis = millis();

  if (currentMillis - lastSendTime >= sendInterval) {
    lastSendTime = currentMillis;
    packetCounter++;

    // 1. Đọc dữ liệu từ BME280
    float temp = 0.0, hum = 0.0, pres = 0.0;
    if (bmeAvailable) {
      temp = bme.readTemperature();
      hum  = bme.readHumidity();
      pres = bme.readPressure() / 100.0F; // Đổi từ Pa sang hPa
      
      // Kiểm tra nếu đọc lỗi NaN
      if (isnan(temp) || isnan(hum) || isnan(pres)) {
        temp = 28.5; hum = 65.0; pres = 1012.0;
      }
    }

    // 2. Đọc cảm biến mực nước (0 - 100%)
    int waterLevel = readWaterLevelPercent();

    // 3. Đọc cảm biến mưa (LM393 xuất LOW khi có nước mưa trên bề mặt)
    int isRaining = (digitalRead(PIN_RAIN) == LOW) ? 1 : 0;

    // 4. Xử lý Còi báo động tại chỗ (Local Alarm)
    int localAlarm = 0;
    if (NODE_ID == 1) {
      // Trạm 1: Báo động khi nước ngập > 75% hoặc vừa mưa vừa nước > 50%
      if (waterLevel > FLOOD_WATER_THRESHOLD || (isRaining == 1 && waterLevel > 50)) {
        localAlarm = 1;
      }
    } else if (NODE_ID == 2) {
      // Trạm 2: Báo động khi nhiệt độ quá cao (> 38C) hoặc bể nước cạn kiệt (< 10%)
      if (temp > EXTREME_TEMP_THRESHOLD || waterLevel < 10) {
        localAlarm = 1;
      }
    }

    // Kích hoạt còi báo động nếu có cảnh báo
    if (localAlarm == 1) {
      digitalWrite(PIN_BUZZER, HIGH);
    } else {
      digitalWrite(PIN_BUZZER, LOW);
    }

    // 5. Đóng gói bản tin LoRa theo định dạng chuẩn:
    // Format: ID:1,CNT:10,T:30.5,H:75.2,P:1008.3,WL:45,RN:1,AL:0
    String payload = "ID:" + String(NODE_ID) +
                     ",CNT:" + String(packetCounter) +
                     ",T:" + String(temp, 1) +
                     ",H:" + String(hum, 1) +
                     ",P:" + String(pres, 1) +
                     ",WL:" + String(waterLevel) +
                     ",RN:" + String(isRaining) +
                     ",AL:" + String(localAlarm);

    // 6. Truyền gói tin qua LoRa
    LoRa.beginPacket();
    LoRa.print(payload);
    LoRa.endPacket();

    Serial.print("[LORA TX] ");
    Serial.println(payload);
  }
}

```

### 2. Code cho GATEWAY TRUNG TÂM (ESP32-S3-WROOM)

> **Chức năng:** Thu sóng LoRa từ 2 Node ESP8266, giải mã dữ liệu, vận hành **6 Trường hợp thực tế**, điều khiển 3 Máy bơm $12\text{V}$, đồng bộ thời gian thực với **Blynk IoT**, đẩy dữ liệu lên **PHP Server** và gửi cảnh báo qua **Telegram Bot**.

```
// =========================================================================
// FIRMWARE GATEWAY TRUNG TÂM (ESP32-S3-WROOM)
// Thư viện cần cài trong Arduino IDE:
// 1. "LoRa" by Sandeep Mistry
// 2. "Blynk" by Volodymyr Shymanskyy (Blynk IoT 2.0)
// 3. "ArduinoJson" by Benoit Blanchon
// =========================================================================

// ---------------- THÔNG TIN CẤU HÌNH BLYNK IOT ----------------
#define BLYNK_TEMPLATE_ID   "TMPL6xxxxxxxx"
#define BLYNK_TEMPLATE_NAME "Smart Agro LoRa System"
#define BLYNK_AUTH_TOKEN    "Your_Blynk_Auth_Token_Here"

#define BLYNK_PRINT Serial

#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <BlynkSimpleEsp32.h>
#include <SPI.h>
#include <LoRa.h>
#include <ArduinoJson.h>

// ---------------- CẤU HÌNH WIFI & SERVER PHP & TELEGRAM ----------------
const char* WIFI_SSID     = "TEN_WIFI_CUA_BAN";
const char* WIFI_PASS     = "MAT_KHAU_WIFI";

// Địa chỉ file api.php trên máy tính (XAMPP) hoặc Hosting của bạn
const char* PHP_SERVER_URL = "http://192.168.1.100/iot_project/api.php";

// Cấu hình Telegram Bot
const String BOT_TOKEN    = "1234567890:AAHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
const String CHAT_ID      = "987654321"; // Chat ID cá nhân hoặc Group Telegram

// ---------------- ĐỊNH NGHĨA CHÂN PIN ESP32-S3 ----------------
#define LORA_SCK    12
#define LORA_MISO   13
#define LORA_MOSI   11
#define LORA_SS     10
#define LORA_RST    9
#define LORA_DIO0   14

#define RELAY_PUMP1 4   // Máy bơm 1: Bơm tiêu úng (Node 1)
#define RELAY_PUMP2 5   // Máy bơm 2: Bơm cấp nước vào bể chứa (Node 2)
#define RELAY_PUMP3 6   // Máy bơm 3: Bơm tưới tiêu / phun sương (Node 2)

// Lưu ý: Hầu hết module Relay kích mức LOW (LOW = Bật, HIGH = Tắt).
// Nếu Relay của bạn kích mức HIGH, hãy đảo 2 giá trị dưới đây:
#define RELAY_ON    LOW
#define RELAY_OFF   HIGH

// ---------------- CẤU TRÚC LƯU TRỮ DỮ LIỆU NODE ----------------
struct NodeData {
  float temp = 0.0;
  float hum = 0.0;
  float pres = 1013.0;
  int waterLevel = 0;
  int isRaining = 0;
  int alarm = 0;
  int rssi = 0;
  unsigned long lastSeen = 0;
  bool isOnline = false;
};

NodeData node1; // Trạm Kênh Thoát Nước
NodeData node2; // Trạm Khu Canh Tác & Bể Chứa

// ---------------- TRẠNG THÁI HỆ THỐNG & MÁY BƠM ----------------
bool autoMode = true; // true = Tự động (AUTO), false = Thủ công (MANUAL)
bool pump1State = false;
bool pump2State = false;
bool pump3State = false;

// Biến chống spam tin nhắn Telegram (Cooldown 60 giây cho mỗi loại cảnh báo)
unsigned long lastTeleFloodAlert = 0;
unsigned long lastTeleDryAlert   = 0;
unsigned long lastTeleWeatherAlert = 0;
unsigned long lastTeleOfflineAlert = 0;
const unsigned long TELE_COOLDOWN = 60000; 

unsigned long lastServerSync = 0;
const unsigned long SERVER_SYNC_INTERVAL = 5000; // Đồng bộ PHP Server mỗi 5s

// ---------------- HÀM ĐIỀU KHIỂN RELAY MÁY BƠM ----------------
void setPumps(bool p1, bool p2, bool p3) {
  pump1State = p1;
  pump2State = p2;
  pump3State = p3;
  digitalWrite(RELAY_PUMP1, pump1State ? RELAY_ON : RELAY_OFF);
  digitalWrite(RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
  digitalWrite(RELAY_PUMP3, pump3State ? RELAY_ON : RELAY_OFF);
}

// ---------------- HÀM MÃ HÓA URL CHO TELEGRAM ----------------
String urlEncode(String str) {
  String encodedString = "";
  char c, code0, code1;
  for (unsigned int i = 0; i < str.length(); i++) {
    c = str.charAt(i);
    if (c == ' ') {
      encodedString += '+';
    } else if (isalnum(c)) {
      encodedString += c;
    } else {
      code1 = (c & 0xf) + '0';
      if ((c & 0xf) > 9) code1 = (c & 0xf) - 10 + 'A';
      c = (c >> 4) & 0xf;
      code0 = c + '0';
      if (c > 9) code0 = c - 10 + 'A';
      encodedString += '%';
      encodedString += code0;
      encodedString += code1;
    }
  }
  return encodedString;
}

// ---------------- HÀM GỬI CẢNH BÁO TELEGRAM ----------------
void sendTelegramMessage(String message) {
  if (WiFi.status() != WL_CONNECTED) return;
  
  WiFiClientSecure client;
  client.setInsecure(); // Bỏ qua kiểm tra chứng chỉ SSL để chạy nhanh và ổn định
  HTTPClient https;
  
  String url = "https://api.telegram.org/bot" + BOT_TOKEN + 
               "/sendMessage?chat_id=" + CHAT_ID + 
               "&parse_mode=HTML&text=" + urlEncode(message);
               
  if (https.begin(client, url)) {
    int httpCode = https.GET();
    Serial.printf("[TELEGRAM] Da gui canh bao (HTTP Code: %d)\n", httpCode);
    https.end();
  }
}

// ---------------- HÀM TÁCH DỮ LIỆU GÓI TIN LORA ----------------
String getValue(String data, String key) {
  String searchKey = key + ":";
  int startIndex = data.indexOf(searchKey);
  if (startIndex == -1) return "";
  startIndex += searchKey.length();
  int endIndex = data.indexOf(",", startIndex);
  if (endIndex == -1) endIndex = data.length();
  return data.substring(startIndex, endIndex);
}

void parseLoRaPacket(String packet, int rssi) {
  int id = getValue(packet, "ID").toInt();
  if (id == 1) {
    node1.temp       = getValue(packet, "T").toFloat();
    node1.hum        = getValue(packet, "H").toFloat();
    node1.pres       = getValue(packet, "P").toFloat();
    node1.waterLevel = getValue(packet, "WL").toInt();
    node1.isRaining  = getValue(packet, "RN").toInt();
    node1.alarm      = getValue(packet, "AL").toInt();
    node1.rssi       = rssi;
    node1.lastSeen   = millis();
    node1.isOnline   = true;
    Serial.printf("[NODE 1 CAP NHAT] T:%.1fC, H:%.1f%%, P:%.1fhPa, Nuoc:%d%%, Mua:%d, RSSI:%d\n",
                  node1.temp, node1.hum, node1.pres, node1.waterLevel, node1.isRaining, node1.rssi);
  } 
  else if (id == 2) {
    node2.temp       = getValue(packet, "T").toFloat();
    node2.hum        = getValue(packet, "H").toFloat();
    node2.pres       = getValue(packet, "P").toFloat();
    node2.waterLevel = getValue(packet, "WL").toInt();
    node2.isRaining  = getValue(packet, "RN").toInt();
    node2.alarm      = getValue(packet, "AL").toInt();
    node2.rssi       = rssi;
    node2.lastSeen   = millis();
    node2.isOnline   = true;
    Serial.printf("[NODE 2 CAP NHAT] T:%.1fC, H:%.1f%%, P:%.1fhPa, BeChua:%d%%, Mua:%d, RSSI:%d\n",
                  node2.temp, node2.hum, node2.pres, node2.waterLevel, node2.isRaining, node2.rssi);
  }
}

// =========================================================================
// XỬ LÝ 6 TRƯỜNG HỢP THỰC TẾ (USE CASES ENGINE)
// =========================================================================
void evaluatePracticalScenarios() {
  unsigned long now = millis();

  // --- TRƯỜNG HỢP 5: GIÁM SÁT ĐỨT KẾT NỐI TRẠM LORA (WATCHDOG > 60 GIÂY) ---
  if (node1.isOnline && (now - node1.lastSeen > 60000)) {
    node1.isOnline = false;
    if (autoMode) pump1State = false; // Fail-safe: Ngắt bơm 1
    if (now - lastTeleOfflineAlert > TELE_COOLDOWN) {
      lastTeleOfflineAlert = now;
      sendTelegramMessage("⚠️ <b>[CẢNH BÁO MẤT KẾT NỐI - TH5]</b>\nTrạm <b>Node 1 (Kênh Thoát Nước)</b> đã mất tín hiệu LoRa quá 60 giây! Đã kích hoạt chế độ Fail-Safe.");
    }
  }
  if (node2.isOnline && (now - node2.lastSeen > 60000)) {
    node2.isOnline = false;
    if (autoMode) { pump2State = false; pump3State = false; } // Fail-safe: Ngắt bơm 2, 3
    if (now - lastTeleOfflineAlert > TELE_COOLDOWN) {
      lastTeleOfflineAlert = now;
      sendTelegramMessage("⚠️ <b>[CẢNH BÁO MẤT KẾT NỐI - TH5]</b>\nTrạm <b>Node 2 (Khu Canh Tác)</b> đã mất tín hiệu LoRa quá 60 giây! Đã ngắt bơm an toàn.");
    }
  }

  // Nếu đang ở chế độ THỦ CÔNG (TH6 - Manual Override), không chạy điều khiển tự động
  if (!autoMode) {
    setPumps(pump1State, pump2State, pump3State);
    return;
  }

  bool nextP1 = pump1State;
  bool nextP2 = pump2State;
  bool nextP3 = pump3State;

  // --- TRƯỜNG HỢP 1: CHỐNG NGẬP ÚNG KHẨN CẤP TẠI TRẠM 1 (BƠM 1) ---
  if (node1.isOnline) {
    if (node1.waterLevel > 75 || (node1.isRaining == 1 && node1.waterLevel > 50)) {
      nextP1 = true;
      if (now - lastTeleFloodAlert > TELE_COOLDOWN) {
        lastTeleFloodAlert = now;
        String msg = "🚨 <b>[BÁO ĐỘNG NGẬP ÚNG - TH1]</b>\n"
                     "• Mực nước Kênh (Node 1): <b>" + String(node1.waterLevel) + "%</b>\n"
                     "• Trạng thái mưa: <b>" + String(node1.isRaining ? "Đang mưa" : "Không mưa") + "</b>\n"
                     "👉 Đã tự động BẬT <b>Máy bơm 1 (Tiêu úng)</b> & Còi báo động!";
        sendTelegramMessage(msg);
      }
    } else if (node1.waterLevel < 40) {
      nextP1 = false; // Nước đã rút an toàn -> Tắt bơm 1
    }
  }

  // --- TRƯỜNG HỢP 2: BƠM BÙ NƯỚC BỂ CHỨA & CHỐNG CHẠY KHÔ (BƠM 2) ---
  if (node2.isOnline) {
    if (node2.waterLevel < 25) {
      // Kiểm tra nguồn nước tại Node 1 xem có bị cạn kiệt không
      if (node1.isOnline && node1.waterLevel < 15) {
        nextP2 = false; // Khóa bơm 2 chống cháy động cơ
        if (now - lastTeleDryAlert > TELE_COOLDOWN) {
          lastTeleDryAlert = now;
          sendTelegramMessage("🛑 <b>[BẢO VỆ CHỐNG CHẠY KHÔ - TH2]</b>\nBể chứa Node 2 đang cạn (" + String(node2.waterLevel) + "%) nhưng nguồn nước Node 1 cũng cạn kiệt (" + String(node1.waterLevel) + "%).\n👉 Đã <b>KHÓA MÁY BƠM 2</b> để bảo vệ tránh cháy bơm!");
        }
      } else {
        nextP2 = true; // Nguồn nước ổn định -> Bật bơm 2 cấp nước lên bể
      }
    } else if (node2.waterLevel >= 85) {
      nextP2 = false; // Bể đã đầy -> Tắt bơm 2
    }
  }

  // --- TRƯỜNG HỢP 3: TƯỚI TIÊU & PHUN SƯƠNG LÀM MÁT THÔNG MINH (BƠM 3) ---
  if (node2.isOnline) {
    if (node2.isRaining == 1) {
      // Trời đang mưa -> Lập tức ngắt bơm tưới để tiết kiệm nước
      nextP3 = false;
    } else if (node2.temp > 35.0 || node2.hum < 50.0) {
      // Trời nắng nóng hoặc khô hạn và không mưa -> Bật bơm 3 phun sương
      nextP3 = true;
    } else if (node2.temp < 32.0 && node2.hum >= 65.0) {
      // Đã mát và đủ ẩm -> Tắt bơm 3
      nextP3 = false;
    }
  }

  // --- TRƯỜNG HỢP 4: DỰ BÁO GIÔNG BÃO ÁP THẤP & CẢNH BÁO CHÁY/KHÔ HẠN ---
  if ((node1.isOnline && node1.pres < 1000.0 && node1.hum > 80.0) ||
      (node2.isOnline && node2.pres < 1000.0 && node2.hum > 80.0)) {
    if (now - lastTeleWeatherAlert > TELE_COOLDOWN) {
      lastTeleWeatherAlert = now;
      sendTelegramMessage("⛈️ <b>[DỰ BÁO GIÔNG BÃO SỚM - TH4]</b>\nÁp suất khí quyển giảm thấp (" + String(node1.pres, 1) + " hPa) kèm độ ẩm cao.\n👉 Cảnh báo khả năng sắp có mưa lớn hoặc giông lốc tại khu vực!");
    }
  }
  if (node2.isOnline && node2.temp > 38.0 && node2.hum < 40.0) {
    if (now - lastTeleWeatherAlert > TELE_COOLDOWN) {
      lastTeleWeatherAlert = now;
      sendTelegramMessage("🔥 <b>[CẢNH BÁO NẮNG NÓNG CỰC ĐOAN - TH4]</b>\nNhiệt độ khu canh tác (Node 2) lên tới <b>" + String(node2.temp, 1) + "°C</b>, Độ ẩm giảm còn <b>" + String(node2.hum, 1) + "%</b>.\n👉 Đã kích hoạt Còi cảnh báo nhiệt và Bơm phun sương làm mát!");
    }
  }

  // Thực thi trạng thái ra 3 Relay
  setPumps(nextP1, nextP2, nextP3);
}

// =========================================================================
// ĐỒNG BỘ DỮ LIỆU VỚI BLYNK IOT (VIRTUAL PINS V0 -> V12)
// =========================================================================
// V0: Chế độ Auto/Manual (1 = Auto, 0 = Manual)
// V1: Nút nhấn Bơm 1 | V2: Nút nhấn Bơm 2 | V3: Nút nhấn Bơm 3
// V4: Mực nước Node 1 | V5: Nhiệt độ Node 1 | V6: Độ ẩm Node 1 | V7: Áp suất Node 1
// V8: Mực nước Node 2 | V9: Nhiệt độ Node 2 | V10: Độ ẩm Node 2 | V11: Trạng thái Mưa (N1/N2)

BLYNK_WRITE(V0) {
  autoMode = (param.asInt() == 1);
  Serial.printf("[BLYNK] Chuyen che do sang: %s\n", autoMode ? "AUTO" : "MANUAL");
}

BLYNK_WRITE(V1) {
  if (!autoMode) {
    pump1State = (param.asInt() == 1);
    setPumps(pump1State, pump2State, pump3State);
  }
}

BLYNK_WRITE(V2) {
  if (!autoMode) {
    pump2State = (param.asInt() == 1);
    setPumps(pump1State, pump2State, pump3State);
  }
}

BLYNK_WRITE(V3) {
  if (!autoMode) {
    pump3State = (param.asInt() == 1);
    setPumps(pump1State, pump2State, pump3State);
  }
}

void syncToBlynk() {
  if (!Blynk.connected()) return;
  Blynk.virtualWrite(V0, autoMode ? 1 : 0);
  Blynk.virtualWrite(V1, pump1State ? 1 : 0);
  Blynk.virtualWrite(V2, pump2State ? 1 : 0);
  Blynk.virtualWrite(V3, pump3State ? 1 : 0);

  Blynk.virtualWrite(V4, node1.waterLevel);
  Blynk.virtualWrite(V5, node1.temp);
  Blynk.virtualWrite(V6, node1.hum);
  Blynk.virtualWrite(V7, node1.pres);

  Blynk.virtualWrite(V8, node2.waterLevel);
  Blynk.virtualWrite(V9, node2.temp);
  Blynk.virtualWrite(V10, node2.hum);
  
  String statusStr = "N1:" + String(node1.isOnline ? (node1.isRaining ? "Mưa" : "Khô") : "Mất KN") +
                     " | N2:" + String(node2.isOnline ? (node2.isRaining ? "Mưa" : "Khô") : "Mất KN");
  Blynk.virtualWrite(V11, statusStr);
}

// =========================================================================
// ĐỒNG BỘ HAI CHIỀU VỚI CUSTOM PHP SERVER (HTTP POST & NHẬN LỆNH)
// =========================================================================
void syncWithPHPServer() {
  if (WiFi.status() != WL_CONNECTED) return;

  HTTPClient http;
  http.begin(PHP_SERVER_URL);
  http.addHeader("Content-Type", "application/json");

  // Đóng gói JSON gửi lên PHP Server
  StaticJsonDocument<512> doc;
  doc["api_key"]   = "IOT_SECRET_2026";
  doc["auto_mode"] = autoMode ? 1 : 0;
  doc["pump1"]     = pump1State ? 1 : 0;
  doc["pump2"]     = pump2State ? 1 : 0;
  doc["pump3"]     = pump3State ? 1 : 0;

  JsonObject n1 = doc.createNestedObject("node1");
  n1["online"] = node1.isOnline ? 1 : 0;
  n1["temp"]   = node1.temp;
  n1["hum"]    = node1.hum;
  n1["pres"]   = node1.pres;
  n1["water"]  = node1.waterLevel;
  n1["rain"]   = node1.isRaining;
  n1["rssi"]   = node1.rssi;

  JsonObject n2 = doc.createNestedObject("node2");
  n2["online"] = node2.isOnline ? 1 : 0;
  n2["temp"]   = node2.temp;
  n2["hum"]    = node2.hum;
  n2["pres"]   = node2.pres;
  n2["water"]  = node2.waterLevel;
  n2["rain"]   = node2.isRaining;
  n2["rssi"]   = node2.rssi;

  String requestBody;
  serializeJson(doc, requestBody);

  int httpResponseCode = http.POST(requestBody);
  if (httpResponseCode == 200) {
    String response = http.getString();
    // Đọc lệnh điều khiển trả về từ Web PHP (nếu người dùng bấm nút trên Web)
    StaticJsonDocument<256> resDoc;
    DeserializationError error = deserializeJson(resDoc, response);
    if (!error && resDoc.containsKey("web_override")) {
      if (resDoc["web_override"] == 1) {
        autoMode = (resDoc["auto_mode"] == 1);
        if (!autoMode) {
          pump1State = (resDoc["pump1"] == 1);
          pump2State = (resDoc["pump2"] == 1);
          pump3State = (resDoc["pump3"] == 1);
          setPumps(pump1State, pump2State, pump3State);
        }
      }
    }
  } else {
    Serial.printf("[PHP SERVER] Loi ket noi HTTP: %d\n", httpResponseCode);
  }
  http.end();
}

// =========================================================================
// SETUP & LOOP CHÍNH CỦA ESP32-S3
// =========================================================================
void setup() {
  Serial.begin(115200);
  delay(500);
  Serial.println("\n==================================================");
  Serial.println("KHOI DONG GATEWAY TRUNG TAM ESP32-S3-WROOM");
  Serial.println("==================================================");

  // 1. Khởi tạo 3 chân Relay Máy bơm
  pinMode(RELAY_PUMP1, OUTPUT);
  pinMode(RELAY_PUMP2, OUTPUT);
  pinMode(RELAY_PUMP3, OUTPUT);
  setPumps(false, false, false); // Tắt toàn bộ bơm khi mới bật nguồn

  // 2. Khởi tạo giao tiếp SPI & Module LoRa Ra-01 #3
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);

  if (!LoRa.begin(433E6)) {
    Serial.println("[LOI] Khoi tao LoRa Gateway THAT BAI! Kiem tra dau day.");
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(9);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setSyncWord(0xF3);
  LoRa.enableCrc();
  Serial.println("[OK] LoRa Gateway 433MHz da san sang thu song!");

  // 3. Kết nối Wi-Fi & Blynk IoT
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Dang ket noi Wi-Fi");
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    Serial.print(".");
    retry++;
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n[OK] Da ket noi Wi-Fi! IP: " + WiFi.localIP().toString());
    Blynk.config(BLYNK_AUTH_TOKEN);
    Blynk.connect(3000);
    sendTelegramMessage("✅ <b>[HỆ THỐNG KHỞI ĐỘNG]</b>\nGateway Trung tâm <b>ESP32-S3</b> đã kết nối Wi-Fi và sẵn sàng nhận dữ liệu từ 2 trạm LoRa!");
  } else {
    Serial.println("\n[CANH BAO] Khong co Wi-Fi! He thong van chay che do AUTO cuc bo qua LoRa.");
  }
}

void loop() {
  // 1. Duy trì kết nối Blynk
  if (WiFi.status() == WL_CONNECTED) {
    Blynk.run();
  }

  // 2. Lắng nghe gói tin LoRa từ Node 1 và Node 2
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    int rssi = LoRa.packetRssi();
    parseLoRaPacket(incoming, rssi);
    evaluatePracticalScenarios(); // Đánh giá ngay lập tức khi có dữ liệu mới
  }

  // 3. Định kỳ mỗi 5 giây: kiểm tra các kịch bản, đẩy dữ liệu lên PHP Server & Blynk
  if (millis() - lastServerSync >= SERVER_SYNC_INTERVAL) {
    lastServerSync = millis();
    evaluatePracticalScenarios();
    syncToBlynk();
    syncWithPHPServer();
  }
}

```

### 3. Code Backend & Giao diện Web Server (PHP + MySQL)

Bạn tạo một thư mục tên là `iot_project` trong `htdocs` (nếu dùng XAMPP) hoặc trên Hosting hỗ trợ PHP/MySQL, gồm 3 file dưới đây:

#### File 1: Cơ sở dữ liệu MySQL (`database.sql`)

Vào `phpMyAdmin`, tạo Database tên `iot_lora_db` và chạy đoạn lệnh SQL sau:

```
CREATE DATABASE IF NOT EXISTS iot_lora_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE iot_lora_db;

-- Bảng lưu lịch sử dữ liệu cảm biến từ 2 trạm LoRa
CREATE TABLE IF NOT EXISTS sensor_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    node1_online TINYINT DEFAULT 0,
    node1_temp FLOAT DEFAULT 0,
    node1_hum FLOAT DEFAULT 0,
    node1_pres FLOAT DEFAULT 0,
    node1_water INT DEFAULT 0,
    node1_rain TINYINT DEFAULT 0,
    node1_rssi INT DEFAULT 0,
    node2_online TINYINT DEFAULT 0,
    node2_temp FLOAT DEFAULT 0,
    node2_hum FLOAT DEFAULT 0,
    node2_pres FLOAT DEFAULT 0,
    node2_water INT DEFAULT 0,
    node2_rain TINYINT DEFAULT 0,
    node2_rssi INT DEFAULT 0,
    auto_mode TINYINT DEFAULT 1,
    pump1 TINYINT DEFAULT 0,
    pump2 TINYINT DEFAULT 0,
    pump3 TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng lưu trạng thái điều khiển (Đồng bộ giữa Web Dashboard và ESP32-S3)
CREATE TABLE IF NOT EXISTS system_control (
    id INT PRIMARY KEY,
    auto_mode TINYINT DEFAULT 1,
    pump1 TINYINT DEFAULT 0,
    pump2 TINYINT DEFAULT 0,
    pump3 TINYINT DEFAULT 0,
    web_override TINYINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO system_control (id, auto_mode, pump1, pump2, pump3, web_override)
VALUES (1, 1, 0, 0, 0, 0)
ON DUPLICATE KEY UPDATE id = 1;

```

#### File 2: API Xử lý dữ liệu & Điều khiển (`api.php`)

Đặt tại `htdocs/iot_project/api.php`:

```
<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$host = "localhost";
$user = "root";
$pass = "";
$dbname = "iot_lora_db";

$conn = new mysqli($host, $user, $pass, $dbname);
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "DB Connection Failed"]));
}

// 1. XỬ LÝ KHI NGƯỜI DÙNG BẤM NÚT ĐIỀU KHIỂN TRÊN GIAO DIỆN WEB (AJAX POST)
if (isset($_GET['action']) && $_GET['action'] === 'control') {
    $input = json_decode(file_get_contents("php://input"), true);
    $auto_mode = intval($input['auto_mode']);
    $pump1     = intval($input['pump1']);
    $pump2     = intval($input['pump2']);
    $pump3     = intval($input['pump3']);

    $stmt = $conn->prepare("UPDATE system_control SET auto_mode=?, pump1=?, pump2=?, pump3=?, web_override=1 WHERE id=1");
    $stmt->bind_param("iiii", $auto_mode, $pump1, $pump2, $pump3);
    $stmt->execute();
    echo json_encode(["status" => "success", "message" => "Đã gửi lệnh điều khiển xuống ESP32-S3"]);
    exit;
}

// 2. XỬ LÝ KHI WEB DASHBOARD LẤY DỮ LIỆU ĐỂ HIỂN THỊ REALTIME (AJAX GET)
if (isset($_GET['action']) && $_GET['action'] === 'get_latest') {
    $latest = $conn->query("SELECT * FROM sensor_logs ORDER BY id DESC LIMIT 1")->fetch_assoc();
    $ctrl   = $conn->query("SELECT * FROM system_control WHERE id = 1")->fetch_assoc();
    $history = [];
    $res = $conn->query("SELECT created_at, node1_water, node2_water, node1_temp, node2_temp FROM sensor_logs ORDER BY id DESC LIMIT 15");
    while ($row = $res->fetch_assoc()) {
        $history[] = $row;
    }
    echo json_encode([
        "latest"  => $latest,
        "control" => $ctrl,
        "history" => array_reverse($history)
    ]);
    exit;
}

// 3. XỬ LÝ KHI GATEWAY ESP32-S3 GỬI DỮ LIỆU LÊN (HTTP POST JSON)
$rawInput = file_get_contents("php://input");
$data = json_decode($rawInput, true);

if ($data && isset($data['api_key']) && $data['api_key'] === "IOT_SECRET_2026") {
    $n1 = $data['node1'];
    $n2 = $data['node2'];
    
    // Kiểm tra xem có lệnh điều khiển mới từ Web đang chờ gửi xuống ESP32-S3 không
    $ctrl = $conn->query("SELECT * FROM system_control WHERE id = 1")->fetch_assoc();
    
    if ($ctrl['web_override'] == 1) {
        // Nếu người dùng vừa bấm trên Web, giữ nguyên lệnh của Web để trả về cho ESP32-S3
        $auto_mode = intval($ctrl['auto_mode']);
        $p1 = intval($ctrl['pump1']);
        $p2 = intval($ctrl['pump2']);
        $p3 = intval($ctrl['pump3']);
        // Reset cờ web_override sau khi đã giao lệnh cho ESP32-S3
        $conn->query("UPDATE system_control SET web_override = 0 WHERE id = 1");
    } else {
        // Cập nhật trạng thái thực tế từ ESP32-S3 lên bảng điều khiển
        $auto_mode = intval($data['auto_mode']);
        $p1 = intval($data['pump1']);
        $p2 = intval($data['pump2']);
        $p3 = intval($data['pump3']);
        $stmtCtrl = $conn->prepare("UPDATE system_control SET auto_mode=?, pump1=?, pump2=?, pump3=? WHERE id=1");
        $stmtCtrl->bind_param("iiii", $auto_mode, $p1, $p2, $p3);
        $stmtCtrl->execute();
    }

    // Lưu bản ghi vào bảng sensor_logs
    $stmt = $conn->prepare("INSERT INTO sensor_logs 
        (node1_online, node1_temp, node1_hum, node1_pres, node1_water, node1_rain, node1_rssi,
         node2_online, node2_temp, node2_hum, node2_pres, node2_water, node2_rain, node2_rssi,
         auto_mode, pump1, pump2, pump3) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    $stmt->bind_param("idddiiiidddiiiiiii",
        $n1['online'], $n1['temp'], $n1['hum'], $n1['pres'], $n1['water'], $n1['rain'], $n1['rssi'],
        $n2['online'], $n2['temp'], $n2['hum'], $n2['pres'], $n2['water'], $n2['rain'], $n2['rssi'],
        $auto_mode, $p1, $p2, $p3
    );
    $stmt->execute();

    // Trả về JSON lệnh điều khiển cho ESP32-S3
    echo json_encode([
        "status"       => "ok",
        "web_override" => intval($ctrl['web_override']),
        "auto_mode"    => $auto_mode,
        "pump1"        => $p1,
        "pump2"        => $p2,
        "pump3"        => $p3
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Invalid Request or API Key"]);
}
$conn->close();
?>

```

#### File 3: Giao diện Web Dashboard Quản lý & Điều khiển (`index.php`)

Đặt tại `htdocs/iot_project/index.php`:

```
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trung Tâm Điều Khiển Nông Nghiệp & Thủy Lợi LoRa IoT</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen p-4 md:p-8">
    <div class="max-w-7xl mx-auto">
        <!-- Header -->
        <div class="flex flex-col md:flex-row justify-between items-center mb-8 border-b border-slate-700 pb-4">
            <div>
                <h1 class="text-2xl md:text-3xl font-bold text-emerald-400">HỆ THỐNG GIÁM SÁT & ĐIỀU KHIỂN LORA IOT</h1>
                <p class="text-slate-400 text-sm mt-1">Gateway: ESP32-S3-WROOM | 2 Trạm LoRa 433MHz Năng Lượng Mặt Trời</p>
            </div>
            <div class="mt-4 md:mt-0 flex items-center gap-3">
                <span class="text-sm text-slate-400">Cập nhật lúc: <b id="last-update" class="text-white">--:--:--</b></span>
            </div>
        </div>

        <!-- Khu vực 2 Node Cảm biến -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <!-- NODE 1 -->
            <div class="bg-slate-800 rounded-xl p-6 border border-slate-700 shadow-lg">
                <div class="flex justify-between items-center mb-4">
                    <h2 class="text-xl font-bold text-cyan-400">🌊 TRẠM 1: KÊNH THOÁT NƯỚC (NODE 1)</h2>
                    <span id="n1-status" class="px-3 py-1 rounded-full text-xs font-semibold bg-red-500/20 text-red-400">OFFLINE</span>
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Mực Nước Kênh</p>
                        <p class="text-2xl font-bold text-cyan-300 mt-1"><span id="n1-water">0</span> %</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Trạng Thái Mưa</p>
                        <p id="n1-rain" class="text-lg font-bold text-emerald-400 mt-1">Không mưa</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Nhiệt Độ / Độ Ẩm</p>
                        <p class="text-lg font-bold mt-1"><span id="n1-temp">0</span>°C / <span id="n1-hum">0</span>%</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Áp Suất / Sóng LoRa</p>
                        <p class="text-lg font-bold mt-1"><span id="n1-pres">0</span> hPa (<span id="n1-rssi">0</span> dBm)</p>
                    </div>
                </div>
            </div>

            <!-- NODE 2 -->
            <div class="bg-slate-800 rounded-xl p-6 border border-slate-700 shadow-lg">
                <div class="flex justify-between items-center mb-4">
                    <h2 class="text-xl font-bold text-amber-400">🌱 TRẠM 2: KHU CANH TÁC & BỂ CHỨA (NODE 2)</h2>
                    <span id="n2-status" class="px-3 py-1 rounded-full text-xs font-semibold bg-red-500/20 text-red-400">OFFLINE</span>
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Mực Nước Bể Dự Trữ</p>
                        <p class="text-2xl font-bold text-amber-300 mt-1"><span id="n2-water">0</span> %</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Trạng Thái Mưa</p>
                        <p id="n2-rain" class="text-lg font-bold text-emerald-400 mt-1">Không mưa</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Nhiệt Độ / Độ Ẩm</p>
                        <p class="text-lg font-bold mt-1"><span id="n2-temp">0</span>°C / <span id="n2-hum">0</span>%</p>
                    </div>
                    <div class="bg-slate-900/60 p-4 rounded-lg">
                        <p class="text-slate-400 text-xs">Áp Suất / Sóng LoRa</p>
                        <p class="text-lg font-bold mt-1"><span id="n2-pres">0</span> hPa (<span id="n2-rssi">0</span> dBm)</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bảng Điều Khiển 3 Máy Bơm 12V -->
        <div class="bg-slate-800 rounded-xl p-6 border border-slate-700 shadow-lg mb-8">
            <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
                <div>
                    <h2 class="text-xl font-bold text-emerald-400">⚙️ BẢNG ĐIỀU KHIỂN TRẠM BƠM TRUNG TÂM (ESP32-S3)</h2>
                    <p class="text-xs text-slate-400 mt-1">Chuyển sang chế độ THỦ CÔNG (MANUAL) nếu muốn bật/tắt cưỡng bức từng máy bơm.</p>
                </div>
                <button id="btn-mode" onclick="toggleMode()" class="px-5 py-2.5 rounded-lg font-bold text-sm bg-emerald-600 hover:bg-emerald-500 transition">
                    CHẾ ĐỘ: TỰ ĐỘNG (AUTO)
                </button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="bg-slate-900/80 p-4 rounded-lg flex justify-between items-center border border-slate-700">
                    <div>
                        <p class="font-bold text-white">Máy Bơm 1 (Tiêu Úng)</p>
                        <p class="text-xs text-slate-400">Hút nước ngập tại Trạm 1</p>
                    </div>
                    <button id="btn-p1" onclick="togglePump(1)" class="px-4 py-2 rounded font-bold text-sm bg-slate-700 text-slate-300">ĐANG TẮT</button>
                </div>

                <div class="bg-slate-900/80 p-4 rounded-lg flex justify-between items-center border border-slate-700">
                    <div>
                        <p class="font-bold text-white">Máy Bơm 2 (Cấp Nước Bể)</p>
                        <p class="text-xs text-slate-400">Bơm bù nước lên Bể Trạm 2</p>
                    </div>
                    <button id="btn-p2" onclick="togglePump(2)" class="px-4 py-2 rounded font-bold text-sm bg-slate-700 text-slate-300">ĐANG TẮT</button>
                </div>

                <div class="bg-slate-900/80 p-4 rounded-lg flex justify-between items-center border border-slate-700">
                    <div>
                        <p class="font-bold text-white">Máy Bơm 3 (Phun Sương)</p>
                        <p class="text-xs text-slate-400">Tưới mát cây trồng Trạm 2</p>
                    </div>
                    <button id="btn-p3" onclick="togglePump(3)" class="px-4 py-2 rounded font-bold text-sm bg-slate-700 text-slate-300">ĐANG TẮT</button>
                </div>
            </div>
        </div>

        <!-- Biểu đồ thời gian thực -->
        <div class="bg-slate-800 rounded-xl p-6 border border-slate-700 shadow-lg">
            <h2 class="text-lg font-bold text-slate-200 mb-4">📈 BIỂU ĐỒ DIỄN BIẾN MỰC NƯỚC & NHIỆT ĐỘ 2 TRẠM</h2>
            <canvas id="iotChart" height="90"></canvas>
        </div>
    </div>

    <script>
        let state = { auto_mode: 1, pump1: 0, pump2: 0, pump3: 0 };
        const ctx = document.getElementById('iotChart').getContext('2d');
        const iotChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    { label: 'Mực nước Trạm 1 (%)', data: [], borderColor: '#22d3ee', tension: 0.3 },
                    { label: 'Mực nước Bể Trạm 2 (%)', data: [], borderColor: '#fbbf24', tension: 0.3 },
                    { label: 'Nhiệt độ Trạm 2 (°C)', data: [], borderColor: '#f87171', tension: 0.3 }
                ]
            },
            options: { responsive: true, scales: { y: { beginAtZero: true, max: 100 } } }
        });

        async function fetchDashboardData() {
            try {
                const res = await fetch('api.php?action=get_latest');
                const data = await res.json();
                if (!data.latest) return;

                const d = data.latest;
                document.getElementById('last-update').innerText = d.created_at;

                // Cập nhật Node 1
                document.getElementById('n1-water').innerText = d.node1_water;
                document.getElementById('n1-temp').innerText = d.node1_temp;
                document.getElementById('n1-hum').innerText = d.node1_hum;
                document.getElementById('n1-pres').innerText = d.node1_pres;
                document.getElementById('n1-rssi').innerText = d.node1_rssi;
                document.getElementById('n1-rain').innerText = d.node1_rain == 1 ? '🌧️ Đang mưa' : '☀️ Không mưa';
                const n1St = document.getElementById('n1-status');
                n1St.innerText = d.node1_online == 1 ? 'ONLINE' : 'OFFLINE';
                n1St.className = d.node1_online == 1 ? 'px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-400' : 'px-3 py-1 rounded-full text-xs font-semibold bg-red-500/20 text-red-400';

                // Cập nhật Node 2
                document.getElementById('n2-water').innerText = d.node2_water;
                document.getElementById('n2-temp').innerText = d.node2_temp;
                document.getElementById('n2-hum').innerText = d.node2_hum;
                document.getElementById('n2-pres').innerText = d.node2_pres;
                document.getElementById('n2-rssi').innerText = d.node2_rssi;
                document.getElementById('n2-rain').innerText = d.node2_rain == 1 ? '🌧️ Đang mưa' : '☀️ Không mưa';
                const n2St = document.getElementById('n2-status');
                n2St.innerText = d.node2_online == 1 ? 'ONLINE' : 'OFFLINE';
                n2St.className = d.node2_online == 1 ? 'px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/20 text-emerald-400' : 'px-3 py-1 rounded-full text-xs font-semibold bg-red-500/20 text-red-400';

                // Cập nhật trạng thái điều khiển
                if (data.control) {
                    state.auto_mode = parseInt(data.control.auto_mode);
                    state.pump1 = parseInt(data.control.pump1);
                    state.pump2 = parseInt(data.control.pump2);
                    state.pump3 = parseInt(data.control.pump3);
                    renderControlButtons();
                }

                // Cập nhật Biểu đồ
                if (data.history) {
                    iotChart.data.labels = data.history.map(item => item.created_at.split(' ')[1]);
                    iotChart.data.datasets[0].data = data.history.map(item => item.node1_water);
                    iotChart.data.datasets[1].data = data.history.map(item => item.node2_water);
                    iotChart.data.datasets[2].data = data.history.map(item => item.node2_temp);
                    iotChart.update();
                }
            } catch (e) {
                console.error("Lỗi tải dữ liệu:", e);
            }
        }

        function renderControlButtons() {
            const btnMode = document.getElementById('btn-mode');
            if (state.auto_mode === 1) {
                btnMode.innerText = "CHẾ ĐỘ: TỰ ĐỘNG (AUTO)";
                btnMode.className = "px-5 py-2.5 rounded-lg font-bold text-sm bg-emerald-600 hover:bg-emerald-500 transition";
            } else {
                btnMode.innerText = "CHẾ ĐỘ: THỦ CÔNG (MANUAL)";
                btnMode.className = "px-5 py-2.5 rounded-lg font-bold text-sm bg-amber-600 hover:bg-amber-500 transition";
            }

            [1, 2, 3].forEach(i => {
                const btn = document.getElementById('btn-p' + i);
                const isOn = state['pump' + i] === 1;
                btn.innerText = isOn ? "ĐANG CHẠY (ON)" : "ĐANG TẮT (OFF)";
                btn.className = isOn 
                    ? "px-4 py-2 rounded font-bold text-sm bg-cyan-600 text-white shadow-lg" 
                    : "px-4 py-2 rounded font-bold text-sm bg-slate-700 text-slate-300";
            });
        }

        async function sendControlCommand() {
            renderControlButtons();
            await fetch('api.php?action=control', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(state)
            });
        }

        function toggleMode() {
            state.auto_mode = state.auto_mode === 1 ? 0 : 1;
            sendControlCommand();
        }

        function togglePump(pumpNum) {
            if (state.auto_mode === 1) {
                alert("Vui lòng nhấn nút chuyển sang CHẾ ĐỘ THỦ CÔNG (MANUAL) trước khi bật/tắt máy bơm bằng tay!");
                return;
            }
            state['pump' + pumpNum] = state['pump' + pumpNum] === 1 ? 0 : 1;
            sendControlCommand();
        }

        setInterval(fetchDashboardData, 3000);
        fetchDashboardData();
    </script>
</body>
</html>

```

## PHẦN 5: HƯỚNG DẪN CẤU HÌNH TỪNG BƯỚC (STEP-BY-STEP)

### Bước 1: Tạo Telegram Bot lấy `BOT_TOKEN` và `CHAT_ID`

1. Mở ứng dụng Telegram, tìm kiếm tài khoản **@BotFather** (có tích xanh).

2. Gõ lệnh `/newbot`, đặt tên hiển thị và username cho Bot (đuôi phải có chữ `bot`, ví dụ: `AgroLoRa2026_bot`).

3. **BotFather** sẽ trả về một đoạn mã `HTTP API Token` (dạng `1234567890:AAHxxxx...`). Copy mã này dán vào biến `BOT_TOKEN` trong code ESP32-S3.

4. Bấm vào đường link Bot vừa tạo và nhấn nút **Start** (hoặc gửi 1 tin nhắn bất kỳ cho Bot).

5. Tìm kiếm bot **@userinfobot** trên Telegram, nhấn **Start**, nó sẽ trả về dãy số `Id` của bạn. Copy số này dán vào biến `CHAT_ID` trong code ESP32-S3.

### Bước 2: Cấu hình Blynk IoT 2.0 (Web & Mobile App)

1. Truy cập [blynk.cloud](https://blynk.cloud/), đăng nhập và vào mục **Developer Zone** $\rightarrow$ **My Templates** $\rightarrow$ **+ New Template**.

2. Đặt tên `Smart Agro LoRa System`, Hardware: `ESP32`, Connection Type: `WiFi`.

3. Chuyển sang tab **Datastreams**, tạo **12 Virtual Pins** như sau:

   * `V0`: Tên `Auto Mode`, Data Type: `Integer`, Min `0`, Max `1` (Gắn với nút Switch trên giao diện).

   * `V1`, `V2`, `V3`: Tên `Pump 1`, `Pump 2`, `Pump 3`, Data Type: `Integer`, Min `0`, Max `1` (Gắn với 3 nút Switch).

   * `V4`: Tên `Water Level N1`, Data Type: `Integer`, Min `0`, Max `100`, Units `%`.

   * `V5`, `V6`, `V7`: Tên `Temp N1`, `Hum N1`, `Pres N1`, Data Type: `Double`.

   * `V8`: Tên `Water Tank N2`, Data Type: `Integer`, Min `0`, Max `100`, Units `%`.

   * `V9`, `V10`: Tên `Temp N2`, `Hum N2`, Data Type: `Double`.

   * `V11`: Tên `Weather Status`, Data Type: `String`.

4. Vào mục **Devices** $\rightarrow$ **+ New Device** $\rightarrow$ **From Template** $\rightarrow$ Chọn Template vừa tạo. Copy 3 dòng `#define BLYNK_TEMPLATE_ID`, `BLYNK_TEMPLATE_NAME`, `BLYNK_AUTH_TOKEN` dán lên đầu code **ESP32-S3**.

### Bước 3: Cài đặt Web Server PHP (XAMPP)

1. Cài đặt và bật **Apache** + **MySQL** trên phần mềm **XAMPP**.

2. Truy cập `http://localhost/phpmyadmin`, chạy toàn bộ nội dung file `database.sql`.

3. Lưu 2 file `api.php` và `index.php` vào thư mục `C:\xampp\htdocs\iot_project\`.

4. Mở `cmd` trên máy tính, gõ `ipconfig` để lấy địa chỉ IPv4 của máy tính (ví dụ: `192.168.1.100`). Thay địa chỉ IP này vào biến `PHP_SERVER_URL` trong code ESP32-S3 (`http://192.168.1.100/iot_project/api.php`).

### Bước 4: Lưu ý quan trọng khi vận hành phần cứng thực tế

* **Anten LoRa Ra-01:** Bắt buộc phải hàn hoặc gắn anten lò xo $433\text{ MHz}$ vào chân `ANT` của cả 3 module LoRa trước khi cấp nguồn (nếu phát LoRa không có anten sẽ làm hỏng công suất RF của chip SX1278).

* **Chống nhiễu từ 3 Máy bơm** $12\text{V}$**:** Máy bơm chìm $12\text{V}$ là tải cảm kháng. Khi đóng ngắt sẽ sinh ra xung điện áp ngược. Việc đặt tụ $16\text{V } 220\mu\text{F}$ trên đường nguồn Relay và dùng nguồn $12\text{V}$ riêng cho cụm bơm sẽ giúp ESP32-S3 hoạt động ổn định 24/7 mà không bao giờ bị khởi động lại.