package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.request.UpdateUserStatusRequest;
import com.vanvan.musicapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        List<User> customers = userService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Integer userId, @RequestBody UpdateUserStatusRequest request) {
        User updatedUser = userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}