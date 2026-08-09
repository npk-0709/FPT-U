"""Doc file DBC.sql va bóc tách toan bo DDL + du lieu mau.

Muc dich: moi so lieu xuat hien trong bao cao Word deu duoc tinh truc tiep tu
script SQL goc, tranh sai lech do go tay.
"""

import datetime
import os
import re

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SQL_PATH = os.path.join(BASE_DIR, "DBC.sql")

CREATE_TABLE_RE = re.compile(
    r"CREATE TABLE \[dbo\]\.\[(?P<table>\w+)\]\((?P<body>.*?)\n\) ON \[PRIMARY\]",
    re.S | re.I,
)
COLUMN_RE = re.compile(
    r"^\s*\[(?P<name>\w+)\]\s+\[(?P<type>\w+)\]"
    r"(?:\((?P<args>[^)]*)\))?"
    r"(?P<identity>\s+IDENTITY\(\d+,\s*\d+\))?"
    r"\s+(?P<null>NOT NULL|NULL)",
    re.I,
)
PK_RE = re.compile(
    r"CONSTRAINT \[(?P<name>\w+)\] PRIMARY KEY CLUSTERED\s*\((?P<cols>.*?)\)WITH",
    re.S | re.I,
)
FK_RE = re.compile(
    r"ALTER TABLE \[dbo\]\.\[(?P<table>\w+)\]\s+WITH CHECK ADD\s+CONSTRAINT "
    r"\[(?P<name>\w+)\] FOREIGN KEY\(\[(?P<col>\w+)\]\)\s*"
    r"REFERENCES \[dbo\]\.\[(?P<ref_table>\w+)\] \(\[(?P<ref_col>\w+)\]\)",
    re.S | re.I,
)
INSERT_RE = re.compile(
    r"^INSERT \[dbo\]\.\[(?P<table>\w+)\] \((?P<cols>[^)]*)\) VALUES \((?P<vals>.*)\)\s*$",
    re.I,
)
CAST_RE = re.compile(r"^CAST\((?P<inner>.*?)\s+AS\s+(?P<type>.*)\)$", re.S | re.I)


def _split_top_level(text):
    """Tach danh sach gia tri theo dau phay o muc ngoai cung."""
    parts, buf, depth, in_str = [], [], 0, False
    i = 0
    while i < len(text):
        ch = text[i]
        if in_str:
            if ch == "'":
                if i + 1 < len(text) and text[i + 1] == "'":
                    buf.append("''")
                    i += 2
                    continue
                in_str = False
            buf.append(ch)
        else:
            if ch == "'":
                in_str = True
                buf.append(ch)
            elif ch == "(":
                depth += 1
                buf.append(ch)
            elif ch == ")":
                depth -= 1
                buf.append(ch)
            elif ch == "," and depth == 0:
                parts.append("".join(buf).strip())
                buf = []
            else:
                buf.append(ch)
        i += 1
    if buf:
        parts.append("".join(buf).strip())
    return parts


def _parse_literal(token, sql_type=None):
    token = token.strip()
    if token.upper() == "NULL":
        return None
    m = CAST_RE.match(token)
    if m:
        return _parse_literal(m.group("inner"), m.group("type"))
    if token.startswith("N'") or token.startswith("n'"):
        token = token[1:]
    if token.startswith("'"):
        raw = token[1:-1].replace("''", "'")
        if sql_type and "date" in sql_type.lower():
            for fmt in ("%Y-%m-%d %H:%M:%S.%f", "%Y-%m-%d %H:%M:%S", "%Y-%m-%d"):
                try:
                    return datetime.datetime.strptime(raw, fmt)
                except ValueError:
                    continue
        return raw
    if re.fullmatch(r"-?\d+", token):
        return int(token)
    if re.fullmatch(r"-?\d*\.\d+", token):
        return float(token)
    return token


def load(sql_path=SQL_PATH):
    with open(sql_path, encoding="utf-8-sig") as fh:
        sql = fh.read()

    tables = {}
    order = []
    for m in CREATE_TABLE_RE.finditer(sql):
        table = m.group("table")
        body = m.group("body")
        order.append(table)
        cols = []
        for line in body.split("\n"):
            if "CONSTRAINT" in line.upper():
                break
            cm = COLUMN_RE.match(line)
            if cm:
                type_str = "[%s]" % cm.group("type")
                if cm.group("args"):
                    type_str = "%s(%s)" % (cm.group("type"), cm.group("args").replace(" ", ""))
                else:
                    type_str = cm.group("type")
                cols.append(
                    {
                        "name": cm.group("name"),
                        "type": type_str,
                        "nullable": cm.group("null").upper() == "NULL",
                        "identity": bool(cm.group("identity")),
                    }
                )
        pk = []
        pk_name = None
        pm = PK_RE.search(body)
        if pm:
            pk_name = pm.group("name")
            pk = re.findall(r"\[(\w+)\]", pm.group("cols"))
        tables[table] = {"columns": cols, "pk": pk, "pk_name": pk_name, "fks": []}

    fks = []
    for m in FK_RE.finditer(sql):
        fk = {
            "name": m.group("name"),
            "table": m.group("table"),
            "column": m.group("col"),
            "ref_table": m.group("ref_table"),
            "ref_column": m.group("ref_col"),
        }
        fks.append(fk)
        tables[fk["table"]]["fks"].append(fk)

    rows = dict((t, []) for t in tables)
    for line in sql.split("\n"):
        line = line.strip()
        if not line.upper().startswith("INSERT "):
            continue
        m = INSERT_RE.match(line)
        if not m:
            raise ValueError("Khong parse duoc dong INSERT: %s" % line[:80])
        table = m.group("table")
        cols = re.findall(r"\[(\w+)\]", m.group("cols"))
        vals = [_parse_literal(v) for v in _split_top_level(m.group("vals"))]
        if len(cols) != len(vals):
            raise ValueError("Lech so cot/gia tri tai: %s" % line[:80])
        rows.setdefault(table, []).append(dict(zip(cols, vals)))

    return {"sql": sql, "order": order, "tables": tables, "fks": fks, "rows": rows}


if __name__ == "__main__":
    db = load()
    print("Tables:", ", ".join(db["order"]))
    for t in db["order"]:
        print("  %-16s cols=%d pk=%s rows=%d" % (
            t, len(db["tables"][t]["columns"]), db["tables"][t]["pk"], len(db["rows"][t])))
    print("Foreign keys:", len(db["fks"]))
    for fk in db["fks"]:
        print("  %s.%s -> %s.%s" % (fk["table"], fk["column"], fk["ref_table"], fk["ref_column"]))
