package ru.alxstn.tastycoffeebulkpurchase.service;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionIsNotOpenException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
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
    public Session addNewSession() throws SessionCreationException {
        try {
            sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
            throw new SessionCreationException(getOnlyOneActiveSessionAllowedMessage());
        } catch (SessionNotFoundException e) {
            return new Session();
        }
    }

    @Override
    public void saveSession(Session session) throws SessionCreationException {
        sessionRepository.save(session);
    }

    @Override
    public Session closeSession(Session session) {
        session.setDateTimeClosed(dateTimeProvider.getCurrentTimestamp());
        session.setClosed(true);
        sessionRepository.save(session);
        orderCreator.createOrder(session);
        return session;
    }

    public void checkSessionCustomerAccessible(Session session) {
        if (session.isClosed())
            throw new SessionIsNotOpenException(getOpenSessionNotFoundMessage());
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
        return sessionRepository.getActiveSession().orElseThrow(() ->
                new SessionNotFoundException(getActiveSessionNotFoundMessage()));
    }

    @Override
    public Session getUnfinishedSession() throws SessionNotFoundException {
        return sessionRepository.getUnfinishedSession().orElseThrow(() ->
                new SessionNotFoundException(getActiveSessionNotFoundMessage()));
    }

    private String getOnlyOneActiveSessionAllowedMessage() {
        return "Only One Active Session Allowed.\n" +
                    "Please finish active session first.";
    }

    private String getActiveSessionNotFoundMessage() {
        return "Активная сессия не обнаружена. \n" +
                "Заказы не принимаются.\n" +
                "Для открытия новой сессии обратитесь к администратору бота.";
    }

    private String getOpenSessionNotFoundMessage() {
        return "Текущая сессия закрыта. \n" +
                "Заказы не принимаются.\n" +
                "Дождитесь завершения сессии.";
    }
}
