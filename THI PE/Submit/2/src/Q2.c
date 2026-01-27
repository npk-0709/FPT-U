#include <stdio.h>
#include <stdlib.h>

double sum(int n) {
    double s = 0;
   //Begin your statements here
    for (int i=1;i<=n;i++){
    	s = s + ((2.0*i-1)/(2.0*i));
	}
    //End your statements
    return s;    
}

//====DO NOT ADD NEW OR CHANGE STATEMENTS IN THE MAIN FUNCTION====
int main() {
    system("cls");
    printf("\nTEST Q2 (3 marks):\n");
    int n;
    double s;
    do{
    	printf("Enter n = "); 
	    scanf("%d", &n);  
	}while(n<=0);    
   
    printf("\nOUTPUT:\n");
    s = sum(n);
    printf("%.2lf\n", s);
    system("pause");
    return 0;
}
//=================================================================