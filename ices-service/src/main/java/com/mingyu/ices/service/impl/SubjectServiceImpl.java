package com.mingyu.ices.service.impl;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.IKnowledgeDao;
import com.mingyu.ices.dao.IQuestionDao;
import com.mingyu.ices.dao.IQuestionTypeDao;
import com.mingyu.ices.dao.ISubjectDao;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.po.*;
import com.mingyu.ices.service.ISubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
@Service
public class SubjectServiceImpl implements ISubjectService {

    @Autowired
    ISubjectDao subjectDao;

    @Autowired
    IQuestionTypeDao questionTypeDao;

    @Autowired
    IQuestionDao questionDao;

    @Autowired
    IKnowledgeDao knowledgeDao;

    @Override
    public BizDataPage<Subject> pageSubject(Integer page) {
        BizDataPage<Subject> bizDataPage=new BizDataPage<>();
        bizDataPage.setPage(page);
        List<Subject> subjectList=subjectDao.pageSubject("1",bizDataPage.getOffset(),bizDataPage.getPagesize());//查询科目信息
        Integer records=subjectDao.countSubject(1);//统计科目数量
        bizDataPage.setRows(subjectList);
        bizDataPage.setRecords(records);
        return bizDataPage;
    }

    @Override
    public List<Subject> listSubject(String status) {
        return subjectDao.listSubject(status);
    }

    @Override
    public Subject getSubjectById(String id) {
        return subjectDao.getSubjectById(id,"1");
    }

    @Override
    public void insertSubject(Subject subject,User user) {

        Integer number=subjectDao.countByName(subject);
        //校验名称是否重复
        if (number>0){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        String subjectId= UUID.randomUUID().toString();
        subject.setId(subjectId);
        subject.setCreater(user.getId());
        subject.setStatus("1");
        addBatchSubjectQuestionType(subjectId,user.getId());
        subjectDao.insertSubject(subject);
    }

    public  void addBatchSubjectQuestionType(String subjectId,String userId){
        List<SysQuestionType> sysQuestionTypeList=questionTypeDao.listSysQuestionType();
        if (sysQuestionTypeList.size()>0){
            for (int i =0;i<sysQuestionTypeList.size();i++ ){
                SysQuestionType sysQuestionType = sysQuestionTypeList.get(i);
                QuestionType questionType = new QuestionType();
                questionType.setId(UUID.randomUUID().toString());
                questionType.setSubjectId(subjectId);
                questionType.setName(sysQuestionType.getName());
                questionType.setCode(sysQuestionType.getCode());
                questionType.setType(sysQuestionType.getType());
                questionType.setCreater(userId);
                questionType.setModifier(userId);
                questionType.setStatus("1");
                questionType.setRemark("系统添加的基础题型");
                questionTypeDao.insertQuestionType(questionType);
            }
        }
    }

    @Override
    public void updateSubjectById(Subject subject,User user) {

        String subjectName=subjectDao.getSubjectById(subject.getId(),"1").getName();
        //校验名称是否重复
        if (!(subjectName.equals(subject.getName()))){
            Integer number=subjectDao.countByName(subject);
            if (number>0){
                throw new BizException(WebConstants.NAME_REPETITION);
            }
        }
        subject.setModifier( user.getId());
        subjectDao.updateSubjectById(subject);
    }

    /**
     * 删除科目信息
     * @param id
     * @return 0:删除成功 1:科目下有试题，不允许删除
     */
    @Override
    public Integer deleteSubjectById(String id) {
        Subject subject=new Subject();
        List<Question> questions = questionDao.getQuestionsBySubjectId(id);
        if (questions != null && questions.size()>0){
            return 1;
        }else{
            subject.setId(id);
            subject.setStatus("2");
            subjectDao.deleteSubjectById(subject);
            Knowledge knowledge = new Knowledge();
            knowledge.setSubjectId(id);
            knowledge.setStatus("2");
            //删除知识点
            knowledgeDao.delKnowledge(knowledge);
            return 0;
        }
    }

}
