package com.xiao.shiro.config;

import com.xiao.shiro.realm.CustomerRealm;
import com.xiao.shiro.session.CustomerSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {


    @Bean
    public CustomerRealm getCustomerRealm(){
        return new CustomerRealm();
    }

    @Bean
    public SecurityManager getSecurityManager(CustomerRealm realm){
        //创建安全管理器  并把realm交给管理器管理
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm);
        //将自定义的会话管理器注册到安全管理器中
        securityManager.setSessionManager(sessionManager());
        //将自定义的redis缓存管理器注册到安全管理器中
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    //配置 启动shiro注解
    @Bean
    public AuthorizationAttributeSourceAdvisor  getAuthorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
    //配置 shiro过滤条件和跳转页面
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(securityManager);
        //登录页面
        bean.setLoginUrl("/tologin");
        //授权失败跳转页面
        bean.setUnauthorizedUrl("/toUnauthorized");
        //必须用LinkedHashMap  要保证过滤的顺序
        Map<String, String> map = new LinkedHashMap<>();
        //anon  匿名访问  也就是说不用登录和授权就能访问
        //authc  需要认证登录才能访问
        //logout 注销 退出登录   跳转到  bean.setLoginUrl()方法设置的页面
        //perms[xx]  有xx的权限才能访问
        //roles[xx]  有xx的角色才能访问
        map.put("/user/index", "anon");
        map.put("/login", "anon");
        //表示有select-user的权限才可以访问
        map.put("/user/select", "perms[select-user]");
        //表示系统管理员的角色才能访问
        map.put("/user/delete", "roles[系统管理员]");
        //只有认证登录才能访问
        map.put("/**", "authc");
        //设置请求的过滤链   过滤链是有顺序的  需要认证的一般放在匿名访问的后面
        bean.setFilterChainDefinitionMap(map);
        return bean;
    }

    //开启shiro注解的方式 要配置 ，DefaultAdvisorAutoProxyCreator，AuthorizationAttributeSourceAdvisor  这两个对象
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        //开启对springaop的代理
        autoProxyCreator.setProxyTargetClass(true);
        return autoProxyCreator;
    }


    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    /**
     * 1.redis的控制器，操作redis
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        return redisManager;
    }

    /**
     * 2.sessionDao
     */
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());
        return sessionDAO;
    }

    /**
     * 3.会话管理器
     */
    public DefaultWebSessionManager sessionManager() {
        CustomerSessionManager sessionManager = new CustomerSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }

    /**
     * 4.缓存管理器
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
}
