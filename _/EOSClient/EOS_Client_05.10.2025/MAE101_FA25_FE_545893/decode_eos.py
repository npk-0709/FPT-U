#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Giải mã file .dat từ hệ thống EOS
# Thử nhiều phương pháp mã hóa phổ biến

import struct

with open('khuongnpse203056.dat', 'rb') as f:
    data = f.read()

print(f"Kích thước file: {len(data)} bytes\n")

# Phương pháp 1: XOR với multi-byte key (key có thể là tên file hoặc mã sinh viên)
print("=== Phương pháp 1: XOR với key dựa trên tên file ===")
keys = [
    b'khuongnpse203056',
    b'se203056',
    b'khuongnp',
    b'EOS',
    b'MAE101',
]

for key in keys:
    print(f"\nThử key: {key.decode('ascii', errors='ignore')}")
    key_len = len(key)
    decoded = bytes([data[i] ^ key[i % key_len] for i in range(min(1000, len(data)))])
    
    # Kiểm tra xem có phải text không
    try:
        text = decoded.decode('utf-8', errors='strict')
        printable = sum(1 for c in text if c.isprintable() or c in '\n\r\t')
        if printable > len(text) * 0.7:
            print(f"✓ Tìm thấy! Preview:")
            print(text[:500])
            
            # Giải mã toàn bộ
            full_decoded = bytes([data[i] ^ key[i % key_len] for i in range(len(data))])
            with open(f'decoded_key_{key.decode("ascii", errors="ignore")}.txt', 'wb') as f:
                f.write(full_decoded)
            print(f"✓ Đã lưu vào: decoded_key_{key.decode('ascii', errors='ignore')}.txt")
            break
    except:
        print("✗ Không phải key này")

# Phương pháp 2: RC4 (stream cipher phổ biến)
print("\n\n=== Phương pháp 2: RC4 Stream Cipher ===")
try:
    from Crypto.Cipher import ARC4
    for key_str in ['khuongnpse203056', 'se203056', 'EOS', 'MAE101']:
        key = key_str.encode()
        cipher = ARC4.new(key)
        decrypted = cipher.decrypt(data[:1000])
        try:
            text = decrypted.decode('utf-8', errors='strict')
            printable = sum(1 for c in text if c.isprintable() or c in '\n\r\t')
            if printable > len(text) * 0.7:
                print(f"✓ RC4 với key '{key_str}' có vẻ đúng!")
                print(text[:500])
                
                cipher = ARC4.new(key)
                full_decrypted = cipher.decrypt(data)
                with open(f'decoded_rc4_{key_str}.txt', 'wb') as f:
                    f.write(full_decrypted)
                print(f"✓ Đã lưu vào: decoded_rc4_{key_str}.txt")
                break
        except:
            pass
except ImportError:
    print("Thư viện pycryptodome chưa được cài đặt")

# Phương pháp 3: Kiểm tra xem có phải là serialized data không
print("\n\n=== Phương pháp 3: Kiểm tra các định dạng serialization ===")
import pickle
import json

# Thử pickle
try:
    obj = pickle.loads(data)
    print(f"✓ File là Python pickle object!")
    print(f"Type: {type(obj)}")
    print(f"Content: {obj}")
    with open('decoded_pickle.txt', 'w', encoding='utf-8') as f:
        f.write(str(obj))
except:
    print("✗ Không phải pickle")

# Thử json (có thể bị encode thành bytes)
try:
    text = data.decode('utf-8', errors='ignore')
    obj = json.loads(text)
    print(f"✓ File là JSON!")
    with open('decoded_json.txt', 'w', encoding='utf-8') as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)
except:
    print("✗ Không phải JSON")

print("\n" + "="*60)
print("Đã thử tất cả các phương pháp phổ biến.")
print("Nếu không tìm thấy kết quả, file có thể cần key mã hóa đặc biệt.")
