package com.kidd.joy.service.impl;

import com.kidd.joy.dao.UserInfoMapper;
import com.kidd.joy.dao.UserPasswordMapper;
import com.kidd.joy.dataobject.UserInfo;
import com.kidd.joy.dataobject.UserPassword;
import com.kidd.joy.error.BusinessException;
import com.kidd.joy.error.EmBusinessError;
import com.kidd.joy.service.UserService;
import com.kidd.joy.service.model.UserModel;
import com.kidd.joy.validator.ValidationResult;
import com.kidd.joy.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserPasswordMapper userPasswordMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {

        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
        if (userInfo == null){
            return null;
        }

        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        return convertToModel(userInfo, userPassword);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {

        if (userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

     /*   if (StringUtils.isEmpty(userModel.getName())
            || userModel.getAge() == null
            || userModel.getGender() == null
            || StringUtils.isEmpty(userModel.getTelephone())
            || StringUtils.isEmpty(userModel.getEncryptPassword())
        ){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }*/

        ValidationResult result = validator.validate(userModel);
        if (result.isHasErorr()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrorMsg());
        }
        UserInfo userInfo = convertFromModel(userModel);
        try{
            userInfoMapper.insertSelective(userInfo);
        } catch (DuplicateKeyException dx){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号重复");
        }
        userModel.setId(userInfo.getId());
        UserPassword userPassword = convertPasswordFromModel(userModel);
        userPasswordMapper.insertSelective(userPassword);
    }

    @Override
    public UserModel validateLogin(String telePhone, String encyptPassword) throws BusinessException {

        UserInfo userInfo = userInfoMapper.selectByTelephone(telePhone);
        if (userInfo == null){
            throw new BusinessException(EmBusinessError.INCORRECT_ACCOUNT);
        }

        UserPassword userPassword = userPasswordMapper.selectByUserId(userInfo.getId());
        if (!com.alibaba.druid.util.StringUtils.equals(userPassword.getEncryptPassword(), encyptPassword)){
            throw new BusinessException(EmBusinessError.INCORRECT_ACCOUNT);
        }

        UserModel userModel = convertToModel(userInfo, userPassword);
        return userModel;
    }

    private UserPassword convertPasswordFromModel(UserModel userModel) {
        UserPassword userPassword = new UserPassword();
        BeanUtils.copyProperties(userModel, userPassword);
        userPassword.setUserId(userModel.getId());
        return userPassword;
    }


    private UserInfo convertFromModel(UserModel userModel) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userModel, userInfo);
        return userInfo;
    }


    private UserModel convertToModel(UserInfo userInfo, UserPassword userPassword){
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userInfo, userModel);

        if (userPassword != null){
            userModel.setEncryptPassword(userPassword.getEncryptPassword());
        }
        return userModel;
    }
}
