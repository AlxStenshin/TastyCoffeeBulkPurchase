package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductBuilder;
import ru.alxstn.tastycoffeebulkpurchase.service.priceListSaver.PriceListFileSaverService;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceListSaverServiceTest {

    PriceListFileSaverService priceListSaver;

    @Mock
    DateTimeProvider dtProvider;

    @BeforeEach
    void init() {
        priceListSaver = new PriceListFileSaverService(dtProvider);
    }

    static String priceListTestFile = "test_priceList.json";

    @AfterAll
    static void cleanup() {
        try {
            Files.deleteIfExists(Path.of(priceListTestFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCorrectlySaveToFile() {
        Product p0 = new ProductBuilder()
                .setCategory("Group0")
                .setSubCategory("SubGroup0")
                .setName("Name0")
                .setPackage(new ProductPackage("Упаковка 250 г"))
                .setPrice(new BigDecimal(0))
                .build();

        Product p1 = new ProductBuilder()
                .setCategory("Group1")
                .setSubCategory("SubGroup1")
                .setName("Name1")
                .setPackage(new ProductPackage("Упаковка 250 г"))
                .setSpecialMark("So Special!")
                .setPrice(new BigDecimal(1))
                .build();

        List<Product> priceList = new ArrayList<>(Arrays.asList(p0, p1));

        priceListSaver.savePriceList(priceList, priceListTestFile);

        File price = new File(priceListTestFile);
        assertTrue(price.exists());

        Gson gson = new Gson();
        Type listOfProductType = new TypeToken<ArrayList<Product>>() {}.getType();
        ArrayList<Product> actual;

        try {
            Reader reader = Files.newBufferedReader(Paths.get(priceListTestFile), StandardCharsets.UTF_8);
            // ToDo: Read back saved priceList and compare with input product list
            //actual = gson.fromJson(reader, listOfProductType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //assertEquals(priceList, actual);
    }
}