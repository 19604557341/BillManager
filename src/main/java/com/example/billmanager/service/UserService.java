package com.example.billmanager.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.user.UserCreateDTO;
import com.example.billmanager.entity.User;
import com.example.billmanager.vo.amount.FieldCheckVO;

public interface UserService extends IService<User> {
    User saveUser(UserCreateDTO userCreateDTO);

    FieldCheckVO checkAccount(String account);
}
