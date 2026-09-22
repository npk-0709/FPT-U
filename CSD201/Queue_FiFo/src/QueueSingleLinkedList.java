public class QueueSingleLinkedList {
    private NodeSingle head;
    private NodeSingle tail;

    public QueueSingleLinkedList(NodeSingle head, NodeSingle tail) {
        this.head = head;
        this.tail = tail;
    }

    public QueueSingleLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public NodeSingle getHead() {
        return head;
    }

    public void setHead(NodeSingle head) {
        this.head = head;
    }

    public NodeSingle getTail() {
        return tail;
    }

    public void setTail(NodeSingle tail) {
        this.tail = tail;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void enqueue(int value) {
        NodeSingle node = new NodeSingle(value);
        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.setNext(node);
            tail = node;
        }
    }

    public NodeSingle dequeue() {
        if (isEmpty()) {
            return null;
        } else if (head == tail) {
            NodeSingle temp = head;
            head = null;
            tail = null;
            return temp;

        } else {
            NodeSingle temp = head;
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
            return temp;
        }
    }

    public void clear() {
        head = null;
        tail = null;
    }

    public NodeSingle peek() {
        return head;
    }
}
