package com.mingyu.ices.domain.dto.user;

import com.mingyu.ices.domain.po.Knowledge;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class KnowledgeResponse extends Knowledge{
    private String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
