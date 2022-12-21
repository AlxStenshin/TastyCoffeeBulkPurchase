package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.CustomerNotificationSettings;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class SetCustomerNotificationSettingsDto extends SerializableInlineObject {

    private CustomerNotificationSettings settings;

    public SetCustomerNotificationSettingsDto() {
        super(SerializableInlineType.CUSTOMER_SETTINGS);
    }

    public SetCustomerNotificationSettingsDto(CustomerNotificationSettings settings) {
        this();
        this.settings = settings;
    }

    public CustomerNotificationSettings getSettings() {
        return settings;
    }
}
