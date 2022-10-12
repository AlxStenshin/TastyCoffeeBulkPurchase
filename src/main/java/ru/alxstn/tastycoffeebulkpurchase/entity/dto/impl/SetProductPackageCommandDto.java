package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductPackageCommandDto extends SerializableInlineObject {

    private String name;

    private String packaging;

    private String cost;

    public SetProductPackageCommandDto() {
        super(SerializableInlineType.SET_PACKAGING);
    }

    public SetProductPackageCommandDto(String name, String packaging, String cost) {
        this();
        this.name = name;
        this.packaging = packaging;
        this.cost = cost;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

}
