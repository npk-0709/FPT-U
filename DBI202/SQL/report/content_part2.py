"""Chuong 4-5: Thiet ke muc logic/vat ly va chuan hoa du lieu."""

import docx_helpers as dh
from stats import vn_int

TABLE_PURPOSE = {
    "tblDepartment": "Lưu danh mục các phòng ban của công ty cùng thông tin "
                     "trưởng phòng và ngày bổ nhiệm.",
    "tblEmployee": "Bảng trung tâm của lược đồ, lưu hồ sơ nhân viên kèm phòng "
                   "ban trực thuộc và người giám sát trực tiếp.",
    "tblLocation": "Danh mục địa điểm (thành phố) nơi công ty có hoạt động.",
    "tblProject": "Lưu các dự án, phòng ban phụ trách và địa điểm triển khai.",
    "tblDependent": "Lưu người phụ thuộc của nhân viên, phục vụ chính sách "
                    "phúc lợi. Đây là bảng của một thực thể yếu.",
    "tblWorksOn": "Bảng trung gian ghi nhận việc nhân viên tham gia dự án "
                  "kèm số giờ làm việc.",
    "tblDepLocation": "Bảng trung gian thể hiện quan hệ nhiều – nhiều giữa "
                      "phòng ban và địa điểm làm việc.",
}

COL_DESC = {
    ("tblDepartment", "depNum"): "Mã số phòng ban, do người dùng tự gán "
                                 "(không tự tăng).",
    ("tblDepartment", "depName"): "Tên phòng ban, ví dụ “Phòng Nghiên cứu và "
                                  "phát triển”.",
    ("tblDepartment", "mgrSSN"): "Mã nhân viên đang giữ chức trưởng phòng.",
    ("tblDepartment", "mgrAssDate"): "Ngày nhân viên được bổ nhiệm làm "
                                     "trưởng phòng.",
    ("tblDependent", "depName"): "Họ tên người phụ thuộc; là khoá bộ phận của "
                                 "thực thể yếu.",
    ("tblDependent", "empSSN"): "Mã nhân viên mà người này phụ thuộc vào.",
    ("tblDependent", "depSex"): "Giới tính người phụ thuộc (M/F).",
    ("tblDependent", "depBirthdate"): "Ngày sinh người phụ thuộc.",
    ("tblDependent", "depRelationship"): "Quan hệ với nhân viên: Vợ, Chồng, "
                                         "Em, Mẹ…",
    ("tblDepLocation", "depNum"): "Mã phòng ban tham gia quan hệ.",
    ("tblDepLocation", "locNum"): "Mã địa điểm mà phòng ban đó làm việc.",
    ("tblEmployee", "empSSN"): "Mã số nhân viên (số định danh), khoá chính.",
    ("tblEmployee", "empName"): "Họ và tên đầy đủ của nhân viên.",
    ("tblEmployee", "empAddress"): "Địa chỉ; thực tế chỉ lưu tên tỉnh/thành "
                                   "dạng văn bản tự do.",
    ("tblEmployee", "empSalary"): "Mức lương; script không ghi rõ đơn vị và "
                                  "chu kỳ trả lương.",
    ("tblEmployee", "empSex"): "Giới tính nhân viên (M/F).",
    ("tblEmployee", "empBirthdate"): "Ngày sinh của nhân viên.",
    ("tblEmployee", "depNum"): "Mã phòng ban mà nhân viên trực thuộc.",
    ("tblEmployee", "supervisorSSN"): "Mã nhân viên giám sát trực tiếp; NULL "
                                      "nếu không có cấp trên.",
    ("tblEmployee", "empStartdate"): "Ngày nhân viên bắt đầu làm việc.",
    ("tblLocation", "locNum"): "Mã địa điểm, tự động tăng nhờ IDENTITY(1,1).",
    ("tblLocation", "locName"): "Tên địa điểm, ở đây là tên thành phố.",
    ("tblProject", "proNum"): "Mã số dự án, khoá chính.",
    ("tblProject", "proName"): "Tên dự án (ProjectA … ProjectE).",
    ("tblProject", "locNum"): "Địa điểm triển khai dự án.",
    ("tblProject", "depNum"): "Phòng ban chịu trách nhiệm dự án.",
    ("tblWorksOn", "empSSN"): "Mã nhân viên tham gia dự án. **Chưa được khai "
                              "báo khoá ngoại.**",
    ("tblWorksOn", "proNum"): "Mã dự án mà nhân viên tham gia.",
    ("tblWorksOn", "workHours"): "Số giờ nhân viên đã làm cho dự án.",
}

