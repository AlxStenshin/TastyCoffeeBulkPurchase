package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

public enum SerializableInlineType {
    SET_MAIN_COMMAND(0),

    SET_PRODUCT_CATEGORY(1),
    SET_PRODUCT_SUBCATEGORY(2),
    SET_PRODUCT_NAME(3),
    SET_PRODUCT_PACKAGING(4),
    SET_PRODUCT_QUANTITY(5),

    ADD_PURCHASE(6),
    EDIT_PURCHASE(7),
    UPDATE_PURCHASE(8),
    REMOVE_PURCHASE(9),
    CLEAR_PURCHASES(10),
    PAYMENT_CONFIRMATION(11),
    CUSTOMER_SETTINGS(12),

    REMOVE_BOT_MESSAGE(13),
    REQUEST_PURCHASE_SUMMARY(14),
    REQUEST_SESSION_SUMMARY(15),

    ;

    private final int index;

    SerializableInlineType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}