
public class Main {
    public static void main(String[] args) {
        // test queue single linked list
//        QueueSingleLinkedList queue = new QueueSingleLinkedList();
//        queue.enqueue(1);
//        queue.enqueue(2);
//        queue.enqueue(3);
//        System.out.println(queue.dequeue().getValue()); // 1
//        System.out.println(queue.dequeue().getValue()); // 2
//        System.out.println(queue.dequeue().getValue()); // 3

        // test queue doubly linked list
        QueueDoublyLinkedList queue = new QueueDoublyLinkedList();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        System.out.println(queue.dequeue().getValue()); // 1
        System.out.println(queue.dequeue().getValue()); // 2
        System.out.println(queue.dequeue().getValue()); // 3
    }
}