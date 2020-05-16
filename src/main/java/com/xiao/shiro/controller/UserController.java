package com.xiao.shiro.controller;

import com.xiao.shiro.dto.UserDto;
import com.xiao.shiro.pojo.UserInfo;
import com.xiao.shiro.service.UserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserInfoService userInfoService;
    @RequestMapping("/getUserInfo")
    public List<UserInfo> getUserInfo() {
        return userInfoService.getUserInfo();
    }
    @RequestMapping("/saveUserInfo")
    public void saveUserInfo(UserInfo userInfo) {
        userInfoService.saveUserInfo(userInfo);
    }
    @RequestMapping("/login")
    public String login(UserInfo userInfo) {
        try {
            UsernamePasswordToken upt = new UsernamePasswordToken(userInfo.getUsername(), userInfo.getPassword());
            SecurityUtils.getSubject().login(upt);
            return "登录成功"+SecurityUtils.getSubject().getSession().getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "登录失败";
        }
    }
    @RequestMapping("/getUserRoleInfo")
    public UserDto getUserRoleInfo(Long userId) {
        return userInfoService.findUserExtInfo(userId);
    }
}
