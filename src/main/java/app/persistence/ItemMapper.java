package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper {
    public static List<CakeBottom> getAllBottoms(ConnectionPool connectionPool) throws DatabaseException { //Local attributes
        List<CakeBottom> cakeBottoms = new ArrayList<>();
        String sql = "SELECT * FROM bottom";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cakeBottoms.add(
                        new CakeBottom(rs.getInt("bot_id"),
                                rs.getString("bot_name"),
                                rs.getInt("bot_price")));
            } // while
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting all bottoms", e);
        } // catch
        return cakeBottoms;
    } // getAllBottoms

    public static CakeBottom getBottomById(ConnectionPool connectionPool,
                                           int bottomId) throws DatabaseException { //Local attributes
        CakeBottom bottom = null;
        String sql = "SELECT * FROM bottom WHERE bot_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bottomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("bot_id");
                String name = rs.getString("bot_name");
                int price = rs.getInt("bot_price");
                bottom = new CakeBottom(id, name, price);
            } // if
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting bottom by id", e);
        } // catch
        return bottom;
    } // getBottomById()

    public static List<CakeTop> getAllToppings(ConnectionPool connectionPool) throws DatabaseException { //Local attributes
        List<CakeTop> cakeTops = new ArrayList<>();
        String sql = "SELECT * FROM topping";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cakeTops.add(
                        new CakeTop(rs.getInt("top_id"),
                                rs.getString("top_name"),
                                rs.getInt("top_price")));
            } // while
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting all toppings", e);
        } // catch
        return cakeTops;
    } // getAllToppings()

    public static CakeTop getToppingById(ConnectionPool connectionPool,
                                         int toppingId) throws DatabaseException {   // Local attributes
        CakeTop topping = null;
        String sql = "SELECT * FROM topping WHERE top_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, toppingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("top_id");                                                   // Retrieves top_id from DB & give as Constructor parameter
                String name = rs.getString("top_name");                                              // Retrieves top_name from DB & give as Constructor parameter
                int price = rs.getInt("top_price");                                                // Retrieves top_price from DB & give as Constructor parameter
                topping = new CakeTop(id, name, price);                                                     // Creates a CakeTop object based on data received from DB
            } // if
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting topping by id", e);
        } // catch
        return topping;
    } // getToppingById()

    public static String getBottomNameById(int bottomId, ConnectionPool pool) throws DatabaseException {
        String name = null;
        String sql = "SELECT bot_name FROM bottom WHERE bot_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bottomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("bot_name");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting bottom name by ID", e);
        }

        return name;
    }

    public static String getToppingNameById(int toppingId, ConnectionPool pool) throws DatabaseException {
        String name = null;
        String sql = "SELECT top_name FROM topping WHERE top_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, toppingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("top_name");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error while getting topping name by ID", e);
        }

        return name;
    }
}
