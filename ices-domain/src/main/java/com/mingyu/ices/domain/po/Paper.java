package com.mingyu.ices.domain.po;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public class Paper implements Serializable{
    private static final long serialVersionUID = -4703831211506433131L;
    private String  id;
    private String  name;
    private String  subjectId;
    private String  type;
    private String  passScore;
    private String  totalScore;
    private String  length;
    private String  questionsJson;
    private String  checkStatus;
    private String  status;
    private String  remark;
    private String  creater;
    private String  createtime;
    private String  modifier;
    private String  modifytime;


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

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
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

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
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

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public Paper(){}


    public Paper(String id){
        this.id = id;
    }

    public Paper(String id, String name, String subjectId, String type, String passScore, String totalScore, String length,
                 String questionsJson, String checkStatus, String status, String remark, String creater, String createtime, String modifier, String modifytime) {
        this.id = id;
        this.name = name;
        this.subjectId = subjectId;
        this.type = type;
        this.passScore = passScore;
        this.totalScore = totalScore;
        this.length = length;
        this.questionsJson = questionsJson;
        this.checkStatus = checkStatus;
        this.status = status;
        this.remark = remark;
        this.creater = creater;
        this.createtime = createtime;
        this.modifier = modifier;
        this.modifytime = modifytime;
    }
}
