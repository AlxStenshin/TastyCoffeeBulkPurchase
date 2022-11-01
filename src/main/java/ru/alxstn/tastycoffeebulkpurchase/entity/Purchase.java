package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;

@Entity
@Table(name = "purchase")
//@NamedEntityGraph(name = "purchase-graph", attributeNodes = {
//        @NamedAttributeNode("customer"),
//        @NamedAttributeNode("product"),
//        @NamedAttributeNode("session"),
//
//})
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

    @Column(name = "product_form")
    private String productForm;

    @Column(name = "count")
    private Integer count;

    public Purchase() { }

    public Purchase(Customer customer, Product product, Session session, String productForm, Integer count) {
        this.customer = customer;
        this.product = product;
        this.session = session;
        this.productForm = productForm;
        this.count = count;
    }

    public Purchase(Purchase purchase, int newCount) {
        this.id = purchase.getId();
        this.customer = purchase.getCustomer();
        this.product = purchase.getProduct();
        this.session = purchase.getSession();
        this.productForm = purchase.getProductForm();
        this.count = newCount;
    }

    public Purchase(Purchase purchase, String newForm) {
        this.id = purchase.getId();
        this.customer = purchase.getCustomer();
        this.product = purchase.getProduct();
        this.session = purchase.getSession();
        this.productForm = newForm;
        this.count = purchase.count;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "customer=" + customer +
                ", product=" + product +
                ", productForm='" + productForm + '\'' +
                ", count=" + count +
                '}';
    }

    public String getProductCountAndTotalPrice() {
        return getCount() + " шт, " + getProduct().getPrice() * getCount() + "₽";
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

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm(String productForm) {
        this.productForm = productForm;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
