package app.persistence;

import app.entities.CakeBottom;
import app.entities.CakeTop;
import app.entities.Order;
import app.entities.User;
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

    private static int createNewOrder(Connection connection, int userId) throws SQLException{

        String sql = "INSTERT INTO orders (user_id, order_date, total_price, is_done) VALUES (?, NOW(), 0, false RETURNING order_id;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
            }
        } throw new SQLException("There was an error creating the order, please try again");
    }

    public static int calculateTotalPrice(int bottomId, int topId, int quantity, ConnectionPool connectionPool) throws DatabaseException {
        CakeBottom bottom = ItemMapper.getBottomById(connectionPool, bottomId);
        CakeTop top = ItemMapper.getToppingById(connectionPool, bottomId);

        return (bottom.getPrice() + top.getPrice() * quantity);
    }

    public static void createuser(String userEmail, String userPassword, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (username, password) values (?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userEmail);
            ps.setString(2, userPassword);


            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to create a user, please try again!");
            }
        } catch (SQLException e) {
            String msg = "An error occurred try agian!";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The email is already in use, please use another or login.";
            }
            throw new DatabaseException(msg, e.getCause());
        }
    }
}
