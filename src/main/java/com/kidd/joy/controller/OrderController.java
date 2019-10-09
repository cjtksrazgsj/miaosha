package com.kidd.joy.controller;

import com.kidd.joy.error.BusinessException;
import com.kidd.joy.error.EmBusinessError;
import com.kidd.joy.response.CommonReturnType;
import com.kidd.joy.service.OrderService;
import com.kidd.joy.service.model.OrderModel;
import com.kidd.joy.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("order")
public class OrderController extends BaseController{

    @Autowired
    OrderService orderService;

    @Autowired
    HttpServletRequest request;

    @RequestMapping(value = "create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType create(
            @RequestParam Integer itemId,
            @RequestParam(required = false) Integer promoId,
            @RequestParam Integer amount
    ) throws BusinessException {
        boolean isLogin = (boolean)(request.getSession().getAttribute("IS_LOGIN"));
        UserModel userModel = (UserModel)(request.getSession().getAttribute("LOGIN_USER"));
        if (!isLogin || userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户未登录");
        }

        OrderModel orderModel
                = orderService.createOrder(userModel.getId(), itemId, promoId, amount);

        return CommonReturnType.create(orderModel);
    }


}
