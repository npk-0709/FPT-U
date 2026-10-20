package datastructure;

import model.Batch;

/**
 *
 * @author Group4_SE2023
 */
public class BatchNode {

    private Batch data;
    private BatchNode next;

    // TODO: Constructors, Getters & Setters

    public BatchNode() {
        this.data = null;
        this.next = null;
    }
    
    public BatchNode(Batch data){
        this.data = data;
        this.next = null;
    }

    public Batch getData() {
        return data;
    }

    public void setData(Batch data) {
        this.data = data;
    }

    public BatchNode getNext() {
        return next;
    }

    public void setNext(BatchNode next) {
        this.next = next;
    }

    
}
