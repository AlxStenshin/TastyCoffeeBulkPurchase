package ru.alxstn.tastycoffeebulkpurchase.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.UpdateHandlerStage;

public interface UpdateHandler {
    boolean handleUpdate(Update update) throws TelegramApiException;

    UpdateHandlerStage getStage();
}
