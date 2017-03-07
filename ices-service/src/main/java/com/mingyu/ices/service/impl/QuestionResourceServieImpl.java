package com.mingyu.ices.service.impl;

import com.mingyu.ices.dao.IQuestionResourceDao;
import com.mingyu.ices.domain.po.QuestionResource;
import com.mingyu.ices.service.IQuestionResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by defi on 2016/7/11.
 */

@Service
public class QuestionResourceServieImpl implements IQuestionResourceService {

    @Autowired
    IQuestionResourceDao questionResourceDao;

    /**
     * 删除题资源信息
     * @param questionId
     * @return
     */
    public void delQuestionResourceByQuestionId(String questionId){
        questionResourceDao.delQuestionResourceByQuestionId(questionId);

    }

    /**
     * 新增题资源信息
     * @param questionResource
     * @return
     */
    public void addQuestionResource(QuestionResource questionResource,String userId){
        questionResource.setId(UUID.randomUUID().toString());
        questionResource.setCreater(userId);
        questionResourceDao.addQuestionResource(questionResource);
    }

    /**
     * 修改试题资源信息
     * @param questionResource
     */
    public void updateQuestionResource(QuestionResource questionResource){
        questionResourceDao.updateQuestionResource(questionResource);
    }

    /**
     * 软删除试题资源信息
     * @param
     * @return
     */
    public void delQuestionResource(QuestionResource questionResource){
        questionResourceDao.delQuestionResource(questionResource);
    }

}
