package com.mingyu.ices.util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

/**
 * XmlUtil
 * xml操作工具类
 * @author yuhao
 * @date 2016/7/12
 */
public class XmlUtil {

    /**
     * 把document对象写入新的文件
     *
     * @param document
     * @throws Exception
     */
    public void writer(Document document) throws Exception {
        // 紧凑的格式
        // OutputFormat format = OutputFormat.createCompactFormat();
        // 排版缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置编码
        format.setEncoding("UTF-8");
        // 创建XMLWriter对象,指定了写出文件及编码格式
        // XMLWriter writer = new XMLWriter(new FileWriter(new
        // File("src//a.xml")),format);
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(
                new FileOutputStream(new File("src//a.xml")), "UTF-8"), format);
        // 写入
        writer.write(document);
        // 立即写入
        writer.flush();
        // 关闭操作
        writer.close();
    }

    public static void main(String[] args) throws Exception{
        try {
            Document document = DocumentHelper.createDocument();
            //写入文件试卷节点内容
            Element paperPoint = document.addElement("paper");
            Element paperId = paperPoint.addElement("paper_id");
            paperId.addText("测试......");

            /*//增加根节点
            Element books = doc.addElement("books");
            //增加子元素
            Element book1 = books.addElement("book");
            Element title1 = book1.addElement("title");
            Element author1 = book1.addElement("author");

            Element book2 = books.addElement("book");
            Element title2 = book2.addElement("title");
            Element author2 = book2.addElement("author");

            //为子节点添加属性
            book1.addAttribute("id", "001");
            //为元素添加内容
            title1.setText("Harry Potter");
            author1.setText("J K. Rowling");

            book2.addAttribute("id", "002");
            title2.setText("Learning XML");
            author2.setText("Erik T. Ray");*/

            //实例化输出格式对象
            OutputFormat format = OutputFormat.createPrettyPrint();
            //设置输出编码
            format.setEncoding("UTF-8");
            //创建需要写入的File对象
            File file = new File("D:" + File.separator + "books.xml");
            //生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            //开始写入，write方法中包含上面创建的Document对象
            writer.write(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
