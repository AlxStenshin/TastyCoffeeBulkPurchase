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
import ru.alxstn.tastycoffeebulkpurchase.service.BasicSessionPurchaseReportCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseFilterService;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class TextFileOrderCreatorServiceTest {

    @Mock
    ProductManagerService productManagerService;

    @Mock
    PurchaseFilterService purchaseFilterService;

    @Mock
    BasicSessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;

    @InjectMocks
    TextFileOrderCreatorService textFileOrderCreator;

    private final Session session = new Session(); {
        session.setId(1L);
        session.setTitle("test");
    }

    private final ProductPackage firstPackage = new ProductPackage("Упаковка 250 г");
    private final ProductPackage secondPackage = new ProductPackage("Упаковка 100 г");
    private final ProductPackage thirdPackage = new ProductPackage("Упаковка 1 кг");

    private final Product firstProduct = new Product("Product One",
            new BigDecimal(1),
            "",
            firstPackage,
            "Group",
            "Subgroup",
            "",
            true);

    private final Product firstProductCoarse = new Product("Product One",
            new BigDecimal(1),
            "",
            firstPackage,
            "Group",
            "Subgroup",
            "Coarse",
            true);

    private final Product secondProduct = new Product("Product Two",
            new BigDecimal(2),
            "",
            secondPackage,
            "Group",
            "Subgroup",
            "",
            true);


    private final Product thirdProductFine = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            "Fine",
            true);

    private final Product thirdProductCoarse = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            "Coarse",
            true);

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Path.of(session.getId() + "_" +  session.getTitle() + "_SessionReport.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateAndSaveCorrectReport() {
        when(sessionPurchaseReportCreatorService.createPerProductReport(session))
                .thenReturn(Map.of(
                        firstProduct, 3,
                        firstProductCoarse, 1,
                        secondProduct, 4,
                        thirdProductCoarse, 1,
                        thirdProductFine, 3));

        assertDoesNotThrow(() -> textFileOrderCreator.placeFullOrder(session));

        Path filePath = Path.of(session.getId() + "_" +  session.getTitle() + "_SessionReport.json");

        try (Reader reader = Files.newBufferedReader(filePath, Charset.forName("windows-1251"))) {
            String report = IOUtils.toString(reader);
            assertFalse(report.contains("Unavailable Product"));
            assertTrue(report.contains("Group, Subgroup, Product One, Упаковка 250 г - 3 шт."));
            assertTrue(report.contains("Group, Subgroup, Product One, Упаковка 250 г, Coarse - 1 шт."));
            assertTrue(report.contains("Group, Subgroup, Product Two, Упаковка 100 г - 4 шт."));
            assertTrue(report.contains("Group, Subgroup2, Product Three, Упаковка 1 кг, Coarse - 1 шт."));
            assertTrue(report.contains("Group, Subgroup2, Product Three, Упаковка 1 кг, Fine - 3 шт."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateBlankReport() {
        when(sessionPurchaseReportCreatorService.createPerProductReport(session))
                .thenReturn(Map.of());

        assertDoesNotThrow(() -> {
            textFileOrderCreator.placeFullOrder(session);
            Path filePath = Path.of(session.getId() + "_" +  session.getTitle() + "_SessionReport.json");
            assertTrue(Files.readString(filePath).isBlank());
        });
    }
}