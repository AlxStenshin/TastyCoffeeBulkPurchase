package ru.alxstn.tastycoffeebulkpurchase.entity;

import java.util.HashMap;
import java.util.Map;

public class Product {

    public Product(String group, String subGroup, String name) {
        this.group = group;
        this.subGroup = subGroup;
        this.name = name;
    }

    private final String name;
    private final String group;
    private final String subGroup;
    private final Map<String, Double> prices = new HashMap<>();
    private String specialMark;

    public void setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
    }
    public void addPrice(String packaging, Double price) {
        prices.put(packaging, price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", subGroup='" + subGroup + '\'' +
                ", specialMark='" + specialMark + '\'' +
                ", prices=" + prices +
                '}';
    }
}
