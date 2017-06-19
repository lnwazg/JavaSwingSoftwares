package com.lnwazg.mydict.util.level;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.mydict.bean.UserLevel;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.WinMgr;

/**
 * 用户等级管理器
 * @author nan.li
 * @version 2014-11-12
 */
public class LevelMgr
{
    private static final Log logger = LogFactory.getLog(LevelMgr.class);
    
    private static final String CONFIG_FILE_PATH = "level.txt";
    
    private static Map<Integer, Level> LEVEL_MAP = new HashMap<Integer, Level>();
    
    static
    {
        loadConfig();
    }
    
    /**
     * 加载等级配置文件
     * @author nan.li
     */
    private static void loadConfig()
    {
        try
        {
            //只能采用getResourceAsStream这种方法来获取jar包中的非图片资源文件。不能通过file类来获取！
            List<String> lines = IOUtils.readLines(LevelMgr.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH), Constant.UTF8_ENCODING);
            if (lines.size() > 0)
            {
                System.out.println(String.format("Loading LevelMgr config...\nOK! Read %d lines!", lines.size()));
                int nextLevelNeedWords = 0;//升级到下一级所需的单词数
                for (String line : lines)
                {
                    if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "#"))
                    {
                        String[] splits = StringUtils.split(line, " ");
                        String title = "";//头衔
                        int levelBegin = 0;//从哪一级开始
                        if (splits.length == 2)
                        {
                            String titleAndLevelBegin = splits[0].trim();
                            title = titleAndLevelBegin.substring(0, 4);
                            levelBegin = Integer.valueOf(titleAndLevelBegin.substring(4));
                            nextLevelNeedWords = Integer.valueOf(splits[1].trim());
                        }
                        else if (splits.length == 1)
                        {
                            String titleAndLevelBegin = splits[0].trim();
                            title = titleAndLevelBegin.substring(0, 4);
                            levelBegin = Integer.valueOf(titleAndLevelBegin.substring(4));
                        }
                        Level level = new Level(title, levelBegin, nextLevelNeedWords);
                        LEVEL_MAP.put(levelBegin, level);
                    }
                }
            }
        }
        catch (IOException e)
        {
            logger.error(e);
        }
    }
    
    /**
     * 学习了新的生词之后，刷新称号
     * @author nan.li
     */
    public static void addWordRefreshTitle()
    {
        UserLevel userLevel = GsonCfgMgr.readObjectAES(UserLevel.class);
        if (userLevel == null)
        {
            userLevel = new UserLevel();
            userLevel.setCurLevel(0);
            userLevel.setCurLevelHaveNum(0);
            LevelMgr.fillLevelDetails(userLevel);
            GsonCfgMgr.writeObjectAES(userLevel);
        }
        int curLevel = userLevel.getCurLevel();//当前等级
        
        int curLevelHaveNum = userLevel.getCurLevelHaveNum();//当前等级拥有的单词数量
        
        int curLevelNeedAllNum = userLevel.getCurLevelNeedAllNum();//当前等级需要的总单词数量
        
        curLevelHaveNum++;//新增了一个单词
        
        if (curLevelHaveNum >= curLevelNeedAllNum)
        {
            curLevelHaveNum = 0;//清空当前级别的技术
            curLevel++;//升级
            System.out.println(String.format("恭喜你升级到LV%d", curLevel));
            userLevel.setCurLevel(curLevel);
            userLevel.setCurLevelHaveNum(curLevelHaveNum);
            updateLevelDetails(userLevel);
            final String titleTemp = userLevel.getTitle();
            final int curLevelTemp = userLevel.getCurLevel();
            ExecMgr.guiExec.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    WinMgr.translateFrame.setTitle(String.format("%s 【%s LV%d】", Constant.TITLE_REMARK, titleTemp, curLevelTemp));
                }
            });
        }
        else
        {
            //级别保持不变
            userLevel.setCurLevelHaveNum(curLevelHaveNum);
            //title无须更新
        }
        GsonCfgMgr.writeObjectAES(userLevel);
    }
    
    private static void updateLevelDetails(UserLevel userLevel)
    {
        fillLevelDetails(userLevel);
    }
    
    /**
     * 初始化时-填充等级信息的详情
     * @author nan.li
     * @param userLevel
     * @return
     */
    public static UserLevel fillLevelDetails(UserLevel userLevel)
    {
        int curLevel = userLevel.getCurLevel();//当前等级
        //        int curLevelHaveNum = userLevel.getCurLevelHaveNum();//当前级别拥有的单词数
        Level level = LEVEL_MAP.get(curLevel / 5 * 5);
        userLevel.setCurLevelNeedAllNum(level.getNextLevelNeedWords());
        userLevel.setTitle(level.getTitle());
        return userLevel;
    }
}
