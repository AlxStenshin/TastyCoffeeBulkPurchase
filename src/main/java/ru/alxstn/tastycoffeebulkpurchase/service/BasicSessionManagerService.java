package ru.alxstn.tastycoffeebulkpurchase.service;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.List;

@Service
public class BasicSessionManagerService implements SessionManagerService {

    private final SessionRepository sessionRepository;
    private final DateTimeProvider dateTimeProvider;
    private final OrderCreatorService orderCreator;

    public BasicSessionManagerService(SessionRepository sessionRepository,
                                      DateTimeProvider dateTimeProvider,
                                      OrderCreatorService orderCreator) {
        this.sessionRepository = sessionRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.orderCreator = orderCreator;
    }

    @Override
    public void saveSession(Session session) throws SessionCreationException {
        sessionRepository.save(session);
    }

    @Override
    public void closeSession(Session session) {
        session.setDateTimeClosed(dateTimeProvider.getCurrentTimestamp());
        sessionRepository.save(session);
        orderCreator.createOrder(session);
    }

    @Override
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public Session getSessionById(long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(SessionNotFoundException::new);
    }

    @Override
    public boolean isNewSessionAllowed() {
        try {
            sessionRepository.getCurrentSession().orElseThrow(SessionNotFoundException::new);
        } catch (SessionNotFoundException e) {
            return true;
        }
        return false;
    }
}
