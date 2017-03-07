package com.mingyu.ices.service;

import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.dto.user.PaperDetailResponse;
import com.mingyu.ices.domain.dto.user.QuestionRandomResponse;
import com.mingyu.ices.domain.po.Paper;
import com.mingyu.ices.domain.po.PaperKnow;
import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.User;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
public interface IPaperService {

    /**
     * 分页查询试卷列表
     * @param page
     * @return
     */
    public BizDataPage<Paper> pagePaper(Integer page,Paper paper);

    /**
     * 新增试卷信息
     * @param paper
     * @param knowIdStr
     * @param user
     */
    public void insertPaper(Paper paper,User user, String knowIdStr);

    /**
     * 删除试卷（软删除）
     * @param status
     * @param id
     */
    public void deletePaperById(String status,String id);

    /**
     * 预览试卷信息
     * @param id
     */
    public PaperDetailResponse previewPaper(String id) throws Exception;

    /**
     * 根据试卷ID查询试卷信息
     * @param id
     */
    public PaperDetailResponse getPaperById(String id) throws Exception;

    /**
     * 修改试卷信息
     * @param paper
     * @param user
     * @param knowIdStr
     */
    public void updatePaper(Paper paper,User user, String knowIdStr);

    /**
     * 更新试卷审核状态
     * @param id
     * @param checkStatus
     * @param user
     */
    public void updatePaperCheckStatus( String id,String checkStatus,User user) ;

    /**
     * 随机取试题信息
     * @param questionTypeId
     * @param numberStr
     * @param knowIdList
     * @return
     */
    public Map<String,List<QuestionRandomResponse>> listRandomQuestion(String questionTypeId, String numberStr,String knowIdList) throws Exception ;

    /**
     * 复制试卷
     * @param paperId
     * @param paperName
     */
    public void  copyPaper(String paperId,String paperName);

    /**
     * 查询试卷列表
     * @param paper
     * @return
     */
    public List<Paper> listPaperByIds(Paper paper);

    /**
     * 导出试卷包
     * @param fileName
     * @param ids
     * @return 返回试卷包文件路径供下载
     */
    public String exportPaper(String fileName,String ids) throws Exception;
}
