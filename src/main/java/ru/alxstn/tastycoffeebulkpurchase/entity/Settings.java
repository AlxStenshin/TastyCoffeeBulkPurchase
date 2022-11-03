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

    @Column(name = "discount_notification")
    private boolean receiveDiscountNotification;

    public Settings() {
        setDefaults();
    }

    private void setDefaults() {
        setReceiveDiscountNotification(true);
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

    public boolean isReceiveDiscountNotification() {
        return receiveDiscountNotification;
    }

    public void setReceiveDiscountNotification(boolean receiveDiscountNotification) {
        this.receiveDiscountNotification = receiveDiscountNotification;
    }

}
