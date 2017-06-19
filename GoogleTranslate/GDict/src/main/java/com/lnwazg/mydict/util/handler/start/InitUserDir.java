package com.lnwazg.mydict.util.handler.start;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.util.Constant;

/**
 * 初始化用户的工作目录
 * @author Administrator
 * @version 2016年4月15日
 */
public class InitUserDir implements IHandler
{
    @Override
    public void handle()
    {
        try
        {
            File dir = new File(Constant.USER_DIR);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            File userFile = new File(dir.getPath() + File.separator + ".configDir");
            FileUtils.writeStringToFile(userFile, "Configs should be saved here!");
            
            File imgDir = new File(Constant.USER_DIR + Constant.IMG_FOLDER);
            if (!imgDir.exists())
            {
                imgDir.mkdirs();
            }
        }
        catch (Exception e)
        {
            System.out.println(String.format("系统目录：%s 创建失败，智能切换到用户目录中！", Constant.USER_DIR));
            Constant.USER_DIR = Constant.USER_DIR_FAILSSAFE;//智能切换
            System.out.println(String.format("当前的用户目录为:%s", Constant.USER_DIR));
        }
    }
    
}
