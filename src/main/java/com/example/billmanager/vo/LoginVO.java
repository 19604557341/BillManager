package com.example.billmanager.vo;

import com.example.billmanager.enums.UserIdentity;
import lombok.Data;

@Data
public class LoginVO {

    private Long userId;

    private String userName;

    private String displayName;

    private String avatar;

    private String phoneNumber;

    private UserIdentity identity;

    private String accessToken;

    private String tokenType = "Bearer";

    private Long expiresIn;
}
