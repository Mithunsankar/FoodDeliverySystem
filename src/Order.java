public class Order {
    public enum Status { PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }

    private static int counter = 0;

    private final int orderId;
    private final String customerName;
    private final String itemName;
    private final double price;
    private volatile Status status;

    public Order(String customerName, String itemName, double price) {
        this.orderId      = ++counter;
        this.customerName = customerName;
        this.itemName     = itemName;
        this.price        = price;
        this.status       = Status.PLACED;
    }

    public int    getOrderId()       { return orderId; }
    public String getCustomerName()  { return customerName; }
    public String getItemName()      { return itemName; }
    public double getPrice()         { return price; }
    public Status getStatus()        { return status; }
    public void   setStatus(Status s){ this.status = s; }

    @Override
    public String toString() {
        return String.format("Order#%02d [%s] %-20s $%.2f  <%s>",
                orderId, customerName, itemName, price, status);
    }
}
