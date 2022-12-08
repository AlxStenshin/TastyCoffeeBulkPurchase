package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

public interface SessionSummaryMessageCreatorService {
    String createSessionSummaryMessage(Session session);
}
