
import java.util.ArrayList;
import java.util.List;

public class ProductBST {

    private static class Node {
        Product product;
        Node left, right;

        Node(Product product) {
            this.product = product;
            this.left  = null;
            this.right = null;
        }
    }

    private Node root;
    private int size;

    public ProductBST() {
        this.root = null;
        this.size = 0;
    }

    public boolean insert(Product product) {
        if (search(product.getBarcode()) != null) {
            System.out.println("  [BST] Barcode '" + product.getBarcode() + "' da ton tai!");
            return false;
        }
        root = insertRec(root, product);
        size++;
        return true;
    }

    private Node insertRec(Node node, Product product) {
        if (node == null) return new Node(product);
        int cmp = product.getBarcode().compareTo(node.product.getBarcode());
        if      (cmp < 0) node.left  = insertRec(node.left,  product);
        else if (cmp > 0) node.right = insertRec(node.right, product);
        return node;
    }

    public Product search(String barcode) {
        return searchRec(root, barcode);
    }

    private Product searchRec(Node node, String barcode) {
        if (node == null) return null;
        int cmp = barcode.compareTo(node.product.getBarcode());
        if (cmp == 0) return node.product;
        if (cmp < 0)  return searchRec(node.left,  barcode);
        return            searchRec(node.right, barcode);
    }

    public boolean delete(String barcode) {
        if (search(barcode) == null) {
            System.out.println("  [BST] Khong tim thay barcode '" + barcode + "'!");
            return false;
        }
        root = deleteRec(root, barcode);
        size--;
        return true;
    }

    private Node deleteRec(Node node, String barcode) {
        if (node == null) return null;
        int cmp = barcode.compareTo(node.product.getBarcode());
        if (cmp < 0) {
            node.left  = deleteRec(node.left,  barcode);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, barcode);
        } else {
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = findMin(node.right);
            node.product   = successor.product;
            node.right     = deleteRec(node.right, successor.product.getBarcode());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void inOrderDisplay() {
        System.out.println("  +------------------------------------------------------------------+");
        System.out.println("  |       DANH MUC SAN PHAM (Sap xep theo ma vach)                  |");
        System.out.println("  +------------------------------------------------------------------+");
        System.out.printf("  | %-10s | %-22s | %-12s | %-12s |\n",
                "MA VACH", "TEN SAN PHAM", "DANH MUC", "GIA");
        System.out.println("  +------------------------------------------------------------------+");
        if (root == null) {
            System.out.println("  | (Danh muc trong)                                                 |");
        } else {
            inOrderRec(root);
        }
        System.out.println("  +------------------------------------------------------------------+");
        System.out.println("  Tong so san pham: " + size);
    }

    private void inOrderRec(Node node) {
        if (node == null) return;
        inOrderRec(node.left);
        System.out.printf("  | %s |\n", node.product.toString());
        inOrderRec(node.right);
    }

    public List<Product> searchByCategory(String category) {
        List<Product> result = new ArrayList<>();
        searchByCategoryRec(root, category, result);
        return result;
    }

    private void searchByCategoryRec(Node node, String category, List<Product> result) {
        if (node == null) return;
        if (node.product.getCategory().equalsIgnoreCase(category)) {
            result.add(node.product);
        }
        searchByCategoryRec(node.left,  category, result);
        searchByCategoryRec(node.right, category, result);
    }

    public void displayTree() {
        System.out.println("  [BST] Cau truc cay:");
        if (root == null) {
            System.out.println("    (Cay rong)");
        } else {
            displayTreeRec(root, "", true);
        }
    }

    private void displayTreeRec(Node node, String prefix, boolean isLeft) {
        if (node == null) return;
        System.out.println(prefix + (isLeft ? "+-- " : "\\-- ")
                + node.product.getBarcode() + " [" + node.product.getName() + "]");
        if (node.left != null || node.right != null) {
            displayTreeRec(node.left,  prefix + (isLeft ? "|   " : "    "), true);
            displayTreeRec(node.right, prefix + (isLeft ? "|   " : "    "), false);
        }
    }

    public int getSize()   { return size; }
    public boolean isEmpty(){ return root == null; }
}
