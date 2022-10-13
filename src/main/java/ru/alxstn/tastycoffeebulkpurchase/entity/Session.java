package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    private List<Purchase> purchases = new ArrayList<>();

    @Column(name = "open_date")
    private LocalDateTime dateTimeOpened;

    @Column(name = "close_date")
    private LocalDateTime dateTimeClosed;

    @Column(name = "discount")
    private int discountPercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTimeOpened() {
        return dateTimeOpened;
    }

    public void setDateTimeOpened(LocalDateTime dateTimeOpened) {
        this.dateTimeOpened = dateTimeOpened;
    }

    public LocalDateTime getDateTimeClosed() {
        return dateTimeClosed;
    }

    public void setDateTimeClosed(LocalDateTime dateTimeClosed) {
        this.dateTimeClosed = dateTimeClosed;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
