package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserController {

    //handlers
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
        app.get("/createuser", ctx -> ctx.render("createuser.html"));
        app.post("/createuser", ctx -> createUser(ctx, connectionPool));
        app.get("/profile", ctx -> showProfile(ctx, connectionPool)); // Only for logged-in non-admin users
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
        User currentUser = ctx.sessionAttribute("currentUser"); // Get user
        if (currentUser == null) {
            ctx.redirect("/login"); // Not logged in
            return;
        }
        if (currentUser.getAdminStatus()) {
            ctx.result("Admins har ikke adgang til denne side."); // Block admin
            return;
        }
        ctx.attribute("currentUser", currentUser); // Set for view
        ctx.render("customerprofile.html"); // Render profile page
    }

}
