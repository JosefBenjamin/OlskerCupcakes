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
                stmt.execute("CREATE TABLE test.orderline   AS (SELECT * FROM public.orderline) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders      AS (SELECT * FROM public.orders)    WITH NO DATA");
                stmt.execute("CREATE TABLE test.users       AS (SELECT * FROM public.users)     WITH NO DATA");
                stmt.execute("CREATE TABLE test.bottom      AS (SELECT * FROM public.bottom)    WITH NO DATA");
                stmt.execute("CREATE TABLE test.topping     AS (SELECT * FROM public.topping)   WITH NO DATA");


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
                stmt.execute ("INSERT INTO test.orderline (ol_id, top_id, bot_id, ol_price, quantity, order_id) VALUES" +
                        "('1','1','1','10','1','1')," +
                        "('2','2','2','20','2','2')," +
                        "('3','3','3','30','3','3')," +
                        "('4','4','4','40','4','4')," +
                        "('5','5','5','50','5','5')");



                // Insert test data in orders table
                stmt.execute("INSERT INTO test.orders (order_id,  total_price, order_date, is_done, user_id) VALUES" +
                        "('1','5', '2025-03-27 14:37:52.123456','true', '1')," +
                        "('2','5', '2025-03-27 14:37:52.123456','true', '2')," +
                        "('3','5', '2025-03-27 14:37:52.123456','true', '3')," +
                        "('4','5', '2025-03-27 14:37:52.123456','true', '4')," +
                        "('5','5', '2025-03-27 14:37:52.123456','true', '5')");


                // Insert test data in users table
                stmt.execute("INSERT INTO test.users (user_id, email,password,is_admin,balance) VALUES" +
                        "('1','user1@email.com','password1','true','1000')," +
                        "('2','user2@email.com','password2','false','100')," +
                        "('3','user3@email.com','password3','false','100')," +
                        "('4','user4@email.com','password4','false','100')," +
                        "('5','user5@email.com','password5','false','100')");


                // Insert test data in topping table
                stmt.execute("INSERT INTO test.topping (top_id, top_name, top_price) VALUES" +
                        "('1','Chocolate','5')," +
                        "('2','Vanilla','5')," +
                        "('3','Strawberry','5')," +
                        "('4','Blueberry','5')," +
                        "('5','Raspberry','5')");


                // Insert test data in bottom table
                stmt.execute("INSERT INTO test.bottom (bot_id, bot_name, bot_price) VALUES" +
                        "('1','Chocolate','5')," +
                        "('2','Vanilla','5')," +
                        "('3','Strawberry','5')," +
                        "('4','Blueberry','5')," +
                        "('5','Raspberry','5')");

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