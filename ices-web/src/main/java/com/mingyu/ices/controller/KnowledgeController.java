package com.mingyu.ices.controller;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.KnowledgeResponse;
import com.mingyu.ices.domain.po.Knowledge;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.service.IKnowledgeService;
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
 * Created by Administrator on 2016/7/1 0001.
 */
@Controller
@RequestMapping("/knowledge")
public class KnowledgeController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(KnowledgeController.class);

    @Autowired
    IKnowledgeService knowledgeService;
    @Autowired
    ISubjectService subjectService;

    /**
     * 跳转知识点管理页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list() {
        List<Subject> subList = subjectService.listSubject("1");
        return new ModelAndView("/subject/knowledge-list").addObject("subList",subList);
    }

    /**
     * 分页查询知识点列表
     * @param subjectId
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pageKnowledge",method = RequestMethod.POST)
    public BizDataPage<KnowledgeResponse> pageKnowledge(String subjectId,Integer page){
        BizDataPage<KnowledgeResponse> knowledgeBizDataPage=new BizDataPage<>();
        try {
            knowledgeBizDataPage=knowledgeService.pageKnowledge(subjectId,page);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-pageKnowledge error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return knowledgeBizDataPage;
    }

    /**
     * 新增知识点
     * @param knowledge
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "insertKnowledge",method = RequestMethod.POST)
    public  Object insertKnowledge(Knowledge knowledge){
        if (knowledge==null || StringUtils.isBlank(knowledge.getName())){
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR);
        }
        try {
            knowledgeService.insertKnowledge(knowledge,getUserInfo());
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-insertKnowledge error: ",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    /**
     * 根据id查询知识点信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getKnowledgeById",method = RequestMethod.POST)
    public Knowledge getKnowledgeById(String id){
        Knowledge knowledge=new Knowledge();
        try {
            knowledge = knowledgeService.getKnowledgeById(id);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-getKnowledgeById error:",e);
            throw new BizException(WebConstants.ERROR);
        }
        return knowledge;
    }

    /**
     * 修改知识点信息
     * @param knowledge
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateKnowledge",method = RequestMethod.POST)
    public Object updateKnowledge(Knowledge knowledge){
        try {
            knowledgeService.updateKnowledge(knowledge,getUserInfo());
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-updateKnowledge error: ",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    /**
     * 删除知识点信息
     * @param id
     */
    @ResponseBody
    @RequestMapping(value = "deleteKnowledge",method = RequestMethod.POST)
    public Object deleteKnowledge(String id){
        try {
            knowledgeService.deleteKnowledge(id,getUserInfo());
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-deleteKnowledge",e);
            throw new BizException(WebConstants.ERROR);
        }
        return 0;
    }

    @ResponseBody
    @RequestMapping(value = "/listKnowledgeBySubjectId",method =RequestMethod.POST)
    public List<Knowledge> listKnowledgeBySubjectId(String subjectId){
        List<Knowledge> knowledgeList=new ArrayList<>();
        try {
            knowledgeList =  knowledgeService.listKnowledgeBySubjectId(subjectId);
        }catch (BizException biz){
            throw biz;
        }catch (Exception e){
            logger.error("Knowledge-listKnowledgeBySubjectId",e);
            throw new BizException(WebConstants.ERROR);
        }
        return knowledgeList;
    }

}
