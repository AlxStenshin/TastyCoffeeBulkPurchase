package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosesSoonNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.time.LocalDateTime;

@Service
public class BasicSessionAutoCloserService implements SessionAutoCloserService {

    Logger logger = LogManager.getLogger(BasicSessionAutoCloserService.class);

    private final SessionManagerService sessionManagerService;
    private final DateTimeProvider dateTimeProvider;
    private final ApplicationEventPublisher publisher;

    public BasicSessionAutoCloserService(SessionManagerService sessionManagerService, DateTimeProvider dateTimeProvider, ApplicationEventPublisher publisher) {
        this.sessionManagerService = sessionManagerService;
        this.dateTimeProvider = dateTimeProvider;
        this.publisher = publisher;
    }

    @Override
    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 5000)
    public void closeActiveSession() {
        try {
            Session activeSession = sessionManagerService.getActiveSession();
            LocalDateTime now = dateTimeProvider.getCurrentTimestamp();
            LocalDateTime closeTime = activeSession.getDateTimeClosed();

            if (now.isAfter(closeTime)) {
                logger.info("Session will be auto closed now: " + activeSession);
                sessionManagerService.closeSession(activeSession);
            }

            if (now.plusHours(1).isAfter(closeTime)) {
                if (!activeSession.isCloseNotificationSent()) {
                    logger.info("Session will be auto closed in one hour: " + activeSession);
                    activeSession.setCloseNotificationSent(true);
                    sessionManagerService.saveSession(activeSession);
                    publisher.publishEvent(new ActiveSessionClosesSoonNotificationEvent(this, activeSession));
                }
            }

        } catch (SessionNotFoundException ignored) {}
    }

}
