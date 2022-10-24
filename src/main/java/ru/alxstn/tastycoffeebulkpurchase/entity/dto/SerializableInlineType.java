package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

public enum SerializableInlineType {
    SET_MAIN_COMMAND(0),
    SET_CATEGORY(1),
    SET_SUBCATEGORY(2),
    SET_PRODUCT_NAME(3),

    SET_PACKAGING(4),
    ADD_PURCHASE(5),
    REMOVE_PURCHASE(6),
    EDIT_PRODUCT_QUANTITY(7),
    ;

    private final int index;

    SerializableInlineType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}