package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.MainMenuCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuCommandHandler extends CallbackUpdateHandler<MainMenuCommandDto> {

    Logger logger = LogManager.getLogger(MainMenuCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private  DtoDeserializer deserializer;

    public MainMenuCommandHandler(ApplicationEventPublisher publisher,
                                  ProductRepository productRepository,
                                  DtoSerializer serializer){
        super();
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<MainMenuCommandDto> getDtoType() {
        return MainMenuCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_MAIN_COMMAND;
    }

    @Override
    protected void handleCallback(Update update, MainMenuCommandDto dto) {
        String message = dto.getMessage();
        logger.info("command received " + message);

        String title = "Выберите категорию:";
        List<String> availableCategories = productRepository.findAllCategories();

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(new ArrayList<>());

        for (String cat : availableCategories) {
            String buttonTitle = cat.substring(0, Math.min(cat.length(), 21));

            String callback = serializer.serialize(
                    new SetCategoryCommandDto(cat));
            assert (callback.length() < 64);

            logger.debug("Trying to set callback (" + callback.length() + ") to button: " + callback);

            if (keyboardRows.get(keyboardRows.size() - 1).size() == 2) {
                keyboardRows.add(new ArrayList<>());
            }
            List<InlineKeyboardButton> currentRow = keyboardRows.get(keyboardRows.size() - 1);

            currentRow.add(InlineKeyboardButton.builder()
                    .text(buttonTitle)
                    .callbackData(callback)
                    .build());
        }

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkup.builder();
        for (var row : keyboardRows) {
            builder.keyboardRow(row);
        }

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                        .text(title)
                        .parseMode("html")
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .replyMarkup(builder.build())
                        .build()));
    }
}
