package ru.alxstn.tastycoffeebulkpurchase.model.api.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsResponse {

    @JsonProperty("data")
    private List<ProductData> data;

    public List<ProductData> getData() {
        return data;
    }

    public void setData(List<ProductData> data) {
        this.data = data;
    }
}


