package app.entities;

import java.sql.Date;
import java.sql.Timestamp;

public class Order {
    private int         orderId;
    private int         userId;
    private int         price;
    private Timestamp   time;
    private boolean     isDone;
    private int         orderLineId;

    public Order(int orderId,
                 int price,
                 Timestamp time,
                 int userID) {

        this.orderId = orderId;
        this.price = price;
        this.time = time;
        this.isDone = false;
        this.userId = userID;
        this.orderLineId = orderLineId;
    }

    public void changeIsDoneStatus(){
        this.isDone = !this.isDone;
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

    public int getOrderLineId() {
        return orderLineId;
    }

    @Override
    public String toString() {
        String result;
        result = "Order ID: " + this.orderId + " \nPrice: " + this.price + "\nTime: " + this.time + "\nIs done: " + this.isDone + "\nUser ID: " + this.userId + "\nOrder line ID: " + this.orderLineId;
        return result;
    }
}
