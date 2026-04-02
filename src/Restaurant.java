// CONCEPT 3 & 4 — Synchronization (intrinsic lock on shared mutable state)
public class Restaurant {

    private final String name;
    private int         totalOrdersAccepted;
    private double      totalRevenue;

    public Restaurant(String name) {
        this.name = name;
    }

    // synchronized → only one thread enters at a time; prevents race conditions
    public synchronized boolean acceptOrder(Order order) {
        if (order.getPrice() <= 0) return false;
        totalOrdersAccepted++;
        totalRevenue += order.getPrice();
        order.setStatus(Order.Status.PREPARING);
        System.out.printf("[RESTAURANT] %-12s accepted  %s%n", name, order);
        return true;
    }

    public synchronized void cancelOrder(Order order) {
        if (order.getStatus() == Order.Status.PLACED ||
            order.getStatus() == Order.Status.PREPARING) {
            totalRevenue -= order.getPrice();
            order.setStatus(Order.Status.CANCELLED);
            System.out.printf("[RESTAURANT] %-12s cancelled %s%n", name, order);
        }
    }

    // synchronized read — consistent view of revenue
    public synchronized double getTotalRevenue()       { return totalRevenue; }
    public synchronized int    getTotalOrdersAccepted(){ return totalOrdersAccepted; }
    public            String   getName()               { return name; }
}
