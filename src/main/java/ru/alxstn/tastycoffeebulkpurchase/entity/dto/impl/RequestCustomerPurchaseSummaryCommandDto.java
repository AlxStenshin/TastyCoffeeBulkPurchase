package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class RequestCustomerPurchaseSummaryCommandDto extends SerializableInlineObject {

    public RequestCustomerPurchaseSummaryCommandDto() {
        super(SerializableInlineType.REQUEST_PURCHASE_SUMMARY);
    }
}
