package com.example.dtos;

import java.io.Serializable;

/**
 * DTO for {@link com.example.entities.Employee}
 */
public record EmployeeDto(String firstName, String lastName) implements Serializable {
}
