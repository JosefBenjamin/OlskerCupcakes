package app.controllers;

import app.entities.CakeBottom;
import app.entities.CakeTop;
import app.entities.CupcakePart;
import app.entities.OrderLine;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/add-to-basket", ctx -> addItemsToBasket(ctx, connectionPool));

    }


    public static void addItemsToBasket(Context ctx, ConnectionPool connectionPool) {
        try {
            int bundId = Integer.parseInt(ctx.formParam("bund"));
            int topId = Integer.parseInt(ctx.formParam("top"));
            int antal = Integer.parseInt(ctx.formParam("antal"));

            CakeBottom selectedBottom = null;
            CakeTop selectedTop = null;

            for (CakeBottom b : ItemMapper.getAllBottoms(connectionPool)) {
                if (b.getId() == bundId) {
                    selectedBottom = b;
                    break;
                }
            }

            for (CakeTop t : ItemMapper.getAllToppings(connectionPool)) {
                if (t.getId() == topId) {
                    selectedTop = t;
                    break;
                }
            }

            if (selectedBottom == null || selectedTop == null) {
                ctx.status(400).result("Invalid topping or bottom selection.");
                return;
            }

            // Create the order item
            int toppingId = selectedTop.getId();
            int bottomId = selectedBottom.getId();
            int quantity = antal;
            int price = (selectedTop.getPrice() + selectedBottom.getPrice()) * quantity;
            int orderId = 0;

            OrderLine orderLine = new OrderLine(toppingId, bottomId, quantity, price, orderId);

            // Retrieve or create basket from session
            List<OrderLine> basket = ctx.sessionAttribute("basket");
            if (basket == null) {
                basket = new ArrayList<>();
            }
            basket.add(orderLine);
            ctx.sessionAttribute("cupcakeAdded", true);
            ctx.sessionAttribute("antal", antal);
            ctx.redirect("/store");


        } catch (Exception e) {
            ctx.status(500).result("Server error: " + e.getMessage());
        }
    }



}
