
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author tranvotiendo
 */
public class BinaryTree {

    private TNode root;

    public BinaryTree() {
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

    //Them vao ben trai Node t
    public void addLeft(int newData, TNode t) {
        // Xet 3 TH: trai, phai (neu da co roi thi return) va TH cay empty
        TNode newNode = new TNode(newData);
        if (this.isEmpty()) {
            this.root = newNode;
            return;
        }
        if (t.getLeft() == null) {
            t.setLeft(newNode);
        }
    }

    public void addRight(int newData, TNode t) {

        TNode newNode = new TNode(newData);
        if (this.isEmpty()) {
            this.root = newNode;
            return;
        }
        if (t.getRight() == null) {
            t.setRight(newNode);
        }
    }

    public void visit(TNode node) {
        System.out.print(node.getData() + " ");
    }

    public void NLR(TNode node) {
        if (node == null) {
            return;
        }
        visit(node);
        NLR(node.getLeft());
        NLR(node.getRight());
    }

    public void LNR(TNode node) {
        if (node == null) {
            return;
        }
        LNR(node.getLeft());
        visit(node);
        LNR(node.getRight());
    }

    public void LRN(TNode node) {
        if (node == null) {
            return;
        }

        LRN(node.getLeft());
        LRN(node.getRight());
        visit(node);
    }

    //Dem so luong nut trong cay
    public int countNode(TNode t) {
        if (t == null) {
            return 0;
        } else {
            int left = countNode(t.getLeft());
            int right = countNode(t.getRight());
            return 1 + left + right;
        }
    }

    public int sumNode(TNode t) {
        if (t == null) {
            return 0;
        } else {
            int left = sumNode(t.getLeft());
            int right = sumNode(t.getRight());
            return t.getData() + left + right;
        }
    }

    public int hightTree(TNode t) {
        if (t == null) {
            return 0;
        } else {
            int left = hightTree(t.getLeft());
            int right = hightTree(t.getRight());

            if (left > right) { // hieu chinh cgi do o day dang bi sai
                return 1 + left;
            } else {
                return 1 + right;
            }
        }
    }
    // ĐÃ SỬA: Tính chiều cao của cây (Level cao nhất, root = 1)

    public int heightTree2(TNode t) {
        if (t == null) {
            return 0;
        }
        int left = heightTree2(t.getLeft());
        int right = heightTree2(t.getRight());
        return 1 + Math.max(left, right);
    }

    public void BFS() {
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

    // BÀI 1: Đếm số nút không phải là Leaf (Internal Node)
    // Một nút là Internal khi nó có ít nhất một con (trái hoặc phải khác null)
    public int countInnerNode(TNode t) {
        if (t == null) {
            return 0;
        }
        // Nếu là lá thì không tính
        if (t.getLeft() == null && t.getRight() == null) {
            return 0;
        }
        // Là nút trong: tính 1 + kết quả 2 nhánh con
        return 1 + countInnerNode(t.getLeft()) + countInnerNode(t.getRight());
    }

    // BÀI 2: Tìm xem có x trong cây tổng quát hay không (Trả về boolean)
    public boolean findValue(TNode t, int x) {
        if (t == null) {
            return false;
        }
        if (t.getData() == x) {
            return true;
        }
        // Tìm bên trái; nếu không có thì tìm tiếp bên phải
        return findValue(t.getLeft(), x) || findValue(t.getRight(), x);
    }

    // BÀI 3: Đếm trong cây có bao nhiêu giá trị x (xử lý cây có thể có giá trị trùng lặp)
    public int countValue(TNode t, int x) {
        if (t == null) {
            return 0;
        }
        int count = (t.getData() == x) ? 1 : 0;
        return count + countValue(t.getLeft(), x) + countValue(t.getRight(), x);
    }
    // ĐÃ SỬA LỖI: Đếm số nút lá (Leaf: không có con nào)

    public int countLeaf2(TNode t) {
        if (t == null) {
            return 0;
        }
        // Nếu cả con trái và con phải đều null -> Đây chính là Lá!
        if (t.getLeft() == null && t.getRight() == null) {
            return 1;
        }
        return countLeaf2(t.getLeft()) + countLeaf2(t.getRight());
    }
}
