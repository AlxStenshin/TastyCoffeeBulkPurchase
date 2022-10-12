package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductNameCommandDto extends SerializableInlineObject {

    private String name;
    private String subCategory;

    public SetProductNameCommandDto() {
        super(SerializableInlineType.SET_PRODUCT_NAME);
    }

    public SetProductNameCommandDto(String name, String subCategory) {
        this();
        this.name = name;
        this.subCategory = subCategory;
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