package ru.alxstn.tastycoffeebulkpurchase.dto.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class BasicDtoDeserializer implements DtoDeserializer {
    @Override
    public <T> Optional<T> deserialize(String json, Class<T> c) {
        try {
            return Optional.ofNullable(new ObjectMapper().readValue(json, c));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
