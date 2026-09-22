"""
Generator for the Relational Model schema diagram (Elmasri/Navathe style),
matching the standard textbook format:
  - each relation = one horizontal row of column cells
  - relation name in bold above the row
  - primary-key attributes underlined
  - foreign keys shown as arrows pointing to the referenced primary-key column
  - plain black & white (no colors)

Run:  python generate_relational_model.py
Output: Lab4_Relational_Model.drawio
"""
import os

# (TableName, [ (col_name, is_pk, fk_target_or_None) ]) -- ordered so FK arrows point upward
TABLES = [
    ('Customer', [('CustomerID', True, None), ('FullName', False, None), ('PhoneNumber', False, None),
                  ('Email', False, None), ('Address', False, None), ('Password', False, None)]),
    ('Employee', [('EmployeeID', True, None), ('FullName', False, None), ('Role', False, None),
                  ('PhoneNumber', False, None), ('Email', False, None), ('Password', False, None)]),
    ('RobotModel', [('ModelID', True, None), ('Brand', False, None), ('ModelName', False, None),
                    ('Specifications', False, None), ('UnitPrice', False, None), ('WarrantyDuration', False, None)]),
    ('Payment', [('PaymentID', True, None), ('Amount', False, None), ('PaymentDate', False, None),
                 ('PaymentMethod', False, None)]),
    ('RobotUnit', [('RobotID', True, None), ('ModelID', False, ('RobotModel', 'ModelID')),
                   ('SerialNumber', False, None), ('Status', False, None)]),
    ('ModelFeature', [('ModelID', True, ('RobotModel', 'ModelID')), ('Feature', True, None)]),
    ('SalesOrder', [('OrderID', True, None), ('CustomerID', False, ('Customer', 'CustomerID')),
                    ('EmployeeID', False, ('Employee', 'EmployeeID')), ('OrderDate', False, None),
                    ('TotalAmount', False, None), ('OrderStatus', False, None)]),
    ('WarrantyRegistration', [('WarrantyID', True, None), ('RobotID', False, ('RobotUnit', 'RobotID')),
                              ('CustomerID', False, ('Customer', 'CustomerID')), ('StartDate', False, None),
                              ('EndDate', False, None)]),
    ('ServiceRequest', [('RequestID', True, None), ('RobotID', False, ('RobotUnit', 'RobotID')),
                        ('CustomerID', False, ('Customer', 'CustomerID')), ('IssueDescription', False, None),
                        ('RequestDate', False, None), ('Status', False, None)]),
    ('DeviceLog', [('LogID', True, None), ('RobotID', False, ('RobotUnit', 'RobotID')),
                   ('LogTime', False, None), ('ErrorCode', False, None)]),
    ('OrderDetail', [('RobotID', True, ('RobotUnit', 'RobotID')), ('OrderID', False, ('SalesOrder', 'OrderID')),
                     ('SellingPrice', False, None)]),
    ('OrderPayment', [('PaymentID', True, ('Payment', 'PaymentID')), ('OrderID', False, ('SalesOrder', 'OrderID'))]),
    ('MaintenanceRecord', [('RecordID', True, None), ('RequestID', False, ('ServiceRequest', 'RequestID')),
                           ('TechnicianID', False, ('Employee', 'EmployeeID')), ('ActionsTaken', False, None),
                           ('ServiceFee', False, None), ('CompletionDate', False, None)]),
    ('LogStatistic', [('LogID', True, ('DeviceLog', 'LogID')), ('MetricName', True, None),
                      ('MetricValue', False, None)]),
    ('ServicePayment', [('PaymentID', True, ('Payment', 'PaymentID')),
                        ('ServiceRecordID', False, ('MaintenanceRecord', 'RecordID'))]),
    ('ReplacedPart', [('RecordID', True, ('MaintenanceRecord', 'RecordID')), ('PartName', True, None)]),
]

# ----- layout (two columns, FK arrows routed through a central corridor)
LEFT = 40
NAME_DY = 34          # name label TOP sits this far above the row
NAME_H = 16
ROW_H = 30
PITCH = 92            # vertical distance between successive tables (compact)
TOP = 50
CHAR_W = 7.2
MIN_W = 64
LANE_STEP = 13
PER_COL = 8           # tables per column

CELL = ('rounded=0;whiteSpace=wrap;html=1;fillColor=none;strokeColor=#000000;'
        'fontColor=#000000;fontSize=12;')
NAME = 'text;html=1;strokeColor=none;fillColor=none;fontColor=#000000;fontStyle=1;fontSize=13;align=left;verticalAlign=middle;'
EDGE = ('edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;endArrow=classicThin;endFill=1;'
        'startArrow=none;strokeColor=#000000;strokeWidth=1.2;'
        'exitX=0.5;exitY=1;exitDx=0;exitDy=0;entryX=0.5;entryY=0;entryDx=0;entryDy=0;')

