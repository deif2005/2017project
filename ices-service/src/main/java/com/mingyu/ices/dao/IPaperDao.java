package com.mingyu.ices.dao;

import com.mingyu.ices.domain.po.Paper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
@Repository
public interface IPaperDao {

    /**
     * 分页查询试卷列表
     * @param paper
     * @param begin
     * @param end
     * @return
     */
    public List<Paper> pagePaper(@Param("paper")Paper paper ,@Param("startPage")Integer begin,@Param("endPage")Integer end);

    /**
     * 查询试卷列表
     * @param paper
     * @return
     */
    public List<Paper> listPaperByIds(Paper paper);

    /**
     * 统计试卷数量
     * @return
     */
    public Integer countPaper(Paper paper);

    /**
     * 新增试卷信息
     * @param paper
     */
    public void insertPaper(Paper paper);

    /**
     * 删除试卷（软删除）
     * @param status
     * @param id
     */
    public void deletePaperById(@Param("status") String status,@Param("id")String id);

    /**
     * 根据试卷ID查询试卷信息
     * @param id
     * @return
     */
    public Paper getPaperById(@Param("id") String id);

    /**
     * 修改试卷信息
     * @param paper
     */
    public void updatePaper(Paper paper);
    /**
     * 更新试卷审核状态
     * @param paper
     */
    public void updatePaperCheckStatus(Paper paper);

    /**
     * 根据科目名称统计
     * @param paper
     * @return
     */
    public Integer countByName(Paper paper);
}
