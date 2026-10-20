package datastructure;

import java.util.ArrayList;
import java.util.List;
import model.Product;

/**
 *
 * @author Group4_SE2023
 */
public class ProductBST {

    private Node root;

    public ProductBST() {
        this.root = null;
    }

    public ProductBST(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public boolean insert(Product product) {
        if(product == null || product.getBarcode() == null || product.getBarcode().trim().isEmpty()){
            return false;
        }
        if(search(product.getBarcode())!=null){
            return false;
        }
        root = insertSupport(root, product);
        return true;
    }

    private Node insertSupport(Node current, Product product) {
        // TODO: Hàm đệ quy hỗ trợ insert
        if (current == null) {
            return new Node(product);
        }
        int compareBarcode = current.getData().getBarcode().compareToIgnoreCase(product.getBarcode());
        if (compareBarcode > 0) {
            current.setLeft(insertSupport(current.getLeft(), product));
        } else if (compareBarcode < 0) {
            current.setRight(insertSupport(current.getRight(), product));
        }
        return current;
    }

    public Product search(String barcode) {
        if(barcode == null || barcode.trim().isEmpty()){
            return null;
        }
        Node node = searchSupport(root, barcode);
        if(node == null){
            return null;
        }
        return node.getData();
    }

    private Node searchSupport(Node current, String barcode) {
        if(current == null){
            return null;
        }
        int compareBarcode = current.getData().getBarcode().compareToIgnoreCase(barcode);
        if(compareBarcode>0){
            return searchSupport(current.getLeft(), barcode);
        }else if(compareBarcode<0){
            return searchSupport(current.getRight(), barcode);
        }
        return current;
    }

    public boolean delete(String barcode) {
        // TODO: Xóa sản phẩm khỏi BST theo barcode
        if(barcode==null || barcode.isEmpty()){
            return false;
        }
        if(search(barcode)==null){
            return false;
        }
        root = deleteSupport(root, barcode);
        return true;
    }

    private Node deleteSupport(Node current, String barcode) {
        if(current == null){
            return null;
        }
        int compareBarcode = current.getData().getBarcode().compareToIgnoreCase(barcode);
        if(compareBarcode>0){
            current.setLeft(deleteSupport(current.getLeft(), barcode));
        }
        else if(compareBarcode<0){
            current.setRight(deleteSupport(current.getRight(), barcode));
        }else{
            if(current.getRight()==null){
                return current.getLeft();
            }
            if(current.getLeft()==null){
                return current.getRight();
            }
            Node min = current.getRight();
            while (min.getLeft()!=null){
                min = min.getLeft();
            }
            current.setData(min.getData());
            current.setRight(deleteSupport(current.getRight(), min.getData().getBarcode()));
        }
        return current;
    }

   public List<Product> inOrderList(){
       List<Product> result = new ArrayList<>();
       inOrderSupport(root, result);
       return result;
   }
   public void inOrderSupport(Node current, List<Product> result){
       if(current == null){
           return;
       }
       inOrderSupport(current.getLeft(), result);
       result.add(current.getData());
       inOrderSupport(current.getRight(), result);
   }

    public boolean isEmpty() {
        return root == null;
    }
}
