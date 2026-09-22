# Sơ đồ Class - Hệ thống Mountain Hiking

## Tổng quan cấu trúc

Hệ thống gồm **12 class/interface/enum**, chia thành 4 nhóm chính:

| Nhóm | Thành phần |
|------|-----------|
| **Person hierarchy** | `Person` (abstract) → `Student`, `Volunteer` |
| **Manager (Collection)** | `Students`, `Volunteers`, `Mountains` |
| **Data/Model** | `Mountain`, `Skill` (enum), `StatisticalInfo`, `Statistics` |
| **Utility** | `Acceptable` (interface), `Inputter`, `Main` |

---

## Class Diagram đầy đủ

```mermaid
classDiagram
    direction TB

    class Serializable {
        <<interface>>
    }

    class Comparable~T~ {
        <<interface>>
        +compareTo(T) int
    }

    %% ===== PERSON HIERARCHY =====
    class Person {
        <<abstract>>
        -long serialVersionUID = 1L$
        #String id
        #String name
        +Person()
        +Person(String id, String name)
        +getId() String
        +setId(String)
        +getName() String
        +setName(String)
        +getDisplayInfo() String*
    }

    class Student {
        -long serialVersionUID = 1L$
        +double DEFAULT_FEE = 6000000$
        +double DISCOUNT_RATE = 0.35$
        -String phone
        -String email
        -String mountainCode
        -double tuitionFee
        +Student()
        +Student(String, String, String, String, String, double)
        +getDisplayInfo() String
        +getPhone() String
        +setPhone(String)
        +getEmail() String
        +setEmail(String)
        +getMountainCode() String
        +setMountainCode(String)
        +getTuitionFee() double
        +setTuitionFee(double)
        +getCampusCode() String
        +calculateFee(String) double$
        +toString() String
        +toCsv() String
        +compareTo(Student) int
        +equals(Object) boolean
        +hashCode() int
    }

    class Volunteer {
        -long serialVersionUID = 2L$
        -Skill skill
        -int maxShiftsPerDay
        -int shiftsToday
        +Volunteer()
        +Volunteer(String, String, Skill, int)
        +getDisplayInfo() String
        +getSkill() Skill
        +setSkill(Skill)
        +getMaxShiftsPerDay() int
        +setMaxShiftsPerDay(int)
        +getShiftsToday() int
        +setShiftsToday(int)
        +assign() boolean
        +hasSkillFor(Skill) boolean
        +toString() String
        +toCsv() String
        +compareTo(Volunteer) int
        +equals(Object) boolean
        +hashCode() int
    }

    class Skill {
        <<enum>>
        MEDIC
        LOGISTIC
        GUIDE_ASSIST
        +showAll()$
        +getByIndex(int) Skill$
    }

    %% Inheritance
    Person --|> Serializable : implements
    Student --|> Person : extends
    Student --|> Comparable~Student~ : implements
    Volunteer --|> Person : extends
    Volunteer --|> Comparable~Volunteer~ : implements
    Volunteer --> Skill : uses

    %% ===== MANAGERS =====
    class Students {
        <<ArrayList~Student~>>
        -String pathFile
        -boolean isSaved
        +Students()
        +Students(String)
        +isSaved() boolean
        +markUnsaved()
        +add(Student) boolean
        +update(Student) boolean
        +delete(String) boolean
        +searchById(String) Student
        +searchByName(String) List~Student~
        +filterByCampusCode(String) List~Student~
        +showAll()
        +showAll(List~Student~)
        +statisticalizeByMountainPeak(Mountains)
        +readFromFile()
        +saveToFile() boolean
        +getPathFile() String
        +getCsvPathFile() String
    }

    class Volunteers {
        <<ArrayList~Volunteer~>>
        -String pathFile
        -boolean isSaved
        +Volunteers()
        +Volunteers(String)
        +isSaved() boolean
        +markUnsaved()
        +add(Volunteer) boolean
        +searchById(String) Volunteer
        +delete(String) boolean
        +showAll()
        +showAll(List~Volunteer~)
        +readFromFile()
        +saveToFile() boolean
        +getPathFile() String
        +getCsvPathFile() String
    }

    class Mountains {
        <<ArrayList~Mountain~>>
        -String pathFile
        +Mountains()
        +Mountains(String)
        +get(String) Mountain
        +isValidMountainCode(String) boolean
        +dataToObject(String) Mountain
        +readFromFile()
        +showAll()
    }

    Students --> Student : manages
    Volunteers --> Volunteer : manages
    Mountains --> Mountain : manages

    %% ===== DATA / MODEL =====
    class Mountain {
        -String mountainCode
        -String mountain
        -String province
        -String description
        +Mountain()
        +Mountain(String, String, String, String)
        +getMountainCode() String
        +getMountain() String
        +getProvince() String
        +getDescription() String
        +toString() String
        +equals(Object) boolean
        +hashCode() int
    }

    class StatisticalInfo {
        -String mountainCode
        -String mountainName
        -int numOfStudent
        -double totalCost
        +StatisticalInfo()
        +StatisticalInfo(String, String, int, double)
        +addStudent(double)
        +getNumOfStudent() int
        +getTotalCost() double
        +toString() String
    }

    class Statistics {
        <<HashMap~String, StatisticalInfo~>>
        +Statistics()
        +Statistics(List~Student~, Mountains)
        +statisticalize(List~Student~, Mountains)
        +show()
    }

    Statistics --> StatisticalInfo : contains
    Statistics --> Student : reads
    Statistics --> Mountains : reads

    %% ===== UTILITY =====
    class Acceptable {
        <<interface>>
        +String STUDENT_ID$
        +String CAMPUS_CODE$
        +String NAME_VALID$
        +String DOUBLE_VALID$
        +String INTEGER_VALID$
        +String PHONE_VALID$
        +String VIETTEL_VALID$
        +String VNPT_VALID$
        +String EMAIL_VALID$
        +String YES_NO_VALID$
        +String MENU_VALID$
        +String VOLUNTEER_ID$
        +String VOLUNTEER_NAME_VALID$
        +String SHIFT_VALID$
        +isValid(String, String) boolean$
    }

    class Inputter {
        -Scanner ndl
        +Inputter()
        +getString(String) String
        +getInt(String) int
        +getDouble(String) double
        +inputAndLoop(String, String) String
        +inputAndLoopAllowEmpty(String, String) String
        +confirmYesNo(String) boolean
        +getMenuChoice(String, int, int) int
    }

    Inputter --> Acceptable : uses patterns

    class Main {
        -Inputter inp$
        -Mountains mountains$
        -Students students$
        -Volunteers volunteers$
        +main(String[])$
        -showMenu()$
        -addNewRegistration()$
        -updateRegistration()$
        -displayRegisteredList()$
        -deleteRegistration()$
        -searchByName()$
        -filterByCampus()$
        -showStatistics()$
        -saveDataToFile()$
        -volunteerManagement()$
        -showVolunteerMenu()$
        -addNewVolunteer()$
        -displayVolunteerList()$
        -updateVolunteer()$
        -assignVolunteerToShift()$
        -deleteVolunteer()$
        -exitProgram()$
    }

    Main --> Inputter : uses
    Main --> Mountains : uses
    Main --> Students : uses
    Main --> Volunteers : uses
```

