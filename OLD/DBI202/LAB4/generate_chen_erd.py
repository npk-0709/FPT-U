"""
Generator for the Conceptual ERD in Chen notation (draw.io / .drawio).

Chen notation conventions used:
- Strong entity        : single rectangle
- Weak entity          : double rectangle (identifying relationship = double diamond)
- Associative/composite: rectangle with an inner diamond
- Attribute            : ellipse (oval); primary key = underlined text
- Relationship         : diamond, with 1 / N / M cardinality labels on the lines

Run:  python generate_chen_erd.py
Output: Lab4_Conceptual_ERD.drawio
"""

import math
import os

# ------------------------------------------------------------------ layout grid
X0, Y0 = 360, 340
DX, DY = 1040, 940


def center(col, row):
    return X0 + col * DX, Y0 + row * DY


# (col, row) for every entity
POS = {
    'RobotModel': (0, 0), 'RobotUnit': (1, 0), 'DeviceLog': (2, 0), 'LogStatistic': (3, 0),
    'ModelFeature': (0, 1), 'WarrantyRegistration': (1, 1), 'ServiceRequest': (2, 1), 'MaintenanceRecord': (3, 1),
    'Customer': (0, 2), 'SalesOrder': (1, 2), 'OrderDetail': (2, 2), 'ReplacedPart': (3, 2),
    'Employee': (0, 3), 'OrderPayment': (1, 3), 'Payment': (2, 3), 'ServicePayment': (3, 3),
}

# entity kind: 'strong', 'weak', 'assoc'
KIND = {
    'Customer': 'strong', 'Employee': 'strong', 'RobotModel': 'strong', 'RobotUnit': 'strong',
    'SalesOrder': 'strong', 'Payment': 'strong', 'WarrantyRegistration': 'strong',
    'ServiceRequest': 'strong', 'MaintenanceRecord': 'strong', 'DeviceLog': 'strong',
    'ModelFeature': 'weak', 'ReplacedPart': 'weak', 'LogStatistic': 'weak',
    'OrderDetail': 'assoc', 'OrderPayment': 'assoc', 'ServicePayment': 'assoc',
}

# attributes per entity: (name, is_pk). FK attributes are intentionally omitted at the
# conceptual level because they are represented by relationships.
ATTRS = {
    'Customer': [('CustomerID', True), ('FullName', False), ('PhoneNumber', False),
                 ('Email', False), ('Address', False), ('Password', False)],
    'Employee': [('EmployeeID', True), ('FullName', False), ('Role', False),
                 ('PhoneNumber', False), ('Email', False), ('Password', False)],
    'RobotModel': [('ModelID', True), ('Brand', False), ('ModelName', False),
                   ('Specifications', False), ('UnitPrice', False), ('WarrantyDuration', False)],
    'RobotUnit': [('RobotID', True), ('SerialNumber', False), ('Status', False)],
    'SalesOrder': [('OrderID', True), ('OrderDate', False), ('TotalAmount', False), ('OrderStatus', False)],
    'Payment': [('PaymentID', True), ('Amount', False), ('PaymentDate', False), ('PaymentMethod', False)],
    'WarrantyRegistration': [('WarrantyID', True), ('StartDate', False), ('EndDate', False)],
    'ServiceRequest': [('RequestID', True), ('IssueDescription', False),
                       ('RequestDate', False), ('Status', False)],
    'MaintenanceRecord': [('RecordID', True), ('ActionsTaken', False),
                          ('ServiceFee', False), ('CompletionDate', False)],
    'DeviceLog': [('LogID', True), ('LogTime', False), ('ErrorCode', False)],
    # weak entities -> partial key (shown underlined as well)
    'ModelFeature': [('Feature', True)],
    'ReplacedPart': [('PartName', True)],
    'LogStatistic': [('MetricName', True), ('MetricValue', False)],
    # associative entities -> own descriptive attributes only
    'OrderDetail': [('SellingPrice', False)],
    'OrderPayment': [],
    'ServicePayment': [],
}

