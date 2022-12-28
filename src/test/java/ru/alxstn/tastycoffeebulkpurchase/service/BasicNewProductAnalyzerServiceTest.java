package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.event.NewProductDiscoveredEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductPriceUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductSpecialMarkUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicNewProductAnalyzerServiceTest {

    @Mock
    static ProductManagerService productManagerService;

    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    BasicNewProductAnalyzerService service;

    private final ProductPackage productPackage = new ProductPackage("Упаковка 250 г");

    @BeforeEach
    void init() {
        Product firstSimilarProduct = new Product("ProductOne",
                new BigDecimal(1),
                "",
                productPackage,
                "Group",
                "Subgroup",
                "",
                true);
        firstSimilarProduct.setActual(false);
        firstSimilarProduct.setDateUpdated(LocalDateTime.MIN);

        Product secondSimilarProduct = new Product("ProductOne",
                new BigDecimal(1),
                "",
                productPackage,
                "Group",
                "Subgroup",
                "",
                true);
        secondSimilarProduct.setActual(false);
        secondSimilarProduct.setDateUpdated(LocalDateTime.MAX);

        when(productManagerService.getSimilarProducts(
                any(String.class),
                any(String.class),
                any(String.class),
                any(String.class),
                any(ProductPackage.class)))
                .thenReturn(List.of(firstSimilarProduct, secondSimilarProduct));
    }

    @Test
    void shouldNotInvokeAnyEventWithUnchangedProduct() {
        service.analyzeNewProducts(List.of(new Product("ProductOne", // <- Nothing Changed
                new BigDecimal(1),
                "",
                productPackage,
                "Group",
                "Subgroup",
                "",
                true)));
        verify(publisher, times(0)).publishEvent(isA(ApplicationEvent.class));
    }

    @Test
    void shouldInvokeProductSpecialMarkChangedEvent() {
        service.analyzeNewProducts(List.of(
                new Product("ProductOne",
                        new BigDecimal(1),
                        "NEW SPECIAL MARK", // <- Mark changed
                        productPackage,
                        "Group",
                        "Subgroup",
                        "",
                        true)));

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof ProductSpecialMarkUpdateEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(1)).publishEvent(isA(ProductSpecialMarkUpdateEvent.class));
    }

    @Test
    void shouldInvokeProductPriceChangedEvent() {
        service.analyzeNewProducts(List.of(
                new Product("ProductOne",
                        new BigDecimal(7), // <- Price changed
                        "",
                        productPackage,
                        "Group",
                        "Subgroup",
                        "",
                        true)));

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof ProductPriceUpdateEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(1)).publishEvent(isA(ProductPriceUpdateEvent.class));
    }

    @Test
    void shouldInvokeNewProductDiscoveredEvent() {

        when(productManagerService.getSimilarProducts(
                any(String.class),
                any(String.class),
                any(String.class),
                any(String.class),
                any(ProductPackage.class)))
                .thenReturn(List.of());     // <- No similar products found

        service.analyzeNewProducts(List.of(
                new Product("ProductTwo", // <- New Product Name
                        new BigDecimal(2),
                        "",
                        productPackage,
                        "Group",
                        "Subgroup",
                        "",
                        true)));

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof NewProductDiscoveredEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(1)).publishEvent(isA(NewProductDiscoveredEvent.class));
    }

}