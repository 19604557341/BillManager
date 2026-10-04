package com.example.billmanager.controller;

import com.example.billmanager.dto.user.UserCreateDTO;
import com.example.billmanager.entity.User;
import com.example.billmanager.service.UserService;
import com.example.billmanager.vo.Result;
import com.example.billmanager.vo.amount.FieldCheckVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "注册，登录，修改，注销，删除接口")
public class UserController {

    private final UserService userService;

    @PostMapping
    public Result<User> saveUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        User user = userService.saveUser(userCreateDTO);

        return Result.success("注册成功", user);
    }

    @GetMapping("/check-account")
    public Result<FieldCheckVO> checkAccount(@RequestParam("account") String account) {
        return Result.success("查询成功", userService.checkAccount(account));
    }
}
