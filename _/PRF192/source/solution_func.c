#include <stdio.h>


void printDivisors( int n){
	int i;
	for (i=1; i<=n; i++){
		if (n%i==0){
			printf("%d ",i);
		}
	}
}

int inputValue(){
	int n;
	printf("\nInput N= ");
	scanf("%d",&n);
	return n;
}


int main(){
	int i , n;
	
	for (i=1; i<=3;i++){
		n = inputValue();
		
		printDivisors(n);
		
		
	}
	
	
	getchar();
	return 0;
}