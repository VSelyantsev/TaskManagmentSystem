package com.taskmanagment.repository;

import com.taskmanagment.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    boolean existsByTaskIdAndAuthorId(UUID taskId, UUID authorId);
    Page<Task> findAllByExecutorId(UUID executorId, Pageable pageable);
    Optional<Task> findByTitle(String title);
}
