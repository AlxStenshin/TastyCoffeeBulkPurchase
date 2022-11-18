package ru.alxstn.tastycoffeebulkpurchase.entity;


import javax.persistence.*;

@Entity
@Table(name = "customer_settings")
public class Settings {

    @Id
    @Column(name = "customer_id")
    private Long id;

    @MapsId
    @JoinColumn(name = "customer_id")
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Customer customer;

    @Column(name = "discount_notification_notification")
    private boolean receiveDiscountNotification;

    @Column(name = "payment_confirmation_notification")
    private boolean receivePaymentConfirmationNotification;

    public Settings() {
        setDefaults();
    }

    private void setDefaults() {
        setReceiveDiscountNotification(true);
        setReceivePaymentConfirmationNotification(true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean getReceiveDiscountNotification() {
        return receiveDiscountNotification;
    }

    public void setReceiveDiscountNotification(boolean receiveDiscountNotification) {
        this.receiveDiscountNotification = receiveDiscountNotification;
    }

    public boolean isReceiveDiscountNotification() {
        return receiveDiscountNotification;
    }

    public boolean isReceivePaymentConfirmationNotification() {
        return receivePaymentConfirmationNotification;
    }

    public void setReceivePaymentConfirmationNotification(boolean receivePaymentConfirmationNotification) {
        this.receivePaymentConfirmationNotification = receivePaymentConfirmationNotification;
    }
}
