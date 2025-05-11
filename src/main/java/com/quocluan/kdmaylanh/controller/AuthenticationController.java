package com.quocluan.kdmaylanh.controller;

;
import com.quocluan.kdmaylanh.entity.AuthenticationRequest;
import com.quocluan.kdmaylanh.entity.AuthenticationResponse;
import com.quocluan.kdmaylanh.entity.RegisterRequest;
import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("auth/registerByCustomer")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.registerByCustomer(request));
    }

    @PostMapping("management/auth/registerByAdmin")
    public ResponseEntity<AuthenticationResponse> registerByAdmin(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.registerByAdmin(request));
    }

    @PostMapping("auth/signIn")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("auth/signOut/{accountId}")
    public ResponseEntity<ResponseObject> signOut(@PathVariable Integer accountId) {
      return authenticationService.logout(accountId);
    }

    @PostMapping("auth/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }


}
