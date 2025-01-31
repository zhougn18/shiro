package com.xiao.shiro.session;


import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
//自定义sessionManager
public class CustomerSessionManager extends DefaultWebSessionManager {
    /**
     * 头信息中具有sessionid
     *      请求头：Authorization： sessionid
     *
     * 指定sessionId的获取方式
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //获取请求头Authorization中的数据
        String id = WebUtils.toHttp(request).getHeader("Authorization");
        if(StringUtils.isEmpty(id)) {
            //如果没有携带，生成新的sessionId
            return super.getSessionId(request,response);
        }else{
            //指定sessionId的来源  指定从请求头里获取sessionId  不指定默认从cookie中获取
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
            //sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }
    }
}
