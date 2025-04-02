package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));
        app.get("/customers", ctx -> renderWithUser(ctx, "customers.html"));
        app.get("/orders", ctx -> renderWithUser(ctx, "orders.html"));
        app.get("/cart", ctx -> renderWithUser(ctx, "cart.html"));
        app.get("/customerprofile", ctx -> renderWithUser(ctx, "customerprofile.html"));
        app.get("/orderconfirmed", ctx -> renderWithUser(ctx, "orderconfirmed.html"));
        app.get("/store", ctx -> renderWithUser(ctx, "store.html"));
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
                ctx.render("login.html");

            } catch (DatabaseException e) {
                ctx.attribute("message", "Dit brugernavn findes allerede. Prøv igen, eller log ind.");
                ctx.render("createuser.html");
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
            if (user.getAdminStatus ()) {
                ctx.attribute("user", user); // Set user attribute
                ctx.redirect("/customers");
            } else {
                ctx.redirect("/store");
            }
        } catch (DatabaseException e) {
            // If not, send back to login page with error message.
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void renderWithUser(Context ctx, String template) {
        User user = ctx.sessionAttribute("currentUser");
        if (user != null) {
            ctx.attribute("user", user);
        }
        ctx.render(template);
    }
}