SCHEMA_TEXT = """tblLocation    ( #locNum , locName )

tblDepartment  ( #depNum , depName , *mgrSSN , mgrAssDate )

tblEmployee    ( #empSSN , empName , empAddress , empSalary , empSex ,
                 empBirthdate , *depNum , *supervisorSSN , empStartdate )

tblDepLocation ( #*depNum , #*locNum )

tblProject     ( #proNum , proName , *locNum , *depNum )

tblWorksOn     ( #empSSN , #*proNum , workHours )

tblDependent   ( #depName , #*empSSN , depSex , depBirthdate ,
                 depRelationship )

--------------------------------------------------------------------------
Ký hiệu:   #  thuộc khoá chính        *  là khoá ngoại
           #* vừa thuộc khoá chính vừa là khoá ngoại
--------------------------------------------------------------------------
Các khoá ngoại đã khai báo:
   tblDepartment.mgrSSN       →  tblEmployee.empSSN
   tblEmployee.depNum         →  tblDepartment.depNum
   tblEmployee.supervisorSSN  →  tblEmployee.empSSN   (tự tham chiếu)
   tblDepLocation.depNum      →  tblDepartment.depNum
   tblDepLocation.locNum      →  tblLocation.locNum
   tblProject.depNum          →  tblDepartment.depNum
   tblProject.locNum          →  tblLocation.locNum
   tblWorksOn.proNum          →  tblProject.proNum
   tblDependent.empSSN        →  tblEmployee.empSSN

Khoá ngoại còn THIẾU:
   tblWorksOn.empSSN          →  tblEmployee.empSSN   (chưa được khai báo)"""

FUNCTIONAL_DEPS = [
    ("tblLocation", "locNum → locName",
     "Một phụ thuộc hàm duy nhất, xuất phát từ khoá chính."),
    ("tblDepartment", "depNum → depName, mgrSSN, mgrAssDate",
     "Nếu bổ sung UNIQUE(depName) thì có thêm depName → depNum, khi đó "
     "depName trở thành khoá dự tuyển."),
    ("tblEmployee",
     "empSSN → empName, empAddress, empSalary, empSex, empBirthdate, "
     "depNum, supervisorSSN, empStartdate",
     "Mọi thuộc tính đều phụ thuộc trực tiếp và duy nhất vào khoá chính."),
    ("tblProject", "proNum → proName, locNum, depNum",
     "Không tồn tại phụ thuộc bắc cầu vì locNum và depNum là khoá ngoại, "
     "không xác định thuộc tính nào khác trong bảng."),
    ("tblDepLocation", "{depNum, locNum} → ∅",
     "Bảng chỉ gồm các thuộc tính khoá, không có thuộc tính không khoá."),
    ("tblWorksOn", "{empSSN, proNum} → workHours",
     "workHours phụ thuộc đầy đủ vào cả hai thành phần của khoá chính."),
    ("tblDependent",
     "{empSSN, depName} → depSex, depBirthdate, depRelationship",
     "Phụ thuộc đầy đủ; không thành phần khoá nào đơn lẻ xác định được các "
     "thuộc tính mô tả."),
]

