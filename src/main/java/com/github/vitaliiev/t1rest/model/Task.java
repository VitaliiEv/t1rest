package com.github.vitaliiev.t1rest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@NotNull
	@Column(nullable = false)
	private String title;
	@Lob
	@Column(nullable = false)
	private String description;
	@NotNull
	private LocalDate dueDate;
	@NotNull
	private Boolean completed;
}
