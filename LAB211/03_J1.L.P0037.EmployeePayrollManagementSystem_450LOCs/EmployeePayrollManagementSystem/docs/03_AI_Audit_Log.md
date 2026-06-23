# AI AUDIT LOG & AI REFLECTION — Employee Payroll Management System (J1.L.P0037)

- **Sinh viên:** _<điền tên>_ · **MSSV:** _<điền MSSV>_ · **Học kỳ:** SU26 (LAB211)

> ⚠️ **LƯU Ý QUAN TRỌNG (chính trực học thuật):** Đây là *khung mẫu* được xây dựng bám
> sát code thực tế của dự án. Bạn **phải đọc, hiểu và chỉnh sửa lại bằng trải nghiệm
> thật của mình** (prompt thật bạn đã hỏi, kết quả AI bạn thật sự nhận được). Khi vấn
> đáp (Oral Viva), giảng viên hỏi 3–5 entries — bạn cần giải thích được *vì sao chọn*,
> *AI sai chỗ nào*, *bạn sửa thế nào*. Nếu chỉ copy log mà không hiểu sẽ bị đánh giá là
> "log giả" và mất tới 30% điểm.

---

## Bảng phân bố entries theo thành phần CT

| Thành phần CT | Số entry | Các entry |
|---|---|---|
| Decomposition | 3 | #001, #006, #021 |
| Pattern Recognition | 4 | #004, #005, #012, #020 |
| Abstraction | 5 | #002, #003, #009, #010, #017 |
| Algorithms | 9 | #007, #008, #011, #013, #014, #015, #016, #018, #019 |
| **Tổng core prompts** | **21** | gồm **2 hallucination**: #011, #012 |

**Cấu trúc mỗi entry (7 phần):** 1) Entry # · 2) Prompt Type · 3) Stage/Component ·
4) Problem/Context · 5) Prompt to AI · 6) AI Response (Summary) · 7) Human Delta
(Critical Thinking · Contextualization · Creative Synthesis · Decision Ownership).

---

### Entry #001 — DECISION-MAKING — Decomposition
- **Context:** Cần chia dự án thành các phần/lớp dễ quản lý trước khi code.
- **Prompt:** "Tôi làm bài LAB211 Java console quản lý nhân viên & tính lương. Nên tổ chức project thành những package/lớp nào để rõ ràng và dễ mở rộng?"
- **AI Response:** Gợi ý tách model / service / util / main; mỗi lớp một trách nhiệm (SRP).
- **Human Delta:**
  - *Critical Thinking:* Gợi ý đúng hướng nhưng còn chung chung, chưa gắn với đặc thù "nhiều vai trò".
  - *Contextualization:* Đề có 4 vai trò tính phụ cấp khác nhau → tôi cần thêm tầng `factory` mà AI không nhắc tới.
  - *Creative Synthesis:* Chốt 5 package: `model`, `factory`, `tools`, `business`, `main`.
  - *Decision Ownership:* Tôi chọn cấu trúc này vì tách rõ "dữ liệu / khởi tạo / công cụ / nghiệp vụ / giao diện", thuận cho phần Decomposition của báo cáo.

### Entry #002 — DECISION-MAKING — Abstraction
- **Context:** 4 vai trò có cách cộng phụ cấp khác nhau; chưa biết nên dùng 1 lớp hay kế thừa.
- **Prompt:** "I have 4 employee roles (Developer/Tester/Manager/HR) with different allowance. Should I use one class with if-else on role, or inheritance with polymorphism?"
- **AI Response:** Khuyên dùng kế thừa: lớp `Employee` trừu tượng + method override cho từng vai trò.
- **Human Delta:**
  - *Critical Thinking:* Đồng ý, và bổ sung lý do AI chưa nêu: **Open/Closed Principle** — thêm role mới không phải sửa vòng tính lương.
  - *Contextualization:* Rubric cho 10đ riêng cho Inheritance/Polymorphism → đây là lựa chọn "ăn điểm".
  - *Creative Synthesis:* Tách hành vi khác biệt thành `roleAllowance()` abstract; phần chung giữ ở lớp cha.
  - *Decision Ownership:* Chốt `Employee` abstract + 4 lớp con; kiểm chứng bằng cách thử thêm role mới chỉ tốn một lớp.

