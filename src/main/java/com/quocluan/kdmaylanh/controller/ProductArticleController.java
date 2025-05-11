package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.ProductArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class ProductArticleController {
    @Autowired
    private ProductArticleService productArticleService;


    @GetMapping("productArticle/findAll")
    public ResponseEntity<ResponseObject> findAllProduct() {
        return productArticleService.findAllProductArticle();
    }

    @GetMapping("productArticle/findById/{productArticleId}")
    public ResponseEntity<ResponseObject> findAllArticleById(@PathVariable Integer productArticleId) {
        return productArticleService.findProductArticleById(productArticleId);
    }

    @PostMapping("employee/productArticle/add")
    public ResponseEntity<ResponseObject> addProductArticle(@RequestBody String json) {
        return productArticleService.addProductArticle(json);
    }

    @DeleteMapping("employee/productArticle/deleteById/{productArticleId}")
    public ResponseEntity<ResponseObject> addProductArticle(@PathVariable Integer productArticleId) {
        return productArticleService.deleteProductArticleById(productArticleId);
    }

}
