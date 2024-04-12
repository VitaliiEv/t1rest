package com.github.vitaliiev.t1rest.repository;

import com.github.vitaliiev.t1rest.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
