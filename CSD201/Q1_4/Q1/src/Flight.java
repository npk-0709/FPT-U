public class Flight {
    String id;
    String destination;
    int price;

    public Flight(String id, String destination, int price) {
        this.id = id;
        this.destination = destination;
        this.price = price;
    }

    public String toString() {
        return id + " " + destination + " " + price;
    }
}
