package ru.alxstn.tastycoffeebulkpurchase.entity;

public class RequiredProductType {

    private String description;
    private boolean value;

    @SuppressWarnings("unused")
    public RequiredProductType() {
        // Required for correct deserialization from thymeleaf html
    }

    public RequiredProductType(String description, boolean value) {
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
