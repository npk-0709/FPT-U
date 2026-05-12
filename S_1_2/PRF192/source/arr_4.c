#include <stdio.h>

int main(){
	int minIndex;
	int a[] = {1,9,6,4,5,3,2,8};
	int n = 8;
	
	int i,j;
	for (i=0; i < n-1 ; i++){
		minIndex = i;
		for (j=i+1; j<n; j++){
			if (a[minIndex] > a[j]){
				minIndex = j;
			}
		}
		if (minIndex > i ){
			int t = a[minIndex];
			a[minIndex] = a[i];
			a[i] = t;
		}
	}
	for (i=0;i<n-1;i++){
		printf("%d ",a[i]);
	}
	getchar();
	return 0;
}