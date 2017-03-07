package com.mingyu.ices.common;

import com.alibaba.druid.support.json.JSONUtils;
import com.mingyu.ices.domain.common.BizException;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MySimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {
    Logger logger = Logger.getLogger(MySimpleMappingExceptionResolver.class);

    @Override

    protected ModelAndView doResolveException(HttpServletRequest request,HttpServletResponse response, Object handler, Exception exception) {

        HandlerMethod mathod = (HandlerMethod) handler;
        ResponseBody body = mathod.getMethodAnnotation(ResponseBody.class);
        // 判断是否用了@responsebody
        if (body == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("success", false);
            if (exception instanceof BizException) {
                map.put("errorMsg", exception.getMessage());
            } else {
                map.put("errorMsg", "系统异常！");
            }
            //这里需要手动将异常打印出来，由于没有配置log，实际生产环境应该打印到log里面
            exception.printStackTrace();
            //对于非ajax请求，我们都统一跳转到error.jsp页面
            return new ModelAndView("/error", map);
        } else {
            // 如果是ajax请求，JSON格式返回
            try {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                Map<String, Object> map = new HashMap<String, Object>();
               // map.put("success", false);
                // 为安全起见，只有业务异常我们对前端可见，否则统一归为系统异常
                if (exception instanceof BizException) {
                    map.put("rtnCode",((BizException) exception).getRtnCode());
                    map.put("msg",((BizException) exception).getMsg());
                } else {
                    map.put("errorMsg", "系统异常！");
                }
                logger.error(JSONUtils.toJSONString(map));
                writer.write(JSONUtils.toJSONString(map));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}