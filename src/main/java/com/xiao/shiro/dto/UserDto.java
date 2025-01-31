package com.xiao.shiro.dto;

import com.xiao.shiro.pojo.PePermission;
import com.xiao.shiro.pojo.PeRole;

import java.util.List;
import java.util.Set;

public class UserDto {
    private Set<String> roles;
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
