package ru.alxstn.tastycoffeebulkpurchase.entity;

import java.util.List;

public class RequiredProductProperties {

    private Session session;
    private List<RequiredProductType> requiredProductTypes;

    public RequiredProductProperties(List<RequiredProductType> requiredProductTypes) {
        this.requiredProductTypes = requiredProductTypes;
    }

    public List<RequiredProductType> getRequiredProductTypes() {
        return requiredProductTypes;
    }

    public void setRequiredProductTypes(List<RequiredProductType> requiredProductTypes) {
        this.requiredProductTypes = requiredProductTypes;
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
                "requiredProductTypes=" + requiredProductTypes;
    }
}
