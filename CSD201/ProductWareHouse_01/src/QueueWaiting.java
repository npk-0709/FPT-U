public class QueueWaiting {
    private Node head;
    private Node tail;

    public QueueWaiting() {
        this.head = null;
        this.tail = null;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public boolean isEmpty() {
        return this.head == null && this.tail == null;
    }

    public void enqueue(String name, double price, int amount) {
        Node node = new Node(new Product(name, price, amount));
        if (this.isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.setNext(node);
            tail = node;
        }
    }

    public Node dequeue() {
        if (this.isEmpty()) {
            return null;
        } else if (head == tail) {
            Node temp = head;
            head = null;
            tail = null;
            return temp;

        } else {
            Node temp = head;
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
            return temp;
        }
    }
}
