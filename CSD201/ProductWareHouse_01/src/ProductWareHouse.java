import java.util.Scanner;

public class ProductWareHouse {
    private ProductList productlist;
    private StackPipeline stackpipline;
    private QueueWaiting queuewaiting;

    public ProductWareHouse() {
        this.productlist = new ProductList();
        this.stackpipline = new StackPipeline();
        this.queuewaiting = new QueueWaiting();
    }


    public boolean isEmpty() {
        return productlist.isEmpty() && stackpipline.isEmpty() && queuewaiting.isEmpty();

    }

    public void load(int n) {
        int i = 0;
        for (i = 0; i < n; i++) {
            System.out.println("InputData:");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();

            String name = input.split(",")[0];
            double price = (double) Double.parseDouble(input.split(",")[1]);
            int amount = (int) Integer.parseInt(input.split(",")[2]);

            if ((amount % 2) == 0) {
                productlist.addHead(name, price, amount);
            } else {
                productlist.addTail(name, price, amount);
            }
        }
    }

    public void waiting() {

        while (!productlist.isEmpty()) {
            Product getHead = productlist.removeHead().getData();
            queuewaiting.enqueue(getHead.getName(), getHead.getPrice(), getHead.getAmount());
        }
    }


    public void process() {

        while (!queuewaiting.isEmpty()) {
            Product tmp = queuewaiting.dequeue().getData();
            stackpipline.push(tmp.getName(), tmp.getPrice(), tmp.getAmount());
        }
    }

    public void transfer() {
        while (!stackpipline.isEmpty()) {
            Node tmp = stackpipline.pop();
            System.out.println(tmp.getData().display());
        }
    }
}
