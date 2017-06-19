package com.lnwazg.mydict.bean;

import com.lnwazg.kit.json.GsonCfgMgr;

/**
 * 系统配置信息
 * @author nan.li
 * @version 2014-11-17
 */
public class SystemConfig
{
    //是否打开了单词本
    private boolean openWordbook;
    
    //是否自动发音
    private boolean autoSpeak;
    
    //是否自动查询
    private boolean autoQuery;
    
    public boolean isAutoQuery()
    {
        return autoQuery;
    }
    
    public void setAutoQuery(boolean autoQuery)
    {
        this.autoQuery = autoQuery;
    }
    
    public boolean isOpenWordbook()
    {
        return openWordbook;
    }
    
    public void setOpenWordbook(boolean openWordbook)
    {
        this.openWordbook = openWordbook;
    }
    
    public boolean isAutoSpeak()
    {
        return autoSpeak;
    }
    
    public void setAutoSpeak(boolean autoSpeak)
    {
        this.autoSpeak = autoSpeak;
    }
    
    /**
     * 快捷好用的帮助类，可快速存取属性
     * @author Administrator
     * @version 2016年4月10日
     */
    public static class Helper
    {
        public static boolean isOpenWordbook()
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig != null)
            {
                return systemConfig.isOpenWordbook();
            }
            return true;
        }
        
        public static void setOpenWordbook(boolean openWordbook)
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig == null)
            {
                systemConfig = new SystemConfig();
            }
            systemConfig.setOpenWordbook(openWordbook);
            GsonCfgMgr.writeObject(systemConfig);
        }
        
        public static boolean isAutoSpeak()
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig != null)
            {
                return systemConfig.isAutoSpeak();
            }
            return false;
        }
        
        public static void setAutoSpeak(boolean autoSpeak)
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig == null)
            {
                systemConfig = new SystemConfig();
            }
            systemConfig.setAutoSpeak(autoSpeak);
            GsonCfgMgr.writeObject(systemConfig);
        }
        
        public static void setAutoQuery(boolean autoQuery)
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig == null)
            {
                systemConfig = new SystemConfig();
            }
            systemConfig.setAutoQuery(autoQuery);
            GsonCfgMgr.writeObject(systemConfig);
        }
        
        public static boolean isAutoQuery()
        {
            SystemConfig systemConfig = GsonCfgMgr.readObject(SystemConfig.class);
            if (systemConfig != null)
            {
                return systemConfig.isAutoQuery();
            }
            return false;
        }
    }
}