### Entry #003 — DECISION-MAKING — Abstraction
- **Context:** Có nên thêm interface cho hành vi tính lương không.
- **Prompt:** "Có nên tách calculateSalary() ra một interface Payable riêng, hay để thẳng trong lớp Employee?"
- **AI Response:** Interface giúp giảm phụ thuộc, minh hoạ đa hình rõ hơn nhưng có thể thừa nếu chỉ một loại đối tượng.
- **Human Delta:**
  - *Critical Thinking:* AI nêu cả ưu/nhược hợp lý.
  - *Contextualization:* Đề nhấn mạnh OOP (abstraction + polymorphism) → có interface sẽ thể hiện rõ năng lực.
  - *Creative Synthesis:* Tạo `interface Payable { double calculateSalary(); }` và `Employee implements Payable`.
  - *Decision Ownership:* Giữ interface để demo đa hình qua kiểu `Payable`/`List<Employee>`.

### Entry #004 — VERIFICATION — Pattern Recognition
- **Context:** Cần regex chuẩn cho ID dạng `E` + 3 chữ số.
- **Prompt:** "Regex `E\d{3}` trong Java có khớp đúng E + đúng 3 chữ số không? Có cần `^...$` khi dùng String.matches()?"
- **AI Response:** `matches()` tự động neo toàn chuỗi nên không bắt buộc `^$`; `E\d{3}` đúng cho 3 chữ số.
- **Human Delta:**
  - *Critical Thinking:* Tôi kiểm chứng: `"E1234".matches("E\\d{3}")` trả về `false` → đúng như AI nói.
  - *Contextualization:* Trong file đọc bằng `Pattern.matcher().matches()` cũng neo toàn chuỗi.
  - *Creative Synthesis:* Đặt hằng `ID_REGEX = "E\\d{3}"` dùng chung cho cả Inputter và Validator.
  - *Decision Ownership:* Dùng `matches()` không cần `^$`; viết test nhanh xác nhận trước khi áp dụng.

### Entry #005 — PROBLEM-SOLVING — Pattern Recognition
- **Context:** Validate role/status — không muốn lặp if so sánh từng giá trị.
- **Prompt:** "Cách validate một chuỗi có nằm trong tập giá trị cho phép (không phân biệt hoa thường) trong Java 8?"
- **AI Response:** Dùng `List`/`Set` chứa giá trị hợp lệ rồi lặp `equalsIgnoreCase`, hoặc stream `anyMatch`.
- **Human Delta:**
  - *Critical Thinking:* Cách stream gọn nhưng tôi giữ vòng for cho dễ đọc với người chấm.
  - *Contextualization:* Role/status đều là tập nhỏ cố định → dùng `Arrays.asList`.
  - *Creative Synthesis:* Tạo `ROLES`, `STATUSES` trong `Validator` + `Inputter.inputInSet()` tái dùng.
  - *Decision Ownership:* Chốt vòng for `equalsIgnoreCase` để chuẩn hoá luôn về đúng dạng trong tập.

### Entry #006 — DECISION-MAKING — Decomposition
- **Context:** Logic tạo đối tượng theo role nên đặt ở đâu.
- **Prompt:** "Việc map role string sang đúng lớp con nên đặt ở đâu để không lặp lại trong load và add?"
- **AI Response:** Gợi ý Factory pattern: một phương thức `create(...)` tập trung việc khởi tạo.
- **Human Delta:**
  - *Critical Thinking:* Hợp lý — tránh lặp switch ở nhiều nơi.
  - *Contextualization:* Cả Function 1 (load) và 2 (add) và 3 (update đổi role) đều cần tạo lớp con.
  - *Creative Synthesis:* `EmployeeFactory.create()` ném `IllegalArgumentException` cho role lạ.
  - *Decision Ownership:* Đặt factory riêng package; load/add/update đều gọi chung một chỗ.

### Entry #007 — PROBLEM-SOLVING — Algorithms
- **Context:** Bảng lương chỉ được tính cho nhân viên đang làm việc.
- **Prompt:** "Làm sao tính tổng lương chỉ cho nhân viên active và vẫn in từng dòng?"
- **AI Response:** Lặp danh sách, lọc `isActive()`, cộng dồn, in từng dòng.
- **Human Delta:**
  - *Critical Thinking:* Đúng; tôi bổ sung cờ `any` để xử lý trường hợp **không có** nhân viên active.
  - *Contextualization:* Đề ghi rõ "for each active employee" → inactive phải bị bỏ qua.
  - *Creative Synthesis:* In bảng có header + dòng kẻ + dòng TOTAL.
  - *Decision Ownership:* Tự kiểm chứng tổng bằng tính tay 1 nhân viên (E004 = 1430.0).

