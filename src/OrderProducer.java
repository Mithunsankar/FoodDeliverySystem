// CONCEPT 2 — Runnable interface
// CONCEPT 7 — Thread priority
// CONCEPT 9 — Thread.sleep()
public class OrderProducer implements Runnable {

    private static final String[][] MENU = {
        {"Alice",   "Margherita Pizza",  "12.99"},
        {"Bob",     "Chicken Biryani",   "10.49"},
        {"Carol",   "Vegan Burger",      "11.00"},
        {"David",   "Pasta Carbonara",   "13.50"},
        {"Eva",     "Sushi Platter",     "18.00"},
        {"Frank",   "BBQ Ribs",          "16.75"},
        {"Grace",   "Caesar Salad",       "8.25"},
        {"Harry",   "Fish & Chips",      "11.99"},
        {"Irene",   "Pad Thai",          "10.00"},
        {"Jake",    "Beef Tacos",         "9.50"},
    };

    private final OrderQueue queue;
    private final int        ordersToPlace;

    public OrderProducer(OrderQueue queue, int ordersToPlace) {
        this.queue         = queue;
        this.ordersToPlace = ordersToPlace;
    }

    @Override
    public void run() {
        System.out.println("[PRODUCER] Order producer started — will place " + ordersToPlace + " orders");
        try {
            for (int i = 0; i < ordersToPlace; i++) {
                String[] entry    = MENU[i % MENU.length];
                Order    order    = new Order(entry[0], entry[1], Double.parseDouble(entry[2]));
                queue.enqueue(order);
                Thread.sleep(300 + (long)(Math.random() * 400));   // pace the orders
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[PRODUCER] All orders placed.");
    }
}
