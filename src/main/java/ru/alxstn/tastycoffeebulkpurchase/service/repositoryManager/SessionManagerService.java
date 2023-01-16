package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionIsNotOpenException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductProperties;

import java.util.List;

public interface SessionManagerService {
    Session addNewSession() throws SessionCreationException;
    void saveSession(Session session) throws SessionCreationException;
    void closeSession(Session session);
    void placeSessionPurchases(RequiredProductProperties requiredProducts);
    RequiredProductProperties buildReqProductTypes(Session session);
    void checkSessionCustomerAccessible(Session session) throws SessionIsNotOpenException;
    List<Session> getAllSessions();
    Session getSessionById(long sessionId);
    Session getActiveSession() throws SessionNotFoundException;
    Session getUnfinishedSession() throws SessionNotFoundException;
}
