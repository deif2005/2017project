package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.QuestionResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by defi on 2016/7/11.
 */

@Repository
public interface IQuestionResourceDao {
    /**
     * 添加试题资源
     * @param questionResource
     * @return
     */
    public void addQuestionResource(QuestionResource questionResource);

    /**
     * 更新试题资源信息
     * @param questionResource
     * @return
     */
    public void updateQuestionResource(QuestionResource questionResource);

    /**
     * 删除试题资源
     * @param questionId 试题Id
     * @return
     */
    public void delQuestionResourceByQuestionId(String questionId);

    /**
     * 软删除试题资源信息
     * @param questionResource 试题Id
     */
    public void delQuestionResource(QuestionResource questionResource);

    /**
     * 复制试题资源信息
     * @param questionResource
     * @return
     */
    public void copyQuestionResourceByQuestionId(QuestionResource questionResource);

    /**
     * 获取试题资源列表
     * @param questionResource
     * @return
     */
    public List<QuestionResource> listQuestionResource(QuestionResource questionResource);
}
