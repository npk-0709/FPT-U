public class NodeDoubly {
    private int value;
    private NodeDoubly next;
    private NodeDoubly prev;

    public NodeDoubly(int value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    public NodeDoubly(int value, NodeDoubly next, NodeDoubly prev) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }

    public NodeDoubly() {
        this.value = 0;
        this.next = null;
        this.prev = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public NodeDoubly getPrev() {
        return prev;
    }

    public void setPrev(NodeDoubly prev) {
        this.prev = prev;
    }

    public NodeDoubly getNext() {
        return next;
    }

    public void setNext(NodeDoubly next) {
        this.next = next;
    }
}
