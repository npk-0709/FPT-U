CREATE DATABASE HCRS_DB;

use HCRS_DB;


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
 
 
 
--##ITEM | Orders in 2025
--##DESC | Filter orders created in 2025 using a date-range condition.
SELECT OrderID, OrderDate, TotalAmount, OrderStatus
FROM SalesOrder
WHERE OrderDate >= '2025-01-01' AND OrderDate < '2026-01-01';

--##ITEM | Orders with total amount from 10 to 40 million
--##DESC | Filter orders by value range using BETWEEN.
SELECT OrderID, TotalAmount, OrderStatus
FROM SalesOrder
WHERE TotalAmount BETWEEN 10000000 AND 40000000;

--##ITEM | Payments by credit card
--##DESC | Filter payment transactions made by Credit Card.
SELECT PaymentID, Amount, PaymentDate
FROM Payment
WHERE PaymentMethod = N'Credit Card';

--##ITEM | Service requests not yet completed
--##DESC | Filter service requests not in Completed status (using IN for multiple statuses).
SELECT RequestID, RobotID, Status, RequestDate
FROM ServiceRequest
WHERE Status IN (N'Pending', N'Assigned', N'In Progress');

--##ITEM | Warranties still valid
--##DESC | Filter warranty registrations whose end date has not passed the current date.
SELECT WarrantyID, RobotID, StartDate, EndDate
FROM WarrantyRegistration
WHERE EndDate >= CAST(GETDATE() AS DATE);

--##ITEM | Device logs with an error code
--##DESC | Filter IoT logs that actually recorded an error code (ErrorCode IS NOT NULL).
SELECT LogID, RobotID, LogTime, ErrorCode
FROM DeviceLog
WHERE ErrorCode IS NOT NULL;

--##ITEM | Maintenance with a service fee
--##DESC | Filter maintenance records that actually incurred a service fee (> 0).
SELECT RecordID, RequestID, ServiceFee, CompletionDate
FROM MaintenanceRecord
WHERE ServiceFee > 0;

--##GROUP | Sorting with ORDER BY
--##ITEM | Robot models by descending price
--##DESC | Sort the catalog from most to least expensive.
SELECT ModelID, ModelName, UnitPrice
FROM RobotModel
ORDER BY UnitPrice DESC;

--##ITEM | Customers by name A-Z
--##DESC | Sort the customer list by full name ascending.
SELECT CustomerID, FullName
FROM Customer
ORDER BY FullName ASC;

--##ITEM | Q1/2025 orders by descending date
--##DESC | List orders in Q1 2025, sorted by order date descending.
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE OrderDate >= '2025-01-01' AND OrderDate < '2025-04-01'
ORDER BY OrderDate DESC;

--##ITEM | Employees by role then by name
--##DESC | Sort employees by Role, and by name within the same role.
SELECT FullName, Role
FROM Employee
ORDER BY Role ASC, FullName ASC;

--##ITEM | Robots by status then by model
--##DESC | Sort robots by Status, and by ModelID within the same status.
SELECT RobotID, ModelID, Status
FROM RobotUnit
ORDER BY Status ASC, ModelID ASC;

--##ITEM | Payments by descending amount
--##DESC | Sort transactions by amount from high to low.
SELECT PaymentID, Amount, PaymentMethod
FROM Payment
ORDER BY Amount DESC;

--##ITEM | Service requests by ascending date
--##DESC | Sort service requests by submission date ascending.
SELECT RequestID, RobotID, RequestDate, Status
FROM ServiceRequest
ORDER BY RequestDate ASC;

--##ITEM | Maintenance by descending fee
--##DESC | Sort maintenance records by service fee descending.
SELECT RecordID, RequestID, ServiceFee
FROM MaintenanceRecord
ORDER BY ServiceFee DESC;

--##ITEM | Warranties by expiry date
--##DESC | Sort warranty registrations by end date ascending.
SELECT WarrantyID, RobotID, EndDate
FROM WarrantyRegistration
ORDER BY EndDate ASC;

--##ITEM | Latest device logs first
--##DESC | Sort IoT logs by time descending (latest first).
SELECT LogID, RobotID, LogTime, ErrorCode
FROM DeviceLog
ORDER BY LogTime DESC;

--##GROUP | Aggregate functions (COUNT, SUM, AVG, MAX, MIN)
--##ITEM | Total number of customers
--##DESC | Count the total number of customers in the system.
SELECT COUNT(*) AS TotalCustomers FROM Customer;

--##ITEM | Total number of robots in stock
--##DESC | Count the total number of robot units managed.
SELECT COUNT(*) AS TotalRobots FROM RobotUnit;

--##ITEM | Average robot model price
--##DESC | Compute the average selling price of robot models.
SELECT AVG(UnitPrice) AS AvgPrice FROM RobotModel;

--##ITEM | Highest and lowest price
--##DESC | Find the highest and lowest selling price in the catalog.
SELECT MAX(UnitPrice) AS MaxPrice, MIN(UnitPrice) AS MinPrice FROM RobotModel;

