package com.mingyu.ices.dao;

import com.mingyu.ices.domain.dto.user.UserEditRequest;
import com.mingyu.ices.domain.po.User;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * UserDao
 * 用户信息dao
 * @author yuhao
 * @date 2016/6/30
 */
@Repository
public interface IUserDao {

    /**
     * 获取用户信息
     * @param user
     * @return
     */
    public User getUser(User user);
    
    /**
     * 根据id修改用户信息
     * @param userEditRequest
     */
    public void updateUserById(UserEditRequest userEditRequest);
}