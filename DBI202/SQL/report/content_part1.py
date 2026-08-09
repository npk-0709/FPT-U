"""Chuong 1-3: Gioi thieu, phan tich nghiep vu, thiet ke muc khai niem."""

import os

import docx_helpers as dh
from stats import vn_date, vn_float, vn_int

BUSINESS_RULES = [
    ("BR01", "Mỗi phòng ban được nhận biết bởi một mã số duy nhất.",
     "PRIMARY KEY (depNum)", "Đã đảm bảo"),
    ("BR02", "Tên phòng ban không được trùng nhau.",
     "UNIQUE (depName)", "Chưa"),
    ("BR03", "Mỗi phòng ban do đúng một nhân viên của công ty làm trưởng phòng, "
     "kèm ngày bổ nhiệm.",
     "FK mgrSSN → tblEmployee", "Một phần"),
    ("BR04", "Ngày bổ nhiệm trưởng phòng không được sớm hơn ngày người đó "
     "vào làm.", "CHECK liên bảng / TRIGGER", "Chưa"),
    ("BR05", "Một phòng ban có thể làm việc tại nhiều địa điểm; một địa điểm "
     "có thể có nhiều phòng ban.",
     "tblDepLocation: PK kép + 2 FK", "Đã đảm bảo"),
    ("BR06", "Mỗi nhân viên được nhận biết bởi một mã số duy nhất.",
     "PRIMARY KEY (empSSN)", "Đã đảm bảo"),
    ("BR07", "Mỗi nhân viên trực thuộc đúng một phòng ban.",
     "FK depNum + NOT NULL", "Một phần"),
    ("BR08", "Mỗi nhân viên có tối đa một người giám sát trực tiếp, và người "
     "này cũng là nhân viên công ty.",
     "FK supervisorSSN tự tham chiếu", "Đã đảm bảo"),
    ("BR09", "Người giám sát phải cùng phòng ban với nhân viên được giám sát.",
     "TRIGGER", "Chưa"),
    ("BR10", "Giới tính chỉ nhận giá trị 'M' hoặc 'F'.",
     "CHECK (empSex IN ('M','F'))", "Chưa"),
    ("BR11", "Lương nhân viên phải là số dương.",
     "CHECK (empSalary > 0)", "Chưa"),
    ("BR12", "Mỗi dự án do đúng một phòng ban phụ trách và được triển khai "
     "tại đúng một địa điểm.",
     "FK depNum, FK locNum + NOT NULL", "Một phần"),
    ("BR13", "Một nhân viên tham gia nhiều dự án, một dự án có nhiều nhân "
     "viên; mỗi cặp ghi nhận số giờ làm.",
     "tblWorksOn: PK kép + 2 FK", "Một phần"),
    ("BR14", "Số giờ làm việc trên một dự án không được âm.",
     "CHECK (workHours >= 0)", "Chưa"),
    ("BR15", "Mỗi người phụ thuộc gắn với đúng một nhân viên và được phân "
     "biệt bởi tên trong phạm vi nhân viên đó (thực thể yếu).",
     "PK (depName, empSSN) + FK empSSN", "Đã đảm bảo"),
    ("BR16", "Khi xoá một nhân viên, các bản ghi người phụ thuộc của nhân "
     "viên đó phải được xoá theo.",
     "FK ... ON DELETE CASCADE", "Chưa"),
    ("BR17", "Không được xoá nhân viên đang giữ chức trưởng phòng.",
     "FK mgrSSN chặn hành vi xoá", "Đã đảm bảo"),
]

