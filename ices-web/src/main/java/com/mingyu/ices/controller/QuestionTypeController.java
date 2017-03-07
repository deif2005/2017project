package com.mingyu.ices.controller;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.po.QuestionType;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IQuestionTypeService;
import com.mingyu.ices.service.ISubjectService;
import com.mingyu.ices.xls.XLSReader;
import com.sun.net.httpserver.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Created by defi on 2016/7/1.
 */
@Controller
@RequestMapping("/questiontype")
public class QuestionTypeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(QuestionTypeController.class);

    @Autowired
    IQuestionTypeService questionTypeService;
    @Autowired
    ISubjectService subjectService;

    /**
     * 跳转题型管理页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView loginView() {
        List<Subject> subList = subjectService.listSubject("1");
        return new ModelAndView("/question/question-type-list").addObject("subList",subList);
    }

    /**
     * 分页显示题型信息
     * @param page
     * @param subjectId
     */
    @ResponseBody
    @RequestMapping(value = "/queryQuestionType",method = RequestMethod.POST)
    public BizDataPage listQuestionType(String subjectId,Integer page){
        BizDataPage<QuestionType> questionTypeBizDataPage = new BizDataPage<>();
        try {
            questionTypeBizDataPage = questionTypeService.listQuestionType(subjectId,page);
        }catch (Exception e){
            logger.error("查询题型信息时发生了异常",e);
            throw new BizException("查询题型信息时发生了异常",e.getMessage());
        }
        return questionTypeBizDataPage;
    }

    /**
     * 查询题型信息
     * @param
     */
    @ResponseBody
    @RequestMapping(value = "/getQuestionTypeById",method = RequestMethod.POST)
    public QuestionType getQuestionType(String Id){
        try {
            QuestionType questionType = questionTypeService.getQuestionTypeById(Id);
            return questionType;
        }catch (Exception e){
            logger.error("QuestionTypeController-getQuestionType",e);
            throw new BizException("获取题型信息出错",e.getMessage());
        }
    }

    /**
     * 添加题型信息
     * @param
     */
    @ResponseBody
    @RequestMapping(value = "/addQuestionType",method = RequestMethod.POST)
    public Object addQuestionType(QuestionType questionType){
        try {
            User user = super.getUserInfo();
            questionType.setCreater(user.getId());
            questionTypeService.addQuestionType(questionType);
        }catch (Exception e){
            logger.error("新增题型信息时发生了异常",e);
            throw new BizException("新增题型信息时发生了异常",e.getMessage());
        }
        return 0;
    }

    /**
     * 更新题型信息
     * @param
     */
    @ResponseBody
    @RequestMapping(value = "/updateQuestionType", method = RequestMethod.POST)
    public Object updateQuestionType(QuestionType questionType){
        try {
            User user = getUserInfo();
            questionType.setModifier(user.getId()); //
            questionTypeService.updateQuestionType(questionType);
        }catch (Exception e){
            logger.error("更新题型信息时发生了异常",e);
            throw new BizException("更新题型信息时发生了异常",e.getMessage());
        }
        return 0;
    }

    /**
     * 从Excel中导入题型数据
     * @param request
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @ResponseBody
    @RequestMapping(value = "/api/questiontype_from_excel{subject_Id}",method = RequestMethod.POST)
    public Objects addQuestionTypeByExcel(@PathVariable String subject_Id, HttpServletRequest request) throws ServletException, IOException{
        try {
            //获取上传文件的路径
            String uploadPath = "/base/questiontype/";
            //0=上传成功；1=没有文件上传；2=上传失败；3=上传异常
            List<String> fileList = this.uploadFile(request,uploadPath).getFileList();
            XLSReader xls = null;
            try {
                //读取excel文件数据
                /*@param realPath 文件绝对路径;@param maxCell 最大列数;@param startRow 读取数据起始行数，
                从第几行开始读,从0开始算;@param cellTypes... 数据类型;*/
                xls = new XLSReader(fileList.get(0), 2, 1, XLSReader.CELL_TYPE_STRING, XLSReader.CELL_TYPE_STRING);
            }catch (BizException ex){
                throw ex;
            }
            //判断是否有记录
            Integer dataCount = xls.rowSize();
            if (dataCount <= 0){
                throw new BizException(WebConstants.ERROR, "导入失败:表格未按照下载的模板格式输入或表格数据为空，请核对！");
            }
            String [][] datas = xls.getData();
            User user = getUserInfo();
            String creater = user.getId();
            questionTypeService.addBatchQuestionType(dataCount,datas,subject_Id,creater);
        }catch (Exception e){
            logger.error("题型信息导入异常：",e);
            throw new BizException(WebConstants.ERROR,"导入失败:"+e.getMessage());
        }
        return null;
    }

    /**
     * 题型列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listQuestionTypeUnconditional",method = RequestMethod.POST)
    public List<QuestionType> listQuestionTypeUnconditional(){
        List<QuestionType> questionTypeList=new ArrayList<>();
        try {
            questionTypeList=questionTypeService.listQuestionType();
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("questiontype-listQuestionTypeUnconditional error:" ,e);
        }
        return questionTypeList;
    }

    /**
     * 题型列表
     * @param subjectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listQuestionTypeBySubjectId",method = RequestMethod.POST)
    public List<QuestionType> listQuestionTypeBySubjectId(String subjectId){
        List<QuestionType> questionTypeList=new ArrayList<>();
        try {
            questionTypeList=questionTypeService.listQuestionTypeBySubjectId(subjectId);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("questiontype-listQuestionTypeBySubjectId error:"+e.getMessage() ,e);
        }
        return questionTypeList;
    }

    /**
     * 软删除题型信息
     * @param questionTypeId
     */
    @ResponseBody
    @RequestMapping(value = "/delQuestionTypeById", method = RequestMethod.POST)
    public String delQuestionTypeById(String questionTypeId){
        try{
            User user = getUserInfo();
            questionTypeService.delQuestionTypeById(questionTypeId, user);
        } catch (BizException e){
            throw e;
        } catch (Exception e){
            logger.error("questiontype-delQuestionTypeById error:"+e.getMessage(),e);
        }
        return "Success";
    }
}
