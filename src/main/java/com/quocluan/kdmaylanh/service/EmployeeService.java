package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.*;
import com.quocluan.kdmaylanh.repository.AccountRepository;
import com.quocluan.kdmaylanh.repository.EmployeeRepository;
import com.quocluan.kdmaylanh.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public ResponseEntity<ResponseObject> findAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();
        if (employeeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", employeeList));
    }

    public ResponseEntity<ResponseObject> findEmployeeById(Integer employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", employee.get()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
    }

    public ResponseEntity<ResponseObject> addEmployee(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (json == null || json.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
            }

            JsonNode jsonObjectEmployee = objectMapper.readTree(json);
            String name = jsonObjectEmployee.get("name") != null ?
                    jsonObjectEmployee.get("name").asText() : null;
            LocalDate birthDate = jsonObjectEmployee.get("birthDate") != null ?
                    LocalDate.parse(jsonObjectEmployee.get("birthDate").asText()) : null;
            String identityNumber = jsonObjectEmployee.get("identityNumber") != null ?
                    jsonObjectEmployee.get("identityNumber").asText() : null;
            String phoneNumber = jsonObjectEmployee.get("phoneNumber") != null ?
                    jsonObjectEmployee.get("phoneNumber").asText() : null;
            String email = jsonObjectEmployee.get("email") != null ?
                    jsonObjectEmployee.get("email").asText() : null;
            String gender = jsonObjectEmployee.get("gender") != null ?
                    jsonObjectEmployee.get("gender").asText() : null;
            String address = jsonObjectEmployee.get("address") != null ?
                    jsonObjectEmployee.get("address").asText() : null;

            if (name == null || birthDate == null || identityNumber == null ||
                    phoneNumber == null || email == null || gender == null || address == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Missing required fields", ""));
            }

            Employee employee = new Employee();
            employee.setName(name);
            employee.setBirthDate(birthDate);
            employee.setIdentityNumber(identityNumber);
            employee.setPhoneNumber(phoneNumber);
            employee.setEmail(email);
            employee.setGender(gender);
            employee.setAddress(address);
            employee.setEmployeeStatus("Active");
            Date currentDate = new Date();
            employee.setHireDate(currentDate);
            Employee savedEmployee = employeeRepository.save(employee);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("OK", "Employee added successfully", savedEmployee));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateEmployee(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer employeeId = jsonNode.get("employeeId") != null ? jsonNode.get("employeeId").asInt() : null;

            if (employeeId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Employee ID is required", ""));
            }

            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
            if (!employeeOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Employee not found", ""));
            }

            Employee employee = employeeOptional.get();

            if (jsonNode.has("name")) {
                employee.setName(jsonNode.get("name").asText());
            }
            if (jsonNode.has("birthDate")) {
                // Assume birthDate is in ISO format (yyyy-MM-dd)
                employee.setBirthDate(LocalDate.parse(jsonNode.get("birthDate").asText()));
            }
            if (jsonNode.has("identityNumber")) {
                employee.setIdentityNumber(jsonNode.get("identityNumber").asText());
            }
            if (jsonNode.has("phoneNumber")) {
                employee.setPhoneNumber(jsonNode.get("phoneNumber").asText());
            }
            if (jsonNode.has("email")) {
                employee.setEmail(jsonNode.get("email").asText());
            }
            if (jsonNode.has("gender")) {
                employee.setGender(jsonNode.get("gender").asText());
            }
            if (jsonNode.has("address")) {
                employee.setAddress(jsonNode.get("address").asText());
            }
            employeeRepository.save(employee);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Employee updated successfully", employee.getEmployeeID()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateEmployeeStatus(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer employeeId = jsonNode.get("employeeId").asInt();
            String status = jsonNode.get("status").asText();

            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
            if (employeeOptional.isPresent()) {
                Employee employee = employeeOptional.get();
                employee.setEmployeeStatus(status);
                employeeRepository.save(employee);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Employee status updated successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Employee not found with ID: " + employeeId, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateStatusOrder(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer orderId = jsonNode.get("orderId").asInt();
            String status = jsonNode.get("status").asText();
            Integer employeeId = jsonNode.get("employeeId").asInt();
            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findOrderStatusByOrderId(orderId);

            if (orderStatusOptional.isPresent()) {
                OrderStatus orderStatus = orderStatusOptional.get();
                Status status1 = new Status();
                status1.setStatusID(status);
                orderStatus.setStatus(status1);
                if (employeeOptional.isPresent()) {
                    Employee employee = employeeOptional.get();
                    orderStatus.setEmployeeID(employee.getEmployeeID());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Employee not found with ID: " + employeeId, null));
                }
                orderStatusRepository.save(orderStatus);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "order status updated successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order status not found with ID: " + orderId, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

}
