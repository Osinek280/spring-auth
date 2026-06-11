package com.example.auth.auth.service;

import com.example.auth.auth.dto.AuthTokens;
import com.example.auth.auth.dto.LoginRequest;
import com.example.auth.auth.dto.RegisterRequest;
import com.example.auth.security.jwt.JwtService;
import com.example.auth.user.entity.AppUser;
import com.example.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthTokens register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalStateException("Email already in use");
    }

    AppUser user = new AppUser();
    user.setName(request.name());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));

    userRepository.save(user);

    String accessToken = jwtService.generateToken(user.getEmail(), user.getId());

    return new AuthTokens(accessToken);
  }

  public AuthTokens login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    AppUser user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String accessToken = jwtService.generateToken(user.getEmail(), user.getId());

    return new AuthTokens(accessToken);
  }
}
