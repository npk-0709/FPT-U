import java.io.RandomAccessFile;

public class FlightDLL {
    class Node {
        Flight data;
        Node prev, next;
        Node(Flight data) {
            this.data = data;
        }
    }

    Node head, tail;

    public boolean isEmpty() {
        return head == null;
    }

    public void ftraverse(RandomAccessFile f) throws Exception {
        if (head == null) {
            f.writeBytes("Empty");
            return;
        }
        Node cur = head;
        while (cur != null) {
            f.writeBytes("(" + cur.data.id + "," + cur.data.destination + "," + cur.data.price + ")");
            cur = cur.next;
        }
    }

    public void btraverse(RandomAccessFile f) throws Exception {
        if (tail == null) {
            f.writeBytes("Empty");
            return;
        }
        Node cur = tail;
        while (cur != null) {
            f.writeBytes("(" + cur.data.id + "," + cur.data.destination + "," + cur.data.price + ")");
            cur = cur.prev;
        }
    }

    public void addSorted(Flight x) {
         if (x == null || x.id == null) {
            return;
        }

        Node cur = head;
        while (cur != null) {
            if (cur.data != null && x.id.equals(cur.data.id)) {
                return;
            }
            cur = cur.next;
        }

        Node p = new Node(x);
        if (isEmpty()) {
            head = tail = p;
            return;
        }

        cur = head;
        while (cur != null && cur.data.price <= x.price) {
            cur = cur.next;
        }

        if (cur == null) {
            tail.next = p;
            p.prev = tail;
            tail = p;
        } else if (cur == head) {
            p.next = head;
            head.prev = p;
            head = p;
        } else {
            p.prev = cur.prev;
            p.next = cur;
            cur.prev.next = p;
            cur.prev = p;
        }
    }

    public void updatePrice(String id, int newPrice) {
         if (id == null) {
            return;
        }

        Node cur = head;
        while (cur != null) {
            if (cur.data != null && id.equals(cur.data.id)) {
                break;
            }
            cur = cur.next;
        }

        if (cur == null) {
            return;
        }

        if (cur == head && cur == tail) {
            head = tail = null;
        } else if (cur == head) {
            head = head.next;
            head.prev = null;
        } else if (cur == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            cur.prev.next = cur.next;
            cur.next.prev = cur.prev;
        }

        cur.prev = null;
        cur.next = null;
        cur.data.price = newPrice;
        addSorted(cur.data);
    }
}
