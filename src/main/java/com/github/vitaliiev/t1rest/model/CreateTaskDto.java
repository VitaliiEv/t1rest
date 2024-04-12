package com.github.vitaliiev.t1rest.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CreateTaskDto {
	@NotNull
	private String title;
	@NotNull
	private String description;
	private LocalDate dueDate;
	private Boolean completed;
}
