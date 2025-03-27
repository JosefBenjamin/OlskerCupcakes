package app.persistence;

import app.entities.CakeBottom;
import app.entities.CakeTop;
import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper
{
    public static List<CakeBottom> getAllBottoms(ConnectionPool connectionPool) throws DatabaseException{

        List<CakeBottom> cakeBottoms = new ArrayList<>();
        String sql = "SELECT * FROM bottom";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                cakeBottoms.add(new CakeBottom(rs.getInt("bot_id"), rs.getString("bot_name"), rs.getInt("bot_price")));
            }
        }

        catch (SQLException e)
        {
            throw new DatabaseException("Error while getting all bottoms", e);
        }
        return  cakeBottoms;
    }


}
