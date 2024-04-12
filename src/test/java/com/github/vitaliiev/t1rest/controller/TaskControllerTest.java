package com.github.vitaliiev.t1rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vitaliiev.t1rest.model.CreateTaskDto;
import com.github.vitaliiev.t1rest.model.Task;
import com.github.vitaliiev.t1rest.model.UpdateTaskDto;
import com.github.vitaliiev.t1rest.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
class TaskControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TaskRepository repository;
	@Autowired
	private ObjectMapper objectMapper;

	private static final String API_URL = "/tasks";
	private static final String API_URL_TEMPLATE = "/tasks/{id}";

	@Test
	void getTasks_WhenEmpty_ReturnEmptyList() throws Exception {
		mockMvc.perform(get(API_URL))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", empty()));
	}

	@Test
	void getTasks_WhenOne_ReturnOne() throws Exception {
		Task task = repository.save(createTask("1", "1", LocalDate.now(), true));
		mockMvc.perform(get(API_URL))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(1)));
		repository.delete(task);
	}


	@Test
	void getTasks_WhenOnePageParamSet_ReturnOne() throws Exception {
		Task task = repository.save(createTask("1a", "1a", LocalDate.now(), true));
		mockMvc.perform(get(API_URL).param("page", "0"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(1)));
		mockMvc.perform(get(API_URL).param("page", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", hasSize(0)));
		repository.delete(task);
	}

	@Test
	void getTasks_WhenOnePageParamInvalid_ExpectBadRequest() throws Exception {
		mockMvc.perform(get(API_URL).param("page", "-1"))
				.andExpect(status().isBadRequest());
	}


	@Test
	void createTask_WhenValidDto_ExpectSuccess() throws Exception {
		CreateTaskDto createTaskDto = createTaskDto("2", "2", LocalDate.now(), true);
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createTaskDto))
				)
				.andExpect(status().isOk())
				.andDo((r -> {
					String contentAsString = r.getResponse().getContentAsString();
					Task task = assertDoesNotThrow(() -> objectMapper.readValue(contentAsString, Task.class));
					assertEquals(task.getTitle(), createTaskDto.getTitle());
					assertEquals(task.getDescription(), createTaskDto.getDescription());
					assertTrue(repository.existsById(task.getId()));
					repository.delete(task);
				}));
	}

	@Test
	void createTask_WhenEmptyBody_ExpectBadRequest() throws Exception {
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}


	@Test
	void createTask_WhenInvalidDto_ExpectBadRequest() throws Exception {
		CreateTaskDto updateTaskDto = createTaskDto(null, "3", LocalDate.now(), true);
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateTaskDto))
				)
				.andExpect(status().isBadRequest());
	}

	@Test
	void createTask_WhenMalformedDto_ExpectBadRequest() throws Exception {
		LocalDate now = LocalDate.now();
		CreateTaskDto updateTaskDto = createTaskDto("4", "4", now, true);
		String asString = objectMapper.writeValueAsString(updateTaskDto);
		String malformed = asString.replace(now.toString(), "abcde");
		mockMvc.perform(post(API_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(malformed)
				)
				.andExpect(status().isBadRequest());
	}

	@Test
	void getTask_WhenExist_ExpectOne() throws Exception {
		Task task = repository.save(createTask("5", "5", LocalDate.now(), true));
		mockMvc.perform(get(API_URL_TEMPLATE, task.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(r -> {
					String contentAsString = r.getResponse().getContentAsString();
					Task t = assertDoesNotThrow(() -> objectMapper.readValue(contentAsString, Task.class));
					assertEquals(t.getTitle(), task.getTitle());
					assertEquals(t.getDescription(), task.getDescription());
					assertEquals(t.getDueDate(), task.getDueDate());
					assertEquals(t.getCompleted(), task.getCompleted());
				});

		repository.delete(task);
	}

	@Test
	void getTask_WhenDoesntExist_Expect404() throws Exception {
		mockMvc.perform(get(API_URL_TEMPLATE, UUID.randomUUID()))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateTask_WhenValidDtoAndExists_ExpectSuccess() throws Exception {
		Task task = repository.save(createTask("6", "6", LocalDate.now(), true));
		UpdateTaskDto updateTaskDto = updateTaskDto("6n", "6n", LocalDate.of(1, 1, 1), false);
		mockMvc.perform(put(API_URL_TEMPLATE, task.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateTaskDto))
				)
				.andExpect(status().isOk())
				.andDo((r -> {
					String contentAsString = r.getResponse().getContentAsString();
					Task t = assertDoesNotThrow(() -> objectMapper.readValue(contentAsString, Task.class));
					assertEquals(t.getTitle(), updateTaskDto.getTitle());
					assertEquals(t.getDescription(), updateTaskDto.getDescription());
					assertEquals(t.getDueDate(), updateTaskDto.getDueDate());
					assertEquals(t.getCompleted(), updateTaskDto.getCompleted());
				}));
		repository.delete(task);
	}

	@Test
	void updateTask_WhenPartialUpdateDtoAndExists_ExpectSuccess() throws Exception {
		Task task = repository.save(createTask("7", "7", LocalDate.now(), true));
		UpdateTaskDto updateTaskDto = updateTaskDto("7n", "7n", null, false);
		mockMvc.perform(put(API_URL_TEMPLATE, task.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateTaskDto))
				)
				.andExpect(status().isOk())
				.andDo((r -> {
					String contentAsString = r.getResponse().getContentAsString();
					Task t = assertDoesNotThrow(() -> objectMapper.readValue(contentAsString, Task.class));
					assertEquals(t.getTitle(), updateTaskDto.getTitle());
					assertEquals(t.getDescription(), updateTaskDto.getDescription());
					assertEquals(t.getDueDate(), task.getDueDate());
					assertEquals(t.getCompleted(), updateTaskDto.getCompleted());
				}));
		repository.delete(task);
	}

	@Test
	void updateTask_WhenEmptyBodyAndExists_ExpectBadRequest() throws Exception {
		Task task = repository.save(createTask("8", "8", LocalDate.now(), true));
		mockMvc.perform(put(API_URL_TEMPLATE, task.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		repository.delete(task);
	}

	@Test
	void updateTask_WhenMalformedDtoAndExists_ExpectBadRequest() throws Exception {
		Task task = repository.save(createTask("9", "9", LocalDate.now(), true));
		LocalDate now = LocalDate.now();
		UpdateTaskDto updateTaskDto = updateTaskDto("9", "9", now, true);
		String asString = objectMapper.writeValueAsString(updateTaskDto);
		String malformed = asString.replace(now.toString(), "abcde");
		mockMvc.perform(put(API_URL_TEMPLATE, task.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(malformed)))
				.andExpect(status().isBadRequest());
		repository.delete(task);
	}

	@Test
	void updateTask_WhenValidDtoAndDoesntExist_Expect404() throws Exception {
		UpdateTaskDto updateTaskDto = updateTaskDto("10n", "10n", LocalDate.of(1, 1, 1), false);
		mockMvc.perform(put(API_URL_TEMPLATE, UUID.randomUUID())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateTaskDto))
				)
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTask_WhenExists_ExpectOk() throws Exception {
		Task task = repository.save(createTask("11", "11", LocalDate.now(), true));
		mockMvc.perform(delete(API_URL_TEMPLATE, task.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		repository.delete(task);
	}


	@Test
	void deleteTask_WhenDontExist_Expect404() throws Exception {
		mockMvc.perform(delete(API_URL_TEMPLATE, UUID.randomUUID())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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