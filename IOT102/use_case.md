# TÓM TẮT BÁO CÁO KỸ THUẬT HỆ THỐNG IOT LORA 433MHZ
**Đề tài:** Trạm Tưới Tiêu & Bồn Chứa Nông Nghiệp Thông Minh (Node 2) & Trạm Trung Tâm Gateway (ESP32-S3)  
**Vi điều khiển:** ESP8266 NodeMCU (Node 2) & ESP32-S3-WROOM-1 (Gateway)  
**Giao tiếp:** LoRa Ra-01 SX1278 (433MHz, SyncWord: 0xF3)

---

## 1. CẤU HÌNH PHẦN CỨNG & SƠ ĐỒ ĐẤU DÂY NODE 2

### Bảng Đấu Dây Chi Tiết (Node 2)
| STT | Tên Linh Kiện | Chân Linh Kiện | Chân Kết Nối Đích (ESP32 / Nguồn) | Màu Dây | Mô Tả Kỹ Thuật |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **ESP8266 NodeMCU** | `VIN` / `VV` | `5V (+)` Nguồn hệ thống | Đỏ đậm | Cấp nguồn 5V DC chính cho mạch vi điều khiển |
| | | `G` | Mass chung (`GND`) | Đen | Nối mass chung toàn mạch |
| | | `3V` | Đường nguồn `3.3V` cảm biến | Đỏ tươi | Cấp nguồn 3.3V chuẩn cho cảm biến & LoRa |
| **2** | **Cảm biến BME280** | `VIN` / `GND` / `SCL` / `SDA` | `3V` / `G` / `D1` / `D2` | Đỏ / Đen / Vàng / Xanh lá | Giám sát nhiệt độ, độ ẩm không khí & áp suất |
| **3** | **Cảm biến độ ẩm đất** | `VCC` / `GND` / `AO` | `3V` / `G` / `A0` | Đỏ / Đen / Cam | Đo giá trị Analog độ ẩm đất qua chân ADC0 |
| **4** | **Module Relay (x2)** | `DC+` / `DC-` / `IN2` / `IN3` | `5V` / `G` / `D0` / `D4` | Đỏ / Đen / Nâu / Hồng | Điều khiển máy bơm tưới gốc & bơm phun sương |
| **5** | **Pin Li-Ion 18650** | `+` / `-` | `B+` / `B-` (Mạch TP4056) | Đỏ / Đen | Lưu trữ năng lượng pin 3.7V - 4.2V |
| **6** | **Mạch sạc TP4056** | `IN+`/`IN-`/`OUT+`/`OUT-` | Tấm Solar & Nguồn 5V | Đỏ / Đen | Quản lý sạc pin năng lượng mặt trời |
| **7** | **Tấm Pin NLMT** | Dây Đỏ (`+`) / Dây Đen (`-`) | `IN+` / `IN-` (Mạch TP4056) | Đỏ / Đen | Thu năng lượng ánh sáng mặt trời |
| **8** | **Bơm nước 5V (x2)** | Dây Đỏ (`+`) / Dây Đen (`-`) | Tiếp điểm `NO` Relay & Mass | Xanh dương / Đen | Bơm nước mini 5V đóng ngắt qua Relay |
| **9** | **Cảm biến mực nước** | `+` / `-` / `S` | `3V` / `G` / `RX` (GPIO3) | Đỏ / Đen / Tím | Phát hiện cạn/đầy nước trong bồn chứa |
| **10**| **Cảm biến nước mưa** | `VCC` / `GND` / `DO` | `3V` / `G` / `D3` (GPIO0) | Đỏ / Đen / Xanh ngọc | Phát hiện trời mưa để ngắt bơm tưới |
| **11**| **LoRa Ra-01 SX1278** | `3.3V`/`GND`/`SCK`/`MISO`/`MOSI`/`NSS`/`RST` | `3V`/`G`/`D5`/`D6`/`D7`/`D8`/`RST` | Đa màu | Giao tiếp truyền thông vô tuyến SPI |
| **12**| **Còi báo động 2** | `+` (VCC) / `-` (GND) | `TX` (GPIO1) / `G` | Vàng / Đen | Còi hú cảnh báo khẩn cấp tại trạm Node 2 |

---

## 2. CẤU HÌNH PHẦN CỨNG & SƠ ĐỒ ĐẤU DÂY GATEWAY

