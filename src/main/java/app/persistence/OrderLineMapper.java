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

    public static List<OrderLine> getAllOrderLines(ConnectionPool pool, int orderID) throws DatabaseException{
        List<OrderLine> result = new ArrayList<>();
        String sql = "SELECT * FROM orderline WHERE order_id = ?";

        try(Connection con = pool.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ){
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int olID = rs.getInt("ol_id");
                int topID = rs.getInt("top_id");
                int botId = rs.getInt("bot_id");
                int ol_price = rs.getInt("ol_price");
                int quantity = rs.getInt("quantity");
                result.add(new OrderLine(olID, topID, botId, ol_price, quantity));
            }
        } catch (SQLException exc){
            throw new DatabaseException("Was unable to fin");
        }
        return result;
    }
}
