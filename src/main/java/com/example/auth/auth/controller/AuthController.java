package com.example.auth.auth.controller;

import com.example.auth.auth.dto.AuthTokens;
import com.example.auth.auth.dto.LoginRequest;
import com.example.auth.auth.dto.RegisterRequest;
import com.example.auth.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody RegisterRequest req) {
    AuthTokens tokens = authService.register(req);

    ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Lax")
        .maxAge(60)
        .build();

    return ResponseEntity.ok()
        .headers(headers -> {
          headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        })
        .build();
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(@RequestBody LoginRequest req) {
    AuthTokens tokens = authService.login(req);

    ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Lax")
        .maxAge(60)
        .build();

    return ResponseEntity.ok()
        .headers(headers -> {
          headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        })
        .build();
  }
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {

    ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Lax")
        .maxAge(0)
        .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .sameSite("Lax")
        .maxAge(0)
        .build();

    return ResponseEntity.ok()
        .headers(headers -> {
          headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
          headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        })
        .build();
  }
}
