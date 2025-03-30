package app.persistence;

import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

}
