package com.lnwazg.mydict.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.lnwazg.kit.audio.AudioMgr;
import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.swing.R;
import com.lnwazg.kit.swing.SwingUtils;
import com.lnwazg.kit.taskman.CallableTask;
import com.lnwazg.mydict.bean.SystemConfig;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.IconMgr;
import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.localdic.DictMap;
import com.lnwazg.mydict.util.translate.Language;
import com.lnwazg.mydict.util.translate.TranslateUtil;

/**
 * 操作面板
 * @author nan.li
 * @version 2014-11-6
 */
public class HandlePanel extends JPanel
{
    private static final long serialVersionUID = -1029412131621700036L;
    
    /**
     * 清除按钮、发音按钮
     */
    private JButton clear, repair, spell;
    
    /**
     * 查看单词本开关按钮、自动发音开关按钮、自动查询手动查询的开关按钮
     */
    private JToggleButton toggleViewWordBook, toggleAutoSpeak, toggleAutoQuery;
    
    private String word;//待发音的单词
    
    public HandlePanel()
    {
        //清除输入框的按钮
        clear = new JButton(IconMgr.eraserIcon);
        SwingUtils.beautyBtn(clear);
        clear.setToolTipText("清空(Esc)");
        clear.setPreferredSize(new Dimension(40, 25));
        clear.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent paramActionEvent)
            {
                clearAll();
            }
        });
        
        //开关单词板的按钮
        toggleViewWordBook = new JToggleButton(IconMgr.wordpadIcons[0]);
        SwingUtils.beautyBtn(toggleViewWordBook);
        toggleViewWordBook.setSelected(true);
        toggleViewWordBook.setToolTipText("生词本已打开");
        toggleViewWordBook.setPreferredSize(new Dimension(40, 25));
        toggleViewWordBook.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (((JToggleButton)e.getSource()).getModel().isSelected())
                {
                    toggleViewWordBook.setIcon(IconMgr.wordpadIcons[0]);
                    WinMgr.translateFrame.add(WinMgr.wordPanel);
                    toggleViewWordBook.setToolTipText("生词本已打开");
                    //                WinMgr.srcPannel.getSrcArea().setRows(8);
                    //                WinMgr.targetPannel.getTargetArea().setRows(8);
                    WinMgr.translateFrame.pack();
                    SystemConfig.Helper.setOpenWordbook(true);
                }
                else
                {
                    toggleViewWordBook.setIcon(IconMgr.wordpadIcons[1]);
                    WinMgr.translateFrame.remove(WinMgr.wordPanel);
                    toggleViewWordBook.setToolTipText("生词本已关闭");
                    //                WinMgr.srcPannel.getSrcArea().setRows(6);
                    //                WinMgr.targetPannel.getTargetArea().setRows(6);
                    WinMgr.translateFrame.pack();
                    SystemConfig.Helper.setOpenWordbook(false);
                }
                
            }
        });
        
        //自动、手动查询的切换按钮
        toggleAutoQuery = new JToggleButton(IconMgr.autoQueryIcons[0]);
        SwingUtils.beautyBtn(toggleAutoQuery);
        toggleAutoQuery.setSelected(true);
        toggleAutoQuery.setToolTipText("自动查询");
        toggleAutoQuery.setPreferredSize(new Dimension(40, 25));
        toggleAutoQuery.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (((JToggleButton)e.getSource()).getModel().isSelected())
                {
                    toggleAutoQuery.setIcon(IconMgr.autoQueryIcons[0]);
                    toggleAutoQuery.setToolTipText("自动查询");
                    WinMgr.translateFrame.pack();
                    SystemConfig.Helper.setAutoQuery(true);
                }
                else
                {
                    toggleAutoQuery.setIcon(IconMgr.autoQueryIcons[1]);
                    toggleAutoQuery.setToolTipText("手动查询(回车键执行查询操作)");
                    WinMgr.translateFrame.pack();
                    SystemConfig.Helper.setAutoQuery(false);
                }
                WinMgr.srcPannel.lastWord = "";//便于切换后可以重新查询同一个单词
            }
        });
        
        //开关自动发音的按钮
        toggleAutoSpeak = new JToggleButton(IconMgr.autoSpeakIcons[0]);
        SwingUtils.beautyBtn(toggleAutoSpeak);
        toggleAutoSpeak.setSelected(true);
        toggleAutoSpeak.setToolTipText("自动发音已开启");
        toggleAutoSpeak.setPreferredSize(new Dimension(40, 25));
        toggleAutoSpeak.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (((JToggleButton)e.getSource()).getModel().isSelected())
                {
                    toggleAutoSpeak.setToolTipText("自动发音已开启");
                    toggleAutoSpeak.setIcon(IconMgr.autoSpeakIcons[0]);
                    SystemConfig.Helper.setAutoSpeak(true);
                }
                else
                {
                    toggleAutoSpeak.setToolTipText("自动发音已关闭");
                    toggleAutoSpeak.setIcon(IconMgr.autoSpeakIcons[1]);
                    SystemConfig.Helper.setAutoSpeak(false);
                }
            }
        });
        //修复错误的翻译的问题
        //        repair = new JButton(IconMgr.repairIcon);
        repair = new JButton(R.icon("icons/repair.png"));//取资源的新方法
        
        SwingUtils.beautyBtn(repair);
        repair.setToolTipText("修复下载有问题的解释(F2)");
        repair.setPreferredSize(new Dimension(40, 25));
        repair.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExecMgr.cachedExec.execute(new Runnable()
                {
                    public void run()
                    {
                        //立即执行
                        String word = WinMgr.srcPannel.getSrcArea().getText();
                        DictMap.remove(word);
                        String result = TranslateUtil.translate(word, Language.CHINA.getValue());
                        Utils.getDetailedTrans(word, result);
                    }
                });
            }
        });
        
        //手动发音的按钮
        spell = new JButton(IconMgr.spellIcon);
        SwingUtils.beautyBtn(spell);
        spell.setToolTipText("发音(F1)");
        spell.setPreferredSize(new Dimension(40, 25));
        spell.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExecMgr.cachedExec.execute(new Runnable()
                {
                    public void run()
                    {
                        File audioFile = null;
                        if (Constant.SPECIAL_WORDS.contains(word))
                        {
                            audioFile = new File(Constant.AUDIO_DIR, String.format("SP_%s.mp3", word));
                        }
                        else
                        {
                            audioFile = new File(Constant.AUDIO_DIR, String.format("%s.mp3", word));
                        }
                        if (audioFile.exists())
                        {
                            AudioMgr.playAudio(audioFile);
                        }
                    }
                });
            }
        });
        
        //设置布局
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(clear);
        add(Box.createHorizontalStrut(10));
        add(toggleViewWordBook);
        add(Box.createHorizontalStrut(10));
        add(toggleAutoQuery);
        add(Box.createHorizontalStrut(10));
        add(repair);
        add(Box.createHorizontalStrut(10));
        add(toggleAutoSpeak);
        add(Box.createHorizontalStrut(10));
        add(spell);
        
        audioOff();
    }
    
    public JButton getClear()
    {
        return clear;
    }
    
    public void clearAll()
    {
        //去除所有正在查询的单词查询查询任务，然后清空输入框
        ExecMgr.taskManager.cleanAndAdd(new CallableTask()
        {
            @Override
            public void run()
            {
                ExecMgr.guiExec.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        WinMgr.targetPannel.getTextPane().setText("");
                        WinMgr.srcPannel.getSrcArea().setText("");
                        WinMgr.srcPannel.getSrcArea().requestFocus();
                    }
                });
            }
        });
    }
    
    /**
     * 关闭发音按钮
     * @author nan.li
     */
    public void audioOff()
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                spell.setVisible(false);
            }
        });
    }
    
    /**
     * 显示发音按钮
     * @author nan.li
     * @param word
     */
    public void audioOn(String word)
    {
        this.word = word;
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                spell.setVisible(true);
                if (SystemConfig.Helper.isAutoSpeak())
                {
                    //假如自动发音已经打开，则自动发音一次！
                    spell.doClick();
                }
            }
        });
    }
    
    public JToggleButton getToggleAutoQuery()
    {
        return toggleAutoQuery;
    }
    
    public JToggleButton getToggleViewWordBook()
    {
        return toggleViewWordBook;
    }
    
    public JToggleButton getToggleAutoSpeak()
    {
        return toggleAutoSpeak;
    }
    
    public JButton getSpell()
    {
        return spell;
    }
    
    public JButton getRepair()
    {
        return repair;
    }
}