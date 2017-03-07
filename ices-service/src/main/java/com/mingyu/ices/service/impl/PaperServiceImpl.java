package com.mingyu.ices.service.impl;

import com.mingyu.ices.config.SystemConfig;
import com.mingyu.ices.constant.FileConstants;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.*;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.PaperDetailResponse;
import com.mingyu.ices.domain.dto.user.QuestionRandomResponse;
import com.mingyu.ices.domain.po.*;
import com.mingyu.ices.service.IPaperService;
import com.mingyu.ices.service.IQuestionService;
import com.mingyu.ices.util.FileUtil;
import com.mingyu.ices.util.ZipUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/1 0001.
 */
@Service
public class PaperServiceImpl implements IPaperService {

    private static Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private static final String httpRplStr = "#httpReplaceStr#";
    //发布路径用以展示(替换成代替符)
    private static final String rplStr = SystemConfig.getStaticShowPath();

    @Autowired
    IPaperDao paperDao;//试卷

    @Autowired
    IPaperKnowDao paperKnowDao;//试卷知识点关系

    @Autowired
    ISectionDao sectionDao;//大题

    @Autowired
    ISectionQuestionsDao sectionQuestionsDao;//大题试题关系

    @Autowired
    IKnowledgeDao knowledgeDao;//知识点

    @Autowired
    IQuestionDao questionDao;//试题

    @Autowired
    IQuestionKnowDao questionKnowDao;//试题知识点关系

    @Autowired
    IChapterDao chapterDao;

    @Autowired
    IQuestionResourceDao resourceDao;

    @Autowired
    IQuestionService questionService;

    @Autowired
    ISubjectDao subjectDao;

    @Override
    public BizDataPage<Paper> pagePaper(Integer page, Paper paper) {
        BizDataPage bizDataPage = new BizDataPage();
        bizDataPage.setPage(page);
        paper.setStatus("1");
        List<Paper> paperList = paperDao.pagePaper(paper, bizDataPage.getOffset(), bizDataPage.getPagesize());
        Integer records = paperDao.countPaper(paper);
        bizDataPage.setRows(paperList);
        bizDataPage.setRecords(records);
        return bizDataPage;
    }

