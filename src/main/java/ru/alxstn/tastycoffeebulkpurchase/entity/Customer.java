package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "customer")
@SecondaryTable(name = "notification_settings",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "customer_id"))
public class Customer {

    @Id
    @PrimaryKeyJoinColumn
    @Column(name = "id")
    private Long chatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @CreationTimestamp
    @Column(name = "registration_timestamp")
    private LocalDateTime registrationTimestamp;

    @Embedded
    CustomerNotificationSettings notificationSettings;

    public Customer() {
    }

    public Customer(Long chatId, String firstName, String lastName, String userName) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(LocalDateTime registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    public CustomerNotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(CustomerNotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    @Override
    public String toString() {
        String customer =  firstName == null ? "" : firstName;
        customer +=  lastName == null ? "" : " " + lastName;
        customer +=  userName == null ? "" : " (@" + userName + ")";
        return customer;
    }
}

