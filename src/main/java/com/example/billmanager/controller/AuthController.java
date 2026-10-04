package com.example.billmanager.controller;

import com.example.billmanager.dto.user.LoginDTO;
import com.example.billmanager.service.AuthService;
import com.example.billmanager.vo.LoginVO;
import com.example.billmanager.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {

        LoginVO loginVO = authService.login(loginDTO);

        return Result.success("登录成功", loginVO);
    }
}
