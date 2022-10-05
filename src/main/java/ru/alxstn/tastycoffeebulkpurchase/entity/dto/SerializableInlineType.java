package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

public enum SerializableInlineType {
    SET_MAIN_COMMAND(0),
    SET_CATEGORY(1),
    SET_SUBCATEGORY(2),

    ADD_PRODUCT(3),
    REMOVE_PRODUCT(4),
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