package com.mingyu.ices.service.impl;

import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.IQuestionDao;
import com.mingyu.ices.dao.IQuestionTypeDao;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.po.Question;
import com.mingyu.ices.domain.po.QuestionType;
import com.mingyu.ices.domain.po.User;
import com.mingyu.ices.service.IQuestionTypeService;
import com.mingyu.ices.constant.EnumOrdinal;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by defi on 2016/7/1.
 * 题型管理 service
 */

@Service
public class QuestionTypeServiceImpl implements IQuestionTypeService {
    @Autowired
    IQuestionTypeDao questionTypeDao;

    @Autowired
    IQuestionDao questionDao;

    /**
     * 分页查询题型信息
     * @param page
     * @param subjectId
     * @return BizDataPage<QuestionType>
     */
    @Override
    public BizDataPage<QuestionType> listQuestionType(String subjectId,Integer page){
        BizDataPage<QuestionType> bizDataPage = new BizDataPage<>();
        bizDataPage.setPage(page);
        Integer status= EnumOrdinal.normal.getValue();
        List<QuestionType> questionTypeList = questionTypeDao.pageQuestionType(subjectId,status
                , bizDataPage.getOffset(), bizDataPage.getPagesize());
         int totalRecord = questionTypeDao.countQuestionType(subjectId,status);
        bizDataPage.setRecords(totalRecord);
        bizDataPage.setRows(questionTypeList);
        return bizDataPage;
    }

    /**
     * 插入题型信息
     * @param questionType
     */
    public void addQuestionType(QuestionType questionType){
        questionType.setId(UUID.randomUUID().toString());
        questionTypeDao.insertQuestionType(questionType);
    }

    /**
     * 更新题型信息
     * @param questionType
     */
    @Override
    public void updateQuestionType(QuestionType questionType){
        questionTypeDao.updateQuestionTypeById(questionType);
    }

    /**
     * 查询题型信息
     * @param Id
     */
    @Override
    public QuestionType getQuestionTypeById(String Id){
        QuestionType questionType = questionTypeDao.getQuestionTypeById(Id);
        return questionType;
    }

    /**
     * 批量插入题型信息
     * @param rows 导入的记录行数  datas 导入的记录数组 Subject_Id 题型所属科目Id
     * @return 插入结果信息
     */
    public String addBatchQuestionType(int rows, String[][] datas, String subject_Id, String userId) throws BizException {
        List<QuestionType> qlist = checkReturnData(rows, datas);
        for (QuestionType questionType:qlist){
            questionType.setSubjectId(subject_Id);
            questionType.setCreater(userId);
            addQuestionType(questionType);
        }
        return "题型数据导入完成";
    }

    /**
     * 返回查询科目题型信息
     * @return 返回核查过的记录list
     */
    @Override
    public List<QuestionType> listQuestionType() {
        return questionTypeDao.listQuestionType(String.valueOf(EnumOrdinal.normal.getValue()));
    }

    /**
     * 获取题型信息集合
     * @param subjectId
     * @return QuestionType结果集
     */
    @Override
    public List<QuestionType>  listQuestionTypeBySubjectId(String subjectId){
        return questionTypeDao.listQuestionTypeBySubjectId(subjectId,String.valueOf(EnumOrdinal.normal.getValue()));
    }

    /**
     * 返回核查后的题型信息
     * @param rows 导入的记录行数  datas 导入的记录数组
     * @return 返回核查过的记录list
     */
    protected List<QuestionType> checkReturnData(int rows, String[][] datas) {
        ArrayList<QuestionType> list = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (int row = 0; row <= rows; row++){
            int idx = row + 1;
            if (StringUtils.isBlank(datas[row][EnumOrdinal.QuestionTypeName.getValue()])){
                throw new BizException(WebConstants.ERROR,
                        "导入的题型数据异常，题型名称不能为空(第"+idx+"行)");
            }else if (StringUtils.isBlank(datas[row][EnumOrdinal.QuestionTypeRemark.getValue()])){
                throw new BizException(WebConstants.ERROR,
                        "导入的题型数据异常，题型备注不能为空,请填入题型的主客观类型(第"+idx+"行)");
            }
            String str = datas[row][EnumOrdinal.QuestionTypeName.getValue()].trim()+"■"+
                    datas[row][EnumOrdinal.QuestionTypeRemark.getValue()].trim();
            //将数组中的值串起来判断是否有重复值
            if (!hashSet.contains(str)){
                hashSet.add(str);
                QuestionType questionType = new QuestionType();
                questionType.setName(datas[row][EnumOrdinal.QuestionTypeName.getValue()]);
                questionType.setRemark(datas[row][EnumOrdinal.QuestionTypeRemark.getValue()]);
                list.add(questionType);
            }else {
                throw new BizException(WebConstants.ERROR,"导入题型信息时,在第"+idx+"行发现重复数据");
            }
        }
        return list;
    }

    /**
     * 软删除题型信息
     * @param questionTypeId
     */
    @Transactional
    public void delQuestionTypeById(String questionTypeId, User user){
        //判断是否存在该题型的试题
        List<Question> questions = questionDao.getQuestionsByTypeId(questionTypeId);
        if (questions != null && questions.size() > 0)
            throw new BizException(WebConstants.EXISTS_RELATED_DATA,"该题型存在试题信息不允许删除");
        else {
            QuestionType questionType = new QuestionType();
            questionType.setId(questionTypeId);
            questionType.setModifier(user.getId());
            questionTypeDao.deleteQuestionTypeById(questionType);
        }
    }
}
