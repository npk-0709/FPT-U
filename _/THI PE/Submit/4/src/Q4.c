#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>
char* lTrim(char* s);
char* rTrim(char* s);
char* trim(char* s);
//----------------------------------------------------
char* capitalizeConsonants(char s[]) {
    for(int i = 0; s[i] != '\0'; i++) {
    	if (isalpha(s[i])){
	    	if (s[i] != 'a' && s[i] != 'o' && s[i] != 'e' && s[i] != 'u' && s[i] != 'i'){
	    		s[i] = toupper(s[i]);
			}	
		}
    }
    
    return s;
    //End your statements
}

//========DO NOT ADD NEW OR CHANGE THE STATEMENTS IN THE MAIN FUNCTION========
int main() {
    system("cls");
    printf("\nTEST Q4 (3 marks):\n");
    char s[50];
    char *str;
   	fflush(stdin);
	printf("Please enter a string: ");
	scanf("%50[^\n]", s);
    trim(s);     
    printf("\nOUTPUT:\n");
    str = capitalizeConsonants(s);
    printf("%s\n", str);
    system("pause");
    return 0;
}

//========//==================================================================


//=== Do not add new or change statements in this function.===
char* lTrim(char* s) {
	int i = 0;
	while (s[i] == ' ')	i++;
	if (i > 0) strcpy(&s[0], &s[i]);
	return s;
}
//=== Do not add new or change statements in this function.===
char* rTrim(char* s) {
	int i = strlen(s)-1;
	while (s[i] == ' ') i--;
	s[i+1] = '\0';
	return s;
}
//=== Do not add new or change statements in this function.===
char* trim(char* s) {
	rTrim(lTrim(s));
	char *ptr = strstr(s, "  ");
	while (ptr != NULL) {
		strcpy(ptr, ptr+1);
		ptr = strstr(s, "  ");
	}
	return s;
}

