
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

int main()
{
	while (1){
		printf("\Input here: ");
	    char c = getchar();
	    if (c=="="){
	    	printf("Done");
	    	break;
		}
	    
	    if (isdigit(c)){
	    	printf("is Digit %c",c);
	    	
		}else if(isupper(c)){
			printf("is Upper %c",c);
			
		}else if (islower(c)){
			printf("is lower %c",c);
			
		}else if (isblank(c)){
			printf("none %c",c);
		}else{
			printf("Else %c",c);
		}
	}
    
    
    
    system("pause");
    return 0;
}