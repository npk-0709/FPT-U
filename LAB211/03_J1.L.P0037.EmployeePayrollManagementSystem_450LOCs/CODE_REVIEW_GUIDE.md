# Tài liệu Giải thích Code & Chuẩn bị Review

## Employee Payroll Management System (J1.L.P0037)

> Tài liệu này giải thích **toàn bộ kiến trúc, các class và method** của dự án, phục vụ cho buổi review.
> Phần trọng tâm (mục 9) đi sâu vào tính năng mới `MealAllowanceManager` **(Quản lý Phụ cấp ăn)**.
>
> Lưu ý: Trong thư mục có 2 bản (`EmployeePayrollManagementSystem` và `Lab211_Su26_..._updated/EmployeePayrollManagementSystemNetbean`). **Source code Java của 2 bản giống hệt nhau** (đã đối chiếu bằng hash), nên tài liệu mô tả chung cho cả hai.

---

## 1. Tổng quan dự án

Đây là ứng dụng **Console (CLI) viết bằng Java thuần** mô phỏng hệ thống quản lý lương nhân viên, gồm các nghiệp vụ:

- Đăng nhập & phân quyền theo vai trò (Admin / Staff).
- CRUD nhân viên (thêm/sửa/xóa/tìm kiếm).
- Tính bảng lương hàng tháng theo từng vai trò công việc (Developer/Tester/Manager/HR).
- Lưu/đọc dữ liệu ra file văn bản (`employees.txt`, `meal_allowances.txt`).
- **(Mới) Quản lý phụ cấp ăn (Meal Allowance)** – CRUD + tính tiền phụ cấp theo số ngày công.

Điểm nhấn kỹ thuật: áp dụng đầy đủ **4 tính chất OOP**, kèm **Interface**, **Abstract class**, **Factory Pattern** và **phân quyền dạng RBAC**.

---



## 2. Cấu trúc thư mục & kiến trúc phân lớp

```
EmployeePayrollManagementSystem/
└── src/
    ├── main/        → Điểm khởi động, vòng lặp menu
    │   └── Main.java
    ├── model/       → Tầng dữ liệu (POJO/Entity + interface)
    │   ├── Payable.java         (interface)
    │   ├── Employee.java        (abstract)
    │   ├── Developer.java / Tester.java / Manager.java / HR.java
    │   ├── Account.java         (abstract)
    │   ├── Admin.java / Staff.java
    │   └── MealAllowance.java   ← entity của tính năng mới
    ├── factory/     → Khởi tạo đối tượng
    │   └── EmployeeFactory.java
    ├── business/    → Tầng nghiệp vụ (xử lý logic + I/O)
    │   ├── AuthService.java
    │   ├── EmployeeManager.java
    │   └── MealAllowanceManager.java   ← nghiệp vụ tính năng mới
    └── tools/       → Tiện ích dùng chung
        ├── Inputter.java        (đọc & kiểm tra input)
        └── Validator.java       (quy tắc & regex hợp lệ)
```

Kiến trúc tách lớp rõ ràng theo trách nhiệm (separation of concerns):


| Tầng       | Vai trò                            | Phụ thuộc                           |
| ---------- | ---------------------------------- | ----------------------------------- |
| `main`     | Điều phối luồng, hiển thị menu     | gọi `business` + `tools`            |
| `business` | Logic nghiệp vụ, đọc/ghi file      | dùng `model`, `factory`, `tools`    |
| `factory`  | Tạo đối tượng `Employee` đúng kiểu | tạo ra `model`                      |
| `model`    | Định nghĩa thực thể & công thức    | độc lập (không phụ thuộc tầng trên) |
| `tools`    | Nhập liệu, kiểm tra hợp lệ         | độc lập                             |


---



## 3. Luồng chạy chương trình — `Main.java`

```java
Locale.setDefault(Locale.US);                 // đảm bảo định dạng số dùng dấu chấm
AuthService authService = new AuthService();
Account currentUser = authService.login();    // bắt buộc đăng nhập trước
```

Các bước:

1. **Đăng nhập** qua `AuthService.login()`. Nếu sai quá 3 lần → in thông báo và thoát.
2. Khởi tạo 2 manager:
  - `EmployeeManager manager = new EmployeeManager("employees.txt")`
  - `MealAllowanceManager mealManager = new MealAllowanceManager("meal_allowances.txt", manager)` — *truyền chính* `manager` *vào để kiểm tra nhân viên có tồn tại không.*
