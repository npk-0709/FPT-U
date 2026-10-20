
/**
 *
 * @author tranvotiendo
 */
public class TNode {

    private int data;
    private TNode left;
    private TNode right;

    public TNode(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public int getData() {
        return data;
    }

    public TNode getLeft() {
        return left;
    }

    public TNode getRight() {
        return right;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setLeft(TNode left) {
        this.left = left;
    }

    public void setRight(TNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "" + data;
    }

}
