package ru.alxstn.tastycoffeebulkpurchase.model;

public enum SessionProductFilterType {
    // ToDo: Add this to html view
    ACCEPT_FILTER("Включить только товары выбранных типов", "Accepted"),
    DISCARD_FILTER("Исключить товары выбранных типов", "Discarded"),
    ;

    private final String description;
    private final String shortDescription;

    SessionProductFilterType(String description, String shortDescription) {
        this.description = description;
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }
}
