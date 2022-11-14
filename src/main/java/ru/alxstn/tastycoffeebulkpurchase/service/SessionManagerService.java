package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionCreationException;

import java.util.List;

public interface SessionManagerService {

    void saveSession(Session session) throws SessionCreationException;
    void closeSession(Session session);
    List<Session> getAllSessions();
    Session getSessionById(long sessionId);
    boolean isNewSessionAllowed();
}
