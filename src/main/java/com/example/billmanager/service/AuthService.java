package com.example.billmanager.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.example.billmanager.dto.user.LoginDTO;
import com.example.billmanager.entity.User;
import com.example.billmanager.vo.LoginVO;
import jakarta.validation.Valid;

public interface AuthService extends IService<User> {
    LoginVO login(@Valid LoginDTO loginDTO);
}
