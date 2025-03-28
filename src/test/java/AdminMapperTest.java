import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "olskercupcakes";

    private static ConnectionPool connectionPool;
    private static UserMapper userMapper;
// comment
    @BeforeAll
    public static void setUpClass() {
        try {
            connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
            userMapper = new UserMapper();
            try (Connection testConnection = connectionPool.getConnection()) {
                try (Statement stmt = testConnection.createStatement()) {
                    stmt.execute("CREATE SCHEMA IF NOT EXISTS test");
                    // The test schema is already created, so we only need to delete/create test tables
                    stmt.execute("DROP TABLE IF EXISTS test.users");

                    // Create tables as copy of original public schema structure
                    stmt.execute("CREATE TABLE IF NOT EXISTS test.users (" +
                            "user_id SERIAL PRIMARY KEY, " +
                            "email VARCHAR(255) NOT NULL, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "is_admin BOOLEAN DEFAULT FALSE, " +
                            "balance INTEGER DEFAULT 0" +
                            ")");


                    // Create sequences for auto generating id's for members and sports
                    stmt.execute("DROP SEQUENCE IF EXISTS test.user_id CASCADE;");
                    stmt.execute("CREATE SEQUENCE test.user_id");
                    stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.user_id')");

                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                fail("Database connection failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
        //Runs once at the beginning of EVERY test, so if you run 5 tests, it will run 5 times
    void setUp() {
        try (Connection testConnection = connectionPool.getConnection()) {
            try (Statement stmt = testConnection.createStatement()) {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test.users");

                // Reset the sequence number
                stmt.execute("SELECT setval('test.user_id', COALESCE((SELECT MAX(user_id) FROM test.users), 1), false)");
                // Insert rows
                stmt.execute("INSERT INTO test.users (email, password, is_admin, balance) " +
                        "VALUES ('example@example.org', 1234, false, 0), " +
                        "('something@example.org', 2345, false, 0), " +
                        "('random@example.org', 1234, false, 0), " +
                        "('johan@example.org', 1234, false, 0)");

                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test.user_id', COALESCE((SELECT MAX(user_id)+1 FROM test.users), 1), false)");
            }
        } catch (SQLException e) {
            fail("Database connection failed");
        }
    }

    //Connection is all good
    @Test
    void testConnection() throws SQLException { //Test is successful
        //assertFalse to make test fail
        assertNotNull(connectionPool.getConnection());
    }

    @Test
    void testNewBalance() throws SQLException { //Test is successful
        List<User> users = null;
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {
            // Set balance of the first user (user_id = 1 or by email)
            stmt.execute("UPDATE test.users SET balance = 100 WHERE email = 'example@example.org'");
        }
        try {
            users = userMapper.getAllUsers(connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }

        assertEquals(100, users.get(0).getBalance());
    }




}
