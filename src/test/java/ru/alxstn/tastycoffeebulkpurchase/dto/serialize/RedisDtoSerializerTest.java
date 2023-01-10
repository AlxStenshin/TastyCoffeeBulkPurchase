package ru.alxstn.tastycoffeebulkpurchase.dto.serialize;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.repository.TelegramCallbackRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class RedisDtoSerializerTest {

    @Mock
    TelegramCallbackRepository repository;

    @InjectMocks
    RedisDtoSerializer serializer;

    private final Session session = new Session();

    private final Customer customer = new Customer(1L,
            "TestCustomer", null, null);

    private final CustomerNotificationSettings notificationSettings = new CustomerNotificationSettings();

    private final ProductPackage productPackage = new ProductPackage("Упаковка 250 г");

    private final Product testProduct = new Product(
            "TestProduct", new BigDecimal("1.0"), "",
            productPackage, "Group", "Subgroup", "", true);

    private final Purchase purchase = new Purchase(customer, testProduct, new Session(), 1);


    @Test
    void shouldCorrectlySerializeClearPurchasesCommandDto() {
        ClearPurchasesCommandDto o = new ClearPurchasesCommandDto(
                customer, List.of(purchase));
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeEditPurchaseCommandDto() {
        EditPurchaseCommandDto o = new EditPurchaseCommandDto(purchase);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializePlaceOrderCommandDto() {
        PlaceOrderCommandDto o = new PlaceOrderCommandDto("");
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeRemoveMessageCommandDto() {
        RemoveMessageCommandDto o = new RemoveMessageCommandDto(1, 1L);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeRemoveProductFromCustomerPurchaseCommandDto() {
        RemoveProductFromCustomerPurchaseCommandDto o =
                new RemoveProductFromCustomerPurchaseCommandDto(testProduct);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeRemovePurchaseCommandDto() {
        RemovePurchaseCommandDto o = new RemovePurchaseCommandDto(purchase);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeReplaceProductForCustomerPurchaseCommandDto() {
        ReplaceProductForCustomerPurchaseCommandDto o =
                new ReplaceProductForCustomerPurchaseCommandDto(testProduct, testProduct);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));

    }

    @Test
    void shouldCorrectlySerializeRequestCustomerPurchaseSummaryCommandDto() {
        RequestCustomerPurchaseSummaryCommandDto o = new RequestCustomerPurchaseSummaryCommandDto(session);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeRequestSessionSummaryCommandDto() {
        RequestSessionSummaryCommandDto o = new RequestSessionSummaryCommandDto(session);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeSetCustomerNotificationSettingsDto() {
        SetCustomerNotificationSettingsDto o = new SetCustomerNotificationSettingsDto(notificationSettings);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeSetOrderPaidCommandDto() {
        SetOrderPaidCommandDto o = new SetOrderPaidCommandDto(session);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeSetProductCategoryCommandDto() {
        SetProductCategoryCommandDto o = new SetProductCategoryCommandDto("", null);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeSetProductNameCommandDto() {
        SetProductNameCommandDto o = new SetProductNameCommandDto("", "", null);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeSetProductSubCategoryCommandDto() {
        SetProductSubCategoryCommandDto o = new SetProductSubCategoryCommandDto("",  null);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

    @Test
    void shouldCorrectlySerializeUpdatePurchaseCommandDto() {
        UpdatePurchaseCommandDto o = new UpdatePurchaseCommandDto(purchase);
        when(repository.save(o)).thenReturn(o);
        assertNotNull(serializer.serialize(o));
    }

}