public class StackSingleLinkedList {
    private NodeSingle head;
    private NodeSingle tail;

    public StackSingleLinkedList(NodeSingle head, NodeSingle tail) {
        this.head = head;
        this.tail = tail;
    }

    public StackSingleLinkedList() {
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

    public void push(int value) {
        NodeSingle newNodeSingle = new NodeSingle(value);
        if (isEmpty()) {
            head = newNodeSingle;
            tail = newNodeSingle;
        } else {
            newNodeSingle.setNext(head);
            head = newNodeSingle;
        }
    }

    public NodeSingle pop() {
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

    public void clean() {
        head = null;
        tail = null;
    }

    public NodeSingle top() {
        return head;
    }
}
