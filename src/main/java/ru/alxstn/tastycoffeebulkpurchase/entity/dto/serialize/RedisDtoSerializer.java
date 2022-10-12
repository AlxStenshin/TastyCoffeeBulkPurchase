package ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.repository.TelegramCallbackRepository;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class RedisDtoSerializer implements DtoSerializer {

    private final TelegramCallbackRepository repository;

    public RedisDtoSerializer(TelegramCallbackRepository repository) {
        this.repository = repository;
    }

    @Override
    public String serialize(Object o) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(new ObjectMapper().writeValueAsString(o).getBytes());
            String myHash = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
            SerializableInlineObject obj = (SerializableInlineObject) o;
            obj.setId(myHash);
            repository.save(obj);
            return myHash;
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
