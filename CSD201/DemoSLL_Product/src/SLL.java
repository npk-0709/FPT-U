public class SLL {

    private Node head;
    private Node tail;

    public SLL() {
        this.head = null;
        this.tail = null;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public boolean isEmpty() {
        return this.head == null && this.tail == null;
    }

    public void addHead(String pid, String name, double price, int quantity) {
        Product newProduct = new Product(pid, name, price, quantity);
        Node newNode = new Node(newProduct);
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            newNode.setNext(this.head);
            this.head = newNode;
        }
    }
    public void addTail(String pid, String name, double price, int quantity) {
        Product newProduct = new Product(pid, name, price, quantity);
        Node newNode = new Node(newProduct);
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
    }

    public void traversal() {
        Node current = this.head;
        if (this.isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        while (current != null) {
            System.out.print(current.getData() + " ");
            current = current.getNext();
        }
        System.out.println();
    }

    public Product findById(String pid) {
        Node current = this.head;
        if (this.isEmpty()) {
            System.out.println("List is empty");
            return null;
        }
        while (current != null) {
            if (current.getData().getPid().equalsIgnoreCase(pid)) {
                return current.getData();
            }
            current = current.getNext();
        }
        System.out.println("Node with id " + pid + " not found");
        return null;
    }

    public void updateData(String pid, String new_pname, double new_price, int new_quantity) {
        Node current = this.head;
        if (this.isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        while (current != null) {
            if (current.getData().getPid().equalsIgnoreCase(pid)) {
                current.getData().setPname(new_pname);
                current.getData().setPrice(new_price);
                current.getData().setQuantity(new_quantity);
                System.out.println("Node with id " + pid + " updated successfully");
                return;
            }
            current = current.getNext();
        }
        System.out.println("Node with id " + pid + " not found");
    }

    public void delete(String pid) {
        Node current = this.head;
        Node previous = null;
        if (this.isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        while (current != null) {
            if (current.getData().getPid().equalsIgnoreCase(pid)) {
                if (previous == null) {
                    this.head = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }
                System.out.println("Node with id " + pid + " deleted successfully");
                return;
            }
            previous = current;
            current = current.getNext();
        }
        System.out.println("Node with id " + pid + " not found");
    }


    public int countInStock() {
        int count = 0;
        Node current = this.head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    public double sumInStock() {
        double sum = 0;
        Node current = this.head;
        while (current != null) {
            sum += current.getData().getPrice() * current.getData().getQuantity();
            current = current.getNext();
        }
        return sum;
    }
}
