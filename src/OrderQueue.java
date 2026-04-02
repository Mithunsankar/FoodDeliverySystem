import java.util.LinkedList;
import java.util.Queue;

// CONCEPT 4 — wait() / notify() for Producer-Consumer coordination
public class OrderQueue {

    private final Queue<Order> queue;
    private final int          maxSize;

    public OrderQueue(int maxSize) {
        this.queue   = new LinkedList<>();
        this.maxSize = maxSize;
    }

    // Called by Producer — blocks when queue is full
    public synchronized void enqueue(Order order) throws InterruptedException {
        while (queue.size() >= maxSize) {
            System.out.println("[QUEUE] Full — producer waiting...");
            wait();                           // releases lock; reacquires on notify
        }
        queue.add(order);
        System.out.printf("[QUEUE]  +enqueued  %s  (size: %d)%n", order, queue.size());
        notifyAll();                          // wake up waiting consumers
    }

    // Called by Consumer — blocks when queue is empty
    public synchronized Order dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        Order order = queue.poll();
        System.out.printf("[QUEUE]  -dequeued  %s  (size: %d)%n", order, queue.size());
        notifyAll();                          // wake up waiting producers
        return order;
    }

    public synchronized int size() { return queue.size(); }
}
