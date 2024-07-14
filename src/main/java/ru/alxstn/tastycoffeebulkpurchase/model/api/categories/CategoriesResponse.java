package ru.alxstn.tastycoffeebulkpurchase.model.api.categories;

import java.util.List;

public class CategoriesResponse {

    private List<CategoryData> data;
    private Object meta;

    public List<CategoryData> getData() {
        return data;
    }

    public void setData(List<CategoryData> data) {
        this.data = data;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }
}
