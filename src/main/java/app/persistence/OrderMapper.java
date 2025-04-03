package app.persistence;

import app.entities.Order;
import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class OrderMapper {

    public static void createOrder(int UserID, ConnectionPool pool)throws DatabaseException{

    }

    public static Order getAnOrderByEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders " +
                "JOIN users ON orders.user_id = users.user_id " +
                "WHERE email =?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);


            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                int price = rs.getInt("total_price");
                Timestamp time = rs.getTimestamp("order_date");
                boolean isDone = rs.getBoolean("is_done");
                return new Order(orderId, userId, price, time, isDone);
            } else {
                throw new DatabaseException("Failed to load an order!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Something wrong with the database", e.getCause());
        }
    }

    public static Order getOrderByOrderLineID(int orderID,
                                              ConnectionPool pool) throws DatabaseException {
        //Local attribute
        Order result = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                int userID = rs.getInt("user_id");
                result = new Order(orderID, price, date, userID);
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }

    public static Order getUncompletedOrderByUserID(int userID, ConnectionPool pool) throws DatabaseException {
        //Local attribute
        Order result = null;
        String sql = "SELECT test.orderline.top_id, " +
                            "test.orderline.bot_id, " +
                            "test.orderline.quantity, " +
                            "test.orderline.order_id, " +
                            "test.orderline.user_id "+
                            "FROM test.orders " +
                            "JOIN orders ON test.orderline.order_id = orders.order_id " +
                            "JOIN users ON test.orderline.user_id = users.user_id " +
                            "WHERE user_id = ? AND is_done=false ";

        try (Connection con       = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                result = new Order(userID, date, price);
            } else {
                return getUncompletedOrderByUserID(userID, pool);
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }

    public static int getOrderPriceByOrderID(int orderID, ConnectionPool pool){
        int result = 0;
        return result;
    }

    public static Order getLatestOrderByUserID(int userID,
                                               ConnectionPool pool) throws DatabaseException {
        // Local attribute
        Order result = null;
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                int orderID = rs.getInt("order_id");
                result = new Order(orderID, price, date, userID);
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }

    public List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {

        List<Order> orderList = new ArrayList<>();

        String sql = "SELECT * FROM orders " +
                "JOIN users ON orders.user_id = users.user_id " +
                "ORDER BY user_id";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                int price = rs.getInt("total_price");
                Timestamp time = rs.getTimestamp("order_date");
                boolean isDone = rs.getBoolean("is_done");
                orderList.add(new Order(userId, userId, price, time, isDone));
            }
        } catch (SQLException e) {
            String msg = "An error occurred try again!";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The email is already in use, please use another or login.";
            }
            throw new DatabaseException(msg, e.getCause());
        }
        return orderList;
    }


    public List<Order> getAllOrdersByUserID(int userID, ConnectionPool pool) throws DatabaseException {
        // Local attributes
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                result.add(new Order(orderID, price, date, userID));
            } // while
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        } // catch
        return result;
    }

    public List<Order> getAllNotDoneOrders(ConnectionPool pool) throws DatabaseException {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE is_done = false ORDER BY order_date DESC";
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                int userID = rs.getInt("user_id");
                result.add(new Order(orderID, price, date, userID));
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }

    public List<Order> getAllDoneOrders(ConnectionPool pool) throws DatabaseException {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE is_done = true ORDER BY order_date DESC";
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                int price = rs.getInt("total_price");
                Timestamp date = rs.getTimestamp("order_date");
                int userID = rs.getInt("user_id");
                result.add(new Order(orderID, price, date, userID));
            }
        } catch (SQLException exc) {
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }
}
