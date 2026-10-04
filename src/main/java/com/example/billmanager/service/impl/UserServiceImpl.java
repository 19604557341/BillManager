package com.example.billmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.billmanager.dto.user.UserCreateDTO;
import com.example.billmanager.entity.User;
import com.example.billmanager.enums.ErrorCode;
import com.example.billmanager.exception.BusinessException;
import com.example.billmanager.mapper.UserMapper;
import com.example.billmanager.service.UserService;
import com.example.billmanager.vo.amount.FieldCheckVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    //新增用户
    @Override
    public User saveUser(UserCreateDTO userCreateDTO) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(userCreateDTO.getUserName() != null, User::getUserName, userCreateDTO.getUserName());
        User selectOneUser = baseMapper.selectOne(userLambdaQueryWrapper);

        if (selectOneUser != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "登录用户名已存在");
        }

        User user = new User();
        user.setUserName(userCreateDTO.getUserName());
        if (userCreateDTO.getDisplayName() == null || userCreateDTO.getDisplayName().isEmpty()) {
            user.setDisplayName(user.getUserName());
        }
        user.setDisplayName(userCreateDTO.getDisplayName());
        user.setAvatar(userCreateDTO.getAvatar());
        user.setPhoneNumber(userCreateDTO.getPhoneNumber());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setIdentity(userCreateDTO.getIdentity());

        baseMapper.insert(user);

        return user;
    }

    //实时查询用户名是否重复
    @Override
    public FieldCheckVO checkAccount(String account) {

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(account != null, User::getUserName, account);
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if (user != null) {
            return FieldCheckVO.fail("登录用户名已存在");
        }
        return FieldCheckVO.ok("用户名可使用");
    }
}
