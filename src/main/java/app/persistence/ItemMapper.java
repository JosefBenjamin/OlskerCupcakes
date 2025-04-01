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

    public static CupcakePart getCupcakePartByID(boolean TRUEisTopping_FALSEisBottom,
                                                 int ID, ConnectionPool pool) throws DatabaseException {
        // Local attributes
        CupcakePart result = null;
        String sqlPart1 = TRUEisTopping_FALSEisBottom ? "topping" : "bottom";
        String sqlPart2 = TRUEisTopping_FALSEisBottom ? "top" : "bot";
        String sql = "SELECT * FROM " + sqlPart1 + " WHERE " + sqlPart2 + "_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // The ID is referring (top/bot)_id in the DB
            ps.setInt(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(sqlPart2 + "_id");                                             // Retrieves (top/bot)_id from DB & give as Constructor parameter
                String name = rs.getString(sqlPart2 + "_name");                                        // Retrieves (top/bot)_name from DB & give as Constructor parameter
                int price = rs.getInt(sqlPart2 + "_price");                                          // Retrieves (top/bot)_price from DB & give as Constructor parameter

                if (TRUEisTopping_FALSEisBottom) {
                    //Returns a CakeTop
                    result = new CakeTop(id, name, price);
                } else {
                    //Returns a CakeBottom
                    result = new CakeBottom(id, name, price);
                } // if-else (inner)
            } // if (outer)
        } catch (SQLException exc) {
            throw new DatabaseException("Error while getting " + sqlPart1 + " part", exc);
        } // catch
        return result;
    } // getCupcakePartByID()

    public static List<CupcakePart> getAllCupcakeParts(boolean TRUEisTopping_FALSEisBottom, ConnectionPool pool) throws DatabaseException {
        // Local attributes
        List<CupcakePart> result = new ArrayList<>();
        String sqlPart1 = TRUEisTopping_FALSEisBottom ? "topping" : "bottom";
        String sqlPart2 = TRUEisTopping_FALSEisBottom ? "top" : "bot";
        String sql = "SELECT * FROM " + sqlPart1 + " WHERE " + sqlPart2 + "_id = ?";

        try (Connection con = pool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (TRUEisTopping_FALSEisBottom) {
                    // Add a CakeTop to the result List
                    result.add(
                            new CakeTop(rs.getInt(sqlPart2 + "_id"),                                   // Retrieves top_id from DB & give as Constructor parameter
                                    rs.getString(sqlPart2 + "_name"),                              // Retrieves top_name from DB & give as Constructor parameter
                                    rs.getInt(sqlPart2 + "_price")));                              // Retrieves top_price from DB & give as Constructor parameter
                } else {
                    // Add a CakeBottom to the result List
                    result.add(new CakeBottom(rs.getInt(sqlPart2 + "_id"),                                   // Retrieves bot_id from DB & give as Constructor parameter
                            rs.getString(sqlPart2 + "_name"),                              // Retrieves bot_name from DB & give as Constructor parameter
                            rs.getInt(sqlPart2 + "_price")));                              // Retrieves bot_price from DB & give as Constructor parameter
                } // if-else
            } // while
        } catch (SQLException exc) {
            throw new DatabaseException("Error while getting " + sqlPart1 + " part", exc);
        } // catch

        return result;
    } // getAllCupcakeParts()
}
