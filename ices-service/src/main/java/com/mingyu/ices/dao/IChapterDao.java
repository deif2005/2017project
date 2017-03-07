package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.Chapter;
import com.mingyu.ices.domain.po.Paper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserDao
 * 章节信息dao
 * @author yuhao
 * @date 2016/7/11
 */
@Repository
public interface IChapterDao {

    /**
     * 获取章节信息
     * @param chapter
     * @return
     */
    public List<Chapter> listChapter(Chapter chapter);

    /**
     * 获取章节信息
     * @param list
     * @return
     */
    public void insertChapter(List<Chapter> list);

    /**
     * 修改章节状态
     * @param chapter
     */
    public void deleteByPaperId(Chapter chapter);
}
