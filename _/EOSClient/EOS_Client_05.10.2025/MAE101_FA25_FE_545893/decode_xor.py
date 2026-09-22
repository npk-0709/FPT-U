#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# Script giải mã file .dat

with open('khuongnpse203056.dat', 'rb') as f:
    data = f.read()

print(f"Kích thước file: {len(data)} bytes\n")

# Thử XOR với các key đơn giản
print("=== Thử giải mã XOR với các key phổ biến ===")
for key in range(256):
    decoded = bytes([b ^ key for b in data[:500]])
    try:
        text = decoded.decode('utf-8', errors='strict')
        # Kiểm tra xem có chứa text có ý nghĩa không
        readable_chars = sum(1 for c in text if c.isprintable() or c in '\n\r\t')
        if readable_chars > len(text) * 0.8:  # Ít nhất 80% ký tự có thể đọc được
            print(f"\n✓ Key tìm thấy: 0x{key:02x} ({key})")
            print(f"Preview (500 ký tự đầu):")
            print(text[:500])
            print("\n" + "="*60)
            
            # Giải mã toàn bộ file
            full_decoded = bytes([b ^ key for b in data])
            with open('decoded_result.txt', 'wb') as out:
                out.write(full_decoded)
            print(f"✓ Đã lưu kết quả đầy đủ vào: decoded_result.txt")
            break
    except:
        pass
else:
    print("\nKhông tìm thấy key XOR đơn giản phù hợp.")
    print("File có thể được mã hóa bằng thuật toán phức tạp hơn.")
    
    # Lưu hex dump để phân tích thêm
    with open('hexdump.txt', 'w') as f:
        for i in range(0, min(1000, len(data)), 16):
            hex_part = ' '.join(f'{b:02x}' for b in data[i:i+16])
            ascii_part = ''.join(chr(b) if 32 <= b < 127 else '.' for b in data[i:i+16])
            f.write(f'{i:08x}  {hex_part:<48}  {ascii_part}\n')
    print("✓ Đã lưu hex dump vào: hexdump.txt")
