#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// ============================================================================
// PHẦN 1: MẢNG 1 CHIỀU CỞ BẢN
// ============================================================================

void phan1_khai_bao_co_ban() {
    printf("\n=== PHẦN 1: KHAI BÁO VÀ KHỞI TẠO MẢNG 1 CHIỀU ===\n");
    
    // Cách 1: Khai báo và khởi tạo từng phần tử
    int arr1[5];
    arr1[0] = 10;
    arr1[1] = 20;
    arr1[2] = 30;
    arr1[3] = 40;
    arr1[4] = 50;
    
    // Cách 2: Khai báo và khởi tạo cùng lúc
    int arr2[5] = {1, 2, 3, 4, 5};
    
    // Cách 3: Khởi tạo một phần (phần còn lại tự động = 0)
    int arr3[5] = {1, 2}; // arr3 = {1, 2, 0, 0, 0}
    
    // Cách 4: Tự động xác định kích thước
    int arr4[] = {10, 20, 30, 40}; // Tự động có 4 phần tử
    
    // In mảng arr2
    printf("Mảng arr2: ");
    for(int i = 0; i < 5; i++) {
        printf("%d ", arr2[i]);
    }
    printf("\n");
}

// ============================================================================
// PHẦN 2: NHẬP XUẤT MẢNG 1 CHIỀU
// ============================================================================

void nhap_mang_1chieu(int arr[], int n) {
    printf("Nhập %d phần tử:\n", n);
    for(int i = 0; i < n; i++) {
        printf("arr[%d] = ", i);
        scanf("%d", &arr[i]);
    }
}

