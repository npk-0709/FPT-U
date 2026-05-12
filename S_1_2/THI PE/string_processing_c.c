#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

// ============================================================================
// PHẦN 1: KHAI BÁO VÀ NHẬP XUẤT CHUỖI CƠ BẢN
// ============================================================================

void phan1_khai_bao_co_ban() {
    printf("\n=== PHẦN 1: KHAI BÁO VÀ KHỞI TẠO CHUỖI ===\n");
    
    // Cách 1: Khai báo mảng ký tự với kích thước cố định
    char str1[50] = "Hello World";
    
    // Cách 2: Khai báo và khởi tạo từng ký tự (phải có '\0' ở cuối)
    char str2[] = {'H', 'e', 'l', 'l', 'o', '\0'};
    
    // Cách 3: Sử dụng con trỏ (chuỗi hằng, không thể sửa đổi)
    char *str3 = "Hello C";
    
    // Cách 4: Khai báo mảng rỗng
    char str4[100];
    
    printf("str1: %s\n", str1);
    printf("str2: %s\n", str2);
    printf("str3: %s\n", str3);
    
    // QUAN TRỌNG: Chuỗi trong C luôn kết thúc bằng ký tự '\0' (null terminator)
    printf("\nLưu ý: Ký tự cuối cùng của chuỗi là '\\0' (có mã ASCII = 0)\n");
}

void phan2_nhap_xuat_chuoi() {
    printf("\n=== PHẦN 2: NHẬP XUẤT CHUỖI ===\n");
    
    char str1[100], str2[100], str3[100];
    
    // Cách 1: Dùng scanf (chỉ nhập 1 từ, dừng khi gặp khoảng trắng)
    printf("Nhập một từ (scanf): ");
    scanf("%s", str1); // Không cần dấu & vì str1 đã là địa chỉ
    printf("Bạn vừa nhập: %s\n\n", str1);
    
    // Xóa bộ đệm
    while(getchar() != '\n');
    
    // Cách 2: Dùng gets (KHÔNG AN TOÀN - không nên dùng)
    // gets(str); // Hàm này đã bị loại bỏ vì nguy hiểm
    
    // Cách 3: Dùng fgets (AN TOÀN, nên dùng)
    printf("Nhập một câu (fgets): ");
    fgets(str2, sizeof(str2), stdin);
    // Xóa ký tự '\n' ở cuối nếu có
    str2[strcspn(str2, "\n")] = '\0';
    printf("Bạn vừa nhập: %s\n\n", str2);
    
    // Cách 4: Dùng scanf với format đặc biệt
    printf("Nhập câu (scanf với %%[^\\n]): ");
    scanf(" %[^\n]", str3); // Đọc đến khi gặp Enter
    printf("Bạn vừa nhập: %s\n", str3);
}

// ============================================================================
// PHẦN 3: CÁC HÀM XỬ LÝ CHUỖI CƠ BẢN (string.h)
// ============================================================================

void phan3_ham_thu_vien_co_ban() {
    printf("\n=== PHẦN 3: HÀM THƯ VIỆN CƠ BẢN ===\n");
    
    char str1[100] = "Hello";
    char str2[100] = "World";
    char str3[100];
    
    // 1. strlen() - Đếm độ dài chuỗi (không tính '\0')
    printf("Độ dài của '%s': %lu\n", str1, strlen(str1));
    
    // 2. strcpy() - Sao chép chuỗi
    strcpy(str3, str1);
    printf("Sau khi copy: str3 = %s\n", str3);
    
    // 3. strncpy() - Sao chép n ký tự đầu
    strncpy(str3, str2, 3);
    str3[3] = '\0'; // Phải thêm '\0' thủ công
    printf("Copy 3 ký tự đầu: str3 = %s\n", str3);
    
    // 4. strcat() - Nối chuỗi
    strcpy(str3, str1);
    strcat(str3, " ");
    strcat(str3, str2);
    printf("Nối chuỗi: %s\n", str3);
    
    // 5. strncat() - Nối n ký tự
    strcpy(str3, str1);
    strncat(str3, str2, 3);
    printf("Nối 3 ký tự: %s\n", str3);
    
    // 6. strcmp() - So sánh chuỗi (trả về 0 nếu bằng nhau)
    printf("\nSo sánh '%s' và '%s': %d\n", str1, str2, strcmp(str1, str2));
    printf("So sánh '%s' và '%s': %d\n", str1, str1, strcmp(str1, str1));
    
    // 7. strncmp() - So sánh n ký tự đầu
    printf("So sánh 2 ký tự đầu 'Hello' và 'Help': %d\n", 
           strncmp("Hello", "Help", 2));
    
    // 8. strchr() - Tìm ký tự đầu tiên
    char *pos = strchr(str1, 'l');
    if(pos != NULL) {
        printf("Tìm 'l' trong '%s' tại vị trí: %ld\n", str1, pos - str1);
    }
    
    // 9. strrchr() - Tìm ký tự cuối cùng
    pos = strrchr(str1, 'l');
    if(pos != NULL) {
        printf("Tìm 'l' cuối cùng trong '%s' tại vị trí: %ld\n", str1, pos - str1);
    }
    
    // 10. strstr() - Tìm chuỗi con
    char str4[] = "Hello World Programming";
    pos = strstr(str4, "World");
    if(pos != NULL) {
        printf("Tìm 'World' trong '%s' tại vị trí: %ld\n", str4, pos - str4);
    }
}

