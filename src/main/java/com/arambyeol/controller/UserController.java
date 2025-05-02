package com.arambyeol.controller;

import com.arambyeol.dto.UserRequestDto;
import com.arambyeol.dto.PasswordResetDto;
import com.arambyeol.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "중복된 사용자명")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.register(requestDto));
    }

    @Operation(summary = "로그인", description = "사용자 인증을 수행합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    @Operation(summary = "사용자명 중복 확인", description = "사용자명의 중복 여부를 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "중복 확인 완료")
    })
    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.checkUsernameDuplicate(username));
    }

    @Operation(summary = "비밀번호 재설정", description = "사용자의 비밀번호를 재설정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDto resetDto) {
        userService.resetPassword(resetDto);
        return ResponseEntity.ok().build();
    }
} 