package com.kujudy.springbootmall.service;

import com.kujudy.springbootmall.dto.UserLoginRequest;
import com.kujudy.springbootmall.dto.UserRegisterRequest;
import com.kujudy.springbootmall.model.User;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);
    User getUserById(Integer userId);
    User login(UserLoginRequest userLoginRequest);
}
