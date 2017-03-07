package com.mingyu.ices.domain.po;

/**
 * Created by defi on 2016/7/5.
 * 题目内容
 */
public class QuestionContent {
    //试题题型
    private String questionType;
    //题干内容
    private String questionContent;
    //试题解释
    private String questionanAlysis;
    //选项串或小题串
    private String questionItemStr;
    //标准答案串
    private String questionAnswerStr;
    //大题小题关系
    private String sectionQuestion;
    //小题ID
    private String questionId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getSectionQuestion() {
        return sectionQuestion;
    }

    public void setSectionQuestion(String sectionQuestion) {
        this.sectionQuestion = sectionQuestion;
    }

    public String getQuestiontype() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getQuestionanAlysis() {
        return questionanAlysis;
    }

    public void setQuestionanAlysis(String questionanAlysis) {
        this.questionanAlysis = questionanAlysis;
    }

    public String getQuestionItemStr() {
        return questionItemStr;
    }

    public void setQuestionItemStr(String questionItemStr) {
        this.questionItemStr = questionItemStr;
    }

    public String getQuestionAnswerStr() {
        return questionAnswerStr;
    }

    public void setQuestionAnswerStr(String questionAnswerStr) {
        this.questionAnswerStr = questionAnswerStr;
    }

}