void xuat_mang_1chieu(int arr[], int n) {
    printf("Mảng: ");
    for(int i = 0; i < n; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}

void phan2_nhap_xuat() {
    printf("\n=== PHẦN 2: NHẬP XUẤT MẢNG ===\n");
    int n;
    printf("Nhập số phần tử: ");
    scanf("%d", &n);
    
    int arr[100]; // Khai báo mảng tối đa 100 phần tử
    nhap_mang_1chieu(arr, n);
    xuat_mang_1chieu(arr, n);
}

// ============================================================================
// PHẦN 3: CÁC THAO TÁC CƠ BẢN VỚI MẢNG 1 CHIỀU
// ============================================================================

// Tìm phần tử lớn nhất
int tim_max(int arr[], int n) {
    int max = arr[0];
    for(int i = 1; i < n; i++) {
        if(arr[i] > max) {
            max = arr[i];
        }
    }
    return max;
}

// Tìm phần tử nhỏ nhất
int tim_min(int arr[], int n) {
    int min = arr[0];
    for(int i = 1; i < n; i++) {
        if(arr[i] < min) {
            min = arr[i];
        }
    }
    return min;
}

// Tính tổng các phần tử
int tinh_tong(int arr[], int n) {
    int tong = 0;
    for(int i = 0; i < n; i++) {
        tong += arr[i];
    }
    return tong;
}

// Tính trung bình cộng
float tinh_trung_binh(int arr[], int n) {
    return (float)tinh_tong(arr, n) / n;
}

// Đếm số phần tử chẵn
int dem_so_chan(int arr[], int n) {
    int dem = 0;
    for(int i = 0; i < n; i++) {
        if(arr[i] % 2 == 0) {
            dem++;
        }
    }
    return dem;
}

// Tìm kiếm tuyến tính
int tim_kiem_tuyen_tinh(int arr[], int n, int x) {
    for(int i = 0; i < n; i++) {
        if(arr[i] == x) {
            return i; // Trả về vị trí tìm thấy
        }
    }
    return -1; // Không tìm thấy
}

void phan3_thao_tac_co_ban() {
    printf("\n=== PHẦN 3: CÁC THAO TÁC CƠ BẢN ===\n");
    int arr[] = {5, 2, 8, 1, 9, 3, 7};
    int n = 7;
    
    xuat_mang_1chieu(arr, n);
    printf("Phần tử lớn nhất: %d\n", tim_max(arr, n));
    printf("Phần tử nhỏ nhất: %d\n", tim_min(arr, n));
    printf("Tổng các phần tử: %d\n", tinh_tong(arr, n));
    printf("Trung bình cộng: %.2f\n", tinh_trung_binh(arr, n));
    printf("Số phần tử chẵn: %d\n", dem_so_chan(arr, n));
    
    int x = 8;
    int vi_tri = tim_kiem_tuyen_tinh(arr, n, x);
    if(vi_tri != -1) {
        printf("Tìm thấy %d tại vị trí %d\n", x, vi_tri);
    } else {
        printf("Không tìm thấy %d\n", x);
    }
}

// ============================================================================
// PHẦN 4: SẮP XẾP MẢNG
// ============================================================================

// Sắp xếp nổi bọt (Bubble Sort)
void bubble_sort(int arr[], int n) {
    for(int i = 0; i < n - 1; i++) {
        for(int j = 0; j < n - i - 1; j++) {
            if(arr[j] > arr[j + 1]) {
                // Hoán đổi arr[j] và arr[j+1]
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

// Sắp xếp chọn (Selection Sort)
void selection_sort(int arr[], int n) {
    for(int i = 0; i < n - 1; i++) {
        int min_idx = i;
        for(int j = i + 1; j < n; j++) {
            if(arr[j] < arr[min_idx]) {
                min_idx = j;
            }
        }
        // Hoán đổi arr[i] và arr[min_idx]
        int temp = arr[i];
        arr[i] = arr[min_idx];
        arr[min_idx] = temp;
    }
}

// Sắp xếp chèn (Insertion Sort)
void insertion_sort(int arr[], int n) {
    for(int i = 1; i < n; i++) {
        int key = arr[i];
        int j = i - 1;
        
        // Di chuyển các phần tử lớn hơn key về phía sau
        while(j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}

void phan4_sap_xep() {
    printf("\n=== PHẦN 4: SẮP XẾP MẢNG ===\n");
    
    int arr1[] = {64, 34, 25, 12, 22, 11, 90};
    int n = 7;
    
    printf("Mảng gốc: ");
    xuat_mang_1chieu(arr1, n);
    
    // Tạo bản sao để test các thuật toán khác nhau
    int arr2[7], arr3[7];
    memcpy(arr2, arr1, sizeof(arr1));
    memcpy(arr3, arr1, sizeof(arr1));
    
    bubble_sort(arr1, n);
    printf("Sau Bubble Sort: ");
    xuat_mang_1chieu(arr1, n);
    
    selection_sort(arr2, n);
    printf("Sau Selection Sort: ");
    xuat_mang_1chieu(arr2, n);
    
    insertion_sort(arr3, n);
    printf("Sau Insertion Sort: ");
    xuat_mang_1chieu(arr3, n);
}

// ============================================================================
// PHẦN 5: THAO TÁC NÂNG CAO VỚI MẢNG 1 CHIỀU
// ============================================================================

// Tìm kiếm nhị phân (yêu cầu mảng đã sắp xếp)
int tim_kiem_nhi_phan(int arr[], int n, int x) {
    int left = 0, right = n - 1;
    
    while(left <= right) {
        int mid = left + (right - left) / 2;
        
        if(arr[mid] == x) {
            return mid;
        }
        
        if(arr[mid] < x) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;
}

// Xóa phần tử tại vị trí k
void xoa_phan_tu(int arr[], int *n, int k) {
    if(k < 0 || k >= *n) {
        printf("Vị trí không hợp lệ!\n");
        return;
    }
    
    // Dịch các phần tử về phía trước
    for(int i = k; i < *n - 1; i++) {
        arr[i] = arr[i + 1];
    }
    (*n)--; // Giảm kích thước mảng
}

// Chèn phần tử x vào vị trí k
void chen_phan_tu(int arr[], int *n, int k, int x) {
    if(k < 0 || k > *n) {
        printf("Vị trí không hợp lệ!\n");
        return;
    }
    
    // Dịch các phần tử về phía sau
    for(int i = *n; i > k; i--) {
        arr[i] = arr[i - 1];
    }
    arr[k] = x;
    (*n)++; // Tăng kích thước mảng
}

// Đảo ngược mảng
void dao_nguoc_mang(int arr[], int n) {
    int left = 0, right = n - 1;
    while(left < right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
    }
}

// Loại bỏ phần tử trùng lặp (mảng đã sắp xếp)
int loai_bo_trung_lap(int arr[], int n) {
    if(n == 0 || n == 1) return n;
    
    int j = 0;
    for(int i = 0; i < n - 1; i++) {
        if(arr[i] != arr[i + 1]) {
            arr[j++] = arr[i];
        }
    }
    arr[j++] = arr[n - 1];
    return j;
}

void phan5_thao_tac_nang_cao() {
    printf("\n=== PHẦN 5: THAO TÁC NÂNG CAO ===\n");
    
    // Tìm kiếm nhị phân
    int arr1[] = {1, 3, 5, 7, 9, 11, 13};
    int n1 = 7;
    printf("Mảng đã sắp xếp: ");
    xuat_mang_1chieu(arr1, n1);
    int x = 7;
    int pos = tim_kiem_nhi_phan(arr1, n1, x);
    printf("Tìm %d bằng tìm kiếm nhị phân: vị trí %d\n", x, pos);
    
    // Xóa phần tử
    int arr2[] = {10, 20, 30, 40, 50};
    int n2 = 5;
    printf("\nMảng trước khi xóa: ");
    xuat_mang_1chieu(arr2, n2);
    xoa_phan_tu(arr2, &n2, 2);
    printf("Sau khi xóa phần tử vị trí 2: ");
    xuat_mang_1chieu(arr2, n2);
    
    // Chèn phần tử
    int arr3[10] = {10, 20, 30, 40, 50};
    int n3 = 5;
    printf("\nMảng trước khi chèn: ");
    xuat_mang_1chieu(arr3, n3);
    chen_phan_tu(arr3, &n3, 2, 25);
    printf("Sau khi chèn 25 vào vị trí 2: ");
    xuat_mang_1chieu(arr3, n3);
    
    // Đảo ngược mảng
    int arr4[] = {1, 2, 3, 4, 5};
    int n4 = 5;
    printf("\nMảng trước khi đảo: ");
    xuat_mang_1chieu(arr4, n4);
    dao_nguoc_mang(arr4, n4);
    printf("Sau khi đảo ngược: ");
    xuat_mang_1chieu(arr4, n4);
}

// ============================================================================
// PHẦN 6: MẢNG 2 CHIỀU CƠ BẢN
// ============================================================================

void nhap_mang_2chieu(int arr[][100], int dong, int cot) {
    printf("Nhập mảng %d x %d:\n", dong, cot);
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            printf("arr[%d][%d] = ", i, j);
            scanf("%d", &arr[i][j]);
        }
    }
}

void xuat_mang_2chieu(int arr[][100], int dong, int cot) {
    printf("Mảng 2 chiều:\n");
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            printf("%4d ", arr[i][j]);
        }
        printf("\n");
    }
}

void phan6_mang_2chieu_co_ban() {
    printf("\n=== PHẦN 6: MẢNG 2 CHIỀU CƠ BẢN ===\n");
    
    // Cách 1: Khai báo và khởi tạo
    int ma_tran1[3][3] = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };
    
    // Cách 2: Khởi tạo liên tiếp
    int ma_tran2[2][4] = {1, 2, 3, 4, 5, 6, 7, 8};
    
    printf("Ma trận 3x3:\n");
    xuat_mang_2chieu(ma_tran1, 3, 3);
    
    printf("\nMa trận 2x4:\n");
    xuat_mang_2chieu(ma_tran2, 2, 4);
}

// ============================================================================
// PHẦN 7: THAO TÁC VỚI MA TRẬN
// ============================================================================

// Tính tổng các phần tử trong ma trận
int tong_ma_tran(int arr[][100], int dong, int cot) {
    int tong = 0;
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            tong += arr[i][j];
        }
    }
    return tong;
}

// Tìm phần tử lớn nhất trong ma trận
int max_ma_tran(int arr[][100], int dong, int cot) {
    int max = arr[0][0];
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            if(arr[i][j] > max) {
                max = arr[i][j];
            }
        }
    }
    return max;
}

