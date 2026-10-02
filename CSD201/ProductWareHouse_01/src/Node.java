public class Node {
    private Product data;
    private Node next;

    public Node(Product node, Node next) {
        this.data = node;
        this.next = next;
    }

    public Node(Product node) {
        this.data = node;
        this.next = null;
    }

    public Product getData() {
        return data;
    }

    public void setNode(Product node) {
        this.data = node;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

}
