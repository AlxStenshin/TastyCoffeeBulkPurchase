package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class RequestCustomerPurchaseSummaryCommandDto extends SerializableInlineObject {

    private final Session session;
    public RequestCustomerPurchaseSummaryCommandDto(Session session) {
        super(SerializableInlineType.REQUEST_PURCHASE_SUMMARY);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
