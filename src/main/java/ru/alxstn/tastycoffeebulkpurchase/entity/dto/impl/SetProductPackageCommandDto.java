package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductPackageCommandDto extends SerializableInlineObject {

    private long productId;

    private String name;

    private String packaging;

    private String cost;

    public SetProductPackageCommandDto() {
        super(SerializableInlineType.SET_PACKAGING);
    }

    public SetProductPackageCommandDto(long productId, String name, String packaging, String cost) {
        this();
        this.productId = productId;
        this.name = name;
        this.packaging = packaging;
        this.cost = cost;
    }

    public long getProductId() {
        return productId;
    }


}