// ============================================================================
// PHẦN 4: XỬ LÝ KÝ TỰ (ctype.h)
// ============================================================================

void phan4_xu_ly_ky_tu() {
    printf("\n=== PHẦN 4: XỬ LÝ KÝ TỰ ===\n");
    
    char ch;
    
    printf("Nhập một ký tự: ");
    scanf(" %c", &ch);
    
    // Kiểm tra loại ký tự
    printf("\nKiểm tra ký tự '%c':\n", ch);
    printf("- Là chữ cái: %s\n", isalpha(ch) ? "Có" : "Không");
    printf("- Là chữ số: %s\n", isdigit(ch) ? "Có" : "Không");
    printf("- Là chữ cái hoặc số: %s\n", isalnum(ch) ? "Có" : "Không");
    printf("- Là chữ hoa: %s\n", isupper(ch) ? "Có" : "Không");
    printf("- Là chữ thường: %s\n", islower(ch) ? "Có" : "Không");
    printf("- Là khoảng trắng: %s\n", isspace(ch) ? "Có" : "Không");
    
    // Chuyển đổi
    printf("\nChuyển đổi:\n");
    printf("- Chuyển thành chữ hoa: %c\n", toupper(ch));
    printf("- Chuyển thành chữ thường: %c\n", tolower(ch));
}

// ============================================================================
// PHẦN 5: VIẾT HÀM XỬ LÝ CHUỖI TỰ TẠO
// ============================================================================

// Hàm đếm độ dài chuỗi (tự viết)
int my_strlen(char str[]) {
    int len = 0;
    while(str[len] != '\0') {
        len++;
    }
    return len;
}

// Hàm sao chép chuỗi (tự viết)
void my_strcpy(char dest[], char src[]) {
    int i = 0;
    while(src[i] != '\0') {
        dest[i] = src[i];
        i++;
    }
    dest[i] = '\0'; // Thêm ký tự kết thúc
}

// Hàm nối chuỗi (tự viết)
void my_strcat(char dest[], char src[]) {
    int i = 0, j = 0;
    
    // Tìm vị trí cuối của dest
    while(dest[i] != '\0') {
        i++;
    }
    
    // Nối src vào dest
    while(src[j] != '\0') {
        dest[i] = src[j];
        i++;
        j++;
    }
    dest[i] = '\0';
}

// Hàm so sánh chuỗi (tự viết)
int my_strcmp(char str1[], char str2[]) {
    int i = 0;
    while(str1[i] != '\0' && str2[i] != '\0') {
        if(str1[i] != str2[i]) {
            return str1[i] - str2[i];
        }
        i++;
    }
    return str1[i] - str2[i];
}

void phan5_ham_tu_viet() {
    printf("\n=== PHẦN 5: HÀM TỰ VIẾT ===\n");
    
    char str1[100] = "Hello";
    char str2[100] = "World";
    char str3[100];
    
    printf("Độ dài '%s': %d\n", str1, my_strlen(str1));
    
    my_strcpy(str3, str1);
    printf("Sau khi copy: %s\n", str3);
    
    my_strcat(str3, " ");
    my_strcat(str3, str2);
    printf("Sau khi nối: %s\n", str3);
    
    printf("So sánh '%s' và '%s': %d\n", str1, str2, my_strcmp(str1, str2));
}

// ============================================================================
// PHẦN 6: CHUYỂN ĐỔI CHỮ HOA, CHỮ THƯỜNG
// ============================================================================

// Chuyển toàn bộ chuỗi thành chữ hoa
void chuyen_chu_hoa(char str[]) {
    for(int i = 0; str[i] != '\0'; i++) {
        str[i] = toupper(str[i]);
    }
}

