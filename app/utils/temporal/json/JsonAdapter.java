package utils.temporal.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Denis Danilin | denis@danilin.name
 * 02.10.2017 17:20
 * core-router ☭ sweat and blood
 */
public class JsonAdapter {
    public static class LocalTimedS extends JsonSerializer<LocalTime> {
        @Override
        public void serialize(final LocalTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeString(value == null ? "" : value.format(DateTimeFormatter.ISO_LOCAL_TIME));
        }
    }

    public static class LocalDatedS extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(final LocalDate value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeString(value == null ? "" : value.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    public static class LocalDateTimedS extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeString(value == null ? "" : value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    public static class ZonedDateTimedS extends JsonSerializer<ZonedDateTime> {
        @Override
        public void serialize(final ZonedDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeString(value == null ? "" : value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }
    }
    public static class LocalTimedD extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return LocalTime.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_LOCAL_TIME);
        }
    }

    public static class LocalDatedD extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return LocalDate.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static class LocalDateTimedD extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return LocalDateTime.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public static class ZonedDateTimedD extends JsonDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return ZonedDateTime.parse(p.readValueAs(String.class), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }
}
