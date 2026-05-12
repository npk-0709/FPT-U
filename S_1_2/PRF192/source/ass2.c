#include <stdio.h>

void printCalc(double r){
	double pi = 3.14;
	printf("Diameter= %lf\n",2*r);
	printf("Circumference= %lf\n",2*pi*r);
	printf("Area= %lf\n",pi*r*r);
}

double inputValue(){
	double n;
	printf("Input N=");
	scanf("%lf",&n);
	return n;
}


int main(){
	
	int r;
	
	printCalc(inputValue());
	
	
	getchar();
	return 0;
}