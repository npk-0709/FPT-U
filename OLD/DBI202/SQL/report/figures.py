"""Sinh cac hinh ve (ERD, bieu do thong ke) cho bao cao dbCOMPANY."""

import os

import matplotlib

matplotlib.use("Agg")
import matplotlib.patches as mpatches
import matplotlib.pyplot as plt
from matplotlib.lines import Line2D

import stats as stats_mod

ASSETS = os.path.join(os.path.dirname(os.path.abspath(__file__)), "assets")

C_HEADER = "#1F4E79"
C_HEADER_TXT = "#FFFFFF"
C_BODY = "#F2F6FA"
C_EDGE = "#1F4E79"
C_LINE = "#2E5C8A"
C_BAD = "#C00000"
PALETTE = ["#1F4E79", "#2E75B6", "#9DC3E6", "#548235", "#BF8F00", "#7B3294"]

plt.rcParams["font.family"] = "DejaVu Sans"


def _ensure_dir():
    if not os.path.isdir(ASSETS):
        os.makedirs(ASSETS)


# ---------------------------------------------------------------- ERD ------
ROW_H = 0.345
HEAD_H = 0.46
BOX_W = 3.45


class Box(object):
    def __init__(self, ax, name, x, top, attrs, width=BOX_W):
        self.name = name
        self.x = x
        self.top = top
        self.w = width
        self.h = HEAD_H + ROW_H * len(attrs)
        self.bottom = top - self.h
        self.attrs = attrs
        ax.add_patch(
            mpatches.FancyBboxPatch(
                (x, self.bottom),
                width,
                self.h,
                boxstyle="round,pad=0,rounding_size=0.06",
                linewidth=1.1,
                edgecolor=C_EDGE,
                facecolor=C_BODY,
                zorder=3,
            )
        )
        ax.add_patch(
            mpatches.Rectangle(
                (x, top - HEAD_H), width, HEAD_H,
                linewidth=1.1, edgecolor=C_EDGE, facecolor=C_HEADER, zorder=4,
            )
        )
        ax.text(
            x + width / 2.0, top - HEAD_H / 2.0, name,
            ha="center", va="center", color=C_HEADER_TXT,
            fontsize=9.0, fontweight="bold", zorder=5,
        )
        for i, (label, kind) in enumerate(attrs):
            y = top - HEAD_H - ROW_H * (i + 0.5)
            tag = {"PK": "PK", "FK": "FK", "PFK": "PK,FK"}.get(kind, "")
            ax.text(
                x + 0.12, y, tag, ha="left", va="center",
                fontsize=6.6, color=C_BAD if kind in ("FK", "PFK") else "#0B6623",
                fontweight="bold", zorder=5,
            )
            ax.text(
                x + 0.78, y, label, ha="left", va="center", fontsize=8.0,
                fontweight="bold" if kind in ("PK", "PFK") else "normal",
                color="#111111", zorder=5,
            )
            if i:
                ax.plot(
                    [x + 0.06, x + width - 0.06],
                    [top - HEAD_H - ROW_H * i] * 2,
                    color="#C9D6E4", linewidth=0.5, zorder=4,
                )

    def left(self, y):
        return (self.x, y)

    def right(self, y):
        return (self.x + self.w, y)

    def cx(self, frac=0.5):
        return self.x + self.w * frac


def UP(text, dx=0.0):
    return (text, dx, 0.12, "center", "bottom")


def DOWN(text, dx=0.0):
    return (text, dx, -0.12, "center", "top")


def LEFT(text, dy=0.12):
    return (text, -0.10, dy, "right", "bottom")


def RIGHT(text, dy=0.12):
    return (text, 0.10, dy, "left", "bottom")


def _route(ax, pts, lf=None, lt=None, dashed=False, color=None):
    """Ve duong noi gap khuc; lf/lt = nhan ban so tai diem dau/cuoi."""
    color = color or C_LINE
    xs = [p[0] for p in pts]
    ys = [p[1] for p in pts]
    ax.plot(
        xs, ys, color=color, linewidth=1.25 if not dashed else 1.5,
        linestyle="--" if dashed else "-", zorder=2, solid_capstyle="round",
    )
    ax.plot([xs[0]], [ys[0]], marker="o", markersize=3.4, color=color, zorder=6)
    ax.plot([xs[-1]], [ys[-1]], marker="o", markersize=3.4, color=color, zorder=6)
    for spec, (px, py) in ((lf, (xs[0], ys[0])), (lt, (xs[-1], ys[-1]))):
        if not spec:
            continue
        text, dx, dy, ha, va = spec
        ax.text(px + dx, py + dy, text, fontsize=8.2, color=color, ha=ha, va=va,
                fontweight="bold", zorder=7)


