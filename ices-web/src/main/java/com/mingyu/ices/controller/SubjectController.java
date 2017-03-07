package com.mingyu.ices.controller;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.ISubjectService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
@Controller
@RequestMapping("/subject")
public class SubjectController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(SubjectController.class);

    @Autowired
    ISubjectService subjectService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        return new ModelAndView("/subject/subject-list");
    }
    /**
     * 分页查询科目列表
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pageSubject",method = RequestMethod.POST)
    public BizDataPage pageSubject(Integer page){
        BizDataPage<Subject> subjectBizDataPage=new BizDataPage<>();
        try {
             subjectBizDataPage=subjectService.pageSubject(page);
        }catch (Exception e){
            logger.error("subject-listSubject error: ",e);
            throw new BizException(WebConstants.ERROR);
        }
        return subjectBizDataPage;
    }

    /**
     * 查询科目列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listSubject",method = RequestMethod.POST)
    public List<Subject> listSubject(){
        List<Subject> subjectList=new ArrayList<>();
        try {
            subjectList=subjectService.listSubject("1");
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("subject-listSubject");
            throw new BizException(WebConstants.ERROR);
        }
        return subjectList;
    }

    /**
     * 根据id查询科目信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getSubjectById",method = RequestMethod.POST)
    public Subject getSubjectById(String id){
        Subject subject=new Subject();
        try {
            subject= subjectService.getSubjectById(id);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("subject-getSubjectById error: " ,e);
            throw new BizException(WebConstants.ERROR);
        }
        return subject;
    }

    /**
     * 添加科目信息
     * @param subject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/insertSubject",method = RequestMethod.POST)
    public Object insertSubject(Subject subject){

        try {
            if (subject==null || StringUtils.isBlank(subject.getName())){
                throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
            }
            User user = getUserInfo();//获取登陆用户信息
            subjectService.insertSubject(subject,user);
        }catch (BizException biz){
            throw biz;
        }
        catch (Exception e){
            logger.error("subject-insertSubject error: ",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    /**
     * 根据id修改科目信息
     * @param subject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateSubjectById",method = RequestMethod.POST)
    public Object updateSubjectById(Subject subject){
        if (subject==null ||StringUtils.isBlank(subject.getName())){
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
        }
        try {
            subjectService.updateSubjectById(subject,getUserInfo());
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("subject-updateSubjectById error :",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    /**
     * 删除科目信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/deleteSubjectById",method = RequestMethod.POST)
    public Object deleteSubjectById(String id){
        Integer result = 0;
        try {
            result = subjectService.deleteSubjectById(id);
            if (result==1){
                throw new BizException(WebConstants.EXISTS_RELATED_DATA,"该科目存在关联的题信息");
            }
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("subject-deleteSubjectById",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }
}
