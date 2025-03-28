package app.persistence;

import app.Main;
import app.entities.OrderLine;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
class OrderLineMapperTest {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "olskercupcakes";
    private static ConnectionPool pool;

    @BeforeAll
    static void setUpClass() {
        pool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
        try(Connection testConnection = pool.getConnection()){
            try (Statement stmt = testConnection.createStatement()){
                // Drop existing tables if they exist already
                stmt.execute("DROP TABLE IF EXISTS test.orderline   CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.order       CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.orders      CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.users       CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.bottom      CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.topping     CASCADE");

                // Drop sequences if they exist already
                stmt.execute("DROP SEQUENCE IF EXISTS test.orderline_id_seq");
                stmt.execute("DROP SEQUENCE IF EXISTS test.order_id_seq");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_id_seq");
                stmt.execute("DROP SEQUENCE IF EXISTS test.users_id_seq");
                stmt.execute("DROP SEQUENCE IF EXISTS test.bottom_id_seq");
                stmt.execute("DROP SEQUENCE IF EXISTS test.topping_id_seq");


                // Create test-tables which copies the product structure without data


                // Create orderline table
                stmt.execute(   "CREATE TABLE IF NOT EXISTS test.orderline (" +
                        "ol_id      SERIAL PRIMARY KEY, " +
                        "top_id     INTEGER NOT NULL, " +
                        "bot_id     INTEGER NOT NULL, " +
                        "ol_price   INTEGER NOT NULL, " +
                        "quantity   INTEGER NOT NULL," +
                        "order_id   INTEGER NOT NULL" +
                        ")");

                // Create orders table
                stmt.execute(   "CREATE TABLE IF NOT EXISTS test.orders (" +
                        "order_id       SERIAL PRIMARY KEY, " +
                        "total_price    INTEGER NOT NULL, " +
                        "order_date     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, " +
                        "is_done        BOOLEAN DEFAULT FALSE, " +
                        "user_id        INTEGER NOT NULL" +
                        ")");

                // Create users tables
                stmt.execute(   "CREATE TABLE IF NOT EXISTS test.users (" +
                        "user_id    SERIAL PRIMARY KEY, " +
                        "email      VARCHAR(255) NOT NULL, " +
                        "password   VARCHAR(255) NOT NULL, " +
                        "is_admin   BOOLEAN DEFAULT FALSE, " +
                        "balance    INTEGER DEFAULT 100" +
                        ")");

                // Create bottom table
                stmt.execute(   "CREATE TABLE IF NOT EXISTS test.bottom (" +
                        "bot_id     SERIAL PRIMARY KEY, " +
                        "bot_name   VARCHAR(255) NOT NULL, " +
                        "bot_price  INTEGER NOT NULL " +
                        ")");


                // Create topping table
                stmt.execute(   "CREATE TABLE IF NOT EXISTS test.topping(" +
                        "top_id     SERIAL PRIMARY KEY, " +
                        "top_name   VARCHAR(255) NOT NULL, " +
                        "top_price  INTEGER NOT NULL " +
                        ")");


                // Create sequences that auto generates IDs for the tables
                stmt.execute("CREATE SEQUENCE test.orderline_id_seq");
                stmt.execute("ALTER TABLE test.orderline ALTER COLUMN ol_id SET DEFAULT nextval('test.orderline_id_seq')");

                stmt.execute("CREATE SEQUENCE test.orders_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_id_seq')");

                stmt.execute("CREATE SEQUENCE test.users_id_seq");
                stmt.execute("ALTER TABLE test.users ALTER COLUMN user_id SET DEFAULT nextval('test.users_id_seq')");

                stmt.execute("CREATE SEQUENCE test.bottom_id_seq");
                stmt.execute("ALTER TABLE test.bottom ALTER COLUMN bot_id SET DEFAULT nextval('test.bottom_id_seq')");

                stmt.execute("CREATE SEQUENCE test.topping_id_seq");
                stmt.execute("ALTER TABLE test.topping ALTER COLUMN top_id SET DEFAULT nextval('test.topping_id_seq')");
            }
        } catch (SQLException exc){
            System.out.println("Could not open a connection to the database");
            fail("Database connection failed");
        }
    }

    @BeforeEach
    public void setUp(){
        try (Connection testConnection = pool.getConnection()){
            try (Statement stmt = testConnection.createStatement()){
                // Clear the tables
                stmt.execute("TRUNCATE TABLE test.orderline CASCADE");
                stmt.execute("TRUNCATE TABLE test.orders    CASCADE");
                stmt.execute("TRUNCATE TABLE test.users     CASCADE");
                stmt.execute("TRUNCATE TABLE test.bottom    CASCADE");
                stmt.execute("TRUNCATE TABLE test.topping   CASCADE");

                // Insert test data in orderline table
                stmt.execute ("INSERT INTO test.orderline (top_id, bot_id, ol_price, quantity, order_id) VALUES" +
                        "('1','1','10','1','1')," +
                        "('2','2','20','2','2')," +
                        "('3','3','30','3','3')," +
                        "('4','4','40','4','4')," +
                        "('5','5','50','5','5')");



                // Insert test data in orders table
                stmt.execute("INSERT INTO test.orders (  total_price, order_date, is_done, user_id) VALUES" +
                        "('5', '2025-03-27 14:37:52.123456','true', '1')," +
                        "('5', '2025-03-27 14:37:52.123456','true', '2')," +
                        "('5', '2025-03-27 14:37:52.123456','true', '3')," +
                        "('5', '2025-03-27 14:37:52.123456','true', '4')," +
                        "('5', '2025-03-27 14:37:52.123456','true', '5')");


                // Insert test data in users table
                stmt.execute("INSERT INTO test.users ( email,password,is_admin,balance) VALUES" +
                        "('user1@email.com','password1','true','1000')," +
                        "('user2@email.com','password2','false','100')," +
                        "('user3@email.com','password3','false','100')," +
                        "('user4@email.com','password4','false','100')," +
                        "('user5@email.com','password5','false','100')");


                // Insert test data in topping table
                stmt.execute("INSERT INTO test.topping ( top_name, top_price) VALUES" +
                        "('Chocolate','5')," +
                        "('Vanilla','5')," +
                        "('Strawberry','5')," +
                        "('Blueberry','5')," +
                        "('Raspberry','5')");


                // Insert test data in bottom table
                stmt.execute("INSERT INTO test.bottom ( bot_name, bot_price) VALUES" +
                        "('Chocolate','5')," +
                        "('Vanilla','5')," +
                        "('Strawberry','5')," +
                        "('Blueberry','5')," +
                        "('Raspberry','5')");

            }
        } catch (SQLException exc){
            System.out.println("Could not open a connection to the database");
            fail("Database setup failed in @BeforeEach");
        }
    }

    @Test
    void createNewOrderLineTest() {
        int topID = 1;
        int botID = 1;
        int quantity = 1;
        int price = 1;
        int orderID = 10;
        try{

            OrderLineMapper.createOrderLine(pool, topID, botID, quantity, price, orderID);
        } catch (DatabaseException exc){
            fail("Database setup failed in createNewOrderLine");
        }
    }

}