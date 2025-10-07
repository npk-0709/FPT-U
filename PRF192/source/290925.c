#include <stdio.h>

double inputN(){
	int n;
	printf("Input N: ");
	scanf("%d",&n);
	return n;
}

void sumDivisors(int N){
	int sum;
	for (int i=1; i<=N/2 ;i++){
		if (N%i==0){
			sum+=i;
		}
	}
	sum+=N;
	printf("SUM= %d",sum);
}

int main(){
	int N;
	N = inputN();
	printf("Divisors List: ");
	sumDivisors(N);
	getchar();
	return 0;
}