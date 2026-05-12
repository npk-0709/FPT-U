#include <stdio.h>

#define ENTER 10

int main() {
    char c;
    int noDigits, noLetters, noOther;
    noDigits = noLetters = noOther = 0;
    
    printf("Enter string: ");
    while ((c = getchar()) != ENTER) {
        if (c >= '0' && c <= '9') {
            noDigits++;
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            noLetters++;
        } else {
            noOther++;
        }
    }
    
    printf("digit: %d\n", noDigits);
    printf("letter: %d\n", noLetters);
    printf("other: %d\n", noOther);
    getchar();
    return 0;
}