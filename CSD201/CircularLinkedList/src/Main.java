public class Main {
    public static void main(String[] args) {
        // test the CLL swap method
        CLL cll = new CLL();
        cll.addTail(1);
        cll.addTail(2);
        cll.addTail(3);
        cll.addTail(4);
        System.out.println("Original list:");
        cll.traversal();
        cll.swapPairs();
        System.out.println("List after swapping head and tail:");
        cll.traversal();
    }
}