// Tính tổng từng hàng
void tong_tung_hang(int arr[][100], int dong, int cot) {
    printf("Tổng từng hàng:\n");
    for(int i = 0; i < dong; i++) {
        int tong = 0;
        for(int j = 0; j < cot; j++) {
            tong += arr[i][j];
        }
        printf("Hàng %d: %d\n", i, tong);
    }
}

// Tính tổng từng cột
void tong_tung_cot(int arr[][100], int dong, int cot) {
    printf("Tổng từng cột:\n");
    for(int j = 0; j < cot; j++) {
        int tong = 0;
        for(int i = 0; i < dong; i++) {
            tong += arr[i][j];
        }
        printf("Cột %d: %d\n", j, tong);
    }
}

// Tính tổng đường chéo chính (ma trận vuông)
int tong_duong_cheo_chinh(int arr[][100], int n) {
    int tong = 0;
    for(int i = 0; i < n; i++) {
        tong += arr[i][i];
    }
    return tong;
}

// Tính tổng đường chéo phụ (ma trận vuông)
int tong_duong_cheo_phu(int arr[][100], int n) {
    int tong = 0;
    for(int i = 0; i < n; i++) {
        tong += arr[i][n - 1 - i];
    }
    return tong;
}

void phan7_thao_tac_ma_tran() {
    printf("\n=== PHẦN 7: THAO TÁC VỚI MA TRẬN ===\n");
    
    int ma_tran[100][100] = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12}
    };
    int dong = 3, cot = 4;
    
    xuat_mang_2chieu(ma_tran, dong, cot);
    printf("\nTổng tất cả phần tử: %d\n", tong_ma_tran(ma_tran, dong, cot));
    printf("Phần tử lớn nhất: %d\n\n", max_ma_tran(ma_tran, dong, cot));
    
    tong_tung_hang(ma_tran, dong, cot);
    printf("\n");
    tong_tung_cot(ma_tran, dong, cot);
    
    // Test với ma trận vuông
    int ma_tran_vuong[100][100] = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };
    printf("\nMa trận vuông 3x3:\n");
    xuat_mang_2chieu(ma_tran_vuong, 3, 3);
    printf("Tổng đường chéo chính: %d\n", tong_duong_cheo_chinh(ma_tran_vuong, 3));
    printf("Tổng đường chéo phụ: %d\n", tong_duong_cheo_phu(ma_tran_vuong, 3));
}

