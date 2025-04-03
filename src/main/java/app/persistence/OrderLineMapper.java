package app.persistence;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import app.entities.*;
import app.exceptions.*;

import java.sql.ResultSet;
import java.sql.Connection;

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

    public static List<OrderLine> getAllOrderLinesByCombo(int toppingID, int bottomID, ConnectionPool pool) throws DatabaseException {
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE top_id = ? AND bot_id = ? ORDER BY order_id ASC";

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

    public static List<OrderLine> getAllOrderlinePerUserID(int userID, ConnectionPool pool) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        /*
        SQL join query that joins the orderline and order tables
        making it possible to search the DB for orderlines for a unique userID
         */
        String sql = "SELECT orderline.ol_id, "                   +
                            "orderline.order_id, "                +
                            "orderline.bot_id, "                  +
                            "bottom.bot_id, "                     +
                            "orderline.top_id, "                  +
                            "topping.top_id, "                    +
                            "orderline.quantity, "                +
                            "orderline.price "                    +
                            "FROM orderline"                      +
                            "JOIN orders"                         +
                            "ON orderline.order_id"               +
                            "JOIN users ON order.order_id"        +
                            "JOIN bottom ON orderline.bot_id"     +
                            "JOIN topping ON orderline.top_id"    +
                            "WHERE users.user_id = ?";
        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int orderlineID = rs.getInt(    "ol_id");
                int orderID     = rs.getInt(    "orderID");
                int botID       = rs.getInt(    "bot_id");
                String botName  = rs.getString( "bot_name");
                int topID       = rs.getInt(    "top_id");
                String topName  = rs.getString( "top_name");
                int quantity    = rs.getInt(    "quantity");
                int price       = rs.getInt(    "ol_price");
                OrderLine orderline = new OrderLine(topID,botID,quantity,price,orderID);
                result.add(orderline);
            }

        }   catch (SQLException exc){
            throw new DatabaseException("It was not possible to connect to database", exc.getMessage());
        }
        if (!result.isEmpty()){
            return result;
        } else {
            throw new DatabaseException("It was not possible to find any orderlines matching the userID (argument)");
        }
    }


}
