CREATE DATABASE db01; -- tạo database mới
DROP DATABASE db01; -- xoa database nếu đã tồn tại
USE db01; -- sử dụng database vừa tạo
create table tbllocation (
    locnum int primary key, 
    locname nvarchar(100) not null
);

create table tbldepartment (
    depnum int primary key,
    depname nvarchar(100) not null,
    mgrssn varchar(20),
    mgrassdate date
);

create table tblemployee (
    empssn varchar(20) primary key,
    empname nvarchar(100) not null,
    empaddress nvarchar(255),
    empsalary decimal(18, 2),
    empsex char(1),
    empbirthdate date,
    depnum int,
    supervisorssn varchar(20),
    empstartdate date
);

create table tbldeplocation (
    depnum int not null,
    locnum int not null,

);

create table tblproject (
    pronum int primary key,
    proname nvarchar(100) not null,
    locnum int,
    depnum int
);

create table tbldependent (
    depname nvarchar(100) not null,
    empssn varchar(20) not null,   
    depsex char(1),
    depbirthdate date,
    deprelationship nvarchar(50)
);

create table tblworkson (
    empssn varchar(20) not null,    
    pronum int not null,           
    workhours decimal(5, 2)
);

alter table tbldeplocation
    add constraint pk_tbldeplocation primary key (depnum, locnum);

alter table tbldependent
    add constraint pk_tbldependent primary key (depname, empssn);

alter table tblworkson
    add constraint pk_tblworkson primary key (empssn, pronum);
    
alter table tbldepartment
    add constraint fk_department_employee foreign key (mgrssn) 
    references tblemployee(empssn);

alter table tblemployee
    add constraint fk_employee_department foreign key (depnum) 
    references tbldepartment(depnum);

alter table tblemployee
    add constraint fk_employee_supervisor foreign key (supervisorssn) 
    references tblemployee(empssn);

alter table tbldeplocation
    add constraint fk_deplocation_department foreign key (depnum) 
    references tbldepartment(depnum);

alter table tbldeplocation
    add constraint fk_deplocation_location foreign key (locnum) 
    references tbllocation(locnum);

alter table tblproject
    add constraint fk_project_location foreign key (locnum) 
    references tbllocation(locnum);

alter table tblproject
    add constraint fk_project_department foreign key (depnum) 
    references tbldepartment(depnum);

alter table tbldependent
    add constraint fk_dependent_employee foreign key (empssn) 
    references tblemployee(empssn) on delete cascade;

alter table tblworkson
    add constraint fk_workson_employee foreign key (empssn) 
    references tblemployee(empssn);

alter table tblworkson
    add constraint fk_workson_project foreign key (pronum) 
    references tblproject(pronum);