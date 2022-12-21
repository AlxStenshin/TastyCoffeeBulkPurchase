package ru.alxstn.tastycoffeebulkpurchase.dto;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Objects;

@RedisHash("SerializableInlineObject")
public class SerializableInlineObject implements Serializable {

    private String id;

    private int index;

    private SerializableInlineObject previous;

    public SerializableInlineObject(SerializableInlineType type) {
        this.index = type.getIndex();
    }

    public int getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SerializableInlineObject getPrevious() {
        return previous;
    }

    public void setPrevious(SerializableInlineObject previous) {
        this.previous = previous;
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
