package ru.alxstn.tastycoffeebulkpurchase.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DtoDeserializer {
    public static <T> Optional<T> deserialize(String json, Class<T> c) {
        try {
            return Optional.ofNullable(new ObjectMapper().readValue(json, c));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
