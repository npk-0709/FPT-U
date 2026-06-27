# -*- coding: utf-8 -*-
"""
Lab 5 Report Generator
=======================
Doc file Lab5_HCRS.sql (nguon duy nhat) va sinh bao cao Word "Lab5_report.docx"
theo dung cau truc nop bai cua Lab 5 (Objective -> SQL Queries -> Functions ->
Procedures -> Triggers -> Views/Indexes -> Conclusion).

Cach dung:
    python create_lab5_report.py

Yeu cau: pip install python-docx
"""

import os
from docx import Document
from docx.shared import Pt, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn, nsdecls
from docx.oxml import parse_xml

HERE = os.path.dirname(os.path.abspath(__file__))
SQL_FILE = os.path.join(HERE, 'Lab5_HCRS.sql')
OUT_FILE = os.path.join(HERE, 'Lab5_report.docx')

# Anh xa tieu de section trong file .sql -> (so muc, tieu de hien thi trong bao cao)
SECTION_MAP = {
    'Basic SQL Queries':        ('3.1', 'Basic Queries (Truy van co ban)'),
    'Intermediate SQL Queries': ('3.2', 'Intermediate Queries (Truy van trung cap)'),
    'Advanced SQL Queries':     ('3.3', 'Advanced Queries (Truy van nang cao)'),
    'User-defined Functions':   ('4',   'Functions'),
    'Stored Procedures':        ('5',   'Stored Procedures'),
    'Triggers':                 ('6',   'Triggers'),
    'Views and Indexes':        ('7',   'Views and Indexes'),
}
QUERY_SECTIONS = ('Basic SQL Queries', 'Intermediate SQL Queries', 'Advanced SQL Queries')


# ============================================================================
# PARSER: doc file .sql theo cac marker --##
# ============================================================================
def parse_sql(path):
    meta, sections = {}, []
    cur_section = cur_group = cur_item = None

    def finalize_item():
        nonlocal cur_item
        if cur_item is not None:
            cur_item['sql'] = '\n'.join(cur_item.pop('sql_lines')).strip('\n').strip()
            cur_group['items'].append(cur_item)
            cur_item = None

    def ensure_group():
        nonlocal cur_group
        if cur_group is None:
            cur_group = {'title': None, 'items': []}
            cur_section['groups'].append(cur_group)

    with open(path, encoding='utf-8') as f:
        for raw in f:
            line = raw.rstrip('\n')
            s = line.strip()
            if s.startswith('--##'):
                body = s[4:].strip()
                key = body.split('|', 1)[0].strip() if '|' in body else body
                val = body.split('|', 1)[1].strip() if '|' in body else ''
                if body.startswith('META'):
                    rest = body[4:].strip()
                    if '|' in rest:
                        k, v = rest.split('|', 1)
                        meta[k.strip()] = v.strip()
                elif body.startswith('SECTION'):
                    finalize_item()
                    cur_section = {'title': val, 'groups': []}
                    cur_group = None
                    sections.append(cur_section)
                elif body.startswith('GROUP'):
                    finalize_item()
                    cur_group = {'title': val, 'items': []}
                    cur_section['groups'].append(cur_group)
                elif body.startswith('ITEM'):
                    finalize_item()
                    ensure_group()
                    cur_item = {'title': val, 'desc': [], 'sql_lines': []}
                elif body.startswith('DESC'):
                    if cur_item is not None:
                        cur_item['desc'].append(val)
                continue
            # dong khong phai marker
            if cur_item is not None:
                if s.startswith('/*='):       # gap banner PART -> ket thuc code cua item
                    finalize_item()
                else:
                    cur_item['sql_lines'].append(line)
        finalize_item()
    return meta, sections


# ============================================================================
# HELPERS dinh dang Word
# ============================================================================
def set_cell_shading(cell, color):
    cell._tc.get_or_add_tcPr().append(parse_xml('<w:shd {} w:fill="{}"/>'.format(nsdecls('w'), color)))


