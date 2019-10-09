package com.kidd.joy;

import com.kidd.joy.dao.UserInfoMapper;
import com.kidd.joy.dataobject.UserInfo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Hello world!
 */
@SpringBootApplication(scanBasePackages = "com.kidd.joy")
@RestController
@MapperScan(value = "com.kidd.joy.dao")
public class MiaoshaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiaoshaApplication.class, args);
    }

    @Autowired
    UserInfoMapper userInfoMapper;

    @RequestMapping(value = "/")
    @ResponseBody
    public String defaultRoute() {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(1);
        if (userInfo == null){
            return "找不到对应的用户！";
        } else {
            return userInfo.getName();
        }
    }
}
