package app.config;

import jakarta.servlet.SessionTrackingMode;
import org.eclipse.jetty.server.session.SessionHandler;

import java.util.EnumSet;


public class SessionConfig {


    public static SessionHandler sessionConfig() {
        //sessionHandler instance allows (jetty) server to remember users
        SessionHandler sessionHandler = new SessionHandler();
        //allows the jetty server to use cookies (true)
        sessionHandler.setUsingCookies(true);
        //only using cookies
        sessionHandler.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
         //the cookie is only for the server (Javascript can't access it)
        sessionHandler.setHttpOnly(true);
        return sessionHandler;
    }

}
