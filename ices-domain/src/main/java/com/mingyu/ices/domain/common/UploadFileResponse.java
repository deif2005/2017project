package com.mingyu.ices.domain.common;

import java.io.Serializable;
import java.util.List;

/**
 * UploadFile
 * 文件上传
 * @author yuhao
 * @date 2016/8/19
 */
public class UploadFileResponse implements Serializable{

    private static final long serialVersionUID = -1968837645835754263L;

    //上传至服务器后的文件列表
    private List<String> fileList;

    //原文件名
    private List<String> fileNameList;

    /**
     * 获取文件列表中第一个文件路径
     * @return
     */
    public String getFirstFile(){
        if(fileList.size() > 0){
            return fileList.get(0);
        }else{
            return null;
        }
    }

    /**
     * 获取文件列表中第一个文件名称
     * @return
     */
    public String getFirstFileName(){
        if(fileNameList.size() > 0){
            return fileNameList.get(0);
        }else{
            return null;
        }
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<String> getFileNameList() {
        return fileNameList;
    }

    public void setFileNameList(List<String> fileNameList) {
        this.fileNameList = fileNameList;
    }
}
