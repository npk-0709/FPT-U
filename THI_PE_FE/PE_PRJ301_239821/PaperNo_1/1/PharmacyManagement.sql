USE [master]
GO
-- 0. Xóa Database PharmacyManagement nếu đã tồn tại trong DBMS --------------------
IF EXISTS (
    SELECT name 
    FROM sys.databases 
    WHERE name = N'PharmacyManagement'
)
BEGIN
    -- Tùy chọn: đưa DB về SINGLE_USER để tránh lỗi đang có kết nối
    ALTER DATABASE [PharmacyManagement] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    
    -- Xóa database
    DROP DATABASE [PharmacyManagement];
END
GO

/****** Object:  Database [PharmacyManagement]    Script Date: 21/09/2026 08:21:35 ******/
CREATE DATABASE [PharmacyManagement]
GO

USE [PharmacyManagement]
GO

-- Create tables for storing data ----------------------------------------------------------
CREATE TABLE [dbo].[User](
	[userID] [nvarchar](25) NOT NULL,
	[password] [nvarchar](79) NOT NULL,
	[fullName] [nvarchar](168) NOT NULL,
	[gender] bit default 1,
	[birthDay] Date not null,
	[roleID] [nvarchar](35) default 'TV',
	[active] [bit] default 0,
 CONSTRAINT [PK_Users] PRIMARY KEY CLUSTERED 
(
	[userID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO


CREATE TABLE [dbo].[Medicine](
	[medCode] [char](7) NOT NULL,					--- Code: MED#### - Validation type
	[nameOfMedicine] [nvarchar](100) NOT NULL,
	[quantity] [int] NOT NULL,
	[price] [float] NOT NULL,
	[expiryDate] [datetime] NOT NULL,
	[status] [int] NOT NULL default 0,				---  -1: Discontinued (No longer sold by the business) 
													---   0: Inactive (Temporarily unavailable for sale) 
													---   1: Active (Currently available for sale)
 CONSTRAINT [PK_Medicine] PRIMARY KEY CLUSTERED 
(
	[medCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- Insert data into the tables used by the application.
INSERT INTO [dbo].[User] ([userID],[password],[fullName],[gender],[birthDay],[roleID],[active]) VALUES
(N'admin',N'1',N'Administrator',1,'1997-02-10',N'QT',1),
(N'user01',N'1',N'Nguyen Quang Minh',1,'1997-12-09',N'TV',1),
(N'user02',N'1',N'Tran Thi Lan Thanh',0,'1998-03-08',N'TV',1),
(N'user03',N'1',N'Le Hoang Duc',1,'1992-10-08',N'TV',1),
(N'user04',N'1',N'Pham Thu Hong',0,'1999-11-20',N'TV',0),
(N'user05',N'1',N'Hoang Quoc Nam',1,'2000-01-12',N'TV',0),
(N'member1',N'1',N'Vo Thi Mai Anh',0,'1994-10-20',N'TV',1),
(N'member2',N'1',N'Bui Minh Khang',1,'1996-05-14',N'TV',1),
(N'user08',N'1',N'Doan Thi Ngoc',0,'1995-07-22',N'QT',0),
(N'user09',N'1',N'Dang Quoc Huy',1,'1993-09-18',N'TV',1),
(N'user10',N'1',N'Phan Thi My Linh',0,'2001-02-28',N'TV',1),
(N'user11',N'1',N'Nguyen Thanh Tung',1,'1991-06-11',N'TV',0),
(N'user12',N'1',N'Tran Hoai Nam',1,'1998-12-03',N'TV',1),
(N'user13',N'1',N'Le Thi Thanh Tam',0,'1997-04-25',N'TV',1),
(N'manager',N'1',N'Pham Van Long',1,'1990-08-19',N'QT',0),
(N'user15',N'1',N'Huynh Thi Kim Oanh',0,'1996-10-07',N'TV',1),
(N'user16',N'1',N'Nguyen Duc Anh',1,'2000-03-16',N'TV',1),
(N'user17',N'1',N'Tran Ngoc Han',0,'1999-01-29',N'TV',0),
(N'user18',N'1',N'Vo Quang Hieu',1,'1994-11-13',N'TV',1),
(N'user19',N'1',N'Dang Thi Phuong',0,'1993-05-06',N'TV',1),
(N'user20',N'1',N'Hoang Minh Tri',1,'2001-09-21',N'TV',0),
(N'supervisor',N'1',N'Do Thi Mai',0,'1995-12-17',N'QT',1),
(N'user22',N'1',N'Bui Quoc Viet',1,'1992-02-09',N'TV',1),
(N'user23',N'1',N'Pham Ngoc Anh',0,'1998-06-30',N'TV',0),
(N'user24',N'1',N'Nguyen Gia Bao',1,'2000-10-15',N'TV',1),
(N'user25',N'1',N'Tran Minh Chau',0,'1997-07-12',N'TV',1);
GO

INSERT INTO [dbo].[Medicine] ([medCode],[nameOfMedicine],[quantity],[price],[expiryDate],[status]) VALUES
(N'MED0001',N'Paracetamol 500mg',0,5000,'2026-01-15',-1),
(N'MED0002',N'Amoxicillin 500mg',0,8000,'2026-03-20',-1),
(N'MED0003',N'Vitamin C 1000mg',0,12000,'2026-05-10',-1),
(N'MED0004',N'Ibuprofen 400mg',0,6500,'2026-06-30',-1),
(N'MED0005',N'Aspirin 500mg',0,4500,'2026-07-25',-1),
(N'MED0006',N'Cough Syrup 100ml',0,15000,'2026-08-15',-1),
(N'MED0007',N'Antibiotic Cream 15g',0,18000,'2026-09-01',-1),
(N'MED0008',N'Pain Relief Tablets',0,3500,'2026-09-10',-1),
(N'MED0009',N'Cold and Flu Syrup 100ml',35,14000,'2026-10-15',0),
(N'MED0010',N'Cetirizine 10mg',50,9000,'2026-11-20',0),
(N'MED0011',N'Loratadine 10mg',0,7500,'2026-12-05',0),
(N'MED0012',N'Omeprazole 20mg',80,22000,'2027-01-15',0),
(N'MED0013',N'Esomeprazole 40mg',60,30000,'2027-02-28',0),
(N'MED0014',N'Azithromycin 250mg',25,18000,'2027-03-20',0),
(N'MED0015',N'Cefuroxime 500mg',0,28000,'2027-04-15',0),
(N'MED0016',N'Metformin 500mg',90,6500,'2027-05-30',0),
(N'MED0017',N'Losartan 50mg',40,12000,'2027-06-18',0),
(N'MED0018',N'Amlodipine 5mg',0,9000,'2027-07-25',0),
(N'MED0019',N'Atorvastatin 20mg',70,25000,'2027-08-10',0),
(N'MED0020',N'Simvastatin 20mg',55,18000,'2027-09-30',0),
(N'MED0021',N'Domperidone 10mg',20,7000,'2027-10-15',0),
(N'MED0022',N'Loperamide 2mg',0,5500,'2027-11-20',0),
(N'MED0023',N'Oral Rehydration Salts',45,4000,'2027-12-10',0),
(N'MED0024',N'Vitamin B Complex',75,8500,'2028-01-15',1),
(N'MED0025',N'Calcium Carbonate 500mg',0,10000,'2028-02-20',1),
(N'MED0026',N'Magnesium Oxide 250mg',65,7000,'2028-03-15',1),
(N'MED0027',N'Diclofenac 50mg',30,6500,'2028-04-25',1),
(N'MED0028',N'Naproxen 250mg',0,8000,'2028-05-30',1),
(N'MED0029',N'Antacid Tablets',100,4500,'2028-06-18',1),
(N'MED0030',N'Salbutamol 4mg',50,6000,'2028-07-20',1),
(N'MED0031',N'Dextromethorphan 15mg',0,5500,'2028-08-15',1),
(N'MED0032',N'Ambroxol 30mg',85,7500,'2028-09-25',1),
(N'MED0033',N'Multivitamin Tablets',120,15000,'2028-10-10',1),
(N'MED0034',N'Iron Supplement 100mg',0,9000,'2028-11-20',1),
(N'MED0035',N'Folic Acid 5mg',40,5000,'2028-12-15',1),
(N'MED0036',N'Hydrocortisone Cream 1%',60,12000,'2029-01-25',1),
(N'MED0037',N'Clotrimazole Cream 1%',0,11000,'2029-02-20',1),
(N'MED0038',N'Mupirocin Ointment 2%',90,16000,'2029-03-15',1);
GO