package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SerializableInlineObject {

    @JsonProperty("i")
    private int index;

    public SerializableInlineObject(SerializableInlineType type) {
        this.index = type.getIndex();
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializableInlineObject that = (SerializableInlineObject) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash( index);
    }

}