def _set_font(run, name='Times New Roman', size=12, bold=False, italic=False, color=None):
    run.font.name = name
    run.font.size = Pt(size)
    run.bold = bold
    run.italic = italic
    if color:
        run.font.color.rgb = color
    run._element.rPr.rFonts.set(qn('w:eastAsia'), name)


def add_para(doc, text, bold=False, italic=False, size=12,
             align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_after=6, space_before=0, color=None):
    p = doc.add_paragraph()
    p.alignment = align
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.space_before = Pt(space_before)
    _set_font(p.add_run(text), size=size, bold=bold, italic=italic, color=color)
    return p


def add_bullet(doc, text, size=12):
    p = doc.add_paragraph(style='List Bullet')
    p.paragraph_format.space_after = Pt(2)
    _set_font(p.add_run(text), size=size)
    return p


def _heading(doc, text, level, color):
    h = doc.add_heading(text, level=level)
    for run in h.runs:
        run.font.name = 'Times New Roman'
        run.font.color.rgb = color
        run._element.rPr.rFonts.set(qn('w:eastAsia'), 'Times New Roman')
    return h


def heading1(doc, text):
    return _heading(doc, text, 1, RGBColor(0, 0, 0))


def heading2(doc, text):
    return _heading(doc, text, 2, RGBColor(0x1F, 0x3B, 0x73))


def heading3(doc, text):
    return _heading(doc, text, 3, RGBColor(0x2E, 0x55, 0x97))


def add_code_block(doc, code):
    """Khoi ma SQL: font Consolas, nen xam nhat, giu nguyen xuong dong."""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(6)
    p.paragraph_format.left_indent = Cm(0.3)
    p.paragraph_format.line_spacing = 1.0
    p._p.get_or_add_pPr().append(parse_xml('<w:shd {} w:fill="F3F3F3"/>'.format(nsdecls('w'))))
    lines = code.split('\n')
    for i, ln in enumerate(lines):
        run = p.add_run(ln if ln else ' ')
        run.font.name = 'Consolas'
        run.font.size = Pt(9)
        run._element.rPr.rFonts.set(qn('w:eastAsia'), 'Consolas')
        if i < len(lines) - 1:
            run.add_break()
    return p


def add_screenshot(doc, label):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(12)
    _set_font(p.add_run('[Chen anh chup ket qua thuc thi tai day - %s]' % label),
              size=10, italic=True, color=RGBColor(0x80, 0x80, 0x80))
    return p


def create_table(doc, headers, rows, widths=None):
    table = doc.add_table(rows=1 + len(rows), cols=len(headers))
    table.style = 'Table Grid'
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    for i, header in enumerate(headers):
        cell = table.rows[0].cells[i]
        cell.text = ''
        run = cell.paragraphs[0].add_run(header)
        _set_font(run, size=10, bold=True)
        set_cell_shading(cell, 'D9E2F3')
    for r_idx, row in enumerate(rows):
        for c_idx, val in enumerate(row):
            cell = table.rows[r_idx + 1].cells[c_idx]
            cell.text = ''
            _set_font(cell.paragraphs[0].add_run(str(val)), size=10)
    return table


def add_item(doc, n, item):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(2)
    _set_font(p.add_run('%d. %s' % (n, item['title'])), size=12, bold=True,
              color=RGBColor(0x33, 0x33, 0x33))
    if item['desc']:
        add_para(doc, ' '.join(item['desc']), size=11, space_after=4)
    add_code_block(doc, item['sql'])
    add_screenshot(doc, item['title'])


# ============================================================================
# CAC PHAN CO DINH (Cover, TOC, Objective, DB overview, Conclusion)
# ============================================================================
def setup_document():
    doc = Document()
    for section in doc.sections:
        section.top_margin = Cm(2.0)
        section.bottom_margin = Cm(2.0)
        section.left_margin = Cm(2.5)
        section.right_margin = Cm(2.0)
    style = doc.styles['Normal']
    style.font.name = 'Times New Roman'
    style.font.size = Pt(12)
    style.paragraph_format.space_after = Pt(6)
    return doc


