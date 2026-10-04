package com.example.billmanager.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.billmanager.config.JwtUtil;
import com.example.billmanager.dto.user.LoginDTO;
import com.example.billmanager.entity.User;
import com.example.billmanager.enums.ErrorCode;
import com.example.billmanager.exception.BusinessException;
import com.example.billmanager.mapper.AuthMapper;
import com.example.billmanager.service.AuthService;
import com.example.billmanager.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {

        String account = loginDTO.getUserName().trim();

        User user;

        if (account.matches("^1[3-9]\\d{9}$")) {
            user = lambdaQuery()
                    .eq(User::getPhoneNumber, account)
                    .one();
        } else if (account.matches("^([A-Za-z0-9_\\-.\\u4e00-\\u9fa5])+@(163.com|qq.com)$")){
            user = lambdaQuery()
                    .eq(User::getEmail, account)
                    .one();
        } else {
            user = lambdaQuery()
                    .eq(User::getUserName, account)
                    .one();
        }

        if (user == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "账号或密码错误");
        }

        boolean passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());

        if (!passwordMatch) {
            throw new BusinessException(ErrorCode.CONFLICT, "账号或密码错误");
        }

        String token = jwtUtil.createToken(user);

        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getUserId());
        loginVO.setUserName(user.getUserName());
        loginVO.setDisplayName(user.getDisplayName());
        loginVO.setAvatar(user.getAvatar());
        loginVO.setPhoneNumber(user.getPhoneNumber());
        loginVO.setIdentity(user.getIdentity());
        loginVO.setAccessToken(token);
        loginVO.setTokenType("Bearer");
        loginVO.setExpiresIn(jwtUtil.getExpireMinutes());

        return loginVO;
    }
}
