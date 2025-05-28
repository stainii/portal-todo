package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Configuration
@Component
@Slf4j
@RequiredArgsConstructor
public class EventListener {

    private final EventService eventService;

    @Bean
    public Consumer<List<Event>> eventChannel() {
        return eventService::receiveEvents;
    }

}
