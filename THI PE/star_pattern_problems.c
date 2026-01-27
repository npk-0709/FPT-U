#include <stdio.h>
#include <stdlib.h>

// ============================================================================
// PHẦN 1: HÌNH CHỮ NHẬT VÀ HÌNH VUÔNG
// ============================================================================

// Bài 1: In hình chữ nhật đặc
void in_hinh_chu_nhat_dac(int dong, int cot) {
    printf("\n=== HÌNH CHỮ NHẬT ĐẶC %dx%d ===\n", dong, cot);
    /*
        Ví dụ 4x6:
        * * * * * *
        * * * * * *
        * * * * * *
        * * * * * *
    */
    for(int i = 1; i <= dong; i++) {
        for(int j = 1; j <= cot; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 2: In hình chữ nhật rỗng
void in_hinh_chu_nhat_rong(int dong, int cot) {
    printf("\n=== HÌNH CHỮ NHẬT RỖNG %dx%d ===\n", dong, cot);
    /*
        Ví dụ 5x8:
        * * * * * * * *
        *             *
        *             *
        *             *
        * * * * * * * *
    */
    for(int i = 1; i <= dong; i++) {
        for(int j = 1; j <= cot; j++) {
            // In * ở viền (hàng đầu, hàng cuối, cột đầu, cột cuối)
            if(i == 1 || i == dong || j == 1 || j == cot) {
                printf("* ");
            } else {
                printf("  "); // Khoảng trắng bên trong
            }
        }
        printf("\n");
    }
}

// Bài 3: In hình vuông đặc
void in_hinh_vuong_dac(int n) {
    printf("\n=== HÌNH VUÔNG ĐẶC %dx%d ===\n", n, n);
    in_hinh_chu_nhat_dac(n, n);
}

// Bài 4: In hình vuông rỗng
void in_hinh_vuong_rong(int n) {
    printf("\n=== HÌNH VUÔNG RỖNG %dx%d ===\n", n, n);
    in_hinh_chu_nhat_rong(n, n);
}

// ============================================================================
// PHẦN 2: TAM GIÁC VUÔNG
// ============================================================================

// Bài 5: Tam giác vuông góc trên bên trái
void tam_giac_vuong_1(int n) {
    printf("\n=== TAM GIÁC VUÔNG 1 (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        *
        * *
        * * *
        * * * *
        * * * * *
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 6: Tam giác vuông góc dưới bên trái
void tam_giac_vuong_2(int n) {
    printf("\n=== TAM GIÁC VUÔNG 2 (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
        * * * *
        * * *
        * *
        *
    */
    for(int i = n; i >= 1; i--) {
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 7: Tam giác vuông góc trên bên phải
void tam_giac_vuong_3(int n) {
    printf("\n=== TAM GIÁC VUÔNG 3 (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
                *
              * *
            * * *
          * * * *
        * * * * *
    */
    for(int i = 1; i <= n; i++) {
        // In khoảng trắng
        for(int j = 1; j <= n - i; j++) {
            printf("  ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 8: Tam giác vuông góc dưới bên phải
void tam_giac_vuong_4(int n) {
    printf("\n=== TAM GIÁC VUÔNG 4 (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
          * * * *
            * * *
              * *
                *
    */
    for(int i = n; i >= 1; i--) {
        // In khoảng trắng
        for(int j = 1; j <= n - i; j++) {
            printf("  ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// ============================================================================
// PHẦN 3: TAM GIÁC CÂN
// ============================================================================

// Bài 9: Tam giác cân đặc
void tam_giac_can_dac(int n) {
    printf("\n=== TAM GIÁC CÂN ĐẶC (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
            *
           * *
          * * *
         * * * *
        * * * * *
    */
    for(int i = 1; i <= n; i++) {
        // In khoảng trắng
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 10: Tam giác cân rỗng
void tam_giac_can_rong(int n) {
    printf("\n=== TAM GIÁC CÂN RỖNG (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
            *
           * *
          *   *
         *     *
        * * * * *
    */
    for(int i = 1; i <= n; i++) {
        // In khoảng trắng đầu
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i || i == n) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// Bài 11: Tam giác cân ngược
void tam_giac_can_nguoc(int n) {
    printf("\n=== TAM GIÁC CÂN NGƯỢC (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
         * * * *
          * * *
           * *
            *
    */
    for(int i = n; i >= 1; i--) {
        // In khoảng trắng
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 12: Tam giác cân ngược rỗng
void tam_giac_can_nguoc_rong(int n) {
    printf("\n=== TAM GIÁC CÂN NGƯỢC RỖNG (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
         *     *
          *   *
           * *
            *
    */
    for(int i = n; i >= 1; i--) {
        // In khoảng trắng đầu
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i || i == n) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// ============================================================================
// PHẦN 4: HÌNH THOI
// ============================================================================

// Bài 13: Hình thoi đặc
void hinh_thoi_dac(int n) {
    printf("\n=== HÌNH THOI ĐẶC (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
            *
           * *
          * * *
         * * * *
        * * * * *
         * * * *
          * * *
           * *
            *
    */
    // Nửa trên (tam giác cân)
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
    
    // Nửa dưới (tam giác cân ngược)
    for(int i = n - 1; i >= 1; i--) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 14: Hình thoi rỗng
void hinh_thoi_rong(int n) {
    printf("\n=== HÌNH THOI RỖNG (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
            *
           * *
          *   *
         *     *
        *       *
         *     *
          *   *
           * *
            *
    */
    // Nửa trên
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
    
    // Nửa dưới
    for(int i = n - 1; i >= 1; i--) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// ============================================================================
// PHẦN 5: CÁC HÌNH NÂNG CAO
// ============================================================================

// Bài 15: Hình cát (Tam giác ngược + Tam giác xuôi)
void hinh_cat(int n) {
    printf("\n=== HÌNH CÁT (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
         * * * *
          * * *
           * *
            *
           * *
          * * *
         * * * *
        * * * * *
    */
    // Nửa trên (tam giác ngược)
    for(int i = n; i >= 1; i--) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
    
    // Nửa dưới (tam giác xuôi, bỏ hàng đầu)
    for(int i = 2; i <= n; i++) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 16: Hình số (In số thay vì *)
void hinh_so_tam_giac(int n) {
    printf("\n=== TAM GIÁC SỐ (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        1
        1 2
        1 2 3
        1 2 3 4
        1 2 3 4 5
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= i; j++) {
            printf("%d ", j);
        }
        printf("\n");
    }
}

// Bài 17: Tam giác Pascal đơn giản
void tam_giac_pascal_don_gian(int n) {
    printf("\n=== TAM GIÁC PASCAL ĐƠN GIẢN (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
            1
           1 1
          1 2 1
         1 3 3 1
        1 4 6 4 1
    */
    for(int i = 0; i < n; i++) {
        int val = 1;
        // In khoảng trắng
        for(int j = 0; j < n - i - 1; j++) {
            printf(" ");
        }
        // In giá trị
        for(int j = 0; j <= i; j++) {
            printf("%d ", val);
            val = val * (i - j) / (j + 1);
        }
        printf("\n");
    }
}

// Bài 18: Hình chữ X
void hinh_chu_X(int n) {
    printf("\n=== HÌNH CHỮ X (n=%d) ===\n", n);
    /*
        Ví dụ n=7:
        *           *
          *       *
            *   *
              *
            *   *
          *       *
        *           *
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n; j++) {
            // In * ở đường chéo chính và đường chéo phụ
            if(j == i || j == n - i + 1) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// Bài 19: Hình chữ Z
void hinh_chu_Z(int n) {
    printf("\n=== HÌNH CHỮ Z (n=%d) ===\n", n);
    /*
        Ví dụ n=7:
        * * * * * * *
              *
            *
          *
        *
        * * * * * * *
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n; j++) {
            // In * ở hàng đầu, hàng cuối, hoặc đường chéo phụ
            if(i == 1 || i == n || j == n - i + 1) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// Bài 20: Tam giác số đặc biệt
void tam_giac_so_dac_biet(int n) {
    printf("\n=== TAM GIÁC SỐ ĐẶC BIỆT (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        1
        2 2
        3 3 3
        4 4 4 4
        5 5 5 5 5
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= i; j++) {
            printf("%d ", i);
        }
        printf("\n");
    }
}

// Bài 21: Hình chữ nhật số
void hinh_chu_nhat_so(int dong, int cot) {
    printf("\n=== HÌNH CHỮ NHẬT SỐ %dx%d ===\n", dong, cot);
    /*
        Ví dụ 4x6:
        1 2 3 4 5 6
        1 2 3 4 5 6
        1 2 3 4 5 6
        1 2 3 4 5 6
    */
    for(int i = 1; i <= dong; i++) {
        for(int j = 1; j <= cot; j++) {
            printf("%d ", j);
        }
        printf("\n");
    }
}

// Bài 22: Tam giác có viền
void tam_giac_co_vien(int n) {
    printf("\n=== TAM GIÁC CÓ VIỀN (n=%d) ===\n", n);
    /*
        Ví dụ n=7:
            *
           * *
          *   *
         *     *
        *       *
         *     *
          *   *
           * *
            *
    */
    // Nửa trên
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
    
    // Nửa dưới
    for(int i = n - 1; i >= 1; i--) {
        for(int j = 1; j <= n - i; j++) {
            printf(" ");
        }
        for(int j = 1; j <= i; j++) {
            if(j == 1 || j == i) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// Bài 23: Hình vuông có đường chéo
void hinh_vuong_duong_cheo(int n) {
    printf("\n=== HÌNH VUÔNG CÓ ĐƯỜNG CHÉO (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        * * * * *
        * *   * *
        *   *   *
        * *   * *
        * * * * *
    */
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= n; j++) {
            // In * ở viền hoặc đường chéo
            if(i == 1 || i == n || j == 1 || j == n || i == j || j == n - i + 1) {
                printf("* ");
            } else {
                printf("  ");
            }
        }
        printf("\n");
    }
}

// Bài 24: Hình bậc thang
void hinh_bac_thang(int n) {
    printf("\n=== HÌNH BẬC THANG (n=%d) ===\n", n);
    /*
        Ví dụ n=4:
            *
          * *
        * * *
        * * * *
    */
    for(int i = 1; i <= n; i++) {
        // In khoảng trắng
        for(int j = 1; j <= (n - i) * 2; j++) {
            printf(" ");
        }
        // In dấu *
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// Bài 25: Hình nửa thoi
void hinh_nua_thoi(int n) {
    printf("\n=== HÌNH NỬA THOI (n=%d) ===\n", n);
    /*
        Ví dụ n=5:
        *
        * *
        * * *
        * * * *
        * * * * *
        * * * *
        * * *
        * *
        *
    */
    // Nửa trên
    for(int i = 1; i <= n; i++) {
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
    
    // Nửa dưới
    for(int i = n - 1; i >= 1; i--) {
        for(int j = 1; j <= i; j++) {
            printf("* ");
        }
        printf("\n");
    }
}

// ============================================================================
// HÀM MAIN - MENU CHƯƠNG TRÌNH
// ============================================================================

int main() {
    int chon, n, dong, cot;
    
    do {
        printf("\n");
        printf("╔═══════════════════════════════════════════════════════════╗\n");
        printf("║        CHƯƠNG TRÌNH IN HÌNH BẰNG DẤU * VÀ SỐ            ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  PHẦN 1: HÌNH CHỮ NHẬT VÀ VUÔNG                          ║\n");
        printf("║  1.  Hình chữ nhật đặc                                   ║\n");
        printf("║  2.  Hình chữ nhật rỗng                                  ║\n");
        printf("║  3.  Hình vuông đặc                                      ║\n");
        printf("║  4.  Hình vuông rỗng                                     ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  PHẦN 2: TAM GIÁC VUÔNG                                  ║\n");
        printf("║  5.  Tam giác vuông góc trên trái                        ║\n");
        printf("║  6.  Tam giác vuông góc dưới trái                        ║\n");
        printf("║  7.  Tam giác vuông góc trên phải                        ║\n");
        printf("║  8.  Tam giác vuông góc dưới phải                        ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  PHẦN 3: TAM GIÁC CÂN                                    ║\n");
        printf("║  9.  Tam giác cân đặc                                    ║\n");
        printf("║  10. Tam giác cân rỗng                                   ║\n");
        printf("║  11. Tam giác cân ngược đặc                              ║\n");
        printf("║  12. Tam giác cân ngược rỗng                             ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  PHẦN 4: HÌNH THOI                                       ║\n");
        printf("║  13. Hình thoi đặc                                       ║\n");
        printf("║  14. Hình thoi rỗng                                      ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  PHẦN 5: CÁC HÌNH NÂNG CAO                               ║\n");
        printf("║  15. Hình cát                                            ║\n");
        printf("║  16. Tam giác số                                         ║\n");
        printf("║  17. Tam giác Pascal                                     ║\n");
        printf("║  18. Hình chữ X                                          ║\n");
        printf("║  19. Hình chữ Z                                          ║\n");
        printf("║  20. Tam giác số đặc biệt                                ║\n");
        printf("║  21. Hình chữ nhật số                                    ║\n");
        printf("║  22. Tam giác có viền                                    ║\n");
        printf("║  23. Hình vuông có đường chéo                            ║\n");
        printf("║  24. Hình bậc thang                                      ║\n");
        printf("║  25. Hình nửa thoi                                       ║\n");
        printf("╠═══════════════════════════════════════════════════════════╣\n");
        printf("║  0.  THOÁT                                               ║\n");
        printf("╚═══════════════════════════════════════════════════════════╝\n");
        printf("Nhập lựa chọn: ");
        scanf("%d", &chon);
        
        switch(chon) {
            case 1:
                printf("Nhập số dòng và cột: ");
                scanf("%d %d", &dong, &cot);
                in_hinh_chu_nhat_dac(dong, cot);
                break;
            case 2:
                printf("Nhập số dòng và cột: ");
                scanf("%d %d", &dong, &cot);
                in_hinh_chu_nhat_rong(dong, cot);
                break;
            case 3:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                in_hinh_vuong_dac(n);
                break;
            case 4:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                in_hinh_vuong_rong(n);
                break;
            case 5:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_vuong_1(n);
                break;
            case 6:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_vuong_2(n);
                break;
            case 7:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_vuong_3(n);
                break;
            case 8:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_vuong_4(n);
                break;
            case 9:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_can_dac(n);
                break;
            case 10:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_can_rong(n);
                break;
            case 11:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_can_nguoc(n);
                break;
            case 12:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_can_nguoc_rong(n);
                break;
            case 13:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                hinh_thoi_dac(n);
                break;
            case 14:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                hinh_thoi_rong(n);
                break;
            case 15:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                hinh_cat(n);
                break;
            case 16:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                hinh_so_tam_giac(n);
                break;
            case 17:
                printf("Nhập số dòng: ");
                scanf("%d", &n);
                tam_giac_pascal_don_gian(n);
                break;
            case 18:
                printf("Nhập kích thước (nên là số lẻ): ");
                scanf("%d", &n);
                hinh_chu_X(n);
                break;
            case 19:
                printf("Nhập kích thước: ");
                scanf("%d", &n);
                hinh_chu_Z(n);
                break;
            case 20:
                printf("Nhập chiều cao: ");
                scanf("%d", &n);
                tam_giac_so_dac_biet(n);
                break;
            case 21:
                printf("Nhập số dòng và cột: ");
                scanf("%d %d", &dong, &cot);
                hinh_chu_nhat_so(dong, cot);
                break;
            case 22:
                printf("Nhập kích thước: ");
                scanf