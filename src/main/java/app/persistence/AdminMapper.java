package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminMapper {

    public static int adjustBalance(String userEmail, String userPassword, int newBalance, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET balance=? WHERE email=? and password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, newBalance);
            ps.setString(2, userEmail);
            ps.setString(3, userPassword);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("No user found");
            }
        } catch(SQLException e) {
             throw new DatabaseException("Couldn't update balance");
        }
        return newBalance;
    }



}
