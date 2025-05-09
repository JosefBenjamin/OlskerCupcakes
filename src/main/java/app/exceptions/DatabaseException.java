package app.exceptions;

public class DatabaseException extends Exception {

    //Constructor overload (3)
    public DatabaseException(String ourMessage, Throwable cause) {
        super(ourMessage, cause);
        System.out.println("userMessage: " + ourMessage);
        System.out.println("Error message: " + cause);
    }

    public DatabaseException(String userMessage, String systemMessage) {
        super(userMessage);
        System.out.println("userMessage: " + userMessage);
        System.out.println("errorMessage: " + systemMessage);
    }

    public DatabaseException(String ourMessage) {
        super(ourMessage);
        System.out.println("userMessage: " + ourMessage);
    }

}
