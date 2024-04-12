package com.github.vitaliiev.t1rest.service;

import com.github.vitaliiev.t1rest.model.CreateTaskDto;
import com.github.vitaliiev.t1rest.model.Task;
import com.github.vitaliiev.t1rest.model.UpdateTaskDto;
import com.github.vitaliiev.t1rest.model.TaskNotFountException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public interface TaskService {

	Page<Task> getTasks(Integer page);

	@Validated
	Task createTask(@Valid @NotNull CreateTaskDto createTaskDto);

	@Validated
	Task getTask(@NotNull UUID id) throws TaskNotFountException;

	@Validated
	Task updateTask(@NotNull UUID id, @Valid @NotNull UpdateTaskDto updateTaskDto) throws TaskNotFountException;

	@Validated
	void deleteTask(@NotNull UUID id) throws TaskNotFountException;
}
