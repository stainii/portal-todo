package be.stijnhooft.portal.todo.controllers;

import be.stijnhooft.portal.todo.model.subscription.Subscription;
import be.stijnhooft.portal.todo.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

  private final SubscriptionService subscriptionService;

  @Autowired
  public SubscriptionController(SubscriptionService subscriptionService) {
    this.subscriptionService = subscriptionService;
  }

  @RequestMapping("/")
  public List<Subscription> findAll() {
    return subscriptionService.findAll();
  }

  @RequestMapping(method = RequestMethod.POST, path = "/")
  public Subscription create(@RequestBody Subscription subscription) {
    return subscriptionService.createOrUpdate(subscription);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
  public Subscription update(@PathVariable("id") String id, @RequestBody Subscription subscription) {
    if (!subscription.getId().equals(id)) {
      throw new IllegalArgumentException("Id in subscription json (" + subscription.getId() + ") does not correspond to id in url (" + id +')');
    }
    return subscriptionService.createOrUpdate(subscription);
  }
}
