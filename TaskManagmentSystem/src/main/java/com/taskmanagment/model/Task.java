package com.taskmanagment.model;

import com.taskmanagment.model.enums.ActivityStatus;
import com.taskmanagment.model.enums.ExecutionStatus;
import com.taskmanagment.model.enums.Priority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "t_task")
public class Task {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID taskId;

    private String title;
    private String description;

    private UUID authorId;
    private UUID executorId;
    private ExecutionStatus status;
    private Priority taskPriority;
    private ActivityStatus availability;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;
}
