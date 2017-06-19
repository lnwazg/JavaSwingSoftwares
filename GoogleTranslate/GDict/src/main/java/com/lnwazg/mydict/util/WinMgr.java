package com.lnwazg.mydict.util;

import javax.swing.JPanel;

import com.lnwazg.mydict.ui.HandlePanel;
import com.lnwazg.mydict.ui.ImageDialog;
import com.lnwazg.mydict.ui.SrcPanel;
import com.lnwazg.mydict.ui.TargetPannel;
import com.lnwazg.mydict.ui.TranslateFrame;
import com.lnwazg.mydict.ui.WordPanel;

/**
 * 窗口管理器<br>
 * 一个大总管，所有的常用的称手工具都可以在这里找到
 * @author nan.li
 * @version 2014-11-6
 */
public class WinMgr
{
    /**
     * 字典表
     */
    //    public static DictMap dictMap;
    
    //======================================================================
    public static TranslateFrame translateFrame;
    
    public static TargetPannel targetPannel;
    
    public static SrcPanel srcPannel;
    
    public static HandlePanel handlePanel;
    
    public static WordPanel wordPanel;
    
    public static JPanel leftPanel;

    public static ImageDialog imageDialog;
    
}
