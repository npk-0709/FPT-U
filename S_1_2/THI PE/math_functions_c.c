#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>

// ============================================================================
// PHẦN 1: HÀM TOÁN HỌC CƠ BẢN (math.h)
// ============================================================================

void phan1_ham_co_ban() {
    printf("\n=== PHẦN 1: HÀM TOÁN HỌC CƠ BẢN (math.h) ===\n");
    
    double x = 16.0, y = 2.5, z = -3.7;
    
    // 1. Hàm lũy thừa và căn
    printf("1. LŨY THỪA VÀ CĂN:\n");
    printf("   sqrt(%.1f) = %.2f (căn bậc 2)\n", x, sqrt(x));
    printf("   cbrt(%.1f) = %.2f (căn bậc 3)\n", x, cbrt(x));
    printf("   pow(%.1f, %.1f) = %.2f (lũy thừa)\n", y, 3.0, pow(y, 3.0));
    printf("   exp(%.1f) = %.2f (e^x)\n", 1.0, exp(1.0));
    
    // 2. Hàm logarit
    printf("\n2. LOGARIT:\n");
    printf("   log(%.1f) = %.2f (ln - logarit tự nhiên)\n", x, log(x));
    printf("   log10(%.1f) = %.2f (log cơ số 10)\n", x, log10(x));
    printf("   log2(%.1f) = %.2f (log cơ số 2)\n", x, log2(x));
    
    // 3. Hàm làm tròn
    printf("\n3. LÀM TRÒN:\n");
    printf("   ceil(%.1f) = %.0f (làm tròn lên)\n", y, ceil(y));
    printf("   floor(%.1f) = %.0f (làm tròn xuống)\n", y, floor(y));
    printf("   round(%.1f) = %.0f (làm tròn gần nhất)\n", y, round(y));
    printf("   trunc(%.1f) = %.0f (cắt phần thập phân)\n", y, trunc(y));
    
    // 4. Hàm giá trị tuyệt đối
    printf("\n4. GIÁ TRỊ TUYỆT ĐỐI:\n");
    printf("   fabs(%.1f) = %.1f\n", z, fabs(z));
    printf("   abs(%d) = %d (cho số nguyên)\n", -5, abs(-5));
    
    // 5. Hàm modulo (chia lấy dư)
    printf("\n5. CHIA LẤY DƯ:\n");
    printf("   fmod(%.1f, %.1f) = %.1f\n", 7.5, 2.3, fmod(7.5, 2.3));
    printf("   remainder(%.1f, %.1f) = %.1f\n", 7.5, 2.3, remainder(7.5, 2.3));
}

// ============================================================================
// PHẦN 2: HÀM LƯỢNG GIÁC
// ============================================================================

void phan2_ham_luong_giac() {
    printf("\n=== PHẦN 2: HÀM LƯỢNG GIÁC ===\n");
    
    double goc_do = 45.0;
    double goc_rad = goc_do * M_PI / 180.0; // Chuyển độ sang radian
    
    printf("Góc: %.1f độ = %.4f radian\n\n", goc_do, goc_rad);
    
    // 1. Hàm lượng giác cơ bản
    printf("1. HÀM CƠ BẢN:\n");
    printf("   sin(%.1f°) = %.4f\n", goc_do, sin(goc_rad));
    printf("   cos(%.1f°) = %.4f\n", goc_do, cos(goc_rad));
    printf("   tan(%.1f°) = %.4f\n", goc_do, tan(goc_rad));
    
    // 2. Hàm lượng giác ngược
    printf("\n2. HÀM NGƯỢC:\n");
    printf("   asin(%.2f) = %.4f rad = %.2f°\n", 0.5, asin(0.5), asin(0.5) * 180 / M_PI);
    printf("   acos(%.2f) = %.4f rad = %.2f°\n", 0.5, acos(0.5), acos(0.5) * 180 / M_PI);
    printf("   atan(%.2f) = %.4f rad = %.2f°\n", 1.0, atan(1.0), atan(1.0) * 180 / M_PI);
    printf("   atan2(%.1f, %.1f) = %.4f rad\n", 1.0, 1.0, atan2(1.0, 1.0));
    
    // 3. Hàm lượng giác hyperbol
    printf("\n3. HÀM HYPERBOL:\n");
    printf("   sinh(%.1f) = %.4f\n", 1.0, sinh(1.0));
    printf("   cosh(%.1f) = %.4f\n", 1.0, cosh(1.0));
    printf("   tanh(%.1f) = %.4f\n", 1.0, tanh(1.0));
    
    // 4. Hằng số toán học quan trọng
    printf("\n4. HẰNG SỐ TOÁN HỌC:\n");
    printf("   PI (M_PI) = %.10f\n", M_PI);
    printf("   E (M_E) = %.10f\n", M_E);
    printf("   ln(2) (M_LN2) = %.10f\n", M_LN2);
    printf("   ln(10) (M_LN10) = %.10f\n", M_LN10);
}

// ============================================================================
// PHẦN 3: HÀM SỐ NGUYÊN TỐ
// ============================================================================

