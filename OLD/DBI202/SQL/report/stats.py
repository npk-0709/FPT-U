"""Tinh cac chi so thong ke tu du lieu mau cua dbCOMPANY."""

from collections import Counter, OrderedDict, defaultdict

import parse_dbc


def build(db=None):
    db = db or parse_dbc.load()
    rows = db["rows"]

    emps = rows["tblEmployee"]
    deps = rows["tblDepartment"]
    dents = rows["tblDependent"]
    locs = rows["tblLocation"]
    pros = rows["tblProject"]
    works = rows["tblWorksOn"]
    deplocs = rows["tblDepLocation"]

    emp_by_ssn = OrderedDict((e["empSSN"], e) for e in emps)
    dep_by_num = OrderedDict((d["depNum"], d) for d in deps)
    loc_by_num = OrderedDict((l["locNum"], l) for l in locs)
    pro_by_num = OrderedDict((p["proNum"], p) for p in pros)

    s = {}
    s["db"] = db
    s["emp_by_ssn"] = emp_by_ssn
    s["dep_by_num"] = dep_by_num
    s["loc_by_num"] = loc_by_num
    s["pro_by_num"] = pro_by_num

    s["row_counts"] = OrderedDict((t, len(rows[t])) for t in db["order"])
    s["total_rows"] = sum(s["row_counts"].values())

    # --- Nhan su & luong theo phong ban ---
    dep_rows = []
    for d in deps:
        members = [e for e in emps if e["depNum"] == d["depNum"]]
        sal = [int(e["empSalary"]) for e in members]
        mgr = emp_by_ssn.get(d["mgrSSN"])
        dep_rows.append(
            {
                "depNum": d["depNum"],
                "depName": d["depName"],
                "mgrSSN": d["mgrSSN"],
                "mgrName": mgr["empName"] if mgr else None,
                "mgrAssDate": d["mgrAssDate"],
                "mgrStartDate": mgr["empStartdate"] if mgr else None,
                "headcount": len(members),
                "total_salary": sum(sal),
                "avg_salary": (sum(sal) / float(len(sal))) if sal else 0.0,
                "min_salary": min(sal) if sal else 0,
                "max_salary": max(sal) if sal else 0,
            }
        )
    s["dep_rows"] = dep_rows

    salaries = [int(e["empSalary"]) for e in emps]
    s["salary"] = {
        "count": len(salaries),
        "total": sum(salaries),
        "avg": sum(salaries) / float(len(salaries)),
        "min": min(salaries),
        "max": max(salaries),
        "min_emps": [e["empName"] for e in emps if int(e["empSalary"]) == min(salaries)],
        "max_emps": [e["empName"] for e in emps if int(e["empSalary"]) == max(salaries)],
    }
    s["sex_counts"] = Counter(e["empSex"] for e in emps)
    s["emp_sex_by_dep"] = OrderedDict(
        (d["depNum"], Counter(e["empSex"] for e in emps if e["depNum"] == d["depNum"]))
        for d in deps
    )

    # --- Giam sat ---
    subordinates = defaultdict(list)
    for e in emps:
        if e["supervisorSSN"] is not None:
            subordinates[e["supervisorSSN"]].append(e["empSSN"])
    s["subordinates"] = subordinates
    s["roots"] = [e for e in emps if e["supervisorSSN"] is None]
    s["supervisors"] = sorted(subordinates.keys())
    s["leaf_emps"] = [e for e in emps if e["empSSN"] not in subordinates]
    s["sup_cross_dep"] = [
        e
        for e in emps
        if e["supervisorSSN"] is not None
        and emp_by_ssn[e["supervisorSSN"]]["depNum"] != e["depNum"]
    ]

    # --- Du an & gio lam ---
    pro_rows = []
    for p in pros:
        w = [x for x in works if x["proNum"] == p["proNum"]]
        pro_rows.append(
            {
                "proNum": p["proNum"],
                "proName": p["proName"],
                "locName": loc_by_num[p["locNum"]]["locName"] if p["locNum"] else None,
                "depNum": p["depNum"],
                "depName": dep_by_num[p["depNum"]]["depName"] if p["depNum"] else None,
                "members": len(w),
                "hours": sum(x["workHours"] for x in w),
            }
        )
    s["pro_rows"] = pro_rows
    s["total_hours"] = sum(p["hours"] for p in pro_rows)

    emp_hours = defaultdict(int)
    emp_projects = defaultdict(int)
    for w in works:
        emp_hours[w["empSSN"]] += w["workHours"]
        emp_projects[w["empSSN"]] += 1
    s["emp_hours"] = emp_hours
    s["emp_projects"] = emp_projects
    s["emps_no_project"] = [e for e in emps if e["empSSN"] not in emp_hours]
    s["emps_with_project"] = [e for e in emps if e["empSSN"] in emp_hours]
    top_hours = sorted(emp_hours.items(), key=lambda kv: -kv[1])
    s["top_hours"] = [
        {
            "empSSN": ssn,
            "empName": emp_by_ssn[ssn]["empName"],
            "depName": dep_by_num[emp_by_ssn[ssn]["depNum"]]["depName"],
            "projects": emp_projects[ssn],
            "hours": h,
        }
        for ssn, h in top_hours
    ]

    dep_hours = defaultdict(int)
    for w in works:
        dep_hours[emp_by_ssn[w["empSSN"]]["depNum"]] += w["workHours"]
    s["dep_hours"] = dep_hours

    # --- Nguoi phu thuoc ---
    s["dent_sex"] = Counter(d["depSex"] for d in dents)
    s["dent_rel"] = Counter(d["depRelationship"] for d in dents)
    dent_by_emp = defaultdict(list)
    for d in dents:
        dent_by_emp[d["empSSN"]].append(d)
    s["dent_by_emp"] = dent_by_emp
    s["emps_no_dependent"] = [e for e in emps if e["empSSN"] not in dent_by_emp]
    dent_by_dep = OrderedDict()
    for d in deps:
        n = sum(
            len(dent_by_emp.get(e["empSSN"], []))
            for e in emps
            if e["depNum"] == d["depNum"]
        )
        dent_by_dep[d["depNum"]] = n
    s["dent_by_dep"] = dent_by_dep

    # --- Dia diem ---
    loc_rows = []
    for l in locs:
        d_of_loc = [x["depNum"] for x in deplocs if x["locNum"] == l["locNum"]]
        p_of_loc = [p for p in pros if p["locNum"] == l["locNum"]]
        loc_rows.append(
            {
                "locNum": l["locNum"],
                "locName": l["locName"],
                "departments": len(d_of_loc),
                "dep_list": sorted(d_of_loc),
                "projects": len(p_of_loc),
            }
        )
    s["loc_rows"] = loc_rows
    dep_locs_map = OrderedDict()
    for d in deps:
        dep_locs_map[d["depNum"]] = sorted(
            x["locNum"] for x in deplocs if x["depNum"] == d["depNum"]
        )
    s["dep_locs_map"] = dep_locs_map

    # --- Bat thuong du lieu ---
    s["mgr_date_issues"] = [
        r
        for r in dep_rows
        if r["mgrAssDate"] and r["mgrStartDate"] and r["mgrAssDate"] < r["mgrStartDate"]
    ]
    s["mgr_not_in_dep"] = [
        r
        for r in dep_rows
        if r["mgrSSN"] and emp_by_ssn[r["mgrSSN"]]["depNum"] != r["depNum"]
    ]
    s["locs_unused"] = [r for r in loc_rows if r["departments"] == 0 and r["projects"] == 0]
    s["deps_no_project"] = [
        d for d in deps if not any(p["depNum"] == d["depNum"] for p in pros)
    ]
    s["deps_no_dependent"] = [d for d in deps if s["dent_by_dep"][d["depNum"]] == 0]
    s["addresses"] = sorted(set(e["empAddress"] for e in emps))
    s["addr_trailing_space"] = [
        e["empAddress"] for e in emps if e["empAddress"] != e["empAddress"].strip()
    ]
    loc_names = set(l["locName"] for l in locs)
    s["addr_not_in_loc"] = sorted(
        a for a in s["addresses"] if a.strip() not in loc_names
    )
    # Nghi van gioi tinh: ten chua "Thi" nhung ghi M, hoac quan he Vo/Chong lech gioi tinh
    s["sex_suspects"] = [
        e for e in emps if (" Thị " in e["empName"] or e["empName"].startswith("Thị ")) and e["empSex"] == "M"
    ]
    rel_conflicts = []
    for d in dents:
        e = emp_by_ssn.get(d["empSSN"])
        if not e:
            continue
        rel = (d["depRelationship"] or "").strip()
        if rel == "Vợ" and e["empSex"] != "M":
            rel_conflicts.append((e, d))
        if rel == "Chồng" and e["empSex"] != "F":
            rel_conflicts.append((e, d))
    s["rel_conflicts"] = rel_conflicts

    # Cot NOT NULL ve nghiep vu nhung khai bao NULL duoc
    nullable_business = []
    for t in db["order"]:
        for c in db["tables"][t]["columns"]:
            if c["nullable"]:
                nullable_business.append((t, c["name"], c["type"]))
    s["nullable_columns"] = nullable_business

    # FK con thieu so voi mo hinh nghiep vu
    declared = set((f["table"], f["column"]) for f in db["fks"])
    s["missing_fks"] = [
        ("tblWorksOn", "empSSN", "tblEmployee", "empSSN")
    ]
    s["missing_fks"] = [m for m in s["missing_fks"] if (m[0], m[1]) not in declared]

    return s