3. **Vòng lặp menu** (`do…while choice != 10`):
  - Hiển thị menu (kèm nhãn `[locked]` cho chức năng người dùng không có quyền).
  - Nhập lựa chọn 1–10 (validate qua `Inputter.inputInt`).
  - **Chốt chặn phân quyền**: `if (!currentUser.canAccess(choice))` → từ chối nếu không có quyền.
  - `switch` điều hướng đến method tương ứng.

Bảng ánh xạ menu → chức năng:


| Lựa chọn | Chức năng                | Method gọi                    |
| -------- | ------------------------ | ----------------------------- |
| 1        | Load dữ liệu nhân viên   | `manager.load()`              |
| 2        | Thêm nhân viên           | `manager.add()`               |
| 3        | Cập nhật nhân viên       | `manager.update()`            |
| 4        | Xóa nhân viên            | `manager.remove()`            |
| 5        | Tìm kiếm theo thuộc tính | `manager.searchByAttribute()` |
| 6        | Tính bảng lương tháng    | `manager.monthlyPayroll()`    |
| 7        | Hiển thị danh sách       | `manager.display()`           |
| 8        | Lưu file                 | `manager.save()`              |
| **9**    | **Quản lý phụ cấp ăn**   | `mealManager.run()`           |
| 10       | Thoát                    | `manager.quit()`              |


---



## 4. Phân quyền — Authentication & Authorization (RBAC)



### `AuthService.java`

- Khởi tạo sẵn 2 tài khoản trong bộ nhớ:
  - `admin / admin123` (vai trò Admin)
  - `staff / staff123` (vai trò Staff)
- `login()`: cho tối đa **3 lần** thử (`MAX_ATTEMPTS = 3`). Mỗi lần gọi `authenticate()` so khớp username/password.
- `authenticate()`: duyệt danh sách account, gọi `account.matches(username, password)`.



### Ma trận quyền (định nghĩa trong `getAllowedFeatures()`)


| Chức năng | 1 Load | 2 Add | 3 Update | 4 Remove | 5 Search | 6 Payroll | 7 Display | 8 Save | 9 Meal | 10 Quit |
| --------- | ------ | ----- | -------- | -------- | -------- | --------- | --------- | ------ | ------ | ------- |
| **Admin** | ✅      | ✅     | ✅        | ✅        | ✅        | ✅         | ✅         | ✅      | ✅      | ✅       |
| **Staff** | ✅      | ❌     | ❌        | ❌        | ✅        | ✅         | ✅         | ❌      | ✅      | ✅       |


→ Staff **không được** thêm/sửa/xóa/lưu nhân viên, nhưng **được** xem, tìm kiếm, tính lương và dùng phụ cấp ăn.

> **Điểm cần lưu ý khi review:** Phân quyền chỉ áp ở **menu chính**. Khi vào menu con phụ cấp (chức năng 9), Staff lại có toàn quyền CRUD phụ cấp. Đây là chủ ý thiết kế hay là điểm cần siết thêm — nên chuẩn bị giải thích.

---



## 5. Tầng Model



### 5.1. `Payable` (interface) — Abstraction

```java
public interface Payable {
    double calculateSalary();
}
```

Hợg". Cho phép xử lýp đồng "có thể tính lươn đa hình mọi nhân viên qua kiểu chung.

### 5.2. `Employee` (abstract class) — lớp cha của mọi nhân viên

- Triển khai `Payable`.
- Thuộc tính: `id, name, baseSalary, workingDays, bonus, status`.
- Hằng `STANDARD_WORKING_DAYS = 26`.
- **2 method trừu tượng** buộc lớp con định nghĩa:
  - `getRole()` — tên vai trò.
  - `roleAllowance()` — phụ cấp theo vai trò.
- **Công thức lương chung** (Template Method):
  ```java
  double salaryByDays = baseSalary / STANDARD_WORKING_DAYS * workingDays;
  return salaryByDays + bonus + roleAllowance();
  ```
