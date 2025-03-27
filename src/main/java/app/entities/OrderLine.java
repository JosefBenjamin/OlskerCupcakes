package app.entities;

public class OrderLine {
    private final int topId;
    private final int bottomId;
    private final int quantity;
    private final int price;
    private final int orderId;
    private final int orderLineId;

    OrderLine(int ol_id, int topID, int bottomID, int quantity, int ol_price, int orderId) {
        this.orderLineId = ol_id;
        this.topId = topID;
        this.bottomId = bottomID;
        this.quantity = quantity;
        this.price = ol_price;
        this.orderId = orderId;
    }

    public int getTopId() {
        return this.topId;
    }

    public int getBottomId() {
        return this.bottomId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getPrice() {
        return this.price;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderLine getOrderedLine() {
        return this;
    }

    @Override
    public String toString() {
        String result;
        result = "Top ID: " + this.topId + " \nBottom ID: " + this.bottomId + "\nQuantity: " + this.quantity + "\nPrice: " + this.price + "\nOrder ID: " + this.orderId;
        return result;
    }
}
