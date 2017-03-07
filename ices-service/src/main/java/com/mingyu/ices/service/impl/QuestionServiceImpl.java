package com.mingyu.ices.service.impl;

import com.mingyu.ices.config.SystemConfig;
import com.mingyu.ices.constant.EnumOrdinal;
import com.mingyu.ices.constant.FileConstants;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.dao.IQuestionDao;
import com.mingyu.ices.dao.IQuestionKnowDao;
import com.mingyu.ices.dao.IQuestionResourceDao;
import com.mingyu.ices.dao.ISectionQuestionsDao;
import com.mingyu.ices.domain.common.BizDataPage;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.domain.dto.user.QuestionResponse;
import com.mingyu.ices.domain.po.*;
import com.mingyu.ices.service.IQuestionService;
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

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * QuestionsServiceImpl
 * 试题相关serviceImpl
 * @author yuhao
 * @date 2016/6/30
 */
@Service
public class QuestionServiceImpl implements IQuestionService {

    private static Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);
    //替换代替符
    private static String httpRplStr = "#httpReplaceStr#";
    //发布路径用以展示(替换成代替符)
    private static String rplStr = SystemConfig.getStaticShowPath();

    @Autowired
    IQuestionDao questionDao;
    @Autowired
    IQuestionKnowDao questionKnowDao;
    @Autowired
    IQuestionResourceDao questionResourceDao;
    @Autowired
    ISectionQuestionsDao sectionQuestionsDao;

    @Override
    public BizDataPage<QuestionResponse> pageQuestion(Question question,Integer page) {
        question.setStatus(String.valueOf(EnumOrdinal.normal.getValue()));
        BizDataPage bizDataPage=new BizDataPage();
        bizDataPage.setPage(page);
        List<QuestionResponse> questionList =questionDao.pageQuestion(question,bizDataPage.getOffset(),bizDataPage.getPagesize());
        for(int i=0 ;i< questionList.size();i++){
            String questionName= questionList.get(i).getName();
            questionList.get(i).setName(questionName.replaceAll(httpRplStr,rplStr));
        }
        Integer records=questionDao.countQuestion(question);
        bizDataPage.setRows(questionList);
        bizDataPage.setRecords(records);
        return bizDataPage;
    }

    /**
     * 获取试题所有内容
     * @param Id
     * @return QuestionResponse
     */
    @Override
    public QuestionResponse getQuestionDetail(String Id) throws Exception {
        QuestionResponse questionDetail = questionDao.getQuestionDetail(Id);
        questionDetail.setName(questionDetail.getName().replaceAll(httpRplStr,rplStr));
        //获取静态资源上传地址+xml文件保存相对地址+xml文件名
        String xmlFullPath = getXmlPath(questionDetail.getId(),questionDetail.getSubjectId());
        //判断xml文件是否存在
        File file = new File(xmlFullPath);
        if (!file.exists()) {
            throw new BizException(WebConstants.ERROR,"试题xml文件不存在，文件路径：" + xmlFullPath);
        }
        //获取题目xml内容
        QuestionContent questionContent = getQuestionXmlInfo(xmlFullPath);
        questionDetail.setQuestionContent(questionContent);
        //获取题目知识点
        List<QuestionKnow> questionKnowList = getQuestionKnow(questionDetail.getId());
        questionDetail.setQuestionKnowList(questionKnowList);
        return questionDetail;
    }

    /**
     * 添加试题信息
     * @param question
     * @return
     */
    public void addQuestion(Question question){
        questionDao.addQuestion(question);
    }

    /**
     * 添加试题知识点信息
     * @param questionKnow
     * @return
     */
    public void addQuestionKnow(QuestionKnow questionKnow){
        questionKnowDao.addQuestionKnow(questionKnow);
    }

    /**
     * 添加试题内容
     * @param questionContent
     * @return
     */
    @Override
    public void addQuestionContent(QuestionContent questionContent,String xmlFilePath) {
        File file = new File(xmlFilePath);
        if (file.exists()){
            if (!file.delete()){
                throw new BizException(WebConstants.ERROR,"添加试题内容时xml文件已存在，并且无法被覆盖");
            }
        }
        try {
            //添加xml文件内容
            Document document = DocumentHelper.createDocument();
            //创建根节点
            Element root = document.addElement("question");
            //添加子节点
            Element eleQuestionType = root.addElement("question_type");
            Element eleQuestionContent = root.addElement("question_content");
            Element eleQuestionAnalysis = root.addElement("question_analysis");
            Element eleQuestionItemstr = root.addElement("question_itemstr");
            Element eleQuestionAnswerstr = root.addElement("question_answerstr");
            //添加子节点内容
            eleQuestionType.setText(questionContent.getQuestiontype());
            /*
              将保存在资源文件中的URL(rplStr)地址中的发布路径替换成
              占位符*httpReplaceStr*，以便于以后项目移植后能替换成新的发布路径
              来访问到资源文件
             */
            eleQuestionContent.setText(questionContent.getQuestionContent().replaceAll(rplStr,httpRplStr));
            if (questionContent.getQuestionanAlysis()==null){questionContent.setQuestionanAlysis("");}
            eleQuestionAnalysis.setText(questionContent.getQuestionanAlysis().replaceAll(rplStr, httpRplStr));
            if (questionContent.getQuestionItemStr()==null){questionContent.setQuestionItemStr("");}
            eleQuestionItemstr.setText(questionContent.getQuestionItemStr().replaceAll(rplStr,httpRplStr));
            if (questionContent.getQuestionAnswerStr()==null){questionContent.setQuestionAnswerStr("");}
            eleQuestionAnswerstr.setText(questionContent.getQuestionAnswerStr().replaceAll(rplStr, httpRplStr));
            //创建输出格式化
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            //因有中文输出需要设置为GBK编码
            outputFormat.setEncoding(FileConstants.FILE_ENCODING);
            File outfile = new File(xmlFilePath);
            XMLWriter writer = new XMLWriter(new FileOutputStream(outfile), outputFormat);
            writer.write(document);
            writer.close();
        }catch (UnsupportedEncodingException e){
            throw new BizException(WebConstants.ERROR,"设置试题xml文件编码发生异常",e.getMessage());
        }catch (IOException e){
            throw new BizException(WebConstants.ERROR,"创建试题xml文件发生异常",e.getMessage());
        }
    }

    /**
     * 添加试题详细信息
     * @param questionResponse
     * @return
     */
    @Override
    @Transactional
    public void addQuestionDetail(QuestionResponse questionResponse){
        questionResponse.setId(UUID.randomUUID().toString());
        questionResponse.setStatus(String.valueOf(EnumOrdinal.normal.getValue()));
        questionResponse.setCheckStatus(String.valueOf(EnumOrdinal.statusUnchecked.getValue()));
        String relPath = SystemConfig.getQuestionXmlSavePath(questionResponse.getSubjectId());
        //获取静态资源上传地址+xml文件保存相对地址+xml文件名
        String xmlFullPath = getXmlPath(questionResponse.getId(),questionResponse.getSubjectId());
        //生成文件路径
        if (!createFilePath(xmlFullPath)){
            throw new BizException(WebConstants.ERROR,"生成文件路径失败");
        }
        //添加题信息
        questionResponse.setContextPath(relPath+questionResponse.getId()+FileConstants.XML_FILE_SUFFIX);
        questionResponse.setName(questionResponse.getName().replaceAll(rplStr,httpRplStr));
        addQuestion(questionResponse);
        List<QuestionKnow> questionKnowList = questionResponse.getQuestionKnowList();
        //添加题知识点信息
        for (QuestionKnow questionKnow:questionKnowList){
            questionKnow.setId(UUID.randomUUID().toString());
            questionKnow.setQuestionId(questionResponse.getId());
            questionKnow.setCreater(questionResponse.getCreater());
            addQuestionKnow(questionKnow);
        }
        //添加题干信息
        addQuestionContent(questionResponse.getQuestionContent(),xmlFullPath);
        //添加资源信息
        addQuestionResource(questionResponse);
    }

    /**
     * 修改题目信息
     * @param questionResponse
     * @return
     */
    @Override
    @Transactional
    public void updateQuestionDetail(QuestionResponse questionResponse) throws Exception{
        //更新试题信息
        questionResponse.setName(questionResponse.getName().replaceAll(rplStr,httpRplStr));
        questionDao.updateQuestion(questionResponse);
        //更新试题知识点
        updateQuestionKnow(questionResponse.getId(),questionResponse.getQuestionKnowList(),questionResponse.getModifier());
        //获取静态资源上传地址+xml文件保存相对地址+xml文件名
        String xmlFullPath = getXmlPath(questionResponse.getId(),questionResponse.getSubjectId());
        //更新题干内容
        updateQuestionContent(questionResponse.getQuestionContent(),xmlFullPath);
        //更新试题资源信息
        addQuestionResource(questionResponse);
    }

    /**
     * 获取题目知识点信息
     * @param questionId
     * @List<QuestionKnow>
     */
    public List<QuestionKnow> getQuestionKnow(String questionId){
        List<QuestionKnow> questionKnowList = questionKnowDao.getQuestionKnow(questionId);
        return questionKnowList;
    }

    /**
     * 保存题目信息时更新知识点信息
     * @param questionId,QuestionKnow
     * @return
     * 目前业务逻辑规定题目必有所属的知识点
     * 但是这个强制性要求很有可能不符合客户
     * 的真实需求，故在这里仍然传questionId
     * 进来，以防以后知识点可不选时，questionknowlist中没有记录,无法取到questionId
     */
    @Transactional
    public void updateQuestionKnow(String questionId, List<QuestionKnow> questionKnowList, String userId){
        //先删除该题所属的知识点信息
        questionKnowDao.delQuestionKnow(questionId);
        //再添加所属的知识点信息
        if (!questionKnowList.isEmpty()){
            for (QuestionKnow questionKnow : questionKnowList) {
                questionKnow.setId(UUID.randomUUID().toString());
                questionKnow.setCreater(userId);
                questionKnow.setQuestionId(questionId);
                questionKnowDao.addQuestionKnow(questionKnow);
            }
        }
    }

    /**
     * 解析question.xml文件内容 读取题干信息
     * @param path
     * @return
     * @throws Exception
     */
    public QuestionContent getQuestionXmlInfo(String path) throws Exception {
        //读取XML文件,获得document对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element eleroot = document.getRootElement();
        //String rplStr = ZKConfig.getStaticShowPath();

        //获取根节点作为迭代器
        Iterator iter = eleroot.elementIterator();
        QuestionContent questionContent = new QuestionContent();
        while (iter.hasNext()){
            Element element = (Element) iter.next();
            if ("question_type".equals(element.getName())){
                questionContent.setQuestionType(element.getText());
            }else if ("question_content".equals(element.getName())){
                questionContent.setQuestionContent(element.getText().replaceAll(httpRplStr, rplStr));
            }else if ("question_analysis".equals(element.getName())){
                questionContent.setQuestionanAlysis(element.getText().replaceAll(httpRplStr, rplStr));
            }else if ("question_itemstr".equals(element.getName())){
                questionContent.setQuestionItemStr(element.getText().replaceAll(httpRplStr, rplStr));
            }else if ("question_answerstr".equals(element.getName())){
                questionContent.setQuestionAnswerStr(element.getText().replaceAll(httpRplStr, rplStr));
            }
        }
        return questionContent;
    }

    /**
     * 更新题目xml内容
     * @param questionContent
     * @return
     */
    public void updateQuestionContent(QuestionContent questionContent,String xmlFilePath) throws Exception{
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(xmlFilePath));
        Element eleroot = document.getRootElement();

        eleroot.element("question_type").setText(questionContent.getQuestiontype());
        eleroot.element("question_content").setText(questionContent.getQuestionContent().replaceAll(rplStr, httpRplStr));
        if (questionContent.getQuestionanAlysis()==null){questionContent.setQuestionanAlysis("");}
        eleroot.element("question_analysis").setText(questionContent.getQuestionanAlysis().replaceAll(rplStr,httpRplStr));
        if (questionContent.getQuestionItemStr()==null){questionContent.setQuestionItemStr("");}
        eleroot.element("question_itemstr").setText(questionContent.getQuestionItemStr().replaceAll(rplStr, httpRplStr));
        if (questionContent.getQuestionAnswerStr()==null){questionContent.setQuestionAnswerStr("");}
        eleroot.element("question_answerstr").setText(questionContent.getQuestionAnswerStr().replaceAll(rplStr, httpRplStr));
        //创建文档格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(FileConstants.FILE_ENCODING);
        //保存xml文件
        File outfile = new File(xmlFilePath);
        XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(outfile), format);
        try {
            //输出文件
            xmlWriter.write(document);
        }finally{
            xmlWriter.close();
        }
    }

    /**
     * 更新题目题目审核状态
     * @param id
     * @param checkStatus
     * @param user
     */
    public void updateQuestionCheckStatus(String id,String checkStatus,User user){
        Question question = new Question();
        question.setId(id);
        question.setCheckStatus(checkStatus);
        question.setModifier(user.getId());
        questionDao.updateQuestionCheckStatus(question);
    }

    /**
     * 删除试题信息
     * @param question
     * @return
     */
    @Transactional
    public void delQuestion(Question question){

        Integer countNumber=sectionQuestionsDao.countByQuestionId(question.getId());
        if (countNumber>0){
            throw new BizException(WebConstants.EXISTS_RELATED_DATA);
        }
        question.setStatus(String.valueOf(EnumOrdinal.delete.getValue()));
        //删除试题信息
        questionDao.delQuestion(question);
        //删除试题资源信息
        QuestionResource questionResource = new QuestionResource();
        questionKnowDao.deleteByQuestionId(question.getId());
        questionResource.setQuestionId(question.getId());
        questionResource.setModifier(question.getModifier());
        questionResource.setStatus(String.valueOf(EnumOrdinal.delete.getValue()));
        questionResourceDao.delQuestionResource(questionResource);
    }

    /**
     * 复制试题信息
     * @param question
     * @return
     */
    @Transactional
    public void copyQuestion(Question question) throws Exception{
        //复制试题内容
        question.setNewquestionId(UUID.randomUUID().toString());
        String relPath = SystemConfig.getQuestionXmlSavePath(question.getSubjectId());
        String oldXmlPath = getXmlPath(question.getId(), question.getSubjectId());
        String newXmlPath = getXmlPath(question.getNewquestionId(),question.getSubjectId());
        //复制xml文件
        File oldFile = new File(oldXmlPath);
        File newFile = new File(newXmlPath);
        //生成文件路径
        if (!createFilePath(newXmlPath)){
            throw new BizException(WebConstants.ERROR,"生成文件路径失败");
        }
        fileChannelCopy(oldFile,newFile);
        question.setContextPath(relPath+question.getNewquestionId()+FileConstants.XML_FILE_SUFFIX);
        questionDao.copyQuestionByQuestionId(question);
        //复制知识点内容
        QuestionKnow questionKnow = new QuestionKnow();
        questionKnow.setQuestionId(question.getId());
        questionKnow.setCreater(question.getCreater());
        questionKnow.setNewquestionId(question.getNewquestionId());
        questionKnowDao.copyQuestionKnowByQuestionId(questionKnow);
        //复制试题资源内容
        QuestionResource questionResource = new QuestionResource();
        questionResource.setQuestionId(question.getId());
        questionResource.setCreater(question.getCreater());
        questionResource.setNewquestionId(question.getNewquestionId());
        questionResourceDao.copyQuestionResourceByQuestionId(questionResource);
    }

    /**
     * 添加资源信息
     * @param
     */
    @Transactional
    public void addQuestionResource(QuestionResponse questionResponse){
        //先删除该题目的所有资源信息
        questionResourceDao.delQuestionResourceByQuestionId(questionResponse.getId());
        //添加资源信息
        for (QuestionResource questionResource : questionResponse.getQuestionResourceList()){
            questionResource.setId(UUID.randomUUID().toString());
            questionResource.setQuestionId(questionResponse.getId());
            questionResource.setCreater(questionResponse.getCreater()==null?questionResponse.getModifier():questionResponse.getCreater());
            questionResource.setPath(questionResource.getPath().replaceAll(rplStr,""));
            questionResourceDao.addQuestionResource(questionResource);
        }
    }

    /**
     * 获取题xml文件路径
     */
    private String getXmlPath(String quesitonId,String subjectId){
        String relPath = SystemConfig.getQuestionXmlSavePath(subjectId);
        //获取静态资源上传地址+xml文件保存相对地址+xml文件名
        String xmlFullPath = SystemConfig.getStaticUploadBasePath() + relPath + quesitonId + FileConstants.XML_FILE_SUFFIX;
        return xmlFullPath;
    }

    /**
     * 使用文件通道的方式复制文件
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public void fileChannelCopy(File s, File t) throws Exception {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try{
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } finally {
            fi.close();
            in.close();
            fo.close();
            out.close();
        }
    }

    /**
     * 创建文件目录
     * @param filePath
     */
    private boolean createFilePath(String filePath){
        boolean isCreated = true;
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (fileParent!=null && !fileParent.exists()){
            isCreated = fileParent.mkdirs();
        }
        return isCreated;
    }
}
