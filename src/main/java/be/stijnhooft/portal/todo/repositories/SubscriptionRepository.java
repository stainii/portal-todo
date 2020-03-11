package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.subscription.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

  List<Subscription> findByOrigin(String origin);

}
