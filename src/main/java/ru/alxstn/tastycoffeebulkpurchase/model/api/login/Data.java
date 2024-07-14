package ru.alxstn.tastycoffeebulkpurchase.model.api.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.alxstn.tastycoffeebulkpurchase.model.api.product.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;

    @JsonIgnore
    @JsonProperty("user")
    private User user;

    @JsonIgnore
    @JsonProperty("expires_in")
    private String expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String  expiresIn) {
        this.expiresIn = expiresIn;
    }
}