- `isActive()`: true nếu `status == "active"`.
- Getter/Setter có **kiểm tra hợp lệ** (vd `setWorkingDays` chỉ nhận 0–26, `setBaseSalary` chỉ nhận > 0).
- `toDataLine()`: xuất 1 dòng CSV để lưu file.
- `toString()`: định dạng 1 dòng bảng để hiển thị.
- `equals()/hashCode()`: so sánh theo `id` (không phân biệt hoa thường) → đảm bảo không trùng ID.



### 5.3. Bốn lớp con — Inheritance + Polymorphism

Mỗi lớp chỉ override `getRole()` và `roleAllowance()`:


| Lớp         | `roleAllowance()`         | Diễn giải                 |
| ----------- | ------------------------- | ------------------------- |
| `Developer` | `0.10 * baseSalary`       | phụ cấp 10% lương cơ bản  |
| `Tester`    | `0.05 * baseSalary`       | phụ cấp 5%                |
| `Manager`   | `0.20 * baseSalary + 200` | phụ cấp 20% + 200 cố định |
| `HR`        | `0.08 * baseSalary`       | phụ cấp 8%                |


→ Cùng lời gọi `calculateSalary()` nhưng kết quả khác nhau tùy đối tượng thực = **đa hình (polymorphism)**.

### 5.4. `Account` (abstract) / `Admin` / `Staff`

- `Account`: giữ `username/password`; có `matches()` để xác thực; **2 method trừu tượng** `getRole()` và `getAllowedFeatures()`; `canAccess(feature)` kiểm tra quyền.
- `Admin`/`Staff`: override trả về vai trò và `Set<Integer>` các chức năng được phép (xem mục 4).



### 5.5. `MealAllowance` (entity tính năng mới) ⭐

```java
public static final double UNIT_PRICE = 30000.0;  // đơn giá 30.000đ / ngày
public static final int MAX_DAYS = 26;            // số ngày tối đa
```


| Thành phần              | Mô tả                                                             |
| ----------------------- | ----------------------------------------------------------------- |
| `id`                    | Mã phụ cấp, định dạng `ML-xxxx` (vd `ML-0001`)                    |
| `employeeId`            | Mã nhân viên hưởng phụ cấp (`Exxx`)                               |
| `month`                 | Tháng áp dụng, định dạng `MM/yyyy`                                |
| `days`                  | Số ngày ăn (0–26)                                                 |
| `getAmount()`           | `days * UNIT_PRICE` → số tiền phụ cấp                             |
| `setDays(int)`          | Chỉ cập nhật khi `0 ≤ days ≤ 26` (bảo vệ dữ liệu)                 |
| `sameKey(empId, month)` | True nếu trùng cặp (nhân viên, tháng) — dùng chặn trùng nghiệp vụ |
| `toDataLine()`          | Xuất CSV: `id, employeeId, month, days`                           |
| `toString()`            | Định dạng dòng bảng hiển thị (kèm Amount)                         |
| `equals()/hashCode()`   | **So theo cặp** `(employeeId, month)` (không theo `id`)           |


> **Điểm review quan trọng:** `equals/hashCode` của `MealAllowance` dựa trên `(employeeId, month)` chứ không phải `id`. Nghĩa là về mặt nghiệp vụ "mỗi nhân viên chỉ có 1 phụ cấp/tháng". Logic chặn trùng (`existsKey`) đã đảm bảo điều này nên không phát sinh mâu thuẫn.

---



## 6. `EmployeeFactory` — Factory Pattern

```java
switch (role.trim().toLowerCase()) {
    case "developer": return new Developer(...);
    case "tester":    return new Tester(...);
    case "manager":   return new Manager(...);
    case "hr":        return new HR(...);
    default: throw new IllegalArgumentException("Unknown role: " + role);
}
```

- Class `final`, constructor `private` → chỉ dùng method tĩnh, không cho khởi tạo.
- Tập trung logic "chọn lớp con theo role" vào **một chỗ duy nhất** → dễ bảo trì, tránh `if/else` rải rác.
- Ném `IllegalArgumentException` khi role null/không hợp lệ (được bắt ở tầng load).

---



## 7. Tầng Business



### 7.1. `EmployeeManager.java`

Quản lý danh sách `List<Employee>` + cờ `dirty` (đánh dấu có thay đổi chưa lưu).


