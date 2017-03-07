package com.mingyu.ices.service;

import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.QuestionResource;

/**
 * Created by defi on 2016/7/11.
 */
public interface IQuestionResourceService {
    /**
     * 删除题资源信息
     * @param questionId
     * @return
     */
    public void delQuestionResourceByQuestionId(String questionId);

    /**
     * 新增题资源信息
     * @param questionResource
     * @return
     */
    public void addQuestionResource(QuestionResource questionResource, String userId);

    /**
     * 修改试题资源信息
     * @param questionResource
     */
    public void updateQuestionResource(QuestionResource questionResource);

    /**
     * 软删除试题资源信息
     * @param
     * @return
     */
    public void delQuestionResource(QuestionResource questionResource);
}