---

## Sơ đồ quan hệ kế thừa (đơn giản)

```mermaid
graph TD
    S[Serializable<br/><<interface>>] 
    P["Person<br/><<abstract>><br/>#id, #name<br/>+getDisplayInfo()*"]
    ST["Student<br/>phone, email<br/>mountainCode, tuitionFee"]
    VL["Volunteer<br/>skill, maxShiftsPerDay<br/>shiftsToday"]
    SK["Skill<br/><<enum>><br/>MEDIC | LOGISTIC | GUIDE_ASSIST"]

    S -.-> P
    P --> ST
    P --> VL
    SK -.-> VL

    style P fill:#f4a261,stroke:#e76f51,stroke-width:2px,color:#000
    style ST fill:#2a9d8f,stroke:#264653,stroke-width:2px,color:#fff
    style VL fill:#e9c46a,stroke:#f4a261,stroke-width:2px,color:#000
    style SK fill:#264653,stroke:#2a9d8f,stroke-width:2px,color:#fff
    style S fill:#e76f51,stroke:#264653,stroke-width:2px,color:#fff
```

---

## Danh sách file (12 files)

| # | File | Loại | Vai trò |
|---|------|------|---------|
| 1 | [Person.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Person.java) | Abstract class | Lớp gốc kế thừa, chứa `id`, `name` |
| 2 | [Student.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Student.java) | Class | Sinh viên đăng ký leo núi |
| 3 | [Volunteer.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Volunteer.java) | Class | Tình nguyện viên (Đề 31) |
| 4 | [Skill.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Skill.java) | Enum | MEDIC, LOGISTIC, GUIDE_ASSIST |
| 5 | [Students.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Students.java) | Manager | Quản lý danh sách Student |
| 6 | [Volunteers.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Volunteers.java) | Manager | Quản lý danh sách Volunteer |
| 7 | [Mountain.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Mountain.java) | Class | Thông tin núi |
| 8 | [Mountains.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Mountains.java) | Manager | Quản lý danh sách núi |
| 9 | [StatisticalInfo.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/StatisticalInfo.java) | Class | Dữ liệu thống kê 1 đỉnh |
| 10 | [Statistics.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Statistics.java) | Class | Tính toán & hiển thị thống kê |
| 11 | [Acceptable.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Acceptable.java) | Interface | Regex validation patterns |
| 12 | [Inputter.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Inputter.java) | Utility | Nhập liệu từ console |
| 13 | [Main.java](file:///c:/Users/Khuong/Desktop/FPTU/LAB211/01_J1.L.P0027.MountainHiking%20_200LOC/Project1_MountainHiking/src/Main.java) | Entry point | Menu chính + Volunteer sub-menu |

---

## Design Patterns sử dụng

| Pattern | Áp dụng |
|---------|---------|
| **Abstraction + Inheritance** | `Person` → `Student` / `Volunteer` |
| **Polymorphism** | `getDisplayInfo()` — mỗi lớp con tự định nghĩa |
| **Enum** | `Skill` — type-safe, có helper methods |
| **Manager/Repository** | `Students`, `Volunteers`, `Mountains` — quản lý collection |
| **Serialization** | Lưu/đọc `.dat` qua `ObjectInputStream/OutputStream` |
| **Strategy (validation)** | `Acceptable` interface — regex patterns tách riêng |
