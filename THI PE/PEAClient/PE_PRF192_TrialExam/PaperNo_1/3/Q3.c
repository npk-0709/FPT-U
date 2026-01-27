#include <stdio.h>
#include <stdlib.h>

//-----------------------------------------------
int sum(int a[],int n){
	//Begin your statements here


	
	//End your statements
}
//DO NOT ADD NEW OR CHANGE STATEMENTS IN THIS FUNCTION
void input(int a[],int* pn){
	scanf("%d",pn);
	int i;
	for(i=0;i<*pn;i++)
		scanf("%d",&a[i]);
}
//==========DO NOT ADD NEW OR CHANGE STATEMENTS IN THE MAIN FUNCTION========
int main(int argc, char* argvs[]){
  	system("cls");
  	printf("\nTEST Q3 (2 marks):\n");
  	int a[100],n, s;
  	input(a,&n);
  	s = sum(a,n);
  	printf("OUTPUT:\n");
  	printf("%d\n",s);
  	system("pause");
  	return 0;
  	//============================================================
}