// Kiểm tra số nguyên tố (cách cơ bản)
int kiem_tra_nguyen_to(int n) {
    if(n < 2) return 0;
    if(n == 2) return 1;
    if(n % 2 == 0) return 0;
    
    for(int i = 3; i <= sqrt(n); i += 2) {
        if(n % i == 0) return 0;
    }
    return 1;
}

// Liệt kê các số nguyên tố <= n
void liet_ke_nguyen_to(int n) {
    printf("Các số nguyên tố <= %d: ", n);
    for(int i = 2; i <= n; i++) {
        if(kiem_tra_nguyen_to(i)) {
            printf("%d ", i);
        }
    }
    printf("\n");
}

// Phân tích thừa số nguyên tố
void phan_tich_thua_so(int n) {
    printf("Phân tích %d = ", n);
    int temp = n;
    int first = 1;
    
    for(int i = 2; i <= sqrt(temp); i++) {
        int count = 0;
        while(n % i == 0) {
            count++;
            n /= i;
        }
        if(count > 0) {
            if(!first) printf(" × ");
            printf("%d", i);
            if(count > 1) printf("^%d", count);
            first = 0;
        }
    }
    
    if(n > 1) {
        if(!first) printf(" × ");
        printf("%d", n);
    }
    printf("\n");
}

void phan3_so_nguyen_to() {
    printf("\n=== PHẦN 3: SỐ NGUYÊN TỐ ===\n");
    
    int n;
    printf("Nhập số n: ");
    scanf("%d", &n);
    
    if(kiem_tra_nguyen_to(n)) {
        printf("%d là số nguyên tố\n\n", n);
    } else {
        printf("%d không là số nguyên tố\n\n", n);
    }
    
    liet_ke_nguyen_to(n);
    printf("\n");
    phan_tich_thua_so(n);
}

// ============================================================================
// PHẦN 4: ƯỚC VÀ BỘI
// ============================================================================

// Tìm ước chung lớn nhất (GCD) - Thuật toán Euclid
int gcd(int a, int b) {
    a = abs(a);
    b = abs(b);
    while(b != 0) {
        int temp = b;
        b = a % b;
        a = temp;
    }
    return a;
}

// Tìm bội chung nhỏ nhất (LCM)
int lcm(int a, int b) {
    return abs(a * b) / gcd(a, b);
}

// Liệt kê tất cả các ước của n
void liet_ke_uoc(int n) {
    printf("Các ước của %d: ", n);
    for(int i = 1; i <= n; i++) {
        if(n % i == 0) {
            printf("%d ", i);
        }
    }
    printf("\n");
}

// Đếm số ước
int dem_so_uoc(int n) {
    int count = 0;
    for(int i = 1; i <= n; i++) {
        if(n % i == 0) {
            count++;
        }
    }
    return count;
}

// Tính tổng các ước (không tính n)
int tong_cac_uoc(int n) {
    int sum = 0;
    for(int i = 1; i < n; i++) {
        if(n % i == 0) {
            sum += i;
        }
    }
    return sum;
}

// Kiểm tra số hoàn hảo (tổng các ước = chính nó)
int kiem_tra_so_hoan_hao(int n) {
    return tong_cac_uoc(n) == n;
}

void phan4_uoc_boi() {
    printf("\n=== PHẦN 4: ƯỚC VÀ BỘI ===\n");
    
    int a, b;
    printf("Nhập hai số a, b: ");
    scanf("%d %d", &a, &b);
    
    printf("\nGCD(%d, %d) = %d\n", a, b, gcd(a, b));
    printf("LCM(%d, %d) = %d\n\n", a, b, lcm(a, b));
    
    printf("Nhập số n để phân tích ước: ");
    int n;
    scanf("%d", &n);
    
    liet_ke_uoc(n);
    printf("Số lượng ước: %d\n", dem_so_uoc(n));
    printf("Tổng các ước (không tính %d): %d\n", n, tong_cac_uoc(n));
    
    if(kiem_tra_so_hoan_hao(n)) {
        printf("%d là số hoàn hảo\n", n);
    } else {
        printf("%d không là số hoàn hảo\n", n);
    }
}

// ============================================================================
// PHẦN 5: GIAI THỪA VÀ TỔ HỢP
// ============================================================================

