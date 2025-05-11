package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.AuthenticationRequest;
import com.vanvan.musicapp.entity.AuthenticationResponse;
import com.vanvan.musicapp.entity.RegisterRequest;
import com.vanvan.musicapp.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

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
//
//    @PostMapping("auth/signOut/{accountId}")
//    public ResponseEntity<ResponseObject> signOut(@PathVariable Integer accountId) {
//      return authenticationService.logout(accountId);
//    }
//
//    @PostMapping("auth/refresh-token")
//    public void refreshToken(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//        authenticationService.refreshToken(request, response);
//    }


}
