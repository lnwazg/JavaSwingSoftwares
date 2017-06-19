package com.lnwazg.mydict.util.handler.start;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.util.Constant;

/**
 * 迁移用户目录
 * @author Administrator
 * @version 2016年4月16日
 */
public class MigrateUserDir implements IHandler
{
    @Override
    public void handle()
    {
        migrateV1_2UserDir();
    }
    
    /**
     * 迁移到V1.2版本的用户目录
     * @author Administrator
     */
    private void migrateV1_2UserDir()
    {
        //如果新的不存在或者不完整，并且老的都在，则迁移
        String[] newFiles = {"com.lnwazg.mydict.bean.SystemConfig", "com.lnwazg.mydict.bean.UserLevel", "com.lnwazg.mydict.bean.WordBook"};
        String newMemFile = "com.lnwazg.mydict.bean.MemCycle";
        String[] oldFiles = {"googletranslate.bean.SystemConfig", "googletranslate.bean.UserLevel", "googletranslate.bean.WordBook"};
        String oldMemFile = "googletranslate.bean.MemCycle";
        
        //如果新文件不存在
        if (!new File(Constant.USER_DIR, newFiles[0]).exists() || !new File(Constant.USER_DIR, newFiles[1]).exists()
            || !new File(Constant.USER_DIR, newFiles[2]).exists())
        {
            //目标目录有任意一方不存在
            System.out.println("即将从老版本中恢复配置信息...");
            
            //将源存在的一切都拷贝过去
            //并且如果老文件存在
            if (new File(Constant.USER_DIR, oldFiles[0]).exists())
            {
                //则复制过去
                try
                {
                    FileUtils.copyFile(new File(Constant.USER_DIR, oldFiles[0]), new File(Constant.USER_DIR, newFiles[0]));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (new File(Constant.USER_DIR, oldFiles[1]).exists())
            {
                try
                {
                    FileUtils.copyFile(new File(Constant.USER_DIR, oldFiles[1]), new File(Constant.USER_DIR, newFiles[1]));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (new File(Constant.USER_DIR, oldFiles[2]).exists())
            {
                try
                {
                    FileUtils.copyFile(new File(Constant.USER_DIR, oldFiles[2]), new File(Constant.USER_DIR, newFiles[2]));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            System.out.println("从老版本中恢复配置信息OK!");
        }
        
        //如果新的背诵词库不存在，并且老的背诵词库还存在，那么将老的拷贝到新的位置
        if (!new File(Constant.USER_DIR, newMemFile).exists() && new File(Constant.USER_DIR, oldMemFile).exists())
        {
            System.out.println("从老版本中恢复背诵词库...");
            try
            {
                FileUtils.copyFile(new File(Constant.USER_DIR, oldMemFile), new File(Constant.USER_DIR, newMemFile));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
    }
    
}
