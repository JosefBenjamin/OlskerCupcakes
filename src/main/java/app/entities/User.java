package app.entities;

public class User {

    private int userId;
    private String email;
    private String password;
    private boolean isAdmin;
    private int balance = 0;

    public User(int userId, String email, String password, boolean isAdmin, int balance) {
        this.isAdmin = isAdmin;
        this.password = password;
        this.email = email;
        this.userId = userId;
        this.balance = balance;
    }

    public User(int userId, String email, String password, boolean isAdmin) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(String email, String password, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public boolean getAdminStatus() {
        return isAdmin;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        String result;
        result = "User ID: " + this.userId + " \nUsername: " + this.email + "\nPassword: " + this.password + "\nIs admin: " + this.isAdmin + "\nBalance: " + this.balance;
        return result;
    }
}
