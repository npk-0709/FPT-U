/*==============================================================================
  DBI202 - Lab 5: SQL Queries, View, Index, Functions, Procedures, and Triggers
  Project 4: Household Cleaning Robot Sales & Maintenance Management System (HCRS&MMS)
  ------------------------------------------------------------------------------
  Database designed in Lab 4 (16 quan he, chuan BCNF). File nay co the chay tron
  ven trong SQL Server Management Studio (SSMS) tu tren xuong duoi.

  Cau truc file:
     PART 1 - DDL: tao database + bang + rang buoc
     PART 2 - DML: du lieu mau (seed data)
     PART 3 - SQL Queries (Basic -> Intermediate -> Advanced)
     PART 4 - User-defined Functions
     PART 5 - Stored Procedures
     PART 6 - Triggers
     PART 7 - Views & Indexes

  Ghi chu: cac dong bat dau bang  --##  la "marker" dung de sinh bao cao Word
  tu dong (create_lab5_report.py). SQL Server xem chung nhu comment binh thuong.
==============================================================================*/

--##META TITLE | Lab 5: SQL Queries, View, Index, Functions, Procedures, and Triggers
--##META SYSTEM | Household Cleaning Robot Sales & Maintenance Management System

/*==============================================================================
  PART 1 - DDL: TAO DATABASE VA CAC BANG
==============================================================================*/
USE master;
GO
IF DB_ID(N'HCRS_DB') IS NOT NULL
BEGIN
    ALTER DATABASE HCRS_DB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE HCRS_DB;
END
GO
CREATE DATABASE HCRS_DB;
GO
USE HCRS_DB;
GO

-- ---------------------------------------------------------------------------
-- 1) Customer - khach hang
-- ---------------------------------------------------------------------------
CREATE TABLE Customer (
    CustomerID   INT            IDENTITY(1,1) NOT NULL,
    FullName     NVARCHAR(100)  NOT NULL,
    PhoneNumber  VARCHAR(15)    NOT NULL,
    Email        NVARCHAR(100)  NOT NULL,
    Address      NVARCHAR(255)  NULL,
    Password     NVARCHAR(255)  NOT NULL,
    CONSTRAINT PK_Customer        PRIMARY KEY (CustomerID),
    CONSTRAINT UQ_Customer_Email  UNIQUE (Email),
    CONSTRAINT UQ_Customer_Phone  UNIQUE (PhoneNumber)
);
GO

-- ---------------------------------------------------------------------------
-- 2) Employee - nhan vien (Sales Staff / Technician / Administrator)
-- ---------------------------------------------------------------------------
CREATE TABLE Employee (
    EmployeeID   INT            IDENTITY(1,1) NOT NULL,
    FullName     NVARCHAR(100)  NOT NULL,
    Role         NVARCHAR(50)   NOT NULL,
    PhoneNumber  VARCHAR(15)    NOT NULL,
    Email        NVARCHAR(100)  NOT NULL,
    Password     NVARCHAR(255)  NOT NULL,
    CONSTRAINT PK_Employee        PRIMARY KEY (EmployeeID),
    CONSTRAINT UQ_Employee_Email  UNIQUE (Email),
    CONSTRAINT CK_Employee_Role   CHECK (Role IN (N'Sales Staff', N'Technician', N'Administrator'))
);
GO

-- ---------------------------------------------------------------------------
-- 3) RobotModel - mau robot (catalog)
-- ---------------------------------------------------------------------------
CREATE TABLE RobotModel (
    ModelID          INT            IDENTITY(1,1) NOT NULL,
    Brand            NVARCHAR(100)  NOT NULL,
    ModelName        NVARCHAR(100)  NOT NULL,
    Specifications   NVARCHAR(MAX)  NULL,
    UnitPrice        DECIMAL(18,2)  NOT NULL,
    WarrantyDuration INT            NOT NULL,
    CONSTRAINT PK_RobotModel             PRIMARY KEY (ModelID),
    CONSTRAINT UQ_RobotModel_ModelName   UNIQUE (ModelName),
    CONSTRAINT CK_RobotModel_UnitPrice   CHECK (UnitPrice > 0),
    CONSTRAINT CK_RobotModel_Warranty    CHECK (WarrantyDuration > 0)
);
GO

-- ---------------------------------------------------------------------------
-- 4) ModelFeature - thuoc tinh da tri "Features" cua RobotModel (weak entity)
-- ---------------------------------------------------------------------------
CREATE TABLE ModelFeature (
    ModelID  INT            NOT NULL,
    Feature  NVARCHAR(100)  NOT NULL,
    CONSTRAINT PK_ModelFeature PRIMARY KEY (ModelID, Feature),
    CONSTRAINT FK_ModelFeature_RobotModel FOREIGN KEY (ModelID)
        REFERENCES RobotModel(ModelID) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- ---------------------------------------------------------------------------
-- 5) RobotUnit - tung chiec robot cu the trong kho
-- ---------------------------------------------------------------------------
CREATE TABLE RobotUnit (
    RobotID       INT          IDENTITY(1,1) NOT NULL,
    ModelID       INT          NOT NULL,
    SerialNumber  VARCHAR(50)  NOT NULL,
    Status        NVARCHAR(30) NOT NULL CONSTRAINT DF_RobotUnit_Status DEFAULT (N'Available'),
    CONSTRAINT PK_RobotUnit            PRIMARY KEY (RobotID),
    CONSTRAINT UQ_RobotUnit_Serial     UNIQUE (SerialNumber),
    CONSTRAINT CK_RobotUnit_Status     CHECK (Status IN (N'Available', N'Sold', N'Under Maintenance', N'Retired')),
    CONSTRAINT FK_RobotUnit_RobotModel FOREIGN KEY (ModelID)
        REFERENCES RobotModel(ModelID)
);
GO

-- ---------------------------------------------------------------------------
-- 6) SalesOrder - don ban hang
-- ---------------------------------------------------------------------------
CREATE TABLE SalesOrder (
    OrderID      INT           IDENTITY(1,1) NOT NULL,
    CustomerID   INT           NOT NULL,
    EmployeeID   INT           NOT NULL,
    OrderDate    DATETIME      NOT NULL CONSTRAINT DF_SalesOrder_Date DEFAULT (GETDATE()),
    TotalAmount  DECIMAL(18,2) NOT NULL CONSTRAINT DF_SalesOrder_Total DEFAULT (0),
    OrderStatus  NVARCHAR(30)  NOT NULL CONSTRAINT DF_SalesOrder_Status DEFAULT (N'Pending'),
    CONSTRAINT PK_SalesOrder           PRIMARY KEY (OrderID),
    CONSTRAINT CK_SalesOrder_Total     CHECK (TotalAmount >= 0),
    CONSTRAINT CK_SalesOrder_Status    CHECK (OrderStatus IN (N'Pending', N'Confirmed', N'Shipped', N'Delivered', N'Cancelled')),
    CONSTRAINT FK_SalesOrder_Customer  FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID),
    CONSTRAINT FK_SalesOrder_Employee  FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);
GO

-- ---------------------------------------------------------------------------
-- 7) OrderDetail - chi tiet don hang (moi RobotUnit chi ban duoc 1 lan)
-- ---------------------------------------------------------------------------
CREATE TABLE OrderDetail (
    RobotID      INT           NOT NULL,
    OrderID      INT           NOT NULL,
    SellingPrice DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_OrderDetail            PRIMARY KEY (RobotID),
    CONSTRAINT CK_OrderDetail_Price      CHECK (SellingPrice > 0),
    CONSTRAINT FK_OrderDetail_RobotUnit  FOREIGN KEY (RobotID) REFERENCES RobotUnit(RobotID),
    CONSTRAINT FK_OrderDetail_SalesOrder FOREIGN KEY (OrderID)
        REFERENCES SalesOrder(OrderID) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- ---------------------------------------------------------------------------
-- 8) Payment - thanh toan (super-type)
-- ---------------------------------------------------------------------------
CREATE TABLE Payment (
    PaymentID     INT           IDENTITY(1,1) NOT NULL,
    Amount        DECIMAL(18,2) NOT NULL,
    PaymentDate   DATETIME      NOT NULL CONSTRAINT DF_Payment_Date DEFAULT (GETDATE()),
    PaymentMethod NVARCHAR(30)  NOT NULL,
    CONSTRAINT PK_Payment          PRIMARY KEY (PaymentID),
    CONSTRAINT CK_Payment_Amount   CHECK (Amount > 0),
    CONSTRAINT CK_Payment_Method   CHECK (PaymentMethod IN (N'Cash', N'Credit Card', N'Bank Transfer', N'E-Wallet'))
);
GO

-- ---------------------------------------------------------------------------
-- 9) OrderPayment - thanh toan cho don ban hang (sub-type cua Payment)
-- ---------------------------------------------------------------------------
CREATE TABLE OrderPayment (
    PaymentID INT NOT NULL,
    OrderID   INT NOT NULL,
    CONSTRAINT PK_OrderPayment          PRIMARY KEY (PaymentID),
    CONSTRAINT FK_OrderPayment_Payment  FOREIGN KEY (PaymentID)
        REFERENCES Payment(PaymentID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_OrderPayment_Order    FOREIGN KEY (OrderID) REFERENCES SalesOrder(OrderID)
);
GO

-- ---------------------------------------------------------------------------
-- 11) WarrantyRegistration - dang ky bao hanh (moi RobotUnit toi da 1 bao hanh)
-- ---------------------------------------------------------------------------
CREATE TABLE WarrantyRegistration (
    WarrantyID INT  IDENTITY(1,1) NOT NULL,
    RobotID    INT  NOT NULL,
    CustomerID INT  NOT NULL,
    StartDate  DATE NOT NULL,
    EndDate    DATE NOT NULL,
    CONSTRAINT PK_Warranty            PRIMARY KEY (WarrantyID),
    CONSTRAINT UQ_Warranty_Robot      UNIQUE (RobotID),
    CONSTRAINT CK_Warranty_Date       CHECK (EndDate > StartDate),
    CONSTRAINT FK_Warranty_RobotUnit  FOREIGN KEY (RobotID) REFERENCES RobotUnit(RobotID),
    CONSTRAINT FK_Warranty_Customer   FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);
