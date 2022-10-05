package ru.alxstn.tastycoffeebulkpurchase.handler.update;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.util.DtoDeserializer;

import java.util.Optional;

public abstract class CallbackUpdateHandler <T extends SerializableInlineObject> implements UpdateHandler {

    Logger logger = LogManager.getLogger(CallbackUpdateHandler.class);

    protected abstract Class<T> getDtoType();

    protected abstract SerializableInlineType getSerializableType();

    protected abstract void handleCallback(Update update, T dto);

    @Override
    public boolean handleUpdate(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery == null || callbackQuery.getMessage() == null) {
            return false;
        }
        String data = callbackQuery.getData();
        Optional<T> dto = DtoDeserializer.deserialize(data, getDtoType());

        if (dto.isEmpty() || dto.get().getIndex() != getSerializableType().getIndex()) {
            return false;
        }

        logger.info("Found callback {}", getSerializableType());
        handleCallback(update, dto.get());
        return true;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.CALLBACK;
    }
}
