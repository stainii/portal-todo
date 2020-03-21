package be.stijnhooft.portal.todo.model.subscription;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Subscription {

    @Id
    private String id;

    /**
     * The origin that sends an data event
     **/
    @NonNull
    private String origin;

    /**
     * To which conditions should the data event apply to fire this subscription. Should be a Spring EL expression.
     * Examples:
     * true (always)
     * data['someProperty'] == "bla"
     **/
    @NonNull
    private String creationCondition;

    /**
     * To which conditions should the data event apply to finish all earlier tasks with the same flowId.
     * Examples:
     * true (always)
     * data['someProperty'] == "bla"
     **/
    @NonNull
    private String completeCondition;

    @NonNull
    private SubscriptionMappingToTask mappingToTask;

}
