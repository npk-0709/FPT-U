public class Node {
    private Product data;
    private Node next;

    public Node(Product data) {
        this.data = data;
        this.next = null;
    }

    public Node(Product data, Node next) {
        this.data = data;
        this.next = next;
    }

    public Node() {
        this.data = null;
        this.next = null;
    }

    public Product getData() {
        return data;
    }

    public void setData(Product data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void display() {
        System.out.println(data.toString());
    }

}
