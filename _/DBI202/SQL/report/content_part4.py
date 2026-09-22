"""Chuong 8-9 va cac phu luc."""

import docx_helpers as dh
from stats import ssn, vn_date, vn_float, vn_int

SQL_FK = """-- Bo sung khoa ngoai con thieu quan trong nhat
ALTER TABLE [dbo].[tblWorksOn]  WITH CHECK
    ADD CONSTRAINT [FK_tblWorksOn_tblEmployee]
    FOREIGN KEY ([empSSN]) REFERENCES [dbo].[tblEmployee] ([empSSN]);
GO

-- Khai bao hanh vi khi xoa / cap nhat ban ghi cha
ALTER TABLE [dbo].[tblDependent]
    DROP CONSTRAINT [FK_tblDependent_tblEmployee];
GO
ALTER TABLE [dbo].[tblDependent]  WITH CHECK
    ADD CONSTRAINT [FK_tblDependent_tblEmployee]
    FOREIGN KEY ([empSSN]) REFERENCES [dbo].[tblEmployee] ([empSSN])
    ON DELETE CASCADE ON UPDATE CASCADE;
GO"""

SQL_CHECK = """-- Toan ven mien gia tri: gioi tinh, luong, so gio lam
ALTER TABLE [dbo].[tblEmployee]
    ADD CONSTRAINT [CK_tblEmployee_empSex]
        CHECK ([empSex] IN ('M', 'F')),
        CONSTRAINT [CK_tblEmployee_empSalary]
        CHECK ([empSalary] > 0),
        CONSTRAINT [CK_tblEmployee_empBirthdate]
        CHECK ([empBirthdate] < [empStartdate]);
GO

ALTER TABLE [dbo].[tblDependent]
    ADD CONSTRAINT [CK_tblDependent_depSex]
        CHECK ([depSex] IN ('M', 'F'));
GO

ALTER TABLE [dbo].[tblWorksOn]
    ADD CONSTRAINT [CK_tblWorksOn_workHours]
        CHECK ([workHours] >= 0 AND [workHours] <= 168);
GO

-- Toan ven thuc the bo sung: khong trung ten phong ban, ten dia diem
ALTER TABLE [dbo].[tblDepartment]
    ADD CONSTRAINT [UQ_tblDepartment_depName] UNIQUE ([depName]);
GO
ALTER TABLE [dbo].[tblLocation]
    ADD CONSTRAINT [UQ_tblLocation_locName] UNIQUE ([locName]);
GO"""

SQL_NOTNULL = """-- Cac cot bat buoc ve nghiep vu khong duoc de trong
ALTER TABLE [dbo].[tblEmployee]
    ALTER COLUMN [empName] nvarchar(100) NOT NULL;
ALTER TABLE [dbo].[tblEmployee]
    ALTER COLUMN [empSalary] decimal(12, 2) NOT NULL;
ALTER TABLE [dbo].[tblDepartment]
    ALTER COLUMN [depName] nvarchar(100) NOT NULL;
ALTER TABLE [dbo].[tblLocation]
    ALTER COLUMN [locName] nvarchar(100) NOT NULL;
ALTER TABLE [dbo].[tblProject]
    ALTER COLUMN [proName] nvarchar(100) NOT NULL;
GO

-- Doi kieu ngay: datetime (8 byte) -> date (3 byte)
ALTER TABLE [dbo].[tblEmployee] ALTER COLUMN [empBirthdate] date;
ALTER TABLE [dbo].[tblEmployee] ALTER COLUMN [empStartdate] date;
ALTER TABLE [dbo].[tblDepartment] ALTER COLUMN [mgrAssDate] date;
ALTER TABLE [dbo].[tblDependent] ALTER COLUMN [depBirthdate] date;
GO"""

