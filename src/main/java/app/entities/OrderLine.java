package app.entities;

public class OrderLine {
    private int topId;
    private int bottomId;
    private int quantity;
    private int price;
    private int orderId;
    private int olID;

    public OrderLine(int topID, int botID, int quantity, int price, int orderId) {
        this.topId = topID;
        this.bottomId = botID;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
    }

    public OrderLine(int olID, int topID, int botID, int price, int quantity, int orderId) {
        this.olID = olID;
        this.topId = topID;
        this.bottomId = botID;
        this.quantity = quantity;
        this.price = price;
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

    public int getOlID() {
        return this.olID;
    }

    public OrderLine getOrderedLine() {
        return this;
    }


    @Override
    public String toString() {
        return "OrderLine{" +
                "topId=" + topId +
                ", bottomId=" + bottomId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderId=" + orderId +
                ", olID=" + olID +
                '}';
    }
}
