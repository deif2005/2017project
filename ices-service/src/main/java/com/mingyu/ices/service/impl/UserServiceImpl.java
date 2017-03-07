package com.mingyu.ices.service.impl;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.IUserDao;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.UserEditRequest;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IUserService;
import com.mingyu.ices.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserService
 * 用户信息相关serviceImpl
 * @author yuhao
 * @date 2016/6/30
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    IUserDao userDao;

    @Override
    public User updateUserById(UserEditRequest userEditRequest,User user) {
        if (!(user.getPassword().equals( EncryptUtil.MD5(userEditRequest.getPassword())))){
            throw new BizException(WebConstants.ORIGINAL_PASSWORD_ERROR);
        }else{
            userEditRequest.setId(user.getId());
            userEditRequest.setModifier(user.getId());
            userEditRequest.setNewPassword(EncryptUtil.MD5(userEditRequest.getNewPassword()));
            userDao.updateUserById(userEditRequest);
            user.setPassword(userEditRequest.getNewPassword());
            return user;
        }
    }

    @Override
    public User getUser(User user) {
        return userDao.getUser(user);
    }
}
