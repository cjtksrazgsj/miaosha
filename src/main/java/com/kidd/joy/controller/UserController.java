package com.kidd.joy.controller;

import com.kidd.joy.controller.viewobject.UserVO;
import com.kidd.joy.error.BusinessException;
import com.kidd.joy.error.EmBusinessError;
import com.kidd.joy.response.CommonReturnType;
import com.kidd.joy.service.UserService;
import com.kidd.joy.service.model.UserModel;
import com.kidd.joy.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("user")
public class UserController extends BaseController{

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @RequestMapping(value = "get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType get(@RequestParam Integer id) throws BusinessException {
        if (id == null){
            return null;
        }
        UserModel userModel = userService.getUserById(id);
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO = convertToVO(userModel);
        return CommonReturnType.create(userVO);
    }


    @ResponseBody
    @RequestMapping(value = "getOpt", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOpt(@RequestParam String telePhone, HttpServletRequest httpServletRequest){

        //generate a code
        Random random = new Random();
        int code = random.nextInt(99999);
        code += 10000;

        //save it in system
        httpServletRequest.getSession().setAttribute(telePhone, String.valueOf(code));
        System.out.println("system save telephone = " + telePhone + ", code = " + code);

        //return to user
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(
            @RequestParam String telePhone,
            @RequestParam String optCode,
            @RequestParam Byte gender,
            @RequestParam Integer age,
            @RequestParam String name,
            @RequestParam String password
    ) throws BusinessException, NoSuchAlgorithmException {

        String inSessionCode = (String)this.httpServletRequest.getSession().getAttribute(telePhone);
        if (inSessionCode == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        if (!com.alibaba.druid.util.StringUtils.equals(optCode, inSessionCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setTelephone(telePhone);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setEncryptPassword(MD5Util.toMd5Str(password));
        userModel.setRegisterMode("byPhone");

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(
            @RequestParam String telePhone,
            @RequestParam String password
    ) throws BusinessException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(telePhone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号或密码不能为空！");
        }

        UserModel userModel
                = userService.validateLogin(telePhone, MD5Util.toMd5Str(password));

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        return CommonReturnType.create(null);
    }

    private UserVO convertToVO(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }




}
