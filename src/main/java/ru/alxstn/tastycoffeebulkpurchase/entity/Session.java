package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "close_notification_sent")
    private boolean closeSoonNotificationSent;

    @Column(name = "finished")
    private boolean finished;

    @Column(name = "discount")
    private int discountPercentage;

    @Column(name = "coffee_weight")
    private double coffeeWeight;

    @Column(name = "tea_weight")
    private double teaWeight;

    @Column(name = "payment_instruction")
    private String paymentInstruction;

    public Session() {
        this.closed = false;
        this.title = "";
        this.paymentInstruction = "";
        this.discountPercentage = 0;
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

    public boolean isCloseSoonNotificationSent() {
        return closeSoonNotificationSent;
    }

    public void setCloseSoonNotificationSent(boolean closeNotificationSent) {
        this.closeSoonNotificationSent = closeNotificationSent;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getCoffeeWeight() {
        return coffeeWeight;
    }

    public void setCoffeeWeight(double coffeeWeight) {
        this.coffeeWeight = coffeeWeight;
    }

    public double getTeaWeight() {
        return teaWeight;
    }

    public void setTeaWeight(double teaWeight) {
        this.teaWeight = teaWeight;
    }

    public String getPaymentInstruction() {
        return paymentInstruction;
    }

    public void setPaymentInstruction(String paymentInstruction) {
        this.paymentInstruction = paymentInstruction;
    }

    @Override
    public String toString() {
        return "SessionId = "  + id;
    }
}
