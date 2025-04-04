package app.entities;

public class OrderLine {
    private int topId;
    private int bottomId;
    private int quantity;
    private int price;
    private int orderId;
    private String topName;
    private String bottomName;

    // We also store the actual objects (if needed)
    private CakeBottom bottom;
    private CakeTop topping;

    // Constructor if you only have IDs
    public OrderLine(int topID, int botID, int quantity, int price, int orderId) {
        this.topId = topID;
        this.bottomId = botID;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
    }

    // Constructor if you have actual objects
    public OrderLine(CakeBottom bottom, CakeTop topping, int quantity, int price, int orderId) {
        this.bottom = bottom;
        this.topping = topping;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;

        // If bottom or topping is non-null, set ID and name
        if (this.bottom != null) {
            this.bottomId = this.bottom.getId();         // or however you get bottom's ID
            this.bottomName = this.bottom.getName();     // or however you get bottom's name
        }
        if (this.topping != null) {
            this.topId = this.topping.getId();           // or however you get topping's ID
            this.topName = this.topping.getName();       // or however you get topping's name
        }
    }

    // Topping & Bottom
    public CakeTop getTopping() {
        return topping;
    }

    public void setTopping(CakeTop topping) {
        this.topping = topping;
        if (topping != null) {
            this.topId = topping.getId();       // update ID
            this.topName = topping.getName();   // update name
        }
    }

    public CakeBottom getBottom() {
        return bottom;
    }

    public void setBottom(CakeBottom bottom) {
        this.bottom = bottom;
        if (bottom != null) {
            this.bottomId = bottom.getId();        // update ID
            this.bottomName = bottom.getName();    // update name
        }
    }

    // ID and quantity
    public int getTopId() {
        return this.topId;
    }

    public int getBottomId() {
        return this.bottomId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Price
    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Order ID
    public int getOrderId() {
        return this.orderId;
    }

    // Names
    public String getTopName() {
        return topName;
    }

    public void setTopName(String topName) {
        this.topName = topName;
    }

    public String getBottomName() {
        return bottomName;
    }

    public void setBottomName(String bottomName) {
        this.bottomName = bottomName;
    }

    // Possibly used for debugging
    public OrderLine getOrderedLine() {
        return this;
    }

    @Override
    public String toString() {
        return "Top ID: " + this.topId +
               " | Bottom ID: " + this.bottomId +
               " | Quantity: " + this.quantity +
               " | Price: " + this.price +
               " | Order ID: " + this.orderId +
               " | Top Name: " + (topName != null ? topName : "null") +
               " | Bottom Name: " + (bottomName != null ? bottomName : "null");
    }
}
