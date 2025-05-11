package com.quocluan.kdmaylanh.controller;


import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.EmployeeService;
import com.quocluan.kdmaylanh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OrderService orderService;

    @GetMapping("management/employee/findAll")
    public ResponseEntity<ResponseObject> findAll() {
        return employeeService.findAllEmployee();
    }

    @GetMapping("employee/findById/{employeeId}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Integer employeeId) {
        return employeeService.findEmployeeById(employeeId);
    }

    @GetMapping("employee/findAllOrderByCustomerId/{employeeId}")
    public ResponseEntity<ResponseObject> findAllOrderByCustomerId(@PathVariable Integer employeeId) {
        return orderService.findAllOrderByCustomerID(employeeId);
    }

    @PostMapping("management/employee/add")
    ResponseEntity<ResponseObject> addEmployee(@RequestBody String json) {
        return employeeService.addEmployee(json);
    }

    @PutMapping("employee/update")
    ResponseEntity<Object> updateInforEmployee(@RequestBody String json) {
        return employeeService.updateEmployee(json);
    }

    @PutMapping("management/employee/updateEmployeeStatus")
    ResponseEntity<Object> updateEmployeeStatus(@RequestBody String json) {
        return employeeService.updateEmployeeStatus(json);
    }

    @PutMapping("employee/updateStatusOrder")
    ResponseEntity<Object> updateStatusOrder(@RequestBody String json) {
        return employeeService.updateStatusOrder(json);
    }




}