// Tính giai thừa (iterative)
long long giai_thua(int n) {
    if(n < 0) return -1;
    if(n == 0 || n == 1) return 1;
    
    long long result = 1;
    for(int i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
}

// Tính giai thừa (recursive)
long long giai_thua_de_quy(int n) {
    if(n < 0) return -1;
    if(n == 0 || n == 1) return 1;
    return n * giai_thua_de_quy(n - 1);
}

// Tính tổ hợp C(n, k) = n! / (k! * (n-k)!)
long long to_hop(int n, int k) {
    if(k > n) return 0;
    if(k == 0 || k == n) return 1;
    
    // Tối ưu: C(n, k) = C(n, n-k)
    if(k > n - k) {
        k = n - k;
    }
    
    long long result = 1;
    for(int i = 0; i < k; i++) {
        result = result * (n - i) / (i + 1);
    }
    return result;
}

// Tính chỉnh hợp A(n, k) = n! / (n-k)!
long long chinh_hop(int n, int k) {
    if(k > n) return 0;
    
    long long result = 1;
    for(int i = n; i > n - k; i--) {
        result *= i;
    }
    return result;
}

// Số Fibonacci
long long fibonacci(int n) {
    if(n <= 1) return n;
    
    long long f0 = 0, f1 = 1, fn;
    for(int i = 2; i <= n; i++) {
        fn = f0 + f1;
        f0 = f1;
        f1 = fn;
    }
    return f1;
}

// Số Fibonacci đệ quy
long long fibonacci_de_quy(int n) {
    if(n <= 1) return n;
    return fibonacci_de_quy(n - 1) + fibonacci_de_quy(n - 2);
}

void phan5_giai_thua_to_hop() {
    printf("\n=== PHẦN 5: GIAI THỪA, TỔ HỢP, FIBONACCI ===\n");
    
    int n, k;
    printf("Nhập n: ");
    scanf("%d", &n);
    
    printf("\n1. GIAI THỪA:\n");
    printf("   %d! = %lld\n", n, giai_thua(n));
    
    printf("\n2. TỔ HỢP VÀ CHỈNH HỢP:\n");
    printf("   Nhập k: ");
    scanf("%d", &k);
    printf("   C(%d, %d) = %lld\n", n, k, to_hop(n, k));
    printf("   A(%d, %d) = %lld\n", n, k, chinh_hop(n, k));
    
    printf("\n3. FIBONACCI:\n");
    printf("   Dãy Fibonacci %d số đầu: ", n);
    for(int i = 0; i < n; i++) {
        printf("%lld ", fibonacci(i));
    }
    printf("\n");
}

// ============================================================================
// PHẦN 6: KIỂM TRA CÁC LOẠI SỐ ĐỐC ĐÁO
// ============================================================================

// Kiểm tra số chính phương
int kiem_tra_chinh_phuong(int n) {
    if(n < 0) return 0;
    int can = (int)sqrt(n);
    return can * can == n;
}

// Kiểm tra số đối xứng (palindrome)
int kiem_tra_so_doi_xung(int n) {
    int nguoc = 0, temp = n;
    while(temp > 0) {
        nguoc = nguoc * 10 + temp % 10;
        temp /= 10;
    }
    return nguoc == n;
}

// Kiểm tra số Armstrong (153 = 1^3 + 5^3 + 3^3)
int kiem_tra_armstrong(int n) {
    int temp = n, sum = 0;
    int so_chu_so = 0;
    
    // Đếm số chữ số
    int t = n;
    while(t > 0) {
        so_chu_so++;
        t /= 10;
    }
    
    // Tính tổng
    while(temp > 0) {
        int digit = temp % 10;
        sum += pow(digit, so_chu_so);
        temp /= 10;
    }
    
    return sum == n;
}

// Tính tổng các chữ số
int tong_chu_so(int n) {
    int sum = 0;
    n = abs(n);
    while(n > 0) {
        sum += n % 10;
        n /= 10;
    }
    return sum;
}

// Tích các chữ số
int tich_chu_so(int n) {
    int product = 1;
    n = abs(n);
    while(n > 0) {
        product *= n % 10;
        n /= 10;
    }
    return product;
}

// Đảo ngược số
int dao_nguoc_so(int n) {
    int nguoc = 0;
    int am = n < 0;
    n = abs(n);
    
    while(n > 0) {
        nguoc = nguoc * 10 + n % 10;
        n /= 10;
    }
    
    return am ? -nguoc : nguoc;
}

void phan6_so_dac_biet() {
    printf("\n=== PHẦN 6: KIỂM TRA CÁC LOẠI SỐ ĐẶC BIỆT ===\n");
    
    int n;
    printf("Nhập số n: ");
    scanf("%d", &n);
    
    printf("\nKiểm tra số %d:\n", n);
    printf("- Số chính phương: %s\n", kiem_tra_chinh_phuong(n) ? "Có" : "Không");
    printf("- Số đối xứng: %s\n", kiem_tra_so_doi_xung(n) ? "Có" : "Không");
    printf("- Số Armstrong: %s\n", kiem_tra_armstrong(n) ? "Có" : "Không");
    printf("- Tổng các chữ số: %d\n", tong_chu_so(n));
    printf("- Tích các chữ số: %d\n", tich_chu_so(n));
    printf("- Số đảo ngược: %d\n", dao_nguoc_so(n));
}

// ============================================================================
// PHẦN 7: HÀM SỐ NGẪU NHIÊN
// ============================================================================

// Sinh số ngẫu nhiên trong khoảng [min, max]
int random_range(int min, int max) {
    return min + rand() % (max - min + 1);
}

// Sinh số thực ngẫu nhiên trong [0, 1]
double random_double() {
    return (double)rand() / RAND_MAX;
}

// Sinh số thực ngẫu nhiên trong [min, max]
double random_double_range(double min, double max) {
    return min + (max - min) * random_double();
}

// Trộn mảng ngẫu nhiên (Fisher-Yates shuffle)
void tron_mang(int arr[], int n) {
    for(int i = n - 1; i > 0; i--) {
        int j = rand() % (i + 1);
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

void phan7_so_ngau_nhien() {
    printf("\n=== PHẦN 7: SỐ NGẪU NHIÊN ===\n");
    
    // Khởi tạo seed cho random
    srand(time(NULL));
    
    printf("1. Sinh 10 số nguyên ngẫu nhiên [1, 100]:\n   ");
    for(int i = 0; i < 10; i++) {
        printf("%d ", random_range(1, 100));
    }
    
    printf("\n\n2. Sinh 10 số thực ngẫu nhiên [0.0, 1.0]:\n   ");
    for(int i = 0; i < 10; i++) {
        printf("%.3f ", random_double());
    }
    
    printf("\n\n3. Sinh 10 số thực ngẫu nhiên [5.0, 15.0]:\n   ");
    for(int i = 0; i < 10; i++) {
        printf("%.2f ", random_double_range(5.0, 15.0));
    }
    
    printf("\n\n4. Trộn mảng [1, 2, 3, 4, 5]:\n   ");
    int arr[] = {1, 2, 3, 4, 5};
    tron_mang(arr, 5);
    for(int i = 0; i < 5; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

// ============================================================================
// PHẦN 8: GIẢI PHƯƠNG TRÌNH
// ============================================================================

// Giải phương trình bậc 1: ax + b = 0
void giai_pt_bac_1(double a, double b) {
    printf("\nGiải phương trình: %.2fx + %.2f = 0\n", a, b);
    
    if(a == 0) {
        if(b == 0) {
            printf("Phương trình vô số nghiệm\n");
        } else {
            printf("Phương trình vô nghiệm\n");
        }
    } else {
        printf("Nghiệm: x = %.4f\n", -b / a);
    }
}

// Giải phương trình bậc 2: ax^2 + bx + c = 0
void giai_pt_bac_2(double a, double b, double c) {
    printf("\nGiải phương trình: %.2fx² + %.2fx + %.2f = 0\n", a, b, c);
    
    if(a == 0) {
        giai_pt_bac_1(b, c);
        return;
    }
    
    double delta = b * b - 4 * a * c;
    printf("Delta = %.4f\n", delta);
    
    if(delta < 0) {
        printf("Phương trình vô nghiệm thực\n");
    } else if(delta == 0) {
        printf("Phương trình có nghiệm kép: x = %.4f\n", -b / (2 * a));
    } else {
        double x1 = (-b + sqrt(delta)) / (2 * a);
        double x2 = (-b - sqrt(delta)) / (2 * a);
        printf("Phương trình có 2 nghiệm phân biệt:\n");
        printf("x1 = %.4f\n", x1);
        printf("x2 = %.4f\n", x2);
    }
}

void phan8_giai_phuong_trinh() {
    printf("\n=== PHẦN 8: GIẢI PHƯƠNG TRÌNH ===\n");
    
    int chon;
    printf("1. Phương trình bậc 1\n");
    printf("2. Phương trình bậc 2\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    if(chon == 1) {
        double a, b;
        printf("Nhập a, b: ");
        scanf("%lf %lf", &a, &b);
        giai_pt_bac_1(a, b);
    } else if(chon == 2) {
        double a, b, c;
        printf("Nhập a, b, c: ");
        scanf("%lf %lf %lf", &a, &b, &c);
        giai_pt_bac_2(a, b, c);
    }
}

// ============================================================================
// PHẦN 9: TÍNH TOÁN HÌNH HỌC
// ============================================================================

void phan9_hinh_hoc() {
    printf("\n=== PHẦN 9: TÍNH TOÁN HÌNH HỌC ===\n");
    
    int chon;
    printf("1. Hình tròn\n");
    printf("2. Hình chữ nhật\n");
    printf("3. Hình tam giác\n");
    printf("4. Hình vuông\n");
    printf("5. Hình thang\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    double r, a, b, c, h;
    
    switch(chon) {
        case 1: // Hình tròn
            printf("Nhập bán kính: ");
            scanf("%lf", &r);
            printf("Chu vi = %.2f\n", 2 * M_PI * r);
            printf("Diện tích = %.2f\n", M_PI * r * r);
            break;
            
        case 2: // Hình chữ nhật
            printf("Nhập chiều dài và chiều rộng: ");
            scanf("%lf %lf", &a, &b);
            printf("Chu vi = %.2f\n", 2 * (a + b));
            printf("Diện tích = %.2f\n", a * b);
            printf("Đường chéo = %.2f\n", sqrt(a * a + b * b));
            break;
            
        case 3: // Tam giác
            printf("Nhập 3 cạnh a, b, c: ");
            scanf("%lf %lf %lf", &a, &b, &c);
            if(a + b > c && b + c > a && c + a > b) {
                double p = (a + b + c) / 2;
                printf("Chu vi = %.2f\n", a + b + c);
                printf("Diện tích (Heron) = %.2f\n", sqrt(p * (p - a) * (p - b) * (p - c)));
            } else {
                printf("Ba cạnh không tạo thành tam giác!\n");
            }
            break;
            
        case 4: // Hình vuông
            printf("Nhập cạnh: ");
            scanf("%lf", &a);
            printf("Chu vi = %.2f\n", 4 * a);
            printf("Diện tích = %.2f\n", a * a);
            printf("Đường chéo = %.2f\n", a * sqrt(2));
            break;
            
        case 5: // Hình thang
            printf("Nhập đáy lớn, đáy nhỏ, chiều cao: ");
            scanf("%lf %lf %lf", &a, &b, &h);
            printf("Diện tích = %.2f\n", (a + b) * h / 2);
            break;
    }
}

// ============================================================================
// PHẦN 10: THỐNG KÊ CƠ BẢN
// ============================================================================

// Tính trung bình cộng
double tinh_trung_binh(double arr[], int n) {
    double sum = 0;
    for(int i = 0; i < n; i++) {
        sum += arr[i];
    }
    return sum / n;
}

// Tìm giá trị lớn nhất
double tim_max_double(double arr[], int n) {
    double max = arr[0];
    for(int i = 1; i < n; i++) {
        if(arr[i] > max) max = arr[i];
    }
    return max;
}

// Tìm giá trị nhỏ nhất
double tim_min_double(double arr[], int n) {
    double min = arr[0];
    for(int i = 1; i < n; i++) {
        if(arr[i] < min) min = arr[i];
    }
    return min;
}

// Tính phương sai
double tinh_phuong_sai(double arr[], int n) {
    double mean = tinh_trung_binh(arr, n);
    double variance = 0;
    for(int i = 0; i < n; i++) {
        variance += pow(arr[i] - mean, 2);
    }
    return variance / n;
}

// Tính độ lệch chuẩn
double tinh_do_lech_chuan(double arr[], int n) {
    return sqrt(tinh_phuong_sai(arr, n));
}

void phan10_thong_ke() {
    printf("\n=== PHẦN 10: THỐNG KÊ CƠ BẢN ===\n");
    
    int n;
    printf("Nhập số lượng phần tử: ");
    scanf("%d", &n);
    
    double arr[100];
    printf("Nhập %d số thực:\n", n);
    for(int i = 0; i < n; i++) {
        printf("arr[%d] = ", i);
        scanf("%lf", &arr[i]);
    }
    
    printf("\nKết quả thống kê:\n");
    printf("- Trung bình: %.4f\n", tinh_trung_binh(arr, n));
    printf("- Giá trị lớn nhất: %.4f\n", tim_max_double(arr, n));
    printf("- Giá trị nhỏ nhất: %.4f\n", tim_min_double(arr, n));
    printf("- Phương sai: %.4f\n", tinh_phuong_sai(arr, n));
    printf("- Độ lệch chuẩn: %.4f\n", tinh_do_lech_chuan(arr, n));
}

// ============================================================================
// PHẦN 11: CHUYỂN ĐỔI HỆ CƠ SỐ
// ============================================================================

// Chuyển từ thập phân sang nhị phân
void dec_to_bin(int n) {
    if(n == 0) {
        printf("0");
        return;
    }
    
    int bin[32], i = 0;
    while(n > 0) {
        bin[i++] = n % 2;
        n /= 2;
    }
    
    for(int j = i - 1; j >= 0; j--) {
        printf("%d", bin[j]);
    }
}

// Chuyển từ thập phân sang bát phân
void dec_to_oct(int n) {
    if(n == 0) {
        printf("0");
        return;
    }
    
    int oct[32], i = 0;
    while(n > 0) {
        oct[i++] = n % 8;
        n /= 8;
    }
    
    for(int j = i - 1; j >= 0; j--) {
        printf("%d", oct[j]);
    }
}

// Chuyển từ thập phân sang thập lục phân
void dec_to_hex(int n) {
    if(n == 0) {
        printf("0");
        return;
    }
    
    char hex[32];
    int i = 0;
    
    while(n > 0) {
        int remainder = n % 16;
        if(remainder < 10) {
            hex[i++] = remainder + '0';
        } else {
            hex[i++] = remainder - 10 + 'A';
        }
        n /= 16;
    }
    
    for(int j = i - 1; j >= 0; j--) {
        printf("%c", hex[j]);
    }
}

// Chuyển từ nhị phân sang thập phân
int bin_to_dec(char bin[]) {
    int dec = 0, base = 1;
    int len = strlen(bin);
    
    for(int i = len - 1; i >= 0; i--) {
        if(bin[i] == '1') {
            dec += base;
        }
        base *= 2;
    }
    return dec;
}

// Chuyển từ bát phân sang thập phân
int oct_to_dec(char oct[]) {
    int dec = 0, base = 1;
    int len = strlen(oct);
    
    for(int i = len - 1; i >= 0; i--) {
        dec += (oct[i] - '0') * base;
        base *= 8;
    }
    return dec;
}

// Chuyển từ thập lục phân sang thập phân
int hex_to_dec(char hex[]) {
    int dec = 0, base = 1;
    int len = strlen(hex);
    
    for(int i = len - 1; i >= 0; i--) {
        if(hex[i] >= '0' && hex[i] <= '9') {
            dec += (hex[i] - '0') * base;
        } else if(hex[i] >= 'A' && hex[i] <= 'F') {
            dec += (hex[i] - 'A' + 10) * base;
        } else if(hex[i] >= 'a' && hex[i] <= 'f') {
            dec += (hex[i] - 'a' + 10) * base;
        }
        base *= 16;
    }
    return dec;
}

void phan11_chuyen_doi_he_co_so() {
    printf("\n=== PHẦN 11: CHUYỂN ĐỔI HỆ CƠ SỐ ===\n");
    
    int chon;
    printf("1. Thập phân -> Nhị phân, Bát phân, Thập lục phân\n");
    printf("2. Nhị phân -> Thập phân\n");
    printf("3. Bát phân -> Thập phân\n");
    printf("4. Thập lục phân -> Thập phân\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    int n;
    char str[100];
    
    switch(chon) {
        case 1:
            printf("Nhập số thập phân: ");
            scanf("%d", &n);
            printf("Nhị phân: ");
            dec_to_bin(n);
            printf("\nBát phân: ");
            dec_to_oct(n);
            printf("\nThập lục phân: ");
            dec_to_hex(n);
            printf("\n");
            break;
            
        case 2:
            printf("Nhập số nhị phân: ");
            scanf("%s", str);
            printf("Thập phân: %d\n", bin_to_dec(str));
            break;
            
        case 3:
            printf("Nhập số bát phân: ");
            scanf("%s", str);
            printf("Thập phân: %d\n", oct_to_dec(str));
            break;
            
        case 4:
            printf("Nhập số thập lục phân: ");
            scanf("%s", str);
            printf("Thập phân: %d\n", hex_to_dec(str));
            break;
    }
}

// ============================================================================
// PHẦN 12: TÍNH TOÁN MA TRẬN NÂNG CAO
// ============================================================================

// Nhân ma trận với số
void nhan_ma_tran_voi_so(double mat[][10], int rows, int cols, double scalar) {
    printf("Ma trận sau khi nhân với %.2f:\n", scalar);
    for(int i = 0; i < rows; i++) {
        for(int j = 0; j < cols; j++) {
            printf("%.2f ", mat[i][j] * scalar);
        }
        printf("\n");
    }
}

// Tính định thức ma trận 2x2
double dinh_thuc_2x2(double mat[2][2]) {
    return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
}

// Tính định thức ma trận 3x3
double dinh_thuc_3x3(double mat[3][3]) {
    return mat[0][0] * (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1])
         - mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0])
         + mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
}

void phan12_ma_tran_nang_cao() {
    printf("\n=== PHẦN 12: TÍNH TOÁN MA TRẬN NÂNG CAO ===\n");
    
    int chon;
    printf("1. Tính định thức ma trận 2x2\n");
    printf("2. Tính định thức ma trận 3x3\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    if(chon == 1) {
        double mat[2][2];
        printf("Nhập ma trận 2x2:\n");
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                printf("mat[%d][%d] = ", i, j);
                scanf("%lf", &mat[i][j]);
            }
        }
        printf("Định thức = %.4f\n", dinh_thuc_2x2(mat));
        
    } else if(chon == 2) {
        double mat[3][3];
        printf("Nhập ma trận 3x3:\n");
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                printf("mat[%d][%d] = ", i, j);
                scanf("%lf", &mat[i][j]);
            }
        }
        printf("Định thức = %.4f\n", dinh_thuc_3x3(mat));
    }
}

