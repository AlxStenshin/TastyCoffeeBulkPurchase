package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.ShowProductNotAvailableAlertDto;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;


@Component
public class ShowProductUnavailableAlertUpdateHandler extends CallbackUpdateHandler<ShowProductNotAvailableAlertDto> {

    private final ApplicationEventPublisher publisher;

    public ShowProductUnavailableAlertUpdateHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    protected Class<ShowProductNotAvailableAlertDto> getDtoType() {
        return ShowProductNotAvailableAlertDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SHOW_PRODUCT_UNAVAILABLE_ALERT;
    }

    @Override
    protected void handleCallback(Update update, ShowProductNotAvailableAlertDto dto) {
        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Продукт '" + dto.getProductName() + "' недосупен для заказа!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));
    }
}
