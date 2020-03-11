package be.stijnhooft.portal.todo.dtos;

import be.stijnhooft.portal.model.domain.Event;
import be.stijnhooft.portal.todo.model.subscription.Subscription;
import lombok.*;

/** Combination of a subscription and the event for which it fires **/
@AllArgsConstructor
@Getter
@ToString @EqualsAndHashCode
public class FiringSubscription {

  @NonNull
  private Subscription subscription;

  @NonNull
  private Event event;

}
