package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class SetOrderPaidCommandDto extends SerializableInlineObject {

    private Session session;

    public SetOrderPaidCommandDto() {
        super(SerializableInlineType.PAYMENT_CONFIRMATION);
    }

    public SetOrderPaidCommandDto( Session session) {
        this();
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
