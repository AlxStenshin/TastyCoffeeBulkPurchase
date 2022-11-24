package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetOrderPaidCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BasicSessionSummaryCustomerNotifierService implements SessionSummaryCustomerNotifierService {

    Logger logger = LogManager.getLogger(BasicSessionSummaryCustomerNotifierService.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;

    public BasicSessionSummaryCustomerNotifierService(ApplicationEventPublisher publisher,
                                                      DtoSerializer serializer) {
        this.publisher = publisher;
        this.serializer = serializer;
    }

    @EventListener
    @Override
    public void createAndPublishSummary(PurchaseSummaryNotificationEvent event) {
        logger.info("Now building and publishing per-customer session summary");

        Session session = event.getSession();
        List<Purchase> currentSessionPurchases = event.getCurrentSessionPurchases();
        Set<Customer> currentSessionCustomers = currentSessionPurchases.stream()
                .map(Purchase::getCustomer)
                .collect(Collectors.toSet());

        for (Customer c : currentSessionCustomers) {
            List<Purchase> currentSessionCustomerPurchases = currentSessionPurchases.stream()
                    .filter(p -> p.getCustomer().equals(c))
                    .collect(Collectors.toList());

            publisher.publishEvent(new SendMessageEvent(this,
                    SendMessage.builder()
                            .chatId(c.getChatId())
                            .parseMode("html")
                            .text(buildMessage(
                                    currentSessionCustomerPurchases,
                                    session.getDiscountPercentage(),
                                    session.getPaymentInstruction()))
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(Collections.singleton(Collections.singletonList(
                                                    buildConfirmationButton(session))))
                                    .build())
                            .build()));
        }
    }

    private InlineKeyboardButton buildConfirmationButton(Session session) {
        return InlineKeyboardButton.builder()
                .text("Оплачено")
                .callbackData(serializer.serialize(new SetOrderPaidCommandDto(session)))
                .build();
    }

    private String buildMessage(List<Purchase> purchases, int discountValue, String paymentInstructions) {
        double totalPrice = 0;
        double discountableTotal = 0;
        double nonDiscountableTotal = 0;

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Ваш заказ:\n\n");
        messageBuilder.append("<code>");

        for (var p : purchases) {
            Product targetProduct = p.getProduct();
            totalPrice += p.getCount() * targetProduct.getPrice();

            if (targetProduct.isDiscountable()) {
                discountableTotal += p.getCount() * targetProduct.getPrice();
            } else {
                nonDiscountableTotal += p.getCount() * targetProduct.getPrice();
            }

            messageBuilder.append(p.getPurchaseSummary());
            messageBuilder.append("\n");
        }

        double discountableTotalWithDiscount = discountableTotal * ((double)(100 - discountValue) / 100);
        double totalPriceWithDiscount = discountableTotalWithDiscount + nonDiscountableTotal;
        messageBuilder.append("</code>");
        messageBuilder.append("\n\n--- Итого ---\n");
        messageBuilder.append("Всего: ").append(totalPrice).append("₽");
        messageBuilder.append("\n   Кофе: ").append(discountableTotal).append("₽");
        messageBuilder.append(discountValue > 0 ? "\n   Кофе с учетом скидки " + discountValue + "%: " + discountableTotalWithDiscount +"₽" : "");
        messageBuilder.append("\n   Другие товары: ").append(nonDiscountableTotal).append("₽");
        messageBuilder.append("\n\nВсего к оплате: ").append(totalPriceWithDiscount).append("₽");
        messageBuilder.append("\n\n");
        messageBuilder.append("Внесите оплату и нажмите кнопку \"Оплачено\"\n");
        messageBuilder.append("Оплата: ").append(paymentInstructions).append("\n");

        logger.info(messageBuilder.toString());
        return messageBuilder.toString();
    }
}
