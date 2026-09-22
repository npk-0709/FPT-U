import java.util.Scanner;


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner getScan  = new Scanner(System.in);
        System.out.print("Input A: ");
        int a = getScan.nextInt();
        System.out.print("Input B: ");
        int b = getScan.nextInt();
        if (a == 0){
            if (b==0){
                System.out.print("Many Solotion");
            }else{
                System.out.print("No Solotion");
            }
        }else{
            double x = (double) -b/a;
            System.out.printf("Result = %.2f \n",x);
            
        }
    }   
    
}
