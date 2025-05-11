package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("product/findById/{productId}")
    public ResponseEntity<ResponseObject> findProductById(@PathVariable Integer productId) {
        return productService.findById(productId);
    }

    @GetMapping("product/findAll")
    public ResponseEntity<ResponseObject> findAllProduct() {
        return productService.findAllProduct();
    }

    @GetMapping("product/findByProductName/{productName}")
    public ResponseEntity<ResponseObject> findByProductName(@PathVariable String productName) {
        return productService.findByProductName(productName);
    }

    @GetMapping("product/findByBrandName/{brandName}")
    public ResponseEntity<ResponseObject> findByBrandName(@PathVariable String brandName) {
        return productService.findByBrandName(brandName);
    }

    @PostMapping("management/product/add")
    public ResponseEntity<Object> addProduct(@RequestBody String json) {
        return productService.addProduct(json);
    }

    @PutMapping("management/product/update")
    public ResponseEntity<Object> updateProduct(@RequestBody String json) {
        return productService.updateProduct(json);
    }

    @PutMapping("management/product/updateDescription")
    public ResponseEntity<Object> updateDescriptionProduct(@RequestBody String json) {
        return productService.updateProductFeature(json);
    }

    @PutMapping("management/product/updateStatusProduct")
    public ResponseEntity<Object> updateStatusProduct(@RequestBody String json) {
        return productService.updateStatusProduct(json);
    }


}
