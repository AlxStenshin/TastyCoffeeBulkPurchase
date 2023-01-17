package ru.alxstn.tastycoffeebulkpurchase.entity;

import java.util.List;

public class DiscardedProductProperties {

    private Session session;
    private List<DiscardedProductType> discardedProductTypes;

    public DiscardedProductProperties(List<DiscardedProductType> discardedProductTypes) {
        this.discardedProductTypes = discardedProductTypes;
    }

    public List<DiscardedProductType> getDiscardedProductTypes() {
        return discardedProductTypes;
    }

    public void setDiscardedProductTypes(List<DiscardedProductType> discardedProductTypes) {
        this.discardedProductTypes = discardedProductTypes;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return  "session=" + session + "\n" +
                "discardedProductTypes=" + discardedProductTypes;
    }
}
