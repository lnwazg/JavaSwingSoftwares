package com.lnwazg.mydict.util.handler.start;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.mydict.bean.UserLevel;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.level.LevelMgr;

public class LoadLevel implements IHandler
{
    
    @Override
    public void handle()
    {
        //加载当前的等级
        UserLevel userLevel = GsonCfgMgr.readObjectAES(UserLevel.class);
        if (userLevel == null)
        {
            userLevel = new UserLevel();
            userLevel.setCurLevel(0);
            userLevel.setCurLevelHaveNum(0);
            LevelMgr.fillLevelDetails(userLevel);
            GsonCfgMgr.writeObjectAES(userLevel);
        }
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
    
}
