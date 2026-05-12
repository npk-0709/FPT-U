#include <stdio.h>

int main() {
    double a, b, c;
    printf("-> a, b, c: ");
    scanf("%lf %lf %lf", &a, &b, &c);
    
    if (a <= 0 || b <= 0 || c <= 0 || a + b <= c || a + c <= b || b + c <= a) {
        printf("khong phai tam giac.\n");
        return 0;
    }
    
    if (a == b && b == c) {
        printf("tam giac deu.\n");
    } else if (a == b || b == c || a == c) {
        printf("tam giac can.\n");
    } else {
        printf("tam giac thuong.\n");
    }
    
    return 0;
}