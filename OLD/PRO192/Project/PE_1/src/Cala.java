
public class Cala {

    private String owner;
    private int price;

    public Cala(String owner, int price) {
        this.owner = owner;
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return owner + ", " + price;
    }

}
