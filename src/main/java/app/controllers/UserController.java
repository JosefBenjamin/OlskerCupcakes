package app.controllers;

import app.entities.Order;
import app.entities.OrderLine;
import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.get(    "/login",           ctx -> ctx.render                   ("login.html"));
        app.post(   "/login",           ctx -> login                        (ctx,           connectionPool));
        app.get(    "/logout",          ctx -> logout                       (ctx));
        app.get(    "/createuser",      ctx -> ctx.render                   ("createuser.html"));
        app.post(   "/createuser",      ctx -> createUser                   (ctx,           connectionPool));

        app.get(    "/customers",       ctx -> renderWithUser               (ctx, "customers.html", connectionPool));
        app.get(    "/orders",          ctx -> renderWithUser               (ctx, "orders.html", connectionPool));
        app.get(    "/cart",            ctx -> ctx.render                   ("cart.html"));
        //app.post(   "/cart",            ctx -> ItemController.addItemsToCart(ctx, connectionPool);
        app.get(    "/customerprofile", ctx -> renderWithUser               (ctx, "customerprofile.html", connectionPool));
        app.get(    "/orderconfirmed",  ctx -> renderWithUser               (ctx, "orderconfirmed.html", connectionPool));
        app.post(   "/store",           ctx -> redirectWithUser             (ctx, "store.html", connectionPool));
    }

    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)) {
            try {
                UserMapper.createUser(email, password1, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn: " + email +
                        " Nu skal du logge på.");
                ctx.redirect("login.html");

            } catch (DatabaseException e) {
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind.");
                ctx.redirect("createuser.html");
            }
        } else {
            ctx.attribute("message", "Dine to passwords matcher ikke! Prøv igen.");
            ctx.render("createuser.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        // Check if user exists in database with the given email + password
        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);

            // Check if user is admin
            if (user.getAdminStatus()) {
                ctx.attribute("currentUser", user); // Set user attribute
                ctx.render("/customers");
            } else {
                ctx.redirect("/store");
            }
        } catch (DatabaseException e) {
            // If not, send back to login page with error message.
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void renderWithUser(Context ctx, String template, ConnectionPool pool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user != null) {
            ctx.attribute("currentUser", user);

        }
        ctx.render(template);
    }

    private static void redirectWithUser(Context ctx, String template,ConnectionPool pool)
    {   // Local attributes
        User user                  = ctx.sessionAttribute("currentUser");

        if (user != null) {
            ctx.attribute("currentUser",  user);
            ItemController.addItemsToCart(ctx, pool);

        }  // if
        ctx.redirect(template);
    } // redirectWithUser()
}