NORMAL_FORMS = [
    ("tblLocation", "locNum", "locName", "✔", "✔", "✔", "✔"),
    ("tblDepartment", "depNum", "depName, mgrSSN, mgrAssDate", "✔", "✔", "✔", "✔"),
    ("tblEmployee", "empSSN", "8 thuộc tính mô tả", "✔", "✔", "✔", "✔"),
    ("tblProject", "proNum", "proName, locNum, depNum", "✔", "✔", "✔", "✔"),
    ("tblDepLocation", "depNum, locNum", "— (không có)", "✔", "✔", "✔", "✔"),
    ("tblWorksOn", "empSSN, proNum", "workHours", "✔", "✔", "✔", "✔"),
    ("tblDependent", "depName, empSSN",
     "depSex, depBirthdate, depRelationship", "✔", "✔", "✔", "✔"),
]


def _key_label(table_info, col_name, fks):
    marks = []
    if col_name in table_info["pk"]:
        marks.append("PK")
    for fk in fks:
        if fk["column"] == col_name:
            marks.append("FK → `%s`" % fk["ref_table"])
    return ", ".join(marks) if marks else "—"


def build(ctx):
    doc, s, cap = ctx.doc, ctx.s, ctx.cap
    db = s["db"]

    # ================================================== CHUONG 4
    dh.heading(doc, "CHƯƠNG 4. THIẾT KẾ MỨC LOGIC VÀ VẬT LÝ", level=1, new_page=True)

    dh.heading(doc, "4.1. Lược đồ quan hệ", level=2)
    dh.para(
        doc,
        "Sau khi chuyển từ mô hình thực thể – liên kết sang mô hình quan hệ, "
        "cơ sở dữ liệu gồm bảy quan hệ với lược đồ như sau:",
    )
    dh.code_block(doc, SCHEMA_TEXT, size=8.5)
    dh.caption(doc, cap.fig(4, "Lược đồ quan hệ của cơ sở dữ liệu dbCOMPANY"))

    dh.heading(doc, "4.2. Chi tiết cấu trúc từng bảng", level=2)
    dh.para(
        doc,
        "Mỗi bảng dưới đây được trình bày kèm mục đích sử dụng, số bản ghi "
        "trong dữ liệu mẫu và mô tả chi tiết từng cột. Cột “Khoá” cho biết "
        "vai trò khoá chính (`PK`) hoặc khoá ngoại (`FK`) cùng bảng đích.",
    )

    order = ["tblLocation", "tblDepartment", "tblEmployee", "tblDepLocation",
             "tblProject", "tblWorksOn", "tblDependent"]
    for idx, tname in enumerate(order, start=1):
        info = db["tables"][tname]
        dh.heading(doc, "4.2.%d. Bảng %s" % (idx, tname), level=3)
        dh.para(
            doc,
            "%s Bảng có **%d cột**, khoá chính là `%s`, chứa **%s bản ghi** "
            "trong dữ liệu mẫu."
            % (TABLE_PURPOSE[tname], len(info["columns"]),
               ", ".join(info["pk"]), vn_int(s["row_counts"][tname])),
        )
        rows = []
        for i, c in enumerate(info["columns"], start=1):
            rows.append([
                str(i),
                "`%s`" % c["name"],
                "`%s`" % c["type"] + (" IDENTITY" if c["identity"] else ""),
                "Có" if c["nullable"] else "Không",
                _key_label(info, c["name"], info["fks"]),
                COL_DESC.get((tname, c["name"]), ""),
            ])
        dh.table(
            doc,
            ["STT", "Tên cột", "Kiểu dữ liệu", "NULL", "Khoá", "Diễn giải"],
            rows,
            widths=[1.3, 2.8, 2.4, 1.6, 3.1, 4.8],
            aligns=["center", None, None, "center", None, None],
            size=9.5,
        )
        dh.caption(doc, cap.table(4, "Cấu trúc bảng %s" % tname))

    dh.heading(doc, "4.3. Tổng hợp khoá chính", level=2)
    dh.table(
        doc,
        ["Bảng", "Tên ràng buộc", "Cột khoá chính", "Loại khoá"],
        [["`%s`" % t, "`%s`" % db["tables"][t]["pk_name"],
          ", ".join("`%s`" % c for c in db["tables"][t]["pk"]),
          "Khoá đơn" if len(db["tables"][t]["pk"]) == 1 else "Khoá kép"]
         for t in order],
        widths=[3.2, 4.6, 5.2, 3.0],
        aligns=[None, None, None, "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(4, "Tổng hợp khoá chính của bảy bảng"))
    n_single = sum(1 for t in order if len(db["tables"][t]["pk"]) == 1)
    dh.para(
        doc,
        "Toàn bộ %d bảng đều có khoá chính, trong đó %d bảng dùng khoá đơn và "
        "%d bảng dùng khoá kép. Nhờ vậy **toàn vẹn thực thể** được bảo đảm "
        "tuyệt đối: không thể tồn tại hai bản ghi trùng khoá hay bản ghi có "
        "khoá rỗng." % (len(order), n_single, len(order) - n_single),
        first_line=0.8,
    )

    dh.heading(doc, "4.4. Tổng hợp khoá ngoại", level=2)
    fk_rows = []
    for fk in db["fks"]:
        fk_rows.append([
            "`%s`" % fk["name"],
            "`%s`.`%s`" % (fk["table"], fk["column"]),
            "`%s`.`%s`" % (fk["ref_table"], fk["ref_column"]),
            "Không khai báo",
        ])
    for fk in s["missing_fks"]:
        fk_rows.append([
            "**(chưa có ràng buộc)**", "`%s`.`%s`" % (fk[0], fk[1]),
            "`%s`.`%s`" % (fk[2], fk[3]), "**Chưa tồn tại**",
        ])
    dh.table(
        doc,
        ["Tên ràng buộc", "Cột tham chiếu", "Bảng/cột đích", "Hành vi"],
        fk_rows,
        widths=[5.6, 4.3, 3.9, 2.2],
        aligns=[None, None, None, "center"],
        size=8.5,
    )
    dh.caption(doc, cap.table(4, "Tổng hợp khoá ngoại đã khai báo và khoá "
                                 "ngoại còn thiếu"))
    dh.para(
        doc,
        "Script khai báo **%d khoá ngoại**, bao phủ hầu hết các quan hệ nghiệp "
        "vụ. Tuy nhiên bảng `tblWorksOn` chỉ có khoá ngoại tới `tblProject` mà "
        "**không có khoá ngoại tới `tblEmployee`**, dù `empSSN` là một nửa "
        "khoá chính của bảng này. Ngoài ra, không khoá ngoại nào khai báo hành "
        "vi `ON DELETE` hay `ON UPDATE`, nghĩa là mọi ràng buộc đều mặc định "
        "`NO ACTION`: hệ quản trị sẽ từ chối thao tác xoá/cập nhật thay vì tự "
        "xử lý dữ liệu liên quan." % len(db["fks"]),
        first_line=0.8,
    )

    dh.heading(doc, "4.5. Phụ thuộc vòng và thứ tự nạp dữ liệu", level=2)
    dh.para(
        doc,
        "Hai bảng `tblDepartment` và `tblEmployee` tham chiếu lẫn nhau: "
        "`tblDepartment.mgrSSN` trỏ tới `tblEmployee.empSSN`, đồng thời "
        "`tblEmployee.depNum` trỏ ngược lại `tblDepartment.depNum`. Đây là một "
        "**phụ thuộc vòng** (circular reference) khiến không thể nạp trọn vẹn "
        "bảng này trước bảng kia.",
        first_line=0.8,
    )
    dh.para(
        doc,
        "Script `DBC.sql` giải quyết vấn đề này một cách khéo léo: toàn bộ dữ "
        "liệu được `INSERT` **trước**, sau đó mới dùng `ALTER TABLE … ADD "
        "CONSTRAINT` để tạo khoá ngoại. Nhờ vậy trong lúc nạp dữ liệu chưa có "
        "ràng buộc nào phải thoả mãn. Nếu muốn nạp dữ liệu khi khoá ngoại đã "
        "tồn tại, cần thực hiện theo trình tự sau:",
        first_line=0.8,
    )
    dh.bullets(doc, [
        "Nạp `tblLocation` (không phụ thuộc bảng nào).",
        "Nạp `tblDepartment` với `mgrSSN` tạm để `NULL`.",
        "Nạp `tblEmployee` với `supervisorSSN` để `NULL` ở cấp cao nhất, sau "
        "đó cập nhật dần theo cây phân cấp.",
        "Cập nhật (`UPDATE`) cột `mgrSSN` của `tblDepartment`.",
        "Nạp các bảng phụ thuộc: `tblDepLocation`, `tblProject`, "
        "`tblWorksOn`, `tblDependent`.",
    ])
    dh.note_box(
        doc,
        "Chính vì phụ thuộc vòng này mà các cột `tblDepartment.mgrSSN` và "
        "`tblEmployee.depNum` buộc phải cho phép `NULL`, dù về nghiệp vụ "
        "chúng là bắt buộc. Đây là một đánh đổi thiết kế cần được ghi nhận "
        "thay vì xem là lỗi.",
        title="Nhận định",
    )

    dh.heading(doc, "4.6. Nhận xét về lựa chọn kiểu dữ liệu", level=2)
    dh.table(
        doc,
        ["Kiểu đang dùng", "Cột áp dụng", "Đánh giá và lý do"],
        [
            ["`decimal(18,0)`", "`empSSN`, `mgrSSN`, `supervisorSSN`",
             "**Không phù hợp.** Mã định danh không dùng để tính toán số học. "
             "Kiểu này chiếm 9 byte, mất số 0 ở đầu và cho phép giá trị âm. "
             "Nên dùng `char(11)` hoặc `varchar(20)`."],
            ["`datetime`",
             "`mgrAssDate`, `empBirthdate`, `empStartdate`, `depBirthdate`",
             "**Dư thừa.** Toàn bộ giá trị trong dữ liệu mẫu đều có phần giờ "
             "là `00:00:00.000`. Kiểu `date` chỉ chiếm 3 byte thay vì 8 byte."],
            ["`nvarchar(50)`", "`depName`, `empName`, `empAddress`, `locName`, "
             "`proName`, `depRelationship`",
             "**Hợp lý.** Kiểu `nvarchar` hỗ trợ Unicode, cần thiết cho tiếng "
             "Việt. Riêng `empAddress` với 50 ký tự có thể chật nếu lưu địa "
             "chỉ đầy đủ."],
            ["`char(1)`", "`empSex`, `depSex`",
             "**Hợp lý về kích thước** nhưng thiếu ràng buộc `CHECK` nên vẫn "
             "nhận được ký tự bất kỳ."],
            ["`int`", "`depNum`, `locNum`, `proNum`, `workHours`",
             "**Hợp lý.** Phù hợp cho mã số nhỏ và số giờ nguyên."],
            ["`decimal(18,0)`", "`empSalary`",
             "**Chấp nhận được** nhưng phần thập phân bằng 0 khiến mọi giá trị "
             "lẻ bị làm tròn. Nên dùng `decimal(12,2)` hoặc `money`."],
        ],
        widths=[3.0, 5.2, 7.8],
        size=9.5,
    )
    dh.caption(doc, cap.table(4, "Đánh giá lựa chọn kiểu dữ liệu"))

    # ================================================== CHUONG 5
    dh.heading(doc, "CHƯƠNG 5. KIỂM TRA CHUẨN HOÁ DỮ LIỆU", level=1, new_page=True)

    dh.heading(doc, "5.1. Xác định các phụ thuộc hàm", level=2)
    dh.para(
        doc,
        "Bước đầu tiên của quá trình kiểm tra chuẩn hoá là liệt kê các phụ "
        "thuộc hàm (functional dependency) trong từng quan hệ. Ký hiệu "
        "`X → Y` nghĩa là giá trị của tập thuộc tính X xác định duy nhất giá "
        "trị của tập thuộc tính Y.",
    )
    dh.table(
        doc,
        ["Quan hệ", "Phụ thuộc hàm", "Nhận xét"],
        [["`%s`" % a, "`%s`" % b, c] for a, b, c in FUNCTIONAL_DEPS],
        widths=[2.8, 6.2, 7.0],
        size=9.5,
    )
    dh.caption(doc, cap.table(5, "Các phụ thuộc hàm trong bảy quan hệ"))

    dh.heading(doc, "5.2. Kiểm tra dạng chuẩn 1 (1NF)", level=2)
    dh.para(
        doc,
        "Một quan hệ đạt 1NF nếu mọi thuộc tính đều mang **giá trị nguyên tố** "
        "(atomic), không có thuộc tính đa trị hay thuộc tính phức hợp, và "
        "không có nhóm thuộc tính lặp.",
        first_line=0.8,
    )
    dh.para(
        doc,
        "Kiểm tra trên `dbCOMPANY`: tất cả cột đều thuộc kiểu vô hướng "
        "(`int`, `decimal`, `char`, `nvarchar`, `datetime`); không có cột nào "
        "lưu danh sách giá trị phân tách bằng dấu phẩy. Đặc biệt, quan hệ "
        "nhiều – nhiều giữa phòng ban và địa điểm **không** được lưu bằng một "
        "cột đa trị mà đã được tách thành bảng `tblDepLocation`. Tương tự, "
        "việc nhân viên tham gia nhiều dự án được tách sang `tblWorksOn`. "
        "Vì vậy **cả bảy quan hệ đều đạt 1NF**.",
        first_line=0.8,
    )

    dh.heading(doc, "5.3. Kiểm tra dạng chuẩn 2 (2NF)", level=2)
    dh.para(
        doc,
        "Một quan hệ đạt 2NF nếu đã đạt 1NF và **không tồn tại phụ thuộc hàm "
        "bộ phận**, tức là không có thuộc tính không khoá nào chỉ phụ thuộc "
        "vào một phần của khoá chính. Điều kiện này chỉ cần xét với các quan "
        "hệ có khoá chính gồm nhiều thuộc tính.",
        first_line=0.8,
    )
    dh.para(doc, "Ba quan hệ có khoá kép cần được xem xét:")
    dh.bullets(doc, [
        "`tblDepLocation(depNum, locNum)`: không có thuộc tính không khoá nào, "
        "do đó không thể tồn tại phụ thuộc bộ phận. Đạt 2NF.",
        "`tblWorksOn(empSSN, proNum, workHours)`: `workHours` là số giờ của "
        "**một nhân viên cụ thể trên một dự án cụ thể**, nên phụ thuộc vào cả "
        "hai thành phần khoá. Nếu chỉ biết `empSSN` hoặc chỉ biết `proNum` thì "
        "không xác định được `workHours`. Đạt 2NF.",
        "`tblDependent(depName, empSSN, …)`: giới tính, ngày sinh và quan hệ "
        "của người phụ thuộc chỉ được xác định khi biết đồng thời tên người "
        "phụ thuộc và nhân viên tương ứng (hai nhân viên khác nhau có thể có "
        "người phụ thuộc trùng tên nhưng khác thông tin). Đạt 2NF.",
    ])
    dh.para(
        doc,
        "Bốn quan hệ còn lại có khoá chính đơn nên **đạt 2NF một cách hiển "
        "nhiên**. Kết luận: cả bảy quan hệ đều đạt 2NF.",
        first_line=0.8,
    )

    dh.heading(doc, "5.4. Kiểm tra dạng chuẩn 3 (3NF) và BCNF", level=2)
    dh.para(
        doc,
        "Một quan hệ đạt 3NF nếu đã đạt 2NF và **không tồn tại phụ thuộc hàm "
        "bắc cầu**: không có thuộc tính không khoá nào xác định một thuộc tính "
        "không khoá khác. Chặt hơn nữa, quan hệ đạt BCNF nếu với mọi phụ thuộc "
        "hàm `X → Y` không tầm thường thì X phải là một siêu khoá.",
        first_line=0.8,
    )
    dh.para(
        doc,
        "Trường hợp cần xét kỹ nhất là `tblEmployee`, vì bảng này chứa nhiều "
        "thuộc tính và hai khoá ngoại. Câu hỏi đặt ra là liệu `depNum` có xác "
        "định thuộc tính nào khác trong bảng hay không. Câu trả lời là không: "
        "`depNum` chỉ trỏ sang bảng `tblDepartment`, còn tên phòng ban và mã "
        "trưởng phòng được lưu ở bảng đó chứ **không** bị lặp lại trong "
        "`tblEmployee`. Đây chính là điểm mà nhiều thiết kế sai thường vi phạm "
        "(lưu kèm `depName` trong bảng nhân viên, tạo ra phụ thuộc bắc cầu "
        "`empSSN → depNum → depName`).",
        first_line=0.8,
    )
    dh.para(
        doc,
        "Tương tự, `tblProject` không lưu kèm tên phòng ban hay tên địa điểm; "
        "`tblDepartment` không lưu kèm tên trưởng phòng. Trong mọi quan hệ, "
        "vế trái của mỗi phụ thuộc hàm không tầm thường đều chính là khoá "
        "chính, do đó **cả bảy quan hệ đều đạt 3NF và đồng thời đạt BCNF**.",
        first_line=0.8,
    )
    dh.table(
        doc,
        ["Quan hệ", "Khoá chính", "Thuộc tính không khoá",
         "1NF", "2NF", "3NF", "BCNF"],
        [["`%s`" % a, "`%s`" % b, c, d, e, f, g]
         for a, b, c, d, e, f, g in NORMAL_FORMS],
        widths=[3.0, 3.0, 4.6, 1.35, 1.35, 1.35, 1.35],
        aligns=[None, None, None, "center", "center", "center", "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(5, "Tổng hợp kết quả kiểm tra chuẩn hoá"))

    dh.heading(doc, "5.5. Thảo luận: những điểm có thể chuẩn hoá thêm", level=2)
    dh.para(
        doc,
        "Đạt BCNF không có nghĩa là thiết kế đã tối ưu về mọi mặt. Quá trình "
        "phân tích phát hiện hai điểm đáng bàn thêm, tuy không phải vi phạm "
        "dạng chuẩn theo định nghĩa hình thức:",
        first_line=0.8,
    )
    dh.bullets(doc, [
        "**Thuộc tính `empAddress` nên được tham chiếu hoá.** Cột này lưu tên "
        "tỉnh/thành dưới dạng văn bản tự do trong khi hệ thống đã có bảng "
        "`tblLocation`. Hệ quả là dữ liệu thiếu nhất quán (xem mục 7.3) và "
        "không thể thống kê nhân sự theo địa bàn một cách đáng tin cậy. Giải "
        "pháp là tách một bảng `tblProvince` (hoặc dùng lại `tblLocation`) và "
        "thay `empAddress` bằng khoá ngoại.",
        "**Khoá chính của `tblDependent` dựa trên tên người.** Về hình thức "
        "đây là lựa chọn đúng cho thực thể yếu, nhưng trên thực tế nó gây hai "
        "bất tiện: một nhân viên không thể có hai người phụ thuộc trùng tên, "
        "và mỗi lần sửa lỗi chính tả trong tên là một lần cập nhật khoá chính. "
        "Thiết kế thực dụng hơn là dùng khoá thay thế (`dependentID` tự tăng) "
        "kèm ràng buộc `UNIQUE(empSSN, depName)`.",
    ])
    dh.para(
        doc,
        "Ngoài ra, cột `mgrSSN` trong `tblDepartment` về bản chất là dữ liệu "
        "có thể suy ra được nếu bảng `tblEmployee` có thêm thuộc tính đánh dấu "
        "chức vụ. Việc lưu tách riêng như hiện tại giúp truy vấn trưởng phòng "
        "nhanh hơn, nhưng đòi hỏi ràng buộc bổ sung để tránh tình trạng một "
        "người được ghi là trưởng phòng của phòng ban mà mình không trực thuộc.",
        first_line=0.8,
    )
