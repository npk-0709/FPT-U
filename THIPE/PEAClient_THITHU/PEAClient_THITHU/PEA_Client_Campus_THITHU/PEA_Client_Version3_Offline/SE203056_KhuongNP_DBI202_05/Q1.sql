create table Departments (
	DeptID varchar(20) primary key,
	name nvarchar(200) not null,
	office nvarchar(100) not null
);

create table Employees (
	EmpCode varchar(20) primary key,
	DeptID varchar(20) not null,
	Name nvarchar(50) not null,
	BirthDate date not null,
	foreign key (DeptID) REFERENCES Departments(DeptID)
);

create table Dependants (
	EmpCode varchar(20) not null ,
	Number int not null,
	Name nvarchar(50) not null,
	BirthDate date not null,
	Role nvarchar(30) not null,
	primary key (EmpCode,Number),
	foreign key (EmpCode) REFERENCES Employees(EmpCode)
);

