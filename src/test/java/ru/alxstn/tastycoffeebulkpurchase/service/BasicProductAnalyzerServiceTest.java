package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductPriceUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductSpecialMarkUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductPackageRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(TastyCoffeeConfigProperties.class)
@TestPropertySource("classpath:application-test.properties")
@DataJpaTest
class BasicProductAnalyzerServiceTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductPackageRepository productPackageRepository;

    @Mock
    ApplicationEventPublisher publisher;

    BasicProductAnalyzerService service;

    @BeforeEach
    void init() {
        productPackageRepository.deleteAll();
        productRepository.deleteAll();

        List<ProductPackage> packages = List.of(
                new ProductPackage("Упаковка 250 г"));
        productPackageRepository.saveAllAndFlush(packages);

        Product productOne = new Product("ProductOne",
                new BigDecimal(1),
                "",
                packages.get(0),
                "Group",
                "Subgroup",
                true);

        Product productTwo = new Product("ProductTwo",
                new BigDecimal(2),
                "",
                packages.get(0),
                "Group",
                "Subgroup",
                true);

        Product productOneUpdated = new Product("ProductOne",
                new BigDecimal(1),
                "",
                packages.get(0),
                "Group",
                "Subgroup",
                true);

        List<Product> sourceProducts = List.of(
                productOne,
                productTwo,
                productOneUpdated
        );

        sourceProducts.forEach(p -> p.setActual(false));

        productRepository.saveAllAndFlush(sourceProducts);
        service = new BasicProductAnalyzerService(productRepository, publisher);
    }

    @Test
    void shouldCorrectlyInjectRepo() {
        assertThat(productRepository).isNotNull();
    }

    @Test
    void shouldInvokeProductSpecialMarkChangedEvent() {
        ProductPackage pack = productPackageRepository.findAll().get(0);
        service.analyzeNewProducts(List.of(
                new Product("ProductOne",
                        new BigDecimal(1),
                        "NEW SPECIAL MARK",
                        pack,
                        "Group",
                        "Subgroup",
                        true)));

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof ProductSpecialMarkUpdateEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());
        verify(publisher, times(1)).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    void shouldInvokeProductPriceChangedEvent() {
        ProductPackage pack = productPackageRepository.findAll().get(0);
        service.analyzeNewProducts(List.of(
                new Product("ProductOne",
                        new BigDecimal(7),
                        "",
                        pack,
                        "Group",
                        "Subgroup",
                        true)));

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof ProductPriceUpdateEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(1)).publishEvent(any(ApplicationEvent.class));
    }


}