### Entry #008 — PROBLEM-SOLVING — Algorithms
- **Context:** Tìm kiếm theo 4 thuộc tính khác nhau (ID/name/role/status).
- **Prompt:** "Cách thiết kế hàm search theo nhiều thuộc tính mà không viết 4 hàm riêng?"
- **AI Response:** Cho người dùng chọn loại thuộc tính, dùng `switch` lấy đúng field rồi `contains()`.
- **Human Delta:**
  - *Critical Thinking:* Hợp lý; tôi dùng `contains()` để hỗ trợ tìm gần đúng (substring).
  - *Contextualization:* Người dùng có thể chỉ nhớ một phần tên → tìm substring tiện hơn equals.
  - *Creative Synthesis:* Chuẩn hoá `toLowerCase()` cả field lẫn keyword.
  - *Decision Ownership:* Một hàm `searchByAttribute()` + switch chọn field; in bảng kết quả.

### Entry #009 — PROBLEM-SOLVING — Abstraction
- **Context:** Update cho phép Enter để giữ giá trị cũ.
- **Prompt:** "Trong Java console, làm sao để khi update người dùng nhấn Enter thì giữ nguyên giá trị cũ?"
- **AI Response:** Đọc cả dòng; nếu chuỗi rỗng thì không thay đổi field.
- **Human Delta:**
  - *Critical Thinking:* Đúng, nhưng cần validate giá trị mới trước khi gán (AI chưa nhấn mạnh).
  - *Contextualization:* Phải tránh gán giá trị sai (vd base âm) làm hỏng dữ liệu.
  - *Creative Synthesis:* `inputOptional()` + nếu nhập sai định dạng/ràng buộc thì in cảnh báo và giữ giá trị cũ.
  - *Decision Ownership:* Chốt: rỗng → giữ cũ; có nhập nhưng sai → cảnh báo + giữ cũ.

### Entry #010 — DECISION-MAKING — Abstraction
- **Context:** Đổi role khi update — role gắn với lớp con cụ thể.
- **Prompt:** "Nếu mỗi role là một lớp con khác nhau, khi update đổi role thì xử lý thế nào cho đúng OOP?"
- **AI Response:** Không thể đổi kiểu của object đã tạo; nên tạo lại object mới đúng lớp con.
- **Human Delta:**
  - *Critical Thinking:* Chính xác — Java không cho "đổi class" của instance.
  - *Contextualization:* Nếu chỉ gán chuỗi role thì `roleAllowance()` đa hình sẽ sai.
  - *Creative Synthesis:* Dùng `EmployeeFactory.create()` tạo lại, giữ nguyên các trường khác, rồi `list.set()`.
  - *Decision Ownership:* Chốt phương án tạo lại đối tượng — đúng bản chất đa hình.

### Entry #011 — VERIFICATION — Algorithms — ⚠️ HALLUCINATION
- **Context:** Đề không cho công thức lương; thử hỏi AI "công thức chuẩn".
- **Prompt:** "What is the standard monthly payroll formula for this assignment?"
- **AI Response:** AI **khẳng định chắc nịch** một "công thức chuẩn" (nhân hệ số thuế cố định, trừ bảo hiểm…) như thể đề có quy định sẵn.
- **Human Delta (phát hiện hallucination — Fabrication / Context Misunderstanding):**
  - *Critical Thinking:* **AI bịa.** Đọc kỹ đề bài → KHÔNG có mục công thức lương nào.
  - *Contextualization:* AI không biết nội dung đề cụ thể nên "đoán" theo công thức lương phổ biến.
  - *How Detected:* Rà soát toàn bộ đề + đối chiếu dữ liệu mẫu; không có hệ số thuế nào khớp.
  - *Corrective Action / Decision Ownership:* **Tự định nghĩa** công thức minh bạch
    `baseSalary/26 × workingDays + bonus + roleAllowance()`, ghi rõ giả định (chia 26 vì
    max working days = 26), và chỉ tính nhân viên active. Không dùng công thức AML bịa.

### Entry #012 — VERIFICATION — Pattern Recognition — ⚠️ HALLUCINATION
- **Context:** Hỏi AI sinh nhanh hàm validate nhân viên.
- **Prompt:** "Generate a validateEmployee() method for these fields."
- **AI Response:** AI sinh hàm validate nhưng **bỏ qua kiểm tra `workingDays` trong khoảng 0–26** (chỉ check `>= 0`).
- **Human Delta (phát hiện hallucination — Oversimplification):**
  - *Critical Thinking:* AI bỏ sót ràng buộc biên trên (26). Đây là lỗi "đơn giản hoá quá mức".
  - *Contextualization:* Đề ghi rõ Working Days Range: 0–26; bỏ sót sẽ cho phép dữ liệu sai (vd 30 ngày).
  - *How Detected:* So từng dòng hàm AI với bảng ràng buộc của đề → thiếu `days <= 26`.
  - *Corrective Action / Decision Ownership:* Thêm `isWorkingDays(d) = d >= 0 && d <= 26` và
    dùng `inputInt(0,26)` để chặn ngay khi nhập (đã test với input 30 → bị từ chối).

