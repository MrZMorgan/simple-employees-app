package com.employees.dto;

import java.util.List;

public record EmployeesSimpleAnswer(
    List<EmployeeResponse> employees
) {
}
