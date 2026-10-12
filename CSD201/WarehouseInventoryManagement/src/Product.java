
public class Product {
    private String barcode;
    private String name;
    private String category;
    private double price;

    public Product(String barcode, String name, String category, double price) {
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getBarcode()   { return barcode; }
    public String getName()      { return name; }
    public String getCategory()  { return category; }
    public double getPrice()     { return price; }

    @Override
    public String toString() {
        return String.format("%-10s | %-22s | %-12s | %,.0f VND",
                barcode, name, category, price);
    }
}
