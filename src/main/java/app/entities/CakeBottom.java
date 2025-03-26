package app.entities;

public class CakeBottom implements CupcakePart{
    private int id;
    private String name;
    private int price;

    public CakeBottom(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }


    // Getters
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    @Override
    public String toString() {
        String result;
        result = "CakeBottom: " + this.name + " \nID: " + this.id + "\nPrice: " + this.price;
        return result;
    }
}
