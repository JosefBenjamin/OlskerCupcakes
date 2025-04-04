package app.controllers;

import app.entities.Order;
import app.entities.OrderLine;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    //handlers
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));
        app.get("/profile", ctx -> showProfile(ctx, connectionPool)); // Only for logged-in non-admin users
        app.post("/profile/update", ctx -> updateUserProfile(ctx, connectionPool));
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

    public static void login(Context ctx, ConnectionPool connectionPool) {

        // Hent for parametre
        String username = ctx.formParam("email");
        String password = ctx.formParam("password");

        // Check om bruger findes i database med de angivne username + password
        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.redirect("/store");
        } catch (DatabaseException e) {
            // Hvis nej, send tilbage til login med fejl besked.
            ctx.attribute("message", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void showProfile(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.redirect("/login");
            return;
        }
        if (currentUser.getAdminStatus()) {
            ctx.result("Admins har ikke adgang til denne side.");
            return;
        }

        try {
            // Load previous orders
            List<Order> previousOrders = new OrderMapper().getAllOrdersByUserID(currentUser.getUserId(), connectionPool);

            // For each order, attach the order lines and set names
            for (Order order : previousOrders) {
                // Get lines for this order
                List<OrderLine> lines = OrderLineMapper.getAllOrderLinesByOrderID(connectionPool, order.getOrderId());

                // Fill in topName/bottomName for each line
                for (OrderLine line : lines) {
                    // These methods come from your ItemMapper or similar
                    line.setTopName(ItemMapper.getToppingNameById(line.getTopId(), connectionPool));
                    line.setBottomName(ItemMapper.getBottomNameById(line.getBottomId(), connectionPool));
                }

                // Attach lines to the order
                order.setOrderLines(lines);
            }

            // Store the orders on the user object
            currentUser.setTidligereOrdrer(previousOrders);

            ctx.attribute("currentUser", currentUser);
            ctx.render("customerprofile.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", "Kunne ikke hente tidligere ordrer: " + e.getMessage());
            ctx.render("customerprofile.html");
        }
    }

    private static void updateUserProfile(Context ctx, ConnectionPool connectionPool) {
        User currentUser = ctx.sessionAttribute("currentUser");
        if (currentUser == null) {
            ctx.redirect("/login");
            return;
        }

        String newEmail = ctx.formParam("newEmail");
        String newPassword = ctx.formParam("newPassword");

        try {
            if (newEmail != null && !newEmail.isBlank()) {
                UserMapper.updateEmail(currentUser.getUserId(), newEmail, connectionPool);
                currentUser = UserMapper.getUserByEmail(newEmail, connectionPool);
            }
            if (newPassword != null && !newPassword.isBlank()) {
                UserMapper.updatePassword(currentUser.getUserId(), newPassword, connectionPool);
            }
            ctx.sessionAttribute("currentUser", currentUser); // Refresh session
            ctx.redirect("/profile");
        } catch (DatabaseException e) {
            ctx.attribute("error", "Kunne ikke opdatere bruger: " + e.getMessage());
            ctx.render("customerprofile.html");
        }
    }


}
