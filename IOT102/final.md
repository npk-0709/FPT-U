# BÁO CÁO KỸ THUẬT & MÃ NGUỒN DỰ ÁN HỆ THỐNG IOT LORA ĐA NĂNG VÙNG SÂU VÙNG XA

**Đề tài: Hệ Thống Giám Sát Môi Trường, Chống Ngập Thủy Lợi & Tưới Tiêu Nông Nghiệp Tự Động Theo Độ Ẩm Đất Sử Dụng Sóng LoRa $433\,\text{MHz}$ Và Năng Lượng Mặt Trời**

---

## PHẦN 1: TỔNG QUAN ĐỀ TÀI & KIẾN TRÚC HỆ THỐNG

### 1.1. Bối cảnh và Mục tiêu thực tế của dự án

Tại các khu vực canh tác nông nghiệp, hồ chứa thủy lợi hoặc vùng sâu vùng xa, sóng WiFi và mạng di động thường không ổn định hoặc không thể kéo dây nguồn điện lưới. Dự án này giải quyết triệt để bài toán đó bằng cách xây dựng mạng cảm biến và điều khiển không dây tầm xa **LoRa $433\,\text{MHz}$ hai chiều**, vận hành độc lập bằng **Năng lượng mặt trời (Solar Power)** kết hợp cơ chế tự động hóa tại biên (*Edge Computing*) và quản trị tập trung qua **Web Server PHP**, **Blynk IoT** và **Telegram Bot**.

Hệ thống được phân chia thành **3 phân hệ phần cứng** rõ ràng, sát với nhu cầu thực tế:

1. **Node 1 (ESP8266 - Trạm Thủy Lợi / Cảnh Báo Ngập Úng):**
   * Đặt tại kênh thoát nước hoặc hồ chứa chính ở khu vực không có WiFi.
   * Trang bị: **Cảm biến mực nước 1**, **Cảm biến mưa 1**, **Cảm biến môi trường BME280 1**, **Còi báo động 1 ($5\,\text{V}$)** và **Relay 1 điều khiển Máy bơm 1 ($12\,\text{V}$ - Bơm xả chống ngập)**.
2. **Node 2 (ESP8266 - Trạm Vườn Nông Nghiệp Thông Minh):**
   * Đặt tại khu vực canh tác/nhà màng ở xa.
   * Trang bị: **Cảm biến độ ẩm đất (Soil Moisture Sensor)**, **Cảm biến mực nước 2** (đo mực nước trong bồn trữ tưới cây), **Cảm biến mưa 2**, **BME280 2**, **Còi báo động 2 ($5\,\text{V}$)** và điều khiển **Máy bơm 2 ($12\,\text{V}$ - Bơm tưới gốc)** cùng **Máy bơm 3 ($12\,\text{V}$ - Bơm phun sương làm mát)**.
3. **Gateway Trung Tâm (ESP32-S3-WROOM):**
   * Đặt tại nhà điều hành (nơi có kết nối WiFi).
   * Giao tiếp **2 chiều qua sóng LoRa $433\,\text{MHz}$** với Node 1 và Node 2, đồng bộ dữ liệu thời gian thực lên **Web Server PHP tự code**, **Blynk IoT**, và gửi cảnh báo tức thì qua **Telegram**.

---

### 1.2. Bảng tổng hợp danh mục linh kiện phần cứng (Bill of Materials - BOM)

| STT | Tên linh kiện | Số lượng | Phân bổ trong hệ thống | Vai trò kỹ thuật |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Vi điều khiển **ESP8266 (NodeMCU)** | $02$ | Node 1, Node 2 | Xử lý cảm biến tại chỗ, điều khiển Relay/Còi và truyền nhận LoRa |
| 2 | Vi điều khiển **ESP32-S3-WROOM** | $01$ | Gateway Trung tâm | Thu phát LoRa 2 chiều, kết nối WiFi đẩy dữ liệu lên PHP, Blynk, Telegram |
| 3 | Module **LoRa Ra-01 SX1278 $433\,\text{MHz}$** | $03$ | Node 1, Node 2, Gateway | Truyền thông không dây tầm xa xuyên vật cản, không phụ thuộc WiFi |
| 4 | Cảm biến **BME280** (I2C) | $02$ | Node 1, Node 2 | Đo Nhiệt độ ($^\circ\text{C}$), Độ ẩm không khí ($\%$), Áp suất khí quyển ($\text{hPa}$) |
| 5 | **Cảm biến độ ẩm đất** (Soil Moisture) | $01$ | Node 2 (Trạm Nông nghiệp) | Đo độ ẩm đất ($0\% \rightarrow 100\%$) quyết định chu kỳ bật/tắt bơm tưới |
| 6 | **Cảm biến mực nước** (Water Sensor v2) | $02$ | Node 1 (Hồ), Node 2 (Bồn) | Đo mực nước ngập tại kênh/hồ (Node 1) và báo cạn bồn tưới (Node 2) |
| 7 | **Cảm biến nước mưa** (Rain Sensor + LM393) | $02$ | Node 1, Node 2 | Phát hiện trạng thái mưa thực tế để chống ngập và ngắt tưới tự động |
| 8 | **Máy bơm nước $12\,\text{V}$ + Module Relay** | $03$ | Node 1 ($1$ bơm), Node 2 ($2$ bơm) | Bơm 1: Xả ngập; Bơm 2: Tưới gốc theo độ ẩm đất; Bơm 3: Phun sương hạ nhiệt |
| 9 | **Còi báo động $5\,\text{V}$ ($2$ dây)** | $02$ | Node 1, Node 2 | Phát âm thanh cảnh báo ngập lũ (Node 1) và cảnh báo cạn bồn nước (Node 2) |
| 10 | **Tấm Pin NLMT + Mạch sạc + Pin $3.7\,\text{V}$** | $02$ bộ | Node 1, Node 2 (+ Gateway) | Cấp nguồn tự duy trì cho các vi điều khiển hoạt động ngoài thực địa |
| 11 | **Tụ hóa $16\,\text{V}$ $220\,\mu\text{F}$** | $05$ | $3$ module LoRa + $2$ mạch nguồn | Lọc nhiễu và chống sụt áp đột ngột (Brown-out) khi phát sóng LoRa / đóng Relay |

---

### 1.3. Sơ đồ khối kiến trúc tổng thể hệ thống

```
+-------------------------------------------------------------------------------+
|                       KHU VỰC THỰC ĐỊA (KHÔNG CÓ WIFI)                        |
|                                                                               |
|  [ NODE 1: TRẠM THỦY LỢI / CHỐNG NGẬP ]   [ NODE 2: TRẠM VƯỜN NÔNG NGHIỆP ]   |
|  - ESP8266 + Pin NLMT 3.7V + Tụ 220uF     - ESP8266 + Pin NLMT 3.7V + Tụ      |
|  - BME280 #1 (Nhiệt, Ẩm, Áp suất)         - BME280 #2 (Nhiệt, Ẩm, Áp suất)    |
|  - CB Mực nước #1 (A0: Đo nước kênh/hồ)   - CB Độ ẩm đất (A0: Đo ẩm đất 0-100)|
|  - CB Mưa #1 (D3: Phát hiện mưa)          - CB Mực nước #2 (RX: Báo cạn bồn)  |
|  - Còi báo động #1 (D4: Báo lũ 5V)        - CB Mưa #2 (D3: Ngắt tưới khi mưa) |
|  - Relay #1 -> Bơm 1 (D0: Xả ngập 12V)    - Relay #2 -> Bơm 2 (D0: Tưới gốc)  |
|                                           - Relay #3 -> Bơm 3 (D4: Phun sương)|
|                                           - Còi báo động #2 (TX: Báo cạn bồn) |
+---------------------+-----------------------------------+---------------------+
                      |                                   |
                      |   Sóng LoRa 433MHz (2 Chiều)      |
                      +-----------------+-----------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                   TRẠM TRUNG TÂM ĐIỀU HÀNH CÓ WIFI (GATEWAY)                  |
|                   ESP32-S3-WROOM + LoRa Ra-01 SX1278 + Tụ 220uF               |
+---------------------+-----------------+-----------------+---------------------+
                      |                 |                 |
           HTTP POST/GET JSON     Blynk Cloud API   HTTPS Bot API
                      |                 |                 |
                      v                 v                 v
             [ WEB SERVER PHP ]   [ BLYNK IOT ]    [ TELEGRAM BOT ]
             (Auto/Manual + Log)  (App Mobile)     (Cảnh báo tức thì)
```

---

## PHẦN 2: PHÂN TÍCH 8 TRƯỜNG HỢP THỰC TẾ (USE CASES) & THUẬT TOÁN

Hệ thống được lập trình theo cơ chế **Tự hành tại biên (Edge Autonomy)**: ngay cả khi mất sóng LoRa hoặc đứt cáp quang Internet tại trung tâm, 2 Node ESP8266 vẫn tự động bảo vệ vườn và chống ngập dựa trên dữ liệu cảm biến tại chỗ.

