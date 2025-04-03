package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.ItemController;
import app.controllers.*;
import app.controllers.UserController;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.*;
import app.entities.*;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.ArrayList;
import java.util.logging.Logger;

import static javassist.runtime.DotClass.fail;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "olskercupcakes";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing
        ArrayList<OrderLine> test = new ArrayList<>();
        try{
            test.add(new OrderLine(1,1,1,10));
            OrderMapper.createOrder(1 ,connectionPool);

        } catch ( DatabaseException exc){
            System.out.println("error while testing orderLine creation");
        }


        app.get("/", ctx -> StoreController.showStore(ctx, connectionPool));
        app.get("/store", ctx -> StoreController.showStore(ctx, connectionPool));

        // Add user-related routes
        UserController.addRoutes(app, connectionPool);
        ItemController.addRoutes(app, connectionPool);
    }
}
