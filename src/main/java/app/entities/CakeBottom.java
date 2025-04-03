package app.entities;

public class CakeBottom extends CupcakePart {
    private int id;
    private String name;
    private int price;

    public CakeBottom(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    CakeBottom(int id){
        this.id = id;
    }

    @Override
    public String toString() {
        String result;
        result = "CakeBottom: " + this.name + " \nID: " + this.id + "\nPrice: " + this.price;
        return result;
    }
}
