package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.NewSessionStartedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosedNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionIsNotOpenException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.PurchaseFilterService;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.ImprovedTextFileCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.TextFileOrderCreatorService;

import java.util.List;

@Service
public class BasicSessionManagerService implements SessionManagerService {

    private final ApplicationEventPublisher publisher;
    private final SessionRepository sessionRepository;
    private final TextFileOrderCreatorService textFileOrderCreator;
    private final ImprovedTextFileCreatorService nextTextFileOrderCreator;
    private final PurchaseFilterService purchaseFilterService;

    public BasicSessionManagerService(ApplicationEventPublisher publisher,
                                      SessionRepository sessionRepository,
                                      TextFileOrderCreatorService textFileOrderCreator,
                                      ImprovedTextFileCreatorService nextTextFileOrderCreator,
                                      PurchaseFilterService purchaseFilterService) {
        this.publisher = publisher;
        this.sessionRepository = sessionRepository;
        this.textFileOrderCreator = textFileOrderCreator;
        this.nextTextFileOrderCreator = nextTextFileOrderCreator;
        this.purchaseFilterService = purchaseFilterService;
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
        if (session.getId() == null) {
            publisher.publishEvent(new NewSessionStartedEvent(this, session));
        }
        sessionRepository.save(session);
    }

    @Override
    public void closeSession(Session session) {
        //session.setDateTimeClosed(dateTimeProvider.getCurrentTimestamp());
        session.setClosed(true);
        session.setCloseSoonNotificationSent(true);
        sessionRepository.save(session);
        publisher.publishEvent(new ActiveSessionClosedNotificationEvent(this, session));
        textFileOrderCreator.placeFullOrder(session);
        nextTextFileOrderCreator.placeFullOrder(session);
    }

    @Override
    public void placeSessionPurchases(SessionProductFilters productFilters) {
        textFileOrderCreator.placeOrderWithProductFilter(productFilters);
    }

    @Override
    public SessionProductFilters buildDiscardedProductTypes(Session session) {
        return purchaseFilterService.createFilter(session,
                SessionProductFilterType.DISCARD_FILTER,
                false);
    }

    @Override
    public SessionProductFilters buildAcceptedProductTypes(Session session) {
        return purchaseFilterService.createFilter(session,
                SessionProductFilterType.ACCEPT_FILTER,
                true);
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
        return """
                Only One Active Session Allowed.
                "Please finish active session first.""";
    }

    private String getActiveSessionNotFoundMessage() {
        return """
                Активная сессия не обнаружена.\s
                Заказы не принимаются.
                Для открытия новой сессии обратитесь к администратору бота.""";
    }

    private String getOpenSessionNotFoundMessage() {
        return """
                Текущая сессия закрыта.\s
                Заказы не принимаются.
                Дождитесь завершения сессии.""";
    }
}
