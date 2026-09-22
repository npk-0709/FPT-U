#include <stdio.h>

int main() {
    double a, b, c;
    printf("Nhap ba he so a, b, c: ");
    scanf("%lf %lf %lf", &a, &b, &c);
    
    if (a == 0) {
        if (b == 0) {
            if (c == 0) {
                printf("Phuong trinh vo so nghiem.\n");
            } else {
                printf("Phuong trinh vo nghiem.\n");
            }
        } else {
            double x = -c / b;
            printf("Nghiem: x = %.2f\n", x);
        }
        return 0;
    }
    
    double delta = b * b - 4 * a * c;
    if (delta > 0) {
        double x1 = (-b + (delta*delta)) / (2 * a);
        double x2 = (-b - (delta*delta)) / (2 * a);
        printf("Hai nghiem thuc: x1 = %.2f, x2 = %.2f\n", x1, x2);
    } else if (delta == 0) {
        double x = -b / (2 * a);
        printf("Nghiem kep: x = %.2f\n", x);
    } else {
        double real = -b / (2 * a);
        double imag = sqrt(-delta) / (2 * a);
        printf("Hai nghiem phuc: x1 = %.2f + %.2fi, x2 = %.2f - %.2fi\n", real, imag, real, imag);
    }
    
    return 0;
}