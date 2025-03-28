package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

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

    public static CakeBottom getBottomById(ConnectionPool connectionPool, int bottomId) throws DatabaseException {

        CakeBottom bottom = null;
        String sql = "SELECT * FROM bottom WHERE bot_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, bottomId);
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
             int id  = rs.getInt("bot_id");
             String name = rs.getString("bot_name");
             int price = rs.getInt("bot_price");
             bottom = new CakeBottom(id, name, price);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error while getting bottom by id", e);
        }
        return bottom;
    }

    public static List<CakeTop> getAllToppings(ConnectionPool connectionPool) throws DatabaseException{

        List<CakeTop> cakeTops = new ArrayList<>();
        String sql = "SELECT * FROM topping";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                cakeTops.add(new CakeTop(rs.getInt("top_id"), rs.getString("top_name"), rs.getInt("top_price")));
            }
        } catch (SQLException e){
            throw new DatabaseException("Error while getting all toppings", e);
        }
        return  cakeTops;
    }

    public static CakeTop getToppingById(ConnectionPool connectionPool, int toppingId) throws DatabaseException {

        CakeTop topping = null;
        String sql = "SELECT * FROM topping WHERE top_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, toppingId);
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
             int id  = rs.getInt("top_id");
             String name = rs.getString("top_name");
             int price = rs.getInt("top_price");
             topping = new CakeTop(id, name, price);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error while getting topping by id", e);
        }
        return topping;
    }

    public static CupcakePart getCupcakePartByID(boolean TRUEisToppingFALSEisBottom, int partID, ConnectionPool pool) throws DatabaseException{
        CupcakePart result= null;
        String sqlPart1 = TRUEisToppingFALSEisBottom ? "topping" : "bottom";
        String sqlPart2 = TRUEisToppingFALSEisBottom ? "top" : "bot";
        String sql = "SELECT * FROM " + sqlPart1 + " WHERE " + sqlPart2 + "_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1,partID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int id      = rs.getInt(sqlPart2+"_id");
                String name = rs.getString(sqlPart2+"_name");
                int price   = rs.getInt(sqlPart2+"_price");

                if(TRUEisToppingFALSEisBottom){
                    // CakeTop
                    result = new CakeTop(id, name, price);
                } else{
                    //CakeBottom
                    result = new CakeBottom(id,name, price);
                }
            }
        } catch (SQLException exc){
            throw new DatabaseException("Error while getting " + sqlPart1 + " part", exc);
        }
        return result;
    }

    public static List<CupcakePart> getAllCupcakeParts(boolean TRUEisToppingFALSEisBottom, ConnectionPool pool) throws DatabaseException{
        List<CupcakePart> result = new ArrayList<>();
        String sqlPart1 = TRUEisToppingFALSEisBottom ? "topping" : "bottom";
        String sqlPart2 = TRUEisToppingFALSEisBottom ? "top" : "bot";
        String sql = "SELECT * FROM " + sqlPart1 + " WHERE " + sqlPart2 + "_id = ?";

        try(Connection con = pool.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                if(TRUEisToppingFALSEisBottom){
                    result.add(new CakeTop(     rs.getInt(sqlPart2+"_id"),
                                                rs.getString(sqlPart2+"_name"),
                                                rs.getInt(sqlPart2+"_price")));
                }else{
                    result.add(new CakeBottom(  rs.getInt(sqlPart2+"_id"),
                                                rs.getString(sqlPart2+"_name"),
                                                rs.getInt(sqlPart2+"_price")));
                }
            }
        } catch (SQLException exc){
            throw new DatabaseException("Error while getting " + sqlPart1 + " part", exc);
        }

        return result;
    }
}