### Entry #013 — PROBLEM-SOLVING — Algorithms
- **Context:** File có khoảng trắng quanh dấu phẩy `, ` không đều.
- **Prompt:** "Tách dòng `E004, David Miller, Developer, ...` trong Java sao cho bỏ được khoảng trắng quanh dấu phẩy?"
- **AI Response:** Gợi ý `split("\\s*,\\s*")` hoặc `trim()` từng phần.
- **Human Delta:**
  - *Critical Thinking:* `split("\\s*,\\s*")` gọn và xử lý luôn khoảng trắng — đúng.
  - *Contextualization:* Dữ liệu mẫu có cả `, ` và `,` → cần regex linh hoạt.
  - *Creative Synthesis:* Dùng `split("\\s*,\\s*")` rồi vẫn kiểm tra `length == 7`.
  - *Decision Ownership:* Chốt regex split; test với dòng E003 lỗi (6 phần) → bị bỏ qua đúng.

### Entry #014 — PROBLEM-SOLVING — Algorithms
- **Context:** File có dòng lỗi khiến `parseDouble` ném exception, sợ crash.
- **Prompt:** "How to safely parse lines that may be malformed in Java without crashing the program?"
- **AI Response:** Bọc try-catch quanh đoạn parse, bỏ qua dòng lỗi.
- **Human Delta:**
  - *Critical Thinking:* Đúng hướng; tôi mở rộng thành "skip + đếm + báo cáo".
  - *Contextualization:* Đề ghi rõ "data reading process may encounter erroneous data".
  - *Creative Synthesis:* Kiểm tra đủ 7 trường + validate range + đếm `ok`/`bad`.
  - *Decision Ownership:* Chọn "skip + report" thay vì dừng toàn bộ; test với 3 dòng lỗi → "skipped 3".

### Entry #015 — DECISION-MAKING — Algorithms
- **Context:** Lưu file phải đúng định dạng 7 trường để load lại được.
- **Prompt:** "Khi ghi lại file, làm sao đảm bảo format khớp với lúc đọc để load lại không lỗi?"
- **AI Response:** Dùng `printf`/`String.format` tạo dòng đúng thứ tự trường, ngăn cách `, `.
- **Human Delta:**
  - *Critical Thinking:* Hợp lý; tôi tách thành `Employee.toDataLine()` để gói định dạng vào model.
  - *Contextualization:* Round số `%.0f` cho base/bonus để file gọn như mẫu.
  - *Creative Synthesis:* `toDataLine()` ↔ `parseAndAdd()` là cặp ghi/đọc đối xứng.
  - *Decision Ownership:* Chốt định dạng `id, name, role, base, days, bonus, status`; test save→load lại đủ bản ghi.

### Entry #016 — PROBLEM-SOLVING — Algorithms
- **Context:** Ràng buộc baseSalary dương, bonus ≥ 0 khi nhập.
- **Prompt:** "Cách viết vòng nhập số trong Java console mà bắt buộc dương / không âm và không crash khi nhập chữ?"
- **AI Response:** Vòng `while(true)` + `try parse` + kiểm tra điều kiện, lặp lại nếu sai.
- **Human Delta:**
  - *Critical Thinking:* Đúng; cần bắt `NumberFormatException` để không crash.
  - *Contextualization:* Người dùng dễ gõ nhầm chữ → phải nhập lại thay vì văng lỗi.
  - *Creative Synthesis:* Tách `inputPositiveDouble()`, `inputNonNegativeDouble()`, `inputInt(min,max)`.
  - *Decision Ownership:* Gom các vòng nhập về `Inputter` để tái dùng cho add/update.

### Entry #017 — PROBLEM-SOLVING — Abstraction
- **Context:** So sánh status không nên phân biệt hoa thường.
- **Prompt:** "So sánh trạng thái 'active'/'Active' nên dùng gì trong Java?"
- **AI Response:** `equalsIgnoreCase`.
- **Human Delta:**
  - *Critical Thinking:* Đúng và đơn giản.
  - *Contextualization:* Dữ liệu file có thể viết hoa/thường khác nhau.
  - *Creative Synthesis:* `isActive()` dùng `"active".equalsIgnoreCase(status)`; setter chuẩn hoá về thường.
  - *Decision Ownership:* Chốt dùng `equalsIgnoreCase` ở mọi nơi so sánh status/role.