### Trường hợp 1: Chống ngập úng tự động & Báo động lũ có độ trễ Hysteresis (Node 1)
* **Điều kiện kích hoạt:** Khi Node 1 ở chế độ `AUTO` và **Cảm biến mực nước 1** tại kênh/hồ dâng cao đạt ngưỡng nguy hiểm $\ge 80\%$.
* **Hành động xử lý:**
  1. Node 1 lập tức kích hoạt **Relay 1 bật Máy bơm 1 ($12\,\text{V}$)** để bơm xả chống ngập và bật **Còi báo động 1 ($5\,\text{V}$)** cảnh báo tại hiện trường.
  2. Máy bơm 1 duy trì xả nước cho đến khi mực nước rút xuống ngưỡng an toàn $\le 50\%$ mới tắt (thuật toán trễ *Hysteresis* giúp chống đóng/ngắt Relay liên tục khi mặt nước dao động).
  3. Gửi gói tin khẩn qua LoRa về Gateway ESP32-S3 để bắn cảnh báo đỏ lên **Telegram**, **Web PHP** và **Blynk**.

### Trường hợp 2: Tưới cây tự động theo Độ Ẩm Đất kết hợp ngắt tưới khi trời mưa (Node 2)
* **Điều kiện kích hoạt:** Khi Node 2 ở chế độ `AUTO`, **Cảm biến độ ẩm đất** báo đất khô ($\le 35\%$), **Cảm biến mưa 2** xác nhận trời không mưa, và **Cảm biến mực nước 2** xác nhận bồn chứa còn nước ($\gt 20\%$).
* **Hành động xử lý:**
  1. Node 2 tự động đóng **Relay 2 bật Máy bơm 2 ($12\,\text{V}$)** để tưới gốc cho cây trồng.
  2. **Cơ chế giữ trễ Hysteresis ($35\% \rightarrow 65\%$):** Máy bơm 2 tiếp tục tưới cho đến khi độ ẩm đất thấm đều đạt $\ge 65\%$ mới ngắt, tránh hiện tượng đất vừa ướt bề mặt đã tắt bơm.
  3. **Cơ chế tiết kiệm nước khi mưa:** Nếu đang tưới mà trời đổ mưa (**Cảm biến mưa 2** chuyển trạng thái sang có mưa), Node 2 lập tức ngắt Máy bơm 2.
  4. Gateway ESP32-S3 gửi thông báo qua Telegram: *"THÔNG BÁO TRẠM 2: Độ ẩm đất thấp, hệ thống đã tự động bật Máy bơm 2 tưới cây."*

### Trường hợp 3: Phun sương hạ nhiệt chống sốc nhiệt cho vườn (Node 2)
* **Điều kiện kích hoạt:** Nhiệt độ không khí từ **BME280 2** vượt ngưỡng nắng nóng gay gắt ($\ge 35^\circ\text{C}$), độ ẩm không khí xuống thấp ($\lt 50\%$), trời không mưa và bồn chứa còn nước ($\gt 20\%$).
* **Hành động xử lý:** Node 2 tự động bật **Máy bơm 3 (Bơm phun sương $12\,\text{V}$)** để làm mát tiểu khí hậu trong vườn. Khi nhiệt độ giảm xuống $\le 33^\circ\text{C}$ hoặc độ ẩm không khí đạt $\ge 65\%$, hệ thống tự động ngắt bơm phun sương.

### Trường hợp 4: Khóa liên động (Dry-Run Interlock) chống cháy máy bơm khi cạn bồn (Node 2)
* **Điều kiện kích hoạt:** **Cảm biến mực nước 2** phát hiện mực nước trong bồn trữ tưới cây tụt xuống mức cạn kiệt ($\le 20\%$).
* **Hành động xử lý:**
  1. Hệ thống **cưỡng bức ngắt ngay lập tức cả Máy bơm 2 (bơm tưới) và Máy bơm 3 (bơm phun sương)** bất kể đất đang khô hay trời đang nóng, nhằm bảo vệ máy bơm $12\,\text{V}$ không chạy khô gây cháy cuộn dây.
  2. Kích hoạt **Còi báo động 2** kêu cảnh báo tại vườn.
  3. Nếu lúc đó đất cũng đang khô ($\le 35\%$), Gateway gửi cảnh báo khẩn cấp qua Telegram: *"CẢNH BÁO TRẠM 2: Đất đang khô nhưng BỒN CHỨA CẠN NƯỚC! Đã khóa bơm bảo vệ."*

### Trường hợp 5: Tự động bù ẩm đất khi thời tiết nắng hạn cực đoan (Node 2)
* **Điều kiện kích hoạt:** Nhiệt độ môi trường từ **BME280 2** tăng cao ($\ge 36^\circ\text{C}$) làm tốc độ bốc hơi nước nhanh khiến độ ẩm đất giảm xuống dưới $45\%$ (dù chưa chạm ngưỡng khô hạn $35\%$).
* **Hành động xử lý:** Node 2 chủ động kích hoạt sớm **Máy bơm 2** để bù ẩm cho đất, ngăn ngừa cây bị héo rũ vào buổi trưa nắng gắt.

### Trường hợp 6: Dự báo & Cảnh báo sớm mưa giông / lũ quét nhờ áp suất khí quyển (Node 1 & Node 2)
* **Điều kiện kích hoạt:** Cảm biến **BME280** ghi nhận áp suất khí quyển tụt giảm thấp bất thường ($\lt 1002\,\text{hPa}$) kết hợp với **Cảm biến mưa** báo có mưa và mực nước tại Node 1 bắt đầu dâng ($\ge 60\%$).
* **Hành động xử lý:**
  1. Node 1 chủ động bật sớm **Máy bơm 1** để hạ thấp mực nước đệm trong hồ chứa trước khi đỉnh lũ về.
  2. Gateway ESP32-S3 gửi cảnh báo thời tiết cực đoan qua Telegram.

### Trường hợp 7: Điều khiển cưỡng bức thủ công (Manual Override) 2 chiều qua Web PHP & Blynk
* **Điều kiện kích hoạt:** Người quản lý chuyển chế độ từ `AUTO` sang `MANUAL` trên giao diện **Web App PHP** hoặc ứng dụng **Blynk IoT**, sau đó nhấn nút Bật/Tắt bất kỳ Máy bơm ($1, 2, 3$) hoặc Còi báo động ($1, 2$).
* **Hành động xử lý:** Gateway ESP32-S3 nhận lệnh từ Server PHP / Blynk và **phát gói tin điều khiển Downlink qua sóng LoRa $433\,\text{MHz}$** xuống đúng Node đích để đóng/ngắt Relay tức thì.

### Trường hợp 8: Giám sát đứt kết nối trạm vùng sâu vùng xa (Watchdog Offline Alert)
* **Điều kiện kích hoạt:** Node 1 hoặc Node 2 bị sự cố mất nguồn hoặc mất sóng LoRa không gửi gói tin về Gateway quá $60$ giây.
* **Hành động xử lý:** Gateway ESP32-S3 tự động chuyển trạng thái trạm đó sang `OFFLINE` trên Web Dashboard và gửi tin nhắn cảnh báo mất kết nối qua Telegram.

---

## PHẦN 3: THIẾT KẾ PHẦN CỨNG, NGUỒN NLMT & SƠ ĐỒ ĐẤU DÂY CHI TIẾT

### 3.1. Phân bổ 5 Tụ hóa $16\,\text{V}$ $220\,\mu\text{F}$ và Nguồn Năng Lượng Mặt Trời

Khi vận hành thực tế bằng pin Li-ion $3.7\,\text{V}$ và mạch sạc năng lượng mặt trời, dòng tiêu thụ của module LoRa SX1278 khi phát sóng tăng vọt lên $\sim 120 - 130\,\text{mA}$ trong vài chục mili-giây, cộng với dòng kích cuộn hút Relay rất dễ gây sụt áp tức thời làm treo vi điều khiển ESP8266/ESP32-S3. Do đó, **5 tụ hóa $16\,\text{V}$ $220\,\mu\text{F}$** được bố trí như sau:

* **Tụ số 1, 2, 3:** Đấu song song trực tiếp sát chân **`VCC (3.3V)` và `GND` của 3 module LoRa Ra-01 SX1278** (Chân âm có vạch trắng nối `GND`, chân dương nối `3.3V`).
* **Tụ số 4, 5:** Đấu song song tại ngõ ra cấp nguồn của **2 mạch sạc Pin NLMT tại Node 1 và Node 2** (giữa đường `VCC` và `GND` tổng) để bù dòng tức thời khi đóng cắt Relay máy bơm.
* **Lưu ý nguồn $3.3\,\text{V}$ và $12\,\text{V}$:** Module **LoRa Ra-01 SX1278** và **BME280** bắt buộc cấp nguồn $3.3\,\text{V}$ từ chân `3V3` của ESP8266/ESP32-S3. Ba máy bơm $12\,\text{V}$ sử dụng nguồn $12\,\text{V}$ đi qua tiếp điểm thường mở (`COM` - `NO`) của Relay để cách ly hoàn toàn dòng tải lớn khỏi vi điều khiển.

---