def erd(path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "erd.png")
    fig, ax = plt.subplots(figsize=(10.0, 6.9))
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 10.9)
    ax.axis("off")

    loc = Box(ax, "tblLocation", 0.35, 9.55, [("locNum (IDENTITY)", "PK"), ("locName", "")])
    dloc = Box(ax, "tblDepLocation", 0.35, 5.45, [("depNum", "PFK"), ("locNum", "PFK")])
    dep = Box(ax, "tblDepartment", 5.05, 9.95, [
        ("depNum", "PK"), ("depName", ""), ("mgrSSN", "FK"), ("mgrAssDate", "")])
    pro = Box(ax, "tblProject", 11.1, 9.95, [
        ("proNum", "PK"), ("proName", ""), ("locNum", "FK"), ("depNum", "FK")])
    emp = Box(ax, "tblEmployee", 5.05, 6.35, [
        ("empSSN", "PK"), ("empName", ""), ("empAddress", ""), ("empSalary", ""),
        ("empSex", ""), ("empBirthdate", ""), ("depNum", "FK"),
        ("supervisorSSN", "FK"), ("empStartdate", "")])
    won = Box(ax, "tblWorksOn", 11.1, 5.75, [
        ("empSSN", "PK"), ("proNum", "PFK"), ("workHours", "")])
    dnt = Box(ax, "tblDependent", 11.1, 2.55, [
        ("depName", "PK"), ("empSSN", "PFK"), ("depSex", ""),
        ("depBirthdate", ""), ("depRelationship", "")])

    # 1. tblDepartment.mgrSSN -> tblEmployee.empSSN (1:1 QUAN LY)
    _route(ax, [(dep.cx(0.22), dep.bottom), (dep.cx(0.22), emp.top)],
           DOWN("1", -0.16), UP("1", -0.16))
    ax.text(dep.cx(0.22) - 0.12, (dep.bottom + emp.top) / 2.0, "QUẢN LÝ",
            fontsize=8.0, rotation=90, ha="right", va="center", color=C_LINE)
    # 2. tblEmployee.depNum -> tblDepartment.depNum (1:N THUOC VE)
    _route(ax, [(emp.cx(0.78), emp.top), (dep.cx(0.78), dep.bottom)],
           UP("N", 0.16), DOWN("1", 0.16))
    ax.text(dep.cx(0.78) + 0.12, (dep.bottom + emp.top) / 2.0, "THUỘC VỀ",
            fontsize=8.0, rotation=90, ha="left", va="center", color=C_LINE)
    # 3. Quan he de quy GIAM SAT
    yl1, yl2 = 5.45, 4.35
    _route(ax, [emp.left(yl1), (emp.x - 0.62, yl1), (emp.x - 0.62, yl2), emp.left(yl2)],
           LEFT("N"), LEFT("1"))
    ax.text(emp.x - 0.74, (yl1 + yl2) / 2.0, "GIÁM SÁT", fontsize=8.0, rotation=90,
            ha="right", va="center", color=C_LINE)
    # 4. tblDepLocation.locNum -> tblLocation.locNum
    _route(ax, [(dloc.cx(0.3), dloc.top), (dloc.cx(0.3), loc.bottom)],
           UP("N", -0.16), DOWN("1", -0.16))
    # 5. tblDepLocation.depNum -> tblDepartment.depNum
    _route(ax, [(dloc.cx(0.72), dloc.top), (dloc.cx(0.72), 8.02),
                (dep.x + 0.45, 8.02), (dep.x + 0.45, dep.bottom)],
           UP("N", 0.16), DOWN("1", -0.18))
    ax.text((dloc.cx(0.72) + dep.x) / 2.0, 7.92, "LÀM VIỆC TẠI (M:N)",
            fontsize=8.0, ha="center", va="top", color=C_LINE)
    # 6. tblProject.depNum -> tblDepartment.depNum
    _route(ax, [dep.right(8.72), pro.left(8.72)], RIGHT("1"), LEFT("N"))
    ax.text((dep.x + dep.w + pro.x) / 2.0, 8.80, "PHỤ TRÁCH",
            fontsize=8.0, ha="center", va="bottom", color=C_LINE)
    # 7. tblProject.locNum -> tblLocation.locNum
    _route(ax, [(pro.cx(0.5), pro.top), (pro.cx(0.5), 10.62),
                (loc.cx(0.5), 10.62), (loc.cx(0.5), loc.top)],
           UP("N", 0.16), UP("1", -0.16))
    ax.text((pro.cx(0.5) + loc.cx(0.5)) / 2.0, 10.70, "ĐẶT TẠI",
            fontsize=8.0, ha="center", va="bottom", color=C_LINE)
    # 8. tblWorksOn.proNum -> tblProject.proNum
    _route(ax, [(won.cx(0.6), won.top), (pro.cx(0.6), pro.bottom)],
           UP("N", 0.16), DOWN("1", 0.16))
    # 9. tblDependent.empSSN -> tblEmployee.empSSN
    _route(ax, [dnt.left(1.95), (emp.x + emp.w + 0.65, 1.95),
                (emp.x + emp.w + 0.65, 3.0), emp.right(3.0)],
           LEFT("N"), RIGHT("1"))
    ax.text(emp.x + emp.w + 0.78, 2.42, "PHỤ THUỘC", fontsize=8.0, rotation=90,
            ha="left", va="center", color=C_LINE)
    # 10. FK CON THIEU: tblWorksOn.empSSN -> tblEmployee.empSSN
    _route(ax, [won.left(4.85), emp.right(4.85)], LEFT("N"), RIGHT("1"),
           dashed=True, color=C_BAD)
    ax.text((emp.x + emp.w + won.x) / 2.0, 4.95,
            "THAM GIA (M:N)\nFK chưa khai báo", fontsize=7.8, ha="center",
            va="bottom", color=C_BAD, fontweight="bold")

    legend = [
        Line2D([0], [0], color=C_LINE, lw=1.4, label="Khoá ngoại đã khai báo trong DBC.sql"),
        Line2D([0], [0], color=C_BAD, lw=1.6, ls="--",
               label="Quan hệ nghiệp vụ nhưng THIẾU khoá ngoại"),
    ]
    ax.legend(handles=legend, loc="lower left", fontsize=8.2, frameon=True,
              bbox_to_anchor=(0.0, -0.015))

    fig.tight_layout(pad=0.2)
    fig.savefig(path, dpi=200)
    plt.close(fig)
    return path


