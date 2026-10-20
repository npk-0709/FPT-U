package datastructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Batch;

/**
 *
 * @author Group4_SE2023
 */
public class BatchQueue {

    private BatchNode front;
    private BatchNode rear;
    private int size;

    public BatchQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public BatchQueue(BatchNode front, BatchNode rear, int size) {
        this.front = front;
        this.rear = rear;
        this.size = size;
    }

    public BatchNode getFront() {
        return front;
    }

    public void setFront(BatchNode front) {
        this.front = front;
    }

    public BatchNode getRear() {
        return rear;
    }

    public void setRear(BatchNode rear) {
        this.rear = rear;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void enqueue(Batch batch) {
        // addTail
        BatchNode newNode = new BatchNode(batch);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    public Batch dequeue() {
        if (isEmpty()) {
            return null;
        }
        BatchNode tmp = front;
        if (front == rear) {
            front = null;
            rear = null;
        } else {
            front = front.getNext();
        }
        size--;
        return tmp.getData();
    }

    public Batch peek() {
        if (isEmpty()) {
            return null;
        }
        return front.getData();
    }

    public boolean isEmpty() {
        return front == null && rear == null;
    }

    public int getTotalQuantityInQueue() {
        // count tong so quantity co trong Queue
        int count = 0;
        if (isEmpty()) {
            return 0;
        }
        BatchNode tmp = front;
        while (tmp != null) {
            count += tmp.getData().getQuantity();
            tmp = tmp.getNext();
        }
        return count;
    }

    public List<Batch> removeExpired(LocalDate today) {
        // Tra ve danh sach cac lo hang bi loai bo 
        List<Batch> removedList = new ArrayList<>();
        if (isEmpty()) {
            return null;
        }
        BatchNode prev = null;
        BatchNode current = front;
        while (current != null) {
            BatchNode nextNode = current.getNext();
            if (current.getData().isExpired(today)) {
                removedList.add(current.getData());
                if (prev == null) {
                    front = nextNode;
                } else {
                    prev.setNext(nextNode);
                }
                if (current == rear) {
                    rear = prev;
                }
                size--;
                current = nextNode;
            } else {
                prev = current;
                current = nextNode;
            }
        }
        return removedList;
    }
}