// ============================================================================
// PHẦN 8: PHÉP TOÁN MA TRẬN NÂNG CAO
// ============================================================================

// Cộng hai ma trận
void cong_ma_tran(int a[][100], int b[][100], int c[][100], int dong, int cot) {
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            c[i][j] = a[i][j] + b[i][j];
        }
    }
}

// Nhân hai ma trận
void nhan_ma_tran(int a[][100], int b[][100], int c[][100], 
                  int dong_a, int cot_a, int cot_b) {
    // Khởi tạo ma trận kết quả = 0
    for(int i = 0; i < dong_a; i++) {
        for(int j = 0; j < cot_b; j++) {
            c[i][j] = 0;
        }
    }
    
    // Nhân ma trận
    for(int i = 0; i < dong_a; i++) {
        for(int j = 0; j < cot_b; j++) {
            for(int k = 0; k < cot_a; k++) {
                c[i][j] += a[i][k] * b[k][j];
            }
        }
    }
}

// Chuyển vị ma trận
void chuyen_vi_ma_tran(int a[][100], int b[][100], int dong, int cot) {
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            b[j][i] = a[i][j];
        }
    }
}

// Kiểm tra ma trận đối xứng
int kiem_tra_doi_xung(int arr[][100], int n) {
    for(int i = 0; i < n; i++) {
        for(int j = 0; j < n; j++) {
            if(arr[i][j] != arr[j][i]) {
                return 0; // Không đối xứng
            }
        }
    }
    return 1; // Đối xứng
}

