# BÁO CÁO DỰ ÁN — Employee Payroll Management System

- **Mã đề:** J1.L.P0037 · **LOC mục tiêu:** 450 · **Học kỳ:** SU26 · **Môn:** LAB211
- **Ngôn ngữ:** Java (Console, OOP) · **JDK:** 1.8
- **Sinh viên:** _<điền tên>_ · **MSSV:** _<điền MSSV>_

> Báo cáo bám theo khung 6 mục bắt buộc: Introduction → Problem Analysis →
> Computational Thinking → OOP Design → Flowchart → Conclusion.

---

## 1. Introduction

Một công ty quy mô vừa cần một ứng dụng **Java console** để quản lý thông tin nhân
viên và **tính lương tháng**. Mỗi nhân viên gồm thông tin cá nhân (ID, tên), thông tin
công việc (vai trò, số ngày công) và dữ liệu lương (lương cơ bản, thưởng, trạng thái).

- **Người dùng:** nhân viên phụ trách tính lương (Payroll Officer).
- **Mục tiêu:** lưu hồ sơ trong tệp văn bản, hỗ trợ thêm/sửa/xoá/tìm kiếm, tính bảng
  lương cho nhân viên đang làm việc, và xuất/lưu dữ liệu an toàn.
- **Phạm vi:** ứng dụng dòng lệnh, dữ liệu lưu ở `employees.txt`, 9 chức năng theo đề.

## 2. Problem Analysis

### 2.1 Dữ liệu vào/ra
- **Đầu vào:** tệp `employees.txt`, mỗi dòng:
  `id, name, role, baseSalary, workingDays, bonus, status`; và dữ liệu nhập từ bàn phím.
- **Đầu ra:** danh sách nhân viên dạng bảng, bảng lương tháng, tệp dữ liệu đã cập nhật.

### 2.2 Ràng buộc (Validation Rules)

| Trường | Quy tắc | Mẫu hợp lệ |
|---|---|---|
| Employee ID | Không rỗng; duy nhất; định dạng `E` + 3 chữ số | `E001` |
| Name | Không rỗng | `David Miller` |
| Role | ∈ {Developer, Tester, Manager, HR} | `Developer` |
| Base Salary | Số dương ( > 0 ) | `1300` |
| Working Days | Số nguyên 0–26 | `21` |
| Bonus | ≥ 0 | `250` |
| Status | `active` hoặc `inactive` | `active` |

### 2.3 Công thức lương (đề KHÔNG cho — tự định nghĩa & giải thích)

```
salary = baseSalary / 26 × workingDays   (lương theo ngày công)
       + bonus
       + roleAllowance()                  (phụ cấp theo vai trò — đa hình)
```

