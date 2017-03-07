package com.mingyu.ices.domain.po;

import java.io.Serializable;

/**
 * Created by Sugior on 2016/7/4.
 */
public class Question implements Serializable{
    private static final long serialVersionUID = 9123953340032802724L;
    private String  id;
    private String  name;
    private String  questionTypeId;
    private String  subjectId;
    private String  contextPath;
    private String  difficulty;
    private String  resource;
    private String  keyword;
    private String  checkStatus;
    private String  status;
    private String  remark;
    private String  creater;
    private String  createtime;
    private String  modifier;
    private String  modifytime;
    //复制试题时用到的新增Id
    private String  newquestionId;

    private String sort;

    //1主观题，2客观题
    private String type;
    //知识点ID
    private String knowId;

    public String getKnowId() {
        return knowId;
    }

    public void setKnowId(String knowId) {
        this.knowId = knowId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestionTypeId() {
        return questionTypeId;
    }

    public void setQuestionTypeId(String questionTypeId) {
        this.questionTypeId = questionTypeId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public void setCreatetime(String createTime) {
        this.createtime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifyTime) {
        this.modifytime = modifyTime;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getNewquestionId() {
        return newquestionId;
    }

    public void setNewquestionId(String copyquestionId) {
        this.newquestionId = copyquestionId;
    }
    //将题目难度id 转换成中文
    public String getDifficultyStr() {
        switch (this.difficulty) {
            case "1":return "难度不限";
            case "2":return "难";
            case "3":return "中";
            case "4":return "易";
            default:return "";
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
