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
@Entity(name = "t_comment")
public class Comment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID commentId;

    private String body;

    @Enumerated(EnumType.STRING)
    private ActivityStatus availability;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "taskId")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "author_comment_id", referencedColumnName = "userId")
    private User user;

}
