package com.quocluan.kdmaylanh.controller;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("employee/customer/findAll")
    public ResponseEntity<ResponseObject> findAll() {
        return customerService.findAllCustomer();
    }

    @GetMapping("customer/findById/{customerId}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Integer customerId) {
        return customerService.findCustomerById(customerId);
    }

    @PutMapping("customer/update")
    public ResponseEntity<ResponseObject> updateCustomerInfor(@RequestBody String json) {
        return customerService.updateCustomerInfor(json);
    }

}
