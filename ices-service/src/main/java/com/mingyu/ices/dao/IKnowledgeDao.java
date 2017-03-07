package com.mingyu.ices.dao;

import com.mingyu.ices.domain.dto.user.KnowledgeResponse;
import com.mingyu.ices.domain.po.Knowledge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public interface IKnowledgeDao {

    /**
     * 分页查询知识点列表。
     * @param status
     * @param subjectId
     * @param begin
     * @param end
     */
    public List<KnowledgeResponse> pageKnowledge(@Param("subjectId")String subjectId,
                                                 @Param("status")String status ,
                                                 @Param("statsPage")Integer begin,
                                                 @Param("endPage")Integer end);

    /**
     * 查询知识点列表
     * @param status
     * @return
     */
    public List<Knowledge> listKnowledge(@Param("status")String status);

    /**
     * 根据id查询知识点信息
     * @param id
     * @return
     */
    public Knowledge getKnowledgeById(@Param("id") String id);

    /**
     * 统计知识点数量
     * @param subjectId
     * @return
     */
    public Integer countKnowledge(@Param("subjectId")String subjectId,@Param("status") Integer status);

    /**
     * 添加知识点信息
     * @param knowledge
     */
    public void insertKnowledge(Knowledge knowledge);

    /**
     * 修改知识点信息
     * @param knowledge
     */
    public void updateKnowledge(Knowledge knowledge);

    /**
     * 根据试卷知识点关系表查询知识点
     * @param id
     * @return
     */
    public List<Knowledge> listKnowLedgeByPaperKnow(@Param("id") String id);

    /**
     * 根据科目ID查询知识点
     * @param status
     * @param subjectId
     * @return
     */
    public List<Knowledge> listKnowledgeBySubjectId(@Param("status") String status ,@Param("subjectId") String subjectId);

    /**
     * 删除知识点信息
     * @param knowledge
     */
    public void delKnowledge(Knowledge knowledge);

    /**
     * 根据名ID或称统计
     * @param knowledge
     * @return
     */
    public Integer countByName(Knowledge knowledge);
}