GO

-- ---------------------------------------------------------------------------
-- 12) ServiceRequest - yeu cau bao tri / sua chua
-- ---------------------------------------------------------------------------
CREATE TABLE ServiceRequest (
    RequestID        INT           IDENTITY(1,1) NOT NULL,
    RobotID          INT           NOT NULL,
    CustomerID       INT           NOT NULL,
    IssueDescription NVARCHAR(MAX) NOT NULL,
    RequestDate      DATETIME      NOT NULL CONSTRAINT DF_Request_Date DEFAULT (GETDATE()),
    Status           NVARCHAR(30)  NOT NULL CONSTRAINT DF_Request_Status DEFAULT (N'Pending'),
    CONSTRAINT PK_ServiceRequest          PRIMARY KEY (RequestID),
    CONSTRAINT CK_Request_Status          CHECK (Status IN (N'Pending', N'Assigned', N'In Progress', N'Completed', N'Cancelled')),
    CONSTRAINT FK_Request_RobotUnit       FOREIGN KEY (RobotID) REFERENCES RobotUnit(RobotID),
    CONSTRAINT FK_Request_Customer        FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);
GO

-- ---------------------------------------------------------------------------
-- 13) MaintenanceRecord - ket qua bao tri (moi ServiceRequest -> 1 record)
-- ---------------------------------------------------------------------------
CREATE TABLE MaintenanceRecord (
    RecordID       INT           IDENTITY(1,1) NOT NULL,
    RequestID      INT           NOT NULL,
    TechnicianID   INT           NOT NULL,
    ActionsTaken   NVARCHAR(MAX) NULL,
    ServiceFee     DECIMAL(18,2) NOT NULL CONSTRAINT DF_Maint_Fee DEFAULT (0),
    CompletionDate DATETIME      NULL,
    CONSTRAINT PK_MaintenanceRecord    PRIMARY KEY (RecordID),
    CONSTRAINT UQ_Maint_Request        UNIQUE (RequestID),
    CONSTRAINT CK_Maint_Fee            CHECK (ServiceFee >= 0),
    CONSTRAINT FK_Maint_Request        FOREIGN KEY (RequestID) REFERENCES ServiceRequest(RequestID),
    CONSTRAINT FK_Maint_Technician     FOREIGN KEY (TechnicianID) REFERENCES Employee(EmployeeID)
);
GO

-- ---------------------------------------------------------------------------
-- 10) ServicePayment - thanh toan cho dich vu bao tri (sub-type cua Payment)
-- ---------------------------------------------------------------------------
CREATE TABLE ServicePayment (
    PaymentID       INT NOT NULL,
    ServiceRecordID INT NOT NULL,
    CONSTRAINT PK_ServicePayment         PRIMARY KEY (PaymentID),
    CONSTRAINT FK_ServicePayment_Payment FOREIGN KEY (PaymentID)
        REFERENCES Payment(PaymentID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_ServicePayment_Record  FOREIGN KEY (ServiceRecordID) REFERENCES MaintenanceRecord(RecordID)
);
GO

-- ---------------------------------------------------------------------------
-- 14) ReplacedPart - linh kien thay the trong 1 lan bao tri (weak entity)
-- ---------------------------------------------------------------------------
CREATE TABLE ReplacedPart (
    RecordID INT           NOT NULL,
    PartName NVARCHAR(100) NOT NULL,
    CONSTRAINT PK_ReplacedPart        PRIMARY KEY (RecordID, PartName),
    CONSTRAINT FK_ReplacedPart_Record FOREIGN KEY (RecordID)
        REFERENCES MaintenanceRecord(RecordID) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- ---------------------------------------------------------------------------
-- 15) DeviceLog - du lieu IoT thu thap tu robot
-- ---------------------------------------------------------------------------
CREATE TABLE DeviceLog (
    LogID     INT         IDENTITY(1,1) NOT NULL,
    RobotID   INT         NOT NULL,
    LogTime   DATETIME    NOT NULL CONSTRAINT DF_DeviceLog_Time DEFAULT (GETDATE()),
    ErrorCode VARCHAR(20) NULL,
    CONSTRAINT PK_DeviceLog          PRIMARY KEY (LogID),
    CONSTRAINT UQ_DeviceLog_RobotTime UNIQUE (RobotID, LogTime),
    CONSTRAINT FK_DeviceLog_RobotUnit FOREIGN KEY (RobotID) REFERENCES RobotUnit(RobotID)
);
GO

-- ---------------------------------------------------------------------------
-- 16) LogStatistic - cac chi so do duoc trong 1 device log (weak entity)
-- ---------------------------------------------------------------------------
CREATE TABLE LogStatistic (
    LogID       INT           NOT NULL,
    MetricName  NVARCHAR(100) NOT NULL,
    MetricValue NVARCHAR(255) NULL,
    CONSTRAINT PK_LogStatistic        PRIMARY KEY (LogID, MetricName),
    CONSTRAINT FK_LogStatistic_Log    FOREIGN KEY (LogID)
        REFERENCES DeviceLog(LogID) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

-- ---------------------------------------------------------------------------
-- (Bang ho tro cho Lab 5) RobotStatusAudit - luu lich su doi trang thai robot
--   duoc ghi tu dong boi TRIGGER trg_RobotUnit_AuditStatus (PART 6).
-- ---------------------------------------------------------------------------
CREATE TABLE RobotStatusAudit (
    AuditID    INT          IDENTITY(1,1) NOT NULL,
    RobotID    INT          NOT NULL,
    OldStatus  NVARCHAR(30) NULL,
    NewStatus  NVARCHAR(30) NULL,
    ChangedAt  DATETIME     NOT NULL CONSTRAINT DF_Audit_Time DEFAULT (GETDATE()),
    CONSTRAINT PK_RobotStatusAudit PRIMARY KEY (AuditID)
);
GO

/*==============================================================================
  PART 2 - DML: DU LIEU MAU (SEED DATA)
  Cac cot khoa chinh la IDENTITY nen khong chen ID -> ID tu tang 1..N theo
  thu tu chen (database vua duoc tao moi).
==============================================================================*/

-- 1) Customer (CustomerID = 1..8)
INSERT INTO Customer (FullName, PhoneNumber, Email, Address, Password) VALUES
 (N'Nguyen Van An',   '0901000001', N'an.nv@gmail.com',    N'12 Le Loi, Q1, TP.HCM',        N'pass001'),
 (N'Tran Thi Binh',   '0901000002', N'binh.tt@gmail.com',  N'34 Hai Ba Trung, Hue',         N'pass002'),
 (N'Le Hoang Cuong',  '0901000003', N'cuong.lh@gmail.com', N'56 Tran Phu, Da Nang',         N'pass003'),
 (N'Pham Thi Dung',   '0901000004', N'dung.pt@gmail.com',  N'78 Nguyen Trai, Ha Noi',       N'pass004'),
 (N'Vu Minh Duc',     '0901000005', N'duc.vm@gmail.com',   N'90 Cach Mang Thang 8, TP.HCM', N'pass005'),
 (N'Dang Thu Ha',     '0901000006', N'ha.dt@gmail.com',    N'21 Ba Trieu, Ha Noi',          N'pass006'),
 (N'Bui Quang Huy',   '0901000007', N'huy.bq@gmail.com',   N'43 Le Duan, Da Nang',          N'pass007'),
 (N'Ngo Thi Lan',     '0901000008', N'lan.nt@gmail.com',   N'65 Vo Van Tan, TP.HCM',        N'pass008');

-- 2) Employee (EmployeeID = 1..8). Technician: 3,4,5,8 | Sales: 1,2,7 | Admin: 6
INSERT INTO Employee (FullName, Role, PhoneNumber, Email, Password) VALUES
 (N'Hoang Van Son',  N'Sales Staff',   '0911000001', N'son.hv@hcrs.vn',  N'emp001'),
 (N'Nguyen Thi Mai', N'Sales Staff',   '0911000002', N'mai.nt@hcrs.vn',  N'emp002'),
 (N'Tran Van Tam',   N'Technician',    '0911000003', N'tam.tv@hcrs.vn',  N'emp003'),
 (N'Le Thi Hong',    N'Technician',    '0911000004', N'hong.lt@hcrs.vn', N'emp004'),
 (N'Pham Van Khoa',  N'Technician',    '0911000005', N'khoa.pv@hcrs.vn', N'emp005'),
 (N'Do Thi Nga',     N'Administrator', '0911000006', N'nga.dt@hcrs.vn',  N'emp006'),
 (N'Vu Van Long',    N'Sales Staff',   '0911000007', N'long.vv@hcrs.vn', N'emp007'),
 (N'Mai Thi Yen',    N'Technician',    '0911000008', N'yen.mt@hcrs.vn',  N'emp008');

-- 3) RobotModel (ModelID = 1..6)
INSERT INTO RobotModel (Brand, ModelName, Specifications, UnitPrice, WarrantyDuration) VALUES
 (N'Roborock', N'S8 Pro Ultra',     N'Luc hut 6000Pa, LiDAR, tu giat lau',        25000000, 24),
 (N'iRobot',   N'Roomba j7+',       N'Nhan dien vat can, tu do rac',              18000000, 12),
 (N'Ecovacs',  N'Deebot T20 Omni',  N'Lau nuoc nong, tu lam sach gie',            20000000, 24),
 (N'Xiaomi',   N'Robot Vacuum X10',  N'Luc hut 4000Pa, dieu khien qua app',        9000000, 12),
 (N'Dreame',   N'L20 Ultra',        N'Chan lau nang ha, LiDAR, tu giat',          22000000, 18),
 (N'Roborock', N'Q Revo',           N'Tu do rac va giat gie, dieu huong LiDAR',   15000000, 24);

-- 4) ModelFeature
INSERT INTO ModelFeature (ModelID, Feature) VALUES
 (1, N'LiDAR Navigation'), (1, N'Auto-empty Dock'), (1, N'Mopping'),
 (2, N'Obstacle Avoidance'), (2, N'Auto-empty Dock'),
 (3, N'Mopping'), (3, N'Self-cleaning'), (3, N'LiDAR Navigation'),
 (4, N'Mopping'), (4, N'App Control'),
 (5, N'LiDAR Navigation'), (5, N'Mopping'), (5, N'Self-cleaning'),
 (6, N'Mopping'), (6, N'Auto-empty Dock');

