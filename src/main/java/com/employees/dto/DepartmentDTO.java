package com.employees.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DepartmentDTO(
    @JsonProperty("department_name")
    String departmentName,
    List<EmployeeDTO> employees
) {
}
