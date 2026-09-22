public class QueueDoublyLinkedList {
    private NodeDoubly head;
    private NodeDoubly tail;

    public QueueDoublyLinkedList(NodeDoubly head, NodeDoubly tail, NodeDoubly prev) {
        this.head = head;
        this.tail = tail;

    }

    public QueueDoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public NodeDoubly getTail() {
        return tail;
    }

    public void setTail(NodeDoubly tail) {
        this.tail = tail;
    }

    public NodeDoubly getHead() {
        return head;
    }

    public void setHead(NodeDoubly head) {
        this.head = head;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void enqueue(int value) {
        NodeDoubly newNode = new NodeDoubly(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
        }
    }

    public NodeDoubly dequeue() {
        if (isEmpty()) {
            return null;
        } else if (head == tail) {
            NodeDoubly temp = head;
            head = null;
            tail = null;
            return temp;

        } else {
            NodeDoubly temp = head;
            head = head.getNext();
            if (head == null) {
                tail = null;
            } else {
                head.setPrev(null);
            }
            return temp;
        }
    }

    public NodeDoubly top() {
        return head;
    }

    public void clean() {
        head = null;
        tail = null;
    }
}