// Chuyển toàn bộ chuỗi thành chữ thường
void chuyen_chu_thuong(char str[]) {
    for(int i = 0; str[i] != '\0'; i++) {
        str[i] = tolower(str[i]);
    }
}

// Viết hoa chữ cái đầu mỗi từ
void viet_hoa_chu_cai_dau(char str[]) {
    int viet_hoa = 1; // Cờ đánh dấu cần viết hoa
    
    for(int i = 0; str[i] != '\0'; i++) {
        if(isspace(str[i])) {
            viet_hoa = 1;
        } else if(viet_hoa && isalpha(str[i])) {
            str[i] = toupper(str[i]);
            viet_hoa = 0;
        } else {
            str[i] = tolower(str[i]);
        }
    }
}

// Đảo chữ hoa thành thường và ngược lại
void dao_chu_hoa_thuong(char str[]) {
    for(int i = 0; str[i] != '\0'; i++) {
        if(isupper(str[i])) {
            str[i] = tolower(str[i]);
        } else if(islower(str[i])) {
            str[i] = toupper(str[i]);
        }
    }
}

void phan6_chuyen_doi_chu() {
    printf("\n=== PHẦN 6: CHUYỂN ĐỔI CHỮ HOA, THƯỜNG ===\n");
    
    char str[100];
    printf("Nhập chuỗi: ");
    fgets(str, sizeof(str), stdin);
    str[strcspn(str, "\n")] = '\0';
    
    char str_copy[100];
    
    strcpy(str_copy, str);
    chuyen_chu_hoa(str_copy);
    printf("Chữ hoa: %s\n", str_copy);
    
    strcpy(str_copy, str);
    chuyen_chu_thuong(str_copy);
    printf("Chữ thường: %s\n", str_copy);
    
    strcpy(str_copy, str);
    viet_hoa_chu_cai_dau(str_copy);
    printf("Viết hoa chữ đầu: %s\n", str_copy);
    
    strcpy(str_copy, str);
    dao_chu_hoa_thuong(str_copy);
    printf("Đảo chữ hoa/thường: %s\n", str_copy);
}

// ============================================================================
// PHẦN 7: ĐẾM VÀ THỐNG KÊ
// ============================================================================

// Đếm số ký tự chữ cái
int dem_chu_cai(char str[]) {
    int count = 0;
    for(int i = 0; str[i] != '\0'; i++) {
        if(isalpha(str[i])) {
            count++;
        }
    }
    return count;
}

// Đếm số ký tự chữ số
int dem_chu_so(char str[]) {
    int count = 0;
    for(int i = 0; str[i] != '\0'; i++) {
        if(isdigit(str[i])) {
            count++;
        }
    }
    return count;
}

// Đếm số khoảng trắng
int dem_khoang_trang(char str[]) {
    int count = 0;
    for(int i = 0; str[i] != '\0'; i++) {
        if(isspace(str[i])) {
            count++;
        }
    }
    return count;
}

// Đếm số từ
int dem_so_tu(char str[]) {
    int count = 0;
    int trong_tu = 0;
    
    for(int i = 0; str[i] != '\0'; i++) {
        if(isspace(str[i])) {
            trong_tu = 0;
        } else if(trong_tu == 0) {
            trong_tu = 1;
            count++;
        }
    }
    return count;
}

// Đếm số lần xuất hiện của ký tự
int dem_ky_tu(char str[], char ch) {
    int count = 0;
    for(int i = 0; str[i] != '\0'; i++) {
        if(str[i] == ch) {
            count++;
        }
    }
    return count;
}

// Đếm nguyên âm và phụ âm
void dem_nguyen_am_phu_am(char str[], int *nguyen_am, int *phu_am) {
    *nguyen_am = 0;
    *phu_am = 0;
    
    for(int i = 0; str[i] != '\0'; i++) {
        char ch = tolower(str[i]);
        if(isalpha(ch)) {
            if(ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                (*nguyen_am)++;
            } else {
                (*phu_am)++;
            }
        }
    }
}

