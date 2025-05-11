package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("management/account/findAllAccountEmployee")
    public ResponseEntity<ResponseObject> findAll() {
        return accountService.findAllAccountEmployee();
    }

    @GetMapping("account/findById/{accountId}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Integer accountId) {
        return accountService.findById(accountId);
    }

//    @PostMapping("account/addCustomerAccount")
//    ResponseEntity<ResponseObject> addCustomerAccount(@RequestBody String json) {
//        return accountService.addCustomerAccount(json);
//    }
//
//    @PostMapping("account/addEmployeeAccount")
//    ResponseEntity<ResponseObject> addEmployeeAccount(@RequestBody String json) {
//        return accountService.addEmployeeAccount(json);
//    }

    @PutMapping("account/updateAccount")
    ResponseEntity<Object> updateAccount(@RequestBody String json) {
        return accountService.updateAccount(json);
    }

    @PutMapping("management/account/lock")
    ResponseEntity<Object> lockAccount(@RequestBody String json) {
        return accountService.lockAccount(json);
    }

}
