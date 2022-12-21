package ru.alxstn.tastycoffeebulkpurchase.dto.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicDtoSerializer implements DtoSerializer {
    @Override
    public String serialize(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize " + o, e);
        }
    }
}
