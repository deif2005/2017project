package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.QuestionKnow;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by defi on 2016/7/6.
 */

@Repository
public interface IQuestionKnowDao {
    /**
     * 获取题目对应的知识点
     * @param questionId
     * @return QuestionKnow
     */
    public List<QuestionKnow> getQuestionKnow(String questionId);

    /**
     * 信增题目知识点对应关系
     * @param questionKnow
     * @return
     */
    public void addQuestionKnow(QuestionKnow questionKnow);

    /**
     * 删除题目对应的知识点
     * @param questionId
     * @return
     */
    public void delQuestionKnow(String questionId);

    /**
     * 复制题知识点信息
     * @param questionKnow
     * @return
     */
    public void copyQuestionKnowByQuestionId(QuestionKnow questionKnow);


    /**
     * 根据试卷ID查询
     * @param paperId
     * @return
     */
    public List<QuestionKnow> listQuestionKnwByPaperId(String paperId);

    /**
     * 批量添加
     * @param questionKnowList
     */
    public void batchAddQuestionKnow(List<QuestionKnow> questionKnowList);

    /**
     * 根据knowId统计
     * @param knowId
     * @return
     */
    Integer countByKnowId(@Param("knowId") String knowId);

    void deleteByQuestionId(@Param("questionId") String questionId);

}
