package com.lnwazg.mydict.util.handler.start;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.SystemUtils;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.WinMgr;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class RegHotKey implements IHandler
{
    @Override
    public void handle()
    {
        regHotKey();
    }
    
    //定义热键标识，用于在设置多个热键时，在事件处理中区分用户按下的热键
    public static final int FUNC_KEY_MARK = 1;
    
    public static final int EXIT_KEY_MARK = 0;
    
    //    public static final int F2_KEY_MARK = 2;
    
    /**
     * 注册唤醒热键
     * @author nan.li
     */
    private void regHotKey()
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            //注册快捷键的功能，仅仅在windows下面有必要！
            try
            {
                //第一步：注册热键，第一个参数表示该热键的标识，第二个参数表示组合键，如果没有则为0，第三个参数为定义的主要热键
                JIntellitype.getInstance().registerHotKey(FUNC_KEY_MARK, JIntellitype.MOD_WIN, (int)'G');//win+g，呼出应用
                JIntellitype.getInstance().registerHotKey(FUNC_KEY_MARK, 0, 44);//    map.put("printscreen", Integer.valueOf(44));   单独按printscreen，可触发 //printscreen，呼出应用 
                JIntellitype.getInstance().registerHotKey(EXIT_KEY_MARK, JIntellitype.MOD_ALT, (int)'Q');//alt+q 退出应用
                //            JIntellitype.getInstance().registerHotKey(F2_KEY_MARK, 0, KeyEvent.VK_F2);//F2 修复查询该单词
                //            rootPane.registerKeyboardAction(new ActionListener()
                //            {
                //                @Override
                //                public void actionPerformed(ActionEvent e)
                //                {
                //                    WinMgr.handlePanel.getRepair().doClick();
                //                }
                //            }, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                
                //第二步：添加热键监听器
                JIntellitype.getInstance().addHotKeyListener(new HotkeyListener()
                {
                    @Override
                    public void onHotKey(int markCode)
                    {
                        switch (markCode)
                        {
                            case FUNC_KEY_MARK:
                                ExecMgr.guiExec.execute(new Runnable()
                                {
                                    public void run()
                                    {
                                        if (WinMgr.translateFrame.isVisible())
                                        {
                                            //可见的时候
                                            if (WinMgr.translateFrame.isFocused())
                                            {
                                                //如果在最前端显示，则将其隐藏
                                                WinMgr.translateFrame.setVisible(false);//设置窗口可见
                                            }
                                            else
                                            {
                                                //如果不在最前端显示（未能获得焦点的时候），则将其获得焦点，即在最前台显示
                                                WinMgr.srcPannel.getSrcArea().setText("");
                                                WinMgr.translateFrame.setExtendedState(Frame.NORMAL);//正常显示窗口
                                                WinMgr.translateFrame.requestFocus();
                                                WinMgr.srcPannel.getSrcArea().requestFocus();//并立即让输入框获得输入焦点，解决查询人的燃眉之急！
                                            }
                                        }
                                        else
                                        {
                                            //如果不可见，则将其设置为可见，并且可以立即可以输入要查询的单词
                                            //仅当采用快捷键唤醒的时候，将输入框的内容立即清空掉
                                            WinMgr.srcPannel.getSrcArea().setText("");
                                            WinMgr.translateFrame.setVisible(true);//设置窗口可见
                                            WinMgr.translateFrame.setExtendedState(Frame.NORMAL);//正常显示窗口
                                            WinMgr.srcPannel.getSrcArea().requestFocus();//并立即让输入框获得输入焦点，解决查询人的燃眉之急！
                                        }
                                    }
                                });
                                break;
                            case EXIT_KEY_MARK:
                                System.exit(0);
                                break;
                            //                        case F2_KEY_MARK:
                            //                            WinMgr.handlePanel.getRepair().doClick();
                            //                            break;
                            default:
                                break;
                        }
                    }
                });
            }
            catch (Exception e)
            {
                //            e.printStackTrace();
                WinMgr.translateFrame.setVisible(false);
                //重复打开该辞典，会导致热键注册失败，因此后打开的应该退出
                JOptionPane.showMessageDialog(WinMgr.translateFrame, "不要重复打开 " + Constant.TITLE_REMARK + " 哦~");
                System.exit(0);
            }
        }
    }
}
