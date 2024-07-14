package ru.alxstn.tastycoffeebulkpurchase.model.api.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductData {

    private int id;
    private Object category; // Since it's null in JSON
    private int category_id;
    private boolean category_hide_weight;
    private String name;
    private List<Offer> offers;
    private Label label;
    private boolean not_available;
    private Object available_at; // Since it's null in JSON
    private Object type; // Since it's null in JSON
    private boolean has_info;
    private int sort_order;
    private boolean skip_discount;
    private Object card_product; // Since it's null in JSON
    private Info info;
    private String keywords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getCategory() {
        return category;
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public boolean isCategory_hide_weight() {
        return category_hide_weight;
    }

    public void setCategory_hide_weight(boolean category_hide_weight) {
        this.category_hide_weight = category_hide_weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public boolean isNot_available() {
        return not_available;
    }

    public void setNot_available(boolean not_available) {
        this.not_available = not_available;
    }

    public Object getAvailable_at() {
        return available_at;
    }

    public void setAvailable_at(Object available_at) {
        this.available_at = available_at;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public boolean isHas_info() {
        return has_info;
    }

    public void setHas_info(boolean has_info) {
        this.has_info = has_info;
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
    }

    public boolean isSkip_discount() {
        return skip_discount;
    }

    public void setSkip_discount(boolean skip_discount) {
        this.skip_discount = skip_discount;
    }

    public Object getCard_product() {
        return card_product;
    }

    public void setCard_product(Object card_product) {
        this.card_product = card_product;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}