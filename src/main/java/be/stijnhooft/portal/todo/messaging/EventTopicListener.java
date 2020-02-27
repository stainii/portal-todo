package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.model.domain.Event;
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

    @Autowired
    public EventTopicListener() {

    }

    @StreamListener(EventTopic.INPUT)
    public void log(List<Event> events) {
        log.info("events coming in! {}", events);
    }

}
