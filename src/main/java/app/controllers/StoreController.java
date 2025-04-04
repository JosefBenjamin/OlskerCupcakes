package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import app.persistence.OrderLineMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        //handler
        app.get("/store", ctx -> showStore(ctx, connectionPool));
        app.post("/add-to-basket", ctx -> addToCart(ctx, connectionPool));
        app.get("/cart", ctx -> showCart(ctx, connectionPool));
    }


    public static void showStore(Context ctx, ConnectionPool pool) throws DatabaseException {
        List<CakeBottom> bundList = ItemMapper.getAllBottoms(pool);
        List<CakeTop> topList = ItemMapper.getAllToppings(pool);

        boolean cupcakeAdded = Boolean.TRUE.equals(ctx.sessionAttribute("cupcakeAdded"));
        ctx.sessionAttribute("cupcakeAdded", false); // reset after read
        Integer antal = ctx.sessionAttribute("antal");

        Map<String, Object> model = new HashMap<>();
        model.put("bundList", bundList);
        model.put("topList", topList);
        model.put("cupcakeAdded", cupcakeAdded);
        model.put("currentUser", ctx.sessionAttribute("currentUser"));
        model.put("antal", antal);

        ctx.render("store.html", model);
    }

    private static void addToCart(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.redirect("/login");
            return;
        }

        int bottomId = Integer.parseInt(ctx.formParam("bund"));
        int topId = Integer.parseInt(ctx.formParam("top"));
        int quantity = Integer.parseInt(ctx.formParam("antal"));

        int totalPrice = OrderLineMapper.calculatePrice(bottomId, topId, quantity, connectionPool);

        // Get or create an open order
        Order order = OrderMapper.getLatestOrderByUserID(currentUser.getUserId(), connectionPool);
        if (order == null || order.isDoneStatus()) {
            order = OrderMapper.createNewOrder(currentUser.getUserId(), connectionPool);
        }

        OrderLineMapper.insertOrderLine(order.getOrderId(), topId, bottomId, quantity, totalPrice, connectionPool);

        //  Recalculate and update total order price
        int newTotalPrice = OrderMapper.getTotalPriceForOrder(order.getOrderId(), connectionPool);
        OrderMapper.updateOrderTotalPrice(order.getOrderId(), newTotalPrice, connectionPool);

        ctx.sessionAttribute("cupcakeAdded", true);
        ctx.sessionAttribute("antal", quantity);
        ctx.redirect("/store");
    }


    public static void showCart(Context ctx, ConnectionPool pool) throws DatabaseException {
        User currentUser = ctx.sessionAttribute("currentUser");

        if (currentUser == null) {
            ctx.redirect("/login");
            return;
        }

        Order latestOrder = OrderMapper.getLatestOrderByUserID(currentUser.getUserId(), pool);

        if (latestOrder != null && !latestOrder.isDoneStatus()) {
            List<OrderLine> orderLines = OrderLineMapper.getAllOrderLinesByOrderID(pool, latestOrder.getOrderId());

            for (OrderLine line : orderLines) {
                String bottomName = ItemMapper.getBottomNameById(line.getBottomId(), pool);
                String toppingName = ItemMapper.getToppingNameById(line.getTopId(), pool);
                line.setBottomName(bottomName);
                line.setTopName(toppingName);
            }

            ctx.attribute("order", latestOrder);
            ctx.attribute("orderLines", orderLines);
            ctx.attribute("totalPrice", latestOrder.getPrice());
        } else {
            ctx.attribute("orderLines", new ArrayList<>());
            ctx.attribute("totalPrice", 0);
        }

        ctx.attribute("currentUser", currentUser);
        ctx.render("cart.html");
    }

}
