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
    public Session getActiveSession() {
        return sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
    }

    @Override
    public String getActiveSessionNotFoundMessage() {
        return "Активная сессия не обнаружена. \n" +
                "Заказы не принимаются.\n" +
                "Для открытия новой сессии обратитесь к администратору бота.";
    }

    @Override
    public String getOpenSessionNotFoundMessage() {
        return "Текущая сессия закрыта. \n" +
                "Заказы не принимаются.\n" +
                "Дождитесь завершения сессии.";
    }

    @Override
    public boolean newSessionAllowed() {
        try {
            sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
            return false;
        } catch (SessionNotFoundException e) {
            return true;
        }
    }
}
