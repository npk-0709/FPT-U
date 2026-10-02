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
        TNode newNode = new TNode(data);
        if (this.isEmpty()) {
            System.out.println("Chuyển newNode thành root vì cây rỗng!");
            this.root = newNode;
        }
        if (parent.getLeft() != null) {
            System.out.println("Nút " + parent.getData() + " đã có con trái!");
            return parent.getLeft();
        }
        parent.setLeft(newNode);
        return newNode;
    }

    // Thêm con phải dùng setRight() và getRight()
    public TNode addRight(TNode parent, int data) {
        TNode newNode = new TNode(data);
        if (this.isEmpty()) {
            System.out.println("Chuyển newNode thành root vì cây rỗng!");
            this.root = newNode;
        }
        if (parent.getRight() != null) {
            System.out.println("Nút " + parent.getData() + " đã có con phải!");
            return parent.getRight();
        }

        parent.setRight(newNode);
        return newNode;
    }

    public int countNode(TNode t) {
        if (t == null) {
            return 0;
        }
        int count_left = countNode(t.getLeft());
        int count_right = countNode(t.getRight());
        return 1 + count_right + count_left;
    }

    public int sumValueNode(TNode t) {
        if (t == null) {
            return 0;
        }
        int left = sumValueNode(t.getLeft());
        int right = sumValueNode(t.getRight());
        return t.getData() + left + right;
    }

    public int getHeight(TNode t) {
        if (t == null) {
            return 0;
        }

        int l = getHeight(t.getLeft());
        int r = getHeight(t.getRight());
        return Math.max(l, r);
    }

    public int countLeaves(TNode t) {
        if (t == null) {
            return 0;
        }
        if (t.getLeft() == null && t.getRight() == null) {
            return 1;
        }
        return countLeaves(t.getLeft()) + countLeaves(t.getRight());
    }

    public int countInnerNode(TNode t) {
        // Nếu cây rỗng hoặc t là nút lá (không có con trái lẫn con phải)
        if (t == null || (t.getLeft() == null && t.getRight() == null)) {
            return 0;
        }

        // Nếu không phải là lá, cộng 1 và đếm tiếp ở 2 nhánh con
        return 1 + countInnerNode(t.getLeft()) + countInnerNode(t.getRight());
    }

    public int countValue(TNode t, int x) {
        if (t == null) {
            return 0;
        }

        // Kiểm tra xem nút hiện tại có giá trị bằng x không
        int count = (t.getData() == x) ? 1 : 0;

        // Cộng số lần xuất hiện từ nhánh trái và nhánh phải
        return count + countValue(t.getLeft(), x) + countValue(t.getRight(), x);
    }

    public TNode deleteNode(TNode root, int key) {
        if (root == null) {
            return null;
        }

        // Tìm nút cần xóa
        if (key < root.getData()) {
            root.setLeft(deleteNode(root.getLeft(), key));
        } else if (key > root.getData()) {
            root.setRight(deleteNode(root.getRight(), key));
        } else {
            // Đã tìm thấy nút cần xóa!

            // Trường hợp 1 & 2: Nút có 0 hoặc 1 con
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }

            // Trường hợp 3: Nút có cả 2 con
            // Tìm giá trị nhỏ nhất bên cây con phải (Min Value)
            root.setData(minValue(root.getRight()));

            // Xóa nút thế chỗ đó ở cây con phải
            root.setRight(deleteNode(root.getRight(), root.getData()));
        }

        return root;
    }

    // Hàm phụ trợ tìm giá trị nhỏ nhất trong cây
    private int minValue(TNode root) {
        int minVal = root.getData();
        while (root.getLeft() != null) {
            minVal = root.getLeft().getData();
            root = root.getLeft();
        }
        return minVal;
    }

    // đếm số nút không là lá countInnerNode
    // đếm xem số lần xuất hiện giá trị x findvalue
    // đếm xem trong cây có bao nhiêu giá trị x count value
    // xóa nút ra khỏi cây

    // Duyệt NLR dùng đệ quy
    public void traverseNLR(TNode node) {
        if (node == null) return;
        System.out.print(node.getData() + " ");
        traverseNLR(node.getLeft());
        traverseNLR(node.getRight());
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