def vn_int(n):
    """Dinh dang so nguyen kieu Viet Nam: 1.697.000"""
    return "{:,}".format(int(n)).replace(",", ".")


def vn_float(n, digits=1):
    txt = "{:,.{d}f}".format(float(n), d=digits)
    if digits <= 0:
        return txt.replace(",", ".")
    int_part, dec_part = txt.split(".")
    return int_part.replace(",", ".") + "," + dec_part


def vn_date(d):
    return d.strftime("%d/%m/%Y") if d else "—"


def ssn(value):
    """Ma dinh danh: khong dung dau phan cach nghin."""
    return "%d" % int(value)


if __name__ == "__main__":
    s = build()
    print("Tong so dong du lieu:", s["total_rows"])
    print("Luong: min=%s max=%s avg=%s tong=%s" % (
        s["salary"]["min"], s["salary"]["max"],
        round(s["salary"]["avg"], 2), s["salary"]["total"]))
    print("Gioi tinh NV:", dict(s["sex_counts"]))
    print("Tong gio lam:", s["total_hours"])
    print("NV chua tham gia du an:", len(s["emps_no_project"]))
    print("NV khong co nguoi phu thuoc:", len(s["emps_no_dependent"]))
    print("FK thieu:", s["missing_fks"])
    print("Loi ngay bo nhiem:", [(r["depNum"], r["mgrName"]) for r in s["mgr_date_issues"]])
    print("Dia diem khong dung:", [r["locName"] for r in s["locs_unused"]])
    print("Truong phong khac phong:", [r["depNum"] for r in s["mgr_not_in_dep"]])
    print("Xung dot quan he-gioi tinh:", len(s["rel_conflicts"]))
    print("Nghi van gioi tinh:", [(e["empName"], e["empSex"]) for e in s["sex_suspects"]])
    print("Dia chi khong khop tblLocation:", s["addr_not_in_loc"])
    for r in s["dep_rows"]:
        print("  Dep %s: hc=%d avg=%.1f hours=%d dependents=%d" % (
            r["depNum"], r["headcount"], r["avg_salary"],
            s["dep_hours"][r["depNum"]], s["dent_by_dep"][r["depNum"]]))
    for r in s["pro_rows"]:
        print("  %s: members=%d hours=%d dep=%s" % (
            r["proName"], r["members"], r["hours"], r["depNum"]))