| Method                                  | Chức năng                                                           |
| --------------------------------------- | ------------------------------------------------------------------- |
| `load()`                                | Đọc `employees.txt`, parse từng dòng, đếm số dòng hợp lệ/lỗi        |
| `parseAndAdd(line)`                     | Tách 7 trường, validate đầy đủ + chống trùng ID rồi tạo qua Factory |
| `add()`                                 | Nhập liệu có kiểm tra, chống trùng ID, thêm nhân viên               |
| `update()`                              | Sửa theo ID; **Enter để giữ giá trị cũ**; validate từng trường      |
| `remove()`                              | Xóa theo ID, có xác nhận Y/N                                        |
| `searchByAttribute()`                   | Tìm theo ID/Name/Role/Status (chứa từ khóa)                         |
| `monthlyPayroll()`                      | Tính & in bảng lương **chỉ nhân viên active**, tính tổng            |
| `display()`                             | In toàn bộ danh sách dạng bảng                                      |
| `save()`                                | Ghi danh sách ra file                                               |
| `quit()`                                | Nếu còn thay đổi chưa lưu → hỏi lưu trước khi thoát                 |
| `exists(id)`                            | Kiểm tra tồn tại (public — để `MealAllowanceManager` dùng)          |
| `findById/isEmpty/printTable/printLine` | Tiện ích nội bộ                                                     |


**Đặc điểm hay:** `load()` có khả năng **bỏ qua dòng lỗi** thay vì crash (xem mục 8 về file dữ liệu chứa dòng lỗi cố ý).

### 7.2. `AuthService.java` — đã mô tả ở mục 4.



### 7.3. `MealAllowanceManager.java` ⭐ → xem chi tiết mục 9.

---



## 8. Tầng Tools



### `Inputter.java` — Nhập liệu an toàn (loop tới khi hợp lệ)

Tất cả method dùng chung 1 `Scanner` tĩnh:


| Method                                           | Mục đích                                   |
| ------------------------------------------------ | ------------------------------------------ |
| `inputInt(prompt, min, max)`                     | Đọc số nguyên trong khoảng                 |
| `inputNonEmpty(prompt)`                          | Chuỗi không rỗng                           |
| `inputByRegex(prompt, regex, err)`               | Chuỗi khớp regex (ID, tháng, Y/N…)         |
| `inputInSet(prompt, allowed)`                    | Giá trị thuộc tập cho trước (role, status) |
| `inputPositiveDouble` / `inputNonNegativeDouble` | Số thực > 0 / ≥ 0                          |
| `inputOptional(prompt)`                          | Cho phép bỏ trống (dùng khi update)        |
| `confirmYesNo(prompt)`                           | Xác nhận Y/N                               |




### `Validator.java` — Quy tắc hợp lệ tập trung

- Regex: `ID_REGEX = "E\\d{3}"`, `MEAL_ALLOWANCE_ID_REGEX = "ML-\\d{4}"`, `MONTH_REGEX = "(0[1-9]|1[0-2])/\\d{4}"`.
- Tập giá trị: `ROLES = [Developer, Tester, Manager, HR]`, `STATUSES = [active, inactive]`.
- Các hàm `isEmployeeId, isRole, isStatus, isPositive, isWorkingDays, isMealAllowanceId, isMonth, isMealDays, isNonNegative, isNotEmpty`.
- Dùng `Pattern` biên dịch sẵn → hiệu năng tốt khi load file lớn.



### Định dạng file dữ liệu

- `employees.txt`: `id, name, role, baseSalary, workingDays, bonus, status` (7 trường).
- `meal_allowances.txt`: `id, employeeId, month, days` (4 trường).
- Tách trường bằng regex `"\\s*,\\s*"` (tự bỏ khoảng trắng quanh dấu phẩy).

> **File** `employees.txt` **hiện chứa vài dòng lỗi cố ý** để minh họa khả năng *load bỏ qua dòng sai*:
>
> - Dòng `E003 ... Developer; 1300, 21,, 250` → dùng `;` và thiếu trường → bị skip.
> - `E004` và `E009` xuất hiện 2 lần → bản trùng ID bị skip.
> Đây là dữ liệu test tốt để demo `parseAndAdd` đếm "skipped invalid line(s)".

---



## 9. ⭐ TRỌNG TÂM: `MealAllowanceManager` (Quản lý Phụ cấp ăn)

Đây là tính năng mới, là phần dễ bị hỏi sâu nhất khi review.

### 9.1. Thiết kế tổng thể