# ------------------------------------------------------------- CHARTS ------
def _finish(fig, ax_list, path):
    for ax in ax_list:
        ax.spines["top"].set_visible(False)
        ax.spines["right"].set_visible(False)
        ax.tick_params(labelsize=8.5)
    fig.tight_layout(pad=0.4)
    fig.savefig(path, dpi=200)
    plt.close(fig)
    return path


def chart_department(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_department.png")
    rows = s["dep_rows"]
    labels = ["P%d" % r["depNum"] for r in rows]
    hc = [r["headcount"] for r in rows]
    avg = [r["avg_salary"] / 1000.0 for r in rows]

    fig, ax = plt.subplots(figsize=(6.4, 3.3))
    bars = ax.bar(labels, hc, color=PALETTE[2], edgecolor=PALETTE[0], width=0.55,
                  label="Số nhân viên")
    for b, v in zip(bars, hc):
        ax.text(b.get_x() + b.get_width() / 2.0, v - 0.22, str(v), ha="center",
                va="top", fontsize=9.0, fontweight="bold", color=PALETTE[0])
    ax.set_ylabel("Số nhân viên", fontsize=9)
    ax.set_ylim(0, max(hc) + 1.4)
    ax.set_xlabel("Phòng ban", fontsize=9)

    ax2 = ax.twinx()
    ax2.plot(labels, avg, color=PALETTE[4], marker="o", linewidth=1.8,
             label="Lương trung bình (nghìn)")
    for x, v in zip(labels, avg):
        ax2.annotate("%.1f" % v, (x, v), textcoords="offset points", xytext=(0, 8),
                     ha="center", fontsize=8.2, color=PALETTE[4], fontweight="bold")
    ax2.set_ylabel("Lương TB (nghìn)", fontsize=9)
    ax2.set_ylim(0, max(avg) * 1.28)
    ax2.spines["top"].set_visible(False)
    ax2.tick_params(labelsize=8.5)

    h1, l1 = ax.get_legend_handles_labels()
    h2, l2 = ax2.get_legend_handles_labels()
    ax.legend(h1 + h2, l1 + l2, fontsize=8.2, loc="upper center",
              bbox_to_anchor=(0.5, 1.16), ncol=2, frameon=False)
    return _finish(fig, [ax], path)


def chart_salary_hist(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_salary.png")
    sal = [int(e["empSalary"]) / 1000.0 for e in s["db"]["rows"]["tblEmployee"]]
    fig, ax = plt.subplots(figsize=(6.4, 3.1))
    bins = [20, 40, 60, 80, 100, 120]
    n, _, patches = ax.hist(sal, bins=bins, color=PALETTE[1], edgecolor="white")
    for p, v in zip(patches, n):
        if v:
            ax.text(p.get_x() + p.get_width() / 2.0, v + 0.1, "%d" % v, ha="center",
                    va="bottom", fontsize=8.5, fontweight="bold", color=PALETTE[0])
    avg_k = s["salary"]["avg"] / 1000.0
    ax.axvline(avg_k, color=C_BAD, linestyle="--", linewidth=1.4)
    ax.text(avg_k - 1.6, max(n) + 0.85,
            "Lương TB = %s nghìn" % stats_mod.vn_float(avg_k, 1),
            fontsize=8.4, color=C_BAD, fontweight="bold", ha="right", va="center")
    ax.set_xlabel("Mức lương (nghìn)", fontsize=9)
    ax.set_ylabel("Số nhân viên", fontsize=9)
    ax.set_xticks(bins)
    ax.set_ylim(0, max(n) + 1.4)
    return _finish(fig, [ax], path)


def chart_project(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_project.png")
    rows = s["pro_rows"]
    labels = [r["proName"] for r in rows]
    hours = [r["hours"] for r in rows]
    members = [r["members"] for r in rows]
    idx = range(len(labels))

    fig, ax = plt.subplots(figsize=(6.4, 3.3))
    b1 = ax.bar([i - 0.19 for i in idx], hours, width=0.38, color=PALETTE[1],
                label="Tổng số giờ")
    ax.set_ylabel("Tổng số giờ", fontsize=9)
    ax.set_ylim(0, max(hours) * 1.22)
    for b, v in zip(b1, hours):
        ax.text(b.get_x() + b.get_width() / 2.0, v + 2, str(v), ha="center",
                va="bottom", fontsize=8.3, fontweight="bold", color=PALETTE[0])

    ax2 = ax.twinx()
    b2 = ax2.bar([i + 0.19 for i in idx], members, width=0.38, color=PALETTE[3],
                 label="Số thành viên")
    ax2.set_ylabel("Số thành viên", fontsize=9)
    ax2.set_ylim(0, max(members) * 1.35)
    for b, v in zip(b2, members):
        ax2.text(b.get_x() + b.get_width() / 2.0, v + 0.1, str(v), ha="center",
                 va="bottom", fontsize=8.3, fontweight="bold", color=PALETTE[3])
    ax2.spines["top"].set_visible(False)
    ax2.tick_params(labelsize=8.5)

    ax.set_xticks(list(idx))
    ax.set_xticklabels(labels)
    ax.legend([b1, b2], ["Tổng số giờ", "Số thành viên"], fontsize=8.2,
              loc="upper center", bbox_to_anchor=(0.5, 1.16), ncol=2, frameon=False)
    return _finish(fig, [ax], path)


def chart_location(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_location.png")
    rows = s["loc_rows"]
    labels = [r["locName"] for r in rows]
    deps = [r["departments"] for r in rows]
    pros = [r["projects"] for r in rows]
    y = list(range(len(labels)))[::-1]

    fig, ax = plt.subplots(figsize=(6.4, 3.2))
    ax.barh([i + 0.19 for i in y], deps, height=0.38, color=PALETTE[1],
            label="Số phòng ban")
    ax.barh([i - 0.19 for i in y], pros, height=0.38, color=PALETTE[4],
            label="Số dự án")
    for i, (d, p) in zip(y, zip(deps, pros)):
        ax.text(d + 0.08, i + 0.19, str(d), va="center", fontsize=8.3,
                fontweight="bold", color=PALETTE[0])
        ax.text(p + 0.08, i - 0.19, str(p), va="center", fontsize=8.3,
                fontweight="bold", color=PALETTE[4])
    ax.set_yticks(y)
    ax.set_yticklabels(labels)
    ax.set_xlabel("Số lượng", fontsize=9)
    ax.set_xlim(0, max(deps + pros) + 0.9)
    ax.legend(fontsize=8.2, loc="lower right", frameon=False)
    return _finish(fig, [ax], path)


def chart_dependent(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_dependent.png")
    rel = s["dent_rel"].most_common()
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(6.5, 2.9))

    labels = [r[0] for r in rel]
    vals = [r[1] for r in rel]
    bars = ax1.bar(labels, vals, color=PALETTE[:len(labels)], width=0.6)
    for b, v in zip(bars, vals):
        ax1.text(b.get_x() + b.get_width() / 2.0, v + 0.06, str(v), ha="center",
                 va="bottom", fontsize=8.5, fontweight="bold")
    ax1.set_title("Theo mối quan hệ", fontsize=9.5, fontweight="bold")
    ax1.set_ylabel("Số người phụ thuộc", fontsize=9)
    ax1.set_ylim(0, max(vals) + 1.1)

    dep_labels = ["P%d" % k for k in s["dent_by_dep"]]
    dep_vals = list(s["dent_by_dep"].values())
    bars2 = ax2.bar(dep_labels, dep_vals, color=PALETTE[1], width=0.55)
    for b, v in zip(bars2, dep_vals):
        ax2.text(b.get_x() + b.get_width() / 2.0, v + 0.06, str(v), ha="center",
                 va="bottom", fontsize=8.5, fontweight="bold")
    ax2.set_title("Theo phòng ban", fontsize=9.5, fontweight="bold")
    ax2.set_ylim(0, max(dep_vals) + 1.1)
    return _finish(fig, [ax1, ax2], path)


def chart_supervision(s, path=None):
    _ensure_dir()
    path = path or os.path.join(ASSETS, "chart_supervision.png")
    subs = s["subordinates"]
    emp = s["emp_by_ssn"]
    roots = sorted(s["roots"], key=lambda e: e["depNum"])

    # Cay ngang: truc X = cap bac giam sat, truc Y = thu tu la
    DX = 3.5
    pos = {}
    cursor = [0.0]

    def place(ssn, depth):
        children = sorted(subs.get(ssn, []), key=lambda x: emp[x]["empName"])
        if not children:
            y = cursor[0]
            cursor[0] -= 1.0
            pos[ssn] = (depth * DX, y, True)
            return y
        ys = [place(c, depth + 1) for c in children]
        y = sum(ys) / float(len(ys))
        pos[ssn] = (depth * DX, y, False)
        return y

    for r in roots:
        place(r["empSSN"], 0)
        cursor[0] -= 0.7

    fig, ax = plt.subplots(figsize=(6.6, 4.5))
    for ssn, children in subs.items():
        x0, y0, _ = pos[ssn]
        elbow = x0 + DX * 0.72
        for c in children:
            x1, y1, _ = pos[c]
            ax.plot([x0 + 0.14, elbow, elbow, x1 - 0.14],
                    [y0, y0, y1, y1], color="#8FAFCB", linewidth=1.0, zorder=1)
    for ssn, (x, y, is_leaf) in pos.items():
        e = emp[ssn]
        is_root = e["supervisorSSN"] is None
        ax.scatter([x], [y], s=190 if is_root else 120,
                   color=PALETTE[0] if is_root else PALETTE[2],
                   edgecolor=PALETTE[0], linewidths=0.9, zorder=3)
        ax.text(x, y, "P%d" % e["depNum"], fontsize=5.4, ha="center", va="center",
                color="white" if is_root else PALETTE[0], fontweight="bold", zorder=4)
        if is_leaf:
            ax.text(x + 0.22, y, e["empName"], fontsize=7.4, ha="left",
                    va="center", color="#222222")
        else:
            ax.text(x - 0.16, y + 0.24, e["empName"], fontsize=7.4, ha="left",
                    va="bottom", color=PALETTE[0], fontweight="bold")
    ax.set_xlim(-0.5, 2 * DX + 3.6)
    ax.set_ylim(cursor[0] + 0.4, 1.5)
    ax.axis("off")
    for d, name in enumerate(["Cấp 1 – Trưởng phòng", "Cấp 2", "Cấp 3"]):
        ax.text(d * DX, 1.05, name, fontsize=7.8, ha="left", va="bottom",
                color="#666666", style="italic")
    legend = [
        Line2D([0], [0], marker="o", color="w", markerfacecolor=PALETTE[0],
               markersize=9, label="Trưởng phòng (supervisorSSN IS NULL)"),
        Line2D([0], [0], marker="o", color="w", markerfacecolor=PALETTE[2],
               markeredgecolor=PALETTE[0], markersize=8, label="Nhân viên"),
    ]
    ax.legend(handles=legend, fontsize=7.8, loc="lower right", frameon=False)
    fig.tight_layout(pad=0.3)
    fig.savefig(path, dpi=200)
    plt.close(fig)
    return path


def build_all(s=None):
    s = s or stats_mod.build()
    return {
        "erd": erd(),
        "department": chart_department(s),
        "salary": chart_salary_hist(s),
        "project": chart_project(s),
        "location": chart_location(s),
        "dependent": chart_dependent(s),
        "supervision": chart_supervision(s),
    }


if __name__ == "__main__":
    for k, v in build_all().items():
        print("%-12s -> %s" % (k, v))