ENTITIES = [
    ("PHÒNG BAN", "tblDepartment", "depNum",
     "depName, mgrSSN, mgrAssDate",
     "Thực thể mạnh. Đơn vị tổ chức của công ty."),
    ("NHÂN VIÊN", "tblEmployee", "empSSN",
     "empName, empAddress, empSalary, empSex, empBirthdate, empStartdate",
     "Thực thể mạnh, trung tâm của lược đồ."),
    ("ĐỊA ĐIỂM", "tblLocation", "locNum", "locName",
     "Thực thể mạnh. Thành phố nơi công ty hiện diện."),
    ("DỰ ÁN", "tblProject", "proNum", "proName",
     "Thực thể mạnh. Công việc do một phòng ban phụ trách."),
    ("NGƯỜI PHỤ THUỘC", "tblDependent", "depName (khoá bộ phận)",
     "depSex, depBirthdate, depRelationship",
     "Thực thể yếu, phụ thuộc tồn tại vào NHÂN VIÊN."),
    ("THAM GIA", "tblWorksOn", "(empSSN, proNum)", "workHours",
     "Quan hệ M:N có thuộc tính riêng, hiện thực thành bảng."),
    ("LÀM VIỆC TẠI", "tblDepLocation", "(depNum, locNum)", "— (không có)",
     "Quan hệ M:N thuần, hiện thực thành bảng."),
]

RELATIONSHIPS = [
    ("R1", "QUẢN LÝ", "tblEmployee — tblDepartment", "1 : 1",
     "tblDepartment.mgrSSN", "mgrAssDate"),
    ("R2", "THUỘC VỀ", "tblDepartment — tblEmployee", "1 : N",
     "tblEmployee.depNum", "—"),
    ("R3", "GIÁM SÁT (đệ quy)", "tblEmployee — tblEmployee", "1 : N",
     "tblEmployee.supervisorSSN", "—"),
    ("R4", "LÀM VIỆC TẠI", "tblDepartment — tblLocation", "M : N",
     "Bảng tblDepLocation", "—"),
    ("R5", "PHỤ TRÁCH", "tblDepartment — tblProject", "1 : N",
     "tblProject.depNum", "—"),
    ("R6", "ĐẶT TẠI", "tblLocation — tblProject", "1 : N",
     "tblProject.locNum", "—"),
    ("R7", "THAM GIA", "tblEmployee — tblProject", "M : N",
     "Bảng tblWorksOn", "workHours"),
    ("R8", "PHỤ THUỘC", "tblEmployee — tblDependent", "1 : N",
     "tblDependent.empSSN", "—"),
]


