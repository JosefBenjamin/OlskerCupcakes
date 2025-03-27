package app.persistence;

import app.entities.OrderLine;
import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class OrderLineMapper {

    //TODO: Make SQL joins from CakeTop and CakeBot
    // In order to view a complete orderline and all orderlines for a given user okay so


    public static List<OrderLine> getAllOrderLinesPerOrderId(int orderID, ConnectionPool pool) throws DatabaseException {
        List<OrderLine> result = new ArrayList<OrderLine>();
        String sql = "SELECT * FROM orderline WHERE order_id = ?";

        try (   Connection con = pool.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("ol_id");
                int topID = rs.getInt("top_id");
                int botID = rs.getInt("bot_id");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");
                result.add(new OrderLine(topID, botID, quantity, price, orderID));
            }

        } catch (SQLException exc){
            throw new DatabaseException("Could not find any orders matching the given order ID", exc.getMessage());
        }

        return result;
    }

    public static List<OrderLine> getAllOrderLinesForCake(int topID, int botID, ConnectionPool pool) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE top_id = ? AND bot_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = pool.getConnection().prepareStatement(sql)){
            ps.setInt(1, topID);
            ps.setInt(2, botID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int orderID = rs.getInt("order_id");
                int price = rs.getInt("ol_price");
                int quantity = rs.getInt("quantity");
                int olID = rs.getInt("ol_id");
                result.add(new OrderLine(topID, botID,quantity, price, orderID));
            }

        } catch (SQLException exc){
            throw new DatabaseException("Could not find any orders matching the given topID", exc.getMessage());
        }

        return result;
    }
}
