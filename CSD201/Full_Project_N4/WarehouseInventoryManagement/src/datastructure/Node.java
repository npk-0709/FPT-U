package datastructure;

import model.Product;

/**
 *
 * @author tranvotiendo
 */
public class Node {

    private Product data;
    private Node left;
    private Node right;

    public Node(Product data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public Product getData() {
        return data;
    }

    public void setData(Product data) {
        this.data = data;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

}
