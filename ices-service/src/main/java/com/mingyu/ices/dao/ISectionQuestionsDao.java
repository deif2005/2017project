package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.SectionQuestions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public interface ISectionQuestionsDao {

    /**
     * 批量添加大题试题关系
     * @param sectionQuestionsList
     */
    public void batchInsertSectionQuestions(@Param("sectionQuestionsList")List<SectionQuestions> sectionQuestionsList);

    /**
     * 修改大题试题关系
     * @param sectionQuestions
     */
    public void updateSectionQuestion(SectionQuestions sectionQuestions);

    /**
     * 批量修改大题试题关系
     * @param sectionQuestionsList
     */
    public void batchUpdateSectionQuestion(@Param("sectionQuestionsList") List<SectionQuestions> sectionQuestionsList);

    /**
     * 批量删除大题试题关系
     * @param sectionQuestionsList
     */
    public void batchDeleteSectionQuestion(@Param("sectionQuestionsList") List<SectionQuestions> sectionQuestionsList);

    /**
     * 根据试卷ID查询
     * @param paperId
     * @return
     */
    public List<SectionQuestions> listSectionQuestionByPaperId(String paperId);

    /**
     * 根据试题ID统计
     * @param questionId
     * @return
     */
    Integer countByQuestionId(@Param("questionId") String questionId);

    /**
     * 根据试卷ID删除
     * @param paperId
     */
    void deleteByPaperId(@Param("paperId") String paperId);
}
