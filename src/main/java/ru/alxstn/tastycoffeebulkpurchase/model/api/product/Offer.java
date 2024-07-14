package ru.alxstn.tastycoffeebulkpurchase.model.api.product;

public class Offer {
    private int id;
    private String name;
    private int price;
    private Object discount; // Since it's null in JSON
    private int weight;
    private String type;
    private boolean is_coffee_or_tea;
    private int max_quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIs_coffee_or_tea() {
        return is_coffee_or_tea;
    }

    public void setIs_coffee_or_tea(boolean is_coffee_or_tea) {
        this.is_coffee_or_tea = is_coffee_or_tea;
    }

    public int getMax_quantity() {
        return max_quantity;
    }

    public void setMax_quantity(int max_quantity) {
        this.max_quantity = max_quantity;
    }
}