    @Override
    @Transactional
    public void insertPaper(Paper paper, User user, String knowIdStr) {

        Integer number=paperDao.countByName(paper);
        //校验名称是否重复
        if (number>0){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        PaperKnow paperKnow = new PaperKnow();
        Chapter chapter = new Chapter();
        String name = paper.getName().trim();//试卷名称
        String subjectId = paper.getSubjectId().trim();//科目ID
        String passScore = paper.getPassScore().trim();//及格分数
        String totalScore = paper.getTotalScore().trim();//总分
        String length = paper.getLength().trim();//考试时长
        String questionsJson = paper.getQuestionsJson().trim();//试题信息json，存放题型、题型数量、题型分数
        String userId = user.getId();//登陆用户ID
        String paperId = UUID.randomUUID().toString();//试卷ID
        String chapterId = UUID.randomUUID().toString(); //生成章节信息
        List<Section> sectionList = parseQuestionsJson(questionsJson, paperId, chapterId, userId);//大题信息
        /************校验试卷信息****start*****/
        //试卷名称
        if (name == null || StringUtils.isBlank(name)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请填写试卷名称");
        } else if (name.length() > 64) {
            throw new BizException(WebConstants.PAPER_NAME_ERROR, "试卷名称太长");
        }

        //所属科目
        if (subjectId == null || StringUtils.isBlank(subjectId)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请选择所属科目");
        }

        //及格分数
        if (passScore == null || StringUtils.isBlank(passScore)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请填写及格分数");
        } else if (!(isNum(passScore) || Float.valueOf(passScore) <= 0 || Float.valueOf(passScore) > 10000)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "及格分数有误");
        }

        //总分
        if (totalScore == null || StringUtils.isBlank(totalScore)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请填写总分");
        } else if (!(isNum(totalScore)) || Float.valueOf(totalScore) <= 0 || Float.valueOf(totalScore) > 10000) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "总分填写有误");
        }

        //考试时长
        if (length == null || StringUtils.isBlank(length)) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请设定考试时长");
        } else if (!isNum(length) || Integer.valueOf(length) < 1 || Integer.valueOf(length) > 9999) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "考试时长设定有误");
        }
        //试题信息json，存放题型、题型数量、题型分数
        if (sectionList == null) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "请设定题信息");
        } else if (questionsJsonCheck(sectionList) == false) {
            throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, "题信息设定有误");
        }
        /************校验试卷信息****end*****/
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        for (int i = 0; i < sectionList.size(); i++) {
            Section section = sectionList.get(i);
            json.put("name", section.getName());
            json.put("questionTypeId", section.getQuestionTypeId());
            json.put("count", section.getCount());
            json.put("score", section.getScore());
            array.add(json);
        }
        paper.setQuestionsJson(array.toString());
        paper.setId(paperId);
        paper.setCreater(userId);//创建人
        paper.setStatus("1");//状态
        paper.setCheckStatus(addCheckStatus(sectionList));//检查状态
        //生成知识点信息
        paperKnow.setId(UUID.randomUUID().toString());//试卷知识点关系ID
        paperKnow.setPaperId(paperId);//试卷ID
        paperKnow.setCreater(userId);//创建人
        chapter.setId(chapterId);
        chapter.setPaperId(paperId);
        chapter.setName("第一章节");
        chapter.setSort("1");
        chapter.setScore(paper.getTotalScore());
        chapter.setRemark("第一章节:在InsertPaper方法中自动生成");
        chapter.setCreater(userId);
        paperDao.insertPaper(paper);//添加到试卷表
        List<Chapter> chapterList = new ArrayList<>();
        chapterList.add(chapter);
        chapterDao.insertChapter(chapterList);//添加到章节表中
        paperKnowDao.insertPaperKnow(getPaperKnow(userId, knowIdStr, paperId));//添加到试卷知识点关系表
        sectionDao.batchInsertSection(sectionList);//添加到大题表
        sectionQuestionsDao.batchInsertSectionQuestions(getSectionQuestions(sectionList, userId, paperId, chapterId));//添加到大题试题表
    }

    @Override
    public void deletePaperById(String status, String id) {
        paperDao.deletePaperById(status, id);
        sectionQuestionsDao.deleteByPaperId(id);
    }

    @Override
    public PaperDetailResponse previewPaper(String id) throws Exception {
        PaperDetailResponse paperDetailResponse = new PaperDetailResponse();
        Paper paper = paperDao.getPaperById(id);//试卷表信息
        List<Knowledge> knowledgeList = knowledgeDao.listKnowLedgeByPaperKnow(id);//知识点
        List<Section> sectionList = sectionDao.listSectionByPaperId(id, null);//大题
        Map<String, List<Question>> questionMap = new HashMap<>();//小题
        List<QuestionContent> questionContentList = new ArrayList<>();
        if (sectionList.size() > 0) {
            for (int i = 0; i < sectionList.size(); i++) {
                Section section = sectionList.get(i);
                String sectionId = section.getId();//大题ID
                String sort = section.getSort();//大题序号
                List<Question> questionList = questionDao.listQuestionBySectionId(sectionId);//小题
                questionMap.put(sort, questionList);
                for (int j = 0; j < questionList.size(); j++) {
                    Question question = questionList.get(j);
                    // QuestionResponse questionResponse =questionService.getQuestionDetail(question.getId());
                    //获取静态资源上传地址+xml文件保存相对地址+xml文件名
                    String xmlFullPath = getXmlPath(question.getId(), question.getSubjectId());
                    QuestionContent questionContent = questionService.getQuestionXmlInfo(xmlFullPath);
                    questionContent.setQuestionId(question.getId());
                    questionContent.setSectionQuestion(sort);
                    questionContentList.add(questionContent);
                }
            }
        }
        paperDetailResponse.setPaper(paper);
        paperDetailResponse.setKnowledgeList(knowledgeList);
        paperDetailResponse.setSectionList(sectionList);
        paperDetailResponse.setQuestionMap(questionMap);
        paperDetailResponse.setQuestionContents(questionContentList);
        return paperDetailResponse;
    }


    @Override
    public PaperDetailResponse getPaperById(String id) throws Exception {
        PaperDetailResponse paperDetailResponse = new PaperDetailResponse();
        Paper paper = paperDao.getPaperById(id);//试卷表信息
        List<Knowledge> knowledgeList = knowledgeDao.listKnowLedgeByPaperKnow(id);//知识点
        List<Section> sectionList = sectionDao.listSectionByPaperId(id, null);//大题
        Map<String, List<Question>> questionMap = new HashMap<>();//小题
        if (sectionList.size() > 0) {
            for (int i = 0; i < sectionList.size(); i++) {
                Section section = sectionList.get(i);
                String sectionId = section.getId();//大题ID
                String sort = section.getSort();//大题序号
                List<Question> questionList = questionDao.listQuestionBySectionId(sectionId);//小题
                for (int j = 0; j < questionList.size(); j++){
                    String questionName= questionList.get(j).getName().replaceAll(httpRplStr, rplStr);
                    questionList.get(j).setName(questionName) ;
                }
                questionMap.put(sort, questionList);
            }
        }
        paperDetailResponse.setPaper(paper);
        paperDetailResponse.setKnowledgeList(knowledgeList);
        paperDetailResponse.setSectionList(sectionList);
        paperDetailResponse.setQuestionMap(questionMap);
        return paperDetailResponse;
    }

    @Override
    @Transactional
    public void updatePaper(Paper paper, User user, String knowIdStr) {

        String paperName=paperDao.getPaperById(paper.getId()).getName();
        if(!(paperName.equals(paper.getName()))){
            Integer number=paperDao.countByName(paper);
            if (number>0){
                throw new BizException(WebConstants.NAME_REPETITION);
            }
        }
        String paperId = paper.getId();
        paperDao.deletePaperById("2", paperId);
        /***********章节***********/
        Chapter chapter = new Chapter();
        chapter.setPaperId(paperId);
        chapter.setStatus("1");//状态
        chapter.setModifier(user.getId());
        chapterDao.deleteByPaperId(chapter);
        /***********大题***************/
        Section section = new Section();
        section.setPaperId(paperId);
        section.setModifier(user.getId());
        section.setStatus("2");
        sectionDao.deleteByPaperId(section);
        insertPaper(paper, user, knowIdStr);

    }


    /**
     * 校验试题信息json，存放题型、题型数量、题型分数
     *
     * @param sectionList
     * @return
     */
    public boolean questionsJsonCheck(List<Section> sectionList) {

        for (int i = 0; i < sectionList.size(); i++) {
            Section section = sectionList.get(i);
            if (!(isNum(section.getCount())) || Integer.parseInt(section.getCount()) < 1) {
                return false;
            }
            if (!(isNum(section.getScore())) || Float.parseFloat(section.getScore()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * * 检查试卷状态
     * 1未完成、2待检查
     *
     * @param str
     * @return
     */
    public String checkStatus(String str) {
        String[] strArr = str.split(";");
        for (int i = 0; i < strArr.length; i++) {
            String[] arr = strArr[i].split(":");
            Integer number = Integer.parseInt(arr[2]);//小题预计数量
            String[] questionArr = arr[5].split(",");//小题实际数量
            if (number == questionArr.length) {
                return "2";
            }
        }
        return "1";

    }


    /**
     * * 检查试卷状态（添加）
     * 1未完成、2待检查
     *
     * @param sectionList
     * @return
     */
    public String addCheckStatus(List<Section> sectionList) {

        for (int i = 0; i < sectionList.size(); i++) {
            String[] questionIdStrArr = sectionList.get(i).getQuestionIdStr().split(",");//小题实际数量
            Integer number = Integer.parseInt(sectionList.get(i).getCount());//小题预计数量
            if (number == questionIdStrArr.length) {
                return "2";
            }
        }
        return "1";

    }

    /**
     * 解析大题json(新增)
     *
     * @param questionsJson
     * @return
     */
    public List<Section> parseQuestionsJson(String questionsJson, String paperId, String chapterId, String userId) {
        List<Section> returnSectionList = new ArrayList<>();
        List<Section> sectionList = JSONArray.toList(JSONArray.fromObject(questionsJson), Section.class);
        for (int i = 0; i < sectionList.size(); i++) {
            Section section = sectionList.get(i);
            section.setId(UUID.randomUUID().toString());//大题ID
            section.setSort(i + 1 + "");//排序
            section.setPaperId(paperId);//试卷ID
            section.setChapterId(chapterId);//章节ID
            section.setStatus("1");//状态
            section.setCreater(userId);//创建人
            returnSectionList.add(section);
        }
        return returnSectionList;
    }


    /**
     * 解析大题json(修改)
     *
     * @param questionsJson
     * @return
     */
    public List<Section> updateParseQuestionsJson(String questionsJson, String paperId, String userId) {
        List<Section> sectionList = new ArrayList<>();
        String[] questionsJsonArr = questionsJson.split(";");
        for (int i = 0; i < questionsJsonArr.length; i++) {
            String[] arr = questionsJsonArr[i].split(":");
            Section section = new Section();
            section.setId(arr[0]);//大题ID
            section.setName(arr[1]);//大题名称
            section.setCount(arr[2]);//小题数
            Integer sort = i + 1;
            section.setSort(sort.toString());//排序
            section.setScore(arr[3]);//每小题分数
            section.setRemark(arr[4]);//大题备注
            section.setModifier(userId);//修改人
            sectionList.add(section);
        }
        return sectionList;
    }

    /**
     * 获取大题试题关系信息
     *
     * @param sectionList
     * @param userName
     * @param paperId
     * @param chapterId
     * @return
     */
    public List<SectionQuestions> getSectionQuestions(List<Section> sectionList, String userName, String paperId, String chapterId) {
        List<SectionQuestions> sectionQuestionsList = new ArrayList<>();
        Integer sort = 0;
        for (int i = 0; i < sectionList.size(); i++) {
            String[] questionsId = sectionList.get(i).getQuestionIdStr().split(",");
            for (int j = 0; j < questionsId.length; j++) {
                sort++;
                SectionQuestions sectionQuestions = new SectionQuestions();
                sectionQuestions.setId(UUID.randomUUID().toString());//ID
                sectionQuestions.setSort(sort.toString());
                sectionQuestions.setSectionId(sectionList.get(i).getId());//大题ID
                sectionQuestions.setChapterId(chapterId);//章节
                sectionQuestions.setPaperId(paperId);//试卷ID
                sectionQuestions.setQuestionId(questionsId[j]);//小题ID
                sectionQuestions.setCreater(userName);//创建人
                sectionQuestionsList.add(sectionQuestions);
            }

        }
        return sectionQuestionsList;
    }

    /**
     * 获取试卷知识点关系
     *
     * @param userName
     * @param knowIdString
     * @param paperId
     * @return
     */
    public List<PaperKnow> getPaperKnow(String userName, String knowIdString, String paperId) {
        List<PaperKnow> paperKnowList = new ArrayList<>();
        String[] knowIdArr = knowIdString.split(",");
        for (int i = 0; i < knowIdArr.length; i++) {
            PaperKnow paperKnow = new PaperKnow();
            paperKnow.setId(UUID.randomUUID().toString());
            paperKnow.setPaperId(paperId);
            paperKnow.setKnowId(knowIdArr[i]);
            paperKnow.setCreater(userName);
            paperKnowList.add(paperKnow);
        }
        return paperKnowList;
    }

//    /**
//     * 获取修改的试卷知识点关系
//     * @param knowIdString
//     * @return
//     */
//    public List<PaperKnow> getUpdatePaperKnow(String knowIdString){
//        List<PaperKnow> paperKnowList=new ArrayList<>();
//        String[] knowIdArr=knowIdString.split(";");
//        for (int i = 0; i <knowIdArr.length ; i++) {
//            PaperKnow paperKnow=new PaperKnow();
//            String knowIdStr=knowIdArr[i];
//            String [] arr=knowIdStr.split(",");
//            paperKnow.setId(arr[0]);
//            paperKnow.setKnowId(arr[1]);
//            paperKnowList.add(paperKnow);
//        }
//        return paperKnowList;
//    }

    public List<SectionQuestions> getUpdateSectionQuestions(String questionsJson, String paperId, String userId) {
        List<SectionQuestions> addSectionQuestionsList = new ArrayList<>();
        List<SectionQuestions> deleteSectionQuestionsList = new ArrayList<>();
        String[] questionsJsonArr = questionsJson.split(";");
        Integer sort = 0;
        for (int i = 0; i < questionsJsonArr.length; i++) {
            String[] arr = questionsJsonArr[i].split(":");
            SectionQuestions addSectionQuestions = new SectionQuestions();//添加
            SectionQuestions deleteSectionQuestions = new SectionQuestions();//删除
            deleteSectionQuestions.setId(arr[1]);//ID（用于删除）
            sort = sort + i + 1;
            addSectionQuestions.setId(UUID.randomUUID().toString());
            addSectionQuestions.setSort(sort.toString());//状态
            addSectionQuestions.setSectionId(arr[1]);//大题ID
            addSectionQuestions.setPaperId(paperId);//试卷ID
            addSectionQuestions.setChapterId("1");//章节ID
            addSectionQuestions.setQuestionId(arr[2]);//小题ID
            addSectionQuestions.setCreater(userId);//创建人
            addSectionQuestionsList.add(addSectionQuestions);
            deleteSectionQuestionsList.add(deleteSectionQuestions);

        }
        sectionQuestionsDao.batchDeleteSectionQuestion(deleteSectionQuestionsList);
        return addSectionQuestionsList;
    }

    /**
     * 更新试卷审核状态
     *
     * @param id
     * @param checkStatus
     * @param user
     */
    public void updatePaperCheckStatus(String id, String checkStatus, User user) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setCheckStatus(checkStatus);
        paper.setModifier(user.getId());
        paperDao.updatePaperCheckStatus(paper);
    }

    @Override
    public Map<String, List<QuestionRandomResponse>> listRandomQuestion(String questionTypeId, String numberStr, String knowIdStr) throws Exception {
        Map<String, List<QuestionRandomResponse>> map = new HashMap<>();
        List<QuestionRandomResponse> listQuestionRandomResponse = new ArrayList<>();
        String[] questionTypeIdArr = questionTypeId.split(";");//试题类型
        String[] numberArr = numberStr.split(",");
        for (int z = 0; z < questionTypeIdArr.length; z++) {

            String[] knowIdArr = knowIdStr.split(",");//知识点
            List<String> knowIdList = new ArrayList<>();
            for (int i = 0; i < knowIdArr.length; i++) {
                knowIdList.add(knowIdArr[i]);
            }
            Integer number = Integer.parseInt(numberArr[z]);
            List<Question> questionList = questionDao.listRandomQuestion(questionTypeIdArr[z], number, knowIdList);//试题
            if(questionList.size()<number){
                int order=z+1;
                throw new BizException(WebConstants.QUESTION_AMOUNT_ERROR);
            }
            //试题内容
            for (int i = 0; i < questionList.size(); i++) {
                Question question = questionList.get(i);
                String contextPath = getXmlPath(question.getId(), question.getSubjectId());
                SAXReader reader = new SAXReader();
                Document document = reader.read(new File(contextPath));
                Element eleroot = document.getRootElement();
                //获取根节点作为迭代器
                Iterator iter = eleroot.elementIterator();
                QuestionRandomResponse questionContent = new QuestionRandomResponse();
                while (iter.hasNext()) {
                    Element element = (Element) iter.next();
                    questionContent.setQuestionId(question.getId());
                    questionContent.setQuestionTypeId(questionTypeId);
                    if ("question_type".equals(element.getName())) {
                        questionContent.setQuestionType(element.getText().replaceAll(httpRplStr, rplStr));
                    } else if ("question_content".equals(element.getName())) {
                        questionContent.setQuestionContent(element.getText().replaceAll(httpRplStr, rplStr));
                    } else if ("question_analysis".equals(element.getName())) {
                        questionContent.setQuestionanAlysis(element.getText().replaceAll(httpRplStr, rplStr));
                    } else if ("question_itemstr".equals(element.getName())) {
                        questionContent.setQuestionItemStr(element.getText().replaceAll(httpRplStr, rplStr));
                    } else if ("question_answerstr".equals(element.getName())) {
                        questionContent.setQuestionAnswerStr(element.getText().replaceAll(httpRplStr, rplStr));
                    }
                }
                listQuestionRandomResponse.add(questionContent);
            }
            map.put(questionTypeIdArr[z], listQuestionRandomResponse);
        }
        return map;
    }

    @Override
    public void copyPaper(String paperId,String paperName) {

        //校验名称是否重复
        Paper param=new Paper();
        param.setName(paperName);
        String name=paperDao.getPaperById(paperId).getName();
        if (!(name.equals(paperName))){
            Integer number=paperDao.countByName(param);
        if (number>0){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        }else if (name.equals(paperName)){
            throw new BizException(WebConstants.NAME_REPETITION);
        }
        /*************查询出是试卷信息***********start********/
        Paper paper = paperDao.getPaperById(paperId);//试卷信息
        Chapter chapter=new Chapter();
        chapter.setPaperId(paperId);
        List<Chapter> chapterList =chapterDao.listChapter(chapter);//章节信息
        List<PaperKnow> paperKnowList = paperKnowDao.listPaperKnowByPaperId(paperId);//试卷知识点关系
        List<Section> sectionList = sectionDao.listSectionByPaperId(paperId, null);//大题信息
        List<SectionQuestions> sectionQuestionsList = sectionQuestionsDao.listSectionQuestionByPaperId(paperId);//大题试题关系
        /***********查询出是试卷信息***********end********/

        /***********复制试卷******************start******/
        paperId = UUID.randomUUID().toString();//试卷ID
        paper.setId(paperId);
        paper.setName(paperName);
        paperDao.insertPaper(paper);//添加到试卷表
        for (int i = 0; i <chapterList.size() ; i++) {
            chapterList.get(i).setPaperId(paperId);
            chapterList.get(i).setId(UUID.randomUUID().toString());
        }
        chapterDao.insertChapter(chapterList);
        if (paperKnowList.size() > 0) {
            for (int i = 0; i < paperKnowList.size(); i++) {
                String paperKnowId = UUID.randomUUID().toString();//试卷知识点关系ID
                paperKnowList.get(i).setId(paperKnowId);
                paperKnowList.get(i).setPaperId(paperId);
            }
            paperKnowDao.insertPaperKnow(paperKnowList);//添加到试卷知识点关系

            if (sectionList.size() > 0) {
                for (int i = 0; i < sectionList.size(); i++) {
                    String newSectionId = UUID.randomUUID().toString();//大题ID
                    String oldSectionId = sectionList.get(i).getId();
                    sectionList.get(i).setId(newSectionId);//修改新的大题ID
                    sectionList.get(i).setPaperId(paperId);//修改为新的试卷ID
                    sectionList.get(i).setChapterId(chapterList.get(0).getId());
                    if (sectionQuestionsList.size() > 0) {
                        List<SectionQuestions> newSectionQuestionsList = new ArrayList<SectionQuestions>();
                        for (int j = 0; j < sectionQuestionsList.size(); j++) {
                            String newSectionQuestionId = UUID.randomUUID().toString();
                            if ((sectionQuestionsList.get(j).getSectionId()).equals(oldSectionId)) {
                                sectionQuestionsList.get(j).setId(newSectionQuestionId);//修改成新的ID
                                sectionQuestionsList.get(j).setSectionId(newSectionId);//修改为新的大题
                                sectionQuestionsList.get(j).setPaperId(paperId);//修改为新的试卷ID
                                newSectionQuestionsList.add(sectionQuestionsList.get(j));
                            }
                        }
                        if (newSectionQuestionsList.size() > 0)
                            sectionQuestionsDao.batchInsertSectionQuestions(newSectionQuestionsList);//大题试题关系
                    }
                }
                sectionDao.batchInsertSection(sectionList);//添加到大题表
            }
        }
    }

    @Override
    public String exportPaper(String fileName, String ids) throws Exception {
        //获取试卷导出业务目录
        String tempPath = SystemConfig.getExportPaperTempPath();
        //导出临时根目录及导出eps包下载地址
        String exportDir = null,exportFilePath,exportFileName = fileName + FileConstants.EXPORT_PAPER_PACKAGE_ZIP_SUFFIX;
        //导出临时试卷目录(用于记录删除)
        String[] paperDirs;
        try {
            //创建本次导出临时文件夹(临时处理根目录/uuid命名文件夹)
            exportDir = SystemConfig.getStaticUploadBasePath() + tempPath;
            //查询试卷列表信息
            List<Paper> paperList = paperDao.listPaperByIds(new Paper(ids));
            //本次打包的试卷list
            String[] fileList = new String[paperList.size()];
            paperDirs = new String[paperList.size()];
            //循环试卷获取试卷内容
            for (int i = 0; i < paperList.size(); i++) {
                Paper p = paperList.get(i);
                //生成当前试卷临时处理唯一文件夹
                String baseDir = exportDir + p.getId() + FileConstants.FILE_SEPARATOR;
                //记录试卷临时文件夹(用于导出完成删除)
                paperDirs[i] = baseDir;
                //创建文件夹
                FileUtil.createDirOrFile(baseDir,false);
                //创建资源目录
                String resourceDir = baseDir + FileConstants.EXPORT_PAPER_RESOURCE_NAME;
                FileUtil.createDirOrFile(resourceDir,false);
                //实例化dom4j
                Document document = DocumentHelper.createDocument();
                //创建文件试卷节点
                Element paperPoint = document.addElement("paper");
                //写入试卷信息
                wirtePaperInfo(paperPoint, p);
                //查询试卷下章节信息列表
                List<Chapter> chapterList = chapterDao.listChapter(new Chapter(p.getId()));
                //创建章节列表节点
                Element topicPoint = paperPoint.addElement("topicList");
                for (Chapter c : chapterList) {
                    //创建章节列表下章节节点
                    Element topic = topicPoint.addElement("topic");
                    //写入章节列表下节点信息
                    wirteTopicInfo(topic, c);
                    //查询章节下大题信息列表
                    List<Section> sectionList = sectionDao.listSectionByPaperId(p.getId(), c.getId());
                    //创建大题列表节点
                    Element sectionPoint = topic.addElement("sectionList");
                    for (Section s : sectionList) {
                        //创建大题列表下大题节节点
                        Element section = sectionPoint.addElement("section");
                        //写入大题信息
                        wirteSectionInfo(section, s);
                        //查询大题下小题信息
                        List<Question> questionList = questionDao.listQuestionBySectionId(s.getId());
                        //创建小题列表节点
                        Element questionPoint = section.addElement("questionList");
                        for (Question q : questionList) {
                            //创建大题列表下小题节点
                            Element question = questionPoint.addElement("question");
                            //写入小题列表下小题节点信息
                            wirteQuestionInfo(question, q, s.getScore());
                            //查询试题资源(1-N个)
                            List<QuestionResource> resourceList = resourceDao.listQuestionResource(new QuestionResource(q.getId()));
                            //有试题次源则生成试题资源list节点及资源信息
                            if(resourceList.size() > 0) {
                                //创建小题列表节点
                                Element resourcesPoint = question.addElement("resourceList");
                                for (QuestionResource r : resourceList) {
                                    //创建资源列表下资源节点
                                    Element resource = resourcesPoint.addElement("resource");
                                    //写入资源列表下资源节点信息
                                    writeQuestionResource(resource, r);
                                    //复到资源文件至当前试卷临时目录resource文件夹下
                                    FileUtil.copyFile(SystemConfig.getStaticUploadBasePath() + r.getPath(), resourceDir + r.getPath(), true);
                                }
                            }
                        }
                    }
                }
                //生成xml文件
                //实例化输出格式对象
                OutputFormat format = OutputFormat.createPrettyPrint();
                //设置输出编码
                format.setEncoding(FileConstants.FILE_ENCODING);
                //生成-当前试卷paper.xml文件路径
                String paperXmlDir = baseDir + FileConstants.EXPORT_PAPER_XML_NAME;
                //创建需要写入的File对象
                File file = new File(paperXmlDir);
                //生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
                XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
                //开始写入，write方法中包含上面创建的Document对象
                writer.write(document);
                writer.flush();
                writer.close();
                //打包-当前试卷压缩包名称
                String paperPkgDir = baseDir + p.getId() + FileConstants.EXPORT_PAPER_ZIP_SUFFIX;
                //将资源文件夹resource及paper.xml文件打包成压缩包
                ZipUtil.compressZip(paperPkgDir,new String[]{paperXmlDir},new String[]{resourceDir}, null);
                //将当前压缩的试卷包文件路径存入数组中，用于压缩成总包
                fileList[i] = paperPkgDir;
            }
            //试卷包文件输出路径
            String PkgPath = exportDir + exportFileName;
            //将试卷包打包(一个eps包下包含1-N个epp包)
            ZipUtil.compressZip(PkgPath, fileList, null, null);
            //循环删除除eps包的其它生成文件
            for (String dir : paperDirs) {
                FileUtil.deleteDirectory(dir);
            }
            exportFilePath = SystemConfig.getStaticShowPath() + tempPath + exportFileName;
        }catch(BizException biz) {
            //异常，删除临时生成的文件夹
            FileUtil.deleteDirectory(exportDir);
            throw biz;
        }catch (Exception e) {
            //异常，删除临时生成的文件夹
            FileUtil.deleteDirectory(exportDir);
            throw e;
        }
        return exportFilePath;
    }

    /**
     * 写入试卷节点信息
     *
     * @param paperPoint
     * @param p
     */
    public void wirtePaperInfo(Element paperPoint, Paper p) {
        Element paperId = paperPoint.addElement("id");                         //试卷id
        Element paperName = paperPoint.addElement("name");                     //试卷名称
        Element paperTime = paperPoint.addElement("length");                   //试卷时长
        Element totalScore = paperPoint.addElement("totalScore");              //试卷总分
        Element paperPassScore = paperPoint.addElement("passScore");           //试卷及格分数
        Element paperCreater = paperPoint.addElement("creater");               //试卷创建者id
        Element createTime = paperPoint.addElement("createTime");              //创建时间

        //写入节点内容
        paperId.setText(p.getId());
        paperName.setText(p.getName());
        //试卷备注/说明
        if (StringUtils.isNotBlank(p.getRemark())) {
            Element paperRemark = paperPoint.addElement("remark");
            paperRemark.setText(p.getRemark());
        }
        paperTime.setText(p.getLength());
        totalScore.setText(p.getTotalScore());
        paperPassScore.setText(p.getPassScore());
        paperCreater.setText(p.getCreater());
        createTime.setText(p.getCreatetime());

        //创建科目节点
        Element subjectPoint = paperPoint.addElement("subject");
        //获取科目信息
        Subject subject = subjectDao.getSubjectById(p.getSubjectId(),null);
        Element subjectId = subjectPoint.addElement("id");                //科目id
        Element subjecctName = subjectPoint.addElement("name");           //科目名称
        subjectId.setText(subject.getId());
        subjecctName.setText(subject.getName());
    }

    /**
     * 写入章节列表下章节节点信息
     *
     * @param topic
     * @param c
     */
    public void wirteTopicInfo(Element topic, Chapter c) {
        //写入章节节点
        Element index = topic.addElement("index");
        Element topicId = topic.addElement("id");
        Element topicName = topic.addElement("name");
        Element topicScore = topic.addElement("score");
        Element topicRemark = topic.addElement("remark");
        //写入章节内容
        index.setText(c.getSort());
        topicId.setText(c.getId());
        topicName.setText(c.getName());
        if (StringUtils.isNotBlank(c.getScore())) {
            topicScore.setText(c.getScore());
        }
        topicRemark.setText(c.getRemark());
    }

    /**
     * 写入大题列表下大题节点信息
     *
     * @param section
     * @param s
     */
    public void wirteSectionInfo(Element section, Section s) {
        //写入章节节点
        Element index = section.addElement("index");
        Element sectionId = section.addElement("id");
        Element sectionName = section.addElement("name");
        Element sectionScore = section.addElement("score");
        Element sectionRemark = section.addElement("remark");
        //写入章节内容
        index.setText(s.getSort());
        sectionId.setText(s.getId());
        sectionName.setText(s.getName());
        sectionScore.setText(s.getScore());
        sectionRemark.setText(s.getRemark());
    }

    /**
     * 写入试题列表下试题节点信息
     *
     * @param question
     * @param q
     * @param score    当前小题分数
     * @throws Exception
     */
    public void wirteQuestionInfo(Element question, Question q, String score) throws Exception {
        //读取试题文件内容
        QuestionContent content = getQuestionXmlInfo(SystemConfig.getStaticUploadBasePath() + q.getContextPath());
        //写入小题节点
        Element questionAnalysis = null;
        Element index = question.addElement("index");
        Element questionId = question.addElement("id");
        Element type = question.addElement("type");         //1主观题，2客观题
        Element questionType = question.addElement("questionType");
        Element questionContent = question.addElement("content");
        Element questionItemStr = question.addElement("itemStr");
        if(StringUtils.isNotBlank(content.getQuestionanAlysis())) {
            questionAnalysis = question.addElement("analysis");
        }
        Element questionAnswer = question.addElement("answer");
        Element questionScore = question.addElement("score");
        Element questionDiff = question.addElement("difficulty");
        //写入小题内容
        index.setText(q.getSort());
        questionId.setText(q.getId());
        type.setText(q.getType());
        questionType.setText(content.getQuestiontype());
        questionContent.setText(content.getQuestionContent());
        questionItemStr.setText(content.getQuestionItemStr());
        if(StringUtils.isNotBlank(content.getQuestionanAlysis())){
            questionAnalysis.setText(content.getQuestionanAlysis());
        }
        questionAnswer.setText(content.getQuestionAnswerStr());
        questionScore.setText(score);
        questionDiff.setText(q.getDifficulty());
    }

    /**
     * 写入资源列表下资源节点信息
     *
     * @param resource
     * @param r
     */
    private void writeQuestionResource(Element resource, QuestionResource r) {
        //写入章节节点
        Element resourceId = resource.addElement("id");
        Element resourceName = resource.addElement("name");
        Element fileDir = resource.addElement("fileDir");
        //写入章节内容
        resourceId.setText(r.getId());
        resourceName.setText(r.getName());
        fileDir.setText("/resource" + r.getPath());
    }

    /**
     * 解析question.xml文件内容 读取题干信息
     *
     * @param path
     * @return
     * @throws Exception
     */
    public QuestionContent getQuestionXmlInfo(String path) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element eleroot = document.getRootElement();
        //获取根节点作为迭代器
        Iterator iter = eleroot.elementIterator();
        QuestionContent questionContent = new QuestionContent();
        while (iter.hasNext()) {
            Element element = (Element) iter.next();
            if ("question_type".equals(element.getName())) {
                questionContent.setQuestionType(element.getText());
            } else if ("question_content".equals(element.getName())) {
                questionContent.setQuestionContent(element.getText());
            } else if ("question_analysis".equals(element.getName())) {
                questionContent.setQuestionanAlysis(element.getText());
            } else if ("question_itemstr".equals(element.getName())) {
                questionContent.setQuestionItemStr(element.getText());
            } else if ("question_answerstr".equals(element.getName())) {
                questionContent.setQuestionAnswerStr(element.getText());
            }
        }
        return questionContent;
    }

    /**
     * 获取题xml文件路径
     */
    private String getXmlPath(String quesitonId, String subjectId) {
        String relPath = SystemConfig.getQuestionXmlSavePath(subjectId);
        //获取静态资源上传地址+xml文件保存相对地址+xml文件名
        String xmlFullPath = SystemConfig.getStaticUploadBasePath() + relPath + quesitonId + ".xml";
        return xmlFullPath;
    }

    public List<Paper> listPaperByIds(Paper paper) {
        return paperDao.listPaperByIds(paper);
    }

    /**
     * 正则表达式判断
     *
     * @param reg 表达式规则
     * @param str 要判断的字符串
     */
    public static boolean checkData(String reg, String str) {
        boolean val = false;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        val = matcher.matches();
        return val;
    }

    /**
     * 判断含最多有两位小数的数值或整数值
     */
    public static boolean isNum(String val) {
        //^[0-9]:以数字开始
        //(）:一个域段(用以让?起作用)
        //.:匹配除\n外的任意字符
        //{2}:循环0-2次
        //?:()中的内容重复0次或一次（有两位或没有小数）
        String reg1 = "^[0-9]+(.[0-9]{0,2})?$";
        return checkData(reg1, val);
    }
}
