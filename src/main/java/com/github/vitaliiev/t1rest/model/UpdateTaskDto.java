package com.github.vitaliiev.t1rest.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UpdateTaskDto {
	private String title;
	private String description;
	private LocalDate dueDate;
	private Boolean completed;
}
