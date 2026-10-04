package com.example.billmanager.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserIdentity {

    ADMIN("管理员"),

    USER("用户");

    @EnumValue
    @JsonValue
    private final String code;

    private final String userIdentity;

   UserIdentity(String userIdentity) {
       this.code = name();
       this.userIdentity = userIdentity;
   }
}
