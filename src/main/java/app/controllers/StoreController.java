package app.controllers;

import app.entities.CakeBottom;
import app.entities.CakeTop;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        //handler
        app.get("/store", ctx -> showStore(ctx, connectionPool));
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
}

