package com.mingyu.ices.service;

import com.mingyu.ices.domain.dto.user.UserEditRequest;
import com.mingyu.ices.domain.po.User;

/**
 * UserService
 * 用户信息相关service
 * @author yuhao
 * @date 2016/6/30
 */
public interface IUserService {

    /**
     * 获取用户信息
     * @param user
     * @return
     */
    public User getUser(User user);
    
    /**
     * 修改用户密码
     * @param userEditRequest
     */
    public User updateUserById(UserEditRequest userEditRequest,User user);
}
