package be.stijnhooft.portal.todo.model;

import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "taskIdGenerator",
        sequenceName = "task_id_sequence")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Task {

    @Id
    @GeneratedValue(generator = "taskIdGenerator")
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    /**
     * Optional date time, which indicates when a task becomes relevant to pick up.
     * Before this date time, the task should not be shown to the user.
     **/
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "due_date_time")
    private LocalDateTime dueDateTime;

    @Column(name = "expected_duration")
    private Duration expectedDuration;

    @NonNull
    @Column(nullable = false)
    private String context;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(nullable = false)
    private Importance importance;

    private String description;

    @OneToMany
    @JoinColumn(name = "main_task_id")
    private List<Task> subTasks = new ArrayList<>();

    @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

}
