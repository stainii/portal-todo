package be.stijnhooft.portal.todo.repositories;

import be.stijnhooft.portal.todo.model.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {
}
