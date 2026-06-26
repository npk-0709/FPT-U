
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner getScan = new Scanner(System.in);
        System.out.print("Input A: ");
        float a = getScan.nextFloat();
        System.out.print("Input B: ");
        float b = getScan.nextFloat();
        System.out.print("Input C: ");
        float c = getScan.nextFloat();
        float delta = b * b - 4 * a * c;
        
        
        
        if (delta < 0) {
            System.out.print("No Solution");
        } else if (delta == 0) {
            float x = -b / 2 * a;
            System.out.printf("Result X1 = X2 = %.2f \n", x);
        } else {
            double x1 = (double) ((-b + Math.sqrt(delta)) / (2 * a));
            double x2 = (double) ((-b - Math.sqrt(delta)) / (2 * a));
            System.out.printf("Result X1= %.2f AND X2= %.2f", x1, x2);
        }
        getScan.close();
    }
}
