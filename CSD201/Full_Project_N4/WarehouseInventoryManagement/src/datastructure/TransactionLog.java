package datastructure;

import java.util.ArrayList;
import java.util.List;
import model.StockTransaction;
import model.TransactionType;

/**
 *
 * @author tranvotiendo
 */
public class TransactionLog {

    private TransactionNode head;
    private TransactionNode tail;
    private int size;

    public TransactionLog() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public TransactionLog(TransactionNode head, TransactionNode tail, int size) {
        this.head = head;
        this.tail = tail;
        this.size = size;
    }

    public TransactionNode getHead() {
        return head;
    }

    public void setHead(TransactionNode head) {
        this.head = head;
    }

    public TransactionNode getTail() {
        return tail;
    }

    public void setTail(TransactionNode tail) {
        this.tail = tail;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return head == null && tail == null;
    }

    public void recordTransaction(StockTransaction transaction) {
        TransactionNode newNode = new TransactionNode(transaction);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public List<StockTransaction> getAllTransactions() {
        List<StockTransaction> result = new ArrayList<>();
        if (isEmpty()) {
            return null;
        }
        TransactionNode tmp = head;
        while (tmp != null) {
            result.add(tmp.getData());
            tmp = tmp.getNext();
        }
        return result;
    }

    public List<StockTransaction> getTransactionsByProduct(String barcode) {
        List<StockTransaction> result = new ArrayList<>();
        if (isEmpty()) {
            return null;
        }
        TransactionNode tmp = head;
        while (tmp != null) {
            if (tmp.getData().getBarcode().equalsIgnoreCase(barcode)) {
                result.add(tmp.getData());
            }
            tmp = tmp.getNext();
        }
        return result;
    }

    public int calculateTotalByType(String barcode, TransactionType type) {
        if(isEmpty()){
            return 0;
        }
        if(barcode==null || barcode.isEmpty()){
            return 0;
        }
        int count = 0;
        TransactionNode tmp = head;
        while(tmp!=null){
            if(tmp.getData().getBarcode().equalsIgnoreCase(barcode) && tmp.getData().getType() == type){
                count += tmp.getData().getQuantity();
            }
            tmp = tmp.getNext();
        }
        return count;
    }
}
