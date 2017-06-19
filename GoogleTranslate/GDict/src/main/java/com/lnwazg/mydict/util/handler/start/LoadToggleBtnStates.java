package com.lnwazg.mydict.util.handler.start;

import javax.swing.JToggleButton;

import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.bean.SystemConfig;
import com.lnwazg.mydict.util.WinMgr;

public class LoadToggleBtnStates implements IHandler
{
    @Override
    public void handle()
    {
        //加载开关按钮
        JToggleButton toggleViewWordBook = WinMgr.handlePanel.getToggleViewWordBook();
        
        if (toggleViewWordBook.isSelected() != SystemConfig.Helper.isOpenWordbook())
        {
            //若状态不同，则要主动触发一次点击事件
            toggleViewWordBook.doClick();
        }
        
        //加载开关按钮
        JToggleButton toggleAutoQuery = WinMgr.handlePanel.getToggleAutoQuery();
        if (toggleAutoQuery.isSelected() != SystemConfig.Helper.isAutoQuery())
        {
            toggleAutoQuery.doClick();
        }
        
        //加载开关按钮
        JToggleButton toggleAutoSpeak = WinMgr.handlePanel.getToggleAutoSpeak();
        if (toggleAutoSpeak.isSelected() != SystemConfig.Helper.isAutoSpeak())
        {
            //若状态不同，则要主动触发一次点击事件
            toggleAutoSpeak.doClick();
        }
    }
}
