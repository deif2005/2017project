package com.mingyu.ices.service;

import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.po.QuestionType;
import com.mingyu.ices.domain.po.SysQuestionType;
import com.mingyu.ices.domain.po.User;

import java.util.List;

/**
 * Created by defi on 2016/7/1.
 */
public interface IQuestionTypeService {

    /**
     * 查询题型信息分页数据
     * @param page
     * @param subjectId
     * @return bizDataPage
     */
    public BizDataPage<QuestionType> listQuestionType(String subjectId,Integer page);

    /**
     * 新增题型信息
     * @param questionType
     * @return
     */
    public void addQuestionType(QuestionType questionType);

    /**
     * 更新题型信息
     * @param questionType
     * @return
     */
    public void updateQuestionType(QuestionType questionType);

    /**
     * 查询题型信息
     * @param Id
     * @return
     */
    public QuestionType getQuestionTypeById(String Id);

    /**
     * 批量插入题型信息
     * @param rows  datas, Subject_Id
     * @return 插入结果信息
     */
    public String addBatchQuestionType(int rows, String[][] datas, String subject_Id, String userId);

    /**
     * 获取题型信息集合
     * @return
     */
    public List<QuestionType> listQuestionType();

    /**
     * 获取题型信息集合
     * @param subjectId
     * @return
     */
    public List<QuestionType> listQuestionTypeBySubjectId(String subjectId);

    /**
     * 软删除题型信息
     * @param questionTypeId, user
     */
    public void delQuestionTypeById(String questionTypeId, User user);
}
