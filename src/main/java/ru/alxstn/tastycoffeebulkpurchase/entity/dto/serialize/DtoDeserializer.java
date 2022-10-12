package ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize;

import java.util.Optional;

public interface DtoDeserializer {
    <T> Optional<T> deserialize(String json, Class<T> c);
}
