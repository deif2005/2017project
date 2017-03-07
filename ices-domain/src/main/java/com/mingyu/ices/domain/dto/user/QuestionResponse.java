package com.mingyu.ices.domain.dto.user;

import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.QuestionContent;
import com.mingyu.ices.domain.po.QuestionKnow;
import com.mingyu.ices.domain.po.QuestionResource;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class QuestionResponse extends Question {
    private String subjectName;

    private String questionTypeName;

    //题目内容（试题xml文件内容）
    private QuestionContent questionContent;

    //题目知识点
    private List<QuestionKnow> questionKnowList;

    //题目资源列表
    private List<QuestionResource> questionResourceList;

    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public QuestionContent getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(QuestionContent questionContent) {
        this.questionContent = questionContent;
    }

    public List<QuestionKnow> getQuestionKnowList() {
        return questionKnowList;
    }

    public void setQuestionKnowList(List<QuestionKnow> questionKnowList) {
        this.questionKnowList = questionKnowList;
    }

    public List<QuestionResource> getQuestionResourceList() {
        return questionResourceList;
    }

    public void setQuestionResourceList(List<QuestionResource> questionResourceList) {
        this.questionResourceList = questionResourceList;
    }
}