def add_cover(doc, meta):
    for _ in range(3):
        doc.add_paragraph()
    add_para(doc, 'FPT UNIVERSITY', bold=True, size=18, align=WD_ALIGN_PARAGRAPH.CENTER)
    add_para(doc, 'DEPARTMENT OF INFORMATION TECHNOLOGY', bold=True, size=13,
             align=WD_ALIGN_PARAGRAPH.CENTER, space_after=28)
    doc.add_paragraph()
    add_para(doc, 'DBI202 - Database Systems', bold=True, size=16, align=WD_ALIGN_PARAGRAPH.CENTER)
    add_para(doc, meta.get('TITLE', 'Lab 5'), bold=True, size=19,
             align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    add_para(doc, meta.get('SYSTEM', ''), italic=True, size=13,
             align=WD_ALIGN_PARAGRAPH.CENTER, space_after=34)
    doc.add_paragraph()
    add_para(doc, 'Group Members:', bold=True, size=13, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    for i in range(1, 6):
        add_para(doc, '[Member %d - Student ID]' % i, size=12,
                 align=WD_ALIGN_PARAGRAPH.CENTER, space_after=3)
    add_para(doc, 'Date: June 2026', bold=True, size=12,
             align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10)
    doc.add_page_break()


def add_toc(doc):
    heading1(doc, 'Table of Contents')
    items = [
        ('1.', 'Objective', 0),
        ('2.', 'Database Design (from Lab 4)', 0),
        ('3.', 'SQL Queries', 0),
        ('', '3.1. Basic Queries', 1),
        ('', '3.2. Intermediate Queries', 1),
        ('', '3.3. Advanced Queries', 1),
        ('4.', 'Functions', 0),
        ('5.', 'Stored Procedures', 0),
        ('6.', 'Triggers', 0),
        ('7.', 'Views and Indexes', 0),
        ('8.', 'Conclusion and Reflection', 0),
    ]
    for num, title, lvl in items:
        p = doc.add_paragraph()
        p.paragraph_format.space_after = Pt(3)
        if lvl == 1:
            p.paragraph_format.left_indent = Cm(1.0)
        _set_font(p.add_run(('%s %s' % (num, title)).strip()), size=12, bold=(lvl == 0))
    doc.add_page_break()


def add_objective(doc):
    heading1(doc, '1. Objective')
    add_para(doc, (
        'Lab 5 nham ren luyen ky nang lap trinh SQL tren co so du lieu da thiet ke o Lab 4 cho '
        'he thong Quan ly ban hang & bao tri robot ve sinh gia dung (Household Cleaning Robot '
        'Sales & Maintenance Management System). Sinh vien thuc hanh viet cac truy van tu co ban '
        'den nang cao, dong thoi xay dung Functions, Stored Procedures, Triggers de ap dat quy tac '
        'nghiep vu va tu dong hoa tac vu, cung voi Views va Indexes.'))
    add_para(doc, 'Cac muc tieu cu the:', bold=True, space_before=4, space_after=2)
    add_bullet(doc, 'Viet truy van SELECT, WHERE, ORDER BY va cac ham tong hop (COUNT/SUM/AVG/MAX/MIN).')
    add_bullet(doc, 'Viet truy van trung cap: JOIN nhieu bang, GROUP BY/HAVING va subquery.')
    add_bullet(doc, 'Viet truy van nang cao: subquery long nhau, EXISTS/IN/ANY/ALL, phep toan tap hop UNION/INTERSECT/EXCEPT.')
    add_bullet(doc, 'Xay dung it nhat 4 Functions, 4 Stored Procedures, 4 Triggers.')
    add_bullet(doc, 'Tao Views don gian hoa truy van va Indexes tang hieu nang.')
    add_para(doc, (
        'Toan bo ma nguon SQL co trong file kem theo "Lab5_HCRS.sql" va co the chay tron ven trong '
        'SQL Server Management Studio (SSMS) tu tren xuong duoi.'), space_before=6, italic=True)
    doc.add_page_break()


DB_TABLES = [
    ['Customer', 'CustomerID (PK), FullName, PhoneNumber, Email, Address, Password', 'Khach hang'],
    ['Employee', 'EmployeeID (PK), FullName, Role, PhoneNumber, Email, Password', 'Nhan vien (ban hang/ky thuat/quan tri)'],
    ['RobotModel', 'ModelID (PK), Brand, ModelName, Specifications, UnitPrice, WarrantyDuration', 'Mau robot (catalog)'],
    ['ModelFeature', 'ModelID (PK,FK), Feature (PK)', 'Tinh nang cua mau robot'],
    ['RobotUnit', 'RobotID (PK), ModelID (FK), SerialNumber, Status', 'Tung chiec robot trong kho'],
    ['SalesOrder', 'OrderID (PK), CustomerID (FK), EmployeeID (FK), OrderDate, TotalAmount, OrderStatus', 'Don ban hang'],
    ['OrderDetail', 'RobotID (PK,FK), OrderID (FK), SellingPrice', 'Chi tiet don hang'],
    ['Payment', 'PaymentID (PK), Amount, PaymentDate, PaymentMethod', 'Thanh toan (super-type)'],
    ['OrderPayment', 'PaymentID (PK,FK), OrderID (FK)', 'Thanh toan cho don hang'],
    ['ServicePayment', 'PaymentID (PK,FK), ServiceRecordID (FK)', 'Thanh toan cho dich vu bao tri'],
    ['WarrantyRegistration', 'WarrantyID (PK), RobotID (FK,UQ), CustomerID (FK), StartDate, EndDate', 'Dang ky bao hanh'],
    ['ServiceRequest', 'RequestID (PK), RobotID (FK), CustomerID (FK), IssueDescription, RequestDate, Status', 'Yeu cau dich vu/sua chua'],
    ['MaintenanceRecord', 'RecordID (PK), RequestID (FK,UQ), TechnicianID (FK), ActionsTaken, ServiceFee, CompletionDate', 'Ket qua bao tri'],
    ['ReplacedPart', 'RecordID (PK,FK), PartName (PK)', 'Linh kien thay the'],
    ['DeviceLog', 'LogID (PK), RobotID (FK), LogTime, ErrorCode', 'Du lieu IoT tu robot'],
    ['LogStatistic', 'LogID (PK,FK), MetricName (PK), MetricValue', 'Chi so do duoc tu log'],
]


def add_db_overview(doc):
    heading1(doc, '2. Database Design (from Lab 4)')
    add_para(doc, (
        'Co so du lieu HCRS_DB gom 16 quan he (da chuan hoa BCNF o Lab 4) bao phu cac nghiep vu: '
        'quan ly nguoi dung, san pham & ton kho, ban hang & thanh toan, bao hanh, bao tri va du '
        'lieu IoT. Bang duoi tom tat cac thuc the chinh; phan DDL day du (kieu du lieu, PK/FK/UNIQUE/'
        'CHECK/DEFAULT) va du lieu mau nam o PART 1 va PART 2 cua file Lab5_HCRS.sql.'))
    create_table(doc, ['Bang (Relation)', 'Thuoc tinh chinh', 'Y nghia'], DB_TABLES)
    add_para(doc, (
        'Ngoai 16 bang tren, Lab 5 bo sung bang ho tro RobotStatusAudit de luu nhat ky thay doi '
        'trang thai robot (duoc ghi tu dong boi trigger).'), space_before=6)
    add_para(doc, (
        'Du lieu mau: 8 khach hang, 8 nhan vien, 6 mau robot, 15 robot, 8 don hang, 8 thanh toan, '
        '8 dang ky bao hanh, 7 yeu cau dich vu, 6 ban ghi bao tri va 10 ban ghi log IoT - du de moi '
        'truy van tra ve ket qua co y nghia.'), space_before=4)
    doc.add_page_break()


def add_conclusion(doc):
    heading1(doc, '8. Conclusion and Reflection')
    add_para(doc, (
        'Qua Lab 5, nhom da thuc hanh day du cac ky thuat lap trinh SQL tren co so du lieu thuc te '
        'cua he thong ban hang & bao tri robot ve sinh gia dung. Tu cac truy van co ban (SELECT, '
        'WHERE, ORDER BY, ham tong hop), den truy van trung cap (JOIN, GROUP BY/HAVING, subquery) '
        'va nang cao (subquery long nhau, EXISTS/IN/ANY/ALL, UNION/INTERSECT/EXCEPT), nhom da khai '
        'thac du lieu o nhieu goc do nghiep vu khac nhau.'))
    add_para(doc, 'Mot so diem rut ra:', bold=True, space_before=4, space_after=2)
    add_bullet(doc, 'Functions giup tai su dung logic (vd: kiem tra tinh trang bao hanh, tinh tong chi tieu khach hang) va dung lai duoc trong View, Procedure.')
    add_bullet(doc, 'Stored Procedures dong goi cac thao tac da buoc (tao don hang, dang ky bao hanh, hoan tat bao tri) trong transaction, dam bao tinh toan ven du lieu khi co loi.')
    add_bullet(doc, 'Triggers ap dat quy tac nghiep vu o muc CSDL: tu dong cap nhat ton kho sau ban hang, ghi nhat ky thay doi trang thai, chan xoa ban ghi cha con rang buoc, va mien phi dich vu khi con bao hanh.')
    add_bullet(doc, 'Views giup don gian hoa cac truy van phuc tap, con Indexes (don cot va ghep) cai thien hieu nang truy van loc/sap xep.')
    add_para(doc, (
        'Viec ket hop rang buoc (Lab 4) voi Functions/Procedures/Triggers (Lab 5) tao nen mot CSDL '
        'vua chac chan ve cau truc, vua tu dong hoa duoc nghiep vu, giam phu thuoc vao tang ung dung '
        'va han che sai sot du lieu.'), space_before=6)


# ============================================================================
# BUILD
# ============================================================================
def build_report(meta, sections):
    doc = setup_document()
    add_cover(doc, meta)
    add_toc(doc)
    add_objective(doc)
    add_db_overview(doc)

    emitted_sql_heading = False
    for sec in sections:
        title = sec['title']
        num, disp = SECTION_MAP.get(title, ('', title))
        if title in QUERY_SECTIONS:
            if not emitted_sql_heading:
                heading1(doc, '3. SQL Queries')
                add_para(doc, (
                    'Phan nay trinh bay cac truy van SQL theo ba muc do tang dan: co ban, trung cap '
                    'va nang cao. Moi truy van deu kem giai thich va cho chen anh chup ket qua thuc thi.'))
                emitted_sql_heading = True
            heading2(doc, '%s. %s' % (num, disp))
        else:
            heading1(doc, '%s. %s' % (num, disp))

        for g in sec['groups']:
            n = 0
            if g['title']:
                heading3(doc, g['title'])
            for it in g['items']:
                n += 1
                add_item(doc, n, it)

        doc.add_page_break()

    add_conclusion(doc)
    doc.save(OUT_FILE)
    return doc


def main():
    if not os.path.exists(SQL_FILE):
        raise SystemExit('Khong tim thay file %s' % SQL_FILE)
    meta, sections = parse_sql(SQL_FILE)

    total_items = sum(len(g['items']) for s in sections for g in s['groups'])
    print('Da doc %s' % SQL_FILE)
    for s in sections:
        cnt = sum(len(g['items']) for g in s['groups'])
        print('  - Section "%s": %d muc' % (s['title'], cnt))
    print('Tong cong: %d muc trong %d section.' % (total_items, len(sections)))

    build_report(meta, sections)
    print('Da tao bao cao: %s' % OUT_FILE)


if __name__ == '__main__':
    main()
