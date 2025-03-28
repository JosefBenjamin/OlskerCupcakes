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

    public static List<OrderLine> getAllOrderLinesByOrderID(ConnectionPool pool, int orderID) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE order_id = ? ORDER BY order_id ASC";

        try(Connection con = pool.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int topID = rs.getInt("top_id");
                int botId = rs.getInt("bot_id");
                int ol_price = rs.getInt("ol_price");
                int quantity = rs.getInt("quantity");
                result.add(new OrderLine(topID, botId, quantity,ol_price,orderID));
            }
        } catch (SQLException exc){
            throw new DatabaseException("Was unable to connect to database: getAllOrderLinesByID", exc);
        }
        return result;
    }

    public static List<OrderLine> getAllOrderLinesByCombo(int toppingID, int bottomID, ConnectionPool pool) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE top_id = ? AND bot_id = ? ORDER BY order_id asc";

        try(Connection con = pool.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1,toppingID);
            ps.setInt(2,bottomID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int orderID = rs.getInt("order_id");
                int orderLinePrice = rs.getInt("ol_price");
                int orderLineQuantity = rs.getInt("quantity");
                result.add(new OrderLine(toppingID, bottomID, orderLineQuantity, orderLinePrice, orderID));
            }
        } catch (SQLException exc){
            throw new DatabaseException("Was unable to connect to database; getAllOrderLinesByCombo", exc);
        }

        return result;
    }

    public static List<OrderLine> getOrderLineByID(int orderID, ConnectionPool pool) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        String sql  ="SELECT * FROM orderline WHERE order_id = ?";

        try(Connection con = pool.getConnection();
            PreparedStatement ps =con.prepareStatement(sql)){

            ps.setInt(1,orderID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int topID       = rs.getInt("top_id");
                int botID       = rs.getInt("bot_id");
                int quantity    = rs.getInt("quantity");
                int price       = rs.getInt("ol_price");
                result.add(new OrderLine(topID,botID, quantity, price, orderID)); 
            }

        } catch(SQLException exc){
            throw new DatabaseException("Was unable to connect to database; getOrderLineByID", exc);
        }
        return result;

    }


}