### Entry #018 — DECISION-MAKING — Algorithms
- **Context:** In bảng nhân viên cho thẳng cột.
- **Prompt:** "Cách căn cột đẹp khi in bảng trong Java console?"
- **AI Response:** Dùng `printf` với width specifier (`%-15s`, `%8.0f`…).
- **Human Delta:**
  - *Critical Thinking:* Đúng; nhưng định dạng số `%,.1f` phụ thuộc **locale**.
  - *Contextualization:* Máy tôi locale VN → ra `1.430,0` thay vì `1,430.0` như mẫu đề.
  - *Creative Synthesis:* Đặt `Locale.setDefault(Locale.US)` ở đầu `main` để đồng nhất.
  - *Decision Ownership:* Chốt format `printf` + ép Locale.US; gói header vào `printTable()`.

### Entry #019 — VERIFICATION — Algorithms
- **Context:** Kiểm chứng tổng payroll có khớp tính tay.
- **Prompt:** "Cho base=1300, days=21, bonus=250, Developer allowance 10% — lương tháng theo công thức của tôi là bao nhiêu?"
- **AI Response:** Tính ra 1300/26×21 + 250 + 130 = 1430.
- **Human Delta:**
  - *Critical Thinking:* Tôi tự tính lại tay: 1050 + 250 + 130 = 1430 → khớp output chương trình.
  - *Contextualization:* Dùng để xác nhận `calculateSalary()` đa hình chạy đúng cho Developer.
  - *Creative Synthesis:* Lặp lại kiểm chứng cho Manager (có +200) → khớp.
  - *Decision Ownership:* Tin tưởng công thức sau khi kiểm chứng 2 vai trò bằng tay.

### Entry #020 — PROBLEM-SOLVING — Pattern Recognition
- **Context:** ID phải duy nhất cả khi Add và khi Load.
- **Prompt:** "Cách đảm bảo ID nhân viên là duy nhất khi thêm mới và khi đọc file?"
- **AI Response:** Kiểm tra tồn tại trước khi thêm; có thể override `equals/hashCode` theo id.
- **Human Delta:**
  - *Critical Thinking:* Tôi làm cả hai: `exists(id)` chặn trùng + override `equals/hashCode` theo id.
  - *Contextualization:* File mẫu có E004 và E009 lặp lại → phải loại trùng khi load.
  - *Creative Synthesis:* `findById()` dùng `equalsIgnoreCase`; load kiểm tra `!exists(id)`.
  - *Decision Ownership:* Test load file có ID trùng → bị skip đúng (2 dòng trùng bị bỏ).

### Entry #021 — VERIFICATION — Decomposition
- **Context:** Rà soát đủ 9 chức năng so với đề trước khi nộp.
- **Prompt:** "Đối chiếu giúp tôi: 9 chức năng đề yêu cầu đã có đủ trong menu chưa?"
- **AI Response:** Liệt kê lại 9 chức năng và đối chiếu với menu.
- **Human Delta:**
  - *Critical Thinking:* Tôi tự checklist lại từng case trong `switch` của `Main`.
  - *Contextualization:* Đảm bảo đúng thứ tự 1–9 như đề (Load…Quit).
  - *Creative Synthesis:* Bổ sung `quit()` nhắc lưu (cờ dirty) — đúng yêu cầu "confirm saving".
  - *Decision Ownership:* Xác nhận 9/9 chức năng có mặt và chạy đúng qua phiên test thực tế.

---

## Tự đánh giá theo rubric AI Reflection (30đ)

- **AI Usage Documentation:** 21 core prompts đủ 7 phần, có prompt/kết quả/quyết định.
- **Critical Thinking & Reflection:** phát hiện 2 hallucination (#011 công thức lương, #012 thiếu range), có corrective action.
- **Effective AI-assisted Learning:** dùng AI cho phân tích yêu cầu, thiết kế class, debug, verify — có iterative prompting (vd #011 → tự định nghĩa công thức).

## Checklist tự chấm

- [x] 9 chức năng chạy đúng
- [x] `Employee` abstract + 4 lớp con + đa hình `calculateSalary()`
- [x] Payroll chỉ tính nhân viên active
- [x] `load` không crash với dòng lỗi, có báo cáo số dòng bỏ
- [x] Công thức lương tự định nghĩa & giải thích (không bịa theo AI)
- [x] 16–24 core prompts, ≥ 2 hallucination (gồm công thức lương)
- [x] Human Delta đủ 4 câu hỏi mỗi entry