SQL_TRIGGER = """-- Quy tac BR04: ngay bo nhiem khong duoc som hon ngay vao lam
CREATE OR ALTER TRIGGER [trg_Department_CheckMgrDate]
ON [dbo].[tblDepartment]
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (
        SELECT 1
        FROM inserted i
             JOIN [dbo].[tblEmployee] e ON e.empSSN = i.mgrSSN
        WHERE i.mgrAssDate < e.empStartdate
    )
    BEGIN
        RAISERROR (N'Ngay bo nhiem truong phong khong duoc som hon ngay vao lam.',
                   16, 1);
        ROLLBACK TRANSACTION;
    END
END
GO

-- Quy tac BR09: nguoi giam sat phai cung phong ban
CREATE OR ALTER TRIGGER [trg_Employee_CheckSupervisorDept]
ON [dbo].[tblEmployee]
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (
        SELECT 1
        FROM inserted i
             JOIN [dbo].[tblEmployee] sup ON sup.empSSN = i.supervisorSSN
        WHERE i.depNum <> sup.depNum
    )
    BEGIN
        RAISERROR (N'Nguoi giam sat phai thuoc cung phong ban.', 16, 1);
        ROLLBACK TRANSACTION;
    END
END
GO

-- Quy tac BR13: truong phong phai la nhan vien cua chinh phong ban do
CREATE OR ALTER TRIGGER [trg_Department_MgrInSameDept]
ON [dbo].[tblDepartment]
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (
        SELECT 1
        FROM inserted i
             JOIN [dbo].[tblEmployee] e ON e.empSSN = i.mgrSSN
        WHERE e.depNum <> i.depNum
    )
    BEGIN
        RAISERROR (N'Truong phong phai truc thuoc phong ban minh quan ly.',
                   16, 1);
        ROLLBACK TRANSACTION;
    END
END
GO"""

SQL_INDEX = """-- Chi muc phu tren cac cot khoa ngoai thuong dung de noi bang
CREATE NONCLUSTERED INDEX [IX_tblEmployee_depNum]
    ON [dbo].[tblEmployee] ([depNum])
    INCLUDE ([empName], [empSalary]);

CREATE NONCLUSTERED INDEX [IX_tblEmployee_supervisorSSN]
    ON [dbo].[tblEmployee] ([supervisorSSN]);

CREATE NONCLUSTERED INDEX [IX_tblProject_depNum]
    ON [dbo].[tblProject] ([depNum]);

CREATE NONCLUSTERED INDEX [IX_tblWorksOn_proNum]
    ON [dbo].[tblWorksOn] ([proNum])
    INCLUDE ([workHours]);
GO"""

SQL_CLEAN = """-- 1. Chuan hoa khoang trang o hai dau cua du lieu van ban
UPDATE [dbo].[tblEmployee] SET empAddress = LTRIM(RTRIM(empAddress));
UPDATE [dbo].[tblEmployee] SET empName    = LTRIM(RTRIM(empName));

-- 2. Tham chieu hoa dia chi nhan vien
CREATE TABLE [dbo].[tblProvince] (
    provinceID   int IDENTITY(1, 1) PRIMARY KEY,
    provinceName nvarchar(100) NOT NULL UNIQUE
);
GO
INSERT INTO [dbo].[tblProvince] (provinceName)
SELECT DISTINCT LTRIM(RTRIM(empAddress))
FROM   [dbo].[tblEmployee]
WHERE  empAddress IS NOT NULL;
GO
ALTER TABLE [dbo].[tblEmployee] ADD provinceID int NULL;
GO
UPDATE e
SET    e.provinceID = p.provinceID
FROM   [dbo].[tblEmployee] e
       JOIN [dbo].[tblProvince] p
         ON p.provinceName = LTRIM(RTRIM(e.empAddress));
GO
ALTER TABLE [dbo].[tblEmployee]  WITH CHECK
    ADD CONSTRAINT [FK_tblEmployee_tblProvince]
    FOREIGN KEY ([provinceID]) REFERENCES [dbo].[tblProvince] ([provinceID]);
GO

-- 3. Sua cac ban ghi co ngay bo nhiem som hon ngay vao lam
UPDATE d
SET    d.mgrAssDate = e.empStartdate
FROM   [dbo].[tblDepartment] d
       JOIN [dbo].[tblEmployee] e ON e.empSSN = d.mgrSSN
WHERE  d.mgrAssDate < e.empStartdate;
GO"""