```java
private final List<MealAllowance> list = new ArrayList<>();
private final String filePath;
private final EmployeeManager employeeManager;   // ← phụ thuộc để kiểm tra nhân viên
private boolean dirty = false;
```

Điểm thiết kế quan trọng: `MealAllowanceManager` **giữ một tham chiếu tới** `EmployeeManager` (được tiêm qua constructor từ `Main`). Nhờ đó nó kiểm tra được "nhân viên có tồn tại không" trước khi tạo phụ cấp → đảm bảo **toàn vẹn tham chiếu (referential integrity)**.

### 9.2. `run()` — vòng lặp menu con (1–6)

Tự động `load()` khi mở, sau đó lặp menu:


| Lựa chọn | Method      | Ý nghĩa                                        |
| -------- | ----------- | ---------------------------------------------- |
| 1        | `add()`     | Tạo phụ cấp                                    |
| 2        | `display()` | Xem danh sách + số tiền                        |
| 3        | `update()`  | Sửa số ngày → tính lại tiền                    |
| 4        | `remove()`  | Xóa theo ID                                    |
| 5        | `save()`    | Lưu ra file                                    |
| 6        | `exit()`    | Quay lại menu chính (hỏi lưu nếu còn thay đổi) |




### 9.3. `load()` — đọc file + thống kê

- Xóa danh sách cũ, đọc từng dòng, bỏ qua dòng trống.
- Mỗi dòng đưa vào `parseAndAdd`; đếm `ok` / `bad`.
- Nếu file chưa tồn tại → in *"No meal allowance file yet (will be created on save)."* (không coi là lỗi — vì lần đầu chạy chưa có file).
- Đặt `dirty = false` sau khi load.



### 9.4. `parseAndAdd(line)` — parse 1 dòng từ file (validate 2 lớp)

```java
String[] p = line.split("\\s*,\\s*");
if (p.length != 4) return false;           // phải đủ 4 trường
int days = Integer.parseInt(p[3]);          // ép kiểu (bắt NumberFormatException)
boolean valid = Validator.isMealAllowanceId(id)
        && Validator.isEmployeeId(employeeId)
        && Validator.isMonth(month)
        && Validator.isMealDays(days)
        && !existsId(id)                    // chống trùng mã phụ cấp
        && !existsKey(employeeId, month);   // chống trùng (nhân viên, tháng)
```

→ Dòng nào sai bất kỳ điều kiện nào sẽ bị **bỏ qua an toàn** (trả `false`), không làm sập chương trình.

### 9.5. `add()` — tạo phụ cấp (nhập tay) — luồng nghiệp vụ chính

Trình tự kiểm tra (mỗi bước fail thì dừng sớm, in lý do):

1. Nhập **ID** theo regex `ML-xxxx` → nếu **trùng ID** ⇒ báo lỗi, dừng.
2. Nhập **Employee ID** theo regex `Exxx` → **gọi** `employeeManager.exists(employeeId)`; nếu nhân viên **không tồn tại** ⇒ báo lỗi, dừng.
3. Nhập **Month** `MM/yyyy` → nếu **(nhân viên, tháng) đã có phụ cấp** (`existsKey`) ⇒ báo lỗi, dừng.
4. Nhập **Days** 0–26 (`Inputter.inputInt`).
5. Tạo `MealAllowance`, set `dirty = true`, in số tiền = `days * 30000`.

→ Đây là method thể hiện rõ nhất 3 ràng buộc nghiệp vụ: **ID duy nhất**, **nhân viên phải tồn tại**, **mỗi nhân viên 1 phụ cấp/tháng**.

### 9.6. `update()` — chỉ sửa số ngày

