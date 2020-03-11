package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.services.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableBinding(EventTopic.class)
@Slf4j
public class EventTopicListener {

    private final EventService eventService;

    @Autowired
    public EventTopicListener(EventService eventService) {
        this.eventService = eventService;
    }

    @StreamListener(EventTopic.INPUT)
    public void receive(List<Event> events) {
        log.info("Received events: " + events);
        eventService.receiveEvents(events);
    }

}
