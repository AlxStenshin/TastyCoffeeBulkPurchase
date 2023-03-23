package ru.alxstn.tastycoffeebulkpurchase.entity;

import jakarta.persistence.*;

@Embeddable
public class CustomerNotificationSettings {

    @Column(name = "discount", table = "notification_settings")
    private boolean receiveDiscountNotification;
    private static final String receiveDiscountNotificationDescription = "Уведомления об изменении скидки";

    @Column(name = "payment_confirmation", table = "notification_settings")
    private boolean receivePaymentConfirmationNotification;
    private static final String receivePaymentConfirmationNotificationDescription = "Уведомления об оплате заказов";

    @Column(name = "new_session", table = "notification_settings")
    private boolean receiveNewSessionStartedNotification;
    private static final String receiveNewSessionStartedNotificationDescription = "Уведомления об открытии новой сессии";

    public CustomerNotificationSettings() {
        this.receiveDiscountNotification = false;
        this.receivePaymentConfirmationNotification = false;
        this.receiveNewSessionStartedNotification = true;
    }

    public CustomerNotificationSettings(CustomerNotificationSettings settings) {
        this.receiveDiscountNotification = settings.isReceiveDiscountNotification();
        this.receivePaymentConfirmationNotification = settings.isReceivePaymentConfirmationNotification();
        this.receiveNewSessionStartedNotification = settings.isReceiveNewSessionStartedNotification();
    }

    public boolean isReceiveDiscountNotification() {
        return receiveDiscountNotification;
    }

    public void setReceiveDiscountNotification(boolean receiveDiscountNotification) {
        this.receiveDiscountNotification = receiveDiscountNotification;
    }

    public boolean isReceivePaymentConfirmationNotification() {
        return receivePaymentConfirmationNotification;
    }

    public void setReceivePaymentConfirmationNotification(boolean receivePaymentConfirmationNotification) {
        this.receivePaymentConfirmationNotification = receivePaymentConfirmationNotification;
    }

    public boolean isReceiveNewSessionStartedNotification() {
        return receiveNewSessionStartedNotification;
    }

    public void setReceiveNewSessionStartedNotification(boolean receiveNewSessionStartedNotification) {
        this.receiveNewSessionStartedNotification = receiveNewSessionStartedNotification;
    }
}
