package com.lnwazg.mydict.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.lnwazg.mydict.util.WinMgr;

/**
 * 主对话框
 * @author nan.li
 * @version 2014-11-6
 */
public class TranslateFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    
    @Override
    protected JRootPane createRootPane()
    {
        //添加点击esc之后清空输入框的事件
        JRootPane rootPane = new JRootPane();
        //注册按键监听器
        rootPane.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //                WinMgr.translateFrame.setExtendedState(JFrame.ICONIFIED);//最小化，并且不可见了
                WinMgr.handlePanel.clearAll();//清空查询输入框
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //注册按键监听器
        //F1发音
        rootPane.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                WinMgr.handlePanel.getSpell().doClick();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //F2修复受损的单词
        rootPane.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                WinMgr.handlePanel.getRepair().doClick();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}