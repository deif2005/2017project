package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.Section;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public interface ISectionDao {

    /**
     * 批量添加大题信息
     * @param sectionList
     */
    public void batchInsertSection(@Param("sectionList") List<Section> sectionList);

    /**
     * 根据试卷ID查询大题信息
     * @param paperId
     * @return
     */
    public List<Section> listSectionByPaperId(@Param("paperId") String paperId, @Param("chapterId") String chapterId);

    /**
     * 修改大题信息
     * @param section
     */
    public void updateSection(Section section);

    /**
     * 批量修改大题信息
     * @param sectionList
     */
    public int batchUpdateSection(@Param("sectionList") List<Section> sectionList);


    /**
     * 根据ID删除
     * @param section
     */
    public void deleteByPaperId(Section section);

}