--##ITEM | Total revenue by orders
--##DESC | Compute the total value of all orders (by the TotalAmount column).
SELECT SUM(TotalAmount) AS TotalRevenue FROM SalesOrder;

--##ITEM | Number of robots ready to sell
--##DESC | Count robots currently in Available status.
SELECT COUNT(*) AS AvailableRobots
FROM RobotUnit
WHERE Status = N'Available';

--##ITEM | Total amount actually collected from payments
--##DESC | Compute the total amount collected across all payment transactions.
SELECT SUM(Amount) AS TotalCollected FROM Payment;

--##ITEM | Average service fee
--##DESC | Compute the average maintenance fee across records.
SELECT AVG(ServiceFee) AS AvgServiceFee FROM MaintenanceRecord;

--##ITEM | Highest service fee
--##DESC | Find the highest maintenance fee ever incurred.
SELECT MAX(ServiceFee) AS MaxServiceFee FROM MaintenanceRecord;

--##ITEM | Total number of service requests
--##DESC | Count the total number of maintenance/repair requests.
SELECT COUNT(*) AS TotalRequests FROM ServiceRequest;

--##ITEM | Number of logs recording an error
--##DESC | Count IoT logs that have an error code (ignoring NULL logs).
SELECT COUNT(ErrorCode) AS ErrorLogCount FROM DeviceLog;

--##SECTION | Intermediate SQL Queries

--##GROUP | Joining multiple tables (INNER JOIN / LEFT JOIN)
--##ITEM | Orders with customer and salesperson names
--##DESC | INNER JOIN the three tables SalesOrder, Customer, Employee to display the customer and the handling employee.
SELECT so.OrderID, c.FullName AS Customer, e.FullName AS SalesStaff,
       so.OrderDate, so.TotalAmount, so.OrderStatus
FROM SalesOrder so
JOIN Customer c ON so.CustomerID = c.CustomerID
JOIN Employee e ON so.EmployeeID = e.EmployeeID;

--##ITEM | Sold robots with model and selling price
--##DESC | JOIN OrderDetail -> RobotUnit -> RobotModel to know which model each sold robot belongs to.
SELECT od.OrderID, od.RobotID, rm.Brand, rm.ModelName, od.SellingPrice
FROM OrderDetail od
JOIN RobotUnit ru ON od.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID
ORDER BY od.OrderID;

--##ITEM | Robots in stock with model information
--##DESC | JOIN RobotUnit with RobotModel to display brand, model name, and status.
SELECT ru.RobotID, ru.SerialNumber, rm.Brand, rm.ModelName, ru.Status
FROM RobotUnit ru
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;

--##ITEM | Service requests with customer name and robot serial
--##DESC | JOIN ServiceRequest with Customer and RobotUnit to see who submitted a request for which robot.
SELECT sr.RequestID, c.FullName AS Customer, ru.SerialNumber,
       sr.IssueDescription, sr.Status
FROM ServiceRequest sr
JOIN Customer c ON sr.CustomerID = c.CustomerID
JOIN RobotUnit ru ON sr.RobotID = ru.RobotID;

--##ITEM | Maintenance history with the assigned technician
--##DESC | JOIN MaintenanceRecord -> ServiceRequest -> Employee to link each maintenance with its issue and technician.
SELECT mr.RecordID, sr.IssueDescription, e.FullName AS Technician,
       mr.ServiceFee, mr.CompletionDate
FROM MaintenanceRecord mr
JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
JOIN Employee e ON mr.TechnicianID = e.EmployeeID;

--##ITEM | Full warranty information
--##DESC | JOIN WarrantyRegistration with RobotUnit, RobotModel and Customer.
SELECT w.WarrantyID, c.FullName AS Owner, rm.ModelName,
       ru.SerialNumber, w.StartDate, w.EndDate
FROM WarrantyRegistration w
JOIN RobotUnit ru ON w.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID
JOIN Customer c ON w.CustomerID = c.CustomerID;

--##ITEM | Payments for sales orders
--##DESC | JOIN Payment -> OrderPayment -> SalesOrder to know which order each payment corresponds to.
SELECT p.PaymentID, p.Amount, p.PaymentMethod, op.OrderID, so.OrderStatus
FROM Payment p
JOIN OrderPayment op ON p.PaymentID = op.PaymentID
JOIN SalesOrder so ON op.OrderID = so.OrderID;

--##ITEM | Payments for maintenance services
--##DESC | JOIN Payment -> ServicePayment -> MaintenanceRecord to see which maintenance a payment is for.
SELECT p.PaymentID, p.Amount, p.PaymentMethod, sp.ServiceRecordID, mr.ActionsTaken
FROM Payment p
JOIN ServicePayment sp ON p.PaymentID = sp.PaymentID
JOIN MaintenanceRecord mr ON sp.ServiceRecordID = mr.RecordID;

