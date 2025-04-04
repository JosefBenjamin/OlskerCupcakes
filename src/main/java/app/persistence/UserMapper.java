package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {


    public static User login(String userEmail, String userPassword, ConnectionPool connectionPool) throws DatabaseException {
        // We no longer compare plain text in SQL
        String sql = "SELECT * FROM users WHERE email = ?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userEmail);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String hashedPassword = rs.getString("password"); // from DB
                boolean isAdmin = rs.getBoolean("is_admin");

                // Check using jBCrypt
                if (BCrypt.checkpw(userPassword, hashedPassword)) {
                    // If correct, return the User
                    return new User(id, email, hashedPassword, isAdmin);
                } else {
                    throw new DatabaseException("Wrong password, please try again!");
                }
            } else {
                throw new DatabaseException("Failed to find user with that email!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Something wrong with the database", e.getCause());
        }
    }

    public static void createUser(String userEmail, String userPassword, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (email, password) VALUES (?, ?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            // Hash the plain-text password with jBCrypt
            String hashedPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());

            ps.setString(1, userEmail);
            ps.setString(2, hashedPassword);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to create a user, please try again!");
            }
        } catch (SQLException e) {
            String msg = "An error occurred try again!";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The email is already in use, please use another or login.";
            }
            throw new DatabaseException(msg, e.getCause());
        }
    }

    public List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {

        List<User> memberList = new ArrayList<>();

        String sql = "select user_id, email, password, is_admin, balance " +
                "from users " +
                "order by user_id ";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");
                int balance = rs.getInt("balance");
                memberList.add(new User(userId, email, password, isAdmin, balance));
            }
        } catch (SQLException e) {
            String msg = "An error occurred try again!";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "The email is already in use, please use another or login.";
            }
            throw new DatabaseException(msg, e.getCause());
        }
        return memberList;
    }

    public static void updateEmail(int userId, String newEmail, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, newEmail);
            ps.setInt(2, userId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                throw new DatabaseException("Failed to update email.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error while updating email.", e);
        }
    }

    public static void updatePassword(int userId, String newPassword, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated != 1) {
                throw new DatabaseException("Failed to update password.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error while updating password.", e);
        }
    }

    public static User getUserByEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");
                int balance = rs.getInt("balance");
                return new User(userId, email, password, isAdmin, balance);
            } else {
                throw new DatabaseException("Bruger ikke fundet.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error while retrieving user by email.", e);
        }
    }

    public String getPasswordByEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            String sql = "SELECT password FROM test.users WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    } else {
                        throw new DatabaseException("User not found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

}
