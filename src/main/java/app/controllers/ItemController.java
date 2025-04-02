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
        app.post("/add-to-cart", ctx -> addItemsToCart(ctx, connectionPool));

    }


    public static void addItemsToCart(Context ctx, ConnectionPool connectionPool) {
        try {
            int bottomId = Integer.parseInt(ctx.formParam("bottom"));
            int topId = Integer.parseInt(ctx.formParam("top"));
            int quantity = Integer.parseInt(ctx.formParam("quantity"));

            CakeBottom selectedBottom = null;
            CakeTop selectedTop = null;

            for (CakeBottom b : ItemMapper.getAllBottoms(connectionPool)) {
                if (b.getId() == bottomId) {
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
            topId = selectedTop.getId();
            bottomId = selectedBottom.getId();
            //int quantity = antal;
            int price = (selectedTop.getPrice() + selectedBottom.getPrice()) * quantity;
            int orderId = 0;

            OrderLine orderLine = new OrderLine(topId, bottomId, quantity, price, orderId);

            // Retrieve or create cart from session
            List<OrderLine> cart = ctx.sessionAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            cart.add(orderLine);
            ctx.sessionAttribute("cupcakeAdded", true);
            ctx.sessionAttribute("quantity", quantity);
            ctx.redirect("/store");


        } catch (Exception e) {
            ctx.status(500).result("Server error: " + e.getMessage());
        }
    }



}