SQL_QC1 = """-- QC01. Ban ghi gio lam "mo coi" (khong ton tai nhan vien tuong ung)
SELECT w.*
FROM   tblWorksOn w
WHERE  NOT EXISTS (SELECT 1 FROM tblEmployee e WHERE e.empSSN = w.empSSN);

-- QC02. Gia tri gioi tinh nam ngoai mien cho phep
SELECT empSSN, empName, empSex FROM tblEmployee WHERE empSex NOT IN ('M', 'F');
SELECT depName, empSSN, depSex FROM tblDependent WHERE depSex NOT IN ('M', 'F');

-- QC03. Luong khong hop le
SELECT empSSN, empName, empSalary FROM tblEmployee WHERE empSalary <= 0;

-- QC04. Ngay bo nhiem truong phong som hon ngay vao lam
SELECT d.depNum, d.depName, e.empName,
       e.empStartdate AS NgayVaoLam, d.mgrAssDate AS NgayBoNhiem,
       DATEDIFF(DAY, d.mgrAssDate, e.empStartdate) AS SoNgayLech
FROM   tblDepartment d JOIN tblEmployee e ON e.empSSN = d.mgrSSN
WHERE  d.mgrAssDate < e.empStartdate;

-- QC05. Truong phong khong thuoc phong ban minh quan ly
SELECT d.depNum, d.depName, e.empName, e.depNum AS PhongThucTe
FROM   tblDepartment d JOIN tblEmployee e ON e.empSSN = d.mgrSSN
WHERE  e.depNum <> d.depNum;"""

SQL_QC2 = """-- QC06. Nguoi giam sat khac phong ban voi nhan vien
SELECT e.empSSN, e.empName, e.depNum, s.empName AS NguoiGiamSat, s.depNum
FROM   tblEmployee e JOIN tblEmployee s ON s.empSSN = e.supervisorSSN
WHERE  e.depNum <> s.depNum;

-- QC07. Dia diem chua duoc su dung
SELECT l.locNum, l.locName
FROM   tblLocation l
WHERE  NOT EXISTS (SELECT 1 FROM tblDepLocation dl WHERE dl.locNum = l.locNum)
  AND  NOT EXISTS (SELECT 1 FROM tblProject p     WHERE p.locNum  = l.locNum);

-- QC08. Nhan vien chua tham gia du an nao
SELECT e.empSSN, e.empName, d.depName
FROM   tblEmployee e LEFT JOIN tblDepartment d ON d.depNum = e.depNum
WHERE  NOT EXISTS (SELECT 1 FROM tblWorksOn w WHERE w.empSSN = e.empSSN);"""

SQL_QC3 = """-- QC09. Du lieu van ban con du khoang trang o hai dau
SELECT empSSN, empName, '[' + empAddress + ']' AS DiaChi
FROM   tblEmployee
WHERE  empAddress <> LTRIM(RTRIM(empAddress))
   OR  empName    <> LTRIM(RTRIM(empName));

-- QC10. Doi chieu tong so gio lam theo phong ban (dung LEFT JOIN
--       de khong bo sot phong ban chua co ai tham gia du an)
SELECT d.depNum, d.depName,
       COUNT(DISTINCT e.empSSN)      AS SoNhanVien,
       ISNULL(SUM(w.workHours), 0)   AS TongGioLam
FROM   tblDepartment d
       LEFT JOIN tblEmployee e ON e.depNum = d.depNum
       LEFT JOIN tblWorksOn  w ON w.empSSN = e.empSSN
GROUP  BY d.depNum, d.depName
ORDER  BY TongGioLam DESC;"""