// ============================================================================
// PHẦN 13: DÃY SỐ ĐẶC BIỆT
// ============================================================================

// Tính tổng S = 1 + 2 + 3 + ... + n
long long tong_day_so_tu_nhien(int n) {
    return (long long)n * (n + 1) / 2;
}

// Tính tổng S = 1^2 + 2^2 + 3^2 + ... + n^2
long long tong_binh_phuong(int n) {
    return (long long)n * (n + 1) * (2 * n + 1) / 6;
}

// Tính tổng S = 1^3 + 2^3 + 3^3 + ... + n^3
long long tong_lap_phuong(int n) {
    long long sum = n * (n + 1) / 2;
    return sum * sum;
}

// Tính tổng S = 1 + 1/2 + 1/3 + ... + 1/n
double tong_nghich_dao(int n) {
    double sum = 0;
    for(int i = 1; i <= n; i++) {
        sum += 1.0 / i;
    }
    return sum;
}

// Tính tổng S = 1! + 2! + 3! + ... + n!
long long tong_giai_thua(int n) {
    long long sum = 0;
    long long fact = 1;
    for(int i = 1; i <= n; i++) {
        fact *= i;
        sum += fact;
    }
    return sum;
}

// Tính e = 1 + 1/1! + 1/2! + 1/3! + ... (n số hạng)
double tinh_e(int n) {
    double e = 1.0;
    double fact = 1.0;
    for(int i = 1; i <= n; i++) {
        fact *= i;
        e += 1.0 / fact;
    }
    return e;
}

