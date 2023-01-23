package ru.alxstn.tastycoffeebulkpurchase.model;



public class ProductTypeFilter {

    private String description;
    private boolean value;

    @SuppressWarnings("unused")
    // Required for correct deserialization from thymeleaf html
    public ProductTypeFilter() {
    }

    public ProductTypeFilter(String description, boolean value) {
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
