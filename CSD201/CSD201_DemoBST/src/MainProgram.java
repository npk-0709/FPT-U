
/**
 *
 * @author tranvotiendo
 */
public class MainProgram {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();
        TNode root = new TNode(5);
        tree.setRoot(root);
        
        //Add trai phai ROOT
        tree.addLeft(7, root);
        tree.addRight(8, root);
        
        //Add trai ROOT.next
        tree.addLeft(2, root.getLeft());
        tree.addRight(6, root.getLeft());
        tree.addLeft(0, root.getLeft().getRight());
        
        //Add phai ROOT.next
        tree.addRight(4, root.getRight());
        System.out.print("NLR: ");
        tree.NLR(root);
        System.out.println("");
        System.out.print("LRN: ");
        tree.LRN(root);
        System.out.println("");
        System.out.print("LNR: ");
        tree.LNR(root);
        System.out.println("");
        
        System.out.println("Count: " + tree.countNode(root));
        System.out.println("Sum: " + tree.sumNode(root));
        System.out.println("Height: " + tree.hightTree(root));
        
        System.out.println("BST");
        tree.BFS();
        
    }
}