def build(ctx):
    doc, s, cap = ctx.doc, ctx.s, ctx.cap
    db = s["db"]

    # ================================================== CHUONG 1
    dh.heading(doc, "CHƯƠNG 1. GIỚI THIỆU", level=1)

    dh.heading(doc, "1.1. Bối cảnh và mục tiêu", level=2)
    dh.para(
        doc,
        "Trong học phần DBI202 – Database Systems, cơ sở dữ liệu COMPANY là "
        "ví dụ kinh điển được dùng để minh hoạ toàn bộ vòng đời thiết kế một "
        "cơ sở dữ liệu quan hệ: từ mô hình thực thể – liên kết, chuyển sang "
        "lược đồ quan hệ, chuẩn hoá, đến cài đặt bằng ngôn ngữ SQL. Tệp "
        "`DBC.sql` được phân tích trong báo cáo này là một hiện thực hoá của "
        "cơ sở dữ liệu đó trên hệ quản trị Microsoft SQL Server, với tên "
        "cơ sở dữ liệu `dbCOMPANY` và dữ liệu mẫu mang tính Việt hoá.",
        first_line=0.8,
    )
    dh.para(doc, "Báo cáo được thực hiện với bốn mục tiêu cụ thể:")
    dh.bullets(doc, [
        "**Tài liệu hoá** đầy đủ cấu trúc cơ sở dữ liệu: bảng, cột, kiểu dữ "
        "liệu, khoá chính, khoá ngoại và các quan hệ giữa các bảng.",
        "**Đối chiếu với lý thuyết chuẩn hoá**, xác định dạng chuẩn cao nhất "
        "mà từng quan hệ đạt được và giải thích cơ sở của kết luận đó.",
        "**Đánh giá tính toàn vẹn** của thiết kế: những quy tắc nghiệp vụ nào "
        "đã được hệ quản trị bảo vệ và những quy tắc nào còn bị bỏ ngỏ.",
        "**Đề xuất cải tiến** cụ thể, kèm mã lệnh SQL có thể áp dụng trực "
        "tiếp, đồng thời chỉ ra các bất thường tồn tại trong dữ liệu mẫu.",
    ])

    dh.heading(doc, "1.2. Đối tượng và phạm vi", level=2)
    dh.para(
        doc,
        "Đối tượng nghiên cứu là toàn bộ nội dung tệp `DBC.sql`, bao gồm ba "
        "thành phần: các câu lệnh định nghĩa dữ liệu (DDL) tạo cơ sở dữ liệu "
        "và bảng, các câu lệnh `INSERT` nạp dữ liệu mẫu, và các câu lệnh "
        "`ALTER TABLE` khai báo khoá ngoại. Báo cáo phân tích cả cấu trúc "
        "(mức lược đồ) và nội dung (mức thể hiện dữ liệu).",
        first_line=0.8,
    )
    dh.para(doc, "Những nội dung **không** thuộc phạm vi báo cáo:")
    dh.bullets(doc, [
        "Các tệp SQL khác trong cùng thư mục làm việc; báo cáo chỉ xét "
        "`DBC.sql` như một đơn vị độc lập.",
        "Đo lường hiệu năng thực tế (thời gian thi hành, kế hoạch truy vấn) "
        "vì việc này phụ thuộc phần cứng và cấu hình máy chủ.",
        "Các vấn đề vận hành như sao lưu – phục hồi, phân quyền người dùng "
        "và bảo mật mức hệ thống.",
    ])

    dh.heading(doc, "1.3. Thông tin về nguồn dữ liệu", level=2)
    sql_path = os.path.join(os.path.dirname(os.path.dirname(
        os.path.abspath(__file__))), "DBC.sql")
    size_kb = os.path.getsize(sql_path) / 1024.0
    n_lines = len(db["sql"].split("\n"))
    n_cols = sum(len(db["tables"][t]["columns"]) for t in db["order"])
    n_insert = sum(1 for l in db["sql"].split("\n")
                   if l.strip().upper().startswith("INSERT "))

    dh.table(
        doc,
        ["Hạng mục", "Giá trị"],
        [
            ["Tên tệp phân tích", "`DBC.sql`"],
            ["Kích thước tệp", "%s KB" % vn_float(size_kb, 1)],
            ["Số dòng mã lệnh", "%s dòng" % vn_int(n_lines)],
            ["Hệ quản trị cơ sở dữ liệu", "Microsoft SQL Server (T-SQL)"],
            ["Tên cơ sở dữ liệu được tạo", "`dbCOMPANY`"],
            ["Số bảng cơ sở", "%d bảng" % len(db["order"])],
            ["Tổng số cột", "%d cột" % n_cols],
            ["Số khoá chính", "%d (mọi bảng đều có khoá chính)" % len(db["order"])],
            ["Số khoá ngoại được khai báo", "%d khoá ngoại" % len(db["fks"])],
            ["Số câu lệnh INSERT", "%s câu lệnh" % vn_int(n_insert)],
            ["Tổng số bản ghi dữ liệu mẫu", "%s bản ghi" % vn_int(s["total_rows"])],
            ["Bảng có nhiều bản ghi nhất",
             "`tblWorksOn` (%d bản ghi)" % s["row_counts"]["tblWorksOn"]],
        ],
        widths=[6.0, 10.0],
    )
    dh.caption(doc, cap.table(1, "Thông tin tổng quan về tệp DBC.sql"))

    dh.heading(doc, "1.4. Phương pháp thực hiện", level=2)
    dh.para(
        doc,
        "Để bảo đảm mọi con số trong báo cáo đều phản ánh đúng nội dung tệp "
        "gốc, nhóm thực hiện không nhập liệu thủ công mà viết một chương "
        "trình nhỏ để bóc tách tự động: đọc `DBC.sql`, phân tích cú pháp các "
        "câu lệnh `CREATE TABLE`, `ALTER TABLE` và `INSERT`, sau đó tính toán "
        "các chỉ số thống kê và sinh trực tiếp tài liệu này. Nhờ vậy, các số "
        "liệu về số cột, số khoá ngoại, số bản ghi hay giá trị lương trung "
        "bình đều được tính lại từ dữ liệu thật thay vì sao chép bằng tay.",
        first_line=0.8,
    )
    dh.para(
        doc,
        "Phần đánh giá thiết kế được thực hiện bằng cách đối chiếu lược đồ "
        "thu được với ba nhóm tiêu chí: lý thuyết chuẩn hoá (1NF đến BCNF), "
        "bốn loại toàn vẹn của mô hình quan hệ (toàn vẹn thực thể, toàn vẹn "
        "tham chiếu, toàn vẹn miền giá trị và toàn vẹn do người dùng định "
        "nghĩa), và các quy tắc nghiệp vụ suy ra từ ngữ nghĩa bài toán.",
        first_line=0.8,
    )

    dh.heading(doc, "1.5. Cấu trúc báo cáo", level=2)
    dh.bullets(doc, [
        "**Chương 1** giới thiệu bối cảnh, phạm vi và phương pháp.",
        "**Chương 2** phân tích nghiệp vụ và hệ thống hoá các quy tắc nghiệp vụ.",
        "**Chương 3** trình bày thiết kế mức khái niệm cùng sơ đồ ERD.",
        "**Chương 4** mô tả chi tiết thiết kế mức logic và vật lý của bảy bảng.",
        "**Chương 5** kiểm tra chuẩn hoá từ 1NF đến BCNF.",
        "**Chương 6** phân tích định lượng dữ liệu mẫu bằng bảng biểu và đồ thị.",
        "**Chương 7** đánh giá chất lượng thiết kế và phát hiện bất thường dữ liệu.",
        "**Chương 8** đề xuất cải tiến kèm mã lệnh SQL cụ thể.",
        "**Chương 9** kết luận; các phụ lục cung cấp dữ liệu chi tiết và "
        "truy vấn kiểm tra.",
    ])

    # ================================================== CHUONG 2
    dh.heading(doc, "CHƯƠNG 2. PHÂN TÍCH NGHIỆP VỤ", level=1, new_page=True)

    dh.heading(doc, "2.1. Mô tả bài toán", level=2)
    dh.para(
        doc,
        "Cơ sở dữ liệu `dbCOMPANY` mô hình hoá hoạt động quản lý nhân sự và "
        "dự án của một công ty công nghệ thông tin. Theo dữ liệu mẫu, công ty "
        "được tổ chức thành %d phòng ban chuyên môn, hiện diện tại %d thành "
        "phố, đang triển khai %d dự án với sự tham gia của %d nhân viên."
        % (len(s["dep_rows"]), len(s["loc_rows"]), len(s["pro_rows"]),
           s["salary"]["count"]),
        first_line=0.8,
    )
    dh.para(
        doc,
        "Hệ thống cần trả lời được những câu hỏi quản trị điển hình như: một "
        "phòng ban do ai phụ trách và hoạt động ở những địa điểm nào; một "
        "nhân viên thuộc phòng ban nào, chịu sự giám sát của ai và đang tham "
        "gia những dự án nào với bao nhiêu giờ làm; mỗi nhân viên có những "
        "người phụ thuộc nào để phục vụ chính sách phúc lợi. Bốn nhóm đối "
        "tượng nghiệp vụ trung tâm vì vậy là **phòng ban**, **nhân viên**, "
        "**dự án** và **người phụ thuộc**, đặt trong bối cảnh các **địa điểm** "
        "làm việc.",
        first_line=0.8,
    )

    dh.heading(doc, "2.2. Hệ thống hoá các quy tắc nghiệp vụ", level=2)
    dh.para(
        doc,
        "Bảng dưới đây tổng hợp %d quy tắc nghiệp vụ suy ra từ ngữ nghĩa bài "
        "toán, đối chiếu với cơ chế mà cơ sở dữ liệu dùng để bảo vệ quy tắc "
        "đó. Cột trạng thái nhận một trong ba giá trị: **Đã đảm bảo** (hệ "
        "quản trị tự động chặn dữ liệu sai), **Một phần** (có cơ chế nhưng "
        "chưa đủ chặt) và **Chưa** (hoàn toàn phụ thuộc vào ứng dụng hoặc "
        "người nhập liệu)." % len(BUSINESS_RULES),
    )
    dh.table(
        doc,
        ["Mã", "Nội dung quy tắc nghiệp vụ", "Cơ chế trong CSDL", "Trạng thái"],
        [[a, b, "`%s`" % c if c.startswith(("PRIMARY", "UNIQUE", "CHECK", "FK"))
          else c, d] for a, b, c, d in BUSINESS_RULES],
        widths=[1.3, 6.7, 4.6, 2.2],
        aligns=["center", None, None, "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(2, "Danh sách quy tắc nghiệp vụ và mức độ "
                                 "được cơ sở dữ liệu bảo vệ"))

    n_ok = sum(1 for r in BUSINESS_RULES if r[3] == "Đã đảm bảo")
    n_part = sum(1 for r in BUSINESS_RULES if r[3] == "Một phần")
    n_no = sum(1 for r in BUSINESS_RULES if r[3] == "Chưa")

    dh.heading(doc, "2.3. Nhận xét về mức độ hiện thực hoá", level=2)
    dh.para(
        doc,
        "Trong %d quy tắc nêu trên, chỉ %d quy tắc (%s%%) được cơ sở dữ liệu "
        "bảo vệ trọn vẹn, %d quy tắc được bảo vệ một phần và %d quy tắc "
        "(%s%%) chưa có bất kỳ cơ chế nào trong lược đồ. Điều này cho thấy "
        "`DBC.sql` đã làm tốt phần **toàn vẹn thực thể** và phần lớn **toàn "
        "vẹn tham chiếu**, nhưng gần như bỏ trống hai nhóm còn lại là **toàn "
        "vẹn miền giá trị** (kiểm soát giá trị hợp lệ của từng cột) và **toàn "
        "vẹn do người dùng định nghĩa** (các quy tắc liên bảng)."
        % (len(BUSINESS_RULES), n_ok,
           vn_float(100.0 * n_ok / len(BUSINESS_RULES), 1), n_part, n_no,
           vn_float(100.0 * n_no / len(BUSINESS_RULES), 1)),
        first_line=0.8,
    )
    dh.note_box(
        doc,
        "Hệ quả thực tế là cơ sở dữ liệu hiện tại vẫn cho phép nhập một nhân "
        "viên có lương âm, giới tính là ký tự `X`, hoặc một bản ghi giờ làm "
        "gắn với mã nhân viên không tồn tại. Các đề xuất khắc phục được "
        "trình bày trong Chương 8.",
        title="Hệ quả cần lưu ý",
    )

    # ================================================== CHUONG 3
    dh.heading(doc, "CHƯƠNG 3. THIẾT KẾ MỨC KHÁI NIỆM", level=1, new_page=True)

    dh.heading(doc, "3.1. Các thực thể và thuộc tính", level=2)
    dh.para(
        doc,
        "Lược đồ gồm bốn thực thể mạnh (PHÒNG BAN, NHÂN VIÊN, ĐỊA ĐIỂM, DỰ "
        "ÁN), một thực thể yếu (NGƯỜI PHỤ THUỘC) và hai quan hệ nhiều – "
        "nhiều được hiện thực hoá thành bảng riêng (THAM GIA, LÀM VIỆC TẠI).",
    )
    dh.table(
        doc,
        ["Thực thể / quan hệ", "Bảng tương ứng", "Định danh",
         "Thuộc tính mô tả", "Vai trò"],
        [[a, "`%s`" % b, "`%s`" % c if not c.startswith("(") else "`%s`" % c,
          d, e] for a, b, c, d, e in ENTITIES],
        widths=[2.7, 2.6, 2.6, 4.4, 3.7],
        size=9.5,
    )
    dh.caption(doc, cap.table(3, "Danh sách thực thể, thuộc tính và vai trò "
                                 "trong lược đồ"))

    dh.heading(doc, "3.2. Các mối quan hệ và bản số", level=2)
    dh.para(
        doc,
        "Tám mối quan hệ dưới đây mô tả toàn bộ liên kết ngữ nghĩa giữa các "
        "thực thể. Đáng chú ý là quan hệ **R3 – GIÁM SÁT** mang tính đệ quy "
        "(một nhân viên giám sát nhiều nhân viên khác trong cùng bảng) và "
        "quan hệ **R1 – QUẢN LÝ** là quan hệ một – một, tạo nên phụ thuộc "
        "vòng giữa hai bảng `tblDepartment` và `tblEmployee`.",
    )
    dh.table(
        doc,
        ["Mã", "Tên quan hệ", "Các thực thể tham gia", "Bản số",
         "Hiện thực hoá", "Thuộc tính riêng"],
        [[a, b, "`%s`" % c.replace(" — ", "` — `"), d,
          "`%s`" % e if "." in e else e, "`%s`" % f if f != "—" else f]
         for a, b, c, d, e, f in RELATIONSHIPS],
        widths=[1.1, 3.1, 4.2, 1.6, 3.6, 2.4],
        aligns=["center", None, None, "center", None, "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(3, "Các mối quan hệ, bản số và cách hiện thực hoá"))

    dh.heading(doc, "3.3. Sơ đồ thực thể – liên kết", level=2)
    dh.para(
        doc,
        "Sơ đồ ở trang sau được sinh tự động từ chính lược đồ đọc được trong "
        "`DBC.sql`. Mỗi khung là một bảng với danh sách cột; nhãn `PK` chỉ "
        "khoá chính, `FK` chỉ khoá ngoại. Các đường liền màu xanh là khoá "
        "ngoại thực sự đã được khai báo; đường nét đứt màu đỏ thể hiện quan "
        "hệ tồn tại về mặt nghiệp vụ nhưng **chưa** được khai báo khoá ngoại "
        "trong script – đây chính là khiếm khuyết quan trọng nhất của thiết "
        "kế, sẽ được phân tích ở Chương 7.",
    )

    sec = dh.landscape_section(doc)
    dh.add_footer_page_numbers(sec)
    dh.figure(doc, ctx.figs["erd"], 23.4,
              cap.fig(3, "Sơ đồ thực thể – liên kết của cơ sở dữ liệu "
                         "dbCOMPANY (sinh tự động từ DBC.sql)"))
    sec2 = dh.portrait_section(doc)
    dh.add_footer_page_numbers(sec2)

    dh.heading(doc, "3.4. Nhận xét về thiết kế khái niệm", level=2)
    dh.bullets(doc, [
        "Lược đồ **phân rã đúng** hai quan hệ nhiều – nhiều thành bảng trung "
        "gian với khoá chính kép, tránh được hiện tượng lặp dữ liệu.",
        "Thực thể yếu NGƯỜI PHỤ THUỘC được mô hình hoá chuẩn mực với khoá "
        "chính gồm khoá của thực thể chủ (`empSSN`) và khoá bộ phận (`depName`).",
        "Quan hệ đệ quy GIÁM SÁT được cài đặt gọn gàng bằng một khoá ngoại tự "
        "tham chiếu, cho phép biểu diễn cây phân cấp quản lý không giới hạn số cấp.",
        "Điểm yếu về mặt khái niệm: quan hệ QUẢN LÝ (1:1) và THUỘC VỀ (1:N) "
        "cùng nối hai bảng `tblDepartment` và `tblEmployee` theo hai chiều "
        "ngược nhau, tạo thành **phụ thuộc vòng** khiến việc nạp dữ liệu và "
        "xoá dữ liệu trở nên phức tạp (phân tích ở mục 4.5).",
        "Thuộc tính `empAddress` chỉ lưu tên tỉnh/thành dưới dạng văn bản tự "
        "do, không liên kết với thực thể ĐỊA ĐIỂM đã có, làm mất cơ hội "
        "kiểm soát giá trị và thống kê theo địa bàn.",
    ])