-- 5) RobotUnit (RobotID = 1..15)
INSERT INTO RobotUnit (ModelID, SerialNumber, Status) VALUES
 (1, 'RBK-S8-0001',  N'Sold'),              -- 1
 (1, 'RBK-S8-0002',  N'Sold'),              -- 2
 (2, 'IRB-J7-0001',  N'Sold'),              -- 3 (dang co request In Progress)
 (3, 'ECO-T20-0001', N'Sold'),              -- 4
 (4, 'XMI-X10-0001', N'Sold'),              -- 5
 (5, 'DRM-L20-0001', N'Sold'),              -- 6
 (6, 'RBK-QR-0001',  N'Sold'),              -- 7
 (2, 'IRB-J7-0002',  N'Sold'),              -- 8
 (4, 'XMI-X10-0002', N'Sold'),              -- 9
 (1, 'RBK-S8-0003',  N'Available'),         -- 10
 (3, 'ECO-T20-0002', N'Under Maintenance'), -- 11
 (5, 'DRM-L20-0002', N'Available'),         -- 12
 (6, 'RBK-QR-0002',  N'Available'),         -- 13
 (4, 'XMI-X10-0003', N'Available'),         -- 14
 (2, 'IRB-J7-0003',  N'Retired');           -- 15

-- 6) SalesOrder (OrderID = 1..8)
INSERT INTO SalesOrder (CustomerID, EmployeeID, OrderDate, TotalAmount, OrderStatus) VALUES
 (1, 1, '2024-02-10T09:30:00', 42500000, N'Delivered'),  -- 1: robot 1 + 8
 (2, 2, '2024-05-15T14:00:00', 38000000, N'Delivered'),  -- 2: robot 3 + 4
 (3, 1, '2024-08-20T10:15:00',  8800000, N'Shipped'),    -- 3: robot 5
 (1, 7, '2025-01-05T16:45:00', 24500000, N'Delivered'),  -- 4: robot 2
 (4, 2, '2025-02-18T11:20:00', 22000000, N'Confirmed'),  -- 5: robot 6
 (5, 7, '2025-03-25T13:10:00', 15000000, N'Delivered'),  -- 6: robot 7
 (6, 1, '2025-06-30T08:50:00',  8900000, N'Pending'),    -- 7: robot 9
 (2, 2, '2025-09-12T15:30:00',        0, N'Cancelled');  -- 8: huy, khong co chi tiet

-- 7) OrderDetail (PK = RobotID; moi robot ban 1 lan)
INSERT INTO OrderDetail (RobotID, OrderID, SellingPrice) VALUES
 (1, 1, 25000000), (8, 1, 17500000),
 (3, 2, 18000000), (4, 2, 20000000),
 (5, 3,  8800000),
 (2, 4, 24500000),
 (6, 5, 22000000),
 (7, 6, 15000000),
 (9, 7,  8900000);

-- 8) Payment (PaymentID = 1..8). 1..6 = don hang, 7..8 = dich vu bao tri
INSERT INTO Payment (Amount, PaymentDate, PaymentMethod) VALUES
 (42500000, '2024-02-10T09:35:00', N'Credit Card'),   -- 1 -> Order1
 (38000000, '2024-05-15T14:05:00', N'Bank Transfer'), -- 2 -> Order2
 ( 8800000, '2024-08-20T10:20:00', N'Cash'),          -- 3 -> Order3
 (24500000, '2025-01-05T16:50:00', N'E-Wallet'),      -- 4 -> Order4
 (22000000, '2025-02-18T11:25:00', N'Credit Card'),   -- 5 -> Order5
 (15000000, '2025-03-25T13:15:00', N'Bank Transfer'), -- 6 -> Order6
 (  850000, '2025-06-05T10:00:00', N'Cash'),          -- 7 -> Rec2
 ( 1200000, '2025-09-09T09:00:00', N'Credit Card');   -- 8 -> Rec6

-- 9) OrderPayment (don hang 7 Pending va 8 Cancelled chua thanh toan)
INSERT INTO OrderPayment (PaymentID, OrderID) VALUES
 (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6);

-- 10) WarrantyRegistration (WarrantyID = 1..8) - robot 9 khong dang ky bao hanh
INSERT INTO WarrantyRegistration (RobotID, CustomerID, StartDate, EndDate) VALUES
 (1, 1, '2024-02-10', '2026-02-10'),   -- het han
 (2, 1, '2025-01-05', '2027-01-05'),   -- con hieu luc
 (3, 2, '2024-05-15', '2025-05-15'),   -- het han
 (4, 2, '2024-05-15', '2026-05-15'),   -- het han
 (5, 3, '2024-08-20', '2025-08-20'),   -- het han
 (6, 4, '2025-02-18', '2026-08-18'),   -- con hieu luc
 (7, 5, '2025-03-25', '2027-03-25'),   -- con hieu luc
 (8, 1, '2024-02-10', '2025-02-10');   -- het han

-- 11) ServiceRequest (RequestID = 1..7)
INSERT INTO ServiceRequest (RobotID, CustomerID, IssueDescription, RequestDate, Status) VALUES
 (1, 1, N'May khong sac duoc pin',            '2024-09-01T08:00:00', N'Completed'),
 (8, 1, N'Banh xe ket, dong co hut yeu',      '2025-06-01T09:00:00', N'Completed'),
 (3, 2, N'Mop khong nha nuoc, dang kiem tra', '2025-07-10T10:30:00', N'In Progress'),
 (5, 3, N'Pin chai, chai nhanh het',          '2025-09-05T14:00:00', N'Completed'),
 (1, 1, N'Cap nhat firmware bi loi',          '2025-05-20T11:00:00', N'Completed'),
 (4, 2, N'Tieng on bat thuong khi hut',       '2025-09-15T15:30:00', N'Completed'),
 (6, 4, N'Robot bao loi E150',                '2025-10-01T07:45:00', N'Pending');

-- 12) MaintenanceRecord (RecordID = 1..6). RequestID 7 (Pending) chua co record
INSERT INTO MaintenanceRecord (RequestID, TechnicianID, ActionsTaken, ServiceFee, CompletionDate) VALUES
 (1, 3, N'Thay pin Lithium moi',                       0,       '2024-09-05T16:00:00'),
 (2, 4, N'Thay banh xe truoc, ve sinh dong co hut',    850000,  '2025-06-05T17:00:00'),
 (5, 3, N'Cai dat lai firmware phien ban on dinh',     0,       '2025-05-22T10:00:00'),
 (6, 5, N'Thay tam loc HEPA bi nghet',                 0,       '2025-09-18T11:30:00'),
 (3, 4, N'Dang kiem tra bom nuoc, chua hoan tat',      0,       NULL),
 (4, 5, N'Thay pin va cam bien va cham',               1200000, '2025-09-09T16:30:00');

-- 13) ServicePayment (record co phi > 0): Rec2 (RecordID=2), Rec6 (RecordID=6)
INSERT INTO ServicePayment (PaymentID, ServiceRecordID) VALUES
 (7, 2), (8, 6);

-- 14) ReplacedPart
INSERT INTO ReplacedPart (RecordID, PartName) VALUES
 (1, N'Pin Lithium'),
 (2, N'Banh xe truoc'), (2, N'Dong co hut'),
 (4, N'Tam loc HEPA'),
 (6, N'Pin Lithium'), (6, N'Cam bien va cham');

-- 15) DeviceLog (LogID = 1..10)
INSERT INTO DeviceLog (RobotID, LogTime, ErrorCode) VALUES
 (1, '2025-06-01T08:00:00', NULL),
 (1, '2025-06-02T08:00:00', 'E102'),
 (3, '2025-07-09T09:30:00', 'E205'),
 (3, '2025-07-10T09:30:00', 'E205'),
 (5, '2025-09-01T07:15:00', 'E311'),
 (5, '2025-09-04T07:15:00', 'E311'),
 (4, '2025-09-10T10:00:00', NULL),
 (6, '2025-09-20T06:45:00', 'E150'),
 (2, '2025-08-01T12:00:00', NULL),
 (7, '2025-08-15T18:30:00', 'E102');

-- 16) LogStatistic
INSERT INTO LogStatistic (LogID, MetricName, MetricValue) VALUES
 (1, N'RunTimeMinutes', N'45'), (1, N'AreaCleaned_m2', N'30'), (1, N'BatteryUsedPct', N'40'),
 (2, N'RunTimeMinutes', N'10'), (2, N'BatteryUsedPct', N'15'),
 (3, N'RunTimeMinutes', N'5'),  (3, N'ErrorCount', N'3'),
 (5, N'RunTimeMinutes', N'60'), (5, N'AreaCleaned_m2', N'55'),
 (7, N'RunTimeMinutes', N'50'), (7, N'AreaCleaned_m2', N'42'),
 (8, N'RunTimeMinutes', N'35'), (8, N'BatteryUsedPct', N'30');
GO

/*==============================================================================
  PART 3 - SQL QUERIES
==============================================================================*/

--##SECTION | Basic SQL Queries

--##GROUP | Truy van SELECT toan bo ban ghi
--##ITEM | Lay tat ca khach hang
--##DESC | Truy xuat toan bo ban ghi cua bang Customer.
SELECT * FROM Customer;

--##ITEM | Lay tat ca nhan vien
--##DESC | Truy xuat toan bo nhan vien (ban hang, ky thuat, quan tri).
SELECT * FROM Employee;

--##ITEM | Lay tat ca mau robot trong catalog
--##DESC | Truy xuat toan bo mau robot cung gia ban va thoi han bao hanh.
SELECT * FROM RobotModel;

--##ITEM | Lay tat ca robot trong kho
--##DESC | Truy xuat toan bo tung chiec robot va trang thai hien tai cua no.
SELECT * FROM RobotUnit;

--##ITEM | Lay tat ca don ban hang
--##DESC | Truy xuat toan bo don ban hang.
SELECT * FROM SalesOrder;

