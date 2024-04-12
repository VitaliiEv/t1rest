package com.github.vitaliiev.t1rest.service;

import com.github.vitaliiev.t1rest.model.CreateTaskDto;
import com.github.vitaliiev.t1rest.model.Task;
import com.github.vitaliiev.t1rest.model.TaskNotFountException;
import com.github.vitaliiev.t1rest.model.UpdateTaskDto;
import com.github.vitaliiev.t1rest.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {

	private final TaskRepository taskRepository;

	private static final int PAGE_SIZE = 100;

	@Override
	@Transactional(readOnly = true)
	public Page<Task> getTasks(Integer page) {
		PageRequest pageRequest = PageRequest.of(page == null ? 0 : page, PAGE_SIZE);
		return taskRepository.findAll(pageRequest);
	}

	@Override
	@Transactional
	public Task createTask(CreateTaskDto createTaskDto) {
		Task task = new Task();
		task.setTitle(createTaskDto.getTitle());
		task.setDescription(createTaskDto.getDescription());
		task.setDueDate(createTaskDto.getDueDate() == null ? LocalDate.now() : createTaskDto.getDueDate());
		task.setCompleted(Boolean.TRUE.equals(createTaskDto.getCompleted()));
		return taskRepository.save(task);
	}

	@Override
	@Transactional(readOnly = true)
	public Task getTask(UUID id) throws TaskNotFountException {
		return taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFountException(id));
	}

	@Override
	@Transactional
	public Task updateTask(UUID id, UpdateTaskDto updateTaskDto) throws TaskNotFountException {
		return taskRepository.findById(id)
				.map(t -> updateFields(t, updateTaskDto))
				.orElseThrow(() -> new TaskNotFountException(id));
	}

	@Override
	@Transactional
	public void deleteTask(UUID id) throws TaskNotFountException {
		if (taskRepository.existsById(id)) {
			taskRepository.deleteById(id);
		} else {
			throw new TaskNotFountException(id);
		}
	}

	private Task updateFields(Task task, UpdateTaskDto updateTaskDto) {
		if (updateTaskDto.getTitle() != null) {
			task.setTitle(updateTaskDto.getTitle());
		}
		if (updateTaskDto.getDescription() != null) {
			task.setDescription(updateTaskDto.getDescription());
		}
		if (updateTaskDto.getDueDate() != null) {
			task.setDueDate(updateTaskDto.getDueDate());
		}
		if (updateTaskDto.getCompleted() != null) {
			task.setCompleted(updateTaskDto.getCompleted());
		}
		return task;
	}
}
