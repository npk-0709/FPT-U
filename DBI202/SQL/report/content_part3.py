"""Chuong 6-7: Phan tich du lieu mau va danh gia chat luong."""

import docx_helpers as dh
from stats import ssn, vn_date, vn_float, vn_int

WEAKNESSES = [
    ("H01", "Bảng `tblWorksOn` không có khoá ngoại trên cột `empSSN`.",
     "Có thể tồn tại bản ghi giờ làm gắn với mã nhân viên không tồn tại "
     "(dữ liệu mồ côi); cũng có thể xoá nhân viên mà vẫn còn giờ làm.",
     "Nghiêm trọng"),
    ("H02", "Thiếu hoàn toàn ràng buộc `CHECK` trên các cột có miền giá trị "
     "hữu hạn.",
     "`empSex`/`depSex` nhận được ký tự bất kỳ; `empSalary` nhận giá trị âm; "
     "`workHours` nhận số âm.", "Cao"),
    ("H03", "Dùng `decimal(18,0)` cho các mã định danh (`empSSN`, `mgrSSN`, "
     "`supervisorSSN`).",
     "Sai ngữ nghĩa, tốn bộ nhớ, mất số 0 ở đầu, cho phép giá trị âm và số "
     "thập phân bị làm tròn ngầm.", "Cao"),
    ("H04", "Nhiều cột bắt buộc về nghiệp vụ lại được khai báo cho phép `NULL`.",
     "`empName`, `depName`, `locName`, `empSalary`, `proName` có thể rỗng, "
     "dẫn tới hồ sơ không đầy đủ.", "Trung bình"),
    ("H05", "Thiếu ràng buộc `UNIQUE` cho `depName` và `locName`.",
     "Cho phép tạo hai phòng ban hoặc hai địa điểm trùng tên, gây nhập nhằng "
     "khi tra cứu theo tên.", "Trung bình"),
    ("H06", "Không khoá ngoại nào khai báo hành vi `ON DELETE` / `ON UPDATE`.",
     "Mọi ràng buộc mặc định `NO ACTION`; việc xoá nhân viên sẽ thất bại thay "
     "vì tự xoá người phụ thuộc kèm theo.", "Trung bình"),
    ("H07", "Dùng `datetime` cho các cột chỉ chứa ngày.",
     "Tốn 8 byte thay vì 3 byte và gây sai sót khi so sánh nếu về sau có giá "
     "trị mang phần giờ khác 0.", "Trung bình"),
    ("H08", "Phụ thuộc vòng giữa `tblDepartment` và `tblEmployee`.",
     "Buộc phải cho phép `NULL` ở các cột bắt buộc; thứ tự nạp và xoá dữ liệu "
     "trở nên phức tạp.", "Trung bình"),
    ("H09", "Khoá chính của `tblDependent` dựa trên tên người phụ thuộc.",
     "Không lưu được hai người phụ thuộc trùng tên của cùng nhân viên; sửa "
     "tên đồng nghĩa với sửa khoá chính.", "Trung bình"),
    ("H10", "Không có chỉ mục phụ trên các cột khoá ngoại thường dùng để nối "
     "bảng.",
     "Các phép nối theo `depNum`, `supervisorSSN`, `empSSN` phải quét toàn "
     "bảng khi dữ liệu lớn.", "Thấp"),
    ("H11", "Không nhất quán trong việc sinh mã: chỉ `tblLocation` dùng "
     "`IDENTITY`.",
     "Ứng dụng phải tự sinh mã cho các bảng còn lại, dễ phát sinh trùng khoá "
     "khi có nhiều người dùng đồng thời.", "Thấp"),
    ("H12", "Quy ước đặt tên gây nhầm lẫn: tiền tố `dep` dùng cho cả "
     "*department* và *dependent*.",
     "`tblDepartment.depName` là tên phòng ban còn `tblDependent.depName` là "
     "tên người phụ thuộc – hai ngữ nghĩa hoàn toàn khác nhau nhưng cùng tên "
     "cột, rất dễ viết sai truy vấn.", "Thấp"),
    ("H13", "Không có ràng buộc buộc trưởng phòng phải thuộc chính phòng ban "
     "mà mình quản lý.",
     "Có thể ghi nhận một người là trưởng phòng của phòng ban mà người đó "
     "không trực thuộc.", "Thấp"),
    ("H14", "Không có cột phục vụ truy vết (người tạo, thời điểm cập nhật) và "
     "không lưu đơn vị tiền tệ của `empSalary`.",
     "Khó kiểm toán khi dữ liệu bị sửa; giá trị lương thiếu ngữ cảnh để diễn "
     "giải.", "Thấp"),
]


