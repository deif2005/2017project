package com.mingyu.ices.domain.dto.user;

import com.mingyu.ices.domain.po.User;

/**
 * UserEditRequest
 * 修改用户信息
 * @author zhaoqian
 * @date 2016/6/29
 */
public class UserEditRequest extends User{

    /**
     *新密码
     */
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
