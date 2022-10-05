package ru.alxstn.tastycoffeebulkpurchase.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.UpdateHandlerStage;

public interface UpdateHandler {
    boolean handleUpdate(Update update);

    UpdateHandlerStage getStage();
}
