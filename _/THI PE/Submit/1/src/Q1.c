#include <stdio.h>
#include <math.h>
#include <stdlib.h>

//---------------------------------------------
void calculateHypotenuse(double a, double b) {
    //Begin your statements here
    double h = 0;
    h = sqrt((pow(a,2)+pow(b,2)));
    printf("%.2lf",h);
    printf("\n");
    //The output result is formatted in two decimal places
    //End your statements here
}

//========DO NOT ADD NEW OR CHANGE THE STATEMENTS IN THE MAIN FUNCTION========
int main() {
    system("cls");
    printf("\nTEST Q1 (2 marks):\n");
    double a, b;
    do{	
	    printf("Enter a = ");
	    scanf("%lf", &a);
	    printf("Enter b = ");
	    scanf("%lf", &b);
	}while(a<=0 || b<=0);
    printf("\nOUTPUT:\n");
    calculateHypotenuse(a,b);
    system("pause");
    return 0;
}
//==================================================================
