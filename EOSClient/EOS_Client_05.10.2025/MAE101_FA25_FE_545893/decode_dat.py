import os

# Đọc file .dat
dat_file = "khuongnpse203056.dat"

# Đọc nội dung file binary
with open(dat_file, 'rb') as f:
    data = f.read()

print(f"File size: {len(data)} bytes")
print(f"First 100 bytes (hex): {data[:100].hex()}")
print(f"First 100 bytes (raw): {data[:100]}")

# Thử decode theo nhiều cách
print("\n--- Thử decode UTF-8 ---")
try:
    text = data.decode('utf-8')
    print(text[:500])
except:
    print("Không thể decode UTF-8")

print("\n--- Thử decode Latin-1 ---")
try:
    text = data.decode('latin-1')
    print(text[:500])
except:
    print("Không thể decode Latin-1")

print("\n--- Thử decode ASCII (ignore errors) ---")
try:
    text = data.decode('ascii', errors='ignore')
    print(text[:500])
except:
    print("Không thể decode ASCII")

# Kiểm tra xem có phải là file mã hóa XOR đơn giản
print("\n--- Thử XOR decode với các key phổ biến ---")
for key in [0x55, 0xAA, 0xFF, 0x42, 0x69]:
    decoded = bytes([b ^ key for b in data[:100]])
    try:
        text = decoded.decode('utf-8', errors='ignore')
        if any(c.isalpha() for c in text):
            print(f"Key 0x{key:02x}: {text[:100]}")
    except:
        pass

# Lưu kết quả vào file text để kiểm tra
with open("decoded_output.txt", "w", encoding="utf-8", errors="ignore") as f:
    f.write("=== HEX DUMP ===\n")
    f.write(data.hex())
    f.write("\n\n=== RAW (Latin-1) ===\n")
    f.write(data.decode('latin-1', errors='ignore'))

print(f"\n✓ Đã lưu kết quả vào decoded_output.txt")
