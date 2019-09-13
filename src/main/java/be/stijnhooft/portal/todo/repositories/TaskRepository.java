package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.Task;
import be.stijnhooft.portal.todo.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByStartDateTimeLessThanAndStatus(LocalDateTime startDateTime, TaskStatus status);

}
