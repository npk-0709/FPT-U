public class StackDoublyLinkedList {
    private NodeDoubly head;
    private NodeDoubly tail;

    public StackDoublyLinkedList(NodeDoubly head, NodeDoubly tail, NodeDoubly prev) {
        this.head = head;
        this.tail = tail;

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

    public void push(int value) {
        NodeDoubly newNodeDoubly = new NodeDoubly(value);
        if (isEmpty()) {
            head = newNodeDoubly;
            tail = newNodeDoubly;
        } else {
            newNodeDoubly.setNext(head);
            head = newNodeDoubly;
        }
    }

    public NodeDoubly pop() {
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