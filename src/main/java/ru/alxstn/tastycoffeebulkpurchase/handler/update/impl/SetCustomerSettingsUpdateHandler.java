package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.CustomerNotificationSettings;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RemoveMessageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetCustomerNotificationSettingsDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class SetCustomerSettingsUpdateHandler extends CallbackUpdateHandler<SetCustomerNotificationSettingsDto> {

    Logger logger = LogManager.getLogger(SetCustomerSettingsUpdateHandler.class);
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;


    public SetCustomerSettingsUpdateHandler(CustomerRepository customerRepository,
                                            ApplicationEventPublisher publisher,
                                            DtoSerializer serializer) {
        this.customerRepository = customerRepository;
        this.publisher = publisher;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetCustomerNotificationSettingsDto> getDtoType() {
        return SetCustomerNotificationSettingsDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.CUSTOMER_SETTINGS;
    }

    @Override
    protected void handleCallback(Update update, SetCustomerNotificationSettingsDto dto) {
        Long eventEmitterId = update.getCallbackQuery().getMessage().getChatId();
        logger.info("Command received: Change Customer Settings, ChatId: " + eventEmitterId);
        Customer eventEmitter = customerRepository.getByChatId(eventEmitterId);

        CustomerNotificationSettings eventEmitterSettings = dto.getSettings();
        eventEmitter.setNotificationSettings(eventEmitterSettings);
        customerRepository.save(eventEmitter);

        MenuNavigationBotMessage<Field> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle("Измените настройки: ");
        answer.setDataSource(getProperties(eventEmitterSettings));
        answer.setButtonCreator(field -> InlineKeyboardButton.builder()
                .text(buildButtonCaption(eventEmitterSettings, field))
                .callbackData(buildButtonCallback(eventEmitterSettings, field))
                .build());

        // Close Settings Button just removes previous (current settings) bot message
        RemoveMessageCommandDto removeMessage = new RemoveMessageCommandDto(
                update.getCallbackQuery().getMessage().getMessageId(),
                update.getCallbackQuery().getMessage().getChatId());

        answer.addAdditionalButtons(List.of(InlineKeyboardButton.builder()
                .text("Закрыть")
                .callbackData(serializer.serialize(removeMessage))
                .build()));

        publisher.publishEvent(new UpdateMessageEvent(this, answer.updatePrevious()));
    }

    private List<Field> getProperties(Object settings) {
        List<Field> properties = new ArrayList<>();
        ReflectionUtils.doWithFields(settings.getClass(),
                field -> {
                    if (field.getType().isPrimitive()) {
                        properties.add(field);
                    }
                });
        return properties;
    }

    private String buildButtonCaption(CustomerNotificationSettings settings, Field field) {
        return getStringValueIconRepresentation(getFieldValue(settings, field).toString()) + " " +
                getFieldValue(settings, getPropertyDescriptionField(settings, field)).toString();
    }

    private String buildButtonCallback(CustomerNotificationSettings settings, Field property) {
        CustomerNotificationSettings newSettings =  switchBooleanPropertyByDescription(settings, property);
        return serializer.serialize(new SetCustomerNotificationSettingsDto(newSettings));
    }

    private Field getPropertyDescriptionField(Object obj, Field property) {
        try {
            return obj.getClass().getDeclaredField(property.getName() + "Description");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Error getting property description value: " + e);
        }
    }

    private CustomerNotificationSettings switchBooleanPropertyByDescription(
            CustomerNotificationSettings settings, Field property) {
        CustomerNotificationSettings newSettings = new CustomerNotificationSettings(settings);
        Object currentValue = getFieldValue(newSettings, property);
        property.setAccessible(true);
        try {
            property.set(newSettings, !(boolean) currentValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error switching property value: " + e);
        }
        return newSettings;
    }

    private Object getFieldValue(Object obj, Field property) {
        try {
            property.setAccessible(true);
            return property.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error getting property value: " + e);
        }
    }

    private String getStringValueIconRepresentation(String result) {
        return result.equals("true") ? "✅" :
                result.equals("false") ? "❌" : result;
    }
}
