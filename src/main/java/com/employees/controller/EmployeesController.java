package com.employees.controller;

import com.employees.service.EmployeesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/employees")
@RestController
public class EmployeesController {

    private final EmployeesService employeesService;

    public EmployeesController(
        final EmployeesService employeesService
    ) {
        this.employeesService = employeesService;
    }

    @GetMapping("/simple-task")
    public ResponseEntity<Void> simpleTask() {
        return new ResponseEntity<>(employeesService.sendProcessedSimpleAnswer());
    }
}
