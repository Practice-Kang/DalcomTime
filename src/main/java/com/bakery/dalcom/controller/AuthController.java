package com.bakery.dalcom.controller;

import com.bakery.dalcom.domain.User;
import com.bakery.dalcom.dto.AuthRequest;
import com.bakery.dalcom.dto.AuthResponse;
import com.bakery.dalcom.security.JwtTokenProvider;
import com.bakery.dalcom.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtTokenProvider jwtTokenProvider, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        // 1️⃣ 클라이언트가 보낸 username으로 DB에서 사용자 조회
        User user = userService.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        // 2️⃣ 비밀번호 검증
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // 3️⃣ 토큰 생성 및 쿠키 저장
        String accessToken = jwtTokenProvider.generateToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtTokenProvider.getJwtExpirationInMillis() / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtTokenProvider.getJwtRefreshExpirationInMillis() / 1000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);
            String newAccessToken = jwtTokenProvider.generateToken(username);

            // 새로운 access_token 쿠키 생성
            ResponseCookie accessCookie = ResponseCookie.from("access_token", newAccessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(jwtTokenProvider.getJwtExpirationInMillis() / 1000)
                    .build();

            // 응답
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .body(new AuthResponse(newAccessToken, refreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
