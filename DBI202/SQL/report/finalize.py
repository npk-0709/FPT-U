"""Cap nhat muc luc bang Microsoft Word va xuat ban PDF de kiem tra.

Chay sau build_docx.py:  python finalize.py
"""

import os
import sys

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DOCX = os.path.join(BASE_DIR, "BaoCao_dbCOMPANY.docx")
PDF = os.path.join(BASE_DIR, "BaoCao_dbCOMPANY.pdf")
PREVIEW_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "preview")

WD_FORMAT_PDF = 17


def update_and_export(docx_path=DOCX, pdf_path=PDF):
    import win32com.client as win32

    app = win32.gencache.EnsureDispatch("Word.Application")
    app.Visible = False
    app.DisplayAlerts = False
    doc = None
    try:
        doc = app.Documents.Open(docx_path)
        # Cap nhat hai lan: lan dau dien noi dung, lan sau chinh lai so trang
        for _ in range(2):
            for i in range(1, doc.TablesOfContents.Count + 1):
                doc.TablesOfContents(i).Update()
            doc.Fields.Update()
            for section in doc.Sections:
                for hf in (section.Footers, section.Headers):
                    for item in hf:
                        item.Range.Fields.Update()
        doc.Save()
        doc.ExportAsFixedFormat(pdf_path, WD_FORMAT_PDF)
        pages = doc.ComputeStatistics(2)  # wdStatisticPages
    finally:
        if doc is not None:
            doc.Close(SaveChanges=False)
        app.Quit()
    return pages


def render_preview(pdf_path=PDF, pages=None, zoom=1.5):
    import fitz

    if not os.path.isdir(PREVIEW_DIR):
        os.makedirs(PREVIEW_DIR)
    doc = fitz.open(pdf_path)
    targets = pages if pages else range(len(doc))
    out = []
    for idx in targets:
        if idx >= len(doc):
            continue
        pix = doc[idx].get_pixmap(matrix=fitz.Matrix(zoom, zoom))
        path = os.path.join(PREVIEW_DIR, "page_%02d.png" % (idx + 1))
        pix.save(path)
        out.append(path)
    total = len(doc)
    doc.close()
    return out, total


if __name__ == "__main__":
    print("Cap nhat muc luc bang Microsoft Word ...")
    pages = update_and_export()
    print("  So trang: %s" % pages)
    print("  Da luu PDF: %s" % PDF)
    wanted = [int(a) - 1 for a in sys.argv[1:]] or None
    files, total = render_preview(pages=wanted)
    print("  PDF co %d trang, da xuat %d anh xem truoc:" % (total, len(files)))
    for f in files:
        print("    %s" % f)