# Regular relationships between two strong entities: (A, cardA, name, B, cardB)
RELATIONSHIPS = [
    ('Customer', '1', 'Places', 'SalesOrder', 'N'),
    ('Employee', '1', 'Processes', 'SalesOrder', 'N'),
    ('RobotModel', '1', 'Includes', 'RobotUnit', 'N'),
    ('Customer', '1', 'Registers', 'WarrantyRegistration', 'N'),
    ('RobotUnit', '1', 'CoveredBy', 'WarrantyRegistration', '1'),
    ('Customer', '1', 'Submits', 'ServiceRequest', 'N'),
    ('RobotUnit', '1', 'Reports', 'ServiceRequest', 'N'),
    ('ServiceRequest', '1', 'ResultsIn', 'MaintenanceRecord', '1'),
    ('Employee', '1', 'Performs', 'MaintenanceRecord', 'N'),
    ('RobotUnit', '1', 'Generates', 'DeviceLog', 'N'),
]

# Weak entity -> owner, identifying relationship (double diamond): (weak, name, owner, cardOwner)
WEAK_RELS = [
    ('ModelFeature', 'Has', 'RobotModel', '1'),
    ('ReplacedPart', 'Replaces', 'MaintenanceRecord', '1'),
    ('LogStatistic', 'Holds', 'DeviceLog', '1'),
]

# Associative entity links: assoc connects to two participants with cardinalities.
# (assoc, participantA, cardOnA, participantB, cardOnB)
ASSOC_LINKS = [
    ('OrderDetail', 'SalesOrder', 'N', 'RobotUnit', '1'),
    ('OrderPayment', 'SalesOrder', 'N', 'Payment', '1'),
    ('ServicePayment', 'MaintenanceRecord', 'N', 'Payment', '1'),
]

cells = []


def esc(s):
    """Escape a string (including embedded HTML tags) for use in an XML attribute value."""
    return (s.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')
             .replace('"', '&quot;'))


def add_rect(cid, x, y, w, h, label, style):
    cells.append(
        f'<mxCell id="{cid}" value="{esc(label)}" style="{style}" vertex="1" parent="1">'
        f'<mxGeometry x="{x:.0f}" y="{y:.0f}" width="{w}" height="{h}" as="geometry"/></mxCell>')


def add_edge(cid, src, tgt, label, style):
    cells.append(
        f'<mxCell id="{cid}" value="{esc(label)}" style="{style}" edge="1" parent="1" '
        f'source="{src}" target="{tgt}"><mxGeometry relative="1" as="geometry"/></mxCell>')


# styles -- high-contrast palette (dark borders, solid fills, bold dark text)
S_ENTITY = ('rounded=0;whiteSpace=wrap;html=1;fillColor=#4472C4;strokeColor=#1F3864;'
            'fontColor=#FFFFFF;strokeWidth=3;fontSize=20;fontStyle=1;'
            'verticalAlign=middle;align=center;')
S_WEAK_OUT = ('rounded=0;whiteSpace=wrap;html=1;fillColor=#8FAADC;strokeColor=#1F3864;'
              'fontColor=#0A1A33;strokeWidth=3;fontSize=20;fontStyle=1;'
              'verticalAlign=middle;align=center;')
S_WEAK_IN = 'rounded=0;whiteSpace=wrap;html=1;fillColor=none;strokeColor=#1F3864;strokeWidth=2.5;'
S_ASSOC_OUT = ('rounded=0;whiteSpace=wrap;html=1;fillColor=#70AD47;strokeColor=#274E13;'
               'fontColor=#FFFFFF;strokeWidth=3;fontSize=20;fontStyle=1;'
               'verticalAlign=top;align=center;spacingTop=6;')
S_ASSOC_IN = 'rhombus;whiteSpace=wrap;html=1;fillColor=none;strokeColor=#FFFFFF;strokeWidth=2.5;'
S_DIAMOND = ('rhombus;whiteSpace=wrap;html=1;fillColor=#FFC000;strokeColor=#7F6000;'
             'fontColor=#3D2E00;strokeWidth=3;fontSize=17;fontStyle=1;')
S_DD_IN = 'rhombus;whiteSpace=wrap;html=1;fillColor=none;strokeColor=#7F6000;strokeWidth=2.5;'
S_ATTR = ('ellipse;whiteSpace=wrap;html=1;fillColor=#FBE5D6;strokeColor=#C55A11;'
          'fontColor=#3D1A00;strokeWidth=2.5;fontSize=15;')
S_ATTR_LINE = 'endArrow=none;html=1;strokeColor=#C55A11;strokeWidth=2;'
S_REL_LINE = ('endArrow=none;html=1;edgeStyle=orthogonalEdgeStyle;rounded=0;'
              'strokeColor=#1F1F1F;strokeWidth=2.5;fontSize=20;fontStyle=1;fontColor=#C00000;'
              'labelBackgroundColor=#FFFFFF;')

