package app.persistence;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import app.entities.*;
import app.exceptions.*;

public class OrderLineMapper {

    public static List<OrderLine> getAllOrderLinesByOrderID(ConnectionPool pool,
                                                            int orderID) throws DatabaseException {
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE order_id = ? ORDER BY order_id ASC";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int topID = rs.getInt("top_id");
                int botId = rs.getInt("bot_id");
                int ol_price = rs.getInt("ol_price");
                int quantity = rs.getInt("quantity");
                result.add(new OrderLine(topID, botId, quantity, ol_price, orderID));
            }
        } catch (SQLException exc) {
            throw new DatabaseException("Was unable to connect to database: getAllOrderLinesByID", exc);
        }
        return result;
    }

    public static List<OrderLine> getAllOrderLinesByCombo(int toppingID,
                                                          int bottomID,
                                                          ConnectionPool pool) throws DatabaseException {
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE top_id = ? AND bot_id = ? ORDER BY order_id asc";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, toppingID);
            ps.setInt(2, bottomID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("order_id");
                int orderLinePrice = rs.getInt("ol_price");
                int orderLineQuantity = rs.getInt("quantity");
                result.add(new OrderLine(toppingID, bottomID, orderLineQuantity, orderLinePrice, orderID));
            }
        } catch (SQLException exc) {
            throw new DatabaseException("Was unable to connect to database; getAllOrderLinesByCombo", exc);
        }

        return result;
    }

    public static void insertOrderLine(int orderId, int topId, int bottomId, int quantity, int price, ConnectionPool pool) throws DatabaseException {
        String sql = "INSERT INTO orderline (order_id, top_id, bot_id, quantity, ol_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, topId);
            ps.setInt(3, bottomId);
            ps.setInt(4, quantity);
            ps.setInt(5, price);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not insert order line", e);
        }
    }

    //
    public static int calculatePrice(int bottomId, int topId, int quantity, ConnectionPool pool) throws DatabaseException {
        String sql = "SELECT b.bot_price AS bottom_price, t.top_price AS top_price\n" +
                "FROM bottom b, topping t\n" +
                "WHERE b.bot_id = ? AND t.top_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bottomId);
            ps.setInt(2, topId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int bottomPrice = rs.getInt("bottom_price");
                int topPrice = rs.getInt("top_price");
                return (bottomPrice + topPrice) * quantity;
            } else {
                throw new DatabaseException("Could not find price for selected bottom/top.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error while calculating price", e);
        }
    }

    public static List<OrderLine> getOrderLinesForOrder(int orderId, ConnectionPool pool) throws DatabaseException {
        List<OrderLine> orderLines = new ArrayList<>();
        String sql = "SELECT ol.*, t.top_name, b.bot_name " +
                "FROM orderline ol " +
                "JOIN topping t ON ol.top_id = t.top_id " +
                "JOIN bottom b ON ol.bot_id = b.bot_id " +
                "WHERE ol.order_id = ? ORDER BY ol.ol_id ASC";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int topID = rs.getInt("top_id");
                int botId = rs.getInt("bot_id");
                int ol_price = rs.getInt("ol_price");
                int quantity = rs.getInt("quantity");
                int orderID = rs.getInt("order_id");

                OrderLine line = new OrderLine(topID, botId, quantity, ol_price, orderID);
                line.setTopName(rs.getString("top_name"));
                line.setBottomName(rs.getString("bot_name"));
                orderLines.add(line);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error fetching order lines for order ID: " + orderId, e);
        }

        return orderLines;
    }





}
