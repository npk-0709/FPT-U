
/**
 *
 * @author tranvotiendo
 */
public class BSTProgram {

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        TNode t = new TNode(15);
        bst.setRoot(t);
//        bst.add(25, t);
//        bst.add(20, t);
//        bst.add(5, t);
//        bst.add(10, t);
//        bst.add(35, t);
//        bst.add(0, t);
//        bst.NLR(t);

        bst.addWithoutRecursion(25);
        bst.addWithoutRecursion(20);
        bst.addWithoutRecursion(5);
        bst.addWithoutRecursion(10);
        bst.addWithoutRecursion(35);
        bst.addWithoutRecursion(0);
        bst.NLR(t);
        System.out.println("");
        bst.BFS();
    }
}
