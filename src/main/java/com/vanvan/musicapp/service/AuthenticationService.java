package com.vanvan.musicapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vanvan.musicapp.Enum.Role;
import com.vanvan.musicapp.Enum.TokenType;
import com.vanvan.musicapp.entity.Token;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.TokenRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.AuthenticationRequest;
import com.vanvan.musicapp.request.ForgotPasswordRequest;
import com.vanvan.musicapp.request.LogOutRequest;
import com.vanvan.musicapp.request.RegisterRequest;
import com.vanvan.musicapp.response.AuthenticationResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSendService emailSendService;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(String.valueOf(request.getRole())))
                .username(request.getName())
                .build();
        User savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);

        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .role(savedUser.getRole().toString())
                .userId(user.getId())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var account = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String status = account.getStatus();
        if (status != null && status.equals("locked")) {
            return AuthenticationResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .role(null)
                    .userId(null)
                    .build();
        }
        var jwtToken = jwtService.generateToken(account);
        var refreshToken = jwtService.generateRefreshToken(account);
        revokeAllUserTokens(account);
        saveUserToken(account, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(String.valueOf(account.getRole()))
                .userId(account.getId())
                .userName(account.getUsername())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validAccountTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validAccountTokens.isEmpty())
            return;
        validAccountTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validAccountTokens);
    }

    private void revokeAllUserTokensLogOut(Integer accountId) {
        var validAccountTokens = tokenRepository.findAllValidTokenByUser(accountId);
        if (validAccountTokens.isEmpty())
            return;
        validAccountTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validAccountTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var account = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, account)) {
                var accessToken = jwtService.generateToken(account);
                revokeAllUserTokens(account);
                saveUserToken(account, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseObject logout(LogOutRequest request) {
        revokeAllUserTokensLogOut(request.getUserId());
        return new ResponseObject("OK", "Log out successfully", true);
    }

    public ResponseObject forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            Map<String, Object> model = new HashMap<>();
            model.put("userName", user.getUsername());
            model.put("resetLink", "http://localhost:3000/auth/reset-password?token=" + resetToken);

            emailSendService.sendMail(
                    request.getEmail(),
                    new String[]{},
                    "Password Reset Request",
                    model
            );

            return new ResponseObject("success", "Đã gửi email đặt lại mật khẩu", user.getId());
        } catch (Exception e) {
            return new ResponseObject("error", "Gửi yêu cầu đặt lại mật khẩu thất bại: " + e.getMessage(), null);
        }
    }

}
