import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// CONCEPT 5 — Thread pool via ExecutorService
public class DeliveryManager {

    private final ExecutorService pool;
    private final Restaurant      restaurant;

    public DeliveryManager(int poolSize, Restaurant restaurant) {
        this.pool       = Executors.newFixedThreadPool(poolSize);
        this.restaurant = restaurant;
        System.out.printf("[MANAGER] DeliveryManager initialized — pool size: %d%n", poolSize);
    }

    public void dispatch(List<Order> orders) {
        for (Order order : orders) {
            pool.submit(new DeliveryTask(order, restaurant));
            System.out.printf("[MANAGER] Dispatched delivery task for Order#%02d%n", order.getOrderId());
        }
    }

    public void shutdown() throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }
        System.out.println("[MANAGER] All delivery threads finished.");
    }
}
