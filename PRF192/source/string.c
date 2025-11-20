#include <stdio.h>
#include <string.h>
#include <ctype.h>

char* lTrim(char s[]) {
    int i = 0;
    while (s[i] == ' ') {
        i++;
    }
    int j = 0;
    while (s[i] != '\0') {
        s[j] = s[i];
        i++;
        j++;
    }
    s[j] = '\0';
    return s;
}

char* rTrim(char s[]) {
    int len = strlen(s);
    while (len > 0 && s[len - 1] == ' ') {
        len--;
    }
    s[len] = '\0';
    return s;
}

char* trim(char s[]) {
    int i = 0, j = 0;
    int spaceCount = 0;
    while (s[i] == ' ') {
        i++;
    }
    while (s[i] != '\0') {
        if (s[i] == ' ') {
            if (spaceCount == 0) {
                s[j++] = ' ';
            }
            spaceCount++;
        } else {
            s[j++] = s[i];
            spaceCount = 0;
        }
        i++;
    }
    if (j > 0 && s[j - 1] == ' ') {
        j--;
    }
    s[j] = '\0';
    return s;
}

char* nameStr(char s[]) {
    int i = 0;
    int newWord = 1; 
    trim(s);
    i = 0;
    newWord = 1;
    while (s[i] != '\0') {
        if (s[i] == ' ') {
            newWord = 1;
        } else {
            if (newWord) {
                s[i] = toupper(s[i]);
                newWord = 0;
            } else {
                s[i] = tolower(s[i]);
            }
        }
        i++;
    }
    
    return s;
}

int main() {
    char str1[100] = "   Hello";
    char str2[100] = "Hello   ";
    char str3[100] = "  I   am   student  ";
    char str4[100] = " hoang  thi   hoa  ";
    
    printf("=== Test lTrim ===\n");
    printf("Before: \"%s\"\n", str1);
    lTrim(str1);
    printf("After:  \"%s\"\n\n", str1);
    
    printf("=== Test rTrim ===\n");
    printf("Before: \"%s\"\n", str2);
    rTrim(str2);
    printf("After:  \"%s\"\n\n", str2);
    
    printf("=== Test trim ===\n");
    printf("Before: \"%s\"\n", str3);
    trim(str3);
    printf("After:  \"%s\"\n\n", str3);
    
    printf("=== Test nameStr ===\n");
    printf("Before: \"%s\"\n", str4);
    nameStr(str4);
    printf("After:  \"%s\"\n\n", str4);
    
    return 0;
}