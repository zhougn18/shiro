package com.xiao.shiro.realm;

import com.xiao.shiro.dto.UserDto;
import com.xiao.shiro.mapper.UserInfoMapper;
import com.xiao.shiro.pojo.UserInfo;
import com.xiao.shiro.service.UserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerRealm extends AuthorizingRealm {
    @Autowired
    private UserInfoService userInfoService;
    //自定义realm的名称  因为shiro框架中可能存在多个realm  根据realm的名称 来判断用哪个realm做处理
    @Override
    public void setName(String name) {
        super.setName("CustomerRealm");
    }
    //授权 当主体（subject）调用获取用户角色时  会调用doGetAuthorizationInfo这个方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("授权开始");
        //可以从principalCollection 获取用户信息
        UserInfo userInfo = (UserInfo) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        UserDto userExtInfo = userInfoService.findUserExtInfo(userInfo.getId());
        info.addRoles(userExtInfo.getRoles());
        info.addStringPermissions(userExtInfo.getPermissions());
        return info;
    }
    //认证  当主体调用(subject)调用用户登录时 会调用 doGetAuthenticationInfo 这个方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("认证开始");
        //用户登录的时候   subject.login(token);  传进来的token类型 是 UsernamePasswordToken
        //所以可以把authenticationToken 强转成UsernamePasswordToken
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String pwd = new String (token.getPassword());
        UserInfo user = userInfoService.findUserByUserNameAndPwd(username, pwd);
        //模拟从数据库中查出来的用户名密码 分别是 xiaoming 123
        if (user != null) {
            //认证通过  把用户信息存到AuthenticationInfo 对象里
            //SimpleAuthenticationInfo 三个参数 如下
            //1、Object principal  用户信息 可以是任何类型的对象
            //2、Object credentials 密码
            //3、String realmName  当前realm的名称
            AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, pwd, getName());
            return authenticationInfo;
        }
        //认证不通过   抛出异常
        throw new RuntimeException("登录失败");
    }
}
