package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.AuthenticationRequest;
import com.vanvan.musicapp.request.ForgotPasswordRequest;
import com.vanvan.musicapp.request.RegisterRequest;
import com.vanvan.musicapp.request.ResetPasswordRequest;
import com.vanvan.musicapp.response.AuthenticationResponse;
import com.vanvan.musicapp.security.JwtService;
import com.vanvan.musicapp.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostMapping("auth/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("auth/sign-in")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("auth/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ResponseEntity.ok("Password reset email sent successfully");
    }

    @PostMapping("auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully");
    }

}
