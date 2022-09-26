package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceListSaverTest {

    private final PriceListFileSaver priceListSaver = new PriceListFileSaver();
    private static final String targetFile = "priceList.json";

    @AfterAll
    static void cleanup() {
        try {
            Files.deleteIfExists(Path.of(targetFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCorrectlySaveToFile() {
        Product p0 = new Product.Builder()
                .setGroup("Group0")
                .setSubGroup("SubGroup0")
                .setName("Name0")
                .setPackage("Pack0")
                .setPrice(0d)
                .build();

        Product p1 = new Product.Builder()
                .setGroup("Group1")
                .setSubGroup("SubGroup1")
                .setName("Name1")
                .setPackage("Pack1")
                .setSpecialMark("So Special!")
                .setPrice(1d)
                .build();

        List<Product> priceList = new ArrayList<>(Arrays.asList(p0, p1));
        priceListSaver.save(priceList, targetFile);

        File price = new File(targetFile);
        assertTrue(price.exists());

        Gson gson = new Gson();
        Type listOfProductType = new TypeToken<ArrayList<Product>>() {}.getType();
        List<Product> actual;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(targetFile));
            actual = gson.fromJson(reader, listOfProductType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(priceList, actual);
    }
}