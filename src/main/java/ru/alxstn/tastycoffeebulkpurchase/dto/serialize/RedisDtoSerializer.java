package ru.alxstn.tastycoffeebulkpurchase.dto.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.annotation.AnnotationExclusionStrategy;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.repository.TelegramCallbackRepository;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class RedisDtoSerializer implements DtoSerializer {

    Logger logger = LogManager.getLogger(RedisDtoSerializer.class);
    private final TelegramCallbackRepository repository;

    public RedisDtoSerializer(TelegramCallbackRepository repository) {
        this.repository = repository;
    }

    @Override
    public String serialize(Object o) {
        try {
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new AnnotationExclusionStrategy())
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();

            JsonElement tree = gson.toJsonTree(o);
            String serialized = gson.toJson(tree);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(serialized.getBytes());
            String myHash = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            SerializableInlineObject obj = (SerializableInlineObject) o;
            obj.setId(myHash);
            repository.save(obj);
            return myHash;
        } catch (NoSuchAlgorithmException | RuntimeException e) {
            logger.error("Error Serializing Redis Object: " + e.getMessage());
            return null;
        }
    }
}
