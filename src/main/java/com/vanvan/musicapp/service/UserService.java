package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.UpdateUserStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllCustomers() {
        List<User> customers = userRepository.findAll().stream()
                .filter(user -> user.getRole().equals(com.vanvan.musicapp.Enum.Role.CUSTOMER))
                .collect(Collectors.toList());

        Collections.reverse(customers);
        return customers;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserStatus(Integer userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(request.getStatus());
        return userRepository.save(user);
    }
}