--##ITEM | Lay du lieu cac bang giao dich va bao tri
--##DESC | Xem nhanh du lieu cac bang con lai: thanh toan, yeu cau dich vu, bao tri, log IoT.
SELECT * FROM Payment;
SELECT * FROM ServiceRequest;
SELECT * FROM MaintenanceRecord;
SELECT * FROM DeviceLog;

--##GROUP | Loc du lieu voi WHERE
--##ITEM | Khach hang o TP.HCM
--##DESC | Loc khach hang co dia chi chua "TP.HCM" bang toan tu LIKE.
SELECT CustomerID, FullName, Address
FROM Customer
WHERE Address LIKE N'%TP.HCM%';

--##ITEM | Nhan vien la ky thuat vien
--##DESC | Loc nhan vien co vai tro (Role) la Technician.
SELECT EmployeeID, FullName, Role
FROM Employee
WHERE Role = N'Technician';

--##ITEM | Mau robot gia tren 20 trieu
--##DESC | Loc cac mau robot cao cap co UnitPrice > 20.000.000 VND.
SELECT ModelID, Brand, ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice > 20000000;

--##ITEM | Mau robot cua thuong hieu Roborock
--##DESC | Loc cac mau robot thuoc thuong hieu Roborock.
SELECT ModelID, ModelName, UnitPrice
FROM RobotModel
WHERE Brand = N'Roborock';

--##ITEM | Robot dang con san trong kho
--##DESC | Loc cac robot co trang thai Available (san sang ban).
SELECT RobotID, ModelID, SerialNumber
FROM RobotUnit
WHERE Status = N'Available';

--##ITEM | Don hang da giao thanh cong
--##DESC | Loc cac don hang co trang thai Delivered.
SELECT OrderID, CustomerID, OrderDate, TotalAmount
FROM SalesOrder
WHERE OrderStatus = N'Delivered';

--##ITEM | Don hang trong nam 2025
--##DESC | Loc cac don hang phat sinh trong nam 2025 bang dieu kien khoang ngay.
SELECT OrderID, OrderDate, TotalAmount, OrderStatus
FROM SalesOrder
WHERE OrderDate >= '2025-01-01' AND OrderDate < '2026-01-01';

--##ITEM | Don hang co tong tien tu 10 den 40 trieu
--##DESC | Loc don hang theo khoang gia tri voi BETWEEN.
SELECT OrderID, TotalAmount, OrderStatus
FROM SalesOrder
WHERE TotalAmount BETWEEN 10000000 AND 40000000;

--##ITEM | Thanh toan bang the tin dung
--##DESC | Loc cac giao dich thanh toan bang Credit Card.
SELECT PaymentID, Amount, PaymentDate
FROM Payment
WHERE PaymentMethod = N'Credit Card';

--##ITEM | Yeu cau dich vu chua hoan tat
--##DESC | Loc yeu cau dich vu chua o trang thai Completed (dung IN cho nhieu trang thai).
SELECT RequestID, RobotID, Status, RequestDate
FROM ServiceRequest
WHERE Status IN (N'Pending', N'Assigned', N'In Progress');

--##ITEM | Bao hanh con hieu luc
--##DESC | Loc cac dang ky bao hanh ma ngay ket thuc chua qua thoi diem hien tai.
SELECT WarrantyID, RobotID, StartDate, EndDate
FROM WarrantyRegistration
WHERE EndDate >= CAST(GETDATE() AS DATE);

--##ITEM | Log thiet bi co ma loi
--##DESC | Loc cac log IoT thuc su ghi nhan ma loi (ErrorCode khac NULL).
SELECT LogID, RobotID, LogTime, ErrorCode
FROM DeviceLog
WHERE ErrorCode IS NOT NULL;

--##ITEM | Lan bao tri co thu phi
--##DESC | Loc cac ban ghi bao tri thuc su phat sinh phi dich vu (> 0).
SELECT RecordID, RequestID, ServiceFee, CompletionDate
FROM MaintenanceRecord
WHERE ServiceFee > 0;

--##GROUP | Sap xep voi ORDER BY
--##ITEM | Mau robot theo gia giam dan
--##DESC | Sap xep catalog tu dat den re.
SELECT ModelID, ModelName, UnitPrice
FROM RobotModel
ORDER BY UnitPrice DESC;

--##ITEM | Khach hang theo ten A-Z
--##DESC | Sap xep danh sach khach hang theo ho ten tang dan.
SELECT CustomerID, FullName
FROM Customer
ORDER BY FullName ASC;

--##ITEM | Don hang quy 1/2025 theo ngay giam dan
--##DESC | Liet ke don hang trong quy 1 nam 2025, sap xep giam dan theo ngay dat hang.
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE OrderDate >= '2025-01-01' AND OrderDate < '2025-04-01'
ORDER BY OrderDate DESC;

--##ITEM | Nhan vien theo vai tro roi theo ten
--##DESC | Sap xep nhan vien theo Role, trong cung vai tro thi theo ten.
SELECT FullName, Role
FROM Employee
ORDER BY Role ASC, FullName ASC;

--##ITEM | Robot theo trang thai roi theo mau
--##DESC | Sap xep robot theo Status, cung trang thai thi theo ModelID.
SELECT RobotID, ModelID, Status
FROM RobotUnit
ORDER BY Status ASC, ModelID ASC;

--##ITEM | Thanh toan theo so tien giam dan
--##DESC | Sap xep cac giao dich theo so tien tu cao den thap.
SELECT PaymentID, Amount, PaymentMethod
FROM Payment
ORDER BY Amount DESC;

--##ITEM | Yeu cau dich vu theo ngay tang dan
--##DESC | Sap xep yeu cau dich vu theo ngay gui tang dan.
SELECT RequestID, RobotID, RequestDate, Status
FROM ServiceRequest
ORDER BY RequestDate ASC;

--##ITEM | Bao tri theo phi giam dan
--##DESC | Sap xep ban ghi bao tri theo phi dich vu giam dan.
SELECT RecordID, RequestID, ServiceFee
FROM MaintenanceRecord
ORDER BY ServiceFee DESC;

--##ITEM | Bao hanh theo ngay het han
--##DESC | Sap xep dang ky bao hanh theo ngay het han tang dan.
SELECT WarrantyID, RobotID, EndDate
FROM WarrantyRegistration
ORDER BY EndDate ASC;

--##ITEM | Log thiet bi moi nhat truoc
--##DESC | Sap xep log IoT theo thoi gian giam dan (moi nhat len dau).
SELECT LogID, RobotID, LogTime, ErrorCode
FROM DeviceLog
ORDER BY LogTime DESC;

--##GROUP | Ham tong hop (COUNT, SUM, AVG, MAX, MIN)
--##ITEM | Tong so khach hang
--##DESC | Dem tong so khach hang trong he thong.
SELECT COUNT(*) AS TongKhachHang FROM Customer;

--##ITEM | Tong so robot trong kho
--##DESC | Dem tong so chiec robot dang quan ly.
SELECT COUNT(*) AS TongSoRobot FROM RobotUnit;

--##ITEM | Gia trung binh cua mau robot
--##DESC | Tinh gia ban trung binh cua cac mau robot.
SELECT AVG(UnitPrice) AS GiaTrungBinh FROM RobotModel;

--##ITEM | Gia cao nhat va thap nhat
--##DESC | Tim gia ban cao nhat va thap nhat trong catalog.
SELECT MAX(UnitPrice) AS GiaCaoNhat, MIN(UnitPrice) AS GiaThapNhat FROM RobotModel;

--##ITEM | Tong doanh thu theo don hang
--##DESC | Tinh tong gia tri tat ca don hang (theo cot TotalAmount).
SELECT SUM(TotalAmount) AS TongDoanhThu FROM SalesOrder;

--##ITEM | So robot con san sang ban
--##DESC | Dem so robot dang o trang thai Available.
SELECT COUNT(*) AS SoRobotConSan
FROM RobotUnit
WHERE Status = N'Available';

--##ITEM | Tong tien thuc thu tu thanh toan
--##DESC | Tinh tong so tien da thu duoc qua tat ca giao dich thanh toan.
SELECT SUM(Amount) AS TongTienDaThu FROM Payment;

--##ITEM | Phi dich vu trung binh
--##DESC | Tinh phi bao tri trung binh tren cac ban ghi.
SELECT AVG(ServiceFee) AS PhiTrungBinh FROM MaintenanceRecord;

--##ITEM | Phi dich vu cao nhat
--##DESC | Tim phi bao tri cao nhat tung phat sinh.
SELECT MAX(ServiceFee) AS PhiCaoNhat FROM MaintenanceRecord;

--##ITEM | Tong so yeu cau dich vu
--##DESC | Dem tong so yeu cau bao tri/sua chua.
SELECT COUNT(*) AS TongYeuCau FROM ServiceRequest;

--##ITEM | So log co ghi nhan loi
--##DESC | Dem so log IoT co ma loi (bo qua cac log NULL).
SELECT COUNT(ErrorCode) AS SoLogLoi FROM DeviceLog;

--##SECTION | Intermediate SQL Queries

--##GROUP | Ket noi nhieu bang (INNER JOIN / LEFT JOIN)
--##ITEM | Don hang kem ten khach va nhan vien ban
--##DESC | INNER JOIN 3 bang SalesOrder, Customer, Employee de hien thi ten khach hang va nhan vien xu ly.
SELECT so.OrderID, c.FullName AS KhachHang, e.FullName AS NhanVienBan,
       so.OrderDate, so.TotalAmount, so.OrderStatus
FROM SalesOrder so
JOIN Customer c ON so.CustomerID = c.CustomerID
JOIN Employee e ON so.EmployeeID = e.EmployeeID;

--##ITEM | Robot da ban kem mau va gia ban
--##DESC | JOIN OrderDetail -> RobotUnit -> RobotModel de biet moi robot da ban thuoc mau nao.
SELECT od.OrderID, od.RobotID, rm.Brand, rm.ModelName, od.SellingPrice
FROM OrderDetail od
JOIN RobotUnit ru ON od.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID
ORDER BY od.OrderID;

--##ITEM | Danh sach robot trong kho kem thong tin mau
--##DESC | JOIN RobotUnit voi RobotModel de hien thi thuong hieu, ten mau, trang thai.
SELECT ru.RobotID, ru.SerialNumber, rm.Brand, rm.ModelName, ru.Status
FROM RobotUnit ru
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;

