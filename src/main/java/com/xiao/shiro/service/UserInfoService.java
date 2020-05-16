package com.xiao.shiro.service;

import com.xiao.shiro.dto.UserDto;
import com.xiao.shiro.mapper.UserInfoMapper;
import com.xiao.shiro.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    public List<UserInfo> getUserInfo(){
        return userInfoMapper.selectAll();
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    public UserInfo findUserByUserNameAndPwd(String userName, String pwd) {
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(pwd);
        userInfo.setUsername(userName);
        List<UserInfo> userInfos = userInfoMapper.select(userInfo);
        if (userInfos != null && !userInfos.isEmpty()) {
            return userInfos.get(0);
        }
        return null;
    }

    public UserDto findUserExtInfo(Long userId) {
        UserDto dto = new UserDto();
        List<Map<String, String>> userRoleInfo = userInfoMapper.findUserRoleInfo(userId);
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        if (userRoleInfo != null && !userRoleInfo.isEmpty()) {
            userRoleInfo.forEach(x->{
                permissions.add(x.get("code"));
                roles.add(x.get("name"));
            });
        }
        dto.setRoles(roles);
        dto.setPermissions(permissions);
        return dto;
    }
}