### 3.2. Giải pháp tối ưu chân GPIO & ADC trên ESP8266 tại Node 2

Vi điều khiển ESP8266 chỉ có **duy nhất 1 chân đọc Analog (`A0`)**. Để kết nối đồng thời **LoRa Ra-01 (SPI)**, **BME280 (I2C)**, **Cảm biến độ ẩm đất**, **Cảm biến mực nước bồn**, **Cảm biến mưa**, **2 Máy bơm $12\,\text{V}$** và **Còi báo động** tại Node 2 một cách ổn định nhất:

1. **Chân `A0` (`ADC0`):** Dành riêng cho **Cảm biến độ ẩm đất (`AO`)** để đọc độ ẩm đất biến thiên liên tục từ $0\% \rightarrow 100\%$.
2. **Chân `RX` (`GPIO3` - cấu hình `FUNCTION_3`):** Dùng để đọc tín hiệu **Cảm biến mực nước 2** đặt trong bồn trữ nước tưới (lấy tín hiệu qua ngưỡng Digital `DO` hoặc đặt cảm biến ở vạch báo cạn $20\%$ của bồn: khi nước chạm vạch xuất mức `HIGH` $\rightarrow$ báo bồn còn nước `$85\%$`; khi nước tụt dưới vạch xuất mức `LOW` $\rightarrow$ báo bồn cạn `$15\%$`).
3. **Chân `TX` (`GPIO1` - cấu hình `FUNCTION_3`):** Dùng để điều khiển **Còi báo động 2 ($5\,\text{V}$)** khi trạm hoạt động độc lập ngoài thực địa bằng pin mặt trời.
4. **Giải phóng chân cho LoRa Ra-01:** Sử dụng chế độ Polling `LoRa.setPins(D8, -1, -1)` (nối chân `RST` của LoRa vào `RST`/`3V3` của ESP8266) giúp tiết kiệm 2 chân GPIO cho hệ thống.

---

### 3.3. Bảng đấu dây chi tiết Node 1 (ESP8266 - Trạm Thủy Lợi / Chống Ngập)

