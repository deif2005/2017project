package com.mingyu.ices.constant;

/**
 * WebConstants
 * web常量类，用于返回页面相关code
 * @author yuhao
 * @date 2016/6/30
 */
public class WebConstants {
    /**
     * 用户会话信息常量key
     */
    public static final String USER_INFO = "USER_INFO";

    /**
     * 成功常量返回code
     */
    public static final String SUCCESS = "000000";

    /**
     * 会话超时返回code
     */
    public static final String SESSION_INVALID = "999998";

    /**
     * 异常常量返回code
     */
    public static final String ERROR = "999999";

    /**
     *请求参数错误(为空、长度、规则错误等绕过前端验证请求后台的非法请求统一提示参数错误，业务需要提示具体信息可单独定义code)
     */
    public static final String REQUEST_PARAMETER_ERROR = "000001";

    /**
     * 上传文件格式错误(文件后缀不匹配)
     */
    public static final String UPLOAD_FILE_FORMAT_ERROR = "000002";

    /**
     * 上传文件为空/没有上传文件
     */
    public static final String UPLOAD_FILE_NULL_ERROR = "000003";

    /**
     * 上传文件压缩包内文件格式有误
     */
    public static final String UPLOAD_ZIP_FILE_FORMAT_ERROR = "000004";

    /**
     * 名字重复
     */
    public static final String NAME_REPETITION = "000005";

    /**
     * 压缩包密码错误
     */
    public static final String ZIP_PASSWORD_ERROR = "000006";
    /**
     * 用户账号不存在或密码错误
     */
    public static final String USER_ACCOUNT_OR_PASSWORD_ERROR = "010001";

    /**
     * 原始密码错误
     */
    public static final String ORIGINAL_PASSWORD_ERROR = "010002";

    /**
     * 题量过大
     */
    public static final String QUESTION_AMOUNT_ERROR="030001";

    /**
     * 存在关联数据
     */
    public static final String EXISTS_RELATED_DATA="030002";

    /**
     * 试卷名称过长
     */
    public static final String PAPER_NAME_ERROR="020001";

}
