package com.mingyu.ices.domain.dto.user;

import com.mingyu.ices.domain.po.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/6 0006.
 */
public class PaperDetailResponse {
    /**
     * 试卷
     */
    private Paper paper;
    /**
     * 知识点
     */
    private List<Knowledge> knowledgeList;
    /**
     * 大题
     */
    private  List<Section> sectionList;
    /**
     * 小题
     */
    private Map<String, List<Question>> QuestionMap;

   private List<QuestionResponse> questionResponseList;

    private List<QuestionContent> questionContents;

    public List<QuestionContent> getQuestionContents() {
        return questionContents;
    }

    public void setQuestionContents(List<QuestionContent> questionContents) {
        this.questionContents = questionContents;
    }

    public List<QuestionResponse> getQuestionResponseList() {
        return questionResponseList;
    }

    public void setQuestionResponseList(List<QuestionResponse> questionResponseList) {
        this.questionResponseList = questionResponseList;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public List<Knowledge> getKnowledgeList() {
        return knowledgeList;
    }

    public void setKnowledgeList(List<Knowledge> knowledgeList) {
        this.knowledgeList = knowledgeList;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public Map<String, List<Question>> getQuestionMap() {
        return QuestionMap;
    }

    public void setQuestionMap(Map<String, List<Question>> questionMap) {
        QuestionMap = questionMap;
    }
}
