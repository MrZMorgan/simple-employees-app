package com.employees.service;

import com.employees.dto.EmployeeDTO;
import com.employees.dto.EmployeesSimpleAnswer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private final RestTemplate restTemplate;

    public EmployeesService(
        final RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Value("${employees-server.url}")
    private String employeeServerUrl;

    private final static String SIMPLE_ANSWER_URL = "/simple-answer";
    private final static String EMPLOYEES_FILE_NAME = "employees-simple-answer.txt";

    public HttpStatusCode sendProcessedSimpleAnswer() {
        final var simpleAnswer = getSimpleAnswer();
        final var employees = simpleAnswer.employees();
        final var departmentsNames = employees.stream()
            .map(it -> it.department().departmentName())
            .distinct()
            .toList();

        final var processedAnswer = departmentsNames.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                it -> employees.stream()
                    .filter(employee -> employee.department().departmentName().equals(it))
                    .map(employee -> new EmployeeDTO(
                        employee.name(),
                        employee.age()
                    ))
            ));

        writeResponseToFile(simpleAnswer);

        return restTemplate
            .postForEntity(employeeServerUrl + SIMPLE_ANSWER_URL, processedAnswer, String.class)
            .getStatusCode();
    }

    private void writeResponseToFile(final EmployeesSimpleAnswer response) {
        try {
            final var filePath = Path.of(EMPLOYEES_FILE_NAME);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }

            Files.write(filePath, response.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EmployeesSimpleAnswer getSimpleAnswer() {
        return restTemplate
            .getForEntity(employeeServerUrl + SIMPLE_ANSWER_URL, EmployeesSimpleAnswer.class)
            .getBody();
    }
}
