// CONCEPT 1 — Thread class extension
// CONCEPT 6 — Daemon thread (background monitor; dies when non-daemon threads finish)
public class OrderMonitorThread extends Thread {

    private final Restaurant restaurant;
    private final int        intervalMs;

    public OrderMonitorThread(Restaurant restaurant, int intervalMs) {
        super("OrderMonitor");
        this.restaurant  = restaurant;
        this.intervalMs  = intervalMs;
        setDaemon(true);          // JVM will not wait for this thread on exit
    }

    @Override
    public void run() {
        System.out.println("[MONITOR] Daemon monitor started — tracking " + restaurant.getName());
        try {
            while (true) {                                 // runs until JVM exits
                Thread.sleep(intervalMs);
                System.out.printf("%n[MONITOR] === %s | Orders: %d | Revenue: $%.2f ===%n%n",
                        restaurant.getName(),
                        restaurant.getTotalOrdersAccepted(),
                        restaurant.getTotalRevenue());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
