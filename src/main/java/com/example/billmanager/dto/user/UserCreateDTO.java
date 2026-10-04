package com.example.billmanager.dto.user;

import com.example.billmanager.enums.UserIdentity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema
public class UserCreateDTO {

    //用户名
    @NotBlank(message = "登录用户名不能为空")
    @Size(min = 4, max = 30, message = "登录用户名长度应为4-30字符")
    @Pattern(regexp = "^[a-zA-Z0-9 ._]+$", message = "登录用户名只能包含大小写字母，数字，.和_")
    private String userName;

    //昵称
    @Size(max = 30, message = "昵称长度不能超过30字符")
    private String displayName;

    //头像
    private String avatar;

    //手机号 可重复
   @Pattern(regexp = "^$|^1[3-9]\\d{9}", message = "手机号格式不正确")
    private String phoneNumber;

    //邮箱
    @Pattern(regexp = "^([A-Za-z0-9_\\-.\\u4e00-\\u9fa5])+@(163.com|qq.com)$", message = "邮箱格式错误")
    private String email;

    //密码
    @NotBlank(message = "密码不可为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[._@!])[A-za-z0-9._@!]{8,20}$")
    private String password;

    //身份
    @NotNull(message = "身份不能为空")
    private UserIdentity identity;


}
