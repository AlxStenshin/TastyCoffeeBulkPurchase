package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "open_date")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime dateTimeOpened;

    @Column(name = "close_date")
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime dateTimeClosed;

    @Column(name = "closed")
    private boolean closed;

    @Column(name = "discount")
    private int discountPercentage;

    @Column(name = "total_weight")
    private double totalWeight;

    @Column(name = "discountable_weight")
    private double discountableWeight;

    public Session() {
        this.closed = false;
        this.title = "";
        this.discountPercentage = 0;
        this.discountableWeight = 0;
        this.totalWeight = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountableWeight() {
        return discountableWeight;
    }

    public void setDiscountableWeight(double discountableWeight) {
        this.discountableWeight = discountableWeight;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    @Override
    public String toString() {
        return "SessionId = "  + id;
    }
}
