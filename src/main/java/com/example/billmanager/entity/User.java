package com.example.billmanager.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.billmanager.enums.UserIdentity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    //用户ID
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    //用户名
    private String userName;

    //昵称
    private String displayName;

    //头像
    private String avatar;

    //手机号
    private String phoneNumber;

    //邮箱
    private String email;

    //密码
    private String password;

    //身份
    private UserIdentity identity;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
