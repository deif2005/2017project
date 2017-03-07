package com.mingyu.ices.controller;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IUserService;
import com.mingyu.ices.util.EncryptUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * LoginController
 * 登陆相关controller
 * @author yuhao
 * @date 2016/6/30
 */
@Controller
public class LoginController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginView() {
        return new ModelAndView("login");
    }

    /**
     * 用户登陆
     * @param user
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public User userLogin(User user) {
        try {
            //验证请求参数
            if(user == null || StringUtils.isBlank(user.getAccount()) || StringUtils.isBlank(user.getPassword())){
                throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
            }else{
                //密码md5
                user.setPassword(EncryptUtil.MD5(user.getPassword()));
                //查询用户是否存在
                user = userService.getUser(user);
                //账号不存在或密码错误
                if(user == null){
                    throw new BizException(WebConstants.USER_ACCOUNT_OR_PASSWORD_ERROR);
                }else{
                    //缓存用户信息至session中
                    request.getSession().setAttribute(WebConstants.USER_INFO, user);
                }
            }
        }catch (BizException biz) {
            throw biz;
        }catch (Exception e){
            logger.error("LoginController-userLogin error：",e);
            throw new BizException(WebConstants.ERROR);
        }
        return user;
    }

    /**
     * 退出
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(){
        request.getSession().removeAttribute(WebConstants.USER_INFO);
        return new ModelAndView("login");
    }
}
