public class Product {

    private String pid;
    private String pname;
    private double price;
    private int quantity;

    public Product(String pid, String pname, double price, int quantity) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // tạo phương thức tostring trả về thông tin "pid, pname, price, quantity" với định dạng pid in hoa và pcice có 2 chữ số thập phân
    @Override
    public String toString() {
        return String.format("%s, %s, %.2f, %d", pid.toUpperCase(), pname, price, quantity);
    }
}
