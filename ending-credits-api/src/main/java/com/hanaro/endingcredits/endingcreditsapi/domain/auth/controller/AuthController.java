package com.hanaro.endingcredits.endingcreditsapi.domain.auth.controller;

import com.hanaro.endingcredits.endingcreditsapi.domain.auth.dto.*;
import com.hanaro.endingcredits.endingcreditsapi.domain.auth.service.AuthService;
import com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.ApiResponseEntity;
import com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.exception.handler.JwtHandler;
import com.hanaro.endingcredits.endingcreditsapi.utils.apiPayload.exception.handler.MemberHandler;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.apache.http.HttpHeaders;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ApiResponseEntity<TokenPairResponseDto> login(LoginDto loginDto) {
        try {
            TokenPairResponseDto tokenPair = authService.generateTokenPairWithLoginDto(loginDto);
            return ApiResponseEntity.onSuccess(tokenPair);
        } catch (MemberHandler e) {
            // MemberHandler 예외 처리
            return ApiResponseEntity.onFailure(
                    e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponseEntity signup(SignUpDto signUpDto) {
        try{
            UUID memberId = authService.signUp(signUpDto);
            return ApiResponseEntity.onSuccess(memberId);
        } catch (MemberHandler e) {
            // MemberHandler 예외 처리
            return ApiResponseEntity.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }

    @Operation(summary = "회원탈퇴")
    @PostMapping("/unsubscribe")
    public ApiResponseEntity unsubscribe(@AuthenticationPrincipal UUID memberId) {
        try{
            authService.unsubscribe(memberId);
            return ApiResponseEntity.onSuccess(null);
        } catch (MemberHandler e) {
            // MemberHandler 예외 처리
            return ApiResponseEntity.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }

    @Operation(summary = "아이디 중복 확인")
    @PostMapping("/id")
    public ApiResponseEntity checkIdentifier(String identifier) {
        try {
            authService.checkIdentifier(identifier);
            return ApiResponseEntity.onSuccess("사용 가능한 아이디입니다.", null);
        } catch (MemberHandler e) {
            // MemberHandler 예외 처리
            return ApiResponseEntity.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }

    @Operation(summary = "JWT 재발급", description = "refresh token을 이용해 새로운 access token과 refresh token을 발급합니다.")
    @PostMapping("/reissue")
    public ApiResponseEntity<TokenPairResponseDto> refresh(String refreshToken) {
        try{
            return ApiResponseEntity.onSuccess(authService.refreshTokenPair(refreshToken));
        }catch (JwtHandler e) {
            // JwtHandler 예외 처리
            return ApiResponseEntity.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }

//    @Operation(summary = "비밀번호 재설정")
//    @PatchMapping("/password")
//    public ApiResponseEntity changePassword(@AuthenticationPrincipal UUID memberId, String password) {
//        authService.changePassword(memberId, password);
//        return ApiResponseEntity.onSuccess(null);
//    }

    @Operation(summary = "간편 비밀번호 재설정")
    @PatchMapping("/simple-password")
    public ApiResponseEntity changeSimplePassword(String identifier, String simplePassword) {
        authService.changeSimplePassword(identifier, simplePassword);
        return ApiResponseEntity.onSuccess(null);
    }

    @Operation(summary = "카카오 로그인 백엔드에서 test 시 사용")
    @GetMapping("/klogin")
    public ResponseEntity<Object> kakaoLogin() {  // Front 연동 후 삭제 예정
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + authService.getKakaoClientId() +
                "&redirect_uri=" + authService.getKakaoRedirectUri() +
                "&response_type=code";

        // 클라이언트를 카카오 인증 URL로 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, kakaoAuthUrl)
                .build();
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/kakao")
    public ApiResponseEntity kakaoLoginCallback(@RequestParam String code) {
        try {
            // 카카오 로그인 프로세스 수행 (AccessToken 및 사용자 정보 처리)
            TokenPairResponseDto tokenPair = authService.processKakaoLogin(code);

            // 인증 성공 응답 반환
            return ApiResponseEntity.onSuccess(tokenPair);
        } catch (MemberHandler e) {
            return ApiResponseEntity.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        }
    }
}

