package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class TextFileOrderCreatorServiceTest {

    @Mock
    PurchaseManagerService purchaseManager;

    @InjectMocks
    TextFileOrderCreatorService textFileOrderCreator;

    private final Session session = new Session();
    {
        session.setId(1L);
    }


    private final Customer firstCustomer = new Customer(1L, "First", "Customer", null);
    private final Customer secondCustomer = new Customer(2L, "Second", "Customer", null);

    private final ProductPackage firstPackage = new ProductPackage("Упаковка 250 г");
    private final ProductPackage secondPackage = new ProductPackage("Упаковка 100 г");
    private final ProductPackage thirdPackage = new ProductPackage("Упаковка 1 кг");

    private final Product firstProduct = new Product("Product One",
            new BigDecimal(1),
            "",
            firstPackage,
            "Group",
            "Subgroup",
            true);

    private final Product secondProduct = new Product("Product Two",
            new BigDecimal(2),
            "",
            secondPackage,
            "Group",
            "Subgroup",
            true);

    private final Product thirdProduct = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            true);

    private final Product unavailableProduct = new Product("Unavailable Product",
            new BigDecimal(0),
            "нет",
            firstPackage,
            "Group3",
            "Subgroup3",
            true);

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Path.of(session.getId() + ".sessionReport.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateCorrectReport() {
        when(purchaseManager.findAllPurchasesInSession(session))
                .thenReturn(List.of(
                        new Purchase(firstCustomer, unavailableProduct, session, 1),

                        new Purchase(firstCustomer, firstProduct, session,  "Крупный", 1),
                        new Purchase(firstCustomer, secondProduct, session, 2),
                        new Purchase(firstCustomer, thirdProduct, session, "Мелкий", 3),

                        new Purchase(secondCustomer, firstProduct, session, 3),
                        new Purchase(secondCustomer, secondProduct, session, 2),
                        new Purchase(secondCustomer, thirdProduct, session, "Крупный", 1)));

        assertDoesNotThrow(() -> textFileOrderCreator.createOrder(session));

        Path filePath = Path.of(session.getId() + ".sessionReport.json");

        try (Reader reader = Files.newBufferedReader(filePath, Charset.forName("windows-1251"))) {
            String report = IOUtils.toString(reader);
            assertFalse(report.contains("Unavailable Product"));
            assertTrue(report.contains("Group Subgroup Product Two - 4 шт."));
            assertTrue(report.contains("Group Subgroup Product One, Крупный - 1 шт."));
            assertTrue(report.contains("Group Subgroup2 Product Three, Крупный - 1 шт."));
            assertTrue(report.contains("Group Subgroup Product One - 3 шт."));
            assertTrue(report.contains("Group Subgroup2 Product Three, Мелкий - 3 шт."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateBlankReport() {
        when(purchaseManager.findAllPurchasesInSession(session))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> {
            textFileOrderCreator.createOrder(session);
            Path filePath = Path.of(session.getId() + ".sessionReport.json");
            assertTrue(Files.readString(filePath).isBlank());
        });
    }
}