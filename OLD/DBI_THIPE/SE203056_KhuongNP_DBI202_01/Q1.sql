create table Departments (
    DeptID varchar(20) primary key ,    
    name nvarchar(200) not null,           
    office nvarchar(100)
);
create table Employees (
    EmpCode varchar(20) primary key ,    
    Name nvarchar(50),           
    BirthDate date , 
    DeptID varchar(20),
    foreign key (DeptID) references Departments(DeptID) on delete cascade
);
create table Dependants (
	Number int not null ,
    EmpCode varchar(20) not null ,    
    Name nvarchar(5) ,           
    BirthDate date , 
    Role nvarchar(30) ,
    primary key (Number,EmpCode),
    foreign key (EmpCode) references Employees(EmpCode) on delete cascade
);