- Tìm theo ID; nếu không thấy ⇒ báo lỗi.
- Hiển thị ngày & tiền hiện tại, nhập số ngày mới (0–26), gọi `m.setDays()`, in lại số tiền mới.
- **Cố ý không cho sửa** `employeeId`**/**`month` vì đó là khóa nghiệp vụ (đổi sẽ phá ràng buộc unique).



### 9.7. `remove()` — xóa có xác nhận

- Tìm theo ID; hỏi `confirmYesNo`; nếu đồng ý mới `list.remove(m)` và set `dirty`.



### 9.8. `save()` / `exit()`

- `save()`: ghi từng `toDataLine()` ra file, in số bản ghi, reset `dirty`.
- `exit()`: nếu `dirty` còn true → hỏi có lưu không trước khi rời menu.



### 9.9. Các helper nội bộ


| Method                    | Vai trò                                          |
| ------------------------- | ------------------------------------------------ |
| `existsId(id)`            | Có tồn tại mã phụ cấp? (qua `findById`)          |
| `existsKey(empId, month)` | Có trùng cặp (nhân viên, tháng)? (qua `sameKey`) |
| `findById(id)`            | Tìm bản ghi theo ID (không phân biệt hoa thường) |
| `isEmpty()`               | Báo "danh sách rỗng" và trả true                 |
| `printTable/printLine`    | In bảng định dạng cột                            |




### 9.10. Sơ đồ luồng `add()` (tóm tắt)

```
Nhập ID (ML-xxxx) ──trùng?──► dừng + báo lỗi
        │ ok
Nhập EmpID (Exxx) ──không tồn tại trong EmployeeManager?──► dừng + báo lỗi
        │ ok
Nhập Month (MM/yyyy) ──đã có phụ cấp tháng này?──► dừng + báo lỗi
        │ ok
Nhập Days (0..26) → tạo MealAllowance → dirty=true → in "Amount = days*30000"
```

---



## 10. Bảng tổng hợp công thức

**Lương tháng của 1 nhân viên:**

```
salary = baseSalary / 26 * workingDays + bonus + roleAllowance
```

- Developer: `+10% base` · Tester: `+5% base` · Manager: `+20% base + 200` · HR: `+8% base`
- Bảng lương tháng (`monthlyPayroll`) **chỉ cộng nhân viên có status = active**.

**Phụ cấp ăn:**

```
amount = days * 30000   (days ∈ [0, 26])
```

---



## 11. Nguyên lý OOP & Design Pattern áp dụng


| Nguyên lý                           | Thể hiện trong code                                                               |
| ----------------------------------- | --------------------------------------------------------------------------------- |
| **Encapsulation**                   | Thuộc tính `private`, truy cập qua getter/setter có kiểm tra hợp lệ               |
| **Abstraction**                     | Interface `Payable`; abstract class `Employee`, `Account`                         |
| **Inheritance**                     | `Developer/Tester/Manager/HR` kế thừa `Employee`; `Admin/Staff` kế thừa `Account` |
| **Polymorphism**                    | `calculateSalary()` & `roleAllowance()` cho kết quả khác nhau theo lớp con        |
| **Factory Pattern**                 | `EmployeeFactory.create(...)` tạo đúng lớp con theo role                          |
| **Single Responsibility**           | Tách `model` / `business` / `tools` / `factory` rõ ràng                           |
| **RBAC (phân quyền)**               | `Account.getAllowedFeatures()` + `canAccess()`                                    |
| **Dependency Injection (thủ công)** | `MealAllowanceManager` nhận `EmployeeManager` qua constructor                     |


---



## 12. Điểm mạnh & Lưu ý khi review

**Điểm mạnh:**

- Kiến trúc phân lớp sạch, dễ mở rộng (thêm role mới chỉ cần 1 lớp con + 1 case trong Factory).
- Validate đầu vào chặt chẽ ở **2 lớp**: khi nhập tay (`Inputter`) và khi đọc file (`parseAndAdd`).
- Cơ chế `dirty` chống mất dữ liệu (nhắc lưu trước khi thoát).
- Tính năng phụ cấp tích hợp tốt với module nhân viên (kiểm tra tồn tại nhân viên).

**Lưu ý nên chuẩn bị giải thích:**

1. Phân quyền chỉ áp ở menu chính; trong menu phụ cấp, Staff có toàn quyền CRUD.
2. `equals/hashCode` của `MealAllowance` dựa trên `(employeeId, month)` chứ không phải `id`.
3. Mật khẩu để **plaintext** trong code (chấp nhận được với bài tập, nhưng nên nêu hướng cải thiện: mã hóa/hash).
4. `employees.txt` chứa dòng lỗi cố ý để demo cơ chế skip — không phải lỗi dữ liệu thật.
5. Nếu xóa nhân viên ở module Employee, các phụ cấp cũ của họ trong `meal_allowances.txt` **không tự bị xóa** (không có ràng buộc cascade) — điểm có thể được hỏi.

---



## 13. Câu hỏi review thường gặp (Q&A gợi ý)

**Q: Vì sao dùng Factory thay vì** `new` **trực tiếp?**
A: Tập trung logic chọn lớp con theo role vào một nơi, tránh lặp `if/else`, dễ bảo trì và mở rộng.

**Q:** `MealAllowanceManager` **làm sao biết nhân viên có tồn tại?**
A: Nó được tiêm `EmployeeManager` qua constructor và gọi `employeeManager.exists(employeeId)` trong `add()`.

**Q: Làm sao đảm bảo mỗi nhân viên chỉ có 1 phụ cấp/tháng?**
A: Qua `existsKey(employeeId, month)` dùng `MealAllowance.sameKey()`; được kiểm tra cả khi nhập tay lẫn khi load file.

**Q: Số tiền phụ cấp tính thế nào?**
A: `amount = days * UNIT_PRICE` với `UNIT_PRICE = 30.000đ`, `days` giới hạn 0–26.

**Q: Chương trình xử lý file dữ liệu sai định dạng ra sao?**
A: `parseAndAdd` trả `false` cho dòng sai và đếm vào "skipped", chương trình vẫn chạy bình thường (không crash).

**Q: Cờ** `dirty` **để làm gì?**
A: Đánh dấu có thay đổi chưa lưu; khi thoát menu sẽ hỏi người dùng có muốn lưu không, tránh mất dữ liệu.

---

## 14. ⭐ CHUẨN BỊ VẤN ĐÁP ĐỀ 35 — MealAllowance (đối chiếu AI Audit Log)

> Mục này bám sát **đề vấn đáp ĐỀ 35**. Yêu cầu cốt lõi cần chứng minh:
> **(1)** `amount` là giá trị **dẫn xuất** `= days * đơn giá` (KHÔNG lưu cứng) ·
> **(2)** `days ∈ [0..26]` · **(3)** mỗi `(employee, month)` chỉ **1 bản ghi** ·
> **(4)** nêu được **ít nhất 1 hallucination đã sửa**.

### 14.1. Đối chiếu 4 câu hỏi vấn đáp với code (đáp án + dẫn chứng)

**① `amount` tính thế nào?**
→ `amount` là **giá trị dẫn xuất**, tính tại chỗ từ `days`, **không** có field lưu cứng, **không** ghi xuống file.

```java
// model/MealAllowance.java
public static final double UNIT_PRICE = 30000.0;   // đơn giá 30.000đ / ngày

public double getAmount() {        // dẫn xuất, không lưu field
    return days * UNIT_PRICE;
}

public String toDataLine() {       // file chỉ lưu days, KHÔNG lưu amount
    return String.format("%s, %s, %s, %d", id, employeeId, month, days);
}
```
Bằng chứng dữ liệu: `meal_allowances.txt` chỉ có 4 cột `id, employeeId, month, days` → không có cột amount.

**② `days` ngoài `[0..26]` thì sao?** → Bị chặn ở **3 tầng** (defense-in-depth):

| Tầng | Cơ chế | Vị trí |
|------|--------|--------|
| Nhập tay | `Inputter.inputInt("Days (0-26): ", 0, 26)` lặp lại tới khi hợp lệ | `MealAllowanceManager.add()/update()` |
| Đọc file | `Validator.isMealDays(days)` = `0 ≤ days ≤ 26`; dòng sai bị bỏ qua (đếm "skipped") | `MealAllowanceManager.parseAndAdd()` |
| Tầng model | `setDays()`/constructor **ném `IllegalArgumentException`** nếu ngoài khoảng | `MealAllowance` |

```java
// model/MealAllowance.java  (đã củng cố ở tầng model)
public void setDays(int days) {
    if (days < 0 || days > MAX_DAYS) {                 // MAX_DAYS = 26
        throw new IllegalArgumentException(
                "Days must be within [0, " + MAX_DAYS + "], got: " + days);
    }
    this.days = days;
}
```

**③ Trùng `(employee, tháng)` thì sao?** → Bị **từ chối**; mỗi nhân viên chỉ 1 phụ cấp/tháng.

```java
// business/MealAllowanceManager.java
private boolean existsKey(String employeeId, String month) {
    for (MealAllowance m : list) {
        if (m.sameKey(employeeId, month)) return true;   // so cặp (emp, month)
    }
    return false;
}
```
Được kiểm tra **cả khi nhập tay** (`add()`) **lẫn khi load file** (`parseAndAdd()`). Ngoài ra `equals()/hashCode()` của `MealAllowance` cũng định danh theo cặp `(employeeId, month)`.

**④ AI có lưu `amount` cứng không?** → **KHÔNG.** Đây chính là hallucination điển hình đã được phòng tránh/sửa (xem 14.2). `amount` luôn được tính qua `getAmount()`; file dữ liệu không có cột amount.

### 14.2. AI Audit Log — 3 core prompt + hallucination (dán vào file Excel)

| # | Loại prompt | Nội dung prompt (rút gọn) | Kết quả AI | Quyết định của SV |
|---|-------------|---------------------------|------------|-------------------|
| 1 | **Quyết định** | "amount nên là field lưu sẵn hay tính từ days?" | AI ban đầu gợi ý thêm `double amount` và ghi vào file | **Chọn dẫn xuất**: dùng `getAmount()`, bỏ field amount → tránh dữ liệu lệch |
| 2 | **Kiểm chứng** | "Ràng buộc hợp lệ của days là gì?" | AI đề xuất `days ≥ 0` | **Siết lại**: `0 ≤ days ≤ 26` (đúng số ngày công tối đa) ở cả input/validator/model |
| 3 | **Giải thuật** | "Công thức tính tiền phụ cấp?" | `amount = days * đơn giá` | Chấp nhận; đặt `UNIT_PRICE = 30.000` là hằng số |

**Hallucination đã bắt & sửa (bắt buộc nêu khi vấn đáp):**

> **Hiện tượng:** AI từng sinh code lưu `amount` thành **field cứng** trong `MealAllowance` và ghi cả `amount` xuống `meal_allowances.txt` (`toDataLine` có 5 cột).
> **Vì sao sai:** `amount` phụ thuộc `days`; nếu sửa `days` mà quên cập nhật `amount`, hoặc sửa tay file, dữ liệu sẽ **mâu thuẫn (out-of-sync)**.
> **Cách sửa:** Bỏ field `amount`, chỉ giữ `getAmount() = days * UNIT_PRICE`; `toDataLine()` chỉ lưu `days`. → `amount` luôn nhất quán.

### 14.3. Các cải tiến code vừa thực hiện (để câu trả lời "chắc" hơn)

3 thay đổi nhỏ, **không đổi hành vi luồng chính** (đầu vào luôn hợp lệ trước khi tới), chỉ làm bất biến vững hơn:

| File | Trước | Sau | Lý do |
|------|-------|-----|-------|
| `MealAllowance` | constructor gán thẳng `this.days = days` | gọi `setDays(days)` | Áp ràng buộc `[0..26]` ngay khi tạo object |
| `MealAllowance` | `setDays` **im lặng** bỏ qua giá trị sai | **ném `IllegalArgumentException`** | Trả lời câu ② thuyết phục: model tự bảo vệ, không "nuốt" lỗi |
| `MealAllowanceManager` | `catch (NumberFormatException)` | `catch (IllegalArgumentException)` | Bao trùm cả lỗi parse lẫn lỗi ràng buộc days khi load (giống `EmployeeManager`) |

### 14.4. Script đáp án ngắn (học thuộc để trả lời nhanh)

1. **amount:** "Em không lưu amount; nó là **giá trị dẫn xuất** `getAmount() = days * 30.000`. File chỉ lưu `days`, nên dữ liệu không bao giờ lệch."
2. **days ngoài [0..26]:** "Bị chặn ở **3 tầng**: `Inputter` ép nhập trong [0..26]; khi load file `Validator.isMealDays` lọc bỏ dòng sai; và **model `setDays` ném `IllegalArgumentException`**."
3. **trùng (emp, tháng):** "Em coi `(employeeId, month)` là **khóa nghiệp vụ**. `existsKey()` chặn trùng cả lúc thêm tay lẫn lúc load; `equals/hashCode` cũng theo cặp này → mỗi nhân viên 1 phụ cấp/tháng."
4. **AI lưu amount cứng?:** "Không. Em đã **bắt hallucination** đó: AI từng đề xuất field `amount` lưu sẵn; em sửa thành dẫn xuất để tránh dữ liệu out-of-sync."