--##ITEM | IoT logs with robot model
--##DESC | JOIN DeviceLog -> RobotUnit -> RobotModel to associate each log with its robot model.
SELECT dl.LogID, rm.ModelName, dl.LogTime, dl.ErrorCode
FROM DeviceLog dl
JOIN RobotUnit ru ON dl.RobotID = ru.RobotID
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;

--##ITEM | All orders including those without line items (LEFT JOIN)
--##DESC | LEFT JOIN SalesOrder with OrderDetail: cancelled orders (without line items) still appear with NULL values.
SELECT so.OrderID, so.OrderStatus, od.RobotID, od.SellingPrice
FROM SalesOrder so
LEFT JOIN OrderDetail od ON so.OrderID = od.OrderID
ORDER BY so.OrderID;

--##ITEM | All customers including those who never bought (LEFT JOIN)
--##DESC | LEFT JOIN Customer with SalesOrder to also find customers who have not placed any order.
SELECT c.CustomerID, c.FullName, so.OrderID
FROM Customer c
LEFT JOIN SalesOrder so ON c.CustomerID = so.CustomerID
ORDER BY c.CustomerID;

--##ITEM | Features of each robot model
--##DESC | JOIN RobotModel with ModelFeature to list features per model.
SELECT rm.ModelName, mf.Feature
FROM RobotModel rm
JOIN ModelFeature mf ON rm.ModelID = mf.ModelID
ORDER BY rm.ModelName;

--##GROUP | Grouping with GROUP BY and HAVING
--##ITEM | Number of robots per model
--##DESC | GROUP BY ModelID, counting the robot units in stock for each model.
SELECT rm.ModelName, COUNT(ru.RobotID) AS Quantity
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
GROUP BY rm.ModelName;

--##ITEM | Number of robots per status
--##DESC | GROUP BY Status to summarize inventory (Available/Sold/Under Maintenance/Retired).
SELECT Status, COUNT(*) AS Quantity
FROM RobotUnit
GROUP BY Status;

--##ITEM | Total spending per customer
--##DESC | GROUP BY customer, computing the total order value.
SELECT c.FullName, SUM(so.TotalAmount) AS TotalSpending
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
ORDER BY TotalSpending DESC;

--##ITEM | Number of orders handled by each salesperson
--##DESC | GROUP BY employee to count the orders handled.
SELECT e.FullName, COUNT(so.OrderID) AS OrderCount
FROM Employee e
JOIN SalesOrder so ON e.EmployeeID = so.EmployeeID
GROUP BY e.FullName;

--##ITEM | Total sales per order (from line items)
--##DESC | GROUP BY OrderID on OrderDetail to compute the actual total selling price of each order.
SELECT OrderID, COUNT(RobotID) AS RobotCount, SUM(SellingPrice) AS TotalAmount
FROM OrderDetail
GROUP BY OrderID;

--##ITEM | Number of robots sold per model
--##DESC | JOIN and GROUP BY to count the robots sold for each model.
SELECT rm.ModelName, COUNT(od.RobotID) AS SoldCount
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
JOIN OrderDetail od ON ru.RobotID = od.RobotID
GROUP BY rm.ModelName;

--##ITEM | Number of service requests per status
--##DESC | GROUP BY Status on ServiceRequest.
SELECT Status, COUNT(*) AS RequestCount
FROM ServiceRequest
GROUP BY Status;

--##ITEM | Robots sent for maintenance 2 or more times (HAVING)
--##DESC | GROUP BY RobotID and filter HAVING COUNT >= 2 to find robots that frequently fail.
SELECT RobotID, COUNT(*) AS RequestCount
FROM ServiceRequest
GROUP BY RobotID
HAVING COUNT(*) >= 2;

--##ITEM | Total service fee per technician
--##DESC | GROUP BY technician to compute the total fee earned from maintenance work.
SELECT e.FullName, COUNT(mr.RecordID) AS MaintenanceCount, SUM(mr.ServiceFee) AS TotalFee
FROM Employee e
JOIN MaintenanceRecord mr ON e.EmployeeID = mr.TechnicianID
GROUP BY e.FullName;

--##ITEM | Number of features per robot model
--##DESC | GROUP BY model to count features.
SELECT rm.ModelName, COUNT(mf.Feature) AS FeatureCount
FROM RobotModel rm
JOIN ModelFeature mf ON rm.ModelID = mf.ModelID
GROUP BY rm.ModelName;

--##ITEM | Robot models with more than 2 units in stock (HAVING)
--##DESC | GROUP BY model and filter HAVING COUNT > 2.
SELECT rm.ModelName, COUNT(ru.RobotID) AS Quantity
FROM RobotModel rm
JOIN RobotUnit ru ON rm.ModelID = ru.ModelID
GROUP BY rm.ModelName
HAVING COUNT(ru.RobotID) > 2;

