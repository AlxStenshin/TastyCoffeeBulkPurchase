package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CustomerNotificationSettings {

    @Column(name = "discount", table = "notification_settings")
    private boolean receiveDiscountNotification;
    private static final String receiveDiscountNotificationDescription = "Уведомления об изменении скидки";

    @Column(name = "payment_confirmation", table = "notification_settings")
    private boolean receivePaymentConfirmationNotification;
    private static final String receivePaymentConfirmationNotificationDescription = "Уведомления об оплате закзазов";

    public CustomerNotificationSettings() {
        this.receiveDiscountNotification = false;
        this.receivePaymentConfirmationNotification = false;
    }

    public CustomerNotificationSettings(CustomerNotificationSettings settings) {
        this.receiveDiscountNotification = settings.isReceiveDiscountNotification();
        this.receivePaymentConfirmationNotification = settings.isReceivePaymentConfirmationNotification();
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
}
