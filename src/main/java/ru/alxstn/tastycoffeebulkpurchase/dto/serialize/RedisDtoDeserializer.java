package ru.alxstn.tastycoffeebulkpurchase.dto.serialize;

import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.repository.TelegramCallbackRepository;

import java.util.Optional;

@Component
public class RedisDtoDeserializer implements DtoDeserializer {

    private final TelegramCallbackRepository repository;

    public RedisDtoDeserializer(TelegramCallbackRepository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> deserialize(String json, Class<T> c) {
        return (Optional<T>) repository.findById(json);
    }
}
