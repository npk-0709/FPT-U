public class Main {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        System.out.println("Cây rỗng? " + tree.isEmpty());

        // Tạo cây
        TNode root = tree.setRoot(5);
        tree.addLeft(root, 7);
        tree.addRight(root, 4);
        tree.addLeft(root.getLeft(), 8);
        tree.addRight(root.getLeft(), 10);
        tree.addRight(root.getRight(), 6);

        System.out.println("Cây rỗng? " + tree.isEmpty());

        System.out.print("\nDuyệt NLR: ");
        tree.traverseNLR(tree.getRoot());

        System.out.print("\nDuyệt LNR: ");
        tree.traverseLNR(tree.getRoot());

        System.out.print("\nDuyệt LRN: ");
        tree.traverseLRN(tree.getRoot());
    }
}