package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

public enum SerializableInlineType {
    SET_MAIN_COMMAND(0),
    SET_CATEGORY(1),
    SET_SUBCATEGORY(2),
    SET_PRODUCT_NAME(3),

    SET_PACKAGING(4),
    ADD_PRODUCT(5),
    REMOVE_PRODUCT(6),
    ;

    private final int index;
    //private final String description;

    SerializableInlineType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}