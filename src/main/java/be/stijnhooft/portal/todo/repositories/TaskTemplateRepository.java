package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.TaskTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateRepository extends MongoRepository<TaskTemplate, String> {
}
