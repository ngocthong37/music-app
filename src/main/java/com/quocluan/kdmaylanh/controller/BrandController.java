package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("brand/findAll")
    public ResponseEntity<ResponseObject> findAllBrands() {
        return brandService.findAllBrands();
    }

    @PutMapping("management/brand/update")
    public ResponseEntity<ResponseObject> updateBrand(@RequestBody String json) {
        return brandService.updateBrand(json);
    }

    @PostMapping("management/brand/add")
    public ResponseEntity<ResponseObject> addBrand(@RequestBody String json) {
        return brandService.addBrand(json);
    }

    @PutMapping("management/brand/updateStatus")
    public ResponseEntity<ResponseObject> updateBrandStatus(@RequestBody String json) {
        return brandService.updateBrandStatus(json);
    }

}
