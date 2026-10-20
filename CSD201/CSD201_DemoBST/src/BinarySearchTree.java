
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author tranvotiendo
 */
public class BinarySearchTree {

    private TNode root;

    public BinarySearchTree() {
        this.root = null;
    }

    public TNode getRoot() {
        return root;
    }

    public void setRoot(TNode root) {
        this.root = root;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public void add(int newData, TNode tmp) {

        TNode newNode = new TNode(newData);

        if (this.isEmpty()) {
            this.root = newNode;
            return;
        }

        if (newData > tmp.getData()) {

            if (tmp.getRight() == null) {
                tmp.setRight(newNode);
                return;
            }
            tmp = tmp.getRight();
            add(newData, tmp);
        }

        if (newData < tmp.getData()) {

            if (tmp.getLeft() == null) {
                tmp.setLeft(newNode);
                return;
            }
            tmp = tmp.getLeft();
            add(newData, tmp);
        }

        if (newData == tmp.getData()) {
            return;
        }

    }

    public void addWithoutRecursion(int newData) {

        TNode newNode = new TNode(newData);

        if (this.isEmpty()) {
            this.root = newNode;
            return;
        }

        TNode tmp = this.root;

        while (tmp != null) {

            if (newData > tmp.getData()) {
                if (tmp.getRight() == null) {
                    tmp.setRight(newNode);
                    return;
                }
                tmp = tmp.getRight();
            }

            if (newData < tmp.getData()) {
                if (tmp.getLeft() == null) {
                    tmp.setLeft(newNode);
                    return;
                }
                tmp = tmp.getLeft();
            }

            if (newData == tmp.getData()) {
                return;
            }
        }
    }

    public void visit(TNode node) {
        System.out.print(node.getData() + " ");
    }

    public void NLR(TNode node) { // Function TEST1
        if (node == null) {
            return;
        }
        visit(node);
        NLR(node.getLeft());
        NLR(node.getRight());
    }

    public void BFS() { // Function TEST2
        if (!this.isEmpty()) {
            Queue<TNode> q = new LinkedList<TNode>();
            q.offer(root); //enqueue
            while (!q.isEmpty()) {
                TNode tmp = q.poll();
                System.out.print(tmp + " ");

                //Enqueue left child
                if (tmp.getLeft() != null) {
                    q.offer(tmp.getLeft());
                }
                //Enqueue right child
                if (tmp.getRight() != null) {
                    q.offer(tmp.getRight());
                }
            }
        }
    }

    private TNode delete(TNode node, int key) {
        if (node == null) {
            return null;
        }

        if (key < node.getData()) {
            node.setLeft(delete(node.getLeft(), key));
        } else if (key > node.getData()) {
            node.setRight(delete(node.getRight(), key));
        } else {
            // tìm thấy node cần xóa

            // không có con trái (kể cả node lá): thay bằng con phải
            if (node.getLeft() == null) {
                return node.getRight();
            }
            // không có con phải: thay bằng con trái
            if (node.getRight() == null) {
                return node.getLeft();
            }

            // có 2 con: lấy successor (min bên phải) đè lên rồi xóa successor
            TNode succ = findMin(node.getRight());
            node.setData(succ.getData());
            node.setRight(delete(node.getRight(), succ.getData()));
        }
        return node;
    }

    private TNode findMin(TNode node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
}
