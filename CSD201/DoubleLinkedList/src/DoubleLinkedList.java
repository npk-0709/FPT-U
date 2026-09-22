public class DoubleLinkedList {

    private Node head;
    private Node tail;

    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public void isEmpty() {
        if (head == null) {
            System.out.println("The list is empty.");
        } else {
            System.out.println("The list is not empty.");
        }
    }

    public void addHead(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
            head.setNext(tail);
            tail.setPrev(head);
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
            head.setPrev(tail);
            tail.setNext(head);
        }
    }

    public void addTail(int data) {
        Node newNode = new Node(data);
        if (tail == null) {
            head = newNode;
            tail = newNode;
            head.setNext(tail);
            tail.setPrev(head);
        } else {
            newNode.setPrev(tail);
            tail.setNext(newNode);
            tail = newNode;
            tail.setNext(head);
            head.setPrev(tail);
        }
    }

    public void traversal() {
        if (head == null) {
            System.out.println("The list is empty.");
            return;
        }
        Node current = head;
        do {
            System.out.print(current.getData() + " ");
            current = current.getNext();
        } while (current != head);
        System.out.println();

    }

    public void backwardTraversal() {
        if (tail == null) {
            System.out.println("The list is empty.");
            return;
        }
        Node current = tail;
        do {
            System.out.print(current.getData() + " ");
            current = current.getPrev();
        } while (current != tail);
        System.out.println();
    }

    public void deleteHead() {
        if (head == null) {
            System.out.println("The list is empty.");
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            head.setPrev(tail);
            tail.setNext(head);
        }
    }

    public void deleteTail() {
        if (tail == null) {
            System.out.println("The list is empty.");
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(head);
            head.setPrev(tail);
        }
    }

}
