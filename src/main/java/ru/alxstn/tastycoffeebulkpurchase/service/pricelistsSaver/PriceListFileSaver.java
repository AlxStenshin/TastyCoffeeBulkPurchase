package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.annotation.AnnotationExclusionStrategy;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class PriceListFileSaver {

    private final DateTimeProvider dateTimeProvider;
    public PriceListFileSaver(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @EventListener
    public void handlePriceList(final PriceListReceivedEvent event) {
        savePriceList(event.getPriceList(), dateTimeProvider.getFormattedCurrentDate() + "_priceList.json");
    }

    public void savePriceList(List<Product> priceList, String targetFileName) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new AnnotationExclusionStrategy())
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        JsonElement tree = gson.toJsonTree(priceList);
        String json = gson.toJson(tree);

        try (PrintWriter out = new PrintWriter(targetFileName)) {
            out.println(json);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
