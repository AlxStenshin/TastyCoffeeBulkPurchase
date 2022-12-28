package ru.alxstn.tastycoffeebulkpurchase.entity;

import ru.alxstn.tastycoffeebulkpurchase.util.BigDecimalUtil;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "count")
    private Integer count;

    public Purchase() { }

    public Purchase(Customer customer, Product product, Session session, Integer count) {
        this.customer = customer;
        this.product = product;
        this.session = session;
        this.count = count;
    }


    public Purchase(Purchase purchase, int newCount) {
        this.id = purchase.getId();
        this.customer = purchase.getCustomer();
        this.product = purchase.getProduct();
        this.session = purchase.getSession();
        this.count = newCount;
    }

    public Purchase(Purchase purchase, String newForm) {
        this.id = purchase.getId();
        this.customer = purchase.getCustomer();
        this.product = purchase.getProduct();
        this.session = purchase.getSession();
        purchase.getProduct().setProductForm(newForm);
        this.count = purchase.count;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "customer=" + customer +
                ", product=" + product +
                ", count=" + count +
                '}';
    }

    public String getPurchaseSummary() {
        String summary = product.getShortName();
        summary += ", " + getProductCountAndTotalPrice();
        return summary;
    }

    public String getProductCountAndTotalPrice() {
        return getCount() + " шт, " + getTotalPrice() + "₽";
    }

    public BigDecimal getTotalPrice() {
        return BigDecimalUtil.multiplyByInt(getProduct().getPrice(), getCount());
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
