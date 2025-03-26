package app.entities;

public class OrderLine {
    private int topId;
    private CakeTop cakeTop;
    private CakeBottom cakeBottom;
    private int bottomId;
    private int quantity;
    private int price;
    private int orderId;

    OrderLine(CakeTop cakeTop, CakeBottom bottom, int quantity, int price, int orderId) {
        this.topId = cakeTop.getId();
        this.bottomId = cakeBottom.getId();
        this.cakeTop = cakeTop;
        this.cakeBottom = bottom;
        this.quantity = quantity;
        this.price = cakeTop.getPrice() + cakeBottom.getPrice();
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
