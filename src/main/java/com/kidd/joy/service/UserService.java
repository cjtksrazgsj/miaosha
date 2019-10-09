package com.kidd.joy.service;

import com.kidd.joy.error.BusinessException;
import com.kidd.joy.service.model.UserModel;

public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;

    UserModel validateLogin(String telePhone, String toMd5Str) throws BusinessException;
}
