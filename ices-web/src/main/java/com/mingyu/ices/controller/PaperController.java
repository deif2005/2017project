package com.mingyu.ices.controller;

import com.alibaba.fastjson.JSONObject;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.PaperDetailResponse;
import com.mingyu.ices.domain.dto.user.QuestionRandomResponse;
import com.mingyu.ices.domain.po.Paper;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IPaperService;
import com.mingyu.ices.service.ISubjectService;
import com.mingyu.ices.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.applet.Main;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/1 0001.
 */

@Controller
@RequestMapping("/paper")
public class PaperController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(PaperController.class);

    @Autowired
    IPaperService paperService;
    @Autowired
    ISubjectService subjectService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        return new ModelAndView("/paper/paper-list");
    }

    @RequestMapping(value = "/addPaper", method = RequestMethod.GET)
    public ModelAndView paperAdd() {
        return new ModelAndView("/paper/paper-add");
    }

    @RequestMapping(value = "/updatePaper", method = RequestMethod.GET)
    public ModelAndView paperUpdate() {
        List<Subject> subList = subjectService.listSubject("1");
        return new ModelAndView("/paper/paper-update").addObject("subjectList",subList);
    }
    @RequestMapping(value = "/previewPaper", method = RequestMethod.GET)
    public ModelAndView previewPaper() {
        return new ModelAndView("/paper/paper-preview");
    }
    /**
     * 分页查询试卷列表
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pagePaper",method = RequestMethod.POST)
    public BizDataPage<Paper> pagePaper(Integer page,Paper paper){
        BizDataPage<Paper> paperBizDataPage;
        try {
            paperBizDataPage=paperService.pagePaper(page,paper);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-pagePaper error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return paperBizDataPage;
    }

    /**
     * 新增试卷信息
     * @param paper
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/insertPaper" ,method = RequestMethod.POST)
    public Object insertPaper(Paper paper,String  knowIdStr){
        try {
            paperService.insertPaper(paper, getUserInfo(), knowIdStr);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-insertPaper error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    @ResponseBody
    @RequestMapping(value = "/updatePaper" , method = RequestMethod.POST)
    public Object updatePaper(Paper paper, String knowIdStr){

        try {//选择题:88:85:767677:1,2,3;填空题:44:60:嗯嗯嗯:4,5,6
//            Paper paper=new Paper("1","这是一场严肃的考试", "11", "1", "60","100", "120",
//                    "e59e0f40-108c-41cb-a017-d3f0501ab417:选择题:88:85:好题目:77,78,79;b67612fa-ddaf-47cc-9578-e40f241d229a:填空题:44:60:真的嗯嗯:80,81,82", "34","22", "777", null,null,"eeeee",null);
//            User user=new User();
//            String knowIdStr="1,7;2,8";
//            String questionsJson="1:1:7;2:2:8;3:3:8";
//            user.setId("1");
            paperService.updatePaper(paper,getUserInfo(),knowIdStr);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-updatePaper error:" , e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    @ResponseBody
    @RequestMapping(value = "/updatePaperCheckStatus" , method = RequestMethod.POST)
    public Object updatePaperCheckStatus( String id,String checkStatus){
        try {
            User user = getUserInfo();
            paperService.updatePaperCheckStatus(id,checkStatus,user);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-updatePaperCheckStatus error:" , e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }
    /**
     * 删除试卷信息
     * @param status
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value ="/deletePaperById" ,method=RequestMethod.POST)
    public Object deletePaperById(String status,String id){
        try {
            paperService.deletePaperById(status,id);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-deletePaperById error:",e);
            throw new BizException(WebConstants.ERROR);
        }
       return 0;
    }

    /**
     * 预览试卷信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/previewPaper",method = RequestMethod.POST)
    public PaperDetailResponse previewPaper(String id){
        PaperDetailResponse paperDetailResponse=new PaperDetailResponse();
        try {
            paperDetailResponse =paperService.previewPaper(id);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-getPaperById error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return paperDetailResponse;
    }

    /**
     * 根据试卷ID查询试卷信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getPaperById",method = RequestMethod.POST)
    public PaperDetailResponse getPaperById(String id){
        PaperDetailResponse paperDetailResponse=new PaperDetailResponse();
        try {
            paperDetailResponse =paperService.getPaperById(id);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-getPaperById error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return paperDetailResponse;
    }

    /**
     * 自动组卷
     * @param questionTypeId
     * @param number
     * @param knowIdList
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/listRandomQuestion",method = RequestMethod.POST)
    public Map<String,List<QuestionRandomResponse>> listRandomQuestion(String questionTypeId, String number,String knowIdList){

//        String questionTypeId="013e6aa1-7df6-450e-830b-2fcacaad93cf;096944a8-9851-4e0c-b23b-30ddb3185339";
//        Integer number=2;
//        String knowIdList="24c96c12-0f23-4565-89b3-935891895071,2dc91e23-9214-4149-9d43-c11e5c9839c4;3800456e-d3f7-42ff-9223-d0d87a067b50";

        Map<String,List<QuestionRandomResponse>> map=new HashMap<>();
        try {
            map =  paperService.listRandomQuestion(questionTypeId, number, knowIdList);
        }catch (BizException biz){
            throw  biz;
        }catch (Exception e){
            logger.error("paper-listRandomQuestion error:" , e);
            throw new BizException(WebConstants.ERROR);
        }
        return map;
    }

    /**
     * 复制试卷
     * @param paperId
     * @param paperName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copyPaper",method = RequestMethod.POST)
    public Object copyPaper(String paperId,String paperName){
        try {
            paperService.copyPaper(paperId,paperName);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("paper-copyPaper error：" ,e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    /**
     * 导出试卷
     * @param paperName
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportPaper", method = RequestMethod.POST)
    public Object exportPaper(String paperName,String ids) {
        JSONObject result = new JSONObject();
        try {
            if (StringUtils.isBlank(paperName) || StringUtils.isBlank(ids)) {
                throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
            }
            //将文件输出到页面下载
            paperName = paperName + "-" + DateUtil.getDateToString(new Date(), "yyyyMMddkkmmss");
            //生成试卷包相关文件并打包成eps格式zip压缩包输出
            String paperPkgPath = paperService.exportPaper(paperName, ids);
            result.put("paperPkgPath", paperPkgPath);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            logger.error("导出试卷异常：", e);
            throw new BizException(WebConstants.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public ModelAndView export() {
        List<Subject> subList = subjectService.listSubject("1");
        return new ModelAndView("/paper/paper-export").addObject("subList",subList);
    }
}
