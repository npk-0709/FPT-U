
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner nhap = new Scanner(System.in);
        
        System.out.print("Vui Long Nhap Tuoi");
        
        int tuoinhap = nhap.nextInt();
        
        Demo vidu = new Demo(tuoinhap);
        String sotuoi = vidu.getAge();
        System.out.println(sotuoi);

    }

}
