package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.Cart;
import com.quocluan.kdmaylanh.entity.Customer;
import com.quocluan.kdmaylanh.entity.Product;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.repository.CartRepository;
import com.quocluan.kdmaylanh.repository.CustomerRepository;
import com.quocluan.kdmaylanh.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<ResponseObject> addToCart(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer customerId = jsonNode.get("customerId").asInt();
            Integer productId = jsonNode.get("productId").asInt();
            Integer quantity = jsonNode.get("quantity").asInt();

            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (!optionalCustomer.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Customer not found with id: " + customerId, ""));
            }
            Customer customer = optionalCustomer.get();

            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (!optionalProduct.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("ERROR", "Product not found with id: " + productId, ""));
            }
            Product product = optionalProduct.get();

            if (product.getInventoryQuantity() < quantity) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject("ERROR", "Not enough quantity in stock for product: " + product.getName(), ""));
            }
            Optional<Cart> optionalCart = Optional.ofNullable(cartRepository.findCartByProductId(productId));
            Cart cart;
            if (optionalCart.isPresent()) {
                cart = optionalCart.get();
                cart.setQuantity(cart.getQuantity() + quantity);
            } else {
                cart = new Cart();
                cart.setCustomer(customer);
                cart.setProduct(product);
                cart.setQuantity(quantity);
            }
            cartRepository.save(cart);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("OK", "Product added to cart successfully", cart.getCartID()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public ResponseEntity<ResponseObject> deleteCartByCartId(Integer cartId) {
        try {
            Optional<Cart> optionalCart = cartRepository.findById(cartId);
            if (optionalCart.isPresent()) {
                Cart cart = optionalCart.get();
                cartRepository.delete(cart);
                return ResponseEntity.ok().body(new ResponseObject("OK", "Cart deleted successfully", cartId));
            } else {
                return ResponseEntity.badRequest().body(new ResponseObject("ERROR", "Cart not found with id: " + cartId, ""));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

}
