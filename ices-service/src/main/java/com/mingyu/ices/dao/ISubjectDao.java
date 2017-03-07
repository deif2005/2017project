package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.Subject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 科目dao
 * Created by Administrator on 2016/6/30 0030.
 */
@Repository
public interface ISubjectDao {

    /**
     * 分页查询科目
     * @param status
     * @param begin
     * @param end
     * @return
     */
    public List<Subject> pageSubject(@Param("status")String status ,@Param("statsPage")Integer begin,@Param("endPage")Integer end);

    /**
     * 查询科目
     * @param status
     * @return
     */
    public List<Subject> listSubject(@Param("status")String status);

    /**
     * 根据id查询科目信息
     * @param id
     * @param status
     * @return
     */
    public Subject getSubjectById(@Param("id") String id,@Param("status") String status);

    /**
     * 统计科目数量
     * @return
     */
    public Integer countSubject(@Param("status") Integer status);

    /**
     * 添加科目信息
     * @param subject
     */
    public void insertSubject(Subject subject);

    /**
     * 根据id修改科目信息
     * @param subject
     */
    public void updateSubjectById(Subject subject);

    /**
     * 修改科目信息
     * @param subject
     */
    public void deleteSubjectById(Subject subject);

    /**
     * 根据科目名称统计
     * @param subject
     * @return
     */
    public Integer countByName(Subject subject);
}
