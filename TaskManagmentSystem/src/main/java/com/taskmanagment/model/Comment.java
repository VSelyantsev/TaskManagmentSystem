package com.taskmanagment.model;

import com.taskmanagment.model.enums.ActivityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "t_entity")
public class Comment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID commentId;

    private String body;
    private ActivityStatus availability;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "taskId")
    private Task task;

}