--##ITEM | Customers spending more than 30 million (HAVING)
--##DESC | GROUP BY customer and use HAVING to filter big spenders.
SELECT c.FullName, SUM(so.TotalAmount) AS TotalSpending
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
HAVING SUM(so.TotalAmount) > 30000000;

--##GROUP | Subqueries in WHERE / FROM
--##ITEM | Customers who have placed an order
--##DESC | Subquery in IN: get customers whose CustomerID appears in SalesOrder.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID IN (SELECT CustomerID FROM SalesOrder);

--##ITEM | Robots never sold
--##DESC | Subquery NOT IN: robots not present in the OrderDetail table.
SELECT RobotID, SerialNumber, Status
FROM RobotUnit
WHERE RobotID NOT IN (SELECT RobotID FROM OrderDetail);

--##ITEM | Robot models more expensive than average
--##DESC | Scalar subquery: compare UnitPrice with the catalog-wide average price.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice > (SELECT AVG(UnitPrice) FROM RobotModel);

--##ITEM | Orders larger than the average value
--##DESC | Scalar subquery computing AVG(TotalAmount) then filtering.
SELECT OrderID, TotalAmount
FROM SalesOrder
WHERE TotalAmount > (SELECT AVG(TotalAmount) FROM SalesOrder);

--##ITEM | Customers who never requested service
--##DESC | Subquery NOT IN on ServiceRequest.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID NOT IN (SELECT CustomerID FROM ServiceRequest);

--##ITEM | Employees who have performed maintenance
--##DESC | Subquery IN on MaintenanceRecord to get technicians who have work.
SELECT EmployeeID, FullName, Role
FROM Employee
WHERE EmployeeID IN (SELECT TechnicianID FROM MaintenanceRecord);

--##ITEM | Revenue per customer (subquery in FROM)
--##DESC | Use a derived table to compute total spending then filter spenders > 20 million.
SELECT t.CustomerID, c.FullName, t.TotalSpending
FROM (SELECT CustomerID, SUM(TotalAmount) AS TotalSpending
      FROM SalesOrder GROUP BY CustomerID) t
JOIN Customer c ON t.CustomerID = c.CustomerID
WHERE t.TotalSpending > 20000000;

--##ITEM | Robots currently under warranty
--##DESC | Subquery IN on WarrantyRegistration.
SELECT RobotID, SerialNumber
FROM RobotUnit
WHERE RobotID IN (SELECT RobotID FROM WarrantyRegistration);

--##ITEM | The most recent order
--##DESC | Scalar subquery using MAX(OrderDate) in WHERE.
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE OrderDate = (SELECT MAX(OrderDate) FROM SalesOrder);

--##ITEM | Robot models with no "Available" unit left
--##DESC | Subquery NOT IN: models not appearing in the set of models that have Available robots.
SELECT ModelID, ModelName
FROM RobotModel
WHERE ModelID NOT IN (
    SELECT ModelID FROM RobotUnit WHERE Status = N'Available'
);

--##SECTION | Advanced SQL Queries

--##GROUP | Nested subqueries
--##ITEM | Customers who bought a Roborock-brand robot
--##DESC | 4-level nesting: RobotModel(Brand) -> RobotUnit -> OrderDetail -> SalesOrder to trace back to the customer.
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

--##ITEM | Robots that incurred a paid maintenance cost
--##DESC | 2-level nesting: MaintenanceRecord(fee>0) -> ServiceRequest to get the RobotID.
SELECT RobotID, SerialNumber
FROM RobotUnit
WHERE RobotID IN (
    SELECT RobotID FROM ServiceRequest
    WHERE RequestID IN (
        SELECT RequestID FROM MaintenanceRecord WHERE ServiceFee > 0
    )
);

--##ITEM | Technicians who repaired an iRobot-brand robot
--##DESC | Multi-level nesting through MaintenanceRecord -> ServiceRequest -> RobotUnit -> RobotModel(Brand).
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

--##ITEM | Robot models that currently have a unit under maintenance
--##DESC | Nested subquery: get the ModelID of robots with Status = Under Maintenance.
SELECT ModelID, ModelName
FROM RobotModel
WHERE ModelID IN (
    SELECT ModelID FROM RobotUnit WHERE Status = N'Under Maintenance'
);

--##ITEM | Customers with a robot still under valid warranty
--##DESC | Nested subquery combining the expiry-date condition with the list of sold robots.
SELECT CustomerID, FullName
FROM Customer
WHERE CustomerID IN (
    SELECT CustomerID FROM WarrantyRegistration
    WHERE EndDate >= CAST(GETDATE() AS DATE)
);

--##ITEM | Customers spending above the average per customer
--##DESC | Nested subquery: compare a customer's total spending with the average of all customers' total spending.
SELECT c.FullName, SUM(so.TotalAmount) AS TotalSpending
FROM Customer c
JOIN SalesOrder so ON c.CustomerID = so.CustomerID
GROUP BY c.FullName
HAVING SUM(so.TotalAmount) > (
    SELECT AVG(t.Total)
    FROM (SELECT SUM(TotalAmount) AS Total FROM SalesOrder GROUP BY CustomerID) t
);