### Bảng Đấu Dây Chi Tiết (Gateway)
| STT | Tên Linh Kiện | Chân Linh Kiện | Chân Kết Nối Đích (ESP32-S3) | Màu Dây | Mô Tả Kỹ Thuật |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | **ESP32-S3-WROOM-1** | `5V` / `GND` / `3V3` | Nguồn ngoài & Đường LoRa | Đỏ / Đen | Vi điều khiển trung tâm thu phát Gateway |
| **2** | **LoRa Ra-01 SX1278** | `3.3V` / `GND` / `SCK` / `MISO` / `MOSI` / `NSS` / `RST` / `DIO0` | `3V3`/`GND`/`12`/`13`/`11`/`10`/`9`/`4` | Đa màu | Thu phát sóng vô tuyến RF (Giao thức SPI) |
| **3** | **LED cảnh báo mưa** | Anode (`+`) / Cathode (`-`) | GPIO7 / `GND` | Xanh ngọc / Đen | Đèn LED chỉ thị trạng thái mưa trực quan |
| **4** | **Còi Gateway** | `+` (VCC) / `-` (GND) | GPIO8 / `GND` | Vàng / Đen | Còi báo động trung tâm tại trạm Gateway |

---

## 3. CÁC TRƯỜNG HỢP VẬN HÀNH THỰC TẾ (8 USE CASES THÀNH CÔNG)

### Phần I: Vận hành & Bảo vệ cục bộ tại Node 2
1. **Tưới gốc tự động theo độ ẩm đất:** Tự động kích hoạt Bơm 2 khi độ ẩm đất đo từ chân A0 xuống dưới ngưỡng $55\%$ ($SOIL\_START\_IRRIG$) và ngắt bơm khi độ ẩm đạt hoặc vượt mức $70\%$ ($SOIL\_STOP\_IRRIG$).
2. **Phun sương hạ nhiệt theo nhiệt độ:** Kích hoạt Bơm 3 khi nhiệt độ môi trường vượt ngưỡng $33.0^\circ\text{C}$ và độ ẩm đất chưa bão hòa, tự động tắt khi nhiệt độ hạ $\le 30.0^\circ\text{C}$ hoặc đất đủ ẩm.
3. **Khóa liên động chống cháy bơm khi cạn bồn chứa:** Kích hoạt khi mực nước bồn giảm dưới $20\%$ ($RX = LOW$, tương ứng mức $15\%$), lập tức khóa cưỡng bức toàn bộ máy bơm (`dryRunLocked = true`) và phát âm thanh còi nhắc nhở mỗi 5 giây.
4. **Hoãn tưới khẩn cấp khi trời mưa:** Lập tức ngắt toàn bộ hoạt động bơm tưới tiêu khi cảm biến mưa phát hiện có mưa ($D3 = LOW$) nhằm tiết kiệm nước và bảo vệ cây trồng.
5. **Phát hiện kẹt bơm / vỡ ống dẫn:** Nhận diện sự cố tắc nghẽn hoặc đứt ống nếu Bơm 2 chạy liên tục trong 3 phút ($180000\text{ ms}$) mà độ ẩm đất không tăng thêm ít nhất $3\%$, từ đó khóa vĩnh viễn cụm bơm và hú còi báo động khẩn cấp.

### Phần II: Giám sát & Quản lý trung tâm tại Gateway
6. **Giám sát vòng đời kết nối Node (Node Watchdog):** Theo dõi thời gian phản hồi tín hiệu LoRa của các trạm (Timeout $> 60\text{ giây}$), tự động cập nhật trạng thái mất kết nối lên cơ sở dữ liệu PHP và đẩy cảnh báo sự cố nguồn NLMT qua Telegram.
7. **Đồng bộ đám mây & Điều khiển từ xa (Cloud Sync & Web Override):** Định kỳ gửi gói tin `POST` JSON chứa toàn bộ thông số cảm biến lên Server PHP (`api.php`) và tiếp nhận lệnh điều khiển thủ công (`MANUAL` / `AUTO`) từ giao diện Web để truyền lệnh xuống Node qua sóng LoRa.
8. **Phản hồi chỉ thị trực quan tại trạm trung tâm:** Cập nhật trạng thái đèn LED chỉ thị mưa (`GPIO7`) và còi hú trung tâm (`GPIO8`) theo thời gian thực dựa vào các gói tin truyền về từ trạm Node mà không phụ thuộc hoàn toàn vào mạng Internet.