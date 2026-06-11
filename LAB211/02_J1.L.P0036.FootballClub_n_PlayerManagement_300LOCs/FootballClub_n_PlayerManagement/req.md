# AI SYSTEM INSTRUCTION: PROJECT J1.L.P0036 - FOOTBALL CLUB & PLAYER MANAGEMENT

## 1. TỔNG QUAN DỰ ÁN (PROJECT CONTEXT)
- [cite_start]**Tên dự án:** Football Club & Player Management (J1.L.P0036)[cite: 150, 152, 154].
- [cite_start]**Mục tiêu:** Xây dựng ứng dụng Java Console quản lý Câu lạc bộ và Cầu thủ cho giải đấu European Elite League (EEL)[cite: 162].
- [cite_start]**Yêu cầu cốt lõi:** Tuân thủ TUYỆT ĐỐI các nguyên lý OOP (Encapsulation, Abstraction, Polymorphism, Inheritance) [cite: 167] [cite_start]và phương pháp Computational Thinking (CT)[cite: 124, 146]. [cite_start]Dữ liệu lưu trong file text `clubs.txt` và `players.txt`[cite: 165].

## 2. QUY CHUẨN KIẾN TRÚC MÃ NGUỒN (ARCHITECTURE & OOP RULES)
[cite_start]AI PHẢI cấu trúc code theo mô hình sau để đạt điểm tối đa của Rubric[cite: 145, 147]:
- **Package `model`:** Chứa dữ liệu. [cite_start]**BẮT BUỘC** tạo một interface `IEntity` (có hàm `displayInfo()`) và các class `Club`, `Player` implement interface này để tính điểm Đa hình (Polymorphism)[cite: 145]. [cite_start]Phải có đầy đủ private fields, constructors, getters/setters, override `toString()`, `equals()`[cite: 145].
- **Package `utils`:** - `ValidationUtils`: Chứa các hàm static bắt lỗi nhập liệu (dùng Regex, Try-Catch) liên tục lặp lại đến khi đúng.
  - [cite_start]`FileUtils`: Xử lý đọc/ghi file TXT định dạng CSV phân cách bằng dấu phẩy[cite: 168, 169].
- [cite_start]**Package `manager`:** Chứa `ClubManager` và `PlayerManager` xử lý logic, tách biệt độc lập (Decomposition)[cite: 147].
- **Package `main`:** Chứa class `Main` chạy menu điều hướng.

## 3. RÀNG BUỘC DỮ LIỆU (STRICT VALIDATIONS - PATTERN RECOGNITION)
AI phải implement các kiểm tra sau trong quá trình nhập liệu:
**Club Constraints:**
- [cite_start]`clubId`: Duy nhất, format `CL-xxxx` (VD: CL-0001)[cite: 174, 175, 204].
- [cite_start]`clubName`, `sponsorBrand`: Không được để trống[cite: 176, 177].
- [cite_start]`budget`: Số thực dương (Double > 0)[cite: 178].

**Player Constraints:**
- [cite_start]`playerId`: Duy nhất, format `Pxxxx` (VD: P0001)[cite: 180, 181].
- [cite_start]`clubId`: BẮT BUỘC phải tồn tại trong danh sách Club hiện có[cite: 182, 204].
- [cite_start]`playerName`: Không được để trống[cite: 183].
- [cite_start]`position`: Chỉ nhận 1 trong 5 giá trị: Goalkeeper, Defender, Midfielder, Forward, Winger (Case-insensitive)[cite: 184].
- [cite_start]`shirtNumber`: Số nguyên từ 1 đến 99[cite: 185]. [cite_start]**CRITICAL RULE:** Phải DUY NHẤT trong cùng một câu lạc bộ (Nhiều club có thể có số áo 10, nhưng trong 1 club chỉ có duy nhất một số áo 10)[cite: 185, 204].

## 4. DANH SÁCH 14 CHỨC NĂNG CẦN IMPLEMENT (ALGORITHM DESIGN)
[cite_start]Khi triển khai class Main và Managers, phải làm đúng logic của 14 chức năng này[cite: 187, 204, 206]:
1. [cite_start]**List clubs:** In dưới dạng bảng có header[cite: 204].
2. [cite_start]**Add club:** Validate theo rule mục 3[cite: 204].
3. [cite_start]**Search club by ID:** Báo lỗi "This club does not exist!" nếu không thấy[cite: 204].
4. [cite_start]**Update club by ID:** Cho phép người dùng nhấn Enter (để trống) để bỏ qua update field đó[cite: 204].
5. [cite_start]**Filter clubs by budget:** In danh sách club có budget <= input[cite: 204].
6. **List players (CRITICAL LOGIC):** Sắp xếp TĂNG DẦN theo Tên Câu Lạc Bộ (Club Name). Nếu cùng Club, sắp xếp TĂNG DẦN theo số áo (Shirt number). [cite_start]*Lưu ý: Phải join với list Club để lấy Club Name*[cite: 193, 204].
7. [cite_start]**Search players by partial name:** Tìm kiếm không phân biệt hoa thường[cite: 194, 204].
8. [cite_start]**Add player:** Validate chặt chẽ rule mục 3, đặc biệt là `clubId` tồn tại và `shirtNumber` duy nhất trong club[cite: 204].
9. [cite_start]**Remove player by ID:** Báo lỗi nếu không thấy[cite: 206].
10. [cite_start]**Update player by ID:** Tương tự chức năng 4, nếu update số áo phải validate lại tính duy nhất trong club đó[cite: 206].
11. [cite_start]**List players by position:** In ra danh sách theo vị trí nhập vào[cite: 206].
12. [cite_start]**Save to files:** Ghi đè list hiện tại xuống file[cite: 206].
13. **Load from files:** Xóa data trên RAM, đọc lại từ file. Validate chặt. [cite_start]Lỗi dòng nào báo "Load data failed!"[cite: 206].
14. [cite_start]**Quit:** TỰ ĐỘNG LƯU thay đổi (nếu có) trước khi thoát[cite: 206].

## 5. CHỈ ĐỊNH CHO AI TRONG PHIÊN LÀM VIỆC NÀY (INSTRUCTIONS FOR AI EXECUTION)
- Sử dụng Java chuẩn, không dùng thư viện ngoài (Third-party libraries).
- Đặt tên biến chuẩn CamelCase, class chuẩn PascalCase. [cite_start]Viết Javadoc comment rõ ràng để tránh "code spaghetti"[cite: 149].
- **Không tự động sinh ra toàn bộ code ngay lập tức.** - Khi người dùng yêu cầu "Bắt đầu triển khai", hãy sinh code **lần lượt theo từng Package** (Model -> Utils -> Managers -> Main) để người dùng dễ kiểm soát và sao chép.