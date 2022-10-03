package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.Id;
import javax.persistence.Entity;
import java.time.LocalDateTime;


@Entity(name = "customer")
public class Customer {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private LocalDateTime registrationTimestamp;

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

    public void setRegistrationTimestamp(LocalDateTime registeredAt) {
        this.registrationTimestamp = registeredAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registrationTimestamp +
                '}';
    }
}
