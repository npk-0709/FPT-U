int main(){
	int n, max= 0,count=0;
	printf("Input N=");
	scanf("%d",&n);
	while (n > 0) {
		count +=1;
	    if (max < n%10){
	    	max = n%10;
		}
	    n = n / 10;
	}
	printf("MAX= %d \n",max);
	printf("COUNT= %d",count);
	getchar();
	return 0;
}