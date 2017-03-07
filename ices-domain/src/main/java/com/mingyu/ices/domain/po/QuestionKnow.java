package com.mingyu.ices.domain.po;

/**
 * Created by defi on 2016/7/6.
 * 题目知识点对应关系
 */
public class QuestionKnow {
    private String id;
    //题目ID
    private String questionId;
    //知识点ID
    private String knowId;
    //知识点名称
    private String knowname;
    private String creater;
    private String createtime;
    //复制试题时用到的新生成的试题Id
    private String newquestionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getKnowId() {
        return knowId;
    }

    public void setKnowId(String knowId) {
        this.knowId = knowId;
    }

    public String getKnowname() {
        return knowname;
    }

    public void setKnowname(String knowname) {
        this.knowname = knowname;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getNewquestionId() {
        return newquestionId;
    }

    public void setNewquestionId(String copyquestionId) {
        this.newquestionId = copyquestionId;
    }
}
