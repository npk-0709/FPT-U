#include <stdio.h>

void clear(){
	while(getchar()!='\n');
}

int getInt(int min, int max){
	
	int value , flag=1  , count;
	char lc;
	
	do {
		printf("Input an integer number is range[%d-%d]: ",min,max);
		count = scanf("%d%c",&value,&lc);
		if (count==0 ){
			printf("Invalid Input");
			clear();
	}else if( lc!='\n'){
		printf("Invalid Input 1");
			clear();

		}else if (value<min || value > max){
			printf("Input Not Range [%d-%d]",min,max);
			clear();
		}else{
			flag = 0;
		}
	}while(flag == 1);
	
	return value;
}


int main(){
	printf("Input = %d",getInt(10,50));
	return 0;
}