package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminMapper {

    //Sets the balance of user/customer account by using email as the identifier
    public static int adjustBalance(String userEmail, int newBalance, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET balance=? WHERE email=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, newBalance);
            ps.setString(2, userEmail);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("No user found");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Couldn't update balance");
        }
        return newBalance;
    }


}
