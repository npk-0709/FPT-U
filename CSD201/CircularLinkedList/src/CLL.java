public class CLL {
    private Node tail;

    public CLL() {
        this.tail = null;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public boolean isEmpty() {
        return tail == null;
    }

    public void addHead(int data) {
        Node newNode = new Node(data);
        if (isEmpty()) {
            tail = newNode;
            tail.setNext(tail);
        } else {
            newNode.setNext(tail.getNext());
            tail.setNext(newNode);
        }
    }

    public void addTail(int data) {
        Node newNode = new Node(data);
        if (isEmpty()) {
            tail = newNode;
            tail.setNext(tail);
        } else {
            newNode.setNext(tail.getNext());
            tail.setNext(newNode);
            tail = newNode;
        }
    }

    public void traversal() {
        if (isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }
        Node current = tail.getNext();
        do {
            System.out.print(current.getData() + " ");
            current = current.getNext();
        } while (current != tail.getNext());
        System.out.println();
    }

    public void deleteHead() {
        if (isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }
        if (tail.getNext() == tail) {
            tail = null;
        } else {
            tail.setNext(tail.getNext().getNext());
        }
    }

    public void deleteTail() {
        if (isEmpty()) {
            System.out.println("The list is empty.");
            return;
        }
        if (tail.getNext() == tail) {
            tail = null;
        } else {
            Node current = tail.getNext();
            while (current.getNext() != tail) {
                current = current.getNext();
            }
            current.setNext(tail.getNext());
            tail = current;
        }
    }

    public void swapPairs() { // 1 2 3 4 -> 2 1 4 3 circular linked list
        if (isEmpty() || tail.getNext() == tail) {
            return;
        }
        Node current = tail.getNext();
        Node prev = tail;
        do {
            Node first = current;
            Node second = current.getNext();
            if (second == tail.getNext()) {
                break;
            }
            first.setNext(second.getNext());
            second.setNext(first);
            prev.setNext(second);
            prev = first;
            current = first.getNext();
        } while (current != tail.getNext());
    }
}


