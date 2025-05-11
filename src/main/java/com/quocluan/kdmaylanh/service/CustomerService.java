package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.Address;
import com.quocluan.kdmaylanh.entity.Customer;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.repository.AddressRepository;
import com.quocluan.kdmaylanh.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    public ResponseEntity<ResponseObject> findAllCustomer() {
        List<Customer> customerList = customerRepository.findAll();
        if (customerList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", customerList));
    }

    public ResponseEntity<ResponseObject> findCustomerById(Integer customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", customer.get()));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
    }

    public ResponseEntity<ResponseObject> updateCustomerInfor(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);

            Integer customerID = jsonNode.get("customerID").asInt();

            Customer customer = customerRepository.findById(customerID)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerID));

            customer.setName(jsonNode.get("name").asText());
            customer.setEmail(jsonNode.get("email").asText());
            customer.setPhoneNumber(jsonNode.get("phoneNumber").asText());
            customer.setSex(jsonNode.get("sex").asText());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dob = LocalDate.parse(jsonNode.get("dob").asText(), dateFormatter);
            customer.setDob(dob);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

            try {
                Date createdAt = dateFormat.parse(jsonNode.get("createdAt").asText());
                customer.setCreatedAt(createdAt);
            } catch (ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
            if (jsonNode.has("modifiedAt")) {
                try {
                    Date modifiedAt = dateFormat.parse(jsonNode.get("modifiedAt").asText());
                    customer.setModifiedAt(modifiedAt);
                } catch (ParseException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            if (jsonNode.has("addresses")) {
                List<Address> addresses = new ArrayList<>();
                JsonNode addressesNode = jsonNode.get("addresses");
                for (JsonNode addressNode : addressesNode) {
                    Integer addressID = addressNode.get("addressID").asInt();
                    Address address = addressRepository.findById(addressID)
                            .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressID));
                    address.setCityID(addressNode.get("cityID").asText());
                    address.setDistrictID(addressNode.get("districtID").asText());
                    address.setWardID(addressNode.get("wardID").asText());
                    address.setDetailAddress(addressNode.get("detailAddress").asText());
                    address.setIsDefault(addressNode.get("isDefault").asInt());
                    address.setPhoneNumber(addressNode.get("phoneNumber").asText());
                    addresses.add(address);
                }
                customer.setAddresses(addresses);
            }
            Customer updatedCustomer = customerRepository.save(customer);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", customer.getCustomerID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }
}
