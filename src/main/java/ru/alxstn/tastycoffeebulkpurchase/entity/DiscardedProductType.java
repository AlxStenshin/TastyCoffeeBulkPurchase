package ru.alxstn.tastycoffeebulkpurchase.entity;

public class DiscardedProductType {

    private String description;
    private boolean value;

    @SuppressWarnings("unused")
    public DiscardedProductType() {
        // Required for correct deserialization from thymeleaf html
    }

    public DiscardedProductType(String description, boolean value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unused")
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return  description + " : " +  value;
    }
}
