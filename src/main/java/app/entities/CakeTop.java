package app.entities;

public class CakeTop extends CupcakePart {
    private int id;
    private String name;
    private int price;

    public CakeTop(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public CakeTop(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        String result;
        result = "CakeTop: " + this.name + " \nID: " + this.id + "\nPrice: " + this.price;
        return result;
    }
}
