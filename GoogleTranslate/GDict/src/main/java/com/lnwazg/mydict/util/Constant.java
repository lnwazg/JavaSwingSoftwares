package com.lnwazg.mydict.util;

import java.awt.Font;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.lnwazg.kit.file.FileKit;

/**
 * 常量类
 * @author  nan.li
 * @version 2013-12-26
 */
public class Constant
{
    /**
     * 用户目录<br>
     * 老目录不再使用<br>
     * 示例：C:/Windows/GoogleTranslate/
     */
    public static String USER_DIR = "C:/Windows/GoogleTranslate/";
    
    static
    {
        if (!new File(USER_DIR).exists())
        {
            //照顾到老用户
            //只有当老目录不存在的时候，才尝试创建自适应的新目录
            //如果老目录存在，则必定是老版本的windows用户
            USER_DIR = FileKit.getConfigBasePathForAll() + "GoogleTranslate/";
        }
    }
    
    /**
     * 故障备份目录
     */
    public static String USER_DIR_FAILSSAFE = System.getProperty("user.dir") + File.separator + "GTconfig";
    
    /**
     * 音频文件目录
     */
    public static String AUDIO_DIR = Constant.USER_DIR + "AUDIO" + File.separator;
    
    /**
     * 最常查询的单词列表-显示个数
     */
    public static final int FREQ_NUM = 5;
    
    /**
     * 复习的单词列表-显示个数
     */
    public static final int REVIEW_NUM = 3;
    
    /**
     * 特殊单词（无法被windows识别为合法字符文件名称的单词列表）
     */
    public static List<String> SPECIAL_WORDS = Arrays.asList("con");
    
    /**
     * 单词自动查询的延迟毫秒数<br>
     * 在这段间隔时间内如果没有输入动作，就会触发一次单词查询操作
     */
    public static long QUERY_DELAY_MILLSECONDS = 1750;//若DELAY时间用户什么都没操作（没有新增或者删除文字），则将当前输入框中的单词加入到单词本中;
    
    /**
     * 切换到小字体的字数限制
     */
    public static int ChangeToSmallFontWordLimit = 20;
    
    public static final Font BigFont = new Font(Font.SERIF, Font.ITALIC, 24);
    
    public static final Font SmallFont = new Font(Font.SERIF, Font.ITALIC, 16);
    
    //======================================================================
    public static final String JS_TPL_CONFIG = "js/dict.js";
    
    public static final String HTML_TPL_CONFIG = "template/target.html";
    
    public static final String TITLE_REMARK = "惊鸿一瞥 ™";
    
    public static final String UTF8_ENCODING = "UTF-8";
    
    public static String JS_TEMPLATE = "";
    
    public static String HTML_TEMPLATE = "";
    
    /**
     * 海词已经成功请求过
     */
    static final String DICTCN_VISITED = "<DICTCN_VISITED/>";
    
    /**
     * 图片已经请求过
     */
    static final String IMGVISITED = "<IMGVISITED/>";
    
    /**
     * 请求网络失败的标记
     */
    static final String MSG_NET_FAIL = "MSG_NET_FAIL";
    
    /**
     * 插图的最大尺寸
     */
    static final int IMAGE_MAX_DIMEN = 187;
    
    public static final int IMG_SERVER_PORT = 1701;
    
    public static final String IMG_SERVER_CONTEXT_PATH = "/image";
    
    public static final String IMG_FOLDER = "image";
    
    public static final String WORD_IMAGE_SUFFIX = ".jpg";
    
    /**
     * 忽略查询图片的单词列表
     */
    public static final List<String> DO_NOT_QUERY_IMG_WORDS = Arrays.asList("about", "penis");
}
