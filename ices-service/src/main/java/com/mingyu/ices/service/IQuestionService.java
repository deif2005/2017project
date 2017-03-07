package com.mingyu.ices.service;

import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.dto.user.QuestionResponse;
import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.QuestionContent;
import com.mingyu.ices.domain.po.QuestionKnow;
import com.mingyu.ices.domain.po.User;

import java.util.List;

/**
 * IQuestionsService
 * 试题相关service
 * @author yuhao
 * @date 2016/6/30
 */
public interface IQuestionService {
    /**
     * 分页查询试卷列表
     * @param page
     * @return
     */
    public BizDataPage<QuestionResponse> pageQuestion(Question question, Integer page);

    /**
     * 读取试题信息
     * @param Id
     * @return QuestionResponse
     */
    public QuestionResponse getQuestionDetail(String Id) throws Exception;

    /**
     * 解析question.xml文件内容
     * @param path xml文件路径
     * @return
     * @throws Exception
     */
    public QuestionContent getQuestionXmlInfo(String path) throws Exception;

    /**
     * 获取题目对应的知识点信息
     * @param questionId 题目id
     * @return QuestionKnow
     */
    public List<QuestionKnow> getQuestionKnow(String questionId);

    /**
     * 添加试题信息
     * @param question
     * @return
     */
    public void addQuestion(Question question);

    /**
     * 添加试题内容
     * @param questionContent
     * @return
     */
    public void addQuestionContent(QuestionContent questionContent,String xmlFilePath);

    /**
     * 添加试题详细信息
     * @param questionResponse
     * @return
     */
    public void addQuestionDetail(QuestionResponse questionResponse);

    /**
     * 保存题目信息
     * @param questionResponse
     * @return
     */
    public void updateQuestionDetail(QuestionResponse questionResponse) throws Exception;

    /**
     * 保存题目信息时更新知识点信息
     * @param questionId,QuestionKnow
     * @return
     */
    public void updateQuestionKnow(String questionId, List<QuestionKnow> questionKnowList, String userId);

    /**
     * 更新题目xml内容
     * @param questionContent
     * @return
     */
    public void updateQuestionContent(QuestionContent questionContent,String xmlFilePath) throws Exception;

    /**
     * 更新题目审核状态
     * @param id
     * @param checkStatus
     * @return
     */
    public void updateQuestionCheckStatus(String id,String checkStatus,User user) ;

    /**
     * 删除试题信息
     * @param question
     * @return
     */
    public void delQuestion(Question question);

    /**
     * 复制试题内容
     * @param question
     * @return
     */
    public void copyQuestion(Question question) throws Exception;

}
