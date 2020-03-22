package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.task.Task;
import be.stijnhooft.portal.todo.model.task.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStatus(TaskStatus status);

    Optional<Task> findFirstByFlowIdOrderByCreationDateTimeDesc(String flowId);
}
