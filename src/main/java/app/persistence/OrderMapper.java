package app.persistence;

import app.entities.Order;
import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class OrderMapper {


    public static Order getAnOrder(String email, ConnectionPool connectionPool) throws DatabaseException {
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


    public List<Order> getAllOrdersByUserID(int userID,
                                            ConnectionPool pool) throws DatabaseException {
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

    public static void createOrder(int userID, int price, ArrayList <OrderLine> orderLines, ConnectionPool connectionPool) throws DatabaseException {
                String sqlOrder     = "INSERT INTO public.\"orders\" (user_id, total_price) values (?,?) RETURNING order_id";
                String sqlOrderLine = "INSERT INTO public.\"orderline\" (order_id, bot_id, top_id, quantity, ol_price) VALUES (?,?,?,?,?)";
        try (   Connection connection = connectionPool.getConnection();
                PreparedStatement psO = connection.prepareStatement(sqlOrder);
                PreparedStatement psOL = connection.prepareStatement(sqlOrderLine)) {

            psO.setInt(1, userID);
            psO.setInt(2,price);
            ResultSet rs = psO.executeQuery();
            if (rs.next()){
                int orderID = rs.getInt("order_id");
                for (OrderLine element : orderLines){
                    psOL.setInt(1,orderID);
                    psOL.setInt(2,element.getBottomId());
                    psOL.setInt(3,element.getTopId());
                    psOL.setInt(4,element.getQuantity());
                    psOL.setInt(5,element.getPrice());
                    psOL.addBatch();
                }
                psOL.executeBatch();
            } else {
                     throw new DatabaseException("Unable to create an Order");
            }
        } catch (SQLException e) {
            String msg = "An error occurred try again!";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The Order is already in use, please use another or login.";
            }
            throw new DatabaseException(msg, e.getCause());
        }
    }
}
