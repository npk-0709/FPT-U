#include <stdio.h>
#include <stdlib.h>

int fact(int n) {
	if (n == 0 || n == 1) return 1;
	return n * fact(n - 1);
}
double calculateS(int n);
//--------------------------------------------------------------------------------
//Students can create new functions here if needed
//
double calculateS(int n) {
	double sum = 0;
	//Begin your codes here
	
	for (int i = 0; i <= n; i++) {
		sum += (1.0 / fact(i));
	}
	
	//End your codes
	return sum;
}
//=============DO NOT ADD NEW OR CHANGE STATEMENTS IN THE MAIN FUNCTION============
int main() {
	system("cls");
	printf("\nTEST Q5 (2 marks):\n");
	int n;
	double sum;
	do {
		printf("Enter n = ");
		scanf("%d",&n);
	} while(n<0);
	printf("\nOUTPUT:\n");
	sum = calculateS(n);
	printf("%.2lf",sum);
	printf("\n");
	system ("pause");
	return(0);
}
//==================================================================================
