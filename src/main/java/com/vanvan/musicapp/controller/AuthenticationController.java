package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.*;
import com.vanvan.musicapp.response.CustomErrorResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.AuthenticationService;
import com.vanvan.musicapp.utils.exception_handler.EmailAlreadyExistsException;
import com.vanvan.musicapp.utils.exception_handler.InactiveAccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CustomErrorResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomErrorResponse("error", "Đăng ký thất bại", null));
        }
    }


    @PostMapping("auth/sign-in")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (InactiveAccountException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new CustomErrorResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CustomErrorResponse("error", "Thông tin đăng nhập không hợp lệ", null));
        }
    }

    @PostMapping("auth/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        ResponseObject response = authenticationService.forgotPassword(request);
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
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

    @PostMapping("auth/log-out")
    public ResponseEntity<ResponseObject> logOut(@RequestBody LogOutRequest request) {
        return ResponseEntity.ok(authenticationService.logout(request));
    }

    @GetMapping("auth/verify")
    public ResponseEntity<ResponseObject> verifyAccount(@RequestParam("token") String token) {
        return ResponseEntity.ok(authenticationService.verifyAccount(token));
    }

    @PostMapping("auth/update-password")
    public ResponseEntity<ResponseObject> updatePassword(@RequestBody UpdatePasswordRequest request) {
        try {
            ResponseObject response = authenticationService.updatePassword(request);
            return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("error", "Cập nhật mật khẩu thất bại", null));
        }
    }

}
