package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionNotFoundException;

import java.util.List;

public interface SessionManagerService {

    void saveSession(Session session) throws SessionCreationException;
    void closeSession(Session session);
    List<Session> getAllSessions();
    Session getSessionById(long sessionId);
    Session getCurrentSession() throws SessionNotFoundException;
    String getSessionNotFoundMessage();
    boolean activeSessionAvailable() throws SessionNotFoundException;
}
