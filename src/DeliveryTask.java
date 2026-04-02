// CONCEPT 2 — Runnable interface implementation
// CONCEPT 9 — Thread.sleep() to simulate delivery travel time
public class DeliveryTask implements Runnable {

    private final Order      order;
    private final Restaurant restaurant;

    public DeliveryTask(Order order, Restaurant restaurant) {
        this.order      = order;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            restaurant.acceptOrder(order);

            // Simulate kitchen prep time
            Thread.sleep(500 + (long)(Math.random() * 800));
            order.setStatus(Order.Status.OUT_FOR_DELIVERY);
            System.out.printf("[DELIVERY]   %-20s is OUT_FOR_DELIVERY  (thread: %s)%n",
                    order.getItemName(), Thread.currentThread().getName());

            // Simulate road travel time
            Thread.sleep(600 + (long)(Math.random() * 1000));
            order.setStatus(Order.Status.DELIVERED);
            System.out.printf("[DELIVERED]  %-20s delivered to %-10s  (thread: %s)%n",
                    order.getItemName(), order.getCustomerName(),
                    Thread.currentThread().getName());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.printf("[INTERRUPTED] Delivery interrupted for %s%n", order.getItemName());
        }
    }
}
