
public class Main {
    public static void main(String[] args) {
        ProductWareHouse pwh = new ProductWareHouse();
        pwh.load(5);
        pwh.waiting();
        pwh.process();
        pwh.transfer();

        if (pwh.isEmpty()) {
            System.out.println("100 Point");
        } else {
            System.out.println("cook");
        }

    }
}