// Tính sin(x) bằng chuỗi Taylor
double tinh_sin(double x, int n) {
    double sin_x = 0;
    double term = x;
    
    for(int i = 0; i < n; i++) {
        sin_x += term;
        term *= -x * x / ((2 * i + 2) * (2 * i + 3));
    }
    return sin_x;
}

// Tính cos(x) bằng chuỗi Taylor
double tinh_cos(double x, int n) {
    double cos_x = 1.0;
    double term = 1.0;
    
    for(int i = 1; i < n; i++) {
        term *= -x * x / ((2 * i - 1) * (2 * i));
        cos_x += term;
    }
    return cos_x;
}

void phan13_day_so_dac_biet() {
    printf("\n=== PHẦN 13: DÃY SỐ ĐẶC BIỆT ===\n");
    
    int n;
    printf("Nhập n: ");
    scanf("%d", &n);
    
    printf("\n1. Tổng 1 + 2 + ... + %d = %lld\n", n, tong_day_so_tu_nhien(n));
    printf("2. Tổng 1² + 2² + ... + %d² = %lld\n", n, tong_binh_phuong(n));
    printf("3. Tổng 1³ + 2³ + ... + %d³ = %lld\n", n, tong_lap_phuong(n));
    printf("4. Tổng 1 + 1/2 + ... + 1/%d = %.6f\n", n, tong_nghich_dao(n));
    printf("5. Tổng 1! + 2! + ... + %d! = %lld\n", n, tong_giai_thua(n));
    printf("6. Tính e với %d số hạng = %.10f\n", n, tinh_e(n));
    printf("   (Giá trị thực của e = %.10f)\n", M_E);
    
    double x = M_PI / 6; // 30 độ
    printf("\n7. Tính sin(30°) với %d số hạng:\n", n);
    printf("   Kết quả = %.10f\n", tinh_sin(x, n));
    printf("   Giá trị thực = %.10f\n", sin(x));
    
    printf("\n8. Tính cos(30°) với %d số hạng:\n", n);
    printf("   Kết quả = %.10f\n", tinh_cos(x, n));
    printf("   Giá trị thực = %.10f\n", cos(x));
}

