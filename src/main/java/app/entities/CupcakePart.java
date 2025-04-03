package app.entities;

public abstract class CupcakePart {
    private int id;
    private String name;
    private int price;

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getId() {
        return this.id;
    }
}
