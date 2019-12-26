package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.task.TaskPatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskPatchRepository extends MongoRepository<TaskPatch, String> {

    List<TaskPatch> findByDateTimeAfter(Instant startDateTime);
    
}
