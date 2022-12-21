package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;

@Repository
public interface TelegramCallbackRepository extends CrudRepository<SerializableInlineObject, String> {
}