--##GROUP | Using EXISTS, IN, ANY/ALL
--##ITEM | Customers who have placed an order (EXISTS)
--##DESC | Correlated EXISTS: at least one order exists for the customer under consideration.
SELECT c.CustomerID, c.FullName
FROM Customer c
WHERE EXISTS (SELECT 1 FROM SalesOrder so WHERE so.CustomerID = c.CustomerID);

--##ITEM | Robots that have never sent any IoT log (NOT EXISTS)
--##DESC | NOT EXISTS: no log row exists in DeviceLog for the robot under consideration.
SELECT ru.RobotID, ru.SerialNumber
FROM RobotUnit ru
WHERE NOT EXISTS (SELECT 1 FROM DeviceLog dl WHERE dl.RobotID = ru.RobotID);

--##ITEM | Robot models with at least 1 available unit (correlated EXISTS)
--##DESC | Correlated EXISTS with RobotUnit Status = Available.
SELECT rm.ModelID, rm.ModelName
FROM RobotModel rm
WHERE EXISTS (
    SELECT 1 FROM RobotUnit ru
    WHERE ru.ModelID = rm.ModelID AND ru.Status = N'Available'
);

--##ITEM | Robots sold in delivered orders (IN)
--##DESC | IN with a subquery filtering OrderIDs that have Delivered status.
SELECT RobotID, OrderID, SellingPrice
FROM OrderDetail
WHERE OrderID IN (SELECT OrderID FROM SalesOrder WHERE OrderStatus = N'Delivered');

--##ITEM | Robot models more expensive than at least 1 Roborock model (ANY)
--##DESC | > ANY: price greater than the lowest price among Roborock models.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice > ANY (SELECT UnitPrice FROM RobotModel WHERE Brand = N'Roborock');

--##ITEM | The most expensive robot model (>= ALL)
--##DESC | >= ALL: price greater than or equal to all others, i.e. the most expensive model.
SELECT ModelName, UnitPrice
FROM RobotModel
WHERE UnitPrice >= ALL (SELECT UnitPrice FROM RobotModel);

--##GROUP | Set operations (UNION / INTERSECT / EXCEPT)
--##ITEM | Combined contact directory (UNION)
--##DESC | UNION merges names + emails of customers and employees into a single directory (duplicates removed).
SELECT FullName, Email, N'Customer' AS ContactType FROM Customer
UNION
SELECT FullName, Email, N'Employee' FROM Employee;

--##ITEM | Customer IDs with any transaction (UNION)
--##DESC | UNION of CustomerIDs from orders and from service requests (union of the two sets).
SELECT CustomerID FROM SalesOrder
UNION
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Customers who both bought and requested service (INTERSECT)
--##DESC | INTERSECT: the intersection of customers with orders and customers with service requests.
SELECT CustomerID FROM SalesOrder
INTERSECT
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Customers who bought but never requested service (EXCEPT)
--##DESC | EXCEPT: customers with orders minus customers with service requests.
SELECT CustomerID FROM SalesOrder
EXCEPT
SELECT CustomerID FROM ServiceRequest;

--##ITEM | Robots never sold (EXCEPT)
--##DESC | EXCEPT: all robots minus robots that appear in OrderDetail.
SELECT RobotID FROM RobotUnit
EXCEPT
SELECT RobotID FROM OrderDetail;

--##ITEM | Robots with both IoT logs and service requests (INTERSECT)
--##DESC | INTERSECT: the intersection of robots with logs and robots with service requests.
SELECT RobotID FROM DeviceLog
INTERSECT
SELECT RobotID FROM ServiceRequest;







--##SECTION | User-defined Functions-------------------------------------------------------------------

--##ITEM | fn_GetWarrantyStatus - check the warranty status of a robot
GO
CREATE FUNCTION dbo.fn_GetWarrantyStatus (@RobotID INT)
RETURNS NVARCHAR(30)
AS
BEGIN
    DECLARE @End DATE;
    SELECT @End = EndDate FROM WarrantyRegistration WHERE RobotID = @RobotID;
    IF @End IS NULL
        RETURN N'No Warranty';
    IF @End >= CAST(GETDATE() AS DATE)
        RETURN N'Under Warranty';
    RETURN N'Warranty Expired';
END;
GO
-- Demo: apply the function to each sold robot
SELECT RobotID, SerialNumber, dbo.fn_GetWarrantyStatus(RobotID) AS WarrantyStatus
FROM RobotUnit
WHERE Status <> N'Available';

--##ITEM | fn_GetCustomerTotalSpending - total spending of a customer
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
-- Demo: list the total spending of each customer
SELECT CustomerID, FullName, dbo.fn_GetCustomerTotalSpending(CustomerID) AS TotalSpending
FROM Customer
ORDER BY TotalSpending DESC;

