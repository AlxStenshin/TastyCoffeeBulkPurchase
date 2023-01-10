package ru.alxstn.tastycoffeebulkpurchase.handler.update;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;

import java.util.Optional;
public abstract class CallbackUpdateHandler <T extends SerializableInlineObject> implements UpdateHandler {

    @Autowired
    private DtoDeserializer deserializer;

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
        Optional<T> dto = deserializer.deserialize(data, getDtoType());

        if (dto.isEmpty() || dto.get().getIndex() != getSerializableType().getIndex()) {
            return false;
        }

        handleCallback(update, dto.get());
        return true;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.CALLBACK;
    }
}
