package app.entities;

public class OrderLine {
    private  int topId;
    private  int bottomId;
    private  int quantity;
    private  int price;
    private  int orderId;
    private  int orderLineId;

    public OrderLine(int ol_id, int topID, int bottomID, int quantity, int ol_price, int orderId) {
        this.orderLineId = ol_id;
        this.topId = topID;
        this.bottomId = bottomID;
        this.quantity = quantity;
        this.price = ol_price;
        this.orderId = orderId;
    }

    public OrderLine(int topID, int bottomID, int quantity, int ol_price, int orderID){
        this.topId = topID;
        this.bottomId = bottomID;
        this.quantity = quantity;
        this.price = ol_price;
        this.orderId = orderID;
        this.orderLineId = -1;
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

    public int getOrderLineId() {
        return this.orderLineId;
    }

    public OrderLine getOrderedLine() {
        return this;
    }

    @Override
    public String toString() {
        String result;
        result = "Top ID: " + this.topId + " \nBottom ID: " + this.bottomId + "\nQuantity: " + this.quantity + "\nPrice: " + this.price + "\nOrder ID: " + this.orderId + "\nOrder Line ID: " + this.orderLineId;
        if (orderLineId == -1){
            result = "Top ID: " + this.topId + " \nBottom ID: " + this.bottomId + "\nQuantity: " + this.quantity + "\nPrice: " + this.price;
        }
        return result;
    }
}