ROADMAP = [
    ("1", "Bổ sung khoá ngoại `FK_tblWorksOn_tblEmployee`",
     "H01", "Rất cao", "Thấp", "Ngay lập tức"),
    ("2", "Thêm các ràng buộc `CHECK` cho giới tính, lương, số giờ",
     "H02", "Cao", "Thấp", "Ngay lập tức"),
    ("3", "Thêm `UNIQUE` cho `depName`, `locName`",
     "H05", "Trung bình", "Thấp", "Ngay lập tức"),
    ("4", "Khai báo `ON DELETE CASCADE` cho `tblDependent`",
     "H06", "Cao", "Thấp", "Ngắn hạn"),
    ("5", "Làm sạch dữ liệu địa chỉ và ngày bổ nhiệm sai",
     "D01, D03", "Cao", "Trung bình", "Ngắn hạn"),
    ("6", "Thêm chỉ mục phụ trên các cột khoá ngoại",
     "H10", "Trung bình", "Thấp", "Ngắn hạn"),
    ("7", "Viết trigger cho các quy tắc liên bảng",
     "BR04, BR09", "Trung bình", "Trung bình", "Trung hạn"),
    ("8", "Chuyển `datetime` sang `date`, siết `NOT NULL`",
     "H04, H07", "Trung bình", "Trung bình", "Trung hạn"),
    ("9", "Đổi kiểu mã định danh sang `char(11)`",
     "H03", "Cao", "Rất cao", "Dài hạn"),
    ("10", "Tách bảng `tblProvince`, dùng khoá thay thế cho `tblDependent`",
     "H09, mục 5.5", "Trung bình", "Cao", "Dài hạn"),
]


