package be.stijnhooft.portal.todo.messaging;

import be.stijnhooft.portal.todo.model.Task;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventTopic {

    String OUTPUT = "eventTopic";

    @Output(OUTPUT)
    MessageChannel eventTopic();

}