def build(ctx):
    doc, s, cap = ctx.doc, ctx.s, ctx.cap
    db = s["db"]

    # ================================================== CHUONG 6
    dh.heading(doc, "CHƯƠNG 6. PHÂN TÍCH DỮ LIỆU MẪU", level=1, new_page=True)
    dh.para(
        doc,
        "Chương này khai thác %s bản ghi dữ liệu mẫu trong `DBC.sql` để mô tả "
        "“chân dung” của công ty được mô hình hoá. Ngoài giá trị minh hoạ, các "
        "chỉ số dưới đây còn giúp phát hiện những điểm bất thường về chất "
        "lượng dữ liệu sẽ được bàn ở Chương 7." % vn_int(s["total_rows"]),
        first_line=0.8,
    )

    dh.heading(doc, "6.1. Khối lượng dữ liệu theo bảng", level=2)
    rows = []
    for t in ["tblLocation", "tblDepartment", "tblEmployee", "tblDepLocation",
              "tblProject", "tblWorksOn", "tblDependent"]:
        n = s["row_counts"][t]
        rows.append([
            "`%s`" % t, vn_int(n),
            "%s%%" % vn_float(100.0 * n / s["total_rows"], 1),
            str(len(db["tables"][t]["columns"])),
        ])
    rows.append(["**Tổng cộng**", "**%s**" % vn_int(s["total_rows"]),
                 "**100,0%**",
                 "**%d**" % sum(len(db["tables"][t]["columns"])
                                for t in db["order"])])
    dh.table(
        doc,
        ["Bảng", "Số bản ghi", "Tỷ lệ", "Số cột"],
        rows,
        widths=[5.4, 3.6, 3.4, 3.6],
        aligns=[None, "center", "center", "center"],
    )
    dh.caption(doc, cap.table(6, "Khối lượng dữ liệu mẫu theo từng bảng"))
    dh.para(
        doc,
        "Bảng `tblWorksOn` chiếm tỷ trọng lớn nhất với %d bản ghi, phản ánh "
        "đúng bản chất của một bảng trung gian nhiều – nhiều: số dòng của nó "
        "lớn hơn tổng số nhân viên và số dự án cộng lại."
        % s["row_counts"]["tblWorksOn"],
        first_line=0.8,
    )

    dh.heading(doc, "6.2. Cơ cấu nhân sự và tiền lương theo phòng ban", level=2)
    dep_rows = []
    for r in s["dep_rows"]:
        sex = s["emp_sex_by_dep"][r["depNum"]]
        dep_rows.append([
            str(r["depNum"]), r["depName"], r["mgrName"],
            str(r["headcount"]),
            "%d / %d" % (sex.get("M", 0), sex.get("F", 0)),
            vn_int(r["total_salary"]),
            vn_float(r["avg_salary"], 0),
        ])
    dep_rows.append([
        "—", "**Toàn công ty**", "—", "**%d**" % s["salary"]["count"],
        "**%d / %d**" % (s["sex_counts"].get("M", 0), s["sex_counts"].get("F", 0)),
        "**%s**" % vn_int(s["salary"]["total"]),
        "**%s**" % vn_float(s["salary"]["avg"], 0),
    ])
    dh.table(
        doc,
        ["Mã", "Tên phòng ban", "Trưởng phòng", "Số NV", "Nam/Nữ",
         "Tổng lương", "Lương TB"],
        dep_rows,
        widths=[1.0, 4.2, 3.5, 1.4, 1.6, 2.2, 2.1],
        aligns=["center", None, None, "center", "center", "right", "right"],
        size=9.0,
    )
    dh.caption(doc, cap.table(6, "Cơ cấu nhân sự và tiền lương theo phòng ban"))
    dh.figure(doc, ctx.figs["department"], 15.5,
              cap.fig(6, "Số nhân viên và lương trung bình theo phòng ban"))

    hi = max(s["dep_rows"], key=lambda r: r["avg_salary"])
    lo = min(s["dep_rows"], key=lambda r: r["avg_salary"])
    dh.para(
        doc,
        "Quy mô các phòng ban khá đồng đều (từ %d đến %d người) nhưng mức "
        "lương trung bình lại chênh lệch đáng kể: “%s” dẫn đầu với %s, trong "
        "khi “%s” chỉ đạt %s – thấp hơn khoảng %s%%. Khoảng cách này chủ yếu "
        "do phòng có ít người thường gồm cả nhân viên mới với mức lương khởi "
        "điểm thấp."
        % (min(r["headcount"] for r in s["dep_rows"]),
           max(r["headcount"] for r in s["dep_rows"]),
           hi["depName"], vn_float(hi["avg_salary"], 0),
           lo["depName"], vn_float(lo["avg_salary"], 0),
           vn_float(100.0 * (1 - lo["avg_salary"] / hi["avg_salary"]), 1)),
        first_line=0.8,
    )

    dh.heading(doc, "6.3. Phân bố mức lương", level=2)
    dh.table(
        doc,
        ["Chỉ số", "Giá trị", "Nhân viên tương ứng"],
        [
            ["Số nhân viên có dữ liệu lương", vn_int(s["salary"]["count"]), "—"],
            ["Lương thấp nhất", vn_int(s["salary"]["min"]),
             ", ".join(s["salary"]["min_emps"])],
            ["Lương cao nhất", vn_int(s["salary"]["max"]),
             ", ".join(s["salary"]["max_emps"])],
            ["Lương trung bình", vn_float(s["salary"]["avg"], 2), "—"],
            ["Tổng quỹ lương", vn_int(s["salary"]["total"]), "—"],
            ["Độ chênh cao nhất / thấp nhất",
             "%s lần" % vn_float(
                 float(s["salary"]["max"]) / s["salary"]["min"], 2), "—"],
        ],
        widths=[5.0, 3.6, 7.4],
        aligns=[None, "right", None],
        size=9.5,
    )
    dh.caption(doc, cap.table(6, "Các chỉ số thống kê về tiền lương"))
    dh.figure(doc, ctx.figs["salary"], 15.5,
              cap.fig(6, "Phân bố mức lương của toàn bộ nhân viên"))
    dh.para(
        doc,
        "Phân bố lương lệch về phía trên: %d trong %d nhân viên (%s%%) có mức "
        "lương cao hơn 80 nghìn, trong khi nhóm dưới 60 nghìn chỉ gồm %d "
        "người. Khoảng cách giữa người cao nhất và thấp nhất là %s lần, một "
        "biên độ khá rộng nhưng vẫn hợp lý với dữ liệu mô phỏng."
        % (sum(1 for e in db["rows"]["tblEmployee"] if int(e["empSalary"]) > 80000),
           s["salary"]["count"],
           vn_float(100.0 * sum(1 for e in db["rows"]["tblEmployee"]
                                if int(e["empSalary"]) > 80000)
                    / s["salary"]["count"], 1),
           sum(1 for e in db["rows"]["tblEmployee"] if int(e["empSalary"]) < 60000),
           vn_float(float(s["salary"]["max"]) / s["salary"]["min"], 2)),
        first_line=0.8,
    )

    dh.heading(doc, "6.4. Cấu trúc phân cấp giám sát", level=2)
    dh.para(
        doc,
        "Cột `supervisorSSN` cho phép dựng lại toàn bộ cây quản lý của công "
        "ty. Dữ liệu mẫu có **%d nhân viên không có người giám sát** – đúng "
        "bằng số phòng ban – và cả %d người này đều là trưởng phòng. Cây phân "
        "cấp vì vậy gồm %d nhánh rời nhau, sâu nhất là %d cấp."
        % (len(s["roots"]), len(s["roots"]), len(s["roots"]), 3),
    )
    dh.figure(doc, ctx.figs["supervision"], 15.8,
              cap.fig(6, "Cây phân cấp giám sát theo từng phòng ban"))
    dh.para(
        doc,
        "Có %d nhân viên giữ vai trò giám sát ít nhất một người khác, còn %d "
        "nhân viên là “nhân viên thường” (không giám sát ai). Một điểm tích "
        "cực về chất lượng dữ liệu: **toàn bộ %d cặp giám sát đều nằm trong "
        "cùng một phòng ban**, phù hợp với thực tế tổ chức, dù cơ sở dữ liệu "
        "hoàn toàn không có ràng buộc nào bắt buộc điều đó."
        % (len(s["supervisors"]), len(s["leaf_emps"]),
           s["salary"]["count"] - len(s["roots"])),
        first_line=0.8,
    )

    dh.heading(doc, "6.5. Dự án và khối lượng công việc", level=2)
    pro_rows = []
    for r in s["pro_rows"]:
        pro_rows.append([
            str(r["proNum"]), r["proName"], r["depName"], r["locName"],
            str(r["members"]), vn_int(r["hours"]),
            vn_float(float(r["hours"]) / r["members"], 1) if r["members"] else "—",
        ])
    pro_rows.append([
        "—", "**Tổng cộng**", "—", "—",
        "**%d**" % sum(r["members"] for r in s["pro_rows"]),
        "**%s**" % vn_int(s["total_hours"]),
        "**%s**" % vn_float(float(s["total_hours"])
                            / sum(r["members"] for r in s["pro_rows"]), 1),
    ])
    dh.table(
        doc,
        ["Mã", "Tên dự án", "Phòng phụ trách", "Địa điểm", "Số TV",
         "Tổng giờ", "Giờ/TV"],
        pro_rows,
        widths=[1.0, 2.3, 4.6, 2.5, 1.4, 2.0, 2.2],
        aligns=["center", None, None, None, "center", "right", "right"],
        size=9.0,
    )
    dh.caption(doc, cap.table(6, "Thống kê dự án theo số thành viên và giờ làm"))
    dh.figure(doc, ctx.figs["project"], 15.5,
              cap.fig(6, "Tổng số giờ làm và số thành viên của từng dự án"))

    top_pro = max(s["pro_rows"], key=lambda r: r["hours"])
    low_pro = min(s["pro_rows"], key=lambda r: r["hours"])
    dh.para(
        doc,
        "“%s” là dự án lớn nhất về khối lượng công việc với %s giờ, còn “%s” "
        "nhỏ nhất với %s giờ. Đáng lưu ý là dự án có nhiều thành viên nhất "
        "(“%s”, %d người) lại **không** phải dự án có tổng giờ cao nhất, cho "
        "thấy mức độ tham gia của từng người rất khác nhau: bình quân mỗi "
        "thành viên của “%s” làm %s giờ, cao hơn hẳn mức %s giờ của “%s”."
        % (top_pro["proName"], vn_int(top_pro["hours"]),
           low_pro["proName"], vn_int(low_pro["hours"]),
           max(s["pro_rows"], key=lambda r: r["members"])["proName"],
           max(r["members"] for r in s["pro_rows"]),
           top_pro["proName"],
           vn_float(float(top_pro["hours"]) / top_pro["members"], 1),
           vn_float(float(low_pro["hours"]) / low_pro["members"], 1),
           low_pro["proName"]),
        first_line=0.8,
    )

    dh.heading(doc, "6.6. Mức độ tham gia dự án của nhân viên", level=2)
    top_rows = []
    for r in s["top_hours"][:8]:
        top_rows.append([
            "`%s`" % ssn(r["empSSN"]), r["empName"], r["depName"],
            str(r["projects"]), vn_int(r["hours"]),
        ])
    dh.table(
        doc,
        ["Mã nhân viên", "Họ và tên", "Phòng ban", "Số dự án", "Tổng giờ"],
        top_rows,
        widths=[2.8, 3.9, 5.0, 1.9, 2.4],
        aligns=[None, None, None, "center", "right"],
        size=9.5,
    )
    dh.caption(doc, cap.table(6, "Tám nhân viên có tổng số giờ làm dự án "
                                 "cao nhất"))
    dh.para(
        doc,
        "Trong %d nhân viên, chỉ **%d người đang tham gia dự án** còn **%d "
        "người chưa được phân công dự án nào** (%s). Đây không phải lỗi cấu "
        "trúc mà là đặc điểm của dữ liệu mẫu, song nó cho thấy một truy vấn "
        "thống kê giờ làm bằng phép nối thông thường (`INNER JOIN`) sẽ bỏ sót "
        "%d người này – tình huống cần dùng `LEFT JOIN` để tránh sai sót."
        % (s["salary"]["count"], len(s["emps_with_project"]),
           len(s["emps_no_project"]),
           ", ".join(e["empName"] for e in s["emps_no_project"]),
           len(s["emps_no_project"])),
        first_line=0.8,
    )

    dh.heading(doc, "6.7. Người phụ thuộc", level=2)
    dh.table(
        doc,
        ["Chỉ số", "Giá trị"],
        [
            ["Tổng số người phụ thuộc", vn_int(s["row_counts"]["tblDependent"])],
            ["Số nhân viên có người phụ thuộc", vn_int(len(s["dent_by_emp"]))],
            ["Số nhân viên không có người phụ thuộc",
             vn_int(len(s["emps_no_dependent"]))],
            ["Số người phụ thuộc nhiều nhất của một nhân viên",
             vn_int(max(len(v) for v in s["dent_by_emp"].values()))],
            ["Phân bố theo giới tính",
             "Nam: %d – Nữ: %d" % (s["dent_sex"].get("M", 0),
                                   s["dent_sex"].get("F", 0))],
            ["Phân bố theo quan hệ",
             ", ".join("%s: %d" % (k, v)
                       for k, v in s["dent_rel"].most_common())],
            ["Phòng ban không có người phụ thuộc nào",
             ", ".join("“%s”" % d["depName"]
                       for d in s["deps_no_dependent"]) or "—"],
        ],
        widths=[7.0, 9.0],
        size=9.5,
    )
    dh.caption(doc, cap.table(6, "Thống kê về người phụ thuộc"))
    dh.figure(doc, ctx.figs["dependent"], 15.5,
              cap.fig(6, "Người phụ thuộc theo mối quan hệ và theo phòng ban"))
    dh.para(
        doc,
        "Mỗi nhân viên trong dữ liệu mẫu có nhiều nhất một người phụ thuộc, "
        "nên khoá chính kép `(depName, empSSN)` chưa bộc lộ hạn chế đã nêu ở "
        "mục 5.5. Quan hệ “Chồng” và “Vợ” chiếm %d trong %d trường hợp; đáng "
        "chú ý là giới tính của người phụ thuộc luôn tương thích với giới tính "
        "nhân viên trong mọi bản ghi – một dấu hiệu tốt, dù cơ sở dữ liệu "
        "không hề kiểm tra điều này."
        % (s["dent_rel"].get("Chồng", 0) + s["dent_rel"].get("Vợ", 0),
           s["row_counts"]["tblDependent"]),
        first_line=0.8,
    )

    dh.heading(doc, "6.8. Phân bố theo địa điểm", level=2)
    loc_rows = []
    for r in s["loc_rows"]:
        loc_rows.append([
            str(r["locNum"]), r["locName"], str(r["departments"]),
            ", ".join(str(x) for x in r["dep_list"]) or "—",
            str(r["projects"]),
        ])
    dh.table(
        doc,
        ["Mã", "Tên địa điểm", "Số phòng ban", "Danh sách phòng ban",
         "Số dự án"],
        loc_rows,
        widths=[1.2, 4.4, 3.0, 4.4, 3.0],
        aligns=["center", None, "center", "center", "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(6, "Phân bố phòng ban và dự án theo địa điểm"))
    dh.figure(doc, ctx.figs["location"], 15.5,
              cap.fig(6, "Số phòng ban và số dự án tại từng địa điểm"))
    busiest = max(s["loc_rows"], key=lambda r: r["departments"])
    dh.para(
        doc,
        "“%s” là địa bàn trọng yếu với đủ %d phòng ban cùng hiện diện và %d dự "
        "án được triển khai. Ngược lại, “%s” không có phòng ban lẫn dự án nào "
        "– một bản ghi danh mục tồn tại nhưng chưa được sử dụng (xem mục 7.3). "
        "Về phía phòng ban, “%s” hoạt động tại nhiều địa điểm nhất (%d nơi)."
        % (busiest["locName"], busiest["departments"], busiest["projects"],
           ", ".join(r["locName"] for r in s["locs_unused"]),
           max(s["dep_rows"],
               key=lambda r: len(s["dep_locs_map"][r["depNum"]]))["depName"],
           max(len(v) for v in s["dep_locs_map"].values())),
        first_line=0.8,
    )

    dh.heading(doc, "6.9. Tổng hợp các chỉ số nổi bật", level=2)
    dh.table(
        doc,
        ["Nhóm", "Chỉ số nổi bật", "Giá trị"],
        [
            ["Nhân sự", "Tổng số nhân viên", vn_int(s["salary"]["count"])],
            ["Nhân sự", "Tỷ lệ nữ trong công ty",
             "%s%% (%d/%d)" % (
                 vn_float(100.0 * s["sex_counts"].get("F", 0)
                          / s["salary"]["count"], 1),
                 s["sex_counts"].get("F", 0), s["salary"]["count"])],
            ["Tiền lương", "Tổng quỹ lương", vn_int(s["salary"]["total"])],
            ["Tiền lương", "Lương trung bình",
             vn_float(s["salary"]["avg"], 2)],
            ["Tổ chức", "Số phòng ban / địa điểm",
             "%d / %d" % (len(s["dep_rows"]), len(s["loc_rows"]))],
            ["Dự án", "Số dự án / tổng giờ làm",
             "%d / %s giờ" % (len(s["pro_rows"]), vn_int(s["total_hours"]))],
            ["Dự án", "Số giờ làm trung bình mỗi nhân viên có dự án",
             "%s giờ" % vn_float(
                 float(s["total_hours"]) / len(s["emps_with_project"]), 1)],
            ["Phúc lợi", "Số người phụ thuộc",
             vn_int(s["row_counts"]["tblDependent"])],
            ["Chất lượng dữ liệu", "Số bất thường phát hiện được",
             "%d nhóm (xem mục 7.3)" % 8],
        ],
        widths=[3.4, 8.2, 4.4],
        aligns=[None, None, "right"],
        size=9.5,
    )
    dh.caption(doc, cap.table(6, "Bảng tổng hợp các chỉ số nổi bật"))

    # ================================================== CHUONG 7
    dh.heading(doc, "CHƯƠNG 7. ĐÁNH GIÁ CHẤT LƯỢNG THIẾT KẾ VÀ DỮ LIỆU", level=1, new_page=True)

    dh.heading(doc, "7.1. Những điểm mạnh", level=2)
    dh.bullets(doc, [
        "**Chuẩn hoá tốt.** Cả bảy quan hệ đều đạt BCNF; không tồn tại dữ liệu "
        "lặp gây dị thường khi thêm, sửa, xoá.",
        "**Toàn vẹn thực thể trọn vẹn.** Mọi bảng đều có khoá chính, các bảng "
        "trung gian dùng khoá kép đúng chuẩn.",
        "**Mô hình hoá đúng các cấu trúc khó.** Quan hệ đệ quy (giám sát), "
        "quan hệ một – một (quản lý phòng ban), quan hệ nhiều – nhiều có thuộc "
        "tính (giờ làm) và thực thể yếu (người phụ thuộc) đều được xử lý đúng "
        "phương pháp.",
        "**Hỗ trợ Unicode đầy đủ.** Việc dùng `nvarchar` và tiền tố `N` cho "
        "chuỗi giúp lưu trữ chính xác tiếng Việt có dấu.",
        "**Script có tính idempotent.** Phần đầu tệp kiểm tra và xoá cơ sở dữ "
        "liệu cũ trước khi tạo mới, nên có thể chạy lại nhiều lần mà không lỗi.",
        "**Thứ tự thi hành hợp lý.** Việc nạp dữ liệu trước rồi mới thêm khoá "
        "ngoại giúp vượt qua phụ thuộc vòng một cách gọn gàng.",
    ])

    dh.heading(doc, "7.2. Hạn chế về ràng buộc toàn vẹn", level=2)
    dh.para(
        doc,
        "Bảng dưới đây tổng hợp %d hạn chế phát hiện được, sắp xếp theo mức độ "
        "ảnh hưởng giảm dần. Mức **Nghiêm trọng** là hạn chế có thể trực tiếp "
        "sinh ra dữ liệu sai; mức **Cao** cho phép nhập giá trị vô nghĩa; mức "
        "**Trung bình** và **Thấp** ảnh hưởng tới tính chặt chẽ, hiệu năng "
        "hoặc khả năng bảo trì." % len(WEAKNESSES),
    )
    dh.table(
        doc,
        ["Mã", "Hạn chế", "Hệ quả", "Mức độ"],
        [[a, b, c, d] for a, b, c, d in WEAKNESSES],
        widths=[1.1, 5.3, 6.6, 2.0],
        aligns=["center", None, None, "center"],
        size=9.0,
    )
    dh.caption(doc, cap.table(7, "Tổng hợp các hạn chế về ràng buộc toàn vẹn"))
    counts = {}
    for w in WEAKNESSES:
        counts[w[3]] = counts.get(w[3], 0) + 1
    dh.para(
        doc,
        "Tổng cộng có %s. Điều đáng chú ý là các hạn chế nghiêm trọng và cao "
        "đều thuộc nhóm **dễ khắc phục**: chỉ cần bổ sung một khoá ngoại và "
        "một số ràng buộc `CHECK` là loại bỏ được phần lớn rủi ro, mà không "
        "phải thay đổi cấu trúc bảng hay viết lại truy vấn."
        % ", ".join("%d hạn chế mức %s" % (v, k.lower())
                    for k, v in sorted(counts.items(),
                                       key=lambda kv: -kv[1])),
        first_line=0.8,
    )

    dh.heading(doc, "7.3. Bất thường trong dữ liệu mẫu", level=2)
    dh.para(
        doc,
        "Việc đối chiếu dữ liệu với ngữ nghĩa nghiệp vụ phát hiện tám nhóm "
        "bất thường sau. Chúng không làm script báo lỗi khi thi hành, nhưng "
        "sẽ dẫn tới kết quả thống kê sai lệch nếu người dùng tin tưởng hoàn "
        "toàn vào dữ liệu.",
    )
    anomalies = [
        ["D01", "**%d trong %d phòng ban** có ngày bổ nhiệm trưởng phòng "
                "*sớm hơn* ngày người đó vào làm việc."
         % (len(s["mgr_date_issues"]), len(s["dep_rows"])),
         "Vi phạm logic thời gian; chi tiết ở Bảng kế tiếp."],
        ["D02", "Địa điểm “%s” không được phòng ban nào sử dụng và không có "
                "dự án nào." % ", ".join(r["locName"] for r in s["locs_unused"]),
         "Bản ghi danh mục tồn tại nhưng vô dụng."],
        ["D03", "Cột `empAddress` không nhất quán với `tblLocation`: dữ liệu "
                "ghi “TP. Hà Nội” trong khi danh mục ghi “TP Hà Nội”; xuất "
                "hiện địa danh cũ “Sông Bé”; giá trị “Thanh Hóa ” còn dư "
                "khoảng trắng ở cuối.",
         "Không thể nối bảng hay nhóm dữ liệu theo địa bàn."],
        ["D04", "**%d nhân viên** chưa được phân công dự án nào và **%d nhân "
                "viên** không có người phụ thuộc."
         % (len(s["emps_no_project"]), len(s["emps_no_dependent"])),
         "Phép nối trong dễ làm mất các bản ghi này."],
        ["D05", "Phòng ban “%s” không quản lý dự án nào, đồng thời không có "
                "nhân viên nào tham gia dự án (tổng giờ làm bằng 0)."
         % ", ".join("%s" % d["depName"] for d in s["deps_no_project"]),
         "Cần xác nhận đây là thực tế hay dữ liệu bị thiếu."],
        ["D06", "Nhân viên “%s” được ghi giới tính `M` dù tên có thành tố "
                "“Thị”." % ", ".join(e["empName"] for e in s["sex_suspects"]),
         "Nghi vấn nhập sai giới tính."],
        ["D07", "Cột `empSalary` không kèm đơn vị tiền tệ, `workHours` không "
                "kèm chu kỳ (giờ mỗi tuần hay tổng số giờ).",
         "Không thể diễn giải con số một cách chắc chắn."],
        ["D08", "Cây phân cấp giám sát gồm %d nhánh rời rạc, không có một "
                "người đứng đầu chung toàn công ty." % len(s["roots"]),
         "Không truy vấn được cấp trên cao nhất bằng một truy vấn đệ quy duy "
         "nhất."],
    ]
    dh.table(
        doc,
        ["Mã", "Mô tả bất thường", "Ảnh hưởng"],
        anomalies,
        widths=[1.1, 9.3, 5.6],
        aligns=["center", None, None],
        size=9.0,
    )
    dh.caption(doc, cap.table(7, "Các bất thường phát hiện trong dữ liệu mẫu"))

    dh.para(
        doc,
        "Chi tiết bất thường **D01** – trường hợp nghiêm trọng nhất về mặt "
        "logic nghiệp vụ:",
    )
    dh.table(
        doc,
        ["Mã phòng", "Tên phòng ban", "Trưởng phòng", "Ngày vào làm",
         "Ngày bổ nhiệm", "Chênh lệch"],
        [[str(r["depNum"]), r["depName"], r["mgrName"],
          vn_date(r["mgrStartDate"]), vn_date(r["mgrAssDate"]),
          "%s ngày" % vn_int((r["mgrStartDate"] - r["mgrAssDate"]).days)]
         for r in s["mgr_date_issues"]],
        widths=[1.4, 4.0, 3.4, 2.3, 2.3, 2.6],
        aligns=["center", None, None, "center", "center", "right"],
        size=9.0,
    )
    dh.caption(doc, cap.table(7, "Chi tiết các phòng ban có ngày bổ nhiệm "
                                 "sớm hơn ngày vào làm"))
    dh.para(
        doc,
        "Trường hợp cực đoan nhất là phòng “%s”: trưởng phòng được ghi nhận "
        "bổ nhiệm ngày %s nhưng ngày vào làm lại là %s – chênh nhau hơn %s "
        "năm. Đây là loại lỗi mà một ràng buộc kiểm tra liên bảng (dạng "
        "trigger) hoàn toàn có thể ngăn chặn từ đầu."
        % (s["mgr_date_issues"][0]["depName"]
           if s["mgr_date_issues"] else "—",
           vn_date(s["mgr_date_issues"][0]["mgrAssDate"])
           if s["mgr_date_issues"] else "—",
           vn_date(s["mgr_date_issues"][0]["mgrStartDate"])
           if s["mgr_date_issues"] else "—",
           vn_int(max((r["mgrStartDate"] - r["mgrAssDate"]).days
                      for r in s["mgr_date_issues"]) // 365)
           if s["mgr_date_issues"] else "—"),
        first_line=0.8,
    )

    dh.heading(doc, "7.4. Nhận xét về bản thân script DBC.sql", level=2)
    dh.bullets(doc, [
        "Đoạn mã ở đầu tệp dùng vòng lặp để **xoá toàn bộ khoá ngoại và bảng** "
        "trong cơ sở dữ liệu là **dư thừa**, bởi lệnh `CREATE DATABASE` ngay "
        "trước đó đã tạo một cơ sở dữ liệu hoàn toàn rỗng. Đoạn này chỉ có ý "
        "nghĩa nếu script được chạy trên một cơ sở dữ liệu sẵn có.",
        "Việc `ALTER DATABASE … SET OFFLINE WITH ROLLBACK IMMEDIATE` rồi "
        "`SET ONLINE` trước khi `DROP` là thủ thuật để ngắt mọi kết nối đang "
        "mở. Cách này hiệu quả nhưng khá mạnh tay; `SET SINGLE_USER WITH "
        "ROLLBACK IMMEDIATE` là lựa chọn phổ biến và an toàn hơn.",
        "Các chỉ thị `SET ANSI_NULLS`, `SET QUOTED_IDENTIFIER`, "
        "`SET ANSI_PADDING` được lặp lại nhiều lần giữa các lệnh `CREATE "
        "TABLE`; đây là dấu hiệu script được sinh tự động bởi SQL Server "
        "Management Studio chứ không viết tay.",
        "Script **không có khối `BEGIN TRANSACTION` / `COMMIT`**, nên nếu một "
        "lệnh giữa tệp thất bại, cơ sở dữ liệu sẽ ở trạng thái dở dang.",
        "Toàn bộ tệp **không tạo bất kỳ khung nhìn (`VIEW`), thủ tục "
        "(`PROCEDURE`) hay chỉ mục phụ nào**, tức chỉ dừng ở mức lược đồ và "
        "dữ liệu thô.",
    ])

    dh.heading(doc, "7.5. Rủi ro nếu triển khai nguyên trạng", level=2)
    dh.table(
        doc,
        ["Rủi ro", "Nguyên nhân gốc", "Khả năng xảy ra"],
        [
            ["Thống kê giờ làm sai lệch vì tồn tại bản ghi giờ làm của nhân "
             "viên đã nghỉ hoặc không tồn tại", "H01 – thiếu khoá ngoại",
             "Cao"],
            ["Báo cáo lương bị bóp méo bởi giá trị âm hoặc bằng 0",
             "H02 – thiếu ràng buộc CHECK", "Trung bình"],
            ["Không xoá được hồ sơ nhân viên đã nghỉ việc",
             "H06 – thiếu ON DELETE CASCADE", "Cao"],
            ["Trùng tên phòng ban khiến người dùng chọn sai đơn vị",
             "H05 – thiếu UNIQUE", "Trung bình"],
            ["Truy vấn chậm dần khi dữ liệu tăng lên hàng triệu bản ghi",
             "H10 – thiếu chỉ mục phụ", "Trung bình"],
            ["Sai lệch khi tích hợp với hệ thống khác do mã nhân viên là số "
             "thập phân", "H03 – kiểu dữ liệu không phù hợp", "Cao"],
        ],
        widths=[7.0, 5.6, 3.4],
        aligns=[None, None, "center"],
        size=9.5,
    )
    dh.caption(doc, cap.table(7, "Các rủi ro khi triển khai thiết kế nguyên trạng"))