def build(ctx):
    doc, s, cap = ctx.doc, ctx.s, ctx.cap
    db = s["db"]

    # ================================================== CHUONG 8
    dh.heading(doc, "CHƯƠNG 8. ĐỀ XUẤT CẢI TIẾN", level=1, new_page=True)
    dh.para(
        doc,
        "Các đề xuất dưới đây được nhóm thành năm nhóm giải pháp, mỗi nhóm kèm "
        "mã lệnh T-SQL có thể áp dụng trực tiếp trên cơ sở dữ liệu "
        "`dbCOMPANY`. Thứ tự trình bày đi từ nhóm đem lại lợi ích cao nhất với "
        "chi phí thấp nhất đến nhóm cần nhiều công sức hơn.",
        first_line=0.8,
    )

    dh.heading(doc, "8.1. Bổ sung toàn vẹn tham chiếu", level=2)
    dh.para(
        doc,
        "Đây là cải tiến quan trọng nhất, khắc phục hạn chế **H01** và **H06**. "
        "Khoá ngoại mới bảo đảm mọi bản ghi giờ làm đều gắn với một nhân viên "
        "thực tồn, còn `ON DELETE CASCADE` cho phép xoá nhân viên mà không bị "
        "chặn bởi các bản ghi người phụ thuộc.",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_FK)
    dh.note_box(
        doc,
        "Nên chạy truy vấn `QC01` ở Phụ lục B **trước** khi thêm khoá ngoại. "
        "Nếu tồn tại bản ghi mồ côi, câu lệnh `ADD CONSTRAINT` sẽ thất bại và "
        "cần xử lý dữ liệu sai trước.",
        title="Lưu ý khi áp dụng",
    )

    dh.heading(doc, "8.2. Bổ sung toàn vẹn miền giá trị", level=2)
    dh.para(
        doc,
        "Nhóm giải pháp này khắc phục **H02** và **H05** – hai hạn chế cho "
        "phép dữ liệu vô nghĩa đi vào cơ sở dữ liệu.",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_CHECK)

    dh.heading(doc, "8.3. Siết chặt kiểu dữ liệu và tính bắt buộc", level=2)
    dh.para(
        doc,
        "Xử lý **H04** và **H07**. Lưu ý rằng không thể đặt `NOT NULL` cho "
        "`tblEmployee.depNum` và `tblDepartment.mgrSSN` chừng nào phụ thuộc "
        "vòng ở mục 4.5 còn tồn tại, nên hai cột này được giữ nguyên.",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_NOTNULL)

    dh.heading(doc, "8.4. Hiện thực hoá các quy tắc nghiệp vụ liên bảng", level=2)
    dh.para(
        doc,
        "Ba quy tắc **BR04**, **BR09** và **BR13** không thể diễn đạt bằng "
        "`CHECK` thông thường vì cần đối chiếu dữ liệu giữa hai bảng. Giải "
        "pháp là dùng trigger:",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_TRIGGER)

    dh.heading(doc, "8.5. Cải thiện hiệu năng truy vấn", level=2)
    dh.para(
        doc,
        "Xử lý **H10**. SQL Server tự tạo chỉ mục cho khoá chính nhưng "
        "**không** tự tạo cho khoá ngoại, trong khi khoá ngoại lại là cột "
        "được dùng nhiều nhất trong các phép nối.",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_INDEX)

    dh.heading(doc, "8.6. Làm sạch và chuẩn hoá dữ liệu", level=2)
    dh.para(
        doc,
        "Nhóm cuối xử lý các bất thường **D01**, **D03** và điểm thảo luận ở "
        "mục 5.5 về việc tham chiếu hoá địa chỉ nhân viên.",
        first_line=0.8,
    )
    dh.code_block(doc, SQL_CLEAN)

    dh.heading(doc, "8.7. Lộ trình áp dụng theo thứ tự ưu tiên", level=2)
    dh.table(
        doc,
        ["#", "Hành động", "Khắc phục", "Lợi ích", "Chi phí", "Thời điểm"],
        [[a, b, c, d, e, f] for a, b, c, d, e, f in ROADMAP],
        widths=[0.9, 5.7, 2.6, 2.2, 2.2, 2.4],
        aligns=["center", None, "center", "center", "center", "center"],
        size=9.0,
    )
    dh.caption(doc, cap.table(8, "Lộ trình áp dụng các đề xuất cải tiến"))
    dh.para(
        doc,
        "Sáu hành động đầu tiên đều có chi phí thấp đến trung bình nhưng giải "
        "quyết được toàn bộ các hạn chế mức nghiêm trọng và cao. Hai hành động "
        "cuối (đổi kiểu mã định danh và tách bảng) tuy mang lại thiết kế đúng "
        "đắn hơn nhưng đòi hỏi sửa cả ứng dụng phía trên, nên chỉ phù hợp khi "
        "có kế hoạch nâng cấp tổng thể.",
        first_line=0.8,
    )

    # ================================================== CHUONG 9
    dh.heading(doc, "CHƯƠNG 9. KẾT LUẬN", level=1, new_page=True)

    dh.heading(doc, "9.1. Kết quả đạt được", level=2)
    dh.para(
        doc,
        "Báo cáo đã phân tích cơ sở dữ liệu `dbCOMPANY` trong tệp `DBC.sql` "
        "gồm %d bảng, %d cột, %d khoá ngoại và %s bản ghi mẫu, với bốn kết quả "
        "chính sau."
        % (len(db["order"]),
           sum(len(db["tables"][t]["columns"]) for t in db["order"]),
           len(db["fks"]), vn_int(s["total_rows"])),
        first_line=0.8,
    )
    dh.bullets(doc, [
        "**Về chuẩn hoá:** cả bảy quan hệ đều đạt dạng chuẩn BCNF, xử lý đúng "
        "các cấu trúc khó như quan hệ đệ quy, quan hệ nhiều – nhiều có thuộc "
        "tính và thực thể yếu. Đây là điểm mạnh rõ rệt nhất.",
        "**Về toàn vẹn:** chỉ %d trong %d quy tắc nghiệp vụ được cơ sở dữ liệu "
        "bảo vệ trọn vẹn. Toàn vẹn thực thể hoàn hảo, toàn vẹn tham chiếu gần "
        "hoàn chỉnh (thiếu một khoá ngoại), nhưng toàn vẹn miền giá trị và "
        "nghiệp vụ gần như bị bỏ trống." % (6, 17),
        "**Về dữ liệu:** phát hiện tám nhóm bất thường, nghiêm trọng nhất là "
        "%d trên %d phòng ban có ngày bổ nhiệm trưởng phòng sớm hơn ngày người "
        "đó vào làm."
        % (len(s["mgr_date_issues"]), len(s["dep_rows"])),
        "**Về cải tiến:** đề xuất %d hành động kèm mã lệnh SQL theo lộ trình "
        "bốn giai đoạn; sáu hành động đầu đủ để loại bỏ mọi hạn chế mức nghiêm "
        "trọng và cao." % len(ROADMAP),
    ])
    dh.para(
        doc,
        "Kết luận chung: `DBC.sql` là một script **đúng về mặt cấu trúc quan "
        "hệ**, phù hợp làm dữ liệu học tập. Nhưng nếu dùng cho hệ thống thực "
        "tế, script cần bổ sung lớp ràng buộc toàn vẹn còn thiếu, vì phần lớn "
        "quy tắc nghiệp vụ đang phụ thuộc vào sự cẩn thận của người nhập liệu "
        "thay vì được hệ quản trị bảo vệ.",
        first_line=0.8,
    )

    dh.heading(doc, "9.2. Hạn chế của báo cáo", level=2)
    dh.bullets(doc, [
        "Các quy tắc nghiệp vụ được **suy luận** từ cấu trúc bảng và dữ liệu "
        "mẫu, không có tài liệu đặc tả gốc để đối chiếu, nên một số nhận định "
        "có thể không trùng yêu cầu thực tế của doanh nghiệp.",
        "Dữ liệu mẫu chỉ gồm %s bản ghi, quá nhỏ để đánh giá hiệu năng hay "
        "kiểm chứng lợi ích thực tế của các chỉ mục được đề xuất."
        % vn_int(s["total_rows"]),
        "Các đoạn mã cải tiến ở Chương 8 chưa được kiểm thử thực tế; chúng "
        "được viết theo cú pháp T-SQL cho SQL Server 2016 trở lên.",
    ])

    dh.heading(doc, "9.3. Hướng phát triển", level=2)
    dh.bullets(doc, [
        "Xây dựng bộ **khung nhìn (`VIEW`)** phục vụ báo cáo thường dùng: nhân "
        "sự theo phòng ban, tổng giờ làm theo dự án, danh sách người phụ thuộc "
        "theo nhân viên.",
        "Viết **thủ tục thường trú và hàm** cho các nghiệp vụ hay dùng: điều "
        "chuyển nhân viên giữa các phòng ban, bổ nhiệm trưởng phòng mới, phân "
        "công nhân viên vào dự án.",
        "Bổ sung **cơ chế truy vết lịch sử** (lịch sử lương, lịch sử điều "
        "chuyển) phục vụ kiểm toán nhân sự.",
        "Mở rộng lược đồ: thêm thực thể CHỨC VỤ, HỢP ĐỒNG, hoặc bảng chấm công "
        "theo ngày thay cho cột `workHours` tổng hợp.",
    ])

    # ================================================== PHU LUC
    dh.heading(doc, "PHỤ LỤC A. TÓM LƯỢC TOÀN BỘ CẤU TRÚC CỘT", level=1, new_page=True)
    dh.para(
        doc,
        "Bảng tổng hợp %d cột của cả bảy bảng, tiện cho việc tra cứu nhanh."
        % sum(len(db["tables"][t]["columns"]) for t in db["order"]),
    )
    all_rows = []
    order = ["tblLocation", "tblDepartment", "tblEmployee", "tblDepLocation",
             "tblProject", "tblWorksOn", "tblDependent"]
    for tname in order:
        info = db["tables"][tname]
        for c in info["columns"]:
            marks = []
            if c["name"] in info["pk"]:
                marks.append("PK")
            for fk in info["fks"]:
                if fk["column"] == c["name"]:
                    marks.append("FK")
            all_rows.append([
                "`%s`" % tname, "`%s`" % c["name"],
                "`%s`" % c["type"] + (" IDENTITY" if c["identity"] else ""),
                "Có" if c["nullable"] else "Không",
                "+".join(marks) if marks else "—",
            ])
    dh.table(
        doc,
        ["Bảng", "Cột", "Kiểu dữ liệu", "NULL", "Khoá"],
        all_rows,
        widths=[3.8, 4.2, 3.6, 2.0, 2.4],
        aligns=[None, None, None, "center", "center"],
        size=9.0,
    )
    dh.caption(doc, cap.raw_table("Bảng A.1. Tóm lược toàn bộ cấu trúc cột"))

    dh.heading(doc, "PHỤ LỤC B. TRUY VẤN KIỂM TRA CHẤT LƯỢNG DỮ LIỆU", level=1, new_page=True)
    dh.para(
        doc,
        "Mười truy vấn dưới đây có thể chạy trực tiếp trên `dbCOMPANY` để phát "
        "hiện lại các vấn đề đã nêu ở Chương 7. Mỗi truy vấn trả về **không "
        "dòng nào** nếu dữ liệu đạt yêu cầu (trừ QC10 mang tính đối chiếu).",
    )
    dh.para(doc, "**Nhóm 1 – Kiểm tra toàn vẹn tham chiếu và miền giá trị:**",
            first_line=0, keep_with_next=True)
    dh.code_block(doc, SQL_QC1, size=8.5)
    dh.para(doc, "**Nhóm 2 – Kiểm tra tính nhất quán nghiệp vụ:**",
            first_line=0, keep_with_next=True)
    dh.code_block(doc, SQL_QC2, size=8.5)
    dh.para(doc, "**Nhóm 3 – Kiểm tra định dạng và đối chiếu tổng hợp:**",
            first_line=0, keep_with_next=True)
    dh.code_block(doc, SQL_QC3, size=8.5)
    dh.para(
        doc,
        "Cách dùng: chạy lần lượt từ QC01 đến QC09 sau mỗi lần nạp hoặc chỉnh "
        "sửa dữ liệu; nếu truy vấn nào trả về dòng thì đó chính là các bản ghi "
        "vi phạm cần xử lý. QC10 không kiểm tra lỗi mà dùng để đối chiếu tổng "
        "giờ làm theo phòng ban với Bảng 6.1, giúp phát hiện sai lệch do bản "
        "ghi giờ làm “mồ côi” gây ra.",
    )

    dh.heading(doc, "PHỤ LỤC C. DANH SÁCH DỮ LIỆU MẪU", level=1, new_page=True)

    dh.heading(doc, "C.1. Danh sách nhân viên", level=2)
    emp_rows = []
    for i, e in enumerate(db["rows"]["tblEmployee"], start=1):
        sup = s["emp_by_ssn"].get(e["supervisorSSN"])
        emp_rows.append([
            str(i), ssn(e["empSSN"]), e["empName"], str(e["depNum"]),
            vn_int(e["empSalary"]), e["empSex"],
            vn_date(e["empStartdate"]),
            sup["empName"] if sup else "— (trưởng phòng)",
        ])
    dh.table(
        doc,
        ["STT", "Mã nhân viên", "Họ và tên", "Phòng", "Lương", "GT",
         "Ngày vào làm", "Người giám sát"],
        emp_rows,
        widths=[1.1, 2.3, 3.3, 1.3, 1.6, 0.8, 2.1, 3.5],
        aligns=["center", None, None, "center", "right", "center", "center",
                None],
        size=8.5,
    )
    dh.caption(doc, cap.raw_table("Bảng C.1. Danh sách %d nhân viên"
                                  % s["salary"]["count"]))

    dh.heading(doc, "C.2. Danh sách người phụ thuộc", level=2)
    dent_rows = []
    for i, d in enumerate(db["rows"]["tblDependent"], start=1):
        e = s["emp_by_ssn"][d["empSSN"]]
        dent_rows.append([
            str(i), d["depName"], d["depSex"], vn_date(d["depBirthdate"]),
            d["depRelationship"], e["empName"],
        ])
    dh.table(
        doc,
        ["STT", "Tên người phụ thuộc", "GT", "Ngày sinh", "Quan hệ",
         "Nhân viên"],
        dent_rows,
        widths=[1.1, 3.9, 0.9, 2.1, 2.1, 3.9],
        aligns=["center", None, "center", "center", "center", None],
        size=9.0,
    )
    dh.caption(doc, cap.raw_table("Bảng C.2. Danh sách %d người phụ thuộc"
                                  % s["row_counts"]["tblDependent"]))

    dh.heading(doc, "C.3. Phân công nhân viên vào dự án", level=2)
    work_rows = []
    for i, w in enumerate(db["rows"]["tblWorksOn"], start=1):
        e = s["emp_by_ssn"][w["empSSN"]]
        p = s["pro_by_num"][w["proNum"]]
        work_rows.append([
            str(i), ssn(w["empSSN"]), e["empName"], p["proName"],
            vn_int(w["workHours"]),
        ])
    dh.table(
        doc,
        ["STT", "Mã nhân viên", "Họ và tên", "Dự án", "Số giờ"],
        work_rows,
        widths=[1.1, 2.6, 5.3, 3.2, 2.2],
        aligns=["center", None, None, "center", "right"],
        size=8.5,
    )
    dh.caption(doc, cap.raw_table(
        "Bảng C.3. Bảng phân công %d dòng của tblWorksOn"
        % s["row_counts"]["tblWorksOn"]))
