package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.Employee;
import com.quocluan.kdmaylanh.entity.Product;
import com.quocluan.kdmaylanh.entity.ProductArticle;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.model.ProductArticleModel;
import com.quocluan.kdmaylanh.repository.EmployeeRepository;
import com.quocluan.kdmaylanh.repository.ProductArticleRepository;
import com.quocluan.kdmaylanh.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductArticleService {
    @Autowired
    private ProductArticleRepository productArticleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<ResponseObject> findAllProductArticle() {
        List<ProductArticle> productArticleList = productArticleRepository.findAll();
        List<ProductArticleModel> productArticleModelList = new ArrayList<>();

        if (productArticleList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }

        for (ProductArticle productArticle : productArticleList) {
            ProductArticleModel productArticleModel = convertToProductArticleModel(productArticle);
            productArticleModelList.add(productArticleModel);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productArticleModelList));
    }

    public ResponseEntity<ResponseObject> findProductArticleById(Integer productArticleId) {
        Optional<ProductArticle> productArticle = productArticleRepository.findById(productArticleId);


        return productArticle.map(article -> ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", convertToProductArticleModel(article)))).orElseGet(()
                -> ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", "")));
    }

    public ResponseEntity<ResponseObject> addProductArticle(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer employeeId = jsonNode.get("employeeId").asInt();
            Integer productId = jsonNode.get("productId").asInt();
            LocalDate createDate = LocalDate.parse(jsonNode.get("createDate").asText());
            String arContent = jsonNode.get("arContent").asText();
            String arHeading = jsonNode.get("arHeading").asText();

            Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
            Optional<Product> productOptional = productRepository.findById(productId);

            if (!employeeOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Employee not found with id: " + employeeId, ""));
            }

            if (!productOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Product not found with id: " + productId, ""));
            }

            ProductArticle productArticle = new ProductArticle();
            productArticle.setEmployee(employeeOptional.get());
            productArticle.setProduct(productOptional.get());
            productArticle.setCreatedDate(createDate);
            productArticle.setArContent(arContent);
            productArticle.setArHeading(arHeading);

            productArticleRepository.save(productArticle);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("OK", "Product article added successfully", productArticle.getArticleID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> deleteProductArticleById(Integer productArticleId) {
        if (!productArticleRepository.existsById(productArticleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("Not found", "Product Article not found with id: " + productArticleId, ""));
        }

        try {
            productArticleRepository.deleteById(productArticleId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Product Article deleted successfully", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("ERROR", "An error occurred while deleting product article", e.getMessage()));
        }
    }


    private ProductArticleModel convertToProductArticleModel(ProductArticle productArticle) {
        ProductArticleModel productArticleModel = new ProductArticleModel();
        productArticleModel.setArticleID(productArticle.getArticleID());
        productArticleModel.setProductName(productArticle.getProduct().getName());
        productArticleModel.setArHeading(productArticle.getArHeading());
        productArticleModel.setArContent(productArticle.getArContent());
        productArticleModel.setEmployeeName(productArticle.getEmployee().getName());
        productArticleModel.setCreatedDate(new Date());
        return productArticleModel;
    }

}