EW, EH = 210, 92          # entity rect size
AW, AH = 166, 62          # attribute oval size
DSZ_W, DSZ_H = 156, 88    # diamond size
AR = 210                  # attribute radius


def build_entity(name):
    cx, cy = center(*POS[name])
    kind = KIND[name]
    if kind == 'assoc':
        w, h = 240, 132
        add_rect(name, cx - w / 2, cy - h / 2, w, h, name, S_ASSOC_OUT)
        add_rect(name + '_in', cx - (w - 24) / 2, cy - (h - 44) / 2 + 8,
                 w - 24, h - 44, '', S_ASSOC_IN)
    elif kind == 'weak':
        w, h = EW, EH
        add_rect(name, cx - w / 2, cy - h / 2, w, h, name, S_WEAK_OUT)
        add_rect(name + '_in', cx - (w - 12) / 2, cy - (h - 12) / 2, w - 12, h - 12, '', S_WEAK_IN)
    else:
        w, h = EW, EH
        add_rect(name, cx - w / 2, cy - h / 2, w, h, name, S_ENTITY)

    # attributes radially
    attrs = ATTRS.get(name, [])
    n = len(attrs)
    if n == 0:
        return
    start = -math.pi / 2
    for i, (aname, is_pk) in enumerate(attrs):
        ang = start + (2 * math.pi) * i / max(n, 1)
        ax = cx + AR * math.cos(ang) - AW / 2
        ay = cy + AR * math.sin(ang) - AH / 2
        label = f'<u>{aname}</u>' if is_pk else aname
        aid = f'{name}_a{i}'
        add_rect(aid, ax, ay, AW, AH, label, S_ATTR)
        add_edge(f'{name}_l{i}', name, aid, '', S_ATTR_LINE)


def build_diamond(did, ax, ay, label, double=False):
    add_rect(did, ax - DSZ_W / 2, ay - DSZ_H / 2, DSZ_W, DSZ_H, label, S_DIAMOND)
    if double:
        add_rect(did + '_in', ax - (DSZ_W - 16) / 2, ay - (DSZ_H - 16) / 2,
                 DSZ_W - 16, DSZ_H - 16, '', S_DD_IN)


def main():
    for name in POS:
        build_entity(name)

    # regular relationships: diamond at midpoint
    for k, (a, ca, rname, b, cb) in enumerate(RELATIONSHIPS):
        ax, ay = center(*POS[a])
        bx, by = center(*POS[b])
        mx, my = (ax + bx) / 2, (ay + by) / 2
        did = f'rel{k}'
        build_diamond(did, mx, my, rname, double=False)
        add_edge(f'rel{k}_a', a, did, ca, S_REL_LINE)
        add_edge(f'rel{k}_b', did, b, cb, S_REL_LINE)

    # weak (identifying) relationships: double diamond at midpoint
    for k, (weak, rname, owner, cown) in enumerate(WEAK_RELS):
        wx, wy = center(*POS[weak])
        ox, oy = center(*POS[owner])
        mx, my = (wx + ox) / 2, (wy + oy) / 2
        did = f'wrel{k}'
        build_diamond(did, mx, my, rname, double=True)
        add_edge(f'wrel{k}_a', owner, did, cown, S_REL_LINE)
        add_edge(f'wrel{k}_b', did, weak, 'N', S_REL_LINE)

    # associative entities connect directly to participants
    for k, (assoc, pa, ca, pb, cb) in enumerate(ASSOC_LINKS):
        add_edge(f'as{k}_a', pa, assoc, ca, S_REL_LINE)
        add_edge(f'as{k}_b', assoc, pb, cb, S_REL_LINE)

    body = '\n        '.join(cells)
    xml = (
        '<mxfile host="Electron">\n'
        '  <diagram name="Conceptual-ERD-Chen" id="chen-erd-1">\n'
        '    <mxGraphModel dx="2400" dy="1600" grid="0" gridSize="10" guides="1" tooltips="1" '
        'connect="1" arrows="1" fold="1" page="0" pageScale="1" pageWidth="4000" pageHeight="3700" '
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

    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'Lab4_Conceptual_ERD.drawio')
    with open(out, 'w', encoding='utf-8') as f:
        f.write(xml)
    print(f'Chen ERD written to {out}  ({len(cells)} cells)')


if __name__ == '__main__':
    main()
