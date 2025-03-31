package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {


    public static User login(String userEmail, String userPassword, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "select * from users where email =? and password=?";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userEmail);
            ps.setString(2, userPassword);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("is_admin");
                return new User(id, email, password, isAdmin);
            } else {
                throw new DatabaseException("Failed to login, please try again!");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Something wrong with the database", e.getCause());
        }
    }

    public static void createUser(String userEmail, String userPassword, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into users (email, password) values (?,?)";

        try (
                Connection connection = connectionPool.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, userEmail);
            ps.setString(2, userPassword);


            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to create a user, please try again!");
            }
        } catch (SQLException e) {
            String msg = "An error occurred try agian!";
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


}
