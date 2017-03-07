package com.mingyu.ices.dao;

import com.mingyu.ices.domain.dto.user.QuestionResponse;
import com.mingyu.ices.domain.po.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/1 0001.
 */

@Repository
public interface IQuestionDao {

    /**
     * 分页查询试题列表
     * @param question
     * @param begin
     * @param end
     * @return
     */
    public List<QuestionResponse> pageQuestion(@Param("question") Question question ,@Param("statsPage")Integer begin,@Param("endPage")Integer end);

    /**
     * 统计试题数量
     * @return
     */
    public Integer countQuestion(@Param("question") Question question);

    /**
     * 添加试题数据
     * @param question
     * @return
     */
    public void addQuestion(Question question);

    /**
     * 读取试题内容
     * @param Id
     * @return question
     */
    public QuestionResponse getQuestionDetail(String Id);

    /**
     * 根据大题试题关系表查询试题信息
     * @param sectionId
     * @return
     */
    public List<Question> listQuestionBySectionId(@Param("sectionId") String sectionId);

    /**
     * 修改试题内容
     * @param question
     * @return
     */
    public void updateQuestion(Question question);

    /**
     * 获取试题信息
     * @param id
     * @return
     */
    public Question getQuestionById(@Param("id") String id);

    /**
     * 修改试题审核状态
     * @param question
     * @return
     */
    public void updateQuestionCheckStatus(Question question);

    /**
     * 随机取试题信息
     * @param questionTypeId
     * @param number
     * @return
     */
    public List<Question> listRandomQuestion(@Param("questionTypeId") String questionTypeId,@Param("number") Integer number ,@Param("knowIdList")List<String> knowIdList );

    /**
     * 软删除试题信息
     * @param question
     */
    public void delQuestion(Question question);

    /**
     * 根据试卷ID查询
     * @param paperId
     * @return
     */
    public List<Question> listQuestionByPaperId(String paperId);

    /**
     * 复制试题信息
     * @param question
     * @return
     */
    public void copyQuestionByQuestionId(Question question);

    /**
     * 获取某科目的所有试题信息
     * @param subjectId
     * @return question
     */
    public List<Question> getQuestionsBySubjectId(String subjectId);

    /**
     * 根据题型获取试题信息
     * @param questionTypeId
     * @return question
     */
    public List<Question> getQuestionsByTypeId(String questionTypeId);
}
