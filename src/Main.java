import java.util.ArrayList;
import java.util.List;

/*
 * Online Food Delivery System — Multithreading Demo
 * ==================================================
 * Concept Map:
 *  1. Thread extension         → OrderMonitorThread extends Thread
 *  2. Runnable interface       → DeliveryTask, OrderProducer, OrderConsumer implement Runnable
 *  3. Thread lifecycle         → NEW → RUNNABLE → TIMED_WAITING → TERMINATED (visible in logs)
 *  4. Synchronization          → Restaurant.acceptOrder/cancelOrder (synchronized methods)
 *  5. wait() / notify()        → OrderQueue.enqueue / dequeue
 *  6. Thread pool              → DeliveryManager uses ExecutorService.newFixedThreadPool
 *  7. Daemon thread            → OrderMonitorThread.setDaemon(true)
 *  8. Thread priority          → Producer = MAX_PRIORITY, Consumers = NORM_PRIORITY
 *  9. join()                   → Main waits for producer and consumers before final report
 * 10. Thread.sleep()           → Used throughout to simulate real-world timing
 */
public class Main {

    private static final int    TOTAL_ORDERS       = 10;
    private static final int    QUEUE_CAPACITY      = 5;
    private static final int    NUM_CONSUMERS       = 2;
    private static final int    DELIVERY_POOL_SIZE  = 3;
    private static final double INITIAL_REVENUE     = 0.0;

    public static void main(String[] args) throws InterruptedException {

        printBanner();

        // --- Shared resources ---
        Restaurant restaurant = new Restaurant("SpiceRoute");
        OrderQueue orderQueue = new OrderQueue(QUEUE_CAPACITY);

        // ── CONCEPT 7: Daemon Thread ──────────────────────────────────────────
        System.out.println("\n--- Starting Daemon Monitor Thread ---");
        OrderMonitorThread monitor = new OrderMonitorThread(restaurant, 4000);
        // setDaemon(true) already called inside constructor
        monitor.start();   // LIFECYCLE: NEW → RUNNABLE
        System.out.println("[MAIN] Monitor is daemon: " + monitor.isDaemon());

        // ── CONCEPT 8: Thread Priority ────────────────────────────────────────
        System.out.println("\n--- Starting Producer Thread (MAX_PRIORITY) ---");
        Thread producerThread = new Thread(new OrderProducer(orderQueue, TOTAL_ORDERS), "ProducerThread");
        producerThread.setPriority(Thread.MAX_PRIORITY);
        System.out.printf("[MAIN] Producer priority: %d%n", producerThread.getPriority());
        producerThread.start();  // LIFECYCLE: NEW → RUNNABLE

        // ── CONCEPT 2 & 8: Runnable + Priority ───────────────────────────────
        System.out.println("\n--- Starting Consumer Threads (NORM_PRIORITY) ---");
        List<Thread>        consumerThreads  = new ArrayList<>();
        List<OrderConsumer> consumerRunnables = new ArrayList<>();

        for (int i = 1; i <= NUM_CONSUMERS; i++) {
            OrderConsumer consumer = new OrderConsumer("CONSUMER-" + i, orderQueue, restaurant);
            Thread        t        = new Thread(consumer, "ConsumerThread-" + i);
            t.setPriority(Thread.NORM_PRIORITY);
            System.out.printf("[MAIN] Consumer-%d priority: %d%n", i, t.getPriority());
            consumerRunnables.add(consumer);
            consumerThreads.add(t);
            t.start();   // LIFECYCLE: NEW → RUNNABLE
        }

        // ── CONCEPT 5: Thread Pool for Deliveries ────────────────────────────
        System.out.println("\n--- Initializing Delivery Thread Pool ---");
        List<Order> directDeliveryOrders = new ArrayList<>();
        String[][] quickOrders = {
            {"ZomattoPlatform", "Garlic Bread",    "5.99"},
            {"ZomattoPlatform", "Cold Coffee",     "4.50"},
            {"ZomattoPlatform", "Cheese Fries",    "6.25"},
            {"ZomattoPlatform", "Mango Lassi",     "3.75"},
            {"ZomattoPlatform", "Spring Rolls",    "7.00"},
        };
        for (String[] o : quickOrders) {
            directDeliveryOrders.add(new Order(o[0], o[1], Double.parseDouble(o[2])));
        }

        DeliveryManager deliveryManager = new DeliveryManager(DELIVERY_POOL_SIZE, restaurant);
        deliveryManager.dispatch(directDeliveryOrders);

        // ── CONCEPT 9: join() — wait for producer to finish ──────────────────
        System.out.println("\n[MAIN] Waiting for producer to finish placing all orders (join)...");
        producerThread.join();   // LIFECYCLE: main → WAITING until producerThread TERMINATES
        System.out.println("[MAIN] Producer finished.");

        // Allow consumers a bit of time to drain the queue after producer stops
        Thread.sleep(3000);

        // Signal consumers to stop after queue is drained
        consumerRunnables.forEach(OrderConsumer::stop);
        for (Thread ct : consumerThreads) {
            ct.interrupt();
            ct.join();           // wait for each consumer to TERMINATE
        }
        System.out.println("[MAIN] All consumers finished.");

        // Wait for all delivery pool tasks to complete
        deliveryManager.shutdown();

        // Monitor daemon auto-dies here since all non-daemon threads are done

        printFinalReport(restaurant);
    }

    // ── CONCEPT 3: Thread Lifecycle visible via Thread.State ─────────────────
    private static void printBanner() {
        System.out.println("=".repeat(70));
        System.out.println("    ONLINE FOOD DELIVERY SYSTEM — MULTITHREADING DEMO");
        System.out.println("=".repeat(70));
        System.out.printf("[MAIN] Running on thread: %-12s  Priority: %d  State: %s%n",
                Thread.currentThread().getName(),
                Thread.currentThread().getPriority(),
                Thread.currentThread().getState());   // RUNNABLE
    }

    private static void printFinalReport(Restaurant r) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                        FINAL REPORT");
        System.out.println("=".repeat(70));
        System.out.printf("  Restaurant       : %s%n",   r.getName());
        System.out.printf("  Orders Accepted  : %d%n",   r.getTotalOrdersAccepted());
        System.out.printf("  Total Revenue    : $%.2f%n", r.getTotalRevenue());
        System.out.println("=".repeat(70));
    }
}
