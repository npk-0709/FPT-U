#include <stdio.h>
#include <stdlib.h>

//--------------------------------------------------------------------------------

void sum(int n) {
	//Begin your codes here
	
	double result = 0;
	for (int i = 1; i <= n; i++) {
		result += (1.0 / (2 * i));
	}
	printf("%.2lf", result);
}

//=============DO NOT ADD NEW OR CHANGE STATEMENTS IN THE MAIN FUNCTION============
int main() {
	system("cls");
	printf("\nTEST Q1 (2 marks):\n");
	int n;
	printf("Enter n: ");
	scanf("%d", &n);
	//================================================================
	//=====DO NOT ADD NEW OR CHANGE STATEMENTS AFTER THIS LINE========
	//==THE OUTPUT AFTER THIS LINE WILL BE USED TO MARK YOUR PROGRAM==
	printf("\nOUTPUT:\n");
	sum(n);
	printf("\n");
	system ("pause");
	return(0);
}
//==================================================================================
