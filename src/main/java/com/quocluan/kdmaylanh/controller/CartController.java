package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("cart/addToCart")
    ResponseEntity<ResponseObject> addToCart(@RequestBody String json) {
        return cartService.addToCart(json);
    }

    @DeleteMapping("cart/deleteCart/{cartId}")
    ResponseEntity<ResponseObject> deleteCartByCartId(@PathVariable Integer cartId) {
        return cartService.deleteCartByCartId(cartId);
    }

}
