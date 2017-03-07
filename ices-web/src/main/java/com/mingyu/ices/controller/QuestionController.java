package com.mingyu.ices.controller;

import com.alibaba.fastjson.JSON;
import com.mingyu.ices.config.SystemConfig;
import com.mingyu.ices.constant.QuestionHardEnum;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.common.StringWrapper;
import com.mingyu.ices.domain.common.UploadFileResponse;
import com.mingyu.ices.domain.dto.user.QuestionResponse;
import com.mingyu.ices.domain.po.*;
import com.mingyu.ices.service.*;
import com.mingyu.ices.util.FileUtil;
import com.mingyu.ices.util.ZipUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * QuestionContrller
 * 试题相关controller
 * @author yuhao
 * @date 2016/6/30
 */
@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController{

    Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IKnowledgeService knowledgeService;
    @Autowired
    private IQuestionResourceService questionResourceService;
    @Autowired
    ISubjectService subjectService;
    @Autowired
    IQuestionTypeService questionTypeService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        List<Subject> subList = subjectService.listSubject("1");
        return new ModelAndView("/question/question-list").addObject("subList",subList);
    }

    /**
     * 跳转添加试题页面
     */
    @RequestMapping(value = "/addQuestion", method = RequestMethod.GET)
    public ModelAndView loginView(String subjectId,String questionTypeId ) {
        ModelAndView view = new ModelAndView("/question/question-add");
        List<Subject> subjectList = subjectService.listSubject("1");
        List<QuestionHardEnum> questionHard = new ArrayList<QuestionHardEnum>();
        EnumSet<QuestionHardEnum> currEnumSet = EnumSet.allOf(QuestionHardEnum.class);
        for(QuestionHardEnum obj:currEnumSet){
            questionHard.add(obj);
        }
        view.addObject("subjectList",subjectList);
        view.addObject("questionHard",questionHard);
        view.addObject("questionTypeId",questionTypeId);
        view.addObject("subjectId",subjectId);
        return view;
    }

    /**
     * 跳转添加试题页面
     */
    @RequestMapping(value = "/editQuestion", method = RequestMethod.GET)
    public ModelAndView editQuestionView(String id) {
        ModelAndView view = new ModelAndView("/question/question-edit");
        try {
            //查询题目信息
            QuestionResponse questionResponse = questionService.getQuestionDetail(id);
            //查询所有科目
            List<Subject> subjectList = subjectService.listSubject("1");
            //题目难度选项的数据
            List<QuestionHardEnum> questionHard = new ArrayList<QuestionHardEnum>();
            EnumSet<QuestionHardEnum> currEnumSet = EnumSet.allOf(QuestionHardEnum.class);
            for(QuestionHardEnum obj:currEnumSet){
                questionHard.add(obj);
            }
            //根据科目查询知识点信息
            List<Knowledge> knowledgeList  = knowledgeService.listKnowledgeBySubjectId(questionResponse.getSubjectId());
            //根据科目查询题型信息
            List<QuestionType> questionTypeList = questionTypeService.listQuestionTypeBySubjectId(questionResponse.getSubjectId());
            QuestionContent content = questionResponse.getQuestionContent();
            content.setQuestionContent("");
            view.addObject("questionResponse",questionResponse);
            view.addObject("contentJson", StringEscapeUtils.escapeHtml(JSON.toJSONString(content)));
            view.addObject("subjectList",subjectList);
            view.addObject("questionHard",questionHard);
            view.addObject("knowledgeList",knowledgeList);
            view.addObject("questionTypeList",questionTypeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 分页查询题列表
     * @param page
     * @param question
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pageQuestion",method = RequestMethod.POST)
    public BizDataPage<QuestionResponse> pageQuestion(Question question,Integer page){
        BizDataPage<QuestionResponse> paperBizDataPage=new BizDataPage<>();
        try {
            paperBizDataPage=questionService.pageQuestion(question,page);
            //paperBizDataPage.setPage(page);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("questions-pageQuestions error:",e);
        }
        return paperBizDataPage;
    }
    /**
     * 上传试题资源
     * @param request
     * @param subjectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadResource", method = RequestMethod.POST)
    public StringWrapper uploadResource(HttpServletRequest request,String subjectId) {
        String filePath = null;
        try {
            //读取zk中配置的试题资源上传路径
            String uploadPath = SystemConfig.getQuestionResourceUploadPath();
            //上传压缩文件
            filePath = super.uploadFile(request,uploadPath + subjectId +"/").getFirstFile();
            //读取zk中配置的文件规则
            String fileRule = SystemConfig.getQuestionResourceUploadRule();
            //解压缩文件并检查文件格式
            ZipUtil.unZip(filePath, null, fileRule);
            //删除压缩包文件
            FileUtil.deleteFile(filePath);
        } catch (BizException be){
            //业务异常，删除上传的压缩包及文件夹
            if(StringUtils.isNotBlank(filePath)){
                FileUtil.deleteFile(filePath);
                FileUtil.deleteDirectory(filePath.substring(0, filePath.indexOf(".")));
            }
            throw be;
        } catch(Exception e){
            logger.error("QuestionContrller-uploadResource error：",e);
            throw new BizException(WebConstants.ERROR,"上传试题资源");
        }
        return new StringWrapper("success");
    }

    /**
     * 上传图片资源文件
     * @param request
     * @param fileType 文件类型（扩展名）
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadPicture", method = RequestMethod.POST)
    public List<String> uploadMediaSource(HttpServletRequest request,String fileType) {
        List<String> resultList = new ArrayList<>();
        String showPath = "";
        String fileName = "";
        try {
            //读取zk中配置的文件上传目录
            String relPath = SystemConfig.getQuestionUploadPath(fileType);
            //读取zk中文件展示地址
            String baseShowPath = SystemConfig.getStaticShowPath();
            //上传压缩文件
            UploadFileResponse uploadFileResponse = super.uploadFile(request,relPath);
            fileName = uploadFileResponse.getFirstFile();
            String OriginalFilename = uploadFileResponse.getFirstFileName();
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            //读取zk中配置的文件展示路径
            showPath = baseShowPath + relPath + fileName;
            resultList.add(showPath);
            resultList.add(OriginalFilename);
        } catch (BizException biz) {
            throw biz;
        } catch (Exception e) {
            logger.error("QuestionController-uploadPicture:", e);
            throw new BizException(WebConstants.ERROR, "上传图片失败");
        }
        return resultList;
    }

    /**
     * 添加试题信息
     * @param questionResponse
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addQuestionDetail", method = RequestMethod.POST)
    public StringWrapper addQuestionDetail(@RequestBody QuestionResponse questionResponse){
        User user = getUserInfo();
        String userId = user.getId();
        questionResponse.setCreater(userId);
        try {
            questionService.addQuestionDetail(questionResponse);
        }catch (Exception e){
            logger.error("QuestionController-addQuestionDetail error:",e);
            throw new BizException(WebConstants.ERROR,"添加题目信息时发生异常");
        }
        return new StringWrapper("保存成功");
    }

    /**
     * 获取试题所有信息
     * @param id  题目ID号
     * @return QuestionResponse
     */
    @ResponseBody
    @RequestMapping(value = "/getQuestionDetail",method = RequestMethod.POST)
    public QuestionResponse getQuestionDetailById(String id){
        QuestionResponse questionResponse = null;
        try {
            questionResponse = questionService.getQuestionDetail(id);

        }catch (Exception e){
            logger.error("QuestionController-getQuestionDetail error:",e);
            throw new BizException(WebConstants.ERROR,"获取试题信息时发生错误");
        }
        return questionResponse;
    }

    /**
     * 获取试题知识点信息
     * @param subjectId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listQuestionKnow", method = RequestMethod.POST)
    public List<Knowledge> listQuestionKnow(String subjectId){
        List<Knowledge> knowledgeList = null;
        try {
            //subjectId ="2e53253a-0d00-456c-8ec9-1d545e9d3108";
            knowledgeList  = knowledgeService.listKnowledgeBySubjectId(subjectId);
        }catch (Exception e){
            logger.error("QuestionController-listQuestionKnow error:",e);
            throw new BizException(WebConstants.ERROR,"获取科目知识点信息时发生错误");
        }
        return knowledgeList;
    }

    @ResponseBody
    @RequestMapping(value = "/testPath",method = RequestMethod.POST)
    public String getPath(HttpServletRequest request){
        String path = request.getServletContext().toString();
                //request.getServletContext().getRealPath("/");
        return path;
    }

    /**
     * 修改试题内容
     * @param questionResponse
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateQuestionDetail", method = RequestMethod.POST)
    public StringWrapper updateQuestionDetail(@RequestBody QuestionResponse questionResponse){
        User user = getUserInfo();
        String userId = user.getId();
        questionResponse.setModifier(userId);
        try {
            questionService.updateQuestionDetail(questionResponse);
        }catch (Exception e){
            logger.error("QuestionContrller-updateQuestionDetail error:",e);
            throw new BizException(WebConstants.ERROR,"修改试题内容时发生错误");
        }
        return new StringWrapper("更新成功");
    }

    /**
     * 删除题目信息
     * @param question
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delQuestionDetailByQuestionId", method = RequestMethod.POST)
    public StringWrapper delQuestionDetail(Question question){
        User user = getUserInfo();
        question.setModifier(user.getId());
        try {
            questionService.delQuestion(question);
        }catch (BizException biz){
            logger.error("QuestionContrller-delQuestionDetail error:",biz);
            throw biz;
        }
        catch (Exception e){
            logger.error("QuestionContrller-delQuestionDetail error:",e);
            throw new BizException(WebConstants.ERROR,"删除试题失败");
        }
        return new StringWrapper("删除成功");
    }

    /**
     * 更新题目审核状态
     * @param id
     * @param type
     */
    @ResponseBody
    @RequestMapping(value = "/updateQuestionCheckStatus",method = RequestMethod.POST)
    public StringWrapper updateQuestionCheckStatus(String id, String type){
        try {
            User user = getUserInfo();
            String checkStatus = "";
            if("pass".equals(type)) {
                checkStatus = "2";
            }else if("cancel".equals(type)) {
                checkStatus = "1";
            }
            questionService.updateQuestionCheckStatus(id, checkStatus,user);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("QuestionContrller-updateQuestionCheckStatus error:",e);
            throw new BizException(WebConstants.ERROR,"更新试题审核状态");
        }
        return new StringWrapper("更新成功");
    }

    /**
     * 复制试题信息
     *
     * @param question
     * @return string
     */
    @ResponseBody
    @RequestMapping(value = "/copyQuestionDetail",method = RequestMethod.POST)
    public String coypQuestionDetail(Question question){
        User user = getUserInfo();//new User();
        //user.setId("ddd");
        try {
            //question.setId("2222");
            //question.setSubjectId("9f61a663-fed7-4ae7-a0fa-f52baf7342a8");
            question.setCreater(user.getId());
            questionService.copyQuestion(question);
        }catch(Exception e){
            logger.error("QuestionContrller-copQuestionDetail error:",e);
            throw new BizException(WebConstants.ERROR,"复制试题发生异常");
        }
        return "copysuccess";
    }

}
