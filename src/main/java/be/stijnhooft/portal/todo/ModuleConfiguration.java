package be.stijnhooft.portal.todo;

import be.stijnhooft.portal.todo.jsondeserializers.LocalDateTimeDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;

@Configuration
public class ModuleConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .registerModule(createLocalDateTimeDeserializerModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private SimpleModule createLocalDateTimeDeserializerModule() {
        return new SimpleModule("LocalDateDeserializer", new Version(1, 0, 0, null, null, null))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

    }

}
