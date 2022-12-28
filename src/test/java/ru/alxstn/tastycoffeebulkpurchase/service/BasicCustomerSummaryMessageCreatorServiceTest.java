package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicCustomerSummaryMessageCreatorServiceTest {

    @Mock
    PurchaseManagerService purchaseManagerService;

    @Mock
    PaymentManagerService paymentManagerService;

    @InjectMocks
    BasicCustomerSummaryMessageCreatorService service;

    private static final Product discountableProduct = new Product("Discountable",
            new BigDecimal("1"),
            "",
            new ProductPackage(" "),
            "Кофе",
            "Кофе для фильтра",
            "",
            false);

    @Test
    void shouldCreate_YourOrderIsEmptyMessage() {
        assertEquals("Ваш заказ пуст", service.buildCustomerSummaryMessage(new Session(), new Customer()));
    }

    @Test
    void shouldCreateMessageWithTotalsOnlyIfSessionHasNoDiscount() {
        Customer customer = new Customer();
        Session session = new Session();

        Purchase purchase = new Purchase(customer, discountableProduct, session, 1);
        when(purchaseManagerService.findAllPurchasesInSessionByCustomer(session, customer)).
                thenReturn(List.of(purchase));

        Payment payment = new Payment(customer, session);
        payment.setTotalAmountWithDiscount(new BigDecimal(1));
        when(paymentManagerService.getCustomerSessionPayment(session, customer))
                .thenReturn(Optional.of(payment));

        String message = service.buildCustomerSummaryMessage(session, customer);

        assertThat(message, not(containsString("Итог без скидки:")));
        assertThat(message, containsString("Ваш заказ:"));
    }

    @Test
    void shouldCreateMessageWithDiscountsIfSessionHasDiscount() {
        Customer customer = new Customer();
        Session session = new Session();
        session.setDiscountPercentage(20);

        Purchase purchase = new Purchase(customer, discountableProduct, session, 1);
        when(purchaseManagerService.findAllPurchasesInSessionByCustomer(session, customer)).
                thenReturn(List.of(purchase));

        Payment payment = new Payment(customer, session);
        payment.setTotalAmountWithDiscount(new BigDecimal(1));
        payment.setTotalAmountNoDiscount(new BigDecimal(1));
        payment.setDiscountableAmountWithDiscount(new BigDecimal(1));
        payment.setDiscountableAmountNoDiscount(new BigDecimal(1));
        payment.setNonDiscountableAmount(new BigDecimal(1));
        when(paymentManagerService.getCustomerSessionPayment(session, customer))
                .thenReturn(Optional.of(payment));

        String message = service.buildCustomerSummaryMessage(session, customer);

        assertThat(message, containsString("Ваш заказ:"));
        assertThat(message, containsString("Итог без скидки:"));
    }

}