# DỰ ÁN HỆ THỐNG IOT LORA ĐA NĂNG VÙNG SÂU VÙNG XA
**Giám Sát Môi Trường, Chống Ngập Thủy Lợi & Tưới Tiêu Tự Động Sử Dụng Năng Lượng Mặt Trời**

---

## 1. TỔNG QUAN KIẾN TRÚC & BÀI TOÁN THỰC TẾ

Với danh sách linh kiện bạn đang sở hữu, mô hình thực tế tối ưu và hoàn chỉnh nhất là **Hệ thống Trạm Giám Sát & Điều Khiển Kép Vùng Sâu Vùng Xa (Nông Nghiệp Thông Minh kết hợp Cảnh Báo Ngập/Tràn Hồ Chứa)**:

* **Node 1 (ESP8266 - Trạm Thủy Lợi / Cảnh Báo Ngập Úng):** Đặt tại kênh thoát nước hoặc hồ chứa chính ở khu vực không có WiFi. Sử dụng **Cảm biến mực nước 1**, **Cảm biến mưa 1**, **BME280 1**, **Còi báo động 1** và điều khiển **Máy bơm 1 ($12\,\text{V}$ - Bơm xả chống ngập / cấp nước nguồn)**.
* **Node 2 (ESP8266 - Trạm Vườn Nông Nghiệp Thông Minh):** Đặt tại khu vực canh tác/nhà màng ở xa. Sử dụng **Cảm biến mực nước 2** (đo mực nước trong bồn trữ tưới cây), **Cảm biến mưa 2**, **BME280 2**, **Còi báo động 2** và điều khiển **Máy bơm 2 ($12\,\text{V}$ - Bơm tưới gốc)** cùng **Máy bơm 3 ($12\,\text{V}$ - Bơm phun sương làm mát)**.
* **Gateway Trung Tâm (ESP32-S3-WROOM):** Đặt tại nhà điều hành (nơi có sóng WiFi). Giao tiếp **2 chiều qua sóng LoRa $433\,\text{MHz}$** với Node 1 & Node 2, đồng bộ dữ liệu lên **Web Server PHP tự code**, **Blynk IoT**, và bắn cảnh báo khẩn cấp qua **Telegram**.

```
+-------------------------------------------------------------------------+
|                    VÙNG SÂU VÙNG XA (KHÔNG CÓ WIFI)                     |
|                                                                         |
|  [ NODE 1: TRẠM THỦY LỢI / CHỐNG NGẬP ]   [ NODE 2: TRẠM NÔNG NGHIỆP ]  |
|  - ESP8266 + Pin NLMT 3.7V + Tụ 220uF     - ESP8266 + Pin NLMT 3.7V     |
|  - BME280 (Nhiệt, Ẩm, Áp suất)            - BME280 (Nhiệt, Ẩm, Áp suất) |
|  - Cảm biến Mực nước 1 (Kênh/Hồ)          - Cảm biến Mực nước 2 (Bồn)   |
|  - Cảm biến Mưa 1                         - Cảm biến Mưa 2              |
|  - Còi báo động 1 (5V)                    - Còi báo động 2 (5V)         |
|  - Relay 1 -> Máy bơm 1 (12V Xả ngập)     - Relay 2 -> Máy bơm 2 (Tưới) |
|                                           - Relay 3 -> Máy bơm 3 (Sương)|
+-------------------+-----------------------------------+-----------------+
                    |                                   |
                    |  Sóng LoRa 433MHz (2 Chiều)       |
                    +-----------------+-----------------+
                                      |
                                      v
+-------------------------------------------------------------------------+
|                 TRẠM TRUNG TÂM CÓ WIFI (GATEWAY)                        |
|                 ESP32-S3-WROOM + LoRa Ra-01 SX1278                      |
|                 (Cấp nguồn từ Pin 3.7V / Mạch sạc + Tụ 220uF)           |
+-------------------+-----------------+-----------------+-----------------+
                    |                 |                 |
         HTTP POST/GET JSON     Blynk Cloud API   HTTPS Bot API
                    |                 |                 |
                    v                 v                 v
           [ WEB SERVER PHP ]   [ BLYNK IOT ]    [ TELEGRAM BOT ]
           (Auto/Manual + DB)   (App Mobile)     (Cảnh báo tức thì)
```

---

## 2. 7 TRƯỜNG HỢP THỰC TẾ (USE CASES) ĐƯỢC TRIỂN KHAI TRONG HỆ THỐNG

Hệ thống hoạt động độc lập tại các Node ngay cả khi mất kết nối về trung tâm (cơ chế *Edge Computing* an toàn) và hỗ trợ ghi đè từ xa:

1. **TH1 - Chống ngập úng tự động & Báo động lũ tại chỗ (Node 1):**
   * **Điều kiện:** Khi **Cảm biến mực nước 1** tại kênh/hồ dâng cao $\ge 80\%$ (chế độ `AUTO`).
   * **Hành động:** Node 1 lập tức kích hoạt **Relay 1 bật Máy bơm 1** để hút/xả nước chống ngập, bật **Còi báo động 1** cảnh báo tại hiện trường, đồng thời gửi gói tin khẩn qua LoRa về ESP32-S3 để đẩy tin nhắn cảnh báo đỏ lên **Telegram**, **Web PHP** và **Blynk**.
2. **TH2 - Tưới cây thông minh kết hợp tránh tưới khi trời mưa (Node 2):**
   * **Điều kiện:** Độ ẩm không khí từ **BME280 2** thấp ($< 55\%$) HOẶC nhiệt độ cao ($> 32^\circ\text{C}$), **VÀ** **Cảm biến mưa 2** xác nhận trời đang khô ráo, **VÀ** **Cảm biến mực nước 2** trong bồn chứa còn đủ nước ($> 20\%$).
   * **Hành động:** Node 2 tự động bật **Máy bơm 2** để tưới cây. Nếu đang tưới mà trời đổ mưa (Cảm biến mưa 2 báo ướt), hệ thống lập tức ngắt Máy bơm 2 để tiết kiệm nước và điện mặt trời.
3. **TH3 - Phun sương hạ nhiệt chống sốc nhiệt cho vườn (Node 2):**
   * **Điều kiện:** Nhiệt độ **BME280 2** vượt ngưỡng nắng nóng gay gắt ($\ge 35^\circ\text{C}$), độ ẩm $< 50\%$, và bồn chứa còn nước ($> 20\%$).
   * **Hành động:** Node 2 bật **Máy bơm 3 (Bơm phun sương)** để làm mát khu vực canh tác và gửi thông báo trạng thái lên Web/Blynk.
4. **TH4 - Bảo vệ chống cháy máy bơm khi cạn bồn chứa (Node 2):**
   * **Điều kiện:** **Cảm biến mực nước 2** (đo bồn trữ nước tưới) tụt xuống mức nguy hiểm ($\le 20\%$).
   * **Hành động:** Hệ thống **cưỡng bức ngắt ngay lập tức cả Máy bơm 2 và Máy bơm 3** để chống chạy khô gây cháy bơm $12\,\text{V}$, kích hoạt **Còi báo động 2** và gửi cảnh báo *"Bồn chứa Node 2 đã cạn nước!"* qua Telegram.
5. **TH5 - Dự báo & Cảnh báo sớm mưa giông / lũ quét nhờ áp suất khí quyển (Node 1 & 2):**
   * **Điều kiện:** Cảm biến **BME280** ghi nhận áp suất khí quyển tụt thấp bất thường ($< 1002\,\text{hPa}$) kết hợp với **Cảm biến mưa** báo có mưa lớn và mực nước bắt đầu dâng ($> 60\%$).
   * **Hành động:** Gateway ESP32-S3 phân tích tổ hợp dữ liệu, gửi cảnh báo sớm qua Telegram: *"CẢNH BÁO THỜI TIẾT CỰC ĐOAN: Áp suất giảm thấp kèm mưa lớn, nguy cơ ngập lũ cao!"*.
6. **TH6 - Điều khiển Cưỡng bức Thủ công (Manual Override) 2 chiều từ Web PHP & Blynk qua LoRa:**
   * **Điều kiện:** Người dùng chuyển công tắc từ `AUTO` sang `MANUAL` trên giao diện **Web App PHP** hoặc ứng dụng **Blynk IoT**, sau đó nhấn nút Bật/Tắt bất kỳ Máy bơm ($1, 2, 3$) hoặc Còi báo động ($1, 2$).
   * **Hành động:** Gateway ESP32-S3 nhận lệnh từ Server PHP / Blynk và **phát gói tin điều khiển ngược qua sóng LoRa $433\,\text{MHz}$** xuống Node 1 hoặc Node 2 để đóng/ngắt Relay tức thì.