void phan8_phep_toan_ma_tran() {
    printf("\n=== PHẦN 8: PHÉP TOÁN MA TRẬN NÂNG CAO ===\n");
    
    // Cộng ma trận
    int a[100][100] = {{1, 2}, {3, 4}};
    int b[100][100] = {{5, 6}, {7, 8}};
    int c[100][100];
    
    printf("Ma trận A:\n");
    xuat_mang_2chieu(a, 2, 2);
    printf("\nMa trận B:\n");
    xuat_mang_2chieu(b, 2, 2);
    
    cong_ma_tran(a, b, c, 2, 2);
    printf("\nA + B:\n");
    xuat_mang_2chieu(c, 2, 2);
    
    // Nhân ma trận
    int d[100][100] = {{1, 2, 3}, {4, 5, 6}};
    int e[100][100] = {{7, 8}, {9, 10}, {11, 12}};
    int f[100][100];
    
    printf("\nMa trận D (2x3):\n");
    xuat_mang_2chieu(d, 2, 3);
    printf("\nMa trận E (3x2):\n");
    xuat_mang_2chieu(e, 3, 2);
    
    nhan_ma_tran(d, e, f, 2, 3, 2);
    printf("\nD x E (2x2):\n");
    xuat_mang_2chieu(f, 2, 2);
    
    // Chuyển vị
    int g[100][100];
    chuyen_vi_ma_tran(d, g, 2, 3);
    printf("\nChuyển vị của D (3x2):\n");
    xuat_mang_2chieu(g, 3, 2);
    
    // Kiểm tra đối xứng
    int h[100][100] = {{1, 2, 3}, {2, 4, 5}, {3, 5, 6}};
    printf("\nMa trận H:\n");
    xuat_mang_2chieu(h, 3, 3);
    if(kiem_tra_doi_xung(h, 3)) {
        printf("Ma trận H là ma trận đối xứng\n");
    } else {
        printf("Ma trận H không đối xứng\n");
    }
}

// ============================================================================
// PHẦN 9: CẤP PHÁT ĐỘNG (DYNAMIC ALLOCATION)
// ============================================================================

void phan9_cap_phat_dong() {
    printf("\n=== PHẦN 9: CẤP PHÁT ĐỘNG ===\n");
    
    int n;
    printf("Nhập số phần tử: ");
    scanf("%d", &n);
    
    // Cấp phát bộ nhớ động cho mảng 1 chiều
    int *arr = (int*)malloc(n * sizeof(int));
    
    if(arr == NULL) {
        printf("Không đủ bộ nhớ!\n");
        return;
    }
    
    // Nhập mảng
    printf("Nhập %d phần tử:\n", n);
    for(int i = 0; i < n; i++) {
        printf("arr[%d] = ", i);
        scanf("%d", &arr[i]);
    }
    
    // Xuất mảng
    printf("Mảng vừa nhập: ");
    for(int i = 0; i < n; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
    
    // Giải phóng bộ nhớ
    free(arr);
    
    // Cấp phát cho mảng 2 chiều
    int dong, cot;
    printf("\nNhập số dòng và cột (dòng cột): ");
    scanf("%d %d", &dong, &cot);
    
    // Cấp phát mảng con trỏ
    int **matrix = (int**)malloc(dong * sizeof(int*));
    for(int i = 0; i < dong; i++) {
        matrix[i] = (int*)malloc(cot * sizeof(int));
    }
    
    // Nhập ma trận
    printf("Nhập ma trận %dx%d:\n", dong, cot);
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            printf("matrix[%d][%d] = ", i, j);
            scanf("%d", &matrix[i][j]);
        }
    }
    
    // Xuất ma trận
    printf("Ma trận vừa nhập:\n");
    for(int i = 0; i < dong; i++) {
        for(int j = 0; j < cot; j++) {
            printf("%4d ", matrix[i][j]);
        }
        printf("\n");
    }
    
    // Giải phóng bộ nhớ
    for(int i = 0; i < dong; i++) {
        free(matrix[i]);
    }
    free(matrix);
}

// ============================================================================
// HÀM MAIN - MENU CHƯƠNG TRÌNH
// ============================================================================

int main() {
    int chon;
    
    do {
        printf("\n");
        printf("╔════════════════════════════════════════════════════════╗\n");
        printf("║     CHƯƠNG TRÌNH HƯỚNG DẪN MẢNG TRONG C               ║\n");
        printf("╠════════════════════════════════════════════════════════╣\n");
        printf("║  1. Khai báo và khởi tạo mảng 1 chiều                 ║\n");
        printf("║  2. Nhập xuất mảng 1 chiều                            ║\n");
        printf("║  3. Các thao tác cơ bản với mảng 1 chiều              ║\n");
        printf("║  4. Sắp xếp mảng                                      ║\n");
        printf("║  5. Thao tác nâng cao với mảng 1 chiều                ║\n");