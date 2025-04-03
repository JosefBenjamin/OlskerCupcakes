package app.entities;

public class OrderLine {
    private int topId;
    private int bottomId;
    private int quantity;
    private int price;
    private int orderId;
    private int userID;

    public OrderLine(int topID,
                     int botID,
                     int quantity,
                     int userID,
                     int orderId) {
        this.topId = topID;
        this.bottomId = botID;
        this.quantity = quantity;
        this.userID = userID;
        this.orderId = orderId;
    }

    public OrderLine(int topID,
                     int botID,
                     int quantity,
                     int price) {
        this.topId = topID;
        this.bottomId = botID;
        this.quantity = quantity;
        this.price = price;
    }

    public int getUserID() {
        return userID;
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
