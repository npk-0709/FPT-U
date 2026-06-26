#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import base64
import zlib
import gzip

with open('khuongnpse203056.dat', 'rb') as f:
    data = f.read()

print(f"Kích thước file: {len(data)} bytes")
print(f"Magic bytes (first 16): {data[:16].hex()}")
print()

# Kiểm tra các file signature phổ biến
signatures = {
    b'PK': 'ZIP/JAR/DOCX',
    b'\x1f\x8b': 'GZIP',
    b'BZh': 'BZIP2',
    b'\x50\x4b\x03\x04': 'ZIP',
    b'\x50\x4b\x05\x06': 'ZIP (empty)',
    b'\x50\x4b\x07\x08': 'ZIP (spanned)',
    b'\x89PNG': 'PNG',
    b'GIF8': 'GIF',
    b'\xff\xd8\xff': 'JPEG',
    b'%PDF': 'PDF',
    b'\x7fELF': 'ELF executable',
    b'MZ': 'Windows executable',
    b'\xca\xfe\xba\xbe': 'Java class',
}

print("=== Kiểm tra File Signature ===")
for sig, desc in signatures.items():
    if data.startswith(sig):
        print(f"✓ Phát hiện: {desc}")
        break
else:
    print("Không nhận diện được signature")

# Thử decompress
print("\n=== Thử giải nén ===")
try:
    decompressed = zlib.decompress(data)
    print(f"✓ ZLIB decompress thành công! Kích thước: {len(decompressed)} bytes")
    with open('decoded_zlib.bin', 'wb') as f:
        f.write(decompressed)
    print("Đã lưu vào: decoded_zlib.bin")
except:
    print("✗ Không phải ZLIB")

try:
    decompressed = gzip.decompress(data)
    print(f"✓ GZIP decompress thành công! Kích thước: {len(decompressed)} bytes")
    with open('decoded_gzip.bin', 'wb') as f:
        f.write(decompressed)
    print("Đã lưu vào: decoded_gzip.bin")
except:
    print("✗ Không phải GZIP")

# Thử Base64
print("\n=== Thử Base64 decode ===")
try:
    decoded = base64.b64decode(data)
    print(f"✓ Base64 decode thành công! Kích thước: {len(decoded)} bytes")
    with open('decoded_base64.bin', 'wb') as f:
        f.write(decoded)
    print("Đã lưu vào: decoded_base64.bin")
except:
    print("✗ Không phải Base64")

# Phân tích entropy để xác định có mã hóa không
print("\n=== Phân tích Entropy ===")
from collections import Counter
byte_counts = Counter(data)
total = len(data)
entropy = 0
for count in byte_counts.values():
    if count > 0:
        p = count / total
        entropy -= p * (p ** 0.5)  # Simplified entropy calculation

print(f"Entropy: {entropy:.4f}")
print("(Entropy cao = dữ liệu được mã hóa/nén; Entropy thấp = dữ liệu thô/text)")

# Thử reverse (đảo ngược bytes)
print("\n=== Thử đảo ngược file ===")
reversed_data = data[::-1]
with open('decoded_reversed.bin', 'wb') as f:
    f.write(reversed_data)
print("Đã lưu file đảo ngược vào: decoded_reversed.bin")

print("\n" + "="*60)
print("Tóm tắt: Đã thử nhiều phương pháp giải mã.")
print("Kiểm tra các file decoded_*.bin để xem kết quả.")
