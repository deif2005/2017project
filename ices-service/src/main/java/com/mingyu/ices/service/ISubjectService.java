package com.mingyu.ices.service;

import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.po.Subject;
import com.mingyu.ices.domain.po.User;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 科目service
 * Created by Administrator on 2016/6/30 0030.
 */
public interface ISubjectService {

    /**
     * 分页查询科目
     * @param page
     * @return
     */
    public BizDataPage<Subject> pageSubject(Integer page);

    /**
     * 查询科目
     * @param status
     * @return
     */
    public List<Subject> listSubject(String status);

    /**
     * 根据id查询科目信息
     * @param id
     * @return
     */
    public Subject getSubjectById(String id);

    /**
     * 添加科目信息
     * @param subject
     * @param user
     */
    public void insertSubject(Subject subject,User user);

    /**
     * 根据id修改科目信息
     * @param subject
     * @param user
     */
    public void updateSubjectById(Subject subject,User user);

    /**
     * 根据id删除科目信息（软删除）
     * @param id
     */
    public Integer deleteSubjectById(String id);


}