| Linh kiện tại Node 1 | Chân Linh Kiện | Chân ESP8266 (NodeMCU) | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **LoRa Ra-01 (#1)** | `VCC` / `GND` | `3V3` / `GND` | **Gắn Tụ $220\,\mu\text{F}$ (#1) sát chân `3V3` và `GND`** |
| | `SCK` | `D5` (`GPIO14`) | Xung nhịp SPI |
| | `MISO` | `D6` (`GPIO12`) | Dữ liệu SPI vào |
| | `MOSI` | `D7` (`GPIO13`) | Dữ liệu SPI ra |
| | `NSS` (`CS`) | `D8` (`GPIO15`) | Chọn chip SPI |
| | `RST` | `RST` (hoặc `3V3`) | Reset đồng bộ cùng ESP8266 |
| **BME280 (#1)** | `VIN` / `GND` | `3V3` / `GND` | Cảm biến Nhiệt độ, Độ ẩm, Áp suất |
| | `SCL` | `D1` (`GPIO5`) | Xung nhịp I2C |
| | `SDA` | `D2` (`GPIO4`) | Dữ liệu I2C |
| **CB Mực Nước (#1)** | `VCC` / `GND` | `3V3` / `GND` | Đặt tại Kênh / Hồ chứa |
| | `S` (Analog) | `A0` (`ADC0`) | Đọc tuyến tính mực nước từ $0\% \rightarrow 100\%$ |
| **CB Nước Mưa (#1)** | `VCC` / `GND` | `3V3` / `GND` | Cấp nguồn cho mạch so sánh LM393 |
| | `DO` (Digital) | `D3` (`GPIO0`) | `LOW` = Đang mưa, `HIGH` = Khô ráo |
| **Relay 1 (Bơm 1)** | `IN` | `D0` (`GPIO16`) | Điều khiển Máy bơm 1 ($12\,\text{V}$ - Xả ngập) |
| **Còi Báo Động 1** | Dây Dương (`+`) | `D4` (`GPIO2`) | Cảnh báo ngập lũ tại chỗ ($5\,\text{V}$) |

---

### 3.4. Bảng đấu dây chi tiết Node 2 (ESP8266 - Trạm Nông Nghiệp + Độ Ẩm Đất)

| Linh kiện tại Node 2 | Chân Linh Kiện | Chân ESP8266 (NodeMCU) | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **LoRa Ra-01 (#2)** | `VCC`, `GND`, `SCK`, `MISO`, `MOSI`, `NSS`, `RST` | `3V3`, `GND`, `D5`, `D6`, `D7`, `D8`, `RST` | **Gắn Tụ $220\,\mu\text{F}$ (#2) sát chân `3V3` và `GND`** |
| **BME280 (#2)** | `VIN`, `GND`, `SCL`, `SDA` | `3V3`, `GND`, `D1`, `D2` | Đo tiểu khí hậu tại vườn canh tác |
| **CB Độ Ẩm Đất** | `VCC` / `GND` | `3V3` / `GND` | Cấp nguồn cho module cảm biến độ ẩm đất |
| | `AO` (Analog) | **`A0` (`ADC0`)** | Đọc chính xác độ ẩm đất từ $0\% \rightarrow 100\%$ |
| **CB Mực Nước (#2)** | `VCC` / `GND` | `3V3` / `GND` | Đặt tại vạch báo cạn ($20\%$) trong bồn chứa nước tưới |
| | `S` / `DO` | **`RX` (`GPIO3`)** | `HIGH` = Bồn còn nước ($\sim 85\%$), `LOW` = Cạn bồn ($\sim 15\%$) |
| **CB Nước Mưa (#2)** | `VCC`, `GND`, `DO` | `3V3`, `GND`, `D3` (`GPIO0`) | `LOW` = Đang mưa (Ngắt bơm tưới), `HIGH` = Khô ráo |
| **Relay 2 (Bơm 2)** | `IN` | `D0` (`GPIO16`) | Điều khiển Máy bơm 2 ($12\,\text{V}$ - Bơm tưới gốc) |
| **Relay 3 (Bơm 3)** | `IN` | `D4` (`GPIO2`) | Điều khiển Máy bơm 3 ($12\,\text{V}$ - Bơm phun sương) |
| **Còi Báo Động 2** | Dây Dương (`+`) | **`TX` (`GPIO1`)** | Cấu hình `FUNCTION_3` điều khiển Còi 2 báo cạn bồn |

---

### 3.5. Bảng đấu dây chi tiết Gateway Trung Tâm (ESP32-S3-WROOM)

| Linh kiện tại Gateway | Chân LoRa Ra-01 (#3) | Chân ESP32-S3-WROOM | Ghi chú kỹ thuật |
| :--- | :--- | :--- | :--- |
| **Nguồn nuôi LoRa** | `VCC` / `GND` | `3V3` / `GND` | **Gắn Tụ $220\,\mu\text{F}$ (#3) sát chân `3V3` và `GND`** |
| **SPI Clock** | `SCK` | `GPIO12` | Chân xung nhịp SPI trên ESP32-S3 |
| **SPI MISO** | `MISO` | `GPIO13` | Chân dữ liệu SPI vào |
| **SPI MOSI** | `MOSI` | `GPIO11` | Chân dữ liệu SPI ra |
| **SPI Chip Select** | `NSS` (`CS`) | `GPIO10` | Chân chọn chip LoRa |
| **LoRa Reset** | `RST` | `GPIO9` | Chân Reset phần cứng cho LoRa |
| **LoRa Interrupt** | `DIO0` | `GPIO4` | Chân báo ngắt gói tin LoRa |

---

## PHẦN 4: GIAO THỨC TRUYỀN THÔNG LORA 2 CHIỀU ($433\,\text{MHz}$)

Để đảm bảo tốc độ truyền nhanh, độ nhạy thu cao và đồng bộ đầy đủ cả **Mực nước** lẫn **Độ ẩm đất**, hệ thống sử dụng khung truyền CSV 12 trường dữ liệu:

1. **Khung truyền Uplink (Từ Node 1 & Node 2 gửi về Gateway ESP32-S3):**
   * Cấu trúc chuẩn (12 trường):
     `UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,SOIL_PCT,RAIN,PUMP_A,PUMP_B,BUZZ,MODE`
   * Giải thích các trường:
     * `NODE_ID`: `1` (Trạm Thủy lợi) hoặc `2` (Trạm Nông nghiệp).
     * `TEMP`, `HUM`, `PRES`: Nhiệt độ ($^\circ\text{C}$), Độ ẩm không khí ($\%$), Áp suất ($\text{hPa}$) từ BME280.
     * `WATER_PCT`: Mực nước kênh/hồ tại Node 1 ($0 - 100\%$) hoặc Mực nước bồn tưới tại Node 2 ($0 - 100\%$).
     * `SOIL_PCT`: Độ ẩm đất tại Node 2 ($0 - 100\%$). Tại Node 1 giá trị này mặc định là `0`.
     * `RAIN`: Trạng thái mưa (`1` = Có mưa, `0` = Khô ráo).
     * `PUMP_A`: Trạng thái Bơm 1 (tại Node 1) hoặc Bơm 2 tưới cây (tại Node 2).
     * `PUMP_B`: Trạng thái Bơm 3 phun sương (tại Node 2).
     * `BUZZ`: Trạng thái Còi báo động (`1` = Bật, `0` = Tắt).
     * `MODE`: Chế độ hoạt động hiện hành (`1` = Tự động `AUTO`, `0` = Thủ công `MANUAL`).
   * Ví dụ gói tin thực tế từ Node 2: `UP,2,33.4,52.0,1008.6,85,32,0,1,0,0,1`

2. **Khung truyền Downlink (Từ Gateway ESP32-S3 phát lệnh điều khiển xuống Node 1 & Node 2):**
   * Cấu trúc chuẩn (6 trường):
     `CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ`
   * Ví dụ Gateway gửi lệnh chuyển Node 2 sang `MANUAL` và bật cả Bơm 2 lẫn Bơm 3: `CMD,2,0,1,1,0`

---

## PHẦN 5: MÃ NGUỒN ĐẦY ĐỦ CHO CÁC VI ĐIỀU KHIỂN (FIRMWARE)

### 5.1. Mã nguồn Node 1 - Trạm Thủy Lợi & Chống Ngập (`Node1_ESP8266.ino`)

Cài đặt thư viện trên Arduino IDE: `LoRa` (Sandeep Mistry), `Adafruit BME280 Library`, `Adafruit Unified Sensor`.

```cpp
/*
 * FILE: Node1_ESP8266.ino
 * VAI TRÒ: Trạm Thủy Lợi & Cảnh Báo Ngập Úng (Node 1)
 * PHẦN CỨNG: ESP8266 + LoRa Ra-01 + BME280 + CB Mực Nước (A0) + CB Mưa (D3) + Bơm 1 (D0) + Còi 1 (D4)
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
#define PIN_RAIN_DIGITAL  D3  // GPIO0 (HIGH = Khô ráo, LOW = Đang mưa)
#define PIN_LORA_SCK      D5  // GPIO14
#define PIN_LORA_MISO     D6  // GPIO12
#define PIN_LORA_MOSI     D7  // GPIO13
#define PIN_LORA_SS       D8  // GPIO15
#define PIN_RELAY_PUMP1   D0  // GPIO16 (Relay Máy bơm 1: Xả ngập)
#define PIN_BUZZER1       D4  // GPIO2  (Còi báo động 1)

// Cấu hình mức kích Relay và Còi
#define RELAY_ON          LOW
#define RELAY_OFF         HIGH
#define BUZZER_ON         HIGH
#define BUZZER_OFF        LOW

// Ngưỡng tự động chống ngập có độ trễ Hysteresis tại Node 1
#define WATER_FLOOD_HIGH  80  // Mực nước >= 80%: Bật bơm xả ngập & Còi báo động
#define WATER_SAFE_LOW    50  // Mực nước <= 50%: Tắt bơm xả & Còi báo động

Adafruit_BME280 bme;
bool bmeAvailable = false;

const int NODE_ID = 1;
bool isAutoMode   = true;     // true = AUTO (1), false = MANUAL (0)
bool pump1State   = false;
bool buzzer1State = false;

float temperature = 0.0;
float humidity    = 0.0;
float pressure    = 0.0;
int waterLevelPct = 0;
int soilMoisturePct = 0;      // Node 1 là trạm thủy lợi nên mặc định = 0
int isRaining     = 0;

unsigned long lastSendTime = 0;
const unsigned long sendInterval = 5000; // Chu kỳ gửi 5000ms

void applyActuators() {
  digitalWrite(PIN_RELAY_PUMP1, pump1State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_BUZZER1, buzzer1State ? BUZZER_ON : BUZZER_OFF);
}

void readSensors() {
  if (bmeAvailable) {
    temperature = bme.readTemperature();
    humidity    = bme.readHumidity();
    pressure    = bme.readPressure() / 100.0F;
  } else {
    temperature = 28.5;
    humidity    = 76.0;
    pressure    = 1008.2;
  }

  // Đọc cảm biến mực nước trung bình 5 lần lấy mẫu để chống nhiễu
  long rawSum = 0;
  for (int i = 0; i < 5; i++) {
    rawSum += analogRead(PIN_WATER_ANALOG);
    delay(2);
  }
  int rawWater = rawSum / 5;
  waterLevelPct = map(constrain(rawWater, 0, 750), 0, 750, 0, 100);

  // Đọc cảm biến mưa (LM393 DO: LOW = Có mưa)
  isRaining = (digitalRead(PIN_RAIN_DIGITAL) == LOW) ? 1 : 0;
}

void runAutoLogic() {
  if (!isAutoMode) return;

  // TH1: Chống ngập úng tự động với ngưỡng Hysteresis (80% bật, 50% tắt)
  if (waterLevelPct >= WATER_FLOOD_HIGH) {
    pump1State   = true;
    buzzer1State = true;
  } else if (waterLevelPct <= WATER_SAFE_LOW) {
    pump1State   = false;
    buzzer1State = false;
  }

  // TH6: Nếu áp suất khí quyển giảm thấp (< 1002 hPa) kèm mưa lớn và nước dâng >= 60% -> Bật sớm bơm xả
  if (isRaining == 1 && pressure < 1002.0 && waterLevelPct >= 60) {
    pump1State = true;
  }

  applyActuators();
}

void sendLoRaPacket() {
  // Khung truyền 12 trường: UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,SOIL_PCT,RAIN,PUMP_A,PUMP_B,BUZZ,MODE
  String payload = "UP," + String(NODE_ID) + "," +
                   String(temperature, 1) + "," +
                   String(humidity, 1) + "," +
                   String(pressure, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(soilMoisturePct) + "," +
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
  // Định dạng lệnh: CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ
  if (!msg.startsWith("CMD,")) return;

  int idx1 = msg.indexOf(',');
  int idx2 = msg.indexOf(',', idx1 + 1);
  int idx3 = msg.indexOf(',', idx2 + 1);
  int idx4 = msg.indexOf(',', idx3 + 1);
  int idx5 = msg.indexOf(',', idx4 + 1);

  if (idx5 == -1) return;

  int targetNode = msg.substring(idx1 + 1, idx2).toInt();
  if (targetNode != NODE_ID) return;

  int cmdMode  = msg.substring(idx2 + 1, idx3).toInt();
  int cmdPump1 = msg.substring(idx3 + 1, idx4).toInt();
  int cmdBuzz1 = msg.substring(idx5 + 1).toInt();

  isAutoMode = (cmdMode == 1);
  if (!isAutoMode) {
    pump1State   = (cmdPump1 == 1);
    buzzer1State = (cmdBuzz1 == 1);
    applyActuators();
  }
}

void setup() {
  Serial.begin(115200);
  delay(200);

  pinMode(PIN_RAIN_DIGITAL, INPUT_PULLUP);
  pinMode(PIN_RELAY_PUMP1, OUTPUT);
  pinMode(PIN_BUZZER1, OUTPUT);
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

### 5.2. Mã nguồn Node 2 - Trạm Nông Nghiệp Thông Minh & Độ Ẩm Đất (`Node2_ESP8266.ino`)

```cpp
/*
 * FILE: Node2_ESP8266.ino
 * VAI TRÒ: Trạm Nông Nghiệp Thông Minh Tích Hợp Độ Ẩm Đất (Node 2)
 * PHẦN CỨNG: ESP8266 + LoRa Ra-01 + BME280 + CB Độ Ẩm Đất (A0) + CB Mực Nước Bồn (RX/GPIO3)
 *            + CB Mưa (D3) + Bơm 2 Tưới Gốc (D0) + Bơm 3 Phun Sương (D4) + Còi 2 (TX/GPIO1)
 */

#include <SPI.h>
#include <LoRa.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME280.h>

#define PIN_SOIL_ANALOG   A0  // Đọc độ ẩm đất liên tục 0 - 100%
#define PIN_I2C_SCL       D1  // GPIO5
#define PIN_I2C_SDA       D2  // GPIO4
#define PIN_RAIN_DIGITAL  D3  // GPIO0 (HIGH = Khô ráo, LOW = Đang mưa)
#define PIN_LORA_SCK      D5  // GPIO14
#define PIN_LORA_MISO     D6  // GPIO12
#define PIN_LORA_MOSI     D7  // GPIO13
#define PIN_LORA_SS       D8  // GPIO15
#define PIN_RELAY_PUMP2   D0  // GPIO16 (Máy bơm 2: Bơm tưới gốc theo độ ẩm đất)
#define PIN_RELAY_PUMP3   D4  // GPIO2  (Máy bơm 3: Bơm phun sương làm mát)
#define PIN_TANK_WATER    3   // Chân RX (GPIO3) đọc trạng thái mực nước bồn tưới
#define PIN_BUZZER2       1   // Chân TX (GPIO1) điều khiển Còi báo động 2

#define RELAY_ON          LOW
#define RELAY_OFF         HIGH
#define BUZZER_ON         HIGH
#define BUZZER_OFF        LOW

// Hiệu chuẩn ADC cho Cảm biến độ ẩm đất (Điều chỉnh theo thực tế)
#define SOIL_ADC_DRY      950 // Giá trị analogRead khi đất khô hoàn toàn (0%)
#define SOIL_ADC_WET      350 // Giá trị analogRead khi đất ướt đẫm (100%)

// Các ngưỡng điều khiển tự động tại Node 2
#define TANK_EMPTY_LIMIT  20  // Mực nước bồn <= 20%: Khóa toàn bộ máy bơm & Bật còi báo cạn
#define SOIL_LOW_START    35  // Độ ẩm đất <= 35%: Bật Máy bơm 2 tưới cây
#define SOIL_HIGH_STOP    65  // Độ ẩm đất >= 65%: Tắt Máy bơm 2 (Đất đã đủ ẩm)
#define TEMP_MIST_HIGH    35.0 // Nhiệt độ >= 35 độ C & Ẩm KK < 50%: Bật Máy bơm 3 phun sương

Adafruit_BME280 bme;
bool bmeAvailable = false;

const int NODE_ID = 2;
bool isAutoMode   = true;
bool pump2State   = false;    // Máy bơm 2 (Tưới gốc)
bool pump3State   = false;    // Máy bơm 3 (Phun sương)
bool buzzer2State = false;    // Còi báo động 2

float temperature   = 0.0;
float humidity      = 0.0;
float pressure      = 0.0;
int waterLevelPct   = 85;     // Mực nước trong bồn trữ tưới cây (%)
int soilMoisturePct = 50;     // Độ ẩm đất (%)
int isRaining       = 0;

unsigned long lastSendTime = 0;
const unsigned long sendInterval = 6300; // Lệch pha với Node 1 để chống va chạm gói tin LoRa

void applyActuators() {
  digitalWrite(PIN_RELAY_PUMP2, pump2State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_RELAY_PUMP3, pump3State ? RELAY_ON : RELAY_OFF);
  digitalWrite(PIN_BUZZER2, buzzer2State ? BUZZER_ON : BUZZER_OFF);
}

void readSensors() {
  if (bmeAvailable) {
    temperature = bme.readTemperature();
    humidity    = bme.readHumidity();
    pressure    = bme.readPressure() / 100.0F;
  } else {
    temperature = 32.0;
    humidity    = 58.0;
    pressure    = 1008.0;
  }

  // 1. Đọc Cảm biến độ ẩm đất tại chân A0 (Lấy trung bình 5 mẫu)
  long rawSoilSum = 0;
  for (int i = 0; i < 5; i++) {
    rawSoilSum += analogRead(PIN_SOIL_ANALOG);
    delay(2);
  }
  int rawSoil = rawSoilSum / 5;
  // Cảm biến độ ẩm đất có đặc tính nghịch đảo: Đất khô ADC cao, Đất ướt ADC thấp
  int pct = map(rawSoil, SOIL_ADC_DRY, SOIL_ADC_WET, 0, 100);
  soilMoisturePct = constrain(pct, 0, 100);

  // 2. Đọc Cảm biến mực nước bồn chứa tại chân RX (GPIO3)
  // Khi nước trên vạch an toàn (HIGH) -> 85%, khi tụt dưới vạch cạn (LOW) -> 15%
  bool tankHasWater = (digitalRead(PIN_TANK_WATER) == HIGH);
  waterLevelPct = tankHasWater ? 85 : 15;

  // 3. Đọc Cảm biến mưa tại chân D3 (LOW = Đang mưa)
  isRaining = (digitalRead(PIN_RAIN_DIGITAL) == LOW) ? 1 : 0;
}

void runAutoLogic() {
  if (!isAutoMode) return;

  // TH4: Khóa liên động bảo vệ chống cháy máy bơm khi bồn chứa cạn nước (<= 20%)
  if (waterLevelPct <= TANK_EMPTY_LIMIT) {
    pump2State   = false;
    pump3State   = false;
    buzzer2State = true; // Bật còi cảnh báo hết nước trong bồn trữ
    applyActuators();
    return;
  } else {
    buzzer2State = false;
  }

  // TH2 & TH5: Tưới gốc tự động theo Độ Ẩm Đất (Hysteresis 35% - 65%) và ngắt khi trời mưa
  if (isRaining == 1) {
    pump2State = false; // Trời đang mưa -> Ngắt bơm tưới ngay lập tức
  } else {
    if (soilMoisturePct <= SOIL_LOW_START) {
      pump2State = true; // Đất khô <= 35% -> Bật bơm tưới
    } else if (soilMoisturePct >= SOIL_HIGH_STOP) {
      pump2State = false; // Đất đã đủ ẩm >= 65% -> Tắt bơm tưới
    }
    // TH5: Bù ẩm sớm nếu nắng nóng cực đoan (>= 36 độ C) và độ ẩm đất dưới 45%
    if (temperature >= 36.0 && soilMoisturePct < 45) {
      pump2State = true;
    }
  }

  // TH3: Phun sương hạ nhiệt khi nắng nóng gay gắt (>= 35 độ C và độ ẩm không khí < 50%)
  if (temperature >= TEMP_MIST_HIGH && humidity < 50.0 && isRaining == 0) {
    pump3State = true;
  } else if (temperature <= (TEMP_MIST_HIGH - 2.0) || humidity >= 65.0 || isRaining == 1) {
    pump3State = false;
  }

  applyActuators();
}

void sendLoRaPacket() {
  // Khung truyền 12 trường: UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,SOIL_PCT,RAIN,PUMP_A,PUMP_B,BUZZ,MODE
  String payload = "UP," + String(NODE_ID) + "," +
                   String(temperature, 1) + "," +
                   String(humidity, 1) + "," +
                   String(pressure, 1) + "," +
                   String(waterLevelPct) + "," +
                   String(soilMoisturePct) + "," +
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
  // Định dạng lệnh: CMD,NODE_ID,MODE,PUMP_A,PUMP_B,BUZZ
  if (!msg.startsWith("CMD,")) return;

  int idx1 = msg.indexOf(',');
  int idx2 = msg.indexOf(',', idx1 + 1);
  int idx3 = msg.indexOf(',', idx2 + 1);
  int idx4 = msg.indexOf(',', idx3 + 1);
  int idx5 = msg.indexOf(',', idx4 + 1);

  if (idx5 == -1) return;

  int targetNode = msg.substring(idx1 + 1, idx2).toInt();
  if (targetNode != NODE_ID) return;

  int cmdMode  = msg.substring(idx2 + 1, idx3).toInt();
  int cmdPump2 = msg.substring(idx3 + 1, idx4).toInt();
  int cmdPump3 = msg.substring(idx4 + 1, idx5).toInt();
  int cmdBuzz2 = msg.substring(idx5 + 1).toInt();

  isAutoMode = (cmdMode == 1);
  if (!isAutoMode) {
    pump2State   = (cmdPump2 == 1);
    pump3State   = (cmdPump3 == 1);
    buzzer2State = (cmdBuzz2 == 1);
    applyActuators();
  }
}

void setup() {
  // Chuyển chân RX (GPIO3) và TX (GPIO1) sang chế độ GPIO thông thường
  pinMode(PIN_TANK_WATER, FUNCTION_3);
  pinMode(PIN_TANK_WATER, INPUT);
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

### 5.3. Mã nguồn Gateway Trung Tâm (`Gateway_ESP32S3.ino`)

Cài đặt thư viện trên Arduino IDE: `LoRa` (Sandeep Mistry), `Blynk` (Volodymyr Shymanskyy), `ArduinoJson` (Benoit Blanchon).

```cpp
/*
 * FILE: Gateway_ESP32S3.ino
 * VAI TRÒ: Trạm Gateway Trung Tâm (ESP32-S3-WROOM)
 * CHỨC NĂNG: Thu/Phát LoRa 2 chiều (12 trường dữ liệu) + Đồng bộ Web PHP + Blynk IoT + Cảnh báo Telegram
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

// Thông tin cấu hình WiFi, Server PHP và Telegram Bot
const char* WIFI_SSID     = "TEN_WIFI_CUA_BAN";
const char* WIFI_PASS     = "MAT_KHAU_WIFI";
const char* PHP_API_URL   = "http://192.168.1.100/iot_lora/api.php"; // Thay bằng IP máy chạy XAMPP / Hosting
const char* TG_BOT_TOKEN  = "YOUR_TELEGRAM_BOT_TOKEN";               // Token từ @BotFather
const char* TG_CHAT_ID    = "YOUR_TELEGRAM_CHAT_ID";                 // ID từ @userinfobot

// Định nghĩa chân SPI LoRa trên ESP32-S3-WROOM
#define LORA_SCK    12
#define LORA_MISO   13
#define LORA_MOSI   11
#define LORA_SS     10
#define LORA_RST    9
#define LORA_DIO0   4

struct NodeData {
  float temp = 0;
  float hum  = 0;
  float pres = 1008;
  int water  = 0;
  int soil   = 0;
  int rain   = 0;
  int pumpA  = 0;
  int pumpB  = 0;
  int buzz   = 0;
  int mode   = 1; // 1 = AUTO, 0 = MANUAL
  int rssi   = 0;
  unsigned long lastSeen = 0;
  bool online = false;
  bool offlineAlertSent = false;
};

NodeData node1, node2;

// Biến lưu lệnh điều khiển hiện tại
int ctrlMode1 = 1, ctrlPump1 = 0, ctrlBuzz1 = 0;
int ctrlMode2 = 1, ctrlPump2 = 0, ctrlPump3 = 0, ctrlBuzz2 = 0;

unsigned long lastServerSync   = 0;
unsigned long lastTelegramTime = 0;
const unsigned long NODE_TIMEOUT_MS = 60000; // 60 giây không phản hồi -> Báo OFFLINE

// Hàm gửi cảnh báo qua Telegram (Chống spam tối thiểu 15 giây giữa 2 tin nhắn)
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
    doc["text"]    = message;
    String body;
    serializeJson(doc, body);
    int httpCode = https.POST(body);
    Serial.printf("[TELEGRAM] HTTP %d: %s\n", httpCode, message.c_str());
    https.end();
  }
}

// Hàm phát gói tin điều khiển Downlink xuống Node 1 hoặc Node 2
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
// V0: Mode Node 1 | V1: Bơm 1 | V2: Còi 1
// V3: Mode Node 2 | V4: Bơm 2 (Tưới gốc) | V5: Bơm 3 (Phun sương) | V6: Còi 2
BLYNK_WRITE(V0) { ctrlMode1 = param.asInt(); sendLoRaCommand(1); }
BLYNK_WRITE(V1) { ctrlPump1 = param.asInt(); ctrlMode1 = 0; Blynk.virtualWrite(V0, 0); sendLoRaCommand(1); }
BLYNK_WRITE(V2) { ctrlBuzz1 = param.asInt(); ctrlMode1 = 0; Blynk.virtualWrite(V0, 0); sendLoRaCommand(1); }

BLYNK_WRITE(V3) { ctrlMode2 = param.asInt(); sendLoRaCommand(2); }
BLYNK_WRITE(V4) { ctrlPump2 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }
BLYNK_WRITE(V5) { ctrlPump3 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }
BLYNK_WRITE(V6) { ctrlBuzz2 = param.asInt(); ctrlMode2 = 0; Blynk.virtualWrite(V3, 0); sendLoRaCommand(2); }

void updateBlynkTelemetry() {
  if (!Blynk.connected()) return;
  Blynk.virtualWrite(V10, node1.temp);
  Blynk.virtualWrite(V11, node1.hum);
  Blynk.virtualWrite(V12, node1.water);
  Blynk.virtualWrite(V13, node2.temp);
  Blynk.virtualWrite(V14, node2.hum);
  Blynk.virtualWrite(V15, node2.water);
  Blynk.virtualWrite(V16, node2.soil); // V16: Độ ẩm đất Node 2 (%)
}

// Kiểm tra 8 trường hợp thực tế để bắn cảnh báo Telegram
void evaluateRealWorldAlerts(int nodeId) {
  if (nodeId == 1) {
    // TH1: Mực nước kênh/hồ dâng cao nguy hiểm (>= 80%)
    if (node1.water >= 80) {
      sendTelegramAlert("🚨 [CẢNH BÁO NGẬP LŨ - NODE 1]\nMực nước kênh/hồ đạt " + String(node1.water) + "%!\nĐã tự động bật Máy bơm xả 1 & Còi báo động 1.");
    }
    // TH6: Áp suất khí quyển giảm sâu kèm mưa lớn
    if (node1.rain == 1 && node1.pres < 1002.0 && node1.water >= 60) {
      sendTelegramAlert("⛈ [CẢNH BÁO GIÔNG BÃO - NODE 1]\nÁp suất giảm thấp (" + String(node1.pres, 1) + " hPa) kèm mưa lớn và nước dâng (" + String(node1.water) + "%). Nguy cơ lũ quét!");
    }
  } else if (nodeId == 2) {
    // TH4: Khóa liên động khi bồn chứa cạn nước (<= 20%)
    if (node2.water <= 20) {
      if (node2.soil <= 35) {
        sendTelegramAlert("⚠️ [KHẨN CẤP - NODE 2]\nĐất đang rất khô (" + String(node2.soil) + "%) nhưng BỒN CHỨA CẠN NƯỚC (" + String(node2.water) + "%)!\nĐã KHÓA Máy bơm 2 & 3 để chống cháy bơm.");
      } else {
        sendTelegramAlert("⚠️ [CẢNH BÁO CẠN BỒN - NODE 2]\nMực nước bồn tưới chỉ còn " + String(node2.water) + "%!\nĐã ngắt bảo vệ Máy bơm 2 & Máy bơm 3.");
      }
    }
    // TH2: Tự động tưới khi độ ẩm đất thấp (<= 35%)
    else if (node2.soil <= 35 && node2.pumpA == 1) {
      sendTelegramAlert("🌱 [TƯỚI TỰ ĐỘNG - NODE 2]\nĐộ ẩm đất xuống thấp (" + String(node2.soil) + "%).\nHệ thống đã tự động BẬT Máy bơm 2 tưới cây.");
    }
    // TH3: Nắng nóng cực đoan bật phun sương (>= 35 độ C)
    if (node2.temp >= 35.0 && node2.pumpB == 1) {
      sendTelegramAlert("🌡 [CẢNH BÁO NẮNG NÓNG - NODE 2]\nNhiệt độ vườn đạt " + String(node2.temp, 1) + "°C, Ẩm KK " + String(node2.hum, 1) + "%.\nĐã bật Máy bơm 3 phun sương hạ nhiệt.");
    }
  }
}

// Giải mã gói tin LoRa 12 trường từ Node 1 & Node 2
void processLoRaPacket(String packet, int rssi) {
  // UP,NODE_ID,TEMP,HUM,PRES,WATER_PCT,SOIL_PCT,RAIN,PUMP_A,PUMP_B,BUZZ,MODE
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
  NodeData* target = (id == 1) ? &node1 : &node2;

  target->temp  = parts[2].toFloat();
  target->hum   = parts[3].toFloat();
  target->pres  = parts[4].toFloat();
  target->water = parts[5].toInt();
  target->soil  = parts[6].toInt();
  target->rain  = parts[7].toInt();
  target->pumpA = parts[8].toInt();
  target->pumpB = parts[9].toInt();
  target->buzz  = parts[10].toInt();
  target->mode  = parts[11].toInt();
  target->rssi  = rssi;
  target->lastSeen = millis();
  target->online   = true;
  target->offlineAlertSent = false;

  if (id == 1 && ctrlMode1 == 1) {
    ctrlPump1 = target->pumpA;
    ctrlBuzz1 = target->buzz;
  } else if (id == 2 && ctrlMode2 == 1) {
    ctrlPump2 = target->pumpA;
    ctrlPump3 = target->pumpB;
    ctrlBuzz2 = target->buzz;
  }

  evaluateRealWorldAlerts(id);

  delay(50);
  sendLoRaCommand(id);
}

// Đồng bộ 2 chiều với Web Server PHP
void syncWithPHPServer() {
  if (WiFi.status() != WL_CONNECTED) return;

  HTTPClient http;
  http.begin(PHP_API_URL);
  http.addHeader("Content-Type", "application/json");

  StaticJsonDocument<1024> doc;
  JsonObject n1 = doc.createNestedObject("node1");
  n1["temp"]   = node1.temp;
  n1["hum"]    = node1.hum;
  n1["pres"]   = node1.pres;
  n1["water"]  = node1.water;
  n1["soil"]   = node1.soil;
  n1["rain"]   = node1.rain;
  n1["pump1"]  = node1.pumpA;
  n1["buzz1"]  = node1.buzz;
  n1["mode"]   = node1.mode;
  n1["rssi"]   = node1.rssi;
  n1["online"] = node1.online ? 1 : 0;

  JsonObject n2 = doc.createNestedObject("node2");
  n2["temp"]   = node2.temp;
  n2["hum"]    = node2.hum;
  n2["pres"]   = node2.pres;
  n2["water"]  = node2.water;
  n2["soil"]   = node2.soil;
  n2["rain"]   = node2.rain;
  n2["pump2"]  = node2.pumpA;
  n2["pump3"]  = node2.pumpB;
  n2["buzz2"]  = node2.buzz;
  n2["mode"]   = node2.mode;
  n2["rssi"]   = node2.rssi;
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
        int webMode1 = cmd["mode1"] | ctrlMode1;
        int webPump1 = cmd["pump1"] | ctrlPump1;
        int webBuzz1 = cmd["buzz1"] | ctrlBuzz1;
        int webMode2 = cmd["mode2"] | ctrlMode2;
        int webPump2 = cmd["pump2"] | ctrlPump2;
        int webPump3 = cmd["pump3"] | ctrlPump3;
        int webBuzz2 = cmd["buzz2"] | ctrlBuzz2;

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

// TH8: Giám sát mất kết nối LoRa (Watchdog Offline Alert)
void checkNodeWatchdog() {
  unsigned long now = millis();
  if (node1.lastSeen > 0 && (now - node1.lastSeen > NODE_TIMEOUT_MS)) {
    node1.online = false;
    if (!node1.offlineAlertSent) {
      node1.offlineAlertSent = true;
      sendTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm Node 1 (Thủy lợi) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT.");
    }
  }
  if (node2.lastSeen > 0 && (now - node2.lastSeen > NODE_TIMEOUT_MS)) {
    node2.online = false;
    if (!node2.offlineAlertSent) {
      node2.offlineAlertSent = true;
      sendTelegramAlert("❌ [MẤT KẾT NỐI LORA]\nTrạm Node 2 (Nông nghiệp) không phản hồi quá 60 giây! Vui lòng kiểm tra nguồn NLMT.");
    }
  }
}

void setup() {
  Serial.begin(115200);
  delay(500);

  SPI.begin(LORA_SCK, LORA_MISO, LORA_MOSI, LORA_SS);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
  if (!LoRa.begin(433E6)) {
    Serial.println("Lỗi khởi tạo LoRa trên Gateway ESP32-S3!");
    while (1) { delay(1000); }
  }
  LoRa.setSpreadingFactor(7);
  LoRa.setSyncWord(0xF3);

  WiFi.begin(WIFI_SSID, WIFI_PASS);
  int retry = 0;
  while (WiFi.status() != WL_CONNECTED && retry < 20) {
    delay(500);
    retry++;
  }

  Blynk.config(BLYNK_AUTH_TOKEN);
  sendTelegramAlert("✅ [HỆ THỐNG KHỞI ĐỘNG]\nGateway ESP32-S3 đã kết nối WiFi và sẵn sàng thu phát LoRa 433MHz!");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    Blynk.run();
  }

  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    String incoming = "";
    while (LoRa.available()) {
      incoming += (char)LoRa.read();
    }
    int rssi = LoRa.packetRssi();
    processLoRaPacket(incoming, rssi);
  }

  if (millis() - lastServerSync >= 3000) {
    lastServerSync = millis();
    checkNodeWatchdog();
    syncWithPHPServer();
    updateBlynkTelemetry();
  }
}
```

---

## PHẦN 6: MÃ NGUỒN HỆ THỐNG WEB SERVER PHP & DASHBOARD (`api.php` & `index.php`)

### 6.1. Mã nguồn Backend RESTful API (`api.php`)

Lưu file tại đường dẫn `C:\xampp\htdocs\iot_lora\api.php`:

```php
<?php
/**
 * FILE: api.php
 * CHỨC NĂNG: Nhận dữ liệu từ Gateway ESP32-S3 (gồm cả Độ ẩm đất), lưu lịch sử JSON và trả lệnh điều khiển
 */
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
date_default_timezone_set("Asia/Ho_Chi_Minh");

$stateFile = __DIR__ . "/iot_state.json";
$logFile   = __DIR__ . "/iot_history.json";

if (!file_exists($stateFile)) {
    $defaultState = [
        "updated_at" => date("Y-m-d H:i:s"),
        "node1" => [
            "temp" => 29.0, "hum" => 75.0, "pres" => 1008.2,
            "water" => 35, "soil" => 0, "rain" => 0,
            "pump1" => 0, "buzz1" => 0, "mode" => 1, "rssi" => -65, "online" => 0
        ],
        "node2" => [
            "temp" => 31.5, "hum" => 58.0, "pres" => 1008.0,
            "water" => 85, "soil" => 48, "rain" => 0,
            "pump2" => 0, "pump3" => 0, "buzz2" => 0, "mode" => 1, "rssi" => -72, "online" => 0
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
    $rawInput  = file_get_contents("php://input");
    $jsonInput = json_decode($rawInput, true);

    // 1. Nhận lệnh điều khiển Auto/Manual từ giao diện Web
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

    // 2. Nhận dữ liệu cảm biến từ Gateway ESP32-S3
    if (isset($jsonInput["node1"]) && isset($jsonInput["node2"])) {
        $currentData["updated_at"] = date("Y-m-d H:i:s");
        $currentData["node1"]      = $jsonInput["node1"];
        $currentData["node2"]      = $jsonInput["node2"];

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

        // Lưu tối đa 30 bản ghi lịch sử gần nhất
        $history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
        if (!is_array($history)) $history = [];
        array_unshift($history, [
            "time"     => date("H:i:s"),
            "n1_water" => $jsonInput["node1"]["water"],
            "n1_temp"  => $jsonInput["node1"]["temp"],
            "n2_soil"  => isset($jsonInput["node2"]["soil"]) ? $jsonInput["node2"]["soil"] : 0,
            "n2_water" => $jsonInput["node2"]["water"],
            "n2_temp"  => $jsonInput["node2"]["temp"],
            "n2_hum"   => $jsonInput["node2"]["hum"]
        ]);
        $history = array_slice($history, 0, 30);
        file_put_contents($logFile, json_encode($history, JSON_PRETTY_PRINT));

        echo json_encode([
            "status"   => "ok",
            "commands" => $currentData["commands"]
        ]);
        exit;
    }
}

$history = file_exists($logFile) ? json_decode(file_get_contents($logFile), true) : [];
echo json_encode([
    "state"   => $currentData,
    "history" => $history
]);
?>
```

---

### 6.2. Mã nguồn Giao diện Điều khiển & Giám sát Web App (`index.php`)

Lưu file tại đường dẫn `C:\xampp\htdocs\iot_lora\index.php`:

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ Thống Quản Lý IoT LoRa Đa Năng Vùng Sâu Vùng Xa</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-900 text-slate-100 min-h-screen p-4 md:p-8">
    <div class="max-w-6xl mx-auto">
        <!-- Header -->
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center bg-slate-800 p-6 rounded-2xl border border-slate-700 mb-6 shadow-lg">
            <div>
                <h1 class="text-2xl md:text-3xl font-bold text-emerald-400">TRUNG TÂM ĐIỀU HÀNH IOT LORA 433MHz</h1>
                <p class="text-slate-400 text-sm mt-1">Gateway ESP32-S3 &bull; 2 Trạm ESP8266 Năng Lượng Mặt Trời &bull; Tích Hợp Cảm Biến Độ Ẩm Đất</p>
            </div>
            <div class="mt-4 md:mt-0 text-right">
                <span class="text-xs text-slate-400 block">Đồng bộ lần cuối:</span>
                <span id="last-update" class="text-sm font-mono text-amber-400">Đang tải...</span>
            </div>
        </div>

        <!-- 2 Node Cards -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <!-- NODE 1: TRẠM THỦY LỢI / CHỐNG NGẬP -->
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg flex flex-col justify-between">
                <div>
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
                            <div class="text-xs text-slate-400">Nhiệt độ / Ẩm KK</div>
                            <div id="n1-th" class="text-lg font-bold text-emerald-300 mt-1">--°C / --%</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60 col-span-2 sm:col-span-3">
                            <div class="text-xs text-slate-400">Áp suất khí quyển (BME280)</div>
                            <div id="n1-pres" class="text-base font-semibold text-purple-300 mt-1">-- hPa</div>
                        </div>
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
            <div class="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-lg flex flex-col justify-between">
                <div>
                    <div class="flex justify-between items-center border-b border-slate-700 pb-4 mb-4">
                        <div>
                            <h2 class="text-xl font-bold text-emerald-400">TRẠM NODE 2: VƯỜN NÔNG NGHIỆP</h2>
                            <p class="text-xs text-slate-400" id="n2-rssi">Sóng LoRa: -- dBm</p>
                        </div>
                        <span id="n2-status" class="px-3 py-1 rounded-full text-xs font-bold bg-slate-700 text-slate-300">KIỂM TRA...</span>
                    </div>

                    <!-- Thông số cảm biến Node 2 (Có thanh tiến trình Độ ẩm đất) -->
                    <div class="grid grid-cols-2 sm:grid-cols-3 gap-3 mb-6">
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-emerald-500/40 col-span-2 sm:col-span-3">
                            <div class="flex justify-between items-center">
                                <span class="text-xs text-slate-300 font-semibold">🌱 Độ ẩm đất canh tác (Ngưỡng tưới: 35% - 65%)</span>
                                <span id="n2-soil" class="text-xl font-bold text-emerald-400">--%</span>
                            </div>
                            <div class="w-full bg-slate-700 h-2.5 rounded-full mt-2 overflow-hidden">
                                <div id="n2-soil-bar" class="bg-emerald-500 h-full rounded-full transition-all duration-300" style="width: 0%"></div>
                            </div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Mực nước Bồn Tưới</div>
                            <div id="n2-water" class="text-xl font-bold text-sky-400 mt-1">--%</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Trạng thái Mưa</div>
                            <div id="n2-rain" class="text-base font-bold text-amber-300 mt-1">--</div>
                        </div>
                        <div class="bg-slate-900/70 p-3 rounded-xl border border-slate-700/60">
                            <div class="text-xs text-slate-400">Nhiệt độ / Ẩm KK</div>
                            <div id="n2-th" class="text-base font-bold text-emerald-300 mt-1">--°C / --%</div>
                        </div>
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
                            <span>BƠM 2 (TƯỚI GỐC)</span>
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
                            <th class="p-3">Độ ẩm đất (Node 2)</th>
                            <th class="p-3">Mực nước Bồn (Node 2)</th>
                            <th class="p-3">Nhiệt độ / Ẩm KK (Node 2)</th>
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
            const manBtn  = document.getElementById(manBtnId);
            if (isAuto == 1) {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-sky-500 text-white";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
            } else {
                autoBtn.className = "px-4 py-1.5 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 border border-slate-600";
                manBtn.className  = "px-4 py-1.5 rounded-lg text-xs font-bold bg-amber-500 text-slate-950";
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
            payload[modeKey]   = 0;
            payload[deviceKey] = nextVal;
            setCommand(payload);
        }

        async function fetchDashboard() {
            try {
                const res  = await fetch('api.php');
                const data = await res.json();
                const st   = data.state;
                currentCommands = st.commands;

                document.getElementById('last-update').innerText = st.updated_at;

                // Cập nhật Node 1
                document.getElementById('n1-water').innerText = st.node1.water + '%';
                document.getElementById('n1-rain').innerText  = st.node1.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n1-th').innerText    = `${st.node1.temp}°C / ${st.node1.hum}%`;
                document.getElementById('n1-pres').innerText  = `${st.node1.pres} hPa`;
                document.getElementById('n1-rssi').innerText  = `Sóng LoRa: ${st.node1.rssi} dBm`;
                const s1 = document.getElementById('n1-status');
                s1.innerText = st.node1.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s1.className = st.node1.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Cập nhật Node 2 (Gồm cả Độ ẩm đất)
                const soilVal = st.node2.soil !== undefined ? st.node2.soil : 0;
                document.getElementById('n2-soil').innerText     = soilVal + '%';
                document.getElementById('n2-soil-bar').style.width = soilVal + '%';
                document.getElementById('n2-water').innerText    = st.node2.water + '%';
                document.getElementById('n2-rain').innerText     = st.node2.rain == 1 ? 'ĐANG MƯA' : 'KHÔ RÁO';
                document.getElementById('n2-th').innerText       = `${st.node2.temp}°C / ${st.node2.hum}%`;
                document.getElementById('n2-rssi').innerText     = `Sóng LoRa: ${st.node2.rssi} dBm`;
                const s2 = document.getElementById('n2-status');
                s2.innerText = st.node2.online == 1 ? 'TRỰC TUYẾN' : 'MẤT KẾT NỐI';
                s2.className = st.node2.online == 1 ? 'px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/40' : 'px-3 py-1 rounded-full text-xs font-bold bg-rose-500/20 text-rose-400 border border-rose-500/40';

                // Nút điều khiển
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
                        <td class="p-3 font-bold text-emerald-400">${row.n2_soil !== undefined ? row.n2_soil : '--'}%</td>
                        <td class="p-3 font-semibold text-sky-300">${row.n2_water}%</td>
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

## PHẦN 7: HƯỚNG DẪN CẤU HÌNH, HIỆU CHUẨN & QUY TRÌNH KIỂM THỬ NGHIỆM THU

### 7.1. Hướng dẫn hiệu chuẩn Cảm biến độ ẩm đất tại Node 2
1. Khi để đầu dò cảm biến độ ẩm đất khô ráo ngoài không khí, giá trị ADC đọc được trên chân `A0` của ESP8266 rơi vào khoảng $920 - 1023$ (được gán vào hằng số `SOIL_ADC_DRY = 950`).
2. Khi cắm đầu dò vào đất ướt sũng nước, giá trị ADC giảm xuống khoảng $300 - 400$ (được gán vào hằng số `SOIL_ADC_WET = 350`).
3. Hàm `map(rawSoil, SOIL_ADC_DRY, SOIL_ADC_WET, 0, 100)` kèm `constrain(..., 0, 100)` trong code `Node2_ESP8266.ino` sẽ quy đổi chính xác thành thang đo từ $0\%$ đến $100\%$.

### 7.2. Bảng quy ước toàn bộ Virtual Pin trên Blynk IoT

| Virtual Pin | Tên Datastream trên Blynk | Kiểu dữ liệu | Khoảng giá trị | Chức năng |
| :--- | :--- | :--- | :--- | :--- |
| **`V0`** | Chế độ Node 1 | Integer | `0` $\rightarrow$ `1` | `1`: Tự động (`AUTO`), `0`: Thủ công (`MANUAL`) |
| **`V1`** | Máy bơm 1 (Xả ngập) | Integer | `0` $\rightarrow$ `1` | Bật/Tắt Máy bơm 1 tại Node 1 |
| **`V2`** | Còi báo động 1 | Integer | `0` $\rightarrow$ `1` | Bật/Tắt Còi 1 tại Node 1 |
| **`V3`** | Chế độ Node 2 | Integer | `0` $\rightarrow$ `1` | `1`: Tự động (`AUTO`), `0`: Thủ công (`MANUAL`) |
| **`V4`** | Máy bơm 2 (Tưới gốc) | Integer | `0` $\rightarrow$ `1` | Bật/Tắt Máy bơm 2 tại Node 2 |
| **`V5`** | Máy bơm 3 (Phun sương) | Integer | `0` $\rightarrow$ `1` | Bật/Tắt Máy bơm 3 tại Node 2 |
| **`V6`** | Còi báo động 2 | Integer | `0` $\rightarrow$ `1` | Bật/Tắt Còi 2 tại Node 2 |
| **`V10`** | Nhiệt độ Node 1 | Double | $0 \rightarrow 60^\circ\text{C}$ | Hiển thị nhiệt độ BME280 tại Node 1 |
| **`V11`** | Độ ẩm KK Node 1 | Double | $0 \rightarrow 100\%$ | Hiển thị độ ẩm không khí tại Node 1 |
| **`V12`** | Mực nước Hồ Node 1 | Integer | $0 \rightarrow 100\%$ | Hiển thị mực nước kênh/hồ tại Node 1 |
| **`V13`** | Nhiệt độ Node 2 | Double | $0 \rightarrow 60^\circ\text{C}$ | Hiển thị nhiệt độ BME280 tại Node 2 |
| **`V14`** | Độ ẩm KK Node 2 | Double | $0 \rightarrow 100\%$ | Hiển thị độ ẩm không khí tại Node 2 |
| **`V15`** | Mực nước Bồn Node 2 | Integer | $0 \rightarrow 100\%$ | Hiển thị mực nước bồn trữ tại Node 2 |
| **`V16`** | **Độ ẩm đất Node 2** | Integer | $0 \rightarrow 100\%$ | **Hiển thị độ ẩm đất canh tác tại Node 2** |

### 7.3. Bảng kịch bản kiểm thử nghiệm thu (Test Cases) dùng cho báo cáo

| Mã TC | Kịch bản thử nghiệm | Thao tác giả lập thực tế | Kết quả đạt yêu cầu |
| :--- | :--- | :--- | :--- |
| **TC01** | Báo động & Xả ngập tự động (Node 1) | Nhúng Cảm biến mực nước 1 ngập quá $80\%$ chiều dài bản mạch. | Relay 1 đóng bật Máy bơm 1, Còi 1 kêu, Telegram báo ngập, Web hiển thị $\ge 80\%$. |
| **TC02** | Tưới cây tự động theo Độ ẩm đất (Node 2) | Để Cảm biến độ ẩm đất ở ngoài không khí khô ($\le 35\%$), Cảm biến mực nước 2 ngâm trong cốc nước. | Relay 2 đóng bật Máy bơm 2, Telegram báo bật tưới tự động, thanh Độ ẩm đất trên Web báo $< 35\%$. |
| **TC03** | Tự ngắt bơm tưới khi đất đủ ẩm (Node 2) | Cắm Cảm biến độ ẩm đất vào cốc đất ướt ($\ge 65\%$). | Relay 2 tự động ngắt Máy bơm 2. |
| **TC04** | Tự ngắt bơm tưới khi trời đổ mưa (Node 2) | Khi Máy bơm 2 đang chạy, nhỏ vài giọt nước lên tấm Cảm biến mưa 2. | Máy bơm 2 ngắt ngay lập tức, Web hiển thị trạng thái `ĐANG MƯA`. |
| **TC05** | Khóa liên động khi cạn bồn tưới (Node 2) | Nhấc Cảm biến mực nước 2 ra khỏi cốc nước (giả lập cạn bồn $\le 20\%$) trong khi đất đang khô. | Máy bơm 2 và 3 tắt ngay lập tức, Còi 2 kêu, Telegram báo khẩn cấp cạn bồn. |
| **TC06** | Điều khiển Manual 2 chiều từ Web/Blynk | Bấm nút `THỦ CÔNG (MANUAL)` trên Web PHP và nhấn bật `BƠM 3 (PHUN SƯƠNG)`. | Sau $1 - 3$ giây, Gateway phát lệnh LoRa xuống Node 2 đóng Relay 3 bật Máy bơm 3. |