--##ITEM | fn_GetModelAvailableUnits - number of available units of a model
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
-- Demo: available stock per model
SELECT ModelID, ModelName, dbo.fn_GetModelAvailableUnits(ModelID) AS AvailableUnits
FROM RobotModel;

--##ITEM | fn_GetMaintenanceHistoryByRobot - maintenance history of a robot (table-valued)

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
-- Demo: maintenance history of robot RobotID = 1
SELECT * FROM dbo.fn_GetMaintenanceHistoryByRobot(1);

--##SECTION | Stored Procedures

--##ITEM | sp_CreateSalesOrder - create a complete sales order
--##DESC | A multi-step procedure within a transaction: check that the robot is available, create the SalesOrder, add OrderDetail, create Payment and OrderPayment, and update inventory (robot -> Sold). Uses TRY/CATCH to roll back on error.
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
        BEGIN
            -- RAISERROR requires (Message, Severity, State)
            RAISERROR (N'Robot does not exist or is no longer available for sale.', 16, 1);
            ROLLBACK TRANSACTION; -- Must rollback manually if we don't jump to CATCH
            RETURN;
        END

        DECLARE @OrderID INT, @PaymentID INT;

        INSERT INTO SalesOrder (CustomerID, EmployeeID, TotalAmount, OrderStatus)
        VALUES (@CustomerID, @EmployeeID, @SellingPrice, N'Confirmed');
        SET @OrderID = SCOPE_IDENTITY();

        INSERT INTO OrderDetail (RobotID, OrderID, SellingPrice)
        VALUES (@RobotID, @OrderID, @SellingPrice);

        UPDATE RobotUnit SET Status = N'Sold' WHERE RobotID = @RobotID;

        INSERT INTO Payment (Amount, PaymentMethod) VALUES (@SellingPrice, @PaymentMethod);
        SET @PaymentID = SCOPE_IDENTITY();
        INSERT INTO OrderPayment (PaymentID, OrderID) VALUES (@PaymentID, @OrderID);

        COMMIT TRANSACTION;
        SELECT @OrderID AS NewOrderID, @PaymentID AS NewPaymentID;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        -- Re-throw the original error info
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrSev INT = ERROR_SEVERITY();
        DECLARE @ErrState INT = ERROR_STATE();
        RAISERROR(@ErrMsg, @ErrSev, @ErrState);
    END CATCH
END;
GO
-- Demo: sell robot RobotID = 10 (Available) to customer 7
EXEC sp_CreateSalesOrder @CustomerID = 7, @EmployeeID = 1, @RobotID = 10,
                         @SellingPrice = 24000000, @PaymentMethod = N'Credit Card';

--##ITEM | sp_RegisterWarranty - register a warranty for a sold robot
--##DESC | A procedure that automatically computes the warranty end date from the model's duration (months). Prevents duplicate registration and checks that the robot exists.
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
    BEGIN
        RAISERROR (N'This robot already has a warranty registration.', 16, 1);
        RETURN;
    END
    DECLARE @Months INT;
    SELECT @Months = rm.WarrantyDuration
    FROM RobotUnit ru 
    JOIN RobotModel rm ON ru.ModelID = rm.ModelID
    WHERE ru.RobotID = @RobotID;
    IF @Months IS NULL
    BEGIN
        RAISERROR (N'Robot does not exist in the system.', 16, 1);
        RETURN;
    END
    DECLARE @End DATE = DATEADD(MONTH, @Months, @StartDate);
    INSERT INTO WarrantyRegistration (RobotID, CustomerID, StartDate, EndDate)
    VALUES (@RobotID, @CustomerID, @StartDate, @End);
    SELECT @RobotID AS RobotID, @StartDate AS StartDate, @End AS EndDate;
END;
GO
-- Demo: register a warranty for robot 10 just sold to customer 7
EXEC sp_RegisterWarranty @RobotID = 10, @CustomerID = 7;

