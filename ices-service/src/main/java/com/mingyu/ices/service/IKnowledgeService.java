package com.mingyu.ices.service;

import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.dto.user.KnowledgeResponse;
import com.mingyu.ices.domain.po.Knowledge;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.domain.po.User;

import java.util.List;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public interface IKnowledgeService {

    /**
     * 分页查询知识点列表
     * @param page
     * @param subjectId
     * @return
     */
    public BizDataPage<KnowledgeResponse> pageKnowledge(String subjectId,Integer page);

    /**
     * 查询知识点列表
     * @return
     */
    public List<Knowledge> listKnowledge();

    /**
     * 根据id查询知识点信息
     * @param id
     * @return
     */
    public Knowledge getKnowledgeById(String id);

    /**
     * 添加知识点信息
     * @param knowledge
     * @param user
     */
    public void insertKnowledge(Knowledge knowledge,User user);

    /**
     * 修改知识点信息
     * @param user
     * @param knowledge
     */
    public void updateKnowledge(Knowledge knowledge,User user);

    /**
     * 删除知识点信息（软删除）
     * @param id
     *  @param user
     */
    public void deleteKnowledge(String id,User user);

    /**
     * 根据科目ID查询知识点
     * @param subjectId
     * @return
     */
    public List<Knowledge> listKnowledgeBySubjectId(String subjectId);
}