cells = []
pos = {}          # cell_id -> (x, y, w)
pk_cell = {}      # (table, col) -> cell_id


def esc(s):
    return (s.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;'))


def colwidth(name):
    return max(MIN_W, int(len(name) * CHAR_W) + 16)


def table_width(cols):
    return sum(colwidth(c[0]) for c in cols)


# column x-origins
left_max_w = max(table_width(cols) for _, cols in TABLES[:PER_COL])
n_fk = sum(1 for _, cols in TABLES for c in cols if c[2] is not None)
CORRIDOR_X0 = LEFT + left_max_w + 28
CORRIDOR_W = n_fk * LANE_STEP + 18
RIGHT_X = CORRIDOR_X0 + CORRIDOR_W + 28
COL_X = [LEFT, RIGHT_X]


def cell_origin(ti):
    col = 0 if ti < PER_COL else 1
    row = ti if ti < PER_COL else ti - PER_COL
    return COL_X[col], TOP + row * PITCH


# build cells
for ti, (tname, cols) in enumerate(TABLES):
    x0, row_y = cell_origin(ti)
    cells.append(
        f'<mxCell id="name_{ti}" value="{esc(tname)}" style="{NAME}" vertex="1" parent="1">'
        f'<mxGeometry x="{x0}" y="{row_y - NAME_DY}" width="320" height="{NAME_H}" as="geometry"/></mxCell>')
    x = x0
    for ci, (cname, is_pk, fk) in enumerate(cols):
        w = colwidth(cname)
        cid = f't{ti}_{ci}'
        label = f'<u>{cname}</u>' if is_pk else cname
        cells.append(
            f'<mxCell id="{cid}" value="{esc(label)}" style="{CELL}" vertex="1" parent="1">'
            f'<mxGeometry x="{x}" y="{row_y}" width="{w}" height="{ROW_H}" as="geometry"/></mxCell>')
        pos[cid] = (x, row_y, w)
        if is_pk:
            pk_cell.setdefault((tname, cname), cid)
        x += w

# build FK edges through the central corridor (one vertical lane per edge)
lane = 0
edge_no = 0
for ti, (tname, cols) in enumerate(TABLES):
    for ci, (cname, is_pk, fk) in enumerate(cols):
        if fk is not None:
            src = f't{ti}_{ci}'
            tgt = pk_cell.get(fk)
            if tgt:
                sx, sy, sw = pos[src]
                tx, ty, tw = pos[tgt]
                scx = sx + sw / 2
                tcx = tx + tw / 2
                bus_x = CORRIDOR_X0 + lane * LANE_STEP
                lane += 1
                p1y = sy + ROW_H + 10          # just below source row (in the gap)
                p2y = ty - 12                  # clean gap just above target row
                pts = (f'<mxPoint x="{scx:.0f}" y="{p1y}"/>'
                       f'<mxPoint x="{bus_x}" y="{p1y}"/>'
                       f'<mxPoint x="{bus_x}" y="{p2y}"/>'
                       f'<mxPoint x="{tcx:.0f}" y="{p2y}"/>')
                cells.append(
                    f'<mxCell id="fk{edge_no}" style="{EDGE}" edge="1" parent="1" '
                    f'source="{src}" target="{tgt}"><mxGeometry relative="1" as="geometry">'
                    f'<Array as="points">{pts}</Array></mxGeometry></mxCell>')
                edge_no += 1

right_max_w = max(table_width(cols) for _, cols in TABLES[PER_COL:])
page_w = RIGHT_X + right_max_w + 60
page_h = TOP + PER_COL * PITCH + 50

body = '\n        '.join(cells)
xml = (
    '<mxfile host="Electron">\n'
    '  <diagram name="Relational-Model" id="relational-model-1">\n'
    f'    <mxGraphModel dx="1400" dy="900" grid="0" gridSize="10" guides="1" tooltips="1" '
    f'connect="1" arrows="1" fold="1" page="0" pageScale="1" pageWidth="{page_w}" pageHeight="{page_h}" '
    'background="#ffffff" math="0" shadow="0">\n'
    '      <root>\n'
    '        <mxCell id="0"/>\n'
    '        <mxCell id="1" parent="0"/>\n'
    f'        {body}\n'
    '      </root>\n'
    '    </mxGraphModel>\n'
    '  </diagram>\n'
    '</mxfile>\n'
)

out = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'Lab4_Relational_Model.drawio')
with open(out, 'w', encoding='utf-8') as f:
    f.write(xml)
print(f'Relational model written to {out}  ({edge_no} FK arrows, {len(TABLES)} tables)')


if __name__ == '__main__':
    pass