--##ITEM | Yeu cau dich vu kem ten khach va serial robot
--##DESC | JOIN ServiceRequest voi Customer va RobotUnit de xem ai gui yeu cau cho robot nao.
SELECT sr.RequestID, c.FullName AS KhachHang, ru.SerialNumber,
       sr.IssueDescription, sr.Status
FROM ServiceRequest sr
JOIN Customer c ON sr.CustomerID = c.CustomerID
JOIN RobotUnit ru ON sr.RobotID = ru.RobotID;

--##ITEM | Lich su bao tri kem ky thuat vien phu trach
--##DESC | JOIN MaintenanceRecord -> ServiceRequest -> Employee de gan moi lan bao tri voi su co va ky thuat vien.
SELECT mr.RecordID, sr.IssueDescription, e.FullName AS KyThuatVien,
       mr.ServiceFee, mr.CompletionDate
FROM MaintenanceRecord mr
JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
JOIN Employee e ON mr.TechnicianID = e.EmployeeID;

--##ITEM | Thong tin bao hanh day du
--##DESC | JOIN WarrantyRegistration voi RobotUnit, RobotModel va Customer.
SELECT w.WarrantyID, c.FullName AS ChuSoHuu, rm.ModelName,
       ru.SerialNumber, w.StartDate, w.EndDate
FROM WarrantyRegistration w
JOIN RobotUnit ru ON w.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID
JOIN Customer c ON w.CustomerID = c.CustomerID;

--##ITEM | Thanh toan cho don hang
--##DESC | JOIN Payment -> OrderPayment -> SalesOrder de biet moi thanh toan tuong ung don hang nao.
SELECT p.PaymentID, p.Amount, p.PaymentMethod, op.OrderID, so.OrderStatus
FROM Payment p
JOIN OrderPayment op ON p.PaymentID = op.PaymentID
JOIN SalesOrder so ON op.OrderID = so.OrderID;

--##ITEM | Thanh toan cho dich vu bao tri
--##DESC | JOIN Payment -> ServicePayment -> MaintenanceRecord de xem thanh toan cho lan bao tri nao.
SELECT p.PaymentID, p.Amount, p.PaymentMethod, sp.ServiceRecordID, mr.ActionsTaken
FROM Payment p
JOIN ServicePayment sp ON p.PaymentID = sp.PaymentID
JOIN MaintenanceRecord mr ON sp.ServiceRecordID = mr.RecordID;

--##ITEM | Log IoT kem mau robot
--##DESC | JOIN DeviceLog -> RobotUnit -> RobotModel de gan log voi mau robot tuong ung.
SELECT dl.LogID, rm.ModelName, dl.LogTime, dl.ErrorCode
FROM DeviceLog dl
JOIN RobotUnit ru ON dl.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;

--##ITEM | Tat ca don hang ke ca don khong co chi tiet (LEFT JOIN)
--##DESC | LEFT JOIN SalesOrder voi OrderDetail: don hang bi huy (khong co dong chi tiet) van hien thi voi gia tri NULL.
SELECT so.OrderID, so.OrderStatus, od.RobotID, od.SellingPrice
FROM SalesOrder so
LEFT JOIN OrderDetail od ON so.OrderID = od.OrderID
ORDER BY so.OrderID;

--##ITEM | Tat ca khach hang ke ca nguoi chua mua (LEFT JOIN)
--##DESC | LEFT JOIN Customer voi SalesOrder de tim ca khach hang chua phat sinh don hang nao.
SELECT c.CustomerID, c.FullName, so.OrderID
FROM Customer c
LEFT JOIN SalesOrder so ON c.CustomerID = so.CustomerID
ORDER BY c.CustomerID;

--##ITEM | Cac tinh nang cua tung mau robot
--##DESC | JOIN RobotModel voi ModelFeature de liet ke tinh nang theo mau.
SELECT rm.ModelName, mf.Feature
FROM RobotModel rm
JOIN ModelFeature mf ON rm.ModelID = mf.ModelID
ORDER BY rm.ModelName;

--##GROUP | Gom nhom voi GROUP BY va HAVING
--##ITEM | So luong robot theo tung mau
--##DESC | GROUP BY ModelID, dem so chiec robot moi mau dang co trong kho.
SELECT rm.ModelName, COUNT(ru.RobotID) AS SoLuong
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
GROUP BY rm.ModelName;

--##ITEM | So luong robot theo trang thai
--##DESC | GROUP BY Status de thong ke ton kho (Available/Sold/Under Maintenance/Retired).
SELECT Status, COUNT(*) AS SoLuong
FROM RobotUnit
GROUP BY Status;

--##ITEM | Tong chi tieu cua tung khach hang
--##DESC | GROUP BY khach hang, tinh tong gia tri don hang.
SELECT c.FullName, SUM(so.TotalAmount) AS TongChiTieu
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
ORDER BY TongChiTieu DESC;

--##ITEM | So don hang moi nhan vien ban da xu ly
--##DESC | GROUP BY nhan vien de dem so don da xu ly.
SELECT e.FullName, COUNT(so.OrderID) AS SoDon
FROM Employee e
JOIN SalesOrder so ON e.EmployeeID = so.EmployeeID
GROUP BY e.FullName;

--##ITEM | Tong tien ban theo tung don (tu chi tiet)
--##DESC | GROUP BY OrderID tren OrderDetail de tinh tong gia ban thuc te cua moi don.
SELECT OrderID, COUNT(RobotID) AS SoRobot, SUM(SellingPrice) AS TongTien
FROM OrderDetail
GROUP BY OrderID;

--##ITEM | So robot da ban theo mau
--##DESC | JOIN va GROUP BY de dem so robot da ban ra cho moi mau.
SELECT rm.ModelName, COUNT(od.RobotID) AS SoDaBan
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
JOIN OrderDetail od ON ru.RobotID = od.RobotID
GROUP BY rm.ModelName;

--##ITEM | So yeu cau dich vu theo trang thai
--##DESC | GROUP BY Status tren ServiceRequest.
SELECT Status, COUNT(*) AS SoYeuCau
FROM ServiceRequest
GROUP BY Status;

--##ITEM | Robot bi gui bao tri tu 2 lan tro len (HAVING)
--##DESC | GROUP BY RobotID va loc HAVING COUNT >= 2 de tim robot hay gap su co.
SELECT RobotID, COUNT(*) AS SoLanYeuCau
FROM ServiceRequest
GROUP BY RobotID
HAVING COUNT(*) >= 2;

--##ITEM | Tong phi dich vu theo ky thuat vien
--##DESC | GROUP BY ky thuat vien de tinh tong phi thu duoc tu cong viec bao tri.
SELECT e.FullName, COUNT(mr.RecordID) AS SoLanBaoTri, SUM(mr.ServiceFee) AS TongPhi
FROM Employee e
JOIN MaintenanceRecord mr ON e.EmployeeID = mr.TechnicianID
GROUP BY e.FullName;

--##ITEM | So tinh nang cua moi mau robot
--##DESC | GROUP BY mau de dem so tinh nang.
SELECT rm.ModelName, COUNT(mf.Feature) AS SoTinhNang
FROM RobotModel rm
JOIN ModelFeature mf ON rm.ModelID = mf.ModelID
GROUP BY rm.ModelName;

--##ITEM | Mau robot co nhieu hon 2 chiec trong kho (HAVING)
--##DESC | GROUP BY mau va loc HAVING COUNT > 2.
SELECT rm.ModelName, COUNT(ru.RobotID) AS SoLuong
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
GROUP BY rm.ModelName
HAVING COUNT(ru.RobotID) > 2;

--##ITEM | Khach hang chi tieu tren 30 trieu (HAVING)
--##DESC | GROUP BY khach hang va dung HAVING de loc nguoi chi tieu lon.
SELECT c.FullName, SUM(so.TotalAmount) AS TongChiTieu
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
HAVING SUM(so.TotalAmount) > 30000000;

--##GROUP | Truy van con (Subquery) trong WHERE / FROM
--##ITEM | Khach hang da tung dat hang
--##DESC | Subquery trong IN: lay khach hang co CustomerID xuat hien trong SalesOrder.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID IN (SELECT CustomerID FROM SalesOrder);

--##ITEM | Robot chua tung duoc ban
--##DESC | Subquery NOT IN: robot khong nam trong bang OrderDetail.
SELECT RobotID, SerialNumber, Status
FROM RobotUnit
WHERE RobotID NOT IN (SELECT RobotID FROM OrderDetail);

--##ITEM | Mau robot dat hon gia trung binh
--##DESC | Subquery vo huong: so sanh UnitPrice voi gia trung binh toan catalog.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice > (SELECT AVG(UnitPrice) FROM RobotModel);

--##ITEM | Don hang lon hon gia tri trung binh
--##DESC | Subquery vo huong tinh AVG(TotalAmount) roi loc.
SELECT OrderID, TotalAmount
FROM SalesOrder
WHERE TotalAmount > (SELECT AVG(TotalAmount) FROM SalesOrder);

--##ITEM | Khach hang chua tung yeu cau dich vu
--##DESC | Subquery NOT IN tren ServiceRequest.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID NOT IN (SELECT CustomerID FROM ServiceRequest);

--##ITEM | Nhan vien da tung thuc hien bao tri
--##DESC | Subquery IN tren MaintenanceRecord de lay ky thuat vien co cong viec.
SELECT EmployeeID, FullName, Role
FROM Employee
WHERE EmployeeID IN (SELECT TechnicianID FROM MaintenanceRecord);

--##ITEM | Doanh thu theo khach hang (subquery trong FROM)
--##DESC | Dung bang dan xuat (derived table) tinh tong chi tieu roi loc nguoi chi tieu > 20 trieu.
SELECT t.CustomerID, c.FullName, t.TongChiTieu
FROM (SELECT CustomerID, SUM(TotalAmount) AS TongChiTieu
      FROM SalesOrder GROUP BY CustomerID) t
JOIN Customer c ON t.CustomerID = c.CustomerID
WHERE t.TongChiTieu > 20000000;

