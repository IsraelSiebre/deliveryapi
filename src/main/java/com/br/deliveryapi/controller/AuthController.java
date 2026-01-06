package com.br.deliveryapi.controller;

import com.br.deliveryapi.dto.admin.AdminRequestDto;
import com.br.deliveryapi.dto.admin.AdminResponseDto;
import com.br.deliveryapi.dto.client.ClientRequestDto;
import com.br.deliveryapi.dto.client.ClientResponseDto;
import com.br.deliveryapi.dto.user.LoginRequestDTO;
import com.br.deliveryapi.dto.user.LoginResponseDTO;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.security.TokenBlacklistService;
import com.br.deliveryapi.service.AdminService;
import com.br.deliveryapi.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ClientService clientService;
    private final AdminService adminService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.email(),
                            loginRequestDTO.password()
                    )
            );

            String token = jwtUtil.generateToken(loginRequestDTO.email());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    @PostMapping("/register/client")
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(dto.toEntity()).toResponseDto());
    }


    @PostMapping("/register/admin")
    public ResponseEntity<AdminResponseDto> registerAdmin(@RequestBody @Valid AdminRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.create(dto.toEntity()).toResponseDto());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            tokenBlacklistService.add(jwt);
            return ResponseEntity.ok("Logout successful. Token blacklisted.");
        }

        return ResponseEntity.badRequest().body("No token provided");
    }
}
