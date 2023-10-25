package com.employees.dto;

public record EmployeeResponse(
    String name,
    byte age,
    DepartmentResponse department
) {
}
