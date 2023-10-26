package com.employees.service;

import com.employees.dto.DepartmentDTO;
import com.employees.dto.EmployeeDTO;
import com.employees.dto.EmployeeResponse;
import com.employees.dto.EmployeesSimpleAnswer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();

    public EmployeesService(
        final RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Value("${employees-server.url}")
    private String employeeServerUrl;

    private final static String SIMPLE_ANSWER_URL = "/simple-answer";
    private final static String EMPLOYEES_FILE_NAME = "employees-simple-answer.json";

    public HttpStatusCode sendProcessedSimpleAnswer() {
        final var simpleAnswer = getSimpleAnswer();
        final var employees = simpleAnswer.employees();
        final var departmentsIds = employees.stream()
            .map(it -> it.department().id())
            .distinct()
            .toList();

        final var processedAnswer = departmentsIds.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                it -> {
                    final var departmentName = employees.stream()
                        .map(EmployeeResponse::department)
                        .filter(department -> department.id() == it)
                        .findAny()
                        .get();

                    final var processedEmployees = employees.stream()
                        .filter(employee -> employee.department().id() == it)
                        .map(employee -> new EmployeeDTO(
                            employee.name(),
                            employee.age()
                        ))
                        .toList();

                    return new DepartmentDTO(departmentName.departmentName(), processedEmployees);
                }
            ));

        writeResponseToFile(processedAnswer);

        return restTemplate
            .postForEntity(employeeServerUrl + SIMPLE_ANSWER_URL, processedAnswer, String.class)
            .getStatusCode();
    }

    private void writeResponseToFile(final Map<Long, DepartmentDTO> processedAnswer) {
        try {
            final var filePath = Path.of(EMPLOYEES_FILE_NAME);
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }

            final var jsonObject = new JsonObject();

            for (final var entry : processedAnswer.entrySet()) {
                jsonObject.add(entry.getKey().toString(), gson.toJsonTree(entry.getValue()));
            }

            final var processedJson = gson.toJson(processedAnswer);

            Files.write(filePath, processedJson.getBytes(), StandardOpenOption.APPEND);
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
