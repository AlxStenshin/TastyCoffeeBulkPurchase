package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionIsNotOpenException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;

import java.util.List;

public interface SessionManagerService {
    Session addNewSession() throws SessionCreationException;
    void saveSession(Session session) throws SessionCreationException;
    Session closeSession(Session session);
    void checkSessionCustomerAccessible(Session session) throws SessionIsNotOpenException;
    List<Session> getAllSessions();
    Session getSessionById(long sessionId);
    Session getActiveSession() throws SessionNotFoundException;
    Session getUnfinishedSession() throws SessionNotFoundException;

}
