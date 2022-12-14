package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class RequestSessionSummaryCommandDto extends SerializableInlineObject {

    private final Session session;

    public RequestSessionSummaryCommandDto(Session session) {
        super(SerializableInlineType.REQUEST_SESSION_SUMMARY);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
