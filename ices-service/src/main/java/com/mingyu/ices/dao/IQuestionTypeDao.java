package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.QuestionType;
import com.mingyu.ices.domain.po.SysQuestionType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by defi on 2016/7/1.
 * 题目类型dao
 */

@Repository
public interface IQuestionTypeDao {

    /**
     * 分页查询题型信息
     * @param subjectId
     * @param startpage
     * @param rows
     * @return List<QuestionType>
     */
    public List<QuestionType> pageQuestionType(@Param("subjectId") String subjectId,
                                                @Param("status") Integer status,
                                                @Param("startpage") Integer startpage,
                                                @Param("rows") Integer rows);

    /**
     * 新增题型信息
     * @param questionType
     */
    public void insertQuestionType(QuestionType questionType);

    /**
     * 修改题型信息
     * @param questionType
     */
    public void updateQuestionTypeById(QuestionType questionType);

    /**
     * 统计题型信息数量
     * @param
     * @return Integer;
     * @param subjectId
     */
    public Integer countQuestionType(@Param("subjectId")String subjectId,@Param("status")Integer status);

    /**
     * 根据id查询题型信息
     * @param  Id
     * @return questiontype
     */
    public QuestionType getQuestionTypeById(String Id);

    /**
     * 获取题型信息集合
     * @param status
     * @return
     */
    public List<QuestionType> listQuestionType(@Param("status") String status);
    /**
     * 获取题型信息集合
     * @param subjectId
     * @param status
     * @return
     */
    public List<QuestionType> listQuestionTypeBySubjectId(@Param("subjectId") String subjectId,@Param("status") String status);

    /**
     * 获取题型信息集合
     * @return
     */
    public List<SysQuestionType> listSysQuestionType();

    /**
     * 软删除题型信息
     *
     */
    public void deleteQuestionTypeById(QuestionType questionType);

}
