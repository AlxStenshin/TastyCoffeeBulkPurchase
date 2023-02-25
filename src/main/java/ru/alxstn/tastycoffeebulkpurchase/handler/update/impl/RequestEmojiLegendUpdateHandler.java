package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.RemoveMessageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.RequestEmojiLegendCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;

import java.util.List;


@Component
public class RequestEmojiLegendUpdateHandler extends CallbackUpdateHandler<RequestEmojiLegendCommandDto> {

    Logger logger = LogManager.getLogger(RequestEmojiLegendUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;


    public RequestEmojiLegendUpdateHandler(ApplicationEventPublisher publisher, DtoSerializer serializer) {
        this.publisher = publisher;
        this.serializer = serializer;
    }


    @Override
    protected Class<RequestEmojiLegendCommandDto> getDtoType() {
        return RequestEmojiLegendCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SHOW_EMOJI_LEGEND;
    }

    @Override
    protected void handleCallback(Update update, RequestEmojiLegendCommandDto dto) {
        logger.info("Request Emoji Legend Command Received");

        String message = "Условные обозначения:\n\n";
        message += "⭐️ - Сорт недели / Сорт месяца\n";
        message += "\uD83C\uDD95 - Новый продукт\n";
        message += "\uD83E\uDEAB️ - Заканчивается\n";
        message += "\uD83D\uDEAB - Продукт недоступен для заказа (закончился)\n";
        message += "\uD83D\uDC4D - Продукт рекомендован производителем\n";
        message += "\uD83D\uDD25️ - Популярный продукт";


        MenuNavigationBotMessage<String> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle(message);
        answer.setDataSource(List.of());

        RemoveMessageCommandDto removeMessage = new RemoveMessageCommandDto(
                update.getCallbackQuery().getMessage().getMessageId(),
                update.getCallbackQuery().getMessage().getChatId());

        answer.addAdditionalButtons(List.of(InlineKeyboardButton.builder()
                .text("Закрыть")
                .callbackData(serializer.serialize(removeMessage))
                .build()));

        publisher.publishEvent(new UpdateMessageEvent(this, answer.updatePrevious()));
    }
}
