package com.mingyu.ices.domain.po;

/**
 * Created by defi on 2016/7/11.
 */
public class QuestionResource {
    private String id;
    private String questionId;        //试题id',
    private String type;                //资源类型',
    private String name;                //资源名称/文件名称',
    private String path;                //资源路径',
    private String status;              //状态：1正常，2删除，3停用或其它',
    private String creater;             //创建人',
    private String createtime;
    private String modifier;
    private String modifytime;
    //复制试题时用到的新生成的试题Id
    private String newquestionId;

    public QuestionResource(){}

    public QuestionResource(String questionId){
        this.questionId = questionId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getNewquestionId() {
        return newquestionId;
    }

    public void setNewquestionId(String copyquestionId) {
        this.newquestionId = copyquestionId;
    }
}
