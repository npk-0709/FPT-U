# Mountain Hiking Registration - Short Report

## 1. Objective

This console application manages student registrations for the Mountain Hiking Challenge. It supports add, update, delete, search, display, filter, statistics, save/load, and exit confirmation with validation.

## 2. Algorithm Design

```text
Load MountainList.csv into Mountains
Load registrations.dat into Students if the file exists
Repeat:
    Show menu 1..9
    Read a valid choice
    Execute the selected function
    Mark data as unsaved after Add/Update/Delete
Until user chooses Exit
If data is unsaved on Exit, ask whether to Save
```

Main menu:

1. New Registration
2. Update Registration Information
3. Display Registered List
4. Delete Registration Information
5. Search Participants by ID or Name
6. Filter Data by Campus
7. Statistics of Registration Numbers by Location
8. Save Data to File
9. Exit

## 3. Class Diagram

```mermaid
classDiagram
    class Acceptable {
        <<interface>>
        +STUDENT_ID
        +PHONE_VALID
        +EMAIL_VALID
        +isValid(data, pattern)
    }

    class Inputter {
        -Scanner ndl
        +getString(message)
        +getMenuChoice(message, min, max)
        +inputAndLoop(message, pattern)
        +inputAndLoopAllowEmpty(message, pattern)
        +confirmYesNo(message)
    }

    class Student {
        -String id
        -String name
        -String phone
        -String email
        -String mountainCode
        -double tuitionFee
        +calculateFee(phone)
        +getCampusCode()
        +toString()
        +equals(obj)
        +compareTo(other)
    }

    class Mountain {
        -String mountainCode
        -String mountain
        -String province
        -String description
        +toString()
        +equals(obj)
    }

    class Students {
        -String pathFile
        -boolean isSaved
        +add(student)
        +searchById(id)
        +searchByName(name)
        +filterByCampusCode(campus)
        +saveToFile()
        +showAll()
    }

    class Mountains {
        -String pathFile
        +readFromFile()
        +isValidMountainCode(code)
        +showAll()
    }

    class Statistics {
        +statisticalize(list, mountains)
        +show()
    }

    class StatisticalInfo {
        -String mountainCode
        -String mountainName
        -int numOfStudent
        -double totalCost
        +addStudent(fee)
        +toString()
    }

    Student ..|> Serializable
    Student ..|> Comparable
    Students --|> ArrayList
    Mountains --|> ArrayList
    Statistics --|> HashMap
    Students --> Student
    Mountains --> Mountain
    Statistics --> StatisticalInfo
    Students --> Statistics
    Inputter --> Acceptable
```

## 4. Use Case Diagram

```mermaid
flowchart LR
    Operator((Operator))
    Validate[[Validate Input]]
    Add[New Registration]
    Update[Update Registration]
    Display[Display Registered List]
    Delete[Delete Registration]
    Search[Search by ID or Name]
    Filter[Filter by Campus]
    Stats[Statistics by Mountain]
    Save[Save Data]
    Exit[Exit]

    Operator --> Add
    Operator --> Update
    Operator --> Display
    Operator --> Delete
    Operator --> Search
    Operator --> Filter
    Operator --> Stats
    Operator --> Save
    Operator --> Exit
    Add -. include .-> Validate
    Update -. include .-> Validate
    Delete -. include .-> Validate
    Search -. include .-> Validate
    Filter -. include .-> Validate
```

## 5. Sequence Diagram - Add Student

```mermaid
sequenceDiagram
    actor Operator
    participant Main
    participant Inputter
    participant Mountains
    participant Students
    participant Student

    Operator->>Main: Choose New Registration
    Main->>Inputter: Input and validate ID/name/phone/email
    Main->>Students: Check duplicated ID
    Main->>Mountains: Validate mountain code
    Main->>Student: Create student and calculate fee
    Main->>Students: Add student and mark unsaved
    Main-->>Operator: Show success message
```

## 6. Self-Assessment Checklist

- Menu 1..9 validates invalid numbers and does not crash.
- Add Student blocks duplicated ID, invalid name, phone, email, and mountain code.
- Search supports exact ID and case-insensitive partial name.
- Update supports Enter to keep old values and recalculates tuition fee after phone changes.
- Delete requires confirmation before removing a registration.
- Display uses aligned table columns.
- Filter campus uses the first two characters of Student ID: CE, DE, HE, SE, QE.
- Statistics count and total fee are grouped by mountain code.
- Save writes `registrations.dat` and exports `registrations.csv`.
- Exit warns the user when there are unsaved changes.
- Code uses private fields, getters/setters, constructors, `toString()`, `equals()`, `Comparable`, interface constants, and clear naming conventions.
