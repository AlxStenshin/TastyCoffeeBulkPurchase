package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.DiscardedProductProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.NewSessionStartedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosedNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionCreationException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionIsNotOpenException;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.BasicPurchaseFilterService;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.TextFileOrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.WebPageOrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.List;

@Service
public class BasicSessionManagerService implements SessionManagerService {

    // ToDo: Session closed developer notification with session stats.
    // ToDo: Refresh session summary button for usage in controller -> html form

    private final ApplicationEventPublisher publisher;
    private final SessionRepository sessionRepository;
    private final DateTimeProvider dateTimeProvider;
    private final WebPageOrderCreatorService webPageOrderCreator;
    private final TextFileOrderCreatorService textFileOrderCreator;
    private final BasicPurchaseFilterService requiredProductsService;

    public BasicSessionManagerService(ApplicationEventPublisher publisher,
                                      SessionRepository sessionRepository,
                                      DateTimeProvider dateTimeProvider,
                                      WebPageOrderCreatorService webPageOrderCreator,
                                      TextFileOrderCreatorService textFileOrderCreator) {
        this.publisher = publisher;
        this.sessionRepository = sessionRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.webPageOrderCreator = webPageOrderCreator;
        this.textFileOrderCreator = textFileOrderCreator;
        this.requiredProductsService = new BasicPurchaseFilterService();
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
        session.setDateTimeClosed(dateTimeProvider.getCurrentTimestamp());
        session.setClosed(true);
        sessionRepository.save(session);
        publisher.publishEvent(new ActiveSessionClosedNotificationEvent(this, session));
    }

    @Override
    public void placeSessionPurchases(DiscardedProductProperties requiredProducts) {
        webPageOrderCreator.placeOrderWithProductTypes(requiredProducts);
        textFileOrderCreator.placeOrderWithProductTypes(requiredProducts);
    }

    @Override
    public DiscardedProductProperties buildDiscardedProductTypes(Session session) {
        return requiredProductsService.createAllDiscardedPropertiesTurnedOff(session);
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
