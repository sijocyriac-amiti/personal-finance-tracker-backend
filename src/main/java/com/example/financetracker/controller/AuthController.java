package com.example.financetracker.controller;

import com.example.financetracker.dto.AuthRequest;
import com.example.financetracker.dto.AuthResponse;
import com.example.financetracker.dto.ApiMessageResponse;
import com.example.financetracker.dto.CurrentUserResponse;
import com.example.financetracker.dto.ForgotPasswordRequest;
import com.example.financetracker.dto.ForgotPasswordResponse;
import com.example.financetracker.dto.RefreshRequest;
import com.example.financetracker.dto.RegisterRequest;
import com.example.financetracker.dto.ResetPasswordRequest;
import com.example.financetracker.domain.RefreshToken;
import com.example.financetracker.domain.User;
import com.example.financetracker.repository.UserRepository;
import com.example.financetracker.security.JwtService;
import com.example.financetracker.service.PasswordResetService;
import com.example.financetracker.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    public AuthController(
        AuthenticationManager authenticationManager,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        RefreshTokenService refreshTokenService,
        PasswordResetService passwordResetService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        String email = normalizeEmail(request.email());
        validateRegistrationRequest(email, request.password(), request.displayName());

        User user = User.builder()
            .username(email)
            .email(email)
            .displayName(request.displayName().trim())
            .password(passwordEncoder.encode(request.password()))
            .roles("ROLE_USER")
            .build();

        userRepository.save(user);

        return createAuthResponse(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        String email = normalizeEmail(request.email());
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return createAuthResponse(user);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        if (!refreshTokenService.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired or revoked refresh token");
        }

        refreshTokenService.revokeToken(refreshToken);
        return createAuthResponse(refreshToken.getUser());
    }

    @PostMapping("/logout")
    public ApiMessageResponse logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.findByToken(request.refreshToken())
            .ifPresent(refreshTokenService::revokeToken);
        return new ApiMessageResponse("Logged out successfully");
    }

    private AuthResponse createAuthResponse(User user) {
        String token = jwtService.generateToken(
            new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")
                )
            )
        );
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        return new AuthResponse(
            token,
            refreshToken,
            "Bearer",
            jwtService.getExpirationSeconds(),
            user.getEmail(),
            user.getDisplayName()
        );
    }

    private void validateRegistrationRequest(String email, String password, String displayName) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name is required");
        }
        validatePassword(password);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasUpper || !hasLower || !hasDigit) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must include upper/lowercase letters and a number"
            );
        }
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        var tokenOpt = userRepository.findByEmail(normalizeEmail(request.email()))
            .map(passwordResetService::createTokenForUser);

        if (tokenOpt.isPresent()) {
            return new ForgotPasswordResponse(
                "Password reset instructions generated successfully",
                tokenOpt.get().getToken()
            );
        }

        return new ForgotPasswordResponse(
            "If an account exists with that email, a reset link has been sent.",
            null
        );
    }

    @PostMapping("/reset-password")
    public ApiMessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        var token = passwordResetService.findByToken(request.token())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password reset token"));

        if (!passwordResetService.isTokenValid(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired or used password reset token");
        }

        validatePassword(request.newPassword());

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        passwordResetService.markAsUsed(token);
        refreshTokenService.revokeAllTokensForUser(user);

        return new ApiMessageResponse("Password has been reset successfully");
    }

    @GetMapping("/me")
    public CurrentUserResponse currentUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Authenticated user not found"));
        return new CurrentUserResponse(user.getEmail(), user.getDisplayName());
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
