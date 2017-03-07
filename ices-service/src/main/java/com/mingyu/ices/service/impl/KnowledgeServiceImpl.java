package com.mingyu.ices.service.impl;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.IKnowledgeDao;
import com.mingyu.ices.dao.IQuestionKnowDao;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.KnowledgeResponse;
import com.mingyu.ices.domain.po.Knowledge;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IKnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
@Service
public class KnowledgeServiceImpl implements IKnowledgeService {
    @Autowired
    IKnowledgeDao knowledgeDao;
    @Autowired
    IQuestionKnowDao questionKnowDao;

    @Override
    public BizDataPage<KnowledgeResponse> pageKnowledge(String subjectId,Integer page) {
        BizDataPage<KnowledgeResponse> bizDataPage=new BizDataPage<>();
        bizDataPage.setPage(page);
        List<KnowledgeResponse> knowledgeList  =knowledgeDao.pageKnowledge(subjectId,"1",bizDataPage.getOffset(),bizDataPage.getPagesize());
        Integer records= knowledgeDao.countKnowledge(subjectId,1);
        bizDataPage.setRows(knowledgeList);
        bizDataPage.setRecords(records);
        return bizDataPage;
    }

    @Override
    public List<Knowledge> listKnowledge() {
        return knowledgeDao.listKnowledge("1");
    }

    @Override
    public Knowledge getKnowledgeById(String id) {
        return knowledgeDao.getKnowledgeById(id);
    }

    @Override
    public void insertKnowledge(Knowledge knowledge,User user) {

        Integer number=knowledgeDao.countByName(knowledge);
        //校验名称是否重复
        if (number>0){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        knowledge.setId(UUID.randomUUID().toString());
        knowledge.setCreater(user.getId());
        knowledge.setStatus("1");
        knowledgeDao.insertKnowledge(knowledge);
    }

    @Override
    public void updateKnowledge(Knowledge knowledge,User user) {

        //校验名称是否重复
        String knowledgeName=knowledgeDao.getKnowledgeById(knowledge.getId()).getName();
        if (!(knowledgeName.equals(knowledge.getName()))){
        Integer number=knowledgeDao.countByName(knowledge);
        if (number>0){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        }
        knowledge.setModifier(user.getId());
        knowledgeDao.updateKnowledge(knowledge);
    }

    @Override
    public void deleteKnowledge(String id,User user) {
        //统计知识点下的试题
        Integer countNumber=questionKnowDao.countByKnowId(id);
        if(countNumber>0){
            throw new BizException(WebConstants.EXISTS_RELATED_DATA);
        }
        Knowledge knowledge=new Knowledge();
        knowledge.setId(id);
        knowledge.setStatus("2");
        knowledge.setModifier(user.getId());
        knowledgeDao.updateKnowledge(knowledge);
    }

    @Override
    public List<Knowledge> listKnowledgeBySubjectId(String subjectId) {
        return knowledgeDao.listKnowledgeBySubjectId("1",subjectId);
    }


}
