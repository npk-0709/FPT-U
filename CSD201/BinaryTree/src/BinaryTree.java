public class BinaryTree {
    private TNode root;

    public BinaryTree() {
        this.root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public TNode getRoot() {
        return root;
    }

    public TNode setRoot(int data) {
        root = new TNode(data);
        return root;
    }

    // Thêm con trái dùng setLeft() và getLeft()
    public TNode addLeft(TNode parent, int data) {
        if (parent == null) {
            System.out.println("Nút cha không tồn tại!");
            return null;
        }
        if (parent.getLeft() != null) {
            System.out.println("Nút " + parent.getData() + " đã có con trái!");
            return parent.getLeft();
        }
        TNode newNode = new TNode(data);
        parent.setLeft(newNode);
        return newNode;
    }

    // Thêm con phải dùng setRight() và getRight()
    public TNode addRight(TNode parent, int data) {
        if (parent == null) {
            System.out.println("Nút cha không tồn tại!");
            return null;
        }
        if (parent.getRight() != null) {
            System.out.println("Nút " + parent.getData() + " đã có con phải!");
            return parent.getRight();
        }
        TNode newNode = new TNode(data);
        parent.setRight(newNode);
        return newNode;
    }

    // Duyệt NLR
    public void traverseNLR(TNode node) {
        if (node == null) return;
        System.out.print(node.getData() + " "); // Lấy dữ liệu qua getData()
        traverseNLR(node.getLeft());           // Lấy con trái qua getLeft()
        traverseNLR(node.getRight());          // Lấy con phải qua getRight()
    }

    // Duyệt LNR
    public void traverseLNR(TNode node) {
        if (node == null) return;
        traverseLNR(node.getLeft());
        System.out.print(node.getData() + " ");
        traverseLNR(node.getRight());
    }

    // Duyệt LRN
    public void traverseLRN(TNode node) {
        if (node == null) return;
        traverseLRN(node.getLeft());
        traverseLRN(node.getRight());
        System.out.print(node.getData() + " ");
    }
}
