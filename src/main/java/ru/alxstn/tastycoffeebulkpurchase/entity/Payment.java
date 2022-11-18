package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;

@Entity(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "payment_status")
    private boolean paymentStatus;

    @Column(name = "amount")
    private double amount;

    public Payment(Customer customer, Session session) {
        this.customer = customer;
        this.session = session;
        this.paymentStatus = false;
        this.amount = 0d;
    }

    public Payment() {

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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

}
