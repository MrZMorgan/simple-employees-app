package com.employees.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepartmentResponse(
    long id,

    @JsonProperty("department_name")
    String departmentName
) {
}
