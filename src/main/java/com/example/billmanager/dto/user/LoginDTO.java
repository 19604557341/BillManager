package com.example.billmanager.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求参数")
public class LoginDTO {

    @NotBlank(message = "登录用户名不可为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;
}
