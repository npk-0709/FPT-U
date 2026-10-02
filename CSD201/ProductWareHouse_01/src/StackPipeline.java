public class StackPipeline {
    private Node head;
    private Node tail;

    public StackPipeline() {
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

    public void push(String name, double price, int amount) {
        Node newNode = new Node(new Product(name, price, amount));
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }
    }

    public Node pop() {
        if (isEmpty()) {
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
