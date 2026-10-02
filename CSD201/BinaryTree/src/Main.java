public class Main {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        System.out.println("Cây rỗng? " + tree.isEmpty());

        // Tạo cây
        TNode root = tree.setRoot(10);
        TNode n20 = tree.addLeft(root, 20);
        TNode n30 = tree.addRight(root, 30);
        tree.addLeft(n20, 40);
        tree.addRight(n20, 50);

        System.out.println("Cây rỗng? " + tree.isEmpty());

        System.out.print("\nDuyệt NLR: ");
        tree.traverseNLR(tree.getRoot());

        System.out.print("\nDuyệt LNR: ");
        tree.traverseLNR(tree.getRoot());

        System.out.print("\nDuyệt LRN: ");
        tree.traverseLRN(tree.getRoot());
    }
}