package utils.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by Pavel Dudin
 * on 04.10.2017
 * padudin@dasreda.ru
 */
public class ErrorDtoSerializer extends JsonSerializer {

    public ErrorDtoSerializer() {
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        ErrorDto errorObject = (ErrorDto) value;
        gen.writeObjectField(errorObject.getName(), errorObject.getMessage());
        gen.writeEndObject();
    }
}