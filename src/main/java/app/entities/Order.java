package app.entities;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Order {
    private int orderId;
    private int userId;
    private int price;
    private Timestamp time;
    private boolean isDone;

    public Order(int orderId,
                 int price,
                 Timestamp time,
                 int userID) {

        this.orderId = orderId;
        this.price = price;
        this.time = time;
        this.isDone = false;
        this.userId = userID;
    }

    public Order(int orderId, int userId, int price, Timestamp time, boolean isDone) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.time = time;
        this.isDone = isDone;
    }

    public Order(int userID, Timestamp time, int price) {
        this.userId = userID;
        this.time = time;
        this.price = price;
        this.isDone = false;
    }

    public void setPrice(ArrayList<OrderLine> orderLine) {
        int totalPrice = 0;
        for(OrderLine element : orderLine){
            totalPrice += element.getPrice();
        }
    }

    public void flipDone() {
        isDone = !isDone;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPrice() {
        return price;
    }

    public Timestamp getTimestamp() {
        return time;
    }

    public boolean isDoneStatus() {
        return isDone;
    }

    @Override
    public String toString() {
        String result;
        result = "Order ID: " + this.orderId + " \nPrice: " + this.price + "\nTime: " + this.time + "\nIs done: " + this.isDone + "\nUser ID: " + this.userId;
        return result;
    }
}
