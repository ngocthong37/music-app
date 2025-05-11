package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.Enum.Role;
import com.quocluan.kdmaylanh.Enum.TokenType;
import com.quocluan.kdmaylanh.entity.*;
import com.quocluan.kdmaylanh.repository.AccountRepository;
import com.quocluan.kdmaylanh.repository.CustomerRepository;
import com.quocluan.kdmaylanh.repository.EmployeeRepository;
import com.quocluan.kdmaylanh.repository.TokenRepository;
import com.quocluan.kdmaylanh.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSendService emailSendService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public AuthenticationResponse registerByCustomer(RegisterRequest request) {
        var account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleID(Role.valueOf("CUSTOMER"))
                .build();
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        Customer savedCustomer = customerRepository.save(customer);
        account.setCustomer(savedCustomer);
        account.setStatus("Active");
        var savedAccount = repository.save(account);
        var jwtToken = jwtService.generateToken(savedAccount);
        var refreshToken = jwtService.generateRefreshToken(savedAccount);

        saveAccountToken(savedAccount, jwtToken);
        return AuthenticationResponse.builder()
                .role(savedAccount.getRoleID().toString())
                .accountId(savedAccount.getAccountID())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse registerByAdmin(RegisterRequest request) {
        String password = request.getPassword();
        var account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleID(Role.valueOf(request.getRole().toString()))
                .build();
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setEmployeeStatus("Active");
        Employee savedEmployee = employeeRepository.save(employee);
        account.setEmployee(savedEmployee);
        account.setStatus("Active");
        var savedAccount = repository.save(account);
        if (savedAccount.getPassword() != null) {
            String[] cc = {};
            Map<String, Object> model = new HashMap<>();
            model.put("userName", savedAccount.getEmployee().getName());
            model.put("email", savedAccount.getEmail());
            model.put("password", password);
            emailSendService.sendMail(savedAccount.getEmail(), cc, "Tài khoản truy cập của bạn đã được tạo", model);
        }
        var jwtToken = jwtService.generateToken(savedAccount);
        var refreshToken = jwtService.generateRefreshToken(savedAccount);

        saveAccountToken(savedAccount, jwtToken);
        return AuthenticationResponse.builder()
                .role(savedAccount.getRoleID().toString())
                .accountId(savedAccount.getAccountID())
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
        var account = repository.findByEmail(request.getEmail())
                .orElseThrow();
        String status = account.getStatus();
        if (status.equals("locked")) {
            return AuthenticationResponse.builder()
                    .accessToken(null)
                    .refreshToken(null)
                    .role(null)
                    .accountId(null)
                    .build();
        }
        var jwtToken = jwtService.generateToken(account);
        var refreshToken = jwtService.generateRefreshToken(account);
        revokeAllUserTokens(account);
        saveAccountToken(account, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(String.valueOf(account.getRoleID()))
                .accountId(account.getAccountID())
                .build();
    }

    private void saveAccountToken(Account account, String jwtToken) {
        var token = Token.builder()
                .account(account)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Account account) {
        var validAccountTokens = tokenRepository.findAllValidTokenByUser(account.getAccountID());
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
        final String accountEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        accountEmail = jwtService.extractUsername(refreshToken);
        if (accountEmail != null) {
            var account = this.repository.findByEmail(accountEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, account)) {
                var accessToken = jwtService.generateToken(account);
                revokeAllUserTokens(account);
                saveAccountToken(account, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
    public ResponseEntity<ResponseObject> logout(Integer accountId) {
        revokeAllUserTokensLogOut(accountId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("OK", "Log out successfully", ""));
    }
}
