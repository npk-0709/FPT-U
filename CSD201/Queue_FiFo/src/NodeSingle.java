public class NodeSingle {
    private int value;
    private NodeSingle next;

    public NodeSingle(int value) {
        this.value = value;
        this.next = null;
    }

    public NodeSingle(int value, NodeSingle next) {
        this.value = value;
        this.next = next;

    }

    public NodeSingle() {
        this.value = 0;
        this.next = null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public NodeSingle getNext() {
        return next;
    }

    public void setNext(NodeSingle next) {
        this.next = next;
    }
}