// ============================================================================
// PHẦN 14: BÀI TOÁN TỐI ƯU HÓA CƠ BẢN
// ============================================================================

// Tìm min/max của hàm bậc 2: f(x) = ax² + bx + c
void tim_cuc_tri_bac_2(double a, double b, double c) {
    if(a == 0) {
        printf("Không phải hàm bậc 2!\n");
        return;
    }
    
    double x = -b / (2 * a);
    double y = a * x * x + b * x + c;
    
    printf("\nHàm số: f(x) = %.2fx² + %.2fx + %.2f\n", a, b, c);
    printf("Đỉnh parabol: (%.4f, %.4f)\n", x, y);
    
    if(a > 0) {
        printf("Hàm số có giá trị nhỏ nhất: %.4f tại x = %.4f\n", y, x);
    } else {
        printf("Hàm số có giá trị lớn nhất: %.4f tại x = %.4f\n", y, x);
    }
}

// Bài toán khoảng cách ngắn nhất giữa 2 điểm
double khoang_cach_2_diem(double x1, double y1, double x2, double y2) {
    return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
}

// Bài toán chu vi và diện tích lớn nhất của hình chữ nhật có chu vi cho trước
void hinh_chu_nhat_dien_tich_max(double chu_vi) {
    // Với chu vi cho trước, diện tích max khi là hình vuông
    double canh = chu_vi / 4;
    double dien_tich = canh * canh;
    
    printf("\nVới chu vi = %.2f\n", chu_vi);
    printf("Diện tích lớn nhất = %.2f (khi là hình vuông cạnh %.2f)\n", dien_tich, canh);
}

