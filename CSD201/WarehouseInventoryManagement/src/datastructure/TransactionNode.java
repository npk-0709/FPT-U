package datastructure;

import model.StockTransaction;

/**
 *
 * @author Group4_SE2023
 */
public class TransactionNode {

    private StockTransaction data;
    private TransactionNode next;

    public TransactionNode() {
        this.data = null;
        this.next = null;
    }

    public TransactionNode(StockTransaction data) {
        this.data = data;
        this.next = null;
    }
    
    public TransactionNode(StockTransaction data, TransactionNode next) {
        this.data = data;
        this.next = next;
    }

    public StockTransaction getData() {
        return data;
    }

    public void setData(StockTransaction data) {
        this.data = data;
    }

    public TransactionNode getNext() {
        return next;
    }

    public void setNext(TransactionNode next) {
        this.next = next;
    }
    
}
