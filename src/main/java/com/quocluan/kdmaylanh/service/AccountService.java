package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.Enum.Role;
import com.quocluan.kdmaylanh.entity.Account;
import com.quocluan.kdmaylanh.entity.Customer;
import com.quocluan.kdmaylanh.entity.Employee;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.model.AccountEmployeeModel;
import com.quocluan.kdmaylanh.model.AccountModel;
import com.quocluan.kdmaylanh.repository.AccountRepository;
import com.quocluan.kdmaylanh.repository.CustomerRepository;
import com.quocluan.kdmaylanh.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ResponseEntity<ResponseObject> findAllAccountEmployee() {
        List<Account> accountList = accountRepository.findAllEmployeeAccount();
        if (accountList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        List<AccountEmployeeModel> accountEmployeeModelList = accountList.stream()
                .map(this::convertToAccountEmployeeModel)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", accountEmployeeModelList));
    }

    public ResponseEntity<ResponseObject> findById(Integer accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        AccountModel accountModel = convertToAccountModel(account.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", accountModel));
    }

    private AccountEmployeeModel convertToAccountEmployeeModel(Account account) {
        AccountEmployeeModel accountEmployeeModel = new AccountEmployeeModel();
        accountEmployeeModel.setAccountID(account.getAccountID());
        accountEmployeeModel.setPhoneNumber(account.getPhoneNumber());
        accountEmployeeModel.setEmail(account.getEmail());
        accountEmployeeModel.setPassword(account.getPassword());
        accountEmployeeModel.setRoleID(String.valueOf(account.getRoleID()));
       if (account.getEmployee() != null) {
            accountEmployeeModel.setEmployeeId(account.getEmployee().getEmployeeID());
        }
        return accountEmployeeModel;
    }

    private AccountModel convertToAccountModel(Account account) {
        AccountModel accountModel = new AccountModel();
        accountModel.setAccountID(account.getAccountID());
        accountModel.setPhoneNumber(account.getPhoneNumber());
        accountModel.setEmail(account.getEmail());
        accountModel.setPassword(account.getPassword());
        if (account.getRoleID().name().equals("ADMIN") || account.getRoleID().name().equals("EMPLOYEE")) {
            accountModel.setName(account.getEmployee().getName());
        }
        else {
            accountModel.setName(account.getCustomer().getName());
        }
        return accountModel;
    }

//    public ResponseEntity<ResponseObject> addCustomerAccount(String json) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            if (json == null || json.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
//            }
//
//            JsonNode jsonObjectAccount = objectMapper.readTree(json);
//            Integer customerId = jsonObjectAccount.get("customerId") != null ?
//                    jsonObjectAccount.get("customerId").asInt() : null;
//            String email = jsonObjectAccount.get("email") != null ?
//                    jsonObjectAccount.get("email").asText() : null;
//            String password = jsonObjectAccount.get("password") != null ?
//                    jsonObjectAccount.get("password").asText() : null;
//            String roleId = jsonObjectAccount.get("roleId") != null ?
//                    jsonObjectAccount.get("roleId").asText() : null;
//            String phoneNumber = jsonObjectAccount.get("phoneNumber") != null ?
//                    jsonObjectAccount.get("phoneNumber").asText() : null;
//
//            if (customerId == null || email == null || password == null || roleId == null || phoneNumber == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(new ResponseObject("ERROR", "Missing required fields", ""));
//            }
//
//            Optional<Customer> customerOptional = customerRepository.findById(customerId);
//            if (!customerOptional.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ResponseObject("ERROR", "Customer not found", ""));
//            }
//
//            // Tạo một đối tượng Account từ dữ liệu JSON
//            Account account = new Account();
//            account.setCustomer(customerOptional.get());
//            account.setEmail(email);
//            account.setPassword(password);
//            account.setRoleID(Role.valueOf(roleId));
//            account.setPhoneNumber(phoneNumber);
//
//            accountRepository.save(account);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new ResponseObject("OK", "Account added successfully", ""));
//
//        } catch (Exception e) {
//            // Trả về phản hồi lỗi nếu có lỗi xảy ra
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
//        }
//    }
//
//    public ResponseEntity<ResponseObject> addEmployeeAccount(String json) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            if (json == null || json.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(new ResponseObject("ERROR", "Empty JSON", ""));
//            }
//            JsonNode jsonObjectAccount = objectMapper.readTree(json);
//            Integer employeeId = jsonObjectAccount.get("employeeId") != null ?
//                    jsonObjectAccount.get("employeeId").asInt() : null;
//            String email = jsonObjectAccount.get("email") != null ?
//                    jsonObjectAccount.get("email").asText() : null;
//            String password = jsonObjectAccount.get("password") != null ?
//                    jsonObjectAccount.get("password").asText() : null;
//            String roleId = jsonObjectAccount.get("roleId") != null ?
//                    jsonObjectAccount.get("roleId").asText() : null;
//            String phoneNumber = jsonObjectAccount.get("phoneNumber") != null ?
//                    jsonObjectAccount.get("phoneNumber").asText() : null;
//
//            if (employeeId == null || email == null || password == null || roleId == null || phoneNumber == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(new ResponseObject("ERROR", "Missing required fields", ""));
//            }
//
//            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
//            if (!employeeOptional.isPresent()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(new ResponseObject("ERROR", "Employee not found", ""));
//            }
//
//            Account account = new Account();
//            account.setEmployee(employeeOptional.get());
//            account.setEmail(email);
//            account.setPassword(password);
//            account.setRoleID(Role.valueOf(roleId));
//            account.setPhoneNumber(phoneNumber);
//            accountRepository.save(account);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new ResponseObject("OK", "Account added successfully", ""));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
//        }
//    }

    public ResponseEntity<Object> updateAccount(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer accountId = jsonNode.get("accountId") != null ? jsonNode.get("accountId").asInt() : null;

            if (accountId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Account ID is required", ""));
            }

            Optional<Account> accountOptional = accountRepository.findById(accountId);
            if (!accountOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Account not found", ""));
            }

            Account account = accountOptional.get();

            if (jsonNode.has("email")) {
                account.setEmail(jsonNode.get("email").asText());
            }
            if (jsonNode.has("password")) {
                account.setPassword(jsonNode.get("password").asText());
            }
            if (jsonNode.has("phoneNumber")) {
                account.setPhoneNumber(jsonNode.get("phoneNumber").asText());
            }

            accountRepository.save(account);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Employee account updated successfully", account.getAccountID()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> lockAccount(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer accountId = jsonNode.get("accountId") != null ? jsonNode.get("accountId").asInt() : null;

            if (accountId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Account ID is required", ""));
            }

            Optional<Account> accountOptional = accountRepository.findById(accountId);
            if (!accountOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Account not found", ""));
            }

            Account account = accountOptional.get();

            if (jsonNode.has("status")) {
                String status = jsonNode.get("status").asText().toLowerCase();
                if (status.equals("locked")) {
                    account.setStatus("locked");
                }
            }

            accountRepository.save(account);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Account status updated successfully", account.getAccountID()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }
}
