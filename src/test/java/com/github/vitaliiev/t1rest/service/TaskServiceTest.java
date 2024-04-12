package com.github.vitaliiev.t1rest.service;

import com.github.vitaliiev.t1rest.model.CreateTaskDto;
import com.github.vitaliiev.t1rest.model.Task;
import com.github.vitaliiev.t1rest.model.TaskNotFountException;
import com.github.vitaliiev.t1rest.model.UpdateTaskDto;
import com.github.vitaliiev.t1rest.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest {

	@Autowired
	private TaskRepository repository;
	@Autowired
	private TaskService taskService;

	@Test
	void getTasks_WhenEmpty_ReturnEmptyList() {
		Page<Task> tasks = taskService.getTasks(null);
		assertEquals(0, tasks.getTotalElements());
		assertEquals(0, tasks.getTotalPages());
	}

	@Test
	void getTasks_WhenOne_ReturnOne() {
		Task task = repository.save(createTask("1", "1", LocalDate.now(), true));

		Page<Task> tasks = taskService.getTasks(null);
		assertEquals(1, tasks.getTotalElements());
		assertEquals(1, tasks.getTotalPages());
		tasks.stream().findFirst().ifPresent(t -> assertEquals(t.getId(), task.getId()));
		repository.delete(task);
	}

	@Test
	void getTasks_WhenOnePageParamSet_ReturnOne() {
		Task task = repository.save(createTask("2", "2", LocalDate.now(), true));
		Page<Task> page0 = taskService.getTasks(0);
		assertEquals(1, page0.getTotalElements());
		assertEquals(1, page0.getTotalPages());
		assertEquals(1, page0.getContent().size());

		Page<Task> page1 = taskService.getTasks(1);
		assertEquals(1, page1.getTotalElements());
		assertEquals(1, page1.getTotalPages());
		assertEquals(0, page1.getContent().size());

		repository.delete(task);
	}

	@Test
	void createTask_WhenValidDto_ExpectSuccess() {
		CreateTaskDto createTaskDto = createTaskDto("3", "3", LocalDate.now(), true);
		Task task = taskService.createTask(createTaskDto);
		assertEquals(task.getTitle(), createTaskDto.getTitle());
		assertEquals(task.getDescription(), createTaskDto.getDescription());
		assertEquals(task.getDueDate(), createTaskDto.getDueDate());
		assertEquals(task.getCompleted(), createTaskDto.getCompleted());
		assertTrue(repository.existsById(task.getId()));
		repository.delete(task);
	}

	@Test
	void createTask_WhenValidDtoWithNullsExpectDefault_ExpectSuccess() {
		CreateTaskDto createTaskDto = createTaskDto("4", "4",null, null);
		Task task = taskService.createTask(createTaskDto);
		assertEquals(task.getTitle(), createTaskDto.getTitle());
		assertEquals(task.getDescription(), createTaskDto.getDescription());
		assertNotNull(task.getDueDate());
		assertEquals(task.getCompleted(), false);
		assertTrue(repository.existsById(task.getId()));
		repository.delete(task);
	}

	@Test
	void getTask_WhenExist_ExpectOne() {
		Task task = repository.save(createTask("5", "5", LocalDate.now(), true));
		Task saved = taskService.getTask(task.getId());
		assertEquals(saved.getTitle(), task.getTitle());
		assertEquals(saved.getDescription(), task.getDescription());
		assertEquals(saved.getDueDate(), task.getDueDate());
		assertEquals(saved.getCompleted(), task.getCompleted());
		repository.delete(task);
	}


	@Test
	void getTask_WhenDoesntExist_ExpectTaskNotFound() {
		assertThrows(TaskNotFountException.class ,() -> taskService.getTask(UUID.randomUUID()));
	}

	@Test
	void updateTask_WhenValidDtoAndExists_ExpectSuccess() throws Exception {
		Task task = repository.save(createTask("6", "6", LocalDate.now(), true));
		UpdateTaskDto updateTaskDto = updateTaskDto("6n", "6n", LocalDate.of(1, 1, 1), false);
		Task updated = taskService.updateTask(task.getId(), updateTaskDto);
		assertEquals(updated.getTitle(), updateTaskDto.getTitle());
		assertEquals(updated.getDescription(), updateTaskDto.getDescription());
		assertEquals(updated.getDueDate(), updateTaskDto.getDueDate());
		assertEquals(updated.getCompleted(), updateTaskDto.getCompleted());
		repository.delete(task);
	}


	@Test
	void updateTask_WhenPartialUpdateDtoAndExists_ExpectSuccess() {
		Task task = repository.save(createTask("7", "7", LocalDate.now(), true));
		UpdateTaskDto updateTaskDto = updateTaskDto("7n", "7n", null, false);
		Task updated = taskService.updateTask(task.getId(), updateTaskDto);
		assertEquals(updated.getTitle(), updateTaskDto.getTitle());
		assertEquals(updated.getDescription(), updateTaskDto.getDescription());
		assertEquals(updated.getDueDate(), task.getDueDate());
		assertEquals(updated.getCompleted(), updateTaskDto.getCompleted());
		repository.delete(task);
	}

	@Test
	void updateTask_WhenValidDtoAndDoesntExist_ExpectTaskNotFound() throws Exception {
		UpdateTaskDto updateTaskDto = updateTaskDto("10n", "10n", LocalDate.of(1, 1, 1), false);
		assertThrows(TaskNotFountException.class ,() -> taskService.updateTask(UUID.randomUUID(), updateTaskDto));
	}

	@Test
	void deleteTask_WhenExists_ExpectDeleted() throws Exception {
		Task task = repository.save(createTask("11", "11", LocalDate.now(), true));
		taskService.deleteTask(task.getId());
		assertFalse(repository.existsById(task.getId()));
	}


	@Test
	void deleteTask_WhenDontExist_ExpectTaskNotFound() {
		assertThrows(TaskNotFountException.class ,() -> taskService.deleteTask(UUID.randomUUID()));

	}


	private Task createTask(String title, String description, LocalDate dueDate, Boolean completed) {
		Task task = new Task();
		task.setTitle(title);
		task.setDescription(description);
		task.setDueDate(dueDate);
		task.setCompleted(completed);
		return task;
	}

	private CreateTaskDto createTaskDto(String title, String description, LocalDate dueDate, Boolean completed) {
		return CreateTaskDto.builder()
				.title(title)
				.description(description)
				.dueDate(dueDate)
				.completed(completed)
				.build();
	}


	private UpdateTaskDto updateTaskDto(String title, String description, LocalDate dueDate, Boolean completed) {
		return UpdateTaskDto.builder()
				.title(title)
				.description(description)
				.dueDate(dueDate)
				.completed(completed)
				.build();
	}
}