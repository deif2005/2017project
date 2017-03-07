package com.mingyu.ices.domain.dto.user;

import com.mingyu.ices.domain.po.QuestionContent;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class QuestionRandomResponse extends QuestionContent{

    private String questionId;

    private String questionTypeId;

    public String getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(String questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
