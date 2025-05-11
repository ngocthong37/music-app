package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.Account;
import com.quocluan.kdmaylanh.entity.Brand;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.model.AccountEmployeeModel;
import com.quocluan.kdmaylanh.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public ResponseEntity<ResponseObject> findAllBrands() {
        List<Brand> brandList = brandRepository.findAll();
        if (brandList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", brandList));
    }

    public ResponseEntity<ResponseObject> updateBrand(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            int brandId = jsonNode.get("brandID").asInt();
            if (brandRepository.existsById(brandId)) {
                Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
                if (jsonNode.has("name")) {
                    brand.setName(jsonNode.get("name").asText());
                }
                if (jsonNode.has("logoPath")) {
                    brand.setLogoPath(jsonNode.get("logoPath").asText());
                }
                Brand updatedBrand = brandRepository.save(brand);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Update brand successfully", updatedBrand.getBrandID()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> updateBrandStatus(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            int brandId = jsonNode.get("brandID").asInt();
            if (brandRepository.existsById(brandId)) {
                Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
                if (jsonNode.has("status")) {
                    brand.setStatus(jsonNode.get("status").asText());
                }
                Brand updatedBrand = brandRepository.save(brand);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Update brand status successfully", updatedBrand.getBrandID()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> addBrand(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            Brand brand = new Brand();
            brand.setName(jsonNode.get("name").asText());
            brand.setLogoPath(jsonNode.get("logoPath").asText());
            brand.setStatus("Active");
            Brand newBrand = brandRepository.save(brand);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Add brand successfully", newBrand.getBrandID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

}