--##ITEM | Robot dang co bao hanh
--##DESC | Subquery IN tren WarrantyRegistration.
SELECT RobotID, SerialNumber
FROM RobotUnit
WHERE RobotID IN (SELECT RobotID FROM WarrantyRegistration);

--##ITEM | Don hang gan day nhat
--##DESC | Subquery vo huong dung MAX(OrderDate) trong WHERE.
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE OrderDate = (SELECT MAX(OrderDate) FROM SalesOrder);

--##ITEM | Mau robot khong con chiec nao "Available"
--##DESC | Subquery NOT IN: mau khong xuat hien trong tap cac mau co robot Available.
SELECT ModelID, ModelName
FROM RobotModel
WHERE ModelID NOT IN (
    SELECT ModelID FROM RobotUnit WHERE Status = N'Available'
);

--##SECTION | Advanced SQL Queries

--##GROUP | Truy van con long nhau (Nested Subqueries)
--##ITEM | Khach hang da mua robot thuong hieu Roborock
--##DESC | Long 4 cap: RobotModel(Brand) -> RobotUnit -> OrderDetail -> SalesOrder de truy ra khach hang.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID IN (
    SELECT CustomerID FROM SalesOrder
    WHERE OrderID IN (
        SELECT OrderID FROM OrderDetail
        WHERE RobotID IN (
            SELECT RobotID FROM RobotUnit
            WHERE ModelID IN (
                SELECT ModelID FROM RobotModel WHERE Brand = N'Roborock'
            )
        )
    )
);

--##ITEM | Robot tung phat sinh chi phi bao tri co thu tien
--##DESC | Long 2 cap: MaintenanceRecord(fee>0) -> ServiceRequest de lay RobotID.
SELECT RobotID, SerialNumber
FROM RobotUnit
WHERE RobotID IN (
    SELECT RobotID FROM ServiceRequest
    WHERE RequestID IN (
        SELECT RequestID FROM MaintenanceRecord WHERE ServiceFee > 0
    )
);

--##ITEM | Ky thuat vien tung sua robot thuong hieu iRobot
--##DESC | Long nhieu cap qua MaintenanceRecord -> ServiceRequest -> RobotUnit -> RobotModel(Brand).
SELECT EmployeeID, FullName
FROM Employee
WHERE EmployeeID IN (
    SELECT TechnicianID FROM MaintenanceRecord
    WHERE RequestID IN (
        SELECT RequestID FROM ServiceRequest
        WHERE RobotID IN (
            SELECT RobotID FROM RobotUnit
            WHERE ModelID IN (
                SELECT ModelID FROM RobotModel WHERE Brand = N'iRobot'
            )
        )
    )
);

--##ITEM | Mau robot hien co chiec dang bao tri
--##DESC | Long subquery: lay ModelID cua nhung robot co Status = Under Maintenance.
SELECT ModelID, ModelName
FROM RobotModel
WHERE ModelID IN (
    SELECT ModelID FROM RobotUnit WHERE Status = N'Under Maintenance'
);

--##ITEM | Khach hang co robot dang con bao hanh hieu luc
--##DESC | Long subquery ket hop dieu kien ngay het han voi danh sach robot da ban.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID IN (
    SELECT CustomerID FROM WarrantyRegistration
    WHERE EndDate >= CAST(GETDATE() AS DATE)
);

--##ITEM | Khach hang chi tieu cao hon muc trung binh moi khach
--##DESC | Subquery long: so sanh tong chi tieu cua khach voi gia tri trung binh cua tong chi tieu cac khach hang.
SELECT c.FullName, SUM(so.TotalAmount) AS TongChiTieu
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
HAVING SUM(so.TotalAmount) > (
    SELECT AVG(t.Tong)
    FROM (SELECT SUM(TotalAmount) AS Tong FROM SalesOrder GROUP BY CustomerID) t
);

--##GROUP | Su dung EXISTS, IN, ANY/ALL
--##ITEM | Khach hang da tung dat hang (EXISTS)
--##DESC | EXISTS tuong quan: ton tai it nhat 1 don hang cua khach hang dang xet.
SELECT c.CustomerID, c.FullName
FROM Customer c
WHERE EXISTS (SELECT 1 FROM SalesOrder so WHERE so.CustomerID = c.CustomerID);

--##ITEM | Robot chua tung gui log IoT nao (NOT EXISTS)
--##DESC | NOT EXISTS: khong ton tai dong log nao trong DeviceLog cho robot dang xet.
SELECT ru.RobotID, ru.SerialNumber
FROM RobotUnit ru
WHERE NOT EXISTS (SELECT 1 FROM DeviceLog dl WHERE dl.RobotID = ru.RobotID);

--##ITEM | Mau robot co it nhat 1 chiec con san (EXISTS tuong quan)
--##DESC | EXISTS tuong quan voi RobotUnit Status = Available.
SELECT rm.ModelID, rm.ModelName
FROM RobotModel rm
WHERE EXISTS (
    SELECT 1 FROM RobotUnit ru
    WHERE ru.ModelID = rm.ModelID AND ru.Status = N'Available'
);

--##ITEM | Robot da ban trong cac don da giao (IN)
--##DESC | IN voi subquery loc cac OrderID co trang thai Delivered.
SELECT RobotID, OrderID, SellingPrice
FROM OrderDetail
WHERE OrderID IN (SELECT OrderID FROM SalesOrder WHERE OrderStatus = N'Delivered');

--##ITEM | Mau robot dat hon it nhat 1 mau Roborock (ANY)
--##DESC | > ANY: gia lon hon gia thap nhat trong cac mau Roborock.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice > ANY (SELECT UnitPrice FROM RobotModel WHERE Brand = N'Roborock');

--##ITEM | Mau robot dat nhat (>= ALL)
--##DESC | >= ALL: gia lon hon hoac bang tat ca cac gia khac, tuc la mau dat nhat.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice >= ALL (SELECT UnitPrice FROM RobotModel);

--##GROUP | Phep toan tap hop (UNION / INTERSECT / EXCEPT)
--##ITEM | Danh ba lien he tong hop (UNION)
--##DESC | UNION gop ten + email cua khach hang va nhan vien thanh mot danh ba duy nhat (loai trung).
SELECT FullName, Email, N'Customer' AS LoaiNguoiDung FROM Customer
UNION
SELECT FullName, Email, N'Employee' FROM Employee;

--##ITEM | ID khach hang co phat sinh giao dich (UNION)
--##DESC | UNION cac CustomerID tu don hang va tu yeu cau dich vu (hop cua hai tap).
SELECT CustomerID FROM SalesOrder
UNION
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Khach hang vua mua hang vua tung yeu cau dich vu (INTERSECT)
--##DESC | INTERSECT: giao cua tap khach co don hang va tap khach co yeu cau dich vu.
SELECT CustomerID FROM SalesOrder
INTERSECT
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Khach hang mua hang nhung chua tung yeu cau dich vu (EXCEPT)
--##DESC | EXCEPT: hieu cua tap khach co don hang tru di tap khach co yeu cau dich vu.
SELECT CustomerID FROM SalesOrder
EXCEPT
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Robot chua tung ban ra (EXCEPT)
--##DESC | EXCEPT: tat ca robot tru di robot da xuat hien trong OrderDetail.
SELECT RobotID FROM RobotUnit
EXCEPT
SELECT RobotID FROM OrderDetail;

--##ITEM | Robot vua co log IoT vua co yeu cau dich vu (INTERSECT)
--##DESC | INTERSECT: giao cua tap robot co log va tap robot co yeu cau dich vu.
SELECT RobotID FROM DeviceLog
INTERSECT
SELECT RobotID FROM ServiceRequest;

/*==============================================================================
  PART 4 - USER-DEFINED FUNCTIONS
==============================================================================*/

--##SECTION | User-defined Functions

--##ITEM | fn_GetWarrantyStatus - kiem tra tinh trang bao hanh cua mot robot
--##DESC | Ham vo huong (scalar) nhan vao RobotID, tra ve "Con bao hanh", "Het bao hanh" hoac "Khong co bao hanh" bang cach so sanh EndDate voi ngay hien tai.
GO
CREATE FUNCTION dbo.fn_GetWarrantyStatus (@RobotID INT)
RETURNS NVARCHAR(30)
AS
BEGIN
    DECLARE @End DATE;
    SELECT @End = EndDate FROM WarrantyRegistration WHERE RobotID = @RobotID;
    IF @End IS NULL
        RETURN N'Khong co bao hanh';
    IF @End >= CAST(GETDATE() AS DATE)
        RETURN N'Con bao hanh';
    RETURN N'Het bao hanh';
END;
GO
-- Demo: ap dung ham cho tung robot da ban
SELECT RobotID, SerialNumber, dbo.fn_GetWarrantyStatus(RobotID) AS TinhTrangBaoHanh
FROM RobotUnit
WHERE Status <> N'Available';

--##ITEM | fn_GetCustomerTotalSpending - tong chi tieu cua mot khach hang
--##DESC | Ham vo huong tinh tong so tien khach hang da thanh toan cho cac don hang (qua Payment + OrderPayment). Dung ISNULL de tra ve 0 neu khach chua thanh toan.
GO
CREATE FUNCTION dbo.fn_GetCustomerTotalSpending (@CustomerID INT)
RETURNS DECIMAL(18,2)
AS
BEGIN
    DECLARE @Total DECIMAL(18,2);
    SELECT @Total = SUM(p.Amount)
    FROM Payment p
    JOIN OrderPayment op ON p.PaymentID = op.PaymentID
    JOIN SalesOrder so ON op.OrderID = so.OrderID
    WHERE so.CustomerID = @CustomerID;
    RETURN ISNULL(@Total, 0);
END;
GO
-- Demo: liet ke tong chi tieu cua moi khach hang
SELECT CustomerID, FullName, dbo.fn_GetCustomerTotalSpending(CustomerID) AS TongChiTieu
FROM Customer
ORDER BY TongChiTieu DESC;

--##ITEM | fn_GetModelAvailableUnits - so robot con san cua mot mau
--##DESC | Ham vo huong tra ve so luong robot dang o trang thai Available cua mot mau, ho tro nhan vien ban hang kiem tra ton kho nhanh.
GO
CREATE FUNCTION dbo.fn_GetModelAvailableUnits (@ModelID INT)
RETURNS INT
AS
BEGIN
    DECLARE @Cnt INT;
    SELECT @Cnt = COUNT(*)
    FROM RobotUnit
    WHERE ModelID = @ModelID AND Status = N'Available';
    RETURN @Cnt;
