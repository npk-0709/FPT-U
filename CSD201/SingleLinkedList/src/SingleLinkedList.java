public class SingleLinkedList {
    public NodeInt head;
    public NodeInt tail;

    public SingleLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public SingleLinkedList(NodeInt head, NodeInt tail) {
        this.head = head;
        this.tail = tail;
    }

    public NodeInt getHead() {
        return head;
    }

    public void setHead(NodeInt head) {
        this.head = head;
    }

    public NodeInt getTail() {
        return tail;
    }

    public void setTail(NodeInt tail) {
        this.tail = tail;
    }

    public boolean isEmpty() {
        return this.head == null && this.tail == null;
    }

    public void addHead(int newData) {
        NodeInt newNode = new NodeInt(newData);
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            newNode.setNext(this.head);
            this.head = newNode;
        }

    }

    public void addTail(int newData) {
        NodeInt newNode = new NodeInt(newData);
        if (this.isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
    }

    public void traversal() {
        NodeInt current = this.head;
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

    public void updateData(int oldData, int newData) {
        NodeInt current = this.head;
        if (this.isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        while (current != null) {
            if (current.getData() == oldData) {
                current.setData(newData);
                return;
            }
            current = current.getNext();
        }
        System.out.println("Node with data " + oldData + " not found");
    }

    public void deleteNode(int data) {

        if (this.isEmpty()) {
            System.out.println("List is empty");
            return;
        }
        if (this.getHead().getNext() == null) {
            if (this.head == this.tail) {
                this.head = null;
                this.tail = null;
                return;
            } else {
                System.out.println("Node with data " + data + " not found");
                return;
            }
        }

        NodeInt current = this.head;
        NodeInt previous = null;
        while (current != null) {
            if (current.getData() == data) {
                if (previous == null) {
                    this.head = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }
                if (current == this.tail) {
                    this.tail = previous;
                }
                return;
            }
            previous = current;
            current = current.getNext();
        }
        System.out.println("Node with data " + data + " not found");
    }

    public int countNode() {
        int count = 0;
        NodeInt current = this.head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }
}
