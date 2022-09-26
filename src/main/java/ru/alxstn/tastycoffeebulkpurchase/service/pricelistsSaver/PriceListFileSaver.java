package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class PriceListFileSaver {

    public void save(List<Product> priceList, String targetFile) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        JsonElement tree = gson.toJsonTree(priceList);
        String json = gson.toJson(tree);

        try (PrintWriter out = new PrintWriter(targetFile)) {
            out.println(json);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
