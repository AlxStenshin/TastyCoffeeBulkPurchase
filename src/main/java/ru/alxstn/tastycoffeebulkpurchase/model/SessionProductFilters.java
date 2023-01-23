package ru.alxstn.tastycoffeebulkpurchase.model;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;

public class SessionProductFilters {

    private Session session;
    private SessionProductFilterType filterType;
    private List<ProductTypeFilter> productTypeFilters;

    public SessionProductFilters(List<ProductTypeFilter> productTypeFilters, SessionProductFilterType filterType) {
        this.productTypeFilters = productTypeFilters;
        this.filterType = filterType;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public SessionProductFilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(SessionProductFilterType filterType) {
        this.filterType = filterType;
    }

    public List<ProductTypeFilter> getProductTypeFilters() {
        return productTypeFilters;
    }

    public void setProductTypeFilters(List<ProductTypeFilter> productTypeFilters) {
        this.productTypeFilters = productTypeFilters;
    }

    @Override
    public String toString() {
        return  "session=" + session + "\n" +
                "Filters=" + productTypeFilters;
    }

}
