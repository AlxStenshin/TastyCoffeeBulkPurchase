package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@Service
public class TextFileOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;

    public TextFileOrderCreatorService(SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService) {
        this.sessionPurchaseReportCreatorService = sessionPurchaseReportCreatorService;
    }

    @Override
    public void createOrder(Session session) {
        logger.info("Now Saving Purchase Summary for session " + session.getId() + ":" + session.getTitle());
        String report = sessionPurchaseReportCreatorService.createPerProductReport(session).entrySet().stream()
                .map(e -> e.getKey().getShortName() + " - " + e.getValue() + " шт.")
                .collect(Collectors.joining("\n"));

        try (PrintWriter out = new PrintWriter(session.getId() + ".sessionReport.json")) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
