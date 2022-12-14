package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class SetProductNameCommandDto extends SerializableInlineObject {

    private String name;
    private String subCategory;

    public SetProductNameCommandDto() {
        super(SerializableInlineType.SET_PRODUCT_NAME);
    }

    public SetProductNameCommandDto(String name, String subCategory, SerializableInlineObject previous) {
        this();
        this.name = name;
        this.subCategory = subCategory;
        this.setPrevious(previous);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}