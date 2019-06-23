package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.TaskPatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskPatchRepository extends MongoRepository<TaskPatch, String> {

    List<TaskPatch> findByDateAfter(LocalDateTime startDateTime);
    
}
