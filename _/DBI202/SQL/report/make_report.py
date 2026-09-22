"""Chay toan bo quy trinh: doc DBC.sql -> ve hinh -> sinh Word -> xuat PDF.

    python make_report.py            (sinh Word, cap nhat muc luc, xuat PDF)
    python make_report.py --no-pdf   (chi sinh Word)
"""

import sys

import build_docx


def main():
    build_docx.main()
    if "--no-pdf" in sys.argv:
        print("Bo qua buoc cap nhat muc luc va xuat PDF.")
        return
    try:
        import finalize
    except ImportError as exc:
        print("Khong the nap finalize.py (%s). Bo qua buoc PDF." % exc)
        return
    try:
        pages = finalize.update_and_export()
        print("Da cap nhat muc luc va xuat PDF (%s trang)." % pages)
    except Exception as exc:
        print("Khong cap nhat duoc muc luc bang Word: %s" % exc)
        print("File .docx van dung duoc; hay mo Word roi bam Ctrl+A, F9.")


if __name__ == "__main__":
    main()
