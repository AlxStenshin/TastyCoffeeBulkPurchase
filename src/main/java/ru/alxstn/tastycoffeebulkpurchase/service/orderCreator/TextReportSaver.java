package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

public class TextReportSaver {

    public static void saveReport(Session session, String report, String title, Optional<SessionProductFilters> productProperties) {
        Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

        File dir = new File("report");
        if (!dir.exists())
            if (!dir.mkdirs())
                logger.error("Could not create text files report directory");

        StringBuilder fileName = new StringBuilder(
                dir.getAbsolutePath() + File.separator +
                        session.getId() + "_" +
                        session.getTitle() + "_" +
                        title + "_"
                );

        if (productProperties.isPresent()) {
            List<String> filteredProducts = productProperties.get().getProductTypeFilters().stream()
                    .filter(ProductTypeFilter::getValue)
                    .map(ProductTypeFilter::getDescription)
                    .toList();

            if (!filteredProducts.isEmpty()) {
                fileName.append(productProperties.get().getFilterType().getShortDescription());
                fileName.append("=");
                for (int i = 0; i < filteredProducts.size(); i++) {
                    fileName.append(filteredProducts.get(i));
                    if (i != filteredProducts.size() - 1)
                        fileName.append(", ");
                }
                fileName.append("_");
            }
        }

        fileName.append("SessionReport.json");
        logger.info("Report File Name: " + fileName);

        try (PrintWriter out = new PrintWriter(fileName.toString())) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
