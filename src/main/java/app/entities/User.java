package app.entities;

public class User {

    private int userId;
    private String username;
    private String password;
    private boolean isAdmin;
    private int balance = 0;

    public User(int userId, String username, String password, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.password = password;
        this.username = username;
        this.userId = userId;
    }


    public boolean getAdminStatus() {
        return isAdmin;
    }
    public int getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        String result;
        result = "User ID: " + this.userId + " \nUsername: " + this.username + "\nPassword: " + this.password + "\nIs admin: " + this.isAdmin + "\nBalance: " + this.balance;
        return result;
    }
}
