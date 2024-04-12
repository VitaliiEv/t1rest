package com.github.vitaliiev.t1rest.controller;

import com.github.vitaliiev.t1rest.model.CreateTaskDto;
import com.github.vitaliiev.t1rest.model.Task;
import com.github.vitaliiev.t1rest.model.UpdateTaskDto;
import com.github.vitaliiev.t1rest.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "tasks", produces = APPLICATION_JSON_VALUE)
public class TaskController {

	private final TaskService taskService;

	@GetMapping
	public Page<Task> getTasks(@RequestParam(required = false) @PositiveOrZero Integer page) {
		return taskService.getTasks(page);
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	public Task createTask(@RequestBody @Valid CreateTaskDto task) {
		return taskService.createTask(task);
	}

	@GetMapping("/{id}")
	public Task getTask(@PathVariable UUID id) {
		return taskService.getTask(id);
	}

	@PutMapping("/{id}")
	public Task updateTask(@PathVariable UUID id, @RequestBody @Valid UpdateTaskDto task) {
		return taskService.updateTask(id, task);
	}

	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable UUID id) {
		taskService.deleteTask(id);
	}
}