END;
GO
-- Demo: ton kho con san theo tung mau
SELECT ModelID, ModelName, dbo.fn_GetModelAvailableUnits(ModelID) AS SoConSan
FROM RobotModel;

--##ITEM | fn_GetMaintenanceHistoryByRobot - lich su bao tri cua mot robot (table-valued)
--##DESC | Ham tra ve bang (inline table-valued function) liet ke toan bo lich su bao tri cua mot robot kem su co, ky thuat vien va phi dich vu.
GO
CREATE FUNCTION dbo.fn_GetMaintenanceHistoryByRobot (@RobotID INT)
RETURNS TABLE
AS
RETURN
(
    SELECT mr.RecordID, sr.RequestID, sr.IssueDescription,
           e.FullName AS Technician, mr.ServiceFee, mr.CompletionDate
    FROM MaintenanceRecord mr
    JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
    JOIN Employee e ON mr.TechnicianID = e.EmployeeID
    WHERE sr.RobotID = @RobotID
);
GO
-- Demo: lich su bao tri cua robot RobotID = 1
SELECT * FROM dbo.fn_GetMaintenanceHistoryByRobot(1);

/*==============================================================================
  PART 5 - STORED PROCEDURES
==============================================================================*/

--##SECTION | Stored Procedures

--##ITEM | sp_CreateSalesOrder - tao don ban hang hoan chinh
--##DESC | Thu tuc da buoc trong mot transaction: kiem tra robot con san, tao SalesOrder, them OrderDetail, tao Payment va OrderPayment, va cap nhat ton kho (robot -> Sold). Co TRY/CATCH de rollback khi loi.
GO
CREATE PROCEDURE sp_CreateSalesOrder
    @CustomerID    INT,
    @EmployeeID    INT,
    @RobotID       INT,
    @SellingPrice  DECIMAL(18,2),
    @PaymentMethod NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM RobotUnit WHERE RobotID = @RobotID AND Status = N'Available')
            THROW 50001, N'Robot khong ton tai hoac khong con san de ban.', 1;

        DECLARE @OrderID INT, @PaymentID INT;

        INSERT INTO SalesOrder (CustomerID, EmployeeID, TotalAmount, OrderStatus)
        VALUES (@CustomerID, @EmployeeID, @SellingPrice, N'Confirmed');
        SET @OrderID = SCOPE_IDENTITY();

        INSERT INTO OrderDetail (RobotID, OrderID, SellingPrice)
        VALUES (@RobotID, @OrderID, @SellingPrice);

        -- Cap nhat ton kho (trigger trg_OrderDetail_AfterInsert cung dam bao dieu nay
        -- cho cac lenh INSERT truc tiep; o day cap nhat tuong minh de thu tuc tu chu).
        UPDATE RobotUnit SET Status = N'Sold' WHERE RobotID = @RobotID;

        INSERT INTO Payment (Amount, PaymentMethod) VALUES (@SellingPrice, @PaymentMethod);
        SET @PaymentID = SCOPE_IDENTITY();
        INSERT INTO OrderPayment (PaymentID, OrderID) VALUES (@PaymentID, @OrderID);

        COMMIT TRANSACTION;
        SELECT @OrderID AS NewOrderID, @PaymentID AS NewPaymentID;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO
-- Demo: ban robot RobotID = 10 (dang Available) cho khach hang 7
EXEC sp_CreateSalesOrder @CustomerID = 7, @EmployeeID = 1, @RobotID = 10,
                         @SellingPrice = 24000000, @PaymentMethod = N'Credit Card';

--##ITEM | sp_RegisterWarranty - dang ky bao hanh cho robot da ban
--##DESC | Thu tuc tu dong tinh ngay het han bao hanh tu thoi han (so thang) cua mau robot. Chan dang ky trung va kiem tra robot ton tai.
GO
CREATE PROCEDURE sp_RegisterWarranty
    @RobotID    INT,
    @CustomerID INT,
    @StartDate  DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;
    IF @StartDate IS NULL SET @StartDate = CAST(GETDATE() AS DATE);

    IF EXISTS (SELECT 1 FROM WarrantyRegistration WHERE RobotID = @RobotID)
        THROW 50002, N'Robot nay da duoc dang ky bao hanh.', 1;

    DECLARE @Months INT;
    SELECT @Months = rm.WarrantyDuration
    FROM RobotUnit ru JOIN RobotModel rm ON ru.ModelID = rm.ModelID
    WHERE ru.RobotID = @RobotID;

    IF @Months IS NULL
        THROW 50003, N'Robot khong ton tai trong he thong.', 1;

    DECLARE @End DATE = DATEADD(MONTH, @Months, @StartDate);
    INSERT INTO WarrantyRegistration (RobotID, CustomerID, StartDate, EndDate)
    VALUES (@RobotID, @CustomerID, @StartDate, @End);

    SELECT @RobotID AS RobotID, @StartDate AS StartDate, @End AS EndDate;
END;
GO
-- Demo: dang ky bao hanh cho robot 10 vua ban cho khach hang 7
EXEC sp_RegisterWarranty @RobotID = 10, @CustomerID = 7;

--##ITEM | sp_CompleteMaintenance - ghi nhan hoan tat bao tri
--##DESC | Thu tuc da buoc: kiem tra yeu cau dich vu, neu robot con bao hanh thi mien phi, tao MaintenanceRecord, cap nhat trang thai yeu cau thanh Completed, va tao thanh toan dich vu neu co phat sinh phi. Goi lai ham fn_GetWarrantyStatus.
GO
CREATE PROCEDURE sp_CompleteMaintenance
    @RequestID     INT,
    @TechnicianID  INT,
    @ActionsTaken  NVARCHAR(MAX),
    @BaseFee       DECIMAL(18,2),
    @PaymentMethod NVARCHAR(30) = N'Cash'
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM ServiceRequest WHERE RequestID = @RequestID)
            THROW 50010, N'Yeu cau dich vu khong ton tai.', 1;
        IF EXISTS (SELECT 1 FROM MaintenanceRecord WHERE RequestID = @RequestID)
            THROW 50011, N'Yeu cau nay da co ban ghi bao tri.', 1;

        DECLARE @RobotID INT;
        SELECT @RobotID = RobotID FROM ServiceRequest WHERE RequestID = @RequestID;

        DECLARE @Fee DECIMAL(18,2) = @BaseFee;
        IF dbo.fn_GetWarrantyStatus(@RobotID) = N'Con bao hanh'
            SET @Fee = 0;   -- bao hanh con hieu luc -> mien phi dich vu

        DECLARE @RecordID INT;
        INSERT INTO MaintenanceRecord (RequestID, TechnicianID, ActionsTaken, ServiceFee, CompletionDate)
        VALUES (@RequestID, @TechnicianID, @ActionsTaken, @Fee, GETDATE());
        SET @RecordID = SCOPE_IDENTITY();

        UPDATE ServiceRequest SET Status = N'Completed' WHERE RequestID = @RequestID;

        IF @Fee > 0
        BEGIN
            DECLARE @PaymentID INT;
            INSERT INTO Payment (Amount, PaymentMethod) VALUES (@Fee, @PaymentMethod);
            SET @PaymentID = SCOPE_IDENTITY();
            INSERT INTO ServicePayment (PaymentID, ServiceRecordID) VALUES (@PaymentID, @RecordID);
        END

        COMMIT TRANSACTION;
        SELECT @RecordID AS NewRecordID, @Fee AS AppliedFee;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO
-- Demo: hoan tat yeu cau dich vu RequestID = 7 (robot 6 con bao hanh -> mien phi)
EXEC sp_CompleteMaintenance @RequestID = 7, @TechnicianID = 5,
                            @ActionsTaken = N'Kiem tra va reset loi E150', @BaseFee = 500000;

--##ITEM | sp_GenerateSalesReport - bao cao doanh thu theo mau & khoang thoi gian
--##DESC | Thu tuc nhan khoang ngay va tra ve bao cao tong hop: so luong ban va doanh thu cua tung mau robot trong khoang do, sap xep theo doanh thu giam dan.
GO
CREATE PROCEDURE sp_GenerateSalesReport
    @FromDate DATE,
    @ToDate   DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT rm.Brand, rm.ModelName,
           COUNT(od.RobotID)      AS SoLuongBan,
           SUM(od.SellingPrice)   AS DoanhThu
    FROM OrderDetail od
    JOIN SalesOrder so ON od.OrderID = so.OrderID
    JOIN RobotUnit  ru ON od.RobotID = ru.RobotID
    JOIN RobotModel rm ON ru.ModelID = rm.ModelID
    WHERE so.OrderDate >= @FromDate
      AND so.OrderDate <  DATEADD(DAY, 1, @ToDate)
    GROUP BY rm.Brand, rm.ModelName
    ORDER BY DoanhThu DESC;
END;
GO
-- Demo: bao cao doanh thu tu 01/2024 den 12/2025
EXEC sp_GenerateSalesReport @FromDate = '2024-01-01', @ToDate = '2025-12-31';

/*==============================================================================
  PART 6 - TRIGGERS
==============================================================================*/

--##SECTION | Triggers

--##ITEM | trg_RobotUnit_AuditStatus - tu dong ghi nhat ky doi trang thai robot
--##DESC | Trigger AFTER UPDATE tren RobotUnit: moi khi cot Status thay doi, tu dong ghi mot dong vao bang RobotStatusAudit (trang thai cu -> moi, thoi diem). Chi ghi khi gia tri thuc su khac nhau.
GO
CREATE TRIGGER trg_RobotUnit_AuditStatus ON RobotUnit
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO RobotStatusAudit (RobotID, OldStatus, NewStatus)
    SELECT i.RobotID, d.Status, i.Status
    FROM inserted i
    JOIN deleted d ON i.RobotID = d.RobotID
    WHERE i.Status <> d.Status;
