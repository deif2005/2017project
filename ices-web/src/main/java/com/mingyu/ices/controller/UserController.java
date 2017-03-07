package com.mingyu.ices.controller;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.UserEditRequest;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    IUserService userService;

    /**
     * 修改用户密码
     * @param userEditRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateUserById",method = RequestMethod.POST)
    public Object updateUserById(UserEditRequest userEditRequest){
        try{
            if(userEditRequest == null
                    || StringUtils.isBlank(userEditRequest.getPassword())
                    || StringUtils.isBlank(userEditRequest.getNewPassword())){
                throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
            }
            User user=getUserInfo();

            user=userService.updateUserById(userEditRequest,user);//修改密码
            request.getSession().setAttribute(WebConstants.USER_INFO,user);//更新当前登录用户的session
        }catch (BizException biz){
            throw biz;
        }
        catch (Exception e){
            logger.error("UserController-updateUserById error:",e);
        }
         return 0;
    }


}
