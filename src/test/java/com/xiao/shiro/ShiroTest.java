package com.xiao.shiro;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.Test;

import java.util.Set;

public class ShiroTest {
    @Test
    public void testAuthenticate(){
        //1、加载ini文件  通过ini文件创建IniSecurityManagerFactory
        IniSecurityManagerFactory managerFactory = new IniSecurityManagerFactory("classpath:shiro1.ini");
        //2、获取安全管理器（SecurityManager）
        SecurityManager instance = managerFactory.getInstance();
        //3、把当前SecurityManager 绑定到当前环境
        SecurityUtils.setSecurityManager(instance);
        //4、获取subject 主体对象
        Subject subject = SecurityUtils.getSubject();
        //5、设置用户名和密码
        UsernamePasswordToken token = new UsernamePasswordToken("xiaoming","123");
        //6、登录
        subject.login(token);
        //7、查看用户是否登录成功  true成功 false 失败
        System.out.println(subject.isAuthenticated());
    }

    @Test
    public void testAuthrizer(){
        //1、加载ini文件  通过ini文件创建IniSecurityManagerFactory
        IniSecurityManagerFactory managerFactory = new IniSecurityManagerFactory("classpath:shiro2.ini");
        //2、获取安全管理器（SecurityManager）
        SecurityManager instance = managerFactory.getInstance();
        //3、把当前SecurityManager 绑定到当前环境
        SecurityUtils.setSecurityManager(instance);
        //4、获取subject 主体对象
        Subject subject = SecurityUtils.getSubject();
        //5、设置用户名和密码
        UsernamePasswordToken token = new UsernamePasswordToken("xiaohong","123456");
        //6、登录  会调用realm里的doGetAuthenticationInfo方法
        subject.login(token);
        //7、查看用户拥有的角色和权限  当获取角色或者权限时 会调用realm里的doGetAuthorizationInfo方法
        System.out.println(subject.hasRole("manager"));
        System.out.println(subject.isPermitted("user:find"));
    }

    @Test
    public void testCustomerRealm(){
        //1、加载ini文件  通过ini文件创建IniSecurityManagerFactory
        IniSecurityManagerFactory managerFactory = new IniSecurityManagerFactory("classpath:shiro3.ini");
        //2、获取安全管理器（SecurityManager）
        SecurityManager instance = managerFactory.getInstance();
        //3、把当前SecurityManager 绑定到当前环境
        SecurityUtils.setSecurityManager(instance);
        //4、获取subject 主体对象
        Subject subject = SecurityUtils.getSubject();
        //5、设置用户名和密码
        UsernamePasswordToken token = new UsernamePasswordToken("xiaoming","123");
        //6、登录
        subject.login(token);
        //7、查看用户拥有的角色和权限
        System.out.println(subject.hasRole("manager"));
        System.out.println(subject.isPermitted("user:find"));
    }

    @Test
    public void testCacheManager(){
        //1、加载ini文件  通过ini文件创建IniSecurityManagerFactory
        IniSecurityManagerFactory managerFactory = new IniSecurityManagerFactory("classpath:shiro3.ini");
        //2、获取安全管理器（SecurityManager） 如果启用CacheManager 需要CachingSecurityManager 对象
        CachingSecurityManager instance = (CachingSecurityManager) managerFactory.getInstance();
        //3、创建缓存管理对象  （这里使用ehcache做缓存所以使用EhCacheManager） 可以使用其他的缓存对象
        EhCacheManager ehCacheManager = new EhCacheManager();
        //4、读取ehcache配置文件
        ehCacheManager.setCacheManagerConfigFile("classpath:shiro-ehcache.xml");
        //5、把EhCacheManager对象 设置到CachingSecurityManager 安全管理器中
        instance.setCacheManager(ehCacheManager);
        //6、把当前SecurityManager 绑定到当前环境
        SecurityUtils.setSecurityManager(instance);
        //7、获取subject 主体对象
        Subject subject = SecurityUtils.getSubject();
        //8、设置用户名和密码
        UsernamePasswordToken token = new UsernamePasswordToken("xiaoming","123");
        //9、登录
        subject.login(token);
        //10、查看用户拥有的角色和权限   第一次获取权限信息的时候会调用realm中的doGetAuthorizationInfo 方法 第二次 直接从缓存中获取  不走realm
        System.out.println(subject.hasRole("manager"));
        System.out.println(subject.isPermitted("user:find"));
        //获取ehcache 用户缓存信息
        Cache<Object, Object> cache = ehCacheManager.getCache("CustomerRealm.authorizationCache");
        //清除 xiaoming用户对应的缓存信息
        cache.remove(new SimplePrincipalCollection("xiaoming", "CustomerRealm"));
        System.out.println(subject.hasRole("manager"));
        System.out.println(subject.isPermitted("user:find"));
    }
}
