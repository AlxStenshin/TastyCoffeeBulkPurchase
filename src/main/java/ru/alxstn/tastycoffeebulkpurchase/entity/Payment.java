package ru.alxstn.tastycoffeebulkpurchase.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

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

    @Column(name = "total_amount_with_discount")
    private BigDecimal totalAmountWithDiscount;

    @Column(name = "total_amount_no_discount")
    private BigDecimal totalAmountNoDiscount;

    @Column(name = "discountable_amount_with_discount")
    private BigDecimal discountableAmountWithDiscount;

    @Column(name = "discountable_amount_no_discount")
    private BigDecimal discountableAmountNoDiscount;

    @Column(name = "non_discountable_amount")
    private BigDecimal NonDiscountableAmount;

    public Payment(Customer customer, Session session) {
        this.customer = customer;
        this.session = session;
        this.paymentStatus = false;
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

    public BigDecimal getTotalAmountWithDiscount() {
        return totalAmountWithDiscount;
    }

    public void setTotalAmountWithDiscount(BigDecimal amount) {
        this.totalAmountWithDiscount = amount;
    }

    public BigDecimal getTotalAmountNoDiscount() {
        return totalAmountNoDiscount;
    }

    public void setTotalAmountNoDiscount(BigDecimal amount) {
        this.totalAmountNoDiscount = amount;
    }

    public BigDecimal getDiscountableAmountWithDiscount() {
        return discountableAmountWithDiscount;
    }

    public void setDiscountableAmountWithDiscount(BigDecimal discountableAmountWithDiscount) {
        this.discountableAmountWithDiscount = discountableAmountWithDiscount;
    }

    public BigDecimal getDiscountableAmountNoDiscount() {
        return discountableAmountNoDiscount;
    }

    public void setDiscountableAmountNoDiscount(BigDecimal discountableAmountNoDiscount) {
        this.discountableAmountNoDiscount = discountableAmountNoDiscount;
    }

    public BigDecimal getNonDiscountableAmount() {
        return NonDiscountableAmount;
    }

    public void setNonDiscountableAmount(BigDecimal totalAmountNonDiscountable) {
        this.NonDiscountableAmount = totalAmountNonDiscountable;
    }
}
