package com.xiao.shiro.controller;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Controller
public class TestController {
    @RequestMapping("/user/index")
    @ResponseBody
    public String index(){
        return "访问index成功";
    }

    @RequestMapping("/user/select")
    @ResponseBody
    public String select(){
        return "访问select成功";
    }

    @RequestMapping("/user/delete")
    @ResponseBody
    public String delete(){
        return "访问delete成功";
    }

    @RequestMapping("/user/login")
    @ResponseBody
    public String login(){
        return "访问login成功";
    }
    //跳登录页
    @RequestMapping("/tologin")
    public String tologin(){
        return "login";
    }
    //跳未授权页面
    @RequestMapping("/toUnauthorized")
    public String toUnauthorized(){
        return "unauthorized";
    }

    //同时有user-add和user-delete 权限才能访问
    @RequiresPermissions(value = {"user-add","user-delete"})
    @RequestMapping("/testPermission")
    @ResponseBody
    public String testPermission(){
        return "测试  Permission 成功";
    }

    //有管理员角色 才能访问
    @RequiresRoles(value = "管理员")
    @RequestMapping("/testRoles")
    @ResponseBody
    public String tesRoles(){
        return "测试  role 成功";
    }

    public static void main(String[] args) {
        //三个参数如下
        //1、Object source 原始密码
        //2、Object salt 盐值
        //3、int hashIterations  加盐几次
        Md5Hash pwd = new Md5Hash("12345", "salt", 2);
        System.out.println(pwd.toString());
    }
    @RequestMapping("/show")
    @ResponseBody
    public String show(HttpSession session){
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String s = attributeNames.nextElement();
            Object attribute = session.getAttribute(s);
            System.out.println("name："+s +"value:  "+JSON.toJSONString(attribute));
        }
        return "aaa";
    }
}
