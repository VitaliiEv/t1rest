package com.github.vitaliiev.t1rest.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaskNotFountException extends RuntimeException {

	public TaskNotFountException(UUID uuid) {
		super(String.format("Task with id [%s] not found", uuid.toString()));
	}
}
