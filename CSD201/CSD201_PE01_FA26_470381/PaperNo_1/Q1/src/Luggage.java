public class Luggage {
    String id;
    String owner;
    int weight;

    public Luggage(String id, String owner, int weight) {
        this.id = id;
        this.owner = owner;
        this.weight = weight;
    }


    public String toString() {
        return id + " " + owner + " " + weight;
    }
}
