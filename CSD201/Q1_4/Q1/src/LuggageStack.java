import java.io.RandomAccessFile;

public class LuggageStack {
    class Node {
        Luggage data;
        Node next;
        Node(Luggage data) {
            this.data = data;
        }
    }

    Node top;

    public boolean isEmpty() {
        return top == null;
    }

    public void push(Luggage x) {
        Node p = new Node(x);
        p.next = top;
        top = p;
    }

    public Luggage pop() {
        if (isEmpty()) return null;
        Luggage x = top.data;
        top = top.next;
        return x;
    }

    public void ftraverse(RandomAccessFile f) throws Exception {
        if (top == null) {
            f.writeBytes("Empty");
            return;
        }
        Node cur = top;
        while (cur != null) {
            f.writeBytes("(" + cur.data.id + "," + cur.data.owner + "," + cur.data.weight + ")");
            cur = cur.next;
        }
    }

    public Luggage peek() {
        Luggage result = null;
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        if (top != null) {
            result = top.data;
        }
        // -----------------------------------------------------
        return result;
    }

    public int unload(int k) {
        int total = 0;
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        while (k > 0 && !isEmpty()) {
            Luggage x = pop();
            if (x != null) {
                total += x.weight;
            }
            k--;
        }
        // -----------------------------------------------------
        return total;
    }
}
