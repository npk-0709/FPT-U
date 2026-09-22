create table Departments(
    DeptID varchar(20) PRIMARY KEY,
    name nvarchar(200) not null,
    office nvarchar(100) not null
);

create table Employees(
    Empcode  varchar(20) PRIMARY KEY,
    Name nvarchar(50),
    BrithDate date not null,
    DeptID varchar(20) not null,
     foreign key (DeptID) REFERENCES Departments(DeptID)
)


create table Dependants(
    Number int not null,
    EmpCode VARCHAR(20) not null, 
    Name nvarchar(50) not null,
    BirthDate date not null,
    Role nvarchar(30) not null,
    PRIMARY key ( Number, EmpCode),
    foreign key (EmpCode) REFERENCES Employees(EmpCode)
)

