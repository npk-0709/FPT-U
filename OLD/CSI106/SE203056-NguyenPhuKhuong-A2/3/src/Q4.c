#include <stdio.h>
#include <stdlib.h>
#include <math.h>
double calculateS(int n);
//--------------------------------------------------------------------------------
double calculateS(int n) {
	double sum = 0;
	//Begin your codes here

	for (int i = 1; i <= n; i++) {
		sum += (float)i / (float)sqrt(i*i*i);
	}

	//End your codes here	
	return sum;
}
//=============DO NOT ADD NEW OR CHANGE STATEMENTS IN THE MAIN FUNCTION============
int main() {
	system("cls");
	printf("\nTEST Q4 (2 marks):\n");
	int n;
	double sum;
	do {
		printf("Enter n = ");
		scanf("%d",&n);
	} while(n<=0);
	printf("\nOUTPUT:\n");
	sum = calculateS(n);
	printf("%.2lf",sum);
	printf("\n");
	system ("pause");
	return(0);
}
//==================================================================================