void phan7_dem_thong_ke() {
    printf("\n=== PHẦN 7: ĐẾM VÀ THỐNG KÊ ===\n");
    
    char str[200];
    printf("Nhập chuỗi: ");
    fgets(str, sizeof(str), stdin);
    str[strcspn(str, "\n")] = '\0';
    
    printf("\nThống kê:\n");
    printf("- Độ dài chuỗi: %lu\n", strlen(str));
    printf("- Số chữ cái: %d\n", dem_chu_cai(str));
    printf("- Số chữ số: %d\n", dem_chu_so(str));
    printf("- Số khoảng trắng: %d\n", dem_khoang_trang(str));
    printf("- Số từ: %d\n", dem_so_tu(str));
    
    int nguyen_am, phu_am;
    dem_nguyen_am_phu_am(str, &nguyen_am, &phu_am);
    printf("- Số nguyên âm: %d\n", nguyen_am);
    printf("- Số phụ âm: %d\n", phu_am);
    
    char ch;
    printf("\nNhập ký tự cần đếm: ");
    scanf(" %c", &ch);
    printf("Ký tự '%c' xuất hiện %d lần\n", ch, dem_ky_tu(str, ch));
}

// ============================================================================
// PHẦN 8: TÌM KIẾM TRONG CHUỖI
// ============================================================================

// Tìm vị trí xuất hiện đầu tiên của ký tự
int tim_ky_tu_dau(char str[], char ch) {
    for(int i = 0; str[i] != '\0'; i++) {
        if(str[i] == ch) {
            return i;
        }
    }
    return -1; // Không tìm thấy
}

// Tìm vị trí xuất hiện cuối cùng của ký tự
int tim_ky_tu_cuoi(char str[], char ch) {
    int pos = -1;
    for(int i = 0; str[i] != '\0'; i++) {
        if(str[i] == ch) {
            pos = i;
        }
    }
    return pos;
}

// Tìm tất cả vị trí của ký tự
void tim_tat_ca_vi_tri(char str[], char ch) {
    printf("Các vị trí của '%c': ", ch);
    int found = 0;
    for(int i = 0; str[i] != '\0'; i++) {
        if(str[i] == ch) {
            printf("%d ", i);
            found = 1;
        }
    }
    if(!found) {
        printf("Không tìm thấy");
    }
    printf("\n");
}

// Tìm chuỗi con
int tim_chuoi_con(char str[], char sub[]) {
    int len_str = strlen(str);
    int len_sub = strlen(sub);
    
    for(int i = 0; i <= len_str - len_sub; i++) {
        int j;
        for(j = 0; j < len_sub; j++) {
            if(str[i + j] != sub[j]) {
                break;
            }
        }
        if(j == len_sub) {
            return i; // Tìm thấy
        }
    }
    return -1; // Không tìm thấy
}

void phan8_tim_kiem() {
    printf("\n=== PHẦN 8: TÌM KIẾM TRONG CHUỖI ===\n");
    
    char str[200];
    printf("Nhập chuỗi: ");
    fgets(str, sizeof(str), stdin);
    str[strcspn(str, "\n")] = '\0';
    
    char ch;
    printf("Nhập ký tự cần tìm: ");
    scanf(" %c", &ch);
    
    int pos = tim_ky_tu_dau(str, ch);
    if(pos != -1) {
        printf("Vị trí đầu tiên: %d\n", pos);
    } else {
        printf("Không tìm thấy ký tự '%c'\n", ch);
    }
    
    pos = tim_ky_tu_cuoi(str, ch);
    if(pos != -1) {
        printf("Vị trí cuối cùng: %d\n", pos);
    }
    
    tim_tat_ca_vi_tri(str, ch);
    
    while(getchar() != '\n');
    char sub[100];
    printf("\nNhập chuỗi con cần tìm: ");
    fgets(sub, sizeof(sub), stdin);
    sub[strcspn(sub, "\n")] = '\0';
    
    pos = tim_chuoi_con(str, sub);
    if(pos != -1) {
        printf("Tìm thấy '%s' tại vị trí: %d\n", sub, pos);
    } else {
        printf("Không tìm thấy '%s'\n", sub);
    }
}

// ============================================================================
// PHẦN 9: XÓA VÀ CHÈN
// ============================================================================

// Xóa ký tự tại vị trí k
void xoa_ky_tu(char str[], int k) {
    int len = strlen(str);
    if(k < 0 || k >= len) {
        printf("Vị trí không hợp lệ!\n");
        return;
    }
    
    for(int i = k; i < len; i++) {
        str[i] = str[i + 1];
    }
}

// Chèn ký tự vào vị trí k
void chen_ky_tu(char str[], char ch, int k) {
    int len = strlen(str);
    if(k < 0 || k > len) {
        printf("Vị trí không hợp lệ!\n");
        return;
    }
    
    // Dịch các ký tự về phía sau
    for(int i = len; i >= k; i--) {
        str[i + 1] = str[i];
    }
    str[k] = ch;
}

