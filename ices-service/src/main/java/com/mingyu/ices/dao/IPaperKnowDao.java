package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.PaperKnow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public interface IPaperKnowDao {

    /**
     * 新增试卷知识点关系
     * @param paperKnowList
     */
    public void insertPaperKnow(@Param("paperKnowList") List<PaperKnow> paperKnowList);

    /**
     * 修改试卷知识点
     * @param paperKnow
     */
    public void updatePaperKnow(PaperKnow paperKnow);

    /**
     * 批量修改试卷知识点
     * @param paperKnowList
     */
    public void batchUpdatePaperKnow(@Param("paperKnowList") List<PaperKnow> paperKnowList);

    /**
     * 根据考试ID查询
     * @param paperId
     * @return
     */
    public List<PaperKnow> listPaperKnowByPaperId(String paperId);

    /**
     * 根据试卷ID删除
     * @param paperId
     */
    public void deleteByPaperId(String paperId);
}
