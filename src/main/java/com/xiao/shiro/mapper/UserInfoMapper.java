package com.xiao.shiro.mapper;

import com.xiao.shiro.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface UserInfoMapper extends Mapper<UserInfo> {
    @Select("SELECT  p.code,r.name FROM pe_user u INNER JOIN pe_user_role ur ON u.id=ur.user_id INNER JOIN pe_role r ON ur.role_id=r.id INNER JOIN pe_role_permission rp ON rp.role_id=r.id INNER JOIN pe_permission p ON rp.permission_id = p.id WHERE u.id=#{userId}")
    List<Map<String,String>> findUserRoleInfo(@Param("userId") Long userId);
}