// Xóa tất cả ký tự ch trong chuỗi
void xoa_tat_ca_ky_tu(char str[], char ch) {
    int i = 0, j = 0;
    while(str[i] != '\0') {
        if(str[i] != ch) {
            str[j] = str[i];
            j++;
        }
        i++;
    }
    str[j] = '\0';
}

// Xóa khoảng trắng thừa
void xoa_khoang_trang_thua(char str[]) {
    int i = 0, j = 0;
    int space_flag = 0;
    
    // Bỏ khoảng trắng đầu
    while(isspace(str[i])) {
        i++;
    }
    
    // Xử lý giữa chuỗi
    while(str[i] != '\0') {
        if(isspace(str[i])) {
            if(!space_flag) {
                str[j++] = ' ';
                space_flag = 1;
            }
        } else {
            str[j++] = str[i];
            space_flag = 0;
        }
        i++;
    }
    
    // Xóa khoảng trắng cuối
    if(j > 0 && str[j - 1] == ' ') {
        j--;
    }
    
    str[j] = '\0';
}

void phan9_xoa_chen() {
    printf("\n=== PHẦN 9: XÓA VÀ CHÈN ===\n");
    
    char str[200];
    printf("Nhập chuỗi: ");
    fgets(str, sizeof(str), stdin);
    str[strcspn(str, "\n")] = '\0';
    
    printf("Chuỗi gốc: '%s'\n", str);
    
    // Test xóa ký tự tại vị trí
    char str_copy[200];
    strcpy(str_copy, str);
    int pos;
    printf("\nNhập vị trí cần xóa: ");
    scanf("%d", &pos);
    xoa_ky_tu(str_copy, pos);
    printf("Sau khi xóa vị trí %d: '%s'\n", pos, str_copy);
    
    // Test chèn ký tự
    strcpy(str_copy, str);
    char ch;
    printf("\nNhập ký tự cần chèn: ");
    scanf(" %c", &ch);
    printf("Nhập vị trí chèn: ");
    scanf("%d", &pos);
    chen_ky_tu(str_copy, ch, pos);
    printf("Sau khi chèn '%c' vào vị trí %d: '%s'\n", ch, pos, str_copy);
    
    // Test xóa tất cả ký tự
    strcpy(str_copy, str);
    printf("\nNhập ký tự cần xóa hết: ");
    scanf(" %c", &ch);
    xoa_tat_ca_ky_tu(str_copy, ch);
    printf("Sau khi xóa tất cả '%c': '%s'\n", ch, str_copy);
    
    // Test xóa khoảng trắng thừa
    strcpy(str_copy, str);
    xoa_khoang_trang_thua(str_copy);
    printf("\nSau khi xóa khoảng trắng thừa: '%s'\n", str_copy);
}

// ============================================================================
// PHẦN 10: ĐẢO NGƯỢC VÀ HOÁN VỊ
// ============================================================================

// Đảo ngược toàn bộ chuỗi
void dao_nguoc_chuoi(char str[]) {
    int len = strlen(str);
    for(int i = 0; i < len / 2; i++) {
        char temp = str[i];
        str[i] = str[len - 1 - i];
        str[len - 1 - i] = temp;
    }
}

// Đảo ngược từng từ trong chuỗi
void dao_nguoc_tung_tu(char str[]) {
    int start = 0;
    int len = strlen(str);
    
    for(int i = 0; i <= len; i++) {
        if(str[i] == ' ' || str[i] == '\0') {
            // Đảo ngược từ hiện tại
            int end = i - 1;
            while(start < end) {
                char temp = str[start];
                str[start] = str[end];
                str[end] = temp;
                start++;
                end--;
            }
            start = i + 1;
        }
    }
}

// Kiểm tra chuỗi đối xứng (palindrome)
int kiem_tra_doi_xung(char str[]) {
    int len = strlen(str);
    for(int i = 0; i < len / 2; i++) {
        if(str[i] != str[len - 1 - i]) {
            return 0; // Không đối xứng
        }
    }
    return 1; // Đối xứng
}

void phan10_dao_nguoc() {
    printf("\n=== PHẦN 10: ĐẢO NGƯỢC VÀ ĐỐI XỨNG ===\n");
    
    char str[200], str_copy[200];
    printf("Nhập chuỗi: ");
    fgets(str, sizeof(str), stdin);
    str[strcspn(str, "\n")] = '\0';
    
    printf("Chuỗi gốc: '%s'\n", str);
    
    strcpy(str_copy, str);
    dao_nguoc_chuoi(str_copy);
    printf("Đảo ngược toàn bộ: '%s'\n", str_copy);
    
    strcpy(str_copy, str);
    dao_nguoc_tung_tu(str_copy);