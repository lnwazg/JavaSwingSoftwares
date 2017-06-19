package com.lnwazg.mydict.util;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.Icon;

import com.lnwazg.kit.swing.ImageUtil;
import com.lnwazg.mydict.GTmain;

/**
 * 图标管理器
 * @author Administrator
 * @version 2016年4月10日
 */
public class IconMgr
{
    public static Image icon = Toolkit.getDefaultToolkit().createImage(GTmain.class.getClassLoader().getResource("icons/icon.png"));//16*16
    
    public static Image iconHeavy = Toolkit.getDefaultToolkit().createImage(GTmain.class.getClassLoader().getResource("icons/iconHeavy.png"));//16*16
    
    /**
     * 自动打开、关闭生词板
     */
    public static Icon[] wordpadIcons = new Icon[] {ImageUtil.getIcon("icons/wordpad_open.png"), ImageUtil.getIcon("icons/wordpad_close.png")};
    
    /**
     * 自动查询单词/手动回车查询单词
     */
    public static Icon[] autoQueryIcons = new Icon[] {ImageUtil.getIcon("icons/auto.png"), ImageUtil.getIcon("icons/auto_not.png")};
    
    /**
     * 清除输入框的图标
     */
    public static Icon eraserIcon = ImageUtil.getIcon("icons/eraser.png");
    
    /**
     * 修复的图标
     */
    public static Icon repairIcon = ImageUtil.getIcon("icons/repair.gif");
    
    /**
     * 开启\关闭自动发音
     */
    public static Icon[] autoSpeakIcons = new Icon[] {ImageUtil.getIcon("icons/bell.png"), ImageUtil.getIcon("icons/bell-mute.png")};
    
    /**
     * 手动发音
     */
    public static Icon spellIcon = ImageUtil.getIcon("icons/audio.gif");
    
}