END;
GO
-- Demo: doi trang thai robot 14 (Available -> Retired) va xem nhat ky
UPDATE RobotUnit SET Status = N'Retired' WHERE RobotID = 14;
SELECT * FROM RobotStatusAudit WHERE RobotID = 14;

--##ITEM | trg_OrderDetail_AfterInsert - tu dong cap nhat ton kho sau khi ban
--##DESC | Trigger AFTER INSERT tren OrderDetail: khi mot robot duoc them vao chi tiet don hang, tu dong chuyen trang thai robot do sang "Sold". Day la vi du "cap nhat ton kho sau giao dich ban hang".
GO
CREATE TRIGGER trg_OrderDetail_AfterInsert ON OrderDetail
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE ru
    SET ru.Status = N'Sold'
    FROM RobotUnit ru
    JOIN inserted i ON ru.RobotID = i.RobotID
    WHERE ru.Status <> N'Sold';
END;
GO
-- Demo: ban robot 12 (dang Available) bang cach chen OrderDetail truc tiep
SELECT RobotID, Status AS TruocKhiBan FROM RobotUnit WHERE RobotID = 12;
DECLARE @demoOrder INT;
INSERT INTO SalesOrder (CustomerID, EmployeeID, TotalAmount, OrderStatus)
VALUES (2, 2, 22000000, N'Confirmed');
SET @demoOrder = SCOPE_IDENTITY();
INSERT INTO OrderDetail (RobotID, OrderID, SellingPrice) VALUES (12, @demoOrder, 22000000);
SELECT RobotID, Status AS SauKhiBan FROM RobotUnit WHERE RobotID = 12;       -- -> Sold
SELECT * FROM RobotStatusAudit WHERE RobotID = 12;                           -- audit ghi nhan

--##ITEM | trg_PreventDeleteCustomerWithOrders - chan xoa khach hang con rang buoc
--##DESC | Trigger INSTEAD OF DELETE tren Customer: khong cho xoa khach hang neu ho con don hang hoac yeu cau dich vu (bao ve ban ghi cha). Neu hop le thi moi thuc hien xoa.
GO
CREATE TRIGGER trg_PreventDeleteCustomerWithOrders ON Customer
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM SalesOrder so JOIN deleted d ON so.CustomerID = d.CustomerID)
       OR EXISTS (SELECT 1 FROM ServiceRequest sr JOIN deleted d ON sr.CustomerID = d.CustomerID)
    BEGIN
        RAISERROR(N'Khong the xoa khach hang dang co don hang hoac yeu cau dich vu.', 16, 1);
        RETURN;
    END
    DELETE FROM Customer WHERE CustomerID IN (SELECT CustomerID FROM deleted);
END;
GO
-- Demo: (a) xoa khach hang khong rang buoc -> thanh cong; (b) xoa khach co don hang -> bi chan
INSERT INTO Customer (FullName, PhoneNumber, Email, Password)
VALUES (N'Khach Tam Demo', '0900000099', N'tam.demo@gmail.com', N'temp');
DELETE FROM Customer WHERE Email = N'tam.demo@gmail.com';   -- thanh cong
BEGIN TRY
    DELETE FROM Customer WHERE CustomerID = 1;               -- bi trigger chan
END TRY
BEGIN CATCH
    PRINT ERROR_MESSAGE();
END CATCH

--##ITEM | trg_Maintenance_AfterInsert - ap dung quy tac bao hanh & dong bo trang thai
--##DESC | Trigger AFTER INSERT tren MaintenanceRecord: (1) neu robot con bao hanh tai thoi diem bao tri thi tu dong dat ServiceFee = 0 (mien phi theo bao hanh); (2) khi co ngay hoan tat thi tu dong cap nhat yeu cau dich vu lien quan sang trang thai Completed.
GO
CREATE TRIGGER trg_Maintenance_AfterInsert ON MaintenanceRecord
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE mr
    SET mr.ServiceFee = 0
    FROM MaintenanceRecord mr
    JOIN inserted i               ON mr.RecordID = i.RecordID
    JOIN ServiceRequest sr        ON i.RequestID = sr.RequestID
    JOIN WarrantyRegistration w   ON sr.RobotID  = w.RobotID
    WHERE i.ServiceFee > 0
      AND CAST(GETDATE() AS DATE) BETWEEN w.StartDate AND w.EndDate;

    UPDATE sr
    SET sr.Status = N'Completed'
    FROM ServiceRequest sr
    JOIN inserted i ON sr.RequestID = i.RequestID
    WHERE i.CompletionDate IS NOT NULL;
END;
GO
-- Demo: tao yeu cau moi cho robot 6 (con bao hanh) roi chen ban ghi bao tri co phi 600000
DECLARE @demoReq INT;
INSERT INTO ServiceRequest (RobotID, CustomerID, IssueDescription, Status)
VALUES (6, 4, N'Bao duong dinh ky trong han bao hanh', N'Assigned');
SET @demoReq = SCOPE_IDENTITY();
INSERT INTO MaintenanceRecord (RequestID, TechnicianID, ActionsTaken, ServiceFee, CompletionDate)
VALUES (@demoReq, 5, N'Bao duong dinh ky', 600000, GETDATE());
-- Ket qua: ServiceFee bi trigger dat ve 0 (con bao hanh) va yeu cau -> Completed
SELECT mr.RecordID, mr.ServiceFee, sr.Status
FROM MaintenanceRecord mr JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
WHERE mr.RequestID = @demoReq;

/*==============================================================================
  PART 7 - VIEWS & INDEXES
==============================================================================*/

--##SECTION | Views and Indexes

--##GROUP | Views - khung nhin don gian hoa truy van phuc tap
--##ITEM | vw_RobotInventory - ton kho robot kem tinh trang bao hanh
--##DESC | View tong hop tung robot voi thuong hieu, ten mau, gia, trang thai va tinh trang bao hanh (goi ham fn_GetWarrantyStatus). Giup nhan vien tra cuu ton kho nhanh chong.
GO
CREATE VIEW vw_RobotInventory AS
SELECT ru.RobotID, ru.SerialNumber, rm.Brand, rm.ModelName, rm.UnitPrice,
       ru.Status, dbo.fn_GetWarrantyStatus(ru.RobotID) AS WarrantyStatus
FROM RobotUnit ru
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;
GO
-- Demo: xem toan bo ton kho qua view
SELECT * FROM vw_RobotInventory ORDER BY RobotID;

--##ITEM | vw_SalesOrderSummary - tom tat don hang kem so tien da thanh toan
--##DESC | View ghep don hang voi ten khach hang, nhan vien ban va tong so tien da thanh toan (subquery), giup bao cao ban hang ma khong can viet lai cac phep JOIN phuc tap.
GO
CREATE VIEW vw_SalesOrderSummary AS
SELECT so.OrderID, c.FullName AS CustomerName, e.FullName AS SalesStaff,
       so.OrderDate, so.TotalAmount, so.OrderStatus,
       ISNULL((SELECT SUM(p.Amount)
               FROM OrderPayment op JOIN Payment p ON op.PaymentID = p.PaymentID
               WHERE op.OrderID = so.OrderID), 0) AS PaidAmount
FROM SalesOrder so
JOIN Customer c ON so.CustomerID = c.CustomerID
JOIN Employee e ON so.EmployeeID = e.EmployeeID;
GO
-- Demo: xem tom tat tat ca don hang
SELECT * FROM vw_SalesOrderSummary ORDER BY OrderID;

--##ITEM | vw_MaintenanceDetails - chi tiet bao tri day du
--##DESC | View ghep ban ghi bao tri voi robot, khach hang, ky thuat vien va mo ta su co, giup quan ly theo doi lich su bao tri ma chi can truy van mot doi tuong duy nhat.
GO
CREATE VIEW vw_MaintenanceDetails AS
SELECT mr.RecordID, ru.SerialNumber, rm.ModelName, c.FullName AS Customer,
       tech.FullName AS Technician, sr.IssueDescription, mr.ServiceFee, mr.CompletionDate
FROM MaintenanceRecord mr
JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
JOIN RobotUnit ru      ON sr.RobotID = ru.RobotID
JOIN RobotModel rm     ON ru.ModelID = rm.ModelID
JOIN Customer c        ON sr.CustomerID = c.CustomerID
JOIN Employee tech     ON mr.TechnicianID = tech.EmployeeID;
GO
-- Demo: xem chi tiet bao tri qua view
SELECT * FROM vw_MaintenanceDetails ORDER BY RecordID;

--##GROUP | Indexes - chi muc tang toc truy van
--##ITEM | IX_RobotUnit_Status - chi muc don cot
--##DESC | Chi muc khong gom cum tren mot cot RobotUnit(Status), tang toc cac truy van loc/thong ke theo trang thai ton kho (vi du dem so robot Available).
GO
CREATE NONCLUSTERED INDEX IX_RobotUnit_Status ON RobotUnit(Status);
GO
-- Demo: truy van huong loi nho chi muc nay (xem Actual Execution Plan trong SSMS)
SELECT RobotID, ModelID, SerialNumber FROM RobotUnit WHERE Status = N'Available';

--##ITEM | IX_SalesOrder_Customer_Date - chi muc ghep (composite)
--##DESC | Chi muc ghep tren SalesOrder(CustomerID, OrderDate) toi uu cac truy van tra cuu don hang theo tung khach hang trong mot khoang thoi gian (vi du lich su mua hang).
GO
CREATE NONCLUSTERED INDEX IX_SalesOrder_Customer_Date ON SalesOrder(CustomerID, OrderDate);
GO
-- Demo: truy van loc theo khach hang va thoi gian
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE CustomerID = 1 AND OrderDate >= '2024-01-01'
ORDER BY OrderDate;

--##ITEM | IX_ServiceRequest_Status - chi muc don cot (bo sung)
--##DESC | Chi muc tren ServiceRequest(Status) giup tang toc viec liet ke cac yeu cau dich vu dang cho xu ly (Pending/Assigned/In Progress).
GO
CREATE NONCLUSTERED INDEX IX_ServiceRequest_Status ON ServiceRequest(Status);
GO
-- Demo
SELECT RequestID, RobotID, RequestDate FROM ServiceRequest WHERE Status = N'Pending';

/*==============================================================================
  HET FILE - Lab5_HCRS.sql
==============================================================================*/
