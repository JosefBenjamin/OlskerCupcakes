package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class OrderMapper {

    public static Order getOrderByOrderLineID(int orderID, ConnectionPool pool) throws DatabaseException {
      //Local attribute
        Order result                = null;
        String sql                  = "SELECT * FROM orders WHERE order_id = ?";

        try(Connection con          = pool.getConnection();
            PreparedStatement ps    = con.prepareStatement(sql)){

            ps.setInt(1,orderID);
            ResultSet rs            = ps.executeQuery();
            if (rs.next()){
                int price           = rs.getInt("total_price");
                Timestamp date      = rs.getTimestamp("order_date");
                int userID          = rs.getInt("user_id");
                result              = new Order(orderID, price, date, userID);
            }
        } catch (SQLException exc){
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }

    public static Order getLatestOrderByUserID(int userID, ConnectionPool pool) throws DatabaseException {
        // Local attribute
        Order result                = null;
        String sql                  = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try(Connection con          = pool.getConnection();
            PreparedStatement ps    = con.prepareStatement(sql)){

            ps.setInt(1,userID);
            ResultSet rs            = ps.executeQuery();
            if (rs.next()){
                int price           = rs.getInt("total_price");
                Timestamp date      = rs.getTimestamp("order_date");
                int orderID          = rs.getInt("order_id");
                result              = new Order(orderID, price, date, userID);
            }
        } catch (SQLException exc){
            throw new DatabaseException(exc.getMessage());
        }

        return result;
    }
}
