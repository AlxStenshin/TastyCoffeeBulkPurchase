package ru.alxstn.tastycoffeebulkpurchase.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tasty-coffee")
public class TastyCoffeeConfigProperties {

    String url;
    String userName;
    String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