void phan14_toi_uu_hoa() {
    printf("\n=== PHẦN 14: BÀI TOÁN TỐI ƯU HÓA ===\n");
    
    int chon;
    printf("1. Tìm cực trị hàm bậc 2\n");
    printf("2. Khoảng cách giữa 2 điểm\n");
    printf("3. Diện tích hình chữ nhật lớn nhất với chu vi cho trước\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    double a, b, c, x1, y1, x2, y2;
    
    switch(chon) {
        case 1:
            printf("Nhập a, b, c: ");
            scanf("%lf %lf %lf", &a, &b, &c);
            tim_cuc_tri_bac_2(a, b, c);
            break;
            
        case 2:
            printf("Nhập tọa độ điểm 1 (x1 y1): ");
            scanf("%lf %lf", &x1, &y1);
            printf("Nhập tọa độ điểm 2 (x2 y2): ");
            scanf("%lf %lf", &x2, &y2);
            printf("Khoảng cách = %.4f\n", khoang_cach_2_diem(x1, y1, x2, y2));
            break;
            
        case 3:
            printf("Nhập chu vi: ");
            scanf("%lf", &a);
            hinh_chu_nhat_dien_tich_max(a);
            break;
    }
}

// ============================================================================
// PHẦN 15: THUẬT TOÁN SỐ HỌC NÂNG CAO
// ============================================================================

// Thuật toán Euclid mở rộng (tìm x, y sao cho ax + by = gcd(a,b))
int gcd_extended(int a, int b, int *x, int *y) {
    if(b == 0) {
        *x = 1;
        *y = 0;
        return a;
    }
    
    int x1, y1;
    int gcd = gcd_extended(b, a % b, &x1, &y1);
    
    *x = y1;
    *y = x1 - (a / b) * y1;
    
    return gcd;
}

// Tính lũy thừa modulo: (base^exp) % mod
long long luy_thua_modulo(long long base, long long exp, long long mod) {
    long long result = 1;
    base %= mod;
    
    while(exp > 0) {
        if(exp % 2 == 1) {
            result = (result * base) % mod;
        }
        base = (base * base) % mod;
        exp /= 2;
    }
    return result;
}

// Kiểm tra số nguyên tố bằng Miller-Rabin (nâng cao)
bool kiem_tra_nguyen_to_nang_cao(long long n, int k) {
    if(n <= 1) return false;
    if(n <= 3) return true;
    if(n % 2 == 0) return false;
    
    // Viết n-1 dưới dạng 2^r * d
    long long d = n - 1;
    while(d % 2 == 0) {
        d /= 2;
    }
    
    // Thực hiện k lần test
    srand(time(NULL));
    for(int i = 0; i < k; i++) {
        long long a = 2 + rand() % (n - 3);
        long long x = luy_thua_modulo(a, d, n);
        
        if(x == 1 || x == n - 1) continue;
        
        bool composite = true;
        long long temp = d;
        while(temp != n - 1) {
            x = (x * x) % n;
            temp *= 2;
            
            if(x == n - 1) {
                composite = false;
                break;
            }
        }
        
        if(composite) return false;
    }
    return true;
}

void phan15_thuat_toan_nang_cao() {
    printf("\n=== PHẦN 15: THUẬT TOÁN SỐ HỌC NÂNG CAO ===\n");
    
    int chon;
    printf("1. Thuật toán Euclid mở rộng\n");
    printf("2. Lũy thừa modulo\n");
    printf("3. Kiểm tra số nguyên tố nâng cao (Miller-Rabin)\n");
    printf("Chọn: ");
    scanf("%d", &chon);
    
    int a, b, x, y;
    long long base, exp, mod, n;
    
    switch(chon) {
        case 1:
            printf("Nhập a, b: ");
            scanf("%d %d", &a, &b);
            int g = gcd_extended(a, b, &x, &y);
            printf("GCD(%d, %d) = %d\n", a, b, g);
            printf("Phương trình: %d*(%d) + %d*(%d) = %d\n", a, x, b, y, g);
            break;
            
        case 2:
            printf("Nhập base, exp, mod: ");
            scanf("%lld %lld %lld", &base, &exp, &mod);
            printf("(%lld^%lld) mod %lld = %lld\n", base, exp, mod, 
                   luy_thua_modulo(base, exp, mod));
            break;
            
        case 3:
            printf("Nhập số cần kiểm tra: ");
            scanf("%lld", &n);
            if(kiem_tra_nguyen_to_nang_cao(n, 5)) {
                printf("%lld có thể là số nguyên tố\n", n);
            } else {
                printf("%lld không phải số nguyên tố\n", n);
            }
            break;
    }
}

// ============================================================================
// HÀM MAIN - MENU CHƯƠNG TRÌNH
// ============================================================================

int main() {
    int chon;
    
    do {
        printf("\n");
        printf("╔═══════════════════════════════════════════════════════════╗\n");
        printf("║        CHƯƠNG TRÌNH HÀM TOÁN HỌC TRONG C                 ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  1.  Hàm toán học cơ bản (sqrt, pow, log, ceil...)       ║\n");
        printf("║  2.  Hàm lượng giác (sin, cos, tan...)                   ║\n");
        printf("║  3.  Số nguyên tố                                        ║\n");
        printf("║  4.  Ước và bội (GCD, LCM)                               ║\n");
        printf("║  5.  Giai thừa, tổ hợp, Fibonacci                        ║\n");
        printf("║  6.  Kiểm tra các loại số đặc biệt                       ║\n");
        printf("║  7.  Số ngẫu nhiên                                       ║\n");
        printf("║  8.  Giải phương trình                                   ║\n");
        printf("║  9.  Tính toán hình học                                  ║\n");
        printf("║  10. Thống kê cơ bản                                     ║\n");
        printf("║  11. Chuyển đổi hệ cơ số                                 ║\n");
        printf("║  12. Ma trận nâng cao                                    ║\n");
        printf("║  13. Dãy số đặc biệt                                     ║\n");
        printf("║  14. Bài toán tối ưu hóa                                 ║\n");
        printf("║  15. Thuật toán số học nâng cao                          ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  0.  THOÁT                                               ║\n");
        printf("╚═══════════════════════════════════════════════════════════╝\n");
        printf("Nhập lựa chọn: ");
        scanf("%d", &chon);
        
        switch(chon) {
            case 1: phan1_ham_co_ban(); break;
            case 2: phan2_ham_luong_giac(); break;
            case 3: phan3_so_nguyen_to(); break;
            case 4: phan4_uoc_boi(); break;
            case 5: phan5_giai_thua_to_hop(); break;
            case 6: phan6_so_dac_biet(); break;
            case 7: phan7_so_ngau_nhien(); break;
            case 8: phan8_giai_phuong_trinh(); break;
            case 9: phan9_hinh_hoc(); break;
            case 10: phan10_thong_ke(); break;
            case 11: phan11_chuyen_doi_he_co_so(); break;
            case 12: phan12_ma_tran_nang_cao(); break;
            case 13: phan13_day_so_dac_biet(); break;
            case 14: phan14_toi_uu_hoa(); break;
            case 15: phan15_thuat_toan_nang_cao(); break;
            case 0:
                printf("\n=== CẢM ƠN BẠN ĐÃ SỬ DỤNG CHƯƠNG TRÌNH ===\n");
                break;
            default:
                printf("\nLựa chọn không hợp lệ!\n");
        }
        
        if(chon != 0) {
            printf("\nNhấn Enter để tiếp tục...");
            while(getchar() != '\n');
            getchar();
        }
        
    } while(chon != 0);
    
    return 0;
}