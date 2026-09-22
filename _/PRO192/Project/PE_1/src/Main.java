
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner getInput = new Scanner(System.in);
        System.out.print("Enter ownner: ");
        String owner = getInput.nextLine();
        System.out.print("Enter price: ");
        int price = getInput.nextInt();
        System.out.print("Enter color: ");
        int color = getInput.nextInt();

        SpecCala spc = new SpecCala(color, owner, price);

        System.out.println("1. Test toString()");
        System.out.println("2. Test setData()");
        System.out.print("3. Test getValue()\nEnter TC(1,2,3): ");

        int choise = getInput.nextInt();

        if (choise == 1) {
            System.out.println(spc.toString());
        } else if (choise == 2) {
            spc.setData();
            System.out.println(spc.tooString());
        } else if (choise == 3) {
            System.out.println(spc.getValue());
        } else {
            System.out.println("Choise 1 2 3");
        }

    }

}
