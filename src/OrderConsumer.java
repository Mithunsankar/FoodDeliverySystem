// CONCEPT 2 — Runnable interface
// CONCEPT 4 — wait() via OrderQueue.dequeue()
// CONCEPT 9 — Thread.sleep()
public class OrderConsumer implements Runnable {

    private final String       id;
    private final OrderQueue   queue;
    private final Restaurant   restaurant;
    private volatile boolean   running = true;

    public OrderConsumer(String id, OrderQueue queue, Restaurant restaurant) {
        this.id         = id;
        this.queue      = queue;
        this.restaurant = restaurant;
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        System.out.printf("[%s] Consumer started%n", id);
        try {
            while (running) {
                Order order = queue.dequeue();
                System.out.printf("[%s] Processing %s%n", id, order);
                restaurant.acceptOrder(order);
                Thread.sleep(400 + (long)(Math.random() * 600));   // simulate processing
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("[%s] Consumer shutting down.%n", id);
    }
}
