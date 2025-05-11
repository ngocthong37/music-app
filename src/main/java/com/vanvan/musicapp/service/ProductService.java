package com.vanvan.musicapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanvan.musicapp.entity.*;
import com.vanvan.musicapp.model.ProductModel;
import com.vanvan.musicapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductFeatureRepository productFeatureRepository;

    @Autowired
    private FeatureRepository featureRepository;

    public ResponseEntity<ResponseObject> findById(Integer productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            ProductModel productModel = convertToProductModel(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productModel));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
    }

    public ResponseEntity<ResponseObject> findAllProduct() {
        List<Product> productList = productRepository.findAll();
        if (productList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        List<ProductModel> productModelList = productList.stream()
                .map(this::convertToProductModel)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productModelList));
    }

    public ResponseEntity<Object> addProduct(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Product product = new Product();
            product.setName(jsonNode.get("name").asText());
            product.setIndoorDimension(jsonNode.get("indoorDimension").asText());
            product.setIndoorWeight(jsonNode.get("indoorWeight").asDouble());
            product.setOutdoorDimension(jsonNode.get("outdoorDimension").asText());
            product.setOutdoorWeight(jsonNode.get("outdoorWeight").asDouble());
            product.setHeatCapacity(jsonNode.get("heatCapacity").asText());
            product.setCoolingCapacity(jsonNode.get("coolingCapacity").asText());
            product.setNumberOfCooling(jsonNode.get("numberOfCooling").asInt());
            product.setPowerComsumption(jsonNode.get("powerComsumption").asDouble());
            product.setPrice(jsonNode.get("price").asDouble());
            product.setIndoorWarranty(jsonNode.get("indoorWarranty").asText());
            product.setOutdoorWarranty(jsonNode.get("outdoorWarranty").asText());
            product.setReleaseDate(jsonNode.get("releaseDate").asText());
            product.setInventoryQuantity(jsonNode.get("inventoryQuantity").asInt());
            product.setRadiatorMaterial(jsonNode.get("radiatorMaterial").asText());
            Integer brandId = jsonNode.get("brandId").asInt();
            Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
            product.setBrand(brand);
            product.setProductStatus(jsonNode.get("productStatus").asText());
            Product savedProduct = productRepository.save(product);
            if (jsonNode.has("productImages")) {
                List<ProductImage> productImages = objectMapper.readValue(
                        jsonNode.get("productImages").traverse(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ProductImage.class)
                );
                productImages.forEach(image -> image.setProduct(savedProduct)); // Sử dụng savedProduct thay vì product
                productImageRepository.saveAll(productImages);
            }
            if (jsonNode.has("features")) {
                JsonNode featuresNode = jsonNode.get("features");
                List<ProductFeature> productFeatures = new ArrayList<>();
                for (JsonNode featureNode : featuresNode) {
                    int featureId = featureNode.get("featureId").asInt();
                    String description = featureNode.get("description").asText();

                    Feature feature = featureRepository.findById(featureId)
                            .orElseThrow(() -> new RuntimeException("Feature not found with id: " + featureId));

                    ProductFeature productFeature = new ProductFeature();
                    productFeature.setId(new ProductFeatureId(savedProduct.getProductID(), featureId));
                    productFeature.setProduct(savedProduct);
                    productFeature.setFeature(feature);
                    productFeature.setDescription(description);

                    productFeatures.add(productFeature);
                }

                // Lưu các đối tượng ProductFeature vào cơ sở dữ liệu
                productFeatureRepository.saveAll(productFeatures);
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("OK", "Product added successfully", savedProduct.getProductID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateProduct(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer productId = jsonNode.get("productId").asInt();
            Product existingProduct = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            existingProduct.setName(jsonNode.get("name").asText());
            existingProduct.setIndoorDimension(jsonNode.get("indoorDimension").asText());
            existingProduct.setIndoorWeight(jsonNode.get("indoorWeight").asDouble());
            existingProduct.setOutdoorDimension(jsonNode.get("outdoorDimension").asText());
            existingProduct.setOutdoorWeight(jsonNode.get("outdoorWeight").asDouble());
            existingProduct.setHeatCapacity(jsonNode.get("heatCapacity").asText());
            existingProduct.setCoolingCapacity(jsonNode.get("coolingCapacity").asText());
            existingProduct.setNumberOfCooling(jsonNode.get("numberOfCooling").asInt());
            existingProduct.setPowerComsumption(jsonNode.get("powerComsumption").asDouble());
            existingProduct.setPrice(jsonNode.get("price").asDouble());
            existingProduct.setIndoorWarranty(jsonNode.get("indoorWarranty").asText());
            existingProduct.setOutdoorWarranty(jsonNode.get("outdoorWarranty").asText());
            existingProduct.setReleaseDate(jsonNode.get("releaseDate").asText());
            existingProduct.setInventoryQuantity(jsonNode.get("inventoryQuantity").asInt());
            existingProduct.setRadiatorMaterial(jsonNode.get("radiatorMaterial").asText());
            Integer brandId = jsonNode.get("brandId").asInt();
            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new RuntimeException("Brand not found with id: " + brandId));
            existingProduct.setBrand(brand);
            existingProduct.setProductStatus(jsonNode.get("productStatus").asText());

            Product savedProduct = productRepository.save(existingProduct);

            // Cập nhật ảnh sản phẩm
            if (jsonNode.has("productImages")) {
                List<ProductImage> productImages = objectMapper.readValue(
                        jsonNode.get("productImages").traverse(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ProductImage.class)
                );
                productImages.forEach(image -> image.setProduct(savedProduct));
                productImages.forEach(image -> image.setIsAvatar(false));
                productImageRepository.saveAll(productImages);
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Product updated successfully", savedProduct.getProductID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateProductFeature(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer productId = jsonNode.get("productId").asInt();
            String description = jsonNode.get("description").asText();

            Optional<ProductFeature> productFeatureOptional = productFeatureRepository.findFeatureByProductId(productId);
            if (productFeatureOptional.isPresent()) {
                ProductFeature productFeature = productFeatureOptional.get();
                productFeature.setDescription(description);
                productFeatureRepository.save(productFeature);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Product feature updated successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Product feature not found with ID: " + productId, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<Object> updateStatusProduct(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer productId = jsonNode.get("productId").asInt();
            String status = jsonNode.get("status").asText();

            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setProductStatus(status);
                productRepository.save(product);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Product status updated successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Product not found with ID: " + productId, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


    private ProductModel convertToProductModel(Product product) {
        ProductModel productModel = new ProductModel();
        productModel.setProductID(product.getProductID());
        productModel.setName(product.getName());
        productModel.setIndoorDimension(product.getIndoorDimension());
        productModel.setIndoorWeight(product.getIndoorWeight());
        productModel.setOutdoorDimension(product.getOutdoorDimension());
        productModel.setOutdoorWeight(product.getOutdoorWeight());
        productModel.setHeatCapacity(product.getHeatCapacity());
        productModel.setCoolingCapacity(product.getCoolingCapacity());
        productModel.setNumberOfCooling(product.getNumberOfCooling());
        productModel.setPowerComsumption(product.getPowerComsumption());
        productModel.setPrice(product.getPrice());
        productModel.setIndoorWarranty(product.getIndoorWarranty());
        productModel.setOutdoorWarranty(product.getOutdoorWarranty());
        productModel.setReleaseDate(product.getReleaseDate());
        productModel.setInventoryQuantity(product.getInventoryQuantity());
        productModel.setRadiatorMaterial(product.getRadiatorMaterial());
        productModel.setBrand(product.getBrand().getName());
        productModel.setProductImages(product.getProductImages());
        productModel.setProductStatus(product.getProductStatus());
        Optional<ProductFeature> productFeature = productFeatureRepository.findFeatureByProductId(product.getProductID());
        productFeature.ifPresent(feature -> productModel.setDescription(feature.getDescription()));
        return productModel;
    }


    public ResponseEntity<ResponseObject> findByProductName(String productName) {
        List<Product> productList = productRepository.findByProductName(productName);
        if (productList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        List<ProductModel> productModelList = productList.stream()
                .map(this::convertToProductModel)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productModelList));
    }

    public ResponseEntity<ResponseObject> findByBrandName(String brandName) {
        List<Product> productList = productRepository.findByBrandName(brandName);
        if (productList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        List<ProductModel> productModelList = productList.stream()
                .map(this::convertToProductModel)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", productModelList));
    }
}
