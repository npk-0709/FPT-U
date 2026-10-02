public class ProductList {

    private Node head;
    private Node tail;

    public ProductList() {
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
        return head == null && tail == null;
    }

    public void addHead(String name, double price, int amount) {
        Node newNode = new Node(new Product(name, price, amount));
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            newNode.setNext(head);
            this.head = newNode;
        }
    }

    public void addTail(String name, double price, int amount) {
        Product newProduct = new Product(name, price, amount);
        Node newNode = new Node(newProduct);
        if (this.isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
    }

    public Node removeHead() {
        Node tmp = null;
        if (this.isEmpty()) {
            return null;
        } else if (head == tail) {
            tmp = head;
            head = null;
            tail = null;
            return tmp;
        } else {
            tmp = head;
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
            return tmp;
        }
    }

}