7. **TH7 - Cảnh báo đứt kết nối Trạm vùng sâu vùng xa (Watchdog Offline Alert):**
   * **Điều kiện:** Node 1 hoặc Node 2 bị sập nguồn hoặc mất sóng LoRa không phản hồi về Gateway quá $60$ giây.
   * **Hành động:** ESP32-S3 tự động đánh dấu Node đó `OFFLINE` trên Web PHP/Blynk và bắn tin nhắn khẩn cấp qua Telegram để kỹ thuật viên đi kiểm tra.

---

## 3. GIẢI PHÁP KỸ THUẬT PHẦN CỨNG & SƠ ĐỒ ĐẤU DÂY CHI TIẾT

### 3.1. Phân bổ 5 Tụ hóa $16\,\text{V}$ $220\,\mu\text{F}$ và Nguồn Năng Lượng Mặt Trời
Trong thực tế, mạch ESP8266/ESP32 chạy bằng pin Li-ion $3.7\,\text{V}$ kết hợp LoRa SX1278 rất dễ bị **treo hoặc khởi động lại (Brown-out Reset)** khi module LoRa phát sóng (dòng tăng vọt lên $\sim 130\,\text{mA}$) hoặc khi đóng cuộn hút Relay. Do đó, **5 tụ $16\,\text{V}$ $220\,\mu\text{F}$** của bạn được bố trí chuẩn kỹ thuật như sau:
* **Tụ số 1, 2, 3:** Hàn/cắm trực tiếp sát chân **`VCC (3.3V)` và `GND` của 3 module LoRa Ra-01 SX1278** (Chân âm có vạch trắng của tụ nối `GND`, chân dương nối `3.3V`). Đây là bí quyết sống còn giúp sóng LoRa truyền xa và không bao giờ bị treo SPI.
* **Tụ số 4, 5:** Cắm tại ngõ ra nguồn của **2 mạch sạc Pin NLMT tại Node 1 và Node 2** (nối song song giữa đường cấp nguồn `VCC` và `GND` nuôi ESP8266 và khối Relay/Còi) để triệt tiêu sụt áp khi đóng Relay.

> **Lưu ý quan trọng về nguồn:**
> * **2 Tấm Pin NLMT + 2 Mạch sạc + Pin $3.7\,\text{V}$:** Cấp nguồn cho **Node 1** và **Node 2** ở ngoài thực địa (và có thể chia sẻ hoặc dùng nguồn sạc tại chỗ cho **Gateway ESP32-S3**). Điện áp từ mạch sạc nuôi ESP8266. Riêng module **LoRa Ra-01 SX1278** và **BME280** bắt buộc phải lấy nguồn từ chân **`3V3`** của ESP8266/ESP32-S3 (tuyệt đối không cấp $5\,\text{V}$ vào Ra-01).
> * **3 Máy bơm $12\,\text{V}$:** Sử dụng nguồn $12\,\text{V}$ riêng (Ắc quy $12\,\text{V}$ hoặc mạch tăng áp Boost từ Pin lên $12\,\text{V}$) đi qua tiếp điểm đóng cắt `COM` và `NO` của Relay. Nối chung `GND` điều khiển để kích Relay.

---

### 3.2. Giải quyết bài toán thiếu chân Analog & GPIO trên ESP8266
ESP8266 chỉ có **duy nhất 1 chân Analog (`A0`)** và một số lượng chân GPIO giới hạn. Để gắn đồng thời **LoRa (SPI)** + **BME280 (I2C)** + **Cảm biến mực nước** + **Cảm biến mưa** + **Relay máy bơm** + **Còi báo động** mà mạch khởi động mượt mà $100\%$:
1. **Cảm biến mực nước (Water Level):** Đấu vào chân **`A0`** để đọc chính xác phần trăm mực nước ($0\% \rightarrow 100\%$).
2. **Cảm biến mưa (Rain Sensor v2):** Sử dụng chân **Digital Output (`DO`)** trên mạch so sánh LM393 đi kèm cảm biến mưa đấu vào chân **`D3` (`GPIO0`)**. Khi trời không mưa, chân `DO` xuất mức `HIGH` ($3.3\,\text{V}$), hoàn toàn tương thích với điều kiện boot của `GPIO0` trên ESP8266! Khi có mưa, `DO` kéo xuống `LOW` ($0\,\text{V}$).
3. **Tối ưu chân LoRa Ra-01:** Thư viện `LoRa.h` khi hoạt động ở chế độ Polling (`LoRa.parsePacket()`) không bắt buộc dùng chân `DIO0` và `RST` điều khiển bằng GPIO. Ta nối chân `RST` của LoRa Ra-01 vào chân `RST` của ESP8266 (hoặc kéo lên `3.3V` qua trở $10\,\text{k}\Omega$) và khai báo `LoRa.setPins(SS, -1, -1)`. Nhờ đó giải phóng được 2 chân GPIO quý giá cho Relay và Còi!

---

### 3.3. Sơ Đồ Đấu Dây Node 1 (ESP8266 - Trạm Thủy Lợi / 1 Bơm)