--##ITEM | sp_CompleteMaintenance - record completion of maintenance
--##DESC | A multi-step procedure: check the service request, waive the fee if the robot is under warranty, create a MaintenanceRecord, update the request status to Completed, and create a service payment if a fee applies. Reuses the fn_GetWarrantyStatus function.
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

        -- Check if request exists
        IF NOT EXISTS (SELECT 1 FROM ServiceRequest WHERE RequestID = @RequestID)
        BEGIN
            RAISERROR (N'Service request does not exist.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Check if record already exists
        IF EXISTS (SELECT 1 FROM MaintenanceRecord WHERE RequestID = @RequestID)
        BEGIN
            RAISERROR (N'This request already has a maintenance record.', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END

        DECLARE @RobotID INT;
        SELECT @RobotID = RobotID FROM ServiceRequest WHERE RequestID = @RequestID;

        -- Calculate Fee based on warranty
        DECLARE @Fee DECIMAL(18,2) = @BaseFee;
        IF dbo.fn_GetWarrantyStatus(@RobotID) = N'Under Warranty'
            SET @Fee = 0;

        DECLARE @RecordID INT;
        INSERT INTO MaintenanceRecord (RequestID, TechnicianID, ActionsTaken, ServiceFee, CompletionDate)
        VALUES (@RequestID, @TechnicianID, @ActionsTaken, @Fee, GETDATE());
        SET @RecordID = SCOPE_IDENTITY();

        UPDATE ServiceRequest SET Status = N'Completed' WHERE RequestID = @RequestID;

        -- Process payment if fee is applicable
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
        
        -- Re-raise error using parameters retrieved from system functions
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrSev INT = ERROR_SEVERITY();
        DECLARE @ErrState INT = ERROR_STATE();
        RAISERROR(@ErrMsg, @ErrSev, @ErrState);
    END CATCH
END;
GO
-- Demo: complete service request RequestID = 7 (robot 6 under warranty -> free)
EXEC sp_CompleteMaintenance @RequestID = 7, @TechnicianID = 5,
                            @ActionsTaken = N'Inspect and reset error E150', @BaseFee = 500000;

--##ITEM | sp_GenerateSalesReport - revenue report by model & date range
--##DESC | A procedure that takes a date range and returns a summary report: units sold and revenue for each robot model within that range, sorted by revenue descending.
GO
CREATE PROCEDURE sp_GenerateSalesReport
    @FromDate DATE,
    @ToDate   DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT rm.Brand, rm.ModelName,
           COUNT(od.RobotID)      AS UnitsSold,
           SUM(od.SellingPrice)   AS Revenue
    FROM OrderDetail od
    JOIN SalesOrder so ON od.OrderID = so.OrderID
    JOIN RobotUnit  ru ON od.RobotID = ru.RobotID
    JOIN RobotModel rm ON ru.ModelID = rm.ModelID
    WHERE so.OrderDate >= @FromDate
      AND so.OrderDate <  DATEADD(DAY, 1, @ToDate)
    GROUP BY rm.Brand, rm.ModelName
    ORDER BY Revenue DESC;
END;
GO
-- Demo: revenue report from 01/2024 to 12/2025
EXEC sp_GenerateSalesReport @FromDate = '2024-01-01', @ToDate = '2025-12-31';

/*==============================================================================
  PART 6 - TRIGGERS
==============================================================================*/

--##SECTION | Triggers

--##ITEM | trg_RobotUnit_AuditStatus - automatically log robot status changes
--##DESC | An AFTER UPDATE trigger on RobotUnit: whenever the Status column changes, it automatically writes a row into the RobotStatusAudit table (old status -> new status, timestamp). It logs only when the values actually differ.
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
-- Demo: change robot 14 status (Available -> Retired) and view the audit log
UPDATE RobotUnit SET Status = N'Retired' WHERE RobotID = 14;
SELECT * FROM RobotStatusAudit WHERE RobotID = 14;

--##ITEM | trg_OrderDetail_AfterInsert - automatically update inventory after a sale
--##DESC | An AFTER INSERT trigger on OrderDetail: when a robot is added to an order line, it automatically changes that robot status to "Sold". This illustrates "updating inventory after a sales transaction".
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
-- Demo: sell robot 12 (Available) by inserting OrderDetail directly
SELECT RobotID, Status AS BeforeSale FROM RobotUnit WHERE RobotID = 12;
DECLARE @demoOrder INT;
INSERT INTO SalesOrder (CustomerID, EmployeeID, TotalAmount, OrderStatus)
VALUES (2, 2, 22000000, N'Confirmed');
SET @demoOrder = SCOPE_IDENTITY();
INSERT INTO OrderDetail (RobotID, OrderID, SellingPrice) VALUES (12, @demoOrder, 22000000);
SELECT RobotID, Status AS AfterSale FROM RobotUnit WHERE RobotID = 12;       -- -> Sold
SELECT * FROM RobotStatusAudit WHERE RobotID = 12;                           -- audit captured

--##ITEM | trg_PreventDeleteCustomerWithOrders - block deletion of referenced customers
--##DESC | An INSTEAD OF DELETE trigger on Customer: it disallows deleting a customer who still has orders or service requests (protecting parent records). It performs the delete only when valid.
GO
CREATE TRIGGER trg_PreventDeleteCustomerWithOrders ON Customer
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM SalesOrder so JOIN deleted d ON so.CustomerID = d.CustomerID)
       OR EXISTS (SELECT 1 FROM ServiceRequest sr JOIN deleted d ON sr.CustomerID = d.CustomerID)
    BEGIN
        RAISERROR(N'Cannot delete a customer who has orders or service requests.', 16, 1);
        RETURN;
    END
    DELETE FROM Customer WHERE CustomerID IN (SELECT CustomerID FROM deleted);
END;
GO
-- Demo: (a) delete an unreferenced customer -> success; (b) delete a customer with orders -> blocked
INSERT INTO Customer (FullName, PhoneNumber, Email, Password)
VALUES (N'Temp Demo Customer', '0900000099', N'tam.demo@gmail.com', N'temp');
DELETE FROM Customer WHERE Email = N'tam.demo@gmail.com';   -- success
BEGIN TRY
    DELETE FROM Customer WHERE CustomerID = 1;               -- blocked by trigger
END TRY
BEGIN CATCH
    PRINT ERROR_MESSAGE();
END CATCH

--##ITEM | trg_Maintenance_AfterInsert - apply the warranty rule & sync status
--##DESC | An AFTER INSERT trigger on MaintenanceRecord: (1) if the robot is under warranty at the time of maintenance, it automatically sets ServiceFee = 0 (free under warranty); (2) when a completion date is present, it automatically updates the related service request to Completed status.
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
-- Demo: create a new request for robot 6 (under warranty) then insert a maintenance record with fee 600000
DECLARE @demoReq INT;
INSERT INTO ServiceRequest (RobotID, CustomerID, IssueDescription, Status)
VALUES (6, 4, N'Periodic maintenance within warranty', N'Assigned');
SET @demoReq = SCOPE_IDENTITY();
INSERT INTO MaintenanceRecord (RequestID, TechnicianID, ActionsTaken, ServiceFee, CompletionDate)
VALUES (@demoReq, 5, N'Periodic maintenance', 600000, GETDATE());
-- Result: ServiceFee set to 0 by the trigger (under warranty) and the request -> Completed
SELECT mr.RecordID, mr.ServiceFee, sr.Status
FROM MaintenanceRecord mr JOIN ServiceRequest sr ON mr.RequestID = sr.RequestID
WHERE mr.RequestID = @demoReq;

/*==============================================================================
  PART 7 - VIEWS & INDEXES
==============================================================================*/

--##SECTION | Views and Indexes

--##GROUP | Views - simplifying complex queries
--##ITEM | vw_RobotInventory - robot inventory with warranty status
--##DESC | A view aggregating each robot with brand, model name, price, status and warranty status (calling fn_GetWarrantyStatus). Helps staff look up inventory quickly.
GO
CREATE VIEW vw_RobotInventory AS
SELECT ru.RobotID, ru.SerialNumber, rm.Brand, rm.ModelName, rm.UnitPrice,
       ru.Status, dbo.fn_GetWarrantyStatus(ru.RobotID) AS WarrantyStatus
FROM RobotUnit ru
JOIN RobotModel rm ON ru.ModelID = rm.ModelID;
GO
-- Demo: view the entire inventory through the view
SELECT * FROM vw_RobotInventory ORDER BY RobotID;

--##ITEM | vw_SalesOrderSummary - order summary with amount paid
--##DESC | A view joining orders with customer name, salesperson and total amount paid (subquery), enabling sales reporting without rewriting complex JOINs.
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
-- Demo: view the summary of all orders
SELECT * FROM vw_SalesOrderSummary ORDER BY OrderID;

--##ITEM | vw_MaintenanceDetails - full maintenance details
--##DESC | A view joining maintenance records with robot, customer, technician and issue description, helping managers track maintenance history by querying a single object.
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
-- Demo: view maintenance details through the view
SELECT * FROM vw_MaintenanceDetails ORDER BY RecordID;

--##GROUP | Indexes - speeding up queries
--##ITEM | IX_RobotUnit_Status - single-column index
--##DESC | A non-clustered index on the single column RobotUnit(Status), speeding up queries that filter/aggregate by inventory status (e.g. counting Available robots).
GO
CREATE NONCLUSTERED INDEX IX_RobotUnit_Status ON RobotUnit(Status);
GO
-- Demo: a query that benefits from this index (see the Actual Execution Plan in SSMS)
SELECT RobotID, ModelID, SerialNumber FROM RobotUnit WHERE Status = N'Available';

--##ITEM | IX_SalesOrder_Customer_Date - composite index
--##DESC | A composite index on SalesOrder(CustomerID, OrderDate) optimizing queries that look up a customer orders within a date range (e.g. purchase history).
GO
CREATE NONCLUSTERED INDEX IX_SalesOrder_Customer_Date ON SalesOrder(CustomerID, OrderDate);
GO
-- Demo: a query filtering by customer and time
SELECT OrderID, OrderDate, TotalAmount
FROM SalesOrder
WHERE CustomerID = 1 AND OrderDate >= '2024-01-01'
ORDER BY OrderDate;

--##ITEM | IX_ServiceRequest_Status - single-column index (additional)
--##DESC | An index on ServiceRequest(Status) helps speed up listing service requests awaiting handling (Pending/Assigned/In Progress).
GO
CREATE NONCLUSTERED INDEX IX_ServiceRequest_Status ON ServiceRequest(Status);
GO
-- Demo
SELECT RequestID, RobotID, RequestDate FROM ServiceRequest WHERE Status = N'Pending';

