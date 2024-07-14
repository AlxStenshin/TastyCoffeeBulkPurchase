package ru.alxstn.tastycoffeebulkpurchase.model.api.product;

public class Info {
    private String processing_method;
    private String rating_q;
    private String mini_description;
    private String sku;

    public String getProcessing_method() {
        return processing_method;
    }

    public void setProcessing_method(String processing_method) {
        this.processing_method = processing_method;
    }

    public String getRating_q() {
        return rating_q;
    }

    public void setRating_q(String rating_q) {
        this.rating_q = rating_q;
    }

    public String getMini_description() {
        return mini_description;
    }

    public void setMini_description(String mini_description) {
        this.mini_description = mini_description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