Phụ cấp theo vai trò: **Developer** 10%·base · **Tester** 5%·base · **Manager**
20%·base + 200 · **HR** 8%·base. Chia 26 vì đề quy định số ngày công tối đa là 26.
Quyết định này được ghi lại và kiểm chứng trong AI Audit Log (Entry #001 và #011).

### 2.4 Các trường hợp biên (Edge cases)

| Nhóm | Trường hợp | Cách xử lý trong code |
|---|---|---|
| Danh sách rỗng | Hiển thị/tìm/tính khi chưa có dữ liệu | `isEmpty()` báo "list is empty", không crash |
| Trùng khoá | Thêm/Load ID đã tồn tại | `exists(id)` chặn |
| Nhập sai kiểu | Nhập chữ khi cần số | vòng lặp nhập lại trong `Inputter` |
| Ngoài khoảng | workingDays ngoài 0–26 | `inputInt(0,26)` lặp lại |
| Dòng dữ liệu lỗi | Thiếu trường/số sai trong file | try-catch + đếm + báo "skipped N invalid lines" |
| Hủy thao tác | Chọn N khi xác nhận xoá | giữ nguyên dữ liệu |
| Thoát chưa lưu | Có thay đổi chưa lưu | cờ `dirty` nhắc lưu |

## 3. Computational Thinking

### 3.1 Decomposition — Phân rã

- **Theo dữ liệu:** một thực thể `Employee` 7 trường; phân hoá theo 4 vai trò → gợi ý
  phân cấp lớp. Tách riêng việc đọc/ghi `employees.txt`.
- **Theo chức năng:** CRUD (2,3,4) · Query (5,7) · Payroll (6) · File (1,8,9).
- **Theo class (OOP view):** `model` (Employee + 4 lớp con + Payable) · `factory`
  (EmployeeFactory) · `tools` (Validator, Inputter) · `business` (EmployeeManager) ·
  `main` (Main). Mỗi package giải quyết một mối quan tâm độc lập.

### 3.2 Pattern Recognition — Nhận diện mẫu

| Mẫu lặp | Giải pháp tái dùng |
|---|---|
| Kiểm tra định dạng | Regex `E\d{3}` cho ID; tập giá trị hợp lệ cho role/status |
| CRUD lặp trên danh sách | Template Add/Update/Remove/Search trên `List<Employee>` |
| if-else theo vai trò | Thay bằng **đa hình**: `roleAllowance()` override ở lớp con |
| Nhập liệu lặp lại | Gom về `Inputter` (inputInt/inputByRegex/inputInSet…) |

### 3.3 Abstraction — Trừu tượng hoá

- **Thuộc tính cốt lõi chung:** id, name, baseSalary, workingDays, bonus, status.
- **Hành vi cốt lõi chung:** `calculateSalary()` (khung), `isActive()`, `toString()`.
- **Phần khác biệt theo vai trò** được trừu tượng hoá thành `roleAllowance()` (abstract)
  và `getRole()` (abstract). Interface `Payable` tách riêng hành vi "có thể tính lương".

### 3.4 Algorithm Design — Thiết kế thuật toán

**Load an toàn (Function 1):**
```
FUNCTION load(path):
    ok = 0; bad = 0; clear(list)
    FOREACH line IN file:
        IF blank(line) THEN CONTINUE
        parts = split(line, ",")
        IF parts.size != 7 THEN bad++; CONTINUE
        TRY parse base/days/bonus
        IF parse fails THEN bad++; CONTINUE
        IF NOT validateAll(parts) OR exists(id) THEN bad++; CONTINUE
        list.add(EmployeeFactory.create(parts)); ok++
    PRINT "Loaded " + ok + ", skipped " + bad
```

**Tính lương tháng (Function 6):**
```
FUNCTION monthlyPayroll():
    total = 0; any = false
    FOREACH e IN list:
        IF e.isActive():
            s = e.calculateSalary()   // đa hình theo role
            print(e.id, e.role, s); total += s; any = true
    IF NOT any THEN print "No active employee"
    ELSE print "TOTAL (active only): " + total
```

(Flowchart minh hoạ đầy đủ ở tệp `01_Diagrams.md`.)

## 4. OOP Design (UML)

Xem **Class Diagram** và **Use Case Diagram** trong `01_Diagrams.md`. Tóm tắt quan hệ:

- `Employee` (abstract) `implements Payable`; `Developer/Tester/Manager/HR` **extends**
  `Employee` và override `roleAllowance()`, `getRole()`.
- `EmployeeManager` **has-a** `List<Employee>` (quan hệ tổng hợp).
- `EmployeeFactory` tạo đúng lớp con theo role (Open/Closed Principle: thêm vai trò mới
  chỉ cần thêm một lớp con + một nhánh factory, **không sửa** vòng tính lương).

**Bốn nguyên lý OOP → vị trí:**

| Nguyên lý | Thể hiện |
|---|---|
| Abstraction | `Employee` abstract; `roleAllowance()`/`getRole()` abstract; interface `Payable` |
| Encapsulation | field `private` + getter/setter có kiểm tra (`setStatus`, `setBonus`, `setWorkingDays`) |
| Inheritance | 4 lớp con kế thừa `Employee` |
| Polymorphism | `calculateSalary()` gọi `roleAllowance()` đa hình; duyệt `List<Employee>` |

## 5. Flowchart

Các sơ đồ luồng cho 4 chức năng phức tạp nhất (Load, Add, Payroll, Update) được trình
bày trong tệp `01_Diagrams.md`.

## 6. Conclusion

- **Đã đạt:** 9/9 chức năng chạy đúng; thiết kế kế thừa + đa hình thực sự (không if-else
  theo role); payroll chỉ tính nhân viên `active`; load không crash với dòng lỗi và báo
  cáo số dòng bị bỏ; validate đầy đủ ràng buộc của đề.
- **Hạn chế:** tra cứu theo ID hiện duyệt `List` O(n); chưa phân tách chức năng đổi role
  ra phương thức riêng; chưa có unit test tự động.
- **Hướng mở rộng:** dùng `HashMap<String,Employee>` để tra cứu O(1); thêm sắp xếp đa
  tiêu chí bằng `Comparator`; xuất báo cáo CSV; thêm vai trò mới (vd Designer) chỉ tốn
  một lớp con nhờ thiết kế Factory.
- **Bài học:** giá trị nằm ở việc *hiểu vì sao* thiết kế kế thừa và *tự định nghĩa* công
  thức lương có giải thích, thay vì chấp nhận output AI một cách thụ động.

---

## Phụ lục — Hướng dẫn biên dịch & chạy

```bash
# Biên dịch (từ thư mục EmployeePayrollManagementSystem)
javac -d out -encoding UTF-8 (Get-ChildItem -Recurse -Filter *.java src).FullName

# Chạy
java -cp out main.Main
```

Trong IntelliJ: mở project, chọn JDK 1.8, Run `main/Main.java`. File `employees.txt`
nằm cùng thư mục project (thư mục làm việc khi chạy).
