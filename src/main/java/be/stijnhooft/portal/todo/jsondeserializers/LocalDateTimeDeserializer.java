package be.stijnhooft.portal.todo.jsondeserializers;

import be.stijnhooft.portal.todo.utils.DateTimeUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * The JavaDateTime module of Jackson does not take the timezone into account when deserializing to a LocalDateTime.
 * This implementation does take into account the timezone.
 *
 * Example, time zone Europe/Brussels:
 * String "2020-01-29 23:00Z" should be deserialized to local date time 2020-01-30 00:00:00.
 */
public class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public LocalDateTimeDeserializer() {
        this(null);
    }

    protected LocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return DateTimeUtils.parseAsLocalDateTime(jsonParser.getValueAsString());
    }

}
