# Sơ đồ thiết kế — Employee Payroll Management System (J1.L.P0037)

> Các sơ đồ viết bằng **Mermaid**. Có thể xem trực tiếp trên GitHub/IntelliJ (plugin Mermaid)
> hoặc dán vào https://mermaid.live để xuất ra ảnh PNG/SVG đính kèm báo cáo.

---

## 1. Use Case Diagram

```mermaid
flowchart LR
    User([Payroll Officer])

    subgraph System[Employee Payroll Management System]
        UC1(["1. Load data from file"])
        UC2(["2. Add employee"])
        UC3(["3. Update employee"])
        UC4(["4. Remove employee"])
        UC5(["5. Search by attribute"])
        UC6(["6. Calculate monthly payroll"])
        UC7(["7. Display employee list"])
        UC8(["8. Save data to file"])
        UC9(["9. Quit (confirm save)"])
    end

    User --- UC1
    User --- UC2
    User --- UC3
    User --- UC4
    User --- UC5
    User --- UC6
    User --- UC7
    User --- UC8
    User --- UC9
```

---

## 2. Class Diagram

```mermaid
classDiagram
    class Payable {
        <<interface>>
        +calculateSalary() double
    }

    class Employee {
        <<abstract>>
        -String id
        -String name
        -double baseSalary
        -int workingDays
        -double bonus
        -String status
        +getRole()* String
        +roleAllowance()* double
        +calculateSalary() double
        +isActive() boolean
        +toDataLine() String
        +toString() String
        +equals(Object) boolean
    }

    class Developer {
        +getRole() String
        +roleAllowance() double
    }
    class Tester {
        +getRole() String
        +roleAllowance() double
    }
    class Manager {
        +getRole() String
        +roleAllowance() double
    }
    class HR {
        +getRole() String
        +roleAllowance() double
    }

    class EmployeeFactory {
        +create(...)$ Employee
    }

    class EmployeeManager {
        -List~Employee~ list
        -String filePath
        -boolean dirty
        +load()
        +add()
        +update()
        +remove()
        +searchByAttribute()
        +monthlyPayroll()
        +display()
        +save()
        +quit()
    }

    class Validator {
        +ID_REGEX$ String
        +isEmployeeId(String)$ boolean
        +isRole(String)$ boolean
        +isStatus(String)$ boolean
    }

    class Inputter {
        +inputInt(...)$ int
        +inputByRegex(...)$ String
        +inputInSet(...)$ String
        +confirmYesNo(String)$ boolean
    }

    class Main {
        +main(String[])$ void
    }

    Payable <|.. Employee
    Employee <|-- Developer
    Employee <|-- Tester
    Employee <|-- Manager
    Employee <|-- HR
    EmployeeManager "1" o-- "*" Employee : has-a
    EmployeeFactory ..> Employee : creates
    EmployeeManager ..> EmployeeFactory : uses
    EmployeeManager ..> Validator : uses
    EmployeeManager ..> Inputter : uses
    Main ..> EmployeeManager : uses
```

---

## 3. Flowchart — Function 1: Load an toàn (xử lý dòng lỗi)

```mermaid
flowchart TD
    A([Start load]) --> B[ok = 0, bad = 0; clear list]
    B --> C{Còn dòng?}
    C -- Không --> Z[In: Loaded ok, skipped bad] --> END([End])
    C -- Có --> D[Đọc 1 dòng]
    D --> E{Dòng rỗng?}
    E -- Có --> C
    E -- Không --> F[split theo dấu phẩy]
    F --> G{Đủ 7 trường?}
    G -- Không --> H[bad++] --> C
    G -- Có --> I[parse baseSalary / workingDays / bonus]
    I --> J{Parse số OK?}
    J -- Lỗi NumberFormat --> H
    J -- OK --> K{Validate tất cả ràng buộc<br/>+ ID chưa trùng?}
    K -- Không --> H
    K -- Có --> L[EmployeeFactory.create role -> đúng lớp con]
    L --> M[list.add; ok++] --> C
```

---

## 4. Flowchart — Function 2: Add employee

```mermaid
flowchart TD
    A([Start add]) --> B[Nhập ID theo regex E + 3 số]
    B --> C{ID đã tồn tại?}
    C -- Có --> D[In 'already exists!'] --> END([Về menu])
    C -- Không --> E[Nhập name không rỗng]
    E --> F[Nhập role thuộc tập hợp lệ]
    F --> G[Nhập baseSalary > 0]
    G --> H[Nhập workingDays 0..26]
    H --> I[Nhập bonus >= 0]
    I --> J[Nhập status active/inactive]
    J --> K[Factory.create -> đúng lớp con]
    K --> L[list.add; dirty = true]
    L --> M[In 'added successfully'] --> END
```

---

## 5. Flowchart — Function 6: Tính lương tháng (chỉ active)

```mermaid
flowchart TD
    A([Start payroll]) --> B[total = 0; any = false]
    B --> C{Còn employee?}
    C -- Không --> G{any == true?}
    G -- Không --> H[In 'No active employee'] --> END([End])
    G -- Có --> I[In TOTAL active only] --> END
    C -- Có --> D{e.isActive?}
    D -- Không --> C
    D -- Có --> E[s = e.calculateSalary  // đa hình theo role]
    E --> F[In dòng; total += s; any = true] --> C
```

---

## 6. Flowchart — Function 3: Update (Enter giữ giá trị cũ, đổi role tạo lại lớp con)

```mermaid
flowchart TD
    A([Start update]) --> B{list rỗng?}
    B -- Có --> END([Về menu])
    B -- Không --> C[Nhập ID cần sửa]
    C --> D{Tìm thấy?}
    D -- Không --> E[In 'does not exist!'] --> END
    D -- Có --> F[Nhập role/base/days/bonus/status<br/>Enter = giữ cũ]
    F --> G[Validate từng trường;<br/>sai hoặc rỗng -> giữ giá trị cũ]
    G --> H[Factory.create với role mới<br/>-> tạo lại đúng lớp con]
    H --> I[list.set thay thế; dirty = true]
    I --> J[In 'Updated successfully'] --> END
```