| Linh kiện tại Node 1 | Chân Linh Kiện | Chân ESP8266 (NodeMCU) | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **LoRa Ra-01 (#1)** | `VCC` / `GND` | `3V3` / `GND` | **Kẹp Tụ $220\,\mu\text{F}$ (#1) sát chân `3V3` - `GND`** |
| | `SCK` | `D5` (`GPIO14`) | Xung nhịp SPI |
| | `MISO` | `D6` (`GPIO12`) | Dữ liệu SPI vào |
| | `MOSI` | `D7` (`GPIO13`) | Dữ liệu SPI ra |
| | `NSS` (`CS`) | `D8` (`GPIO15`) | Chọn chip SPI |
| | `RST` | `RST` (hoặc `3V3`) | Reset đồng bộ cùng ESP8266 |
| **BME280 (#1)** | `VIN` / `GND` | `3V3` / `GND` | Cảm biến Nhiệt độ, Độ ẩm, Áp suất |
| | `SCL` | `D1` (`GPIO5`) | Xung nhịp I2C |
| | `SDA` | `D2` (`GPIO4`) | Dữ liệu I2C |
| **CB Mực Nước (#1)** | `VCC` / `GND` | `3V3` / `GND` | Đặt tại Kênh / Hồ chứa |
| | `S` (Signal) | `A0` (`ADC0`) | Đọc Analog $0 - 1023$ |
| **CB Nước Mưa (#1)** | `VCC` / `GND` | `3V3` / `GND` | Qua bo LM393 đi kèm |
| | `DO` (Digital) | `D3` (`GPIO0`) | `LOW` = Có mưa, `HIGH` = Khô ráo |
| **Relay 1 (Bơm 1)** | `IN` | `D0` (`GPIO16`) | Đóng cắt Máy bơm 1 ($12\,\text{V}$) |
| **Còi Báo Động 1** | Dây Dương (`+`) | `D4` (`GPIO2`) | Qua Transistor/Opto kích còi $5\,\text{V}$ |

---

### 3.4. Sơ Đồ Đấu Dây Node 2 (ESP8266 - Trạm Nông Nghiệp / 2 Bơm)

Tại Node 2 có **2 Máy bơm $12\,\text{V}$** (Bơm 2: Tưới cây, Bơm 3: Phun sương) và **1 Còi báo động**. Ta sử dụng thêm chân `RX` (`GPIO3` chuyển sang chế độ `FUNCTION_3` GPIO) để điều khiển Còi báo động:

| Linh kiện tại Node 2 | Chân Linh Kiện | Chân ESP8266 (NodeMCU) | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **LoRa Ra-01 (#2)** | `VCC`, `GND`, `SCK`, `MISO`, `MOSI`, `NSS`, `RST` | `3V3`, `GND`, `D5`, `D6`, `D7`, `D8`, `RST` | **Kẹp Tụ $220\,\mu\text{F}$ (#2) sát chân `3V3` - `GND`** |
| **BME280 (#2)** | `VIN`, `GND`, `SCL`, `SDA` | `3V3`, `GND`, `D1`, `D2` | Đặt tại Vườn / Nhà màng |
| **CB Mực Nước (#2)** | `VCC`, `GND`, `S` | `3V3`, `GND`, `A0` | Đo mực nước trong Bồn trữ tưới |
| **CB Nước Mưa (#2)** | `VCC`, `GND`, `DO` | `3V3`, `GND`, `D3` (`GPIO0`) | `LOW` = Có mưa, `HIGH` = Khô ráo |
| **Relay 2 (Bơm 2)** | `IN` | `D0` (`GPIO16`) | Điều khiển Máy bơm 2 (Bơm tưới) |
| **Relay 3 (Bơm 3)** | `IN` | `D4` (`GPIO2`) | Điều khiển Máy bơm 3 (Phun sương) |
| **Còi Báo Động 2** | Dây Dương (`+`) | `RX` (`GPIO3`) | Cấu hình `pinMode(3, FUNCTION_3)` |

---

### 3.5. Sơ Đồ Đấu Dây Gateway Trung Tâm (ESP32-S3-WROOM)

| Linh kiện tại Gateway | Chân LoRa Ra-01 (#3) | Chân ESP32-S3-WROOM | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **Nguồn nuôi LoRa** | `VCC` / `GND` | `3V3` / `GND` | **Kẹp Tụ $220\,\mu\text{F}$ (#3) sát chân `3V3` - `GND`** |
| **SPI Clock** | `SCK` | `GPIO12` | Chân SPI chuẩn trên ESP32-S3 |
| **SPI MISO** | `MISO` | `GPIO13` | Chân SPI chuẩn trên ESP32-S3 |
| **SPI MOSI** | `MOSI` | `GPIO11` | Chân SPI chuẩn trên ESP32-S3 |
| **SPI Chip Select** | `NSS` (`CS`) | `GPIO10` | Chọn chip LoRa |
| **LoRa Reset** | `RST` | `GPIO9` | Chân Reset phần cứng cho LoRa |
| **LoRa Interrupt** | `DIO0` | `GPIO4` | Chân báo ngắt gói tin LoRa |

---

## 4. GIAO THỨC TRUYỀN THÔNG LORA 2 CHIỀU CHỐNG XUNG ĐỘT

Để 2 Node gửi dữ liệu về Gateway và nhận lệnh điều khiển (`AUTO/MANUAL`, bật/tắt Bơm & Còi) mà không bị trùng sóng $433\,\text{MHz}$:
1. **Gói tin Uplink (Từ Node 1 / Node 2 gửi lên Gateway):**
   * Định dạng chuỗi CSV gọn nhẹ giúp truyền xa và nhanh:
     `UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,RAIN,PUMP_A,PUMP_B,BUZZ,MODE`
   * Ví dụ Node 1 gửi: `UP,1,29.5,78.2,1008.5,85,1,1,0,1,1`
   * Ví dụ Node 2 gửi: `UP,2,34.2,48.0,1009.1,65,0,1,0,0,1`
   * Node 1 gửi định kỳ mỗi $5000\,\text{ms}$, Node 2 gửi định kỳ mỗi $6300\,\text{ms}$ (lệch pha thời gian + kiểm tra kênh rỗi để tránh va chạm gói tin).
2. **Gói tin Downlink (Từ Gateway ESP32-S3 phát lệnh xuống Node 1 & Node 2):**
   * Ngay sau khi nhận được gói tin `UP` từ một Node (hoặc ngay khi có thay đổi lệnh từ Web PHP / Blynk), Gateway phát phản hồi:
     `CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ`
   * Trong đó: `MODE` (`1` = Auto, `0` = Manual), `PUMP_A` (`1` = Bật, `0` = Tắt), `PUMP_B` (`1` = Bật, `0` = Tắt), `BUZZ` (`1` = Bật, `0` = Tắt).

---

## 5. MÃ NGUỒN ĐẦY ĐỦ CHO CÁC VI ĐIỀU KHIỂN

### 5.1. Code Node 1 - Trạm Thủy Lợi / Cảnh Báo Ngập (`Node1_ESP8266.ino`)

Cài đặt các thư viện trên Arduino IDE: `LoRa` (Sandeep Mistry), `Adafruit BME280 Library`, `Adafruit Unified Sensor`.

```cpp
/*
 * FILE: Node1_ESP8266.ino
 * VAI TRÒ: Trạm Thủy Lợi & Chống Ngập (Node 1)
 * PHẦN CỨNG: ESP8266 + LoRa Ra-01 + BME280 + CB Mực Nước + CB Mưa + 1 Bơm 12V + Còi 5V
 */

#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

// Định nghĩa chân kết nối trên ESP8266 (NodeMCU)
#define PIN_WATER_ANALOG  A0
#define PIN_I2C_SCL       D1  // GPIO5
#define PIN_I2C_SDA       D2  // GPIO4
#define PIN_RAIN_DIGITAL  D3  // GPIO0 (HIGH = Khô, LOW = Mưa)
#define PIN_LORA_SCK      D5  // GPIO14
#define PIN_LORA_MISO     D6  // GPIO12
#define PIN_LORA_MOSI     D7  // GPIO13
#define PIN_LORA_SS       D8  // GPIO15
#define PIN_RELAY_PUMP1   D0  // GPIO16
#define PIN_BUZZER1       D4  // GPIO2

// Cấu hình mức kích của Module Relay (Đổi thành HIGH/LOW tùy loại Relay của bạn)
#define RELAY_ON          LOW
#define RELAY_OFF         HIGH
#define BUZZER_ON         HIGH
#define BUZZER_OFF        LOW

// Ngưỡng tự động tại Node 1
#define WATER_FLOOD_HIGH  80  // >= 80%: Bật bơm xả ngập & Còi báo động
#define WATER_SAFE_LOW    50  // <= 50%: Tắt bơm xả (Tạo trễ Hysteresis chống nhấp nháy relay)

Adafruit_BME280 bme;
bool bmeAvailable = false;

// Biến trạng thái hệ thống Node 1
const int NODE_ID = 1;
bool isAutoMode = true;       // true = AUTO, false = MANUAL
bool pump1State = false;
bool buzzer1State = false;

float temperature = 0.0;
float humidity = 0.0;
float pressure = 0.0;
int waterLevelPct = 0;
int isRaining = 0;            // 1 = Có mưa, 0 = Không mưa

unsigned long lastSendTime = 0;
const unsigned long sendInterval = 5000; // Gửi mỗi 5 giây

void applyActuators() {
  digitalWrite(PIN_RELAY_PUMP1, pump1State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_BUZZER1, buzzer1State ? BUZZER_ON : BUZZER_OFF);
}

void readSensors() {
  if (bmeAvailable) {
    temperature = bme.readTemperature();
    humidity = bme.readHumidity();
    pressure = bme.readPressure() / 100.0F; // Đổi sang hPa
  } else {
    temperature = 28.5;
    humidity = 75.0;
    pressure = 1008.0;
  }

  // Đọc cảm biến mực nước (Trung bình 5 lần lấy mẫu để chống nhiễu)
  long rawSum = 0;
  for (int i = 0; i < 5; i++) {
    rawSum += analogRead(PIN_WATER_ANALOG);
    delay(2);
  }
  int rawWater = rawSum / 5;
  // Hiệu chuẩn dải ADC của cảm biến mực nước (0 - 750 ứng với 0 - 100%)
  waterLevelPct = map(constrain(rawWater, 0, 750), 0, 750, 0, 100);

  // Đọc cảm biến mưa (LM393 DO: LOW khi có mưa)
  isRaining = (digitalRead(PIN_RAIN_DIGITAL) == LOW) ? 1 : 0;
}

void runAutoLogic() {
  if (!isAutoMode) return; // Nếu đang ở chế độ Thủ công thì giữ nguyên lệnh từ Server/Blynk

  // TH1: Chống ngập úng tự động & Cảnh báo lũ
  if (waterLevelPct >= WATER_FLOOD_HIGH) {
    pump1State = true;
    buzzer1State = true;
  } else if (waterLevelPct <= WATER_SAFE_LOW) {
    pump1State = false;
    buzzer1State = false;
  }

  // TH5 (Tại chỗ): Nếu mưa to + áp suất thấp + nước đã dâng trên 65% -> Bật sớm bơm xả
  if (isRaining == 1 && pressure < 1002.0 && waterLevelPct >= 65) {
    pump1State = true;
  }

  applyActuators();
}

void sendLoRaPacket() {
  // Định dạng: UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,RAIN,PUMP1,0,BUZZ1,MODE
  String payload = "UP," + String(NODE_ID) + "," +
                   String(temperature, 1) + "," +
                   String(humidity, 1) + "," +
                   String(pressure, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(isRaining) + "," +
                   String(pump1State ? 1 : 0) + ",0," +
                   String(buzzer1State ? 1 : 0) + "," +
                   String(isAutoMode ? 1 : 0);

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();
  Serial.println("[NODE 1 -> GATEWAY] " + payload);
}

void parseLoRaDownlink(String msg) {
  // Định dạng: CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ
  if (!msg.startsWith("CMD,")) return;

  int idx1 = msg.indexOf(',');
  int idx2 = msg.indexOf(',', idx1 + 1);
  int idx3 = msg.indexOf(',', idx2 + 1);
  int idx4 = msg.indexOf(',', idx3 + 1);
  int idx5 = msg.indexOf(',', idx4 + 1);

  if (idx5 == -1) return;

  int targetNode = msg.substring(idx1 + 1, idx2).toInt();
  if (targetNode != NODE_ID) return; // Không phải lệnh cho Node 1

  int cmdMode = msg.substring(idx2 + 1, idx3).toInt();
  int cmdPump1 = msg.substring(idx3 + 1, idx4).toInt();
  int cmdBuzz1 = msg.substring(idx5 + 1).toInt();

  isAutoMode = (cmdMode == 1);
  if (!isAutoMode) {
    pump1State = (cmdPump1 == 1);
    buzzer1State = (cmdBuzz1 == 1);
    applyActuators();
  }
  Serial.println("[GATEWAY -> NODE 1] Đã cập nhật lệnh: Mode=" + String(isAutoMode ? "AUTO" : "MANUAL"));
}

void setup() {
  Serial.begin(115200);
  delay(200);

  pinMode(PIN_RAIN_DIGITAL, INPUT_PULLUP);
  pinMode(PIN_RELAY_PUMP1, OUTPUT);
  pinMode(PIN_BUZZER1, OUTPUT);
  applyActuators();

  // Khởi tạo I2C cho BME280
  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76) || bme.begin(0x77)) {
    bmeAvailable = true;
    Serial.println("BME280 Node 1 OK!");
  } else {
    Serial.println("Không tìm thấy BME280 tại Node 1 (Dùng giá trị dự phòng)!");
  }

  // Khởi tạo LoRa Ra-01 SX1278 (433MHz)
  LoRa.setPins(PIN_LORA_SS, -1, -1); // Giải phóng chân RST & DIO0
  if (!LoRa.begin(433E6)) {
    Serial.println("Lỗi khởi tạo LoRa Node 1!");
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSyncWord(0xF3); // Mã đồng bộ mạng riêng chống nhiễu
  Serial.println("Node 1 sẵn sàng hoạt động!");
}

void loop() {
  // 1. Luôn lắng nghe lệnh điều khiển từ Gateway ESP32-S3
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    parseLoRaDownlink(incoming);
  }

  // 2. Đọc cảm biến, xử lý tự động và truyền LoRa định kỳ
  if (millis() - lastSendTime >= sendInterval) {
    lastSendTime = millis();
    readSensors();
    runAutoLogic();
    sendLoRaPacket();
  }
}
```

---

### 5.2. Code Node 2 - Trạm Nông Nghiệp Thông Minh / 2 Bơm (`Node2_ESP8266.ino`)

```cpp
/*
 * FILE: Node2_ESP8266.ino
 * VAI TRÒ: Trạm Nông Nghiệp Thông Minh (Node 2)
 * PHẦN CỨNG: ESP8266 + LoRa Ra-01 + BME280 + CB Mực Nước (Bồn) + CB Mưa + 2 Bơm 12V + Còi 5V
 */

#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

#define PIN_WATER_ANALOG  A0
#define PIN_I2C_SCL       D1  // GPIO5
#define PIN_I2C_SDA       D2  // GPIO4
#define PIN_RAIN_DIGITAL  D3  // GPIO0 (HIGH = Khô, LOW = Mưa)
#define PIN_LORA_SCK      D5  // GPIO14
#define PIN_LORA_MISO     D6  // GPIO12
#define PIN_LORA_MOSI     D7  // GPIO13
#define PIN_LORA_SS       D8  // GPIO15
#define PIN_RELAY_PUMP2   D0  // GPIO16 (Máy bơm 2: Tưới gốc)
#define PIN_RELAY_PUMP3   D4  // GPIO2  (Máy bơm 3: Phun sương làm mát)
#define PIN_BUZZER2       3   // Chân RX (GPIO3) chuyển thành GPIO điều khiển Còi 2

#define RELAY_ON          LOW
#define RELAY_OFF         HIGH
#define BUZZER_ON         HIGH
#define BUZZER_OFF        LOW

// Ngưỡng tự động tại Node 2
#define TANK_EMPTY_LIMIT  20  // <= 20%: Cạn bồn -> Cấm chạy bơm & bật còi báo cạn nước
#define HUMIDITY_LOW      55.0 // Độ ẩm < 55% -> Bật bơm tưới (Bơm 2)
#define TEMP_MIST_HIGH    35.0 // Nhiệt độ >= 35 độ C -> Bật bơm phun sương (Bơm 3)

Adafruit_BME280 bme;
bool bmeAvailable = false;

const int NODE_ID = 2;
bool isAutoMode = true;
bool pump2State = false;      // Bơm tưới cây
bool pump3State = false;      // Bơm phun sương
bool buzzer2State = false;

float temperature = 0.0;
float humidity = 0.0;
float pressure = 0.0;
int waterLevelPct = 0;        // Mực nước bồn trữ
int isRaining = 0;

unsigned long lastSendTime = 0;
const unsigned long sendInterval = 6300; // Lệch chu kỳ với Node 1 để tránh đụng gói LoRa

void applyActuators() {
  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP3, pump3State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_BUZZER2, buzzer2State ? BUZZER_ON : BUZZER_OFF);
}

void readSensors() {
  if (bmeAvailable) {
    temperature = bme.readTemperature();
    humidity = bme.readHumidity();
    pressure = bme.readPressure() / 100.0F;
  } else {
    temperature = 31.0;
    humidity = 60.0;
    pressure = 1008.0;
  }

  long rawSum = 0;
  for (int i = 0; i < 5; i++) {
    rawSum += analogRead(PIN_WATER_ANALOG);
    delay(2);
  }
  int rawWater = rawSum / 5;
  waterLevelPct = map(constrain(rawWater, 0, 750), 0, 750, 0, 100);

  isRaining = (digitalRead(PIN_RAIN_DIGITAL) == LOW) ? 1 : 0;
}

void runAutoLogic() {
  if (!isAutoMode) return;

  // TH4: Bảo vệ chống cháy bơm khi bồn chứa cạn nước (<= 20%)
  if (waterLevelPct <= TANK_EMPTY_LIMIT) {
    pump2State = false;
    pump3State = false;
    buzzer2State = true; // Kêu còi cảnh báo hết nước trong bồn
    applyActuators();
    return;
  } else {
    buzzer2State = false;
  }

  // TH2: Tưới tiêu thông minh (Chỉ tưới khi trời KHÔNG mưa)
  if (isRaining == 1) {
    pump2State = false; // Trời mưa -> Ngắt bơm tưới ngay lập tức
  } else {
    if (humidity < HUMIDITY_LOW || temperature > 32.5) {
      pump2State = true;
    } else if (humidity > (HUMIDITY_LOW + 8.0) && temperature < 31.0) {
      pump2State = false;
    }
  }

  // TH3: Phun sương hạ nhiệt khi nắng nóng gay gắt (>= 35 độ C)
  if (temperature >= TEMP_MIST_HIGH && humidity < 50.0 && isRaining == 0) {
    pump3State = true;
  } else if (temperature <= (TEMP_MIST_HIGH - 2.0) || humidity >= 65.0 || isRaining == 1) {
    pump3State = false;
  }

  applyActuators();
}

void sendLoRaPacket() {
  // Định dạng: UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,RAIN,PUMP2,PUMP3,BUZZ2,MODE
  String payload = "UP," + String(NODE_ID) + "," +
                   String(temperature, 1) + "," +
                   String(humidity, 1) + "," +
                   String(pressure, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(isRaining) + "," +
                   String(pump2State ? 1 : 0) + "," +
                   String(pump3State ? 1 : 0) + "," +
                   String(buzzer2State ? 1 : 0) + "," +
                   String(isAutoMode ? 1 : 0);

  LoRa.beginPacket();
  LoRa.print(payload);
  LoRa.endPacket();
}

void parseLoRaDownlink(String msg) {
  // Định dạng: CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ
  if (!msg.startsWith("CMD,")) return;

  int idx1 = msg.indexOf(',');
  int idx2 = msg.indexOf(',', idx1 + 1);
  int idx3 = msg.indexOf(',', idx2 + 1);
  int idx4 = msg.indexOf(',', idx3 + 1);
  int idx5 = msg.indexOf(',', idx4 + 1);

  if (idx5 == -1) return;

  int targetNode = msg.substring(idx1 + 1, idx2).toInt();
  if (targetNode != NODE_ID) return;

  int cmdMode = msg.substring(idx2 + 1, idx3).toInt();
  int cmdPump2 = msg.substring(idx3 + 1, idx4).toInt();
  int cmdPump3 = msg.substring(idx4 + 1, idx5).toInt();
  int cmdBuzz2 = msg.substring(idx5 + 1).toInt();

  isAutoMode = (cmdMode == 1);
  if (!isAutoMode) {
    pump2State = (cmdPump2 == 1);
    pump3State = (cmdPump3 == 1);
    buzzer2State = (cmdBuzz2 == 1);
    applyActuators();
  }
}

void setup() {
  // Chuyển chân RX (GPIO3) thành GPIO thông thường để điều khiển Còi 2
  pinMode(PIN_BUZZER2, FUNCTION_3);
  pinMode(PIN_BUZZER2, OUTPUT);
  pinMode(PIN_RELAY_PUMP2, OUTPUT);
  pinMode(PIN_RELAY_PUMP3, OUTPUT);
  pinMode(PIN_RAIN_DIGITAL, INPUT_PULLUP);
  applyActuators();

  Wire.begin(PIN_I2C_SDA, PIN_I2C_SCL);
  if (bme.begin(0x76) || bme.begin(0x77)) {
    bmeAvailable = true;
  }

  LoRa.setPins(PIN_LORA_SS, -1, -1);
  if (!LoRa.begin(433E6)) {
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSyncWord(0xF3);
}

void loop() {
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    parseLoRaDownlink(incoming);
  }

  if (millis() - lastSendTime >= sendInterval) {
    lastSendTime = millis();
    readSensors();
    runAutoLogic();
    sendLoRaPacket();
  }
}
```

---

### 5.3. Code Gateway Trung Tâm (`Gateway_ESP32S3.ino`)

Cài đặt thư viện trên Arduino IDE: `LoRa` (Sandeep Mistry), `Blynk` (Volodymyr Shymanskyy), `ArduinoJson` (Benoit Blanchon).

```cpp
/*
 * FILE: Gateway_ESP32S3.ino
 * VAI TRÒ: Trạm Gateway Trung Tâm (ESP32-S3-WROOM)
 * CHỨC NĂNG: Thu/Phát LoRa 2 chiều + Đẩy dữ liệu lên PHP Server + Đồng bộ Blynk IoT + Cảnh báo Telegram
 */

#define BLYNK_TEMPLATE_ID   "TMPL6xxxxxxxx"
#define BLYNK_TEMPLATE_NAME "Smart LoRa Farm and Flood"
#define BLYNK_AUTH_TOKEN    "YOUR_BLYNK_AUTH_TOKEN"

#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <HTTPClient.h>
#include <SPI.h>
#include <LoRa.h>
#include <BlynkSimpleEsp32.h>
#include <ArduinoJson.h>

// Cấu hình WiFi, Server PHP và Telegram
const char* WIFI_SSID     = "TEN_WIFI_CUA_BAN";
const char* WIFI_PASS     = "MAT_KHAU_WIFI";
const char* PHP_API_URL   = "http://192.168.1.100/iot_lora/api.php"; // Đổi IP máy chạy XAMPP / Domain của bạn
const char* TG_BOT_TOKEN  = "YOUR_TELEGRAM_BOT_TOKEN";               // Lấy từ @BotFather
const char* TG_CHAT_ID    = "YOUR_TELEGRAM_CHAT_ID";                 // Lấy từ @userinfobot

// Chân SPI LoRa trên ESP32-S3-WROOM
#define LORA_SCK    12
#define LORA_MISO   13
#define LORA_MOSI   11
#define LORA_SS     10
#define LORA_RST    9
#define LORA_DIO0   4

struct NodeData {
  float temp = 0;
  float hum = 0;
  float pres = 1008;
  int water = 0;
  int rain = 0;
  int pumpA = 0;
  int pumpB = 0;
  int buzz = 0;
  int mode = 1; // 1 = AUTO, 0 = MANUAL
  int rssi = 0;
  unsigned long lastSeen = 0;
  bool online = false;
  bool offlineAlertSent = false;
};

NodeData node1, node2;

// Trạng thái điều khiển mong muốn (từ Web PHP hoặc Blynk)
int ctrlMode1 = 1, ctrlPump1 = 0, ctrlBuzz1 = 0;
int ctrlMode2 = 1, ctrlPump2 = 0, ctrlPump3 = 0, ctrlBuzz2 = 0;

unsigned long lastServerSync = 0;
unsigned long lastTelegramTime = 0;
const unsigned long NODE_TIMEOUT_MS = 60000; // 60 giây mất tín hiệu -> Báo OFFLINE

// Hàm gửi cảnh báo qua Telegram (Chống spam tối thiểu 15 giây giữa các tin)
void sendTelegramAlert(String message) {
  if (millis() - lastTelegramTime < 15000 && lastTelegramTime != 0) return;
  lastTelegramTime = millis();

  if (WiFi.status() != WL_CONNECTED) return;
  WiFiClientSecure client;
  client.setInsecure();
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
    Serial.printf("[TELEGRAM] Sent (HTTP %d): %s\n", httpCode, message.c_str());
    https.end();
  }
}

// Phát lệnh điều khiển ngược xuống Node 1 hoặc Node 2 qua LoRa
void sendLoRaCommand(int nodeId) {
  String cmd = "";
  if (nodeId == 1) {
    cmd = "CMD,1," + String(ctrlMode1) + "," + String(ctrlPump1) + ",0," + String(ctrlBuzz1);
  } else if (nodeId == 2) {
    cmd = "CMD,2," + String(ctrlMode2) + "," + String(ctrlPump2) + "," + String(ctrlPump3) + "," + String(ctrlBuzz2);
  }
  LoRa.beginPacket();
  LoRa.print(cmd);
  LoRa.endPacket();
  Serial.println("[GATEWAY -> LORA DOWNLINK] " + cmd);
}

// ================= CÁC CHÂN VIRTUAL PIN BLYNK IOT =================
// V0: Chế độ Node 1 (1=Auto, 0=Manual) | V1: Bơm 1 | V2: Còi 1
// V3: Chế độ Node 2 (1=Auto, 0=Manual) | V4: Bơm 2 | V5: Bơm 3 | V6: Còi 2
BLYNK_WRITE(V0) { ctrlMode1 = param.asInt(); sendLoRaCommand(1); }
BLYNK_WRITE(V1) { ctrlPump1 = param.asInt(); ctrlMode1 = 0; Blynk.virtualWrite(V0, 0); sendLoRaCommand(1); }
BLYNK_WRITE(V2) { ctrlBuzz1 = param.asInt(); ctrlMode1 = 0; Blynk.virtualWrite(V0, 0); sendLoRaCommand(1); }

BLYNK_WRITE(V3) { ctrlMode2 = param.asInt(); sendLoRaCommand(2); }
BLYNK_WRITE(V4) { ctrlPump2 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }
BLYNK_WRITE(V5) { ctrlPump3 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }
BLYNK_WRITE(V6) { ctrlBuzz2 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }

void updateBlynkTelemetry() {
  if (!Blynk.connected()) return;
  // Cập nhật dữ liệu cảm biến lên Blynk
  Blynk.virtualWrite(V10, node1.temp);
  Blynk.virtualWrite(V11, node1.hum);
  Blynk.virtualWrite(V12, node1.water);
  Blynk.virtualWrite(V13, node2.temp);
  Blynk.virtualWrite(V14, node2.hum);
  Blynk.virtualWrite(V15, node2.water);
}

// Kiểm tra 7 kịch bản thực tế để bắn cảnh báo Telegram
void evaluateRealWorldAlerts(int nodeId) {
  if (nodeId == 1) {
    // TH1: Mực nước kênh/hồ cao nguy hiểm
    if (node1.water >= 80) {
      sendTelegramAlert("🚨 [CẢNH BÁO NGẬP - NODE 1]\nMực nước hồ chứa đạt " + String(node1.water) + "%!\nĐã kích hoạt Máy bơm xả 1 & Còi báo động 1.");
    }
    // TH5: Áp suất khí quyển giảm sâu kèm mưa lớn
    if (node1.rain == 1 && node1.pres < 1002.0 && node1.water >= 60) {
      sendTelegramAlert("⛈ [CẢNH BÁO GIÔNG BÃO - NODE 1]\nÁp suất giảm thấp (" + String(node1.pres, 1) + " hPa) kèm mưa lớn và nước dâng (" + String(node1.water) + "%). Nguy cơ lũ quét!");
    }
  } else if (nodeId == 2) {
    // TH4: Cạn bồn chứa nước tưới
    if (node2.water <= 20) {
      sendTelegramAlert("⚠️ [CẢNH BÁO CẠN BỒN - NODE 2]\nMực nước bồn tưới chỉ còn " + String(node2.water) + "%!\nĐã ngắt khẩn cấp Bơm 2 & Bơm 3 để chống cháy máy bơm.");
    }
    // TH3: Nắng nóng cực đoan bật phun sương
    if (node2.temp >= 35.0 && node2.pumpB == 1) {
      sendTelegramAlert("🌡 [CẢNH BÁO NẮNG NÓNG - NODE 2]\nNhiệt độ vườn đạt " + String(node2.temp, 1) + "°C, Độ ẩm " + String(node2.hum, 1) + "%.\nĐã bật Máy bơm 3 phun sương làm mát.");
    }
  }
}

// Phân tích gói tin Uplink từ Node 1 & Node 2
void processLoRaPacket(String packet, int rssi) {
  // UP,NODE_ID,TEMP,HUM,PRES,WATER,RAIN,PUMP_A,PUMP_B,BUZZ,MODE
  if (!packet.startsWith("UP,")) return;

  String parts[11];
  int partIdx = 0;
  int startPos = 0;
  for (int i = 0; i < (int)packet.length(); i++) {
    if (packet.charAt(i) == ',' || i == (int)packet.length() - 1) {
      int endPos = (i == (int)packet.length() - 1) ? i + 1 : i;
      if (partIdx < 11) {
        parts[partIdx++] = packet.substring(startPos, endPos);
      }
      startPos = i + 1;
    }
  }
  if (partIdx < 11) return;

  int id = parts[1].toInt();
  NodeData* target = (id == 1) ? &node1 : &node2;

  target->temp  = parts[2].toFloat();
  target->hum   = parts[3].toFloat();
  target->pres  = parts[4].toFloat();
  target->water = parts[5].toInt();
  target->rain  = parts[6].toInt();
  target->pumpA = parts[7].toInt();
  target->pumpB = parts[8].toInt();
  target->buzz  = parts[9].toInt();
  target->mode  = parts[10].toInt();
  target->rssi  = rssi;
  target->lastSeen = millis();
  target->online = true;
  target->offlineAlertSent = false;

  // Nếu đang ở chế độ AUTO, đồng bộ trạng thái thực tế của Node về biến điều khiển Gateway
  if (id == 1 && ctrlMode1 == 1) {
    ctrlPump1 = target->pumpA;
    ctrlBuzz1 = target->buzz;
  } else if (id == 2 && ctrlMode2 == 1) {
    ctrlPump2 = target->pumpA;
    ctrlPump3 = target->pumpB;
    ctrlBuzz2 = target->buzz;
  }

  evaluateRealWorldAlerts(id);

  // Phản hồi ngay gói tin lệnh Downlink cho Node vừa gửi
  delay(50);
  sendLoRaCommand(id);
}

// Đồng bộ 2 chiều với Web Server PHP (Gửi thông số cảm biến & Nhận lệnh Auto/Manual từ Web)
void syncWithPHPServer() {
  if (WiFi.status() != WL_CONNECTED) return;

  HTTPClient http;
  http.begin(PHP_API_URL);
  http.addHeader("Content-Type", "application/json");

  StaticJsonDocument<1024> doc;
  JsonObject n1 = doc.createNestedObject("node1");
  n1["temp"] = node1.temp;
  n1["hum"] = node1.hum;
  n1["pres"] = node1.pres;
  n1["water"] = node1.water;
  n1["rain"] = node1.rain;
  n1["pump1"] = node1.pumpA;
  n1["buzz1"] = node1.buzz;
  n1["mode"] = node1.mode;
  n1["rssi"] = node1.rssi;
  n1["online"] = node1.online ? 1 : 0;

  JsonObject n2 = doc.createNestedObject("node2");
  n2["temp"] = node2.temp;
  n2["hum"] = node2.hum;
  n2["pres"] = node2.pres;
  n2["water"] = node2.water;
  n2["rain"] = node2.rain;
  n2["pump2"] = node2.pumpA;
  n2["pump3"] = node2.pumpB;
  n2["buzz2"] = node2.buzz;
  n2["mode"] = node2.mode;
  n2["rssi"] = node2.rssi;
  n2["online"] = node2.online ? 1 : 0;

  String requestBody;
  serializeJson(doc, requestBody);

  int httpResponseCode = http.POST(requestBody);
  if (httpResponseCode == 200) {
    String response = http.getString();
    StaticJsonDocument<512> resDoc;
    if (deserializeJson(resDoc, response) == DeserializationError::Ok) {
      if (resDoc.containsKey("commands")) {
        JsonObject cmd = resDoc["commands"];
        int webMode1  = cmd["mode1"] | ctrlMode1;
        int webPump1  = cmd["pump1"] | ctrlPump1;
        int webBuzz1  = cmd["buzz1"] | ctrlBuzz1;
        int webMode2  = cmd["mode2"] | ctrlMode2;
        int webPump2  = cmd["pump2"] | ctrlPump2;
        int webPump3  = cmd["pump3"] | ctrlPump3;
        int webBuzz2  = cmd["buzz2"] | ctrlBuzz2;

        // Nếu người dùng vừa bấm nút trên Web PHP -> Phát lệnh LoRa ngay lập tức
        if (webMode1 != ctrlMode1 || webPump1 != ctrlPump1 || webBuzz1 != ctrlBuzz1) {
          ctrlMode1 = webMode1; ctrlPump1 = webPump1; ctrlBuzz1 = webBuzz1;
          sendLoRaCommand(1);
        }
        if (webMode2 != ctrlMode2 || webPump2 != ctrlPump2 || webPump3 != ctrlPump3 || webBuzz2 != ctrlBuzz2) {
          ctrlMode2 = webMode2; ctrlPump2 = webPump2; ctrlPump3 = webPump3; ctrlBuzz2 = webBuzz2;
          sendLoRaCommand(2);
        }
      }
    }
  }
  http.end();
}

// TH7: Giám sát mất kết nối LoRa (Watchdog Offline Alert)
void checkNodeWatchdog() {
  unsigned long now = millis();
  if (node1.lastSeen > 0 && (now - node1.lastSeen > NODE_TIMEOUT_MS)) {
    node1.online = false;
    if (!node1.offlineAlertSent) {
      node1.offlineAlertSent = true;
      sendTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm Node 1 (Thủy lợi) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT tại hiện trường.");
    }
  }
  if (node2.lastSeen > 0 && (now - node2.lastSeen > NODE_TIMEOUT_MS)) {
    node2.online = false;
    if (!node2.offlineAlertSent) {
      node2.offlineAlertSent = true;
      sendTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm Node 2 (Nông nghiệp) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT tại hiện trường.");
    }
  }
}

void setup() {
  Serial.begin(115200);
  delay(500);

  // Khởi tạo SPI cho LoRa trên ESP32-S3
  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(433E6)) {
    Serial.println("Lỗi khởi tạo LoRa trên Gateway ESP32-S3!");
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSyncWord(0xF3);
  Serial.println("LoRa Gateway 433MHz OK!");

  // Kết nối WiFi & Blynk
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Đang kết nối WiFi");
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    Serial.print(".");
    retry++;
  }
  Serial.println("\nWiFi Connected! IP: " + WiFi.localIP().toString());

  Blynk.config(BLYNK_AUTH_TOKEN);
  sendTelegramAlert("✅ [HỆ THỐNG KHỞI ĐỘNG]\nGateway ESP32-S3 đã kết nối WiFi và sẵn sàng thu phát LoRa 433MHz!");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    Blynk.run();
  }

  // Nhận gói tin LoRa từ Node 1 & Node 2
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    int rssi = LoRa.packetRssi();
    Serial.printf("[LORA RX | RSSI: %d dBm] %s\n", rssi, incoming.c_str());
    processLoRaPacket(incoming, rssi);
  }

  // Đồng bộ Web Server PHP & Blynk mỗi 3 giây
  if (millis() - lastServerSync >= 3000) {
    lastServerSync = millis();
    checkNodeWatchdog();
    syncWithPHPServer();
    updateBlynkTelemetry();
  }
}
```

---

## 6. MÃ NGUỒN WEB SERVER PHP & DASHBOARD ĐIỀU KHIỂN (`api.php` & `index.php`)

Hệ thống Web PHP dưới đây được thiết kế **tự động khởi tạo cơ sở dữ liệu SQLite / JSON ngay trong thư mục web** (không cần phải cấu hình MySQL phức tạp, chỉ cần chép 2 file `api.php` và `index.php` vào thư mục `htdocs/iot_lora/` của XAMPP hoặc Hosting PHP là chạy ngay lập tức!).

### 6.1. File xử lý Backend API (`api.php`)

Tạo file `C:\xampp\htdocs\iot_lora\api.php` với nội dung sau:

```php
<?php
/**
 * FILE: api.php
 * CHỨC NĂNG: Nhận dữ liệu từ ESP32-S3 Gateway, lưu lịch sử và trả về lệnh điều khiển Auto/Manual
 */
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Ho_Chi_Minh");

$stateFile = __DIR__ . "/iot_state.json";
$logFile   = __DIR__ . "/iot_history.json";

// Khởi tạo dữ liệu mặc định nếu file chưa tồn tại
if (!file_exists($stateFile)) {
    $defaultState = [
        "updated_at" => date("Y-m-d H:i:s"),
        "node1" => [
            "temp" => 29.0, "hum" => 75.0, "pres" => 1008.2,
            "water" => 35, "rain" => 0, "pump1" => 0, "buzz1" => 0,
            "mode" => 1, "rssi" => -65, "online" => 0
        ],
        "node2" => [
            "temp" => 31.5, "hum" => 58.0, "pres" => 1008.0,
            "water" => 70, "rain" => 0, "pump2" => 0, "pump3" => 0, "buzz2" => 0,
            "mode" => 1, "rssi" => -72, "online" => 0
        ],
        "commands" => [
            "mode1" => 1, "pump1" => 0, "buzz1" => 0,
            "mode2" => 1, "pump2" => 0, "pump3" => 0, "buzz2" => 0
        ]
    ];
    file_put_contents($stateFile, json_encode($defaultState, JSON_PRETTY_PRINT));
}

$currentData = json_decode(file_get_contents($stateFile), true);
$method = $_SERVER["REQUEST_METHOD"];

if ($method === "POST") {
    $rawInput = file_get_contents("php://input");
    $jsonInput = json_decode($rawInput, true);

    // Trường hợp 1: Web Dashboard gửi lệnh điều khiển Auto / Manual
    if (isset($jsonInput["action"]) && $jsonInput["action"] === "set_command") {
        foreach ($jsonInput["commands"] as $key => $val) {
            if (isset($currentData["commands"][$key])) {
                $currentData["commands"][$key] = intval($val);
            }
        }
        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));
        echo json_encode(["status" => "ok", "commands" => $currentData["commands"]]);
        exit;
    }

    // Trường hợp 2: ESP32-S3 Gateway đẩy dữ liệu cảm biến của Node 1 & Node 2 lên
    if (isset($jsonInput["node1"]) && isset($jsonInput["node2"])) {
        $currentData["updated_at"] = date("Y-m-d H:i:s");
        $currentData["node1"] = $jsonInput["node1"];
        $currentData["node2"] = $jsonInput["node2"];

        // Nếu Node đang ở chế độ AUTO (1), đồng bộ trạng thái Bơm/Còi vào bảng commands để Web hiển thị đúng
        if ($currentData["commands"]["mode1"] == 1) {
            $currentData["commands"]["pump1"] = intval($jsonInput["node1"]["pump1"]);
            $currentData["commands"]["buzz1"] = intval($jsonInput["node1"]["buzz1"]);
        }
        if ($currentData["commands"]["mode2"] == 1) {
            $currentData["commands"]["pump2"] = intval($jsonInput["node2"]["pump2"]);
            $currentData["commands"]["pump3"] = intval($jsonInput["node2"]["pump3"]);
            $currentData["commands"]["buzz2"] = intval($jsonInput["node2"]["buzz2"]);
        }

        file_put_contents($stateFile, json_encode($currentData, JSON_PRETTY_PRINT));

        // Lưu tối đa 30 bản ghi lịch sử gần nhất để vẽ biểu đồ / tra cứu
        $history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
        if (!is_array($history)) $history = [];
        array_unshift($history, [
            "time" => date("H:i:s"),
            "n1_water" => $jsonInput["node1"]["water"],
            "n1_temp"  => $jsonInput["node1"]["temp"],
            "n2_water" => $jsonInput["node2"]["water"],
            "n2_temp"  => $jsonInput["node2"]["temp"],
            "n2_hum"   => $jsonInput["node2"]["hum"]
        ]);
        $history = array_slice($history, 0, 30);
        file_put_contents($logFile, json_encode($history, JSON_PRETTY_PRINT));

        echo json_encode([
            "status" => "ok",
            "commands" => $currentData["commands"]
        ]);
        exit;
    }
}

// GET request: Trả về toàn bộ trạng thái và lịch sử cho giao diện Web
$history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
echo json_encode([
    "state" => $currentData,
    "history" => $history
]);
?>
```

---

### 6.2. File Giao diện Điều khiển Web App (`index.php`)

Tạo file `C:\xampp\htdocs\iot_lora\index.php` với nội dung sau:

```php
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Quản Lý IoT LoRa Vùng Sâu Vùng Xa</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen p-4 md:p-8">
    <div class="max-w-6xl mx-auto">
        <!-- Header -->
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center bg-slate-800 p-6 rounded-2xl border border-slate-700 mb-6 shadow-lg">
            <div>
                <h1 class="text-2xl md:text-3xl font-bold text-emerald-400">TRUNG TÂM ĐIỀU HÀNH IOT LORA 433MHz</h1>
                <p class="text-slate-400 text-sm mt-1">Gateway ESP32-S3 &bull; 2 Trạm ESP8266 Năng Lượng Mặt Trời &bull; Điều Khiển Auto / Manual</p>
            </div>
            <div class="mt-4 md:mt-0 text-right">
                <span class="text-xs text-slate-400 block">Cập nhật lần cuối:</span>
                <span id="last-update" class="text-sm font-mono text-amber-400">Đang tải...</span>
            </div>
        </div>

        <!-- 2 Node Cards -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <!-- NODE 1: TRẠM THỦY LỢI / CHỐNG NGẬP -->
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg">
                <div class="flex justify-between items-center border-b border-slate-700 pb-4 mb-4">
                    <div>
                        <h2 class="text-xl font-bold text-sky-400">TRẠM NODE 1: THỦY LỢI & CHỐNG NGẬP</h2>
                        <p class="text-xs text-slate-400" id="n1-rssi">Sóng LoRa: -- dBm</p>
                    </div>
                    <span id="n1-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-700 text-slate-300">KIỂM TRA...</span>
                </div>

                <!-- Thông số cảm biến Node 1 -->
                <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Mực nước Kênh/Hồ</div>
                        <div id="n1-water" class="text-2xl font-bold text-sky-400 mt-1">--%</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Trạng thái Mưa</div>
                        <div id="n1-rain" class="text-lg font-bold text-amber-300 mt-1">--</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Nhiệt độ / Độ ẩm</div>
                        <div id="n1-th" class="text-lg font-bold text-emerald-300 mt-1">--°C / --%</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60 col-span-2 sm:col-span-3">
                        <div class="text-xs text-slate-400">Áp suất khí quyển (BME280)</div>
                        <div id="n1-pres" class="text-base font-semibold text-purple-300 mt-1">-- hPa</div>
                    </div>
                </div>

                <!-- Bảng điều khiển Node 1 -->
                <div class="bg-slate-900 p-4 rounded-xl border border-slate-700">
                    <div class="flex justify-between items-center mb-4">
                        <span class="font-semibold text-sm">Chế độ vận hành Node 1:</span>
                        <div class="flex gap-2">
                            <button onclick="setCommand({mode1: 1})" id="btn-n1-auto" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">TỰ ĐỘNG (AUTO)</button>
                            <button onclick="setCommand({mode1: 0})" id="btn-n1-man" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">THỦ CÔNG (MANUAL)</button>
                        </div>
                    </div>
                    <div class="grid grid-cols-2 gap-3">
                        <button onclick="toggleDevice('pump1', 'mode1')" id="btn-pump1" class="p-3 rounded-xl font-bold text-sm border transition flex flex-col items-center justify-center">
                            <span>MÁY BƠM 1 (XẢ NGẬP)</span>
                            <span id="txt-pump1" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('buzz1', 'mode1')" id="btn-buzz1" class="p-3 rounded-xl font-bold text-sm border transition flex flex-col items-center justify-center">
                            <span>CÒI BÁO ĐỘNG 1</span>
                            <span id="txt-buzz1" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                    </div>
                </div>
            </div>

            <!-- NODE 2: TRẠM NÔNG NGHIỆP THÔNG MINH -->
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg">
                <div class="flex justify-between items-center border-b border-slate-700 pb-4 mb-4">
                    <div>
                        <h2 class="text-xl font-bold text-emerald-400">TRẠM NODE 2: VƯỜN NÔNG NGHIỆP</h2>
                        <p class="text-xs text-slate-400" id="n2-rssi">Sóng LoRa: -- dBm</p>
                    </div>
                    <span id="n2-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-700 text-slate-300">KIỂM TRA...</span>
                </div>

                <!-- Thông số cảm biến Node 2 -->
                <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Mực nước Bồn Tưới</div>
                        <div id="n2-water" class="text-2xl font-bold text-emerald-400 mt-1">--%</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Trạng thái Mưa</div>
                        <div id="n2-rain" class="text-lg font-bold text-amber-300 mt-1">--</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                        <div class="text-xs text-slate-400">Nhiệt độ / Độ ẩm</div>
                        <div id="n2-th" class="text-lg font-bold text-emerald-300 mt-1">--°C / --%</div>
                    </div>
                    <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60 col-span-2 sm:col-span-3">
                        <div class="text-xs text-slate-400">Áp suất khí quyển (BME280)</div>
                        <div id="n2-pres" class="text-base font-semibold text-purple-300 mt-1">-- hPa</div>
                    </div>
                </div>

                <!-- Bảng điều khiển Node 2 -->
                <div class="bg-slate-900 p-4 rounded-xl border border-slate-700">
                    <div class="flex justify-between items-center mb-4">
                        <span class="font-semibold text-sm">Chế độ vận hành Node 2:</span>
                        <div class="flex gap-2">
                            <button onclick="setCommand({mode2: 1})" id="btn-n2-auto" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">TỰ ĐỘNG (AUTO)</button>
                            <button onclick="setCommand({mode2: 0})" id="btn-n2-man" class="px-4 py-1.5 rounded-lg text-xs font-bold transition">THỦ CÔNG (MANUAL)</button>
                        </div>
                    </div>
                    <div class="grid grid-cols-3 gap-2">
                        <button onclick="toggleDevice('pump2', 'mode2')" id="btn-pump2" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>BƠM 2 (TƯỚI CÂY)</span>
                            <span id="txt-pump2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('pump3', 'mode2')" id="btn-pump3" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>BƠM 3 (PHUN SƯƠNG)</span>
                            <span id="txt-pump3" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                        <button onclick="toggleDevice('buzz2', 'mode2')" id="btn-buzz2" class="p-3 rounded-xl font-bold text-xs border transition flex flex-col items-center justify-center">
                            <span>CÒI BÁO 2</span>
                            <span id="txt-buzz2" class="text-xs mt-1 font-normal">Đang Tắt</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bảng Nhật Ký Thực Tế -->
        <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg">
            <h3 class="text-lg font-bold text-slate-200 mb-4">NHẬT KÝ DỮ LIỆU THU THẬP GẦN NHẤT</h3>
            <div class="overflow-x-auto">
                <table class="w-full text-left text-sm text-slate-300">
                    <thead class="text-xs uppercase bg-slate-900 text-slate-400">
                        <tr>
                            <th class="p-3">Thời gian</th>
                            <th class="p-3">Mực nước Hồ (Node 1)</th>
                            <th class="p-3">Nhiệt độ (Node 1)</th>
                            <th class="p-3">Mực nước Bồn (Node 2)</th>
                            <th class="p-3">Nhiệt độ / Ẩm (Node 2)</th>
                        </tr>
                    </thead>
                    <tbody id="history-body" class="divide-y divide-slate-700"></tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        let currentCommands = { mode1: 1, pump1: 0, buzz1: 0, mode2: 1, pump2: 0, pump3: 0, buzz2: 0 };

        function styleButton(btnId, txtId, isActive) {
            const btn = document.getElementById(btnId);
            const txt = document.getElementById(txtId);
            if (isActive == 1) {
                btn.className = "p-3 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-emerald-600 border-emerald-400 text-white shadow-lg";
                txt.innerText = "ĐANG BẬT (ON)";
            } else {
                btn.className = "p-3 rounded-xl font-bold text-xs md:text-sm border transition flex flex-col items-center justify-center bg-slate-800 border-slate-600 text-slate-300 hover:bg-slate-700";
                txt.innerText = "ĐANG TẮT (OFF)";
            }
        }

        function styleModeButtons(autoBtnId, manBtnId, isAuto) {
            const autoBtn = document.getElementById(autoBtnId);
            const manBtn = document.getElementById(manBtnId);
            if (isAuto == 1) {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-sky-500 text-white";
                manBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
            } else {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
                manBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-amber-500 text-slate-950";
            }
        }

        async function setCommand(newCmds) {
            currentCommands = { ...currentCommands, ...newCmds };
            await fetch('api.php', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'set_command', commands: currentCommands })
            });
            fetchDashboard();
        }

        function toggleDevice(deviceKey, modeKey) {
            const nextVal = currentCommands[deviceKey] == 1 ? 0 : 1;
            const payload = {};
            payload[modeKey] = 0; // Tự động chuyển sang MANUAL khi bấm nút thiết bị
            payload[deviceKey] = nextVal;
            setCommand(payload);
        }

        async function fetchDashboard() {
            try {
                const res = await fetch('api.php');
                const data = await res.json();
                const st = data.state;
                currentCommands = st.commands;

                document.getElementById('last-update').innerText = st.updated_at;

                // Node 1
                document.getElementById('n1-water').innerText = st.node1.water + '%';
                document.getElementById('n1-rain').innerText = st.node1.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n1-th').innerText = `${st.node1.temp}°C / ${st.node1.hum}%`;
                document.getElementById('n1-pres').innerText = `${st.node1.pres} hPa`;
                document.getElementById('n1-rssi').innerText = `Sóng LoRa: ${st.node1.rssi} dBm`;
                const s1 = document.getElementById('n1-status');
                s1.innerText = st.node1.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s1.className = st.node1.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Node 2
                document.getElementById('n2-water').innerText = st.node2.water + '%';
                document.getElementById('n2-rain').innerText = st.node2.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n2-th').innerText = `${st.node2.temp}°C / ${st.node2.hum}%`;
                document.getElementById('n2-pres').innerText = `${st.node2.pres} hPa`;
                document.getElementById('n2-rssi').innerText = `Sóng LoRa: ${st.node2.rssi} dBm`;
                const s2 = document.getElementById('n2-status');
                s2.innerText = st.node2.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s2.className = st.node2.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Cập nhật trạng thái nút điều khiển
                styleModeButtons('btn-n1-auto', 'btn-n1-man', currentCommands.mode1);
                styleModeButtons('btn-n2-auto', 'btn-n2-man', currentCommands.mode2);
                styleButton('btn-pump1', 'txt-pump1', currentCommands.pump1);
                styleButton('btn-buzz1', 'txt-buzz1', currentCommands.buzz1);
                styleButton('btn-pump2', 'txt-pump2', currentCommands.pump2);
                styleButton('btn-pump3', 'txt-pump3', currentCommands.pump3);
                styleButton('btn-buzz2', 'txt-buzz2', currentCommands.buzz2);

                // Bảng lịch sử
                const tbody = document.getElementById('history-body');
                tbody.innerHTML = (data.history || []).slice(0, 8).map(row => `
                    <tr class="hover:bg-slate-700/40">
                        <td class="p-3 font-mono text-xs">${row.time}</td>
                        <td class="p-3 font-semibold text-sky-400">${row.n1_water}%</td>
                        <td class="p-3">${row.n1_temp}°C</td>
                        <td class="p-3 font-semibold text-emerald-400">${row.n2_water}%</td>
                        <td class="p-3">${row.n2_temp}°C / ${row.n2_hum}%</td>
                    </tr>
                `).join('');
            } catch (e) {
                console.error(e);
            }
        }

        fetchDashboard();
        setInterval(fetchDashboard, 2500);
    </script>
</body>
</html>
```

---

## 7. HƯỚNG DẪN CẤU HÌNH TỪNG BƯỚC (TELEGRAM, BLYNK, WEB PHP)

### Bước 1: Tạo Bot Telegram nhận cảnh báo tức thì
1. Mở ứng dụng Telegram, tìm **`@BotFather`** $\rightarrow$ Gõ lệnh `/newbot` $\rightarrow$ Đặt tên cho Bot và nhận chuỗi **`HTTP API Token`** (ví dụ: `7123456:AAH...`). Điền vào biến `TG_BOT_TOKEN` trong code `Gateway_ESP32S3.ino`.
2. Tìm **`@userinfobot`** trên Telegram $\rightarrow$ Nhấn **Start** để lấy dãy số **`Id`** của bạn. Điền vào biến `TG_CHAT_ID` trong code `Gateway_ESP32S3.ino`.
3. Nhấn nút **Start** trong khung chat với Bot bạn vừa tạo để kích hoạt nhận tin nhắn.

### Bước 2: Cấu hình Blynk IoT (Web & Mobile App)
1. Đăng nhập vào [Blynk.Cloud](https://blynk.cloud/) $\rightarrow$ Tạo **New Template** (Hardware: `ESP32`, Connection: `WiFi`).
2. Vào tab **Datastreams**, tạo các **Virtual Pin** sau:
   * **Điều khiển (Integer, Min `0`, Max `1`):**
     * `V0`: Chế độ Node 1 (`1` = Auto, `0` = Manual)
     * `V1`: Máy bơm 1 (Xả ngập)
     * `V2`: Còi báo động 1
     * `V3`: Chế độ Node 2 (`1` = Auto, `0` = Manual)
     * `V4`: Máy bơm 2 (Tưới cây)
     * `V5`: Máy bơm 3 (Phun sương)
     * `V6`: Còi báo động 2
   * **Hiển thị Cảm biến (Double / Integer):**
     * `V10`: Nhiệt độ Node 1 | `V11`: Độ ẩm Node 1 | `V12`: Mực nước Node 1 (%)
     * `V13`: Nhiệt độ Node 2 | `V14`: Độ ẩm Node 2 | `V15`: Mực nước Node 2 (%)
3. Copy 3 dòng `BLYNK_TEMPLATE_ID`, `BLYNK_TEMPLATE_NAME`, `BLYNK_AUTH_TOKEN` dán vào đầu file `Gateway_ESP32S3.ino`.

### Bước 3: Chạy Web Server PHP
1. Cài đặt **XAMPP** trên máy tính (hoặc upload lên Hosting có hỗ trợ PHP).
2. Tạo thư mục `C:\xampp\htdocs\iot_lora\` và lưu 2 file **`api.php`** và **`index.php`** vào đó.
3. Mở Command Prompt gõ `ipconfig` để xem địa chỉ IPv4 của máy tính (ví dụ `192.168.1.100`), sau đó cập nhật dòng `PHP_API_URL` trong code `Gateway_ESP32S3.ino` thành `"http://192.168.1.100/iot_lora/api.php"`.
4. Mở trình duyệt truy cập `http://localhost/iot_lora/` để điều khiển và giám sát toàn bộ hệ thống theo thời gian thực.