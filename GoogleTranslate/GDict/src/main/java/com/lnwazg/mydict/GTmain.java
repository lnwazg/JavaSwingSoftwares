package com.lnwazg.mydict;

import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.handlerseq.HandlerSequence;
import com.lnwazg.kit.singleton.B;
import com.lnwazg.kit.swing.SwingUtils;
import com.lnwazg.mydict.ui.HandlePanel;
import com.lnwazg.mydict.ui.ImageDialog;
import com.lnwazg.mydict.ui.SrcPanel;
import com.lnwazg.mydict.ui.TargetPannel;
import com.lnwazg.mydict.ui.TranslateFrame;
import com.lnwazg.mydict.ui.WordPanel;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.IconMgr;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.handler.start.DownloadDictTask;
import com.lnwazg.mydict.util.handler.start.InitDictMap;
import com.lnwazg.mydict.util.handler.start.InitGsonCfgMgrDir;
import com.lnwazg.mydict.util.handler.start.InitUserDir;
import com.lnwazg.mydict.util.handler.start.LoadLevel;
import com.lnwazg.mydict.util.handler.start.LoadTemplateConfig;
import com.lnwazg.mydict.util.handler.start.LoadToggleBtnStates;
import com.lnwazg.mydict.util.handler.start.LoadWordBook;
import com.lnwazg.mydict.util.handler.start.MigrateUserDir;
import com.lnwazg.mydict.util.handler.start.RegHotKey;
import com.lnwazg.mydict.util.handler.start.StartLocalHttpServer;

/**
 * Swing程序跑得慢而且卡，绝不会是swing的问题，而是因为程序中未将耗时的操作放在线程池中执行，而导致了界面响应变慢！
 * 写一个框架，或者写一个小型的解析器，大概是最有趣也最一劳永逸的事情了吧！
 * 很多时候，真的是要从架构层去改进的，才能真正获得提升和可用性！！
 * 本地的图片服务器，初现效果！
 * 内建一个词库文件，在空闲时间逐一下载到本地！
 * 整合各种工具类，见缝插针维护kit
 * 闲暇时间，就收集各色通用好用的工具类！！！
 * 熟悉 回顾设计模式 并使用！
 * 对话模式高仿微信，但是比微信更懂客户需要
 * 让框架的威力内嵌于那个util包中，高可配置化，所有都能享受到！例如job框架
 * 
 * 启示录：
 * 1.界面更新过程中的耗时任务必须单独开线程并放入到线程池中执行！
 * 2.放入线程池里面的任务如果有依赖关系，则一定不要分开起线程！否则会由于数据一致性问题而造成逻辑错乱！
 * 
 * @author nan.li
 * @version 2014-11-6
 */
public class GTmain
{
    private static final Log logger = LogFactory.getLog(GTmain.class);
    
    public static void main(String[] args)
    {
        new GTmain();
    }
    
    public GTmain()
    {
        SwingUtils.showEnv();
        SwingUtils.patchJdkDictAppImeBug();
        //加载美化版UI
        loadBeautyUI();
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                init();
            }
        });
    }
    
    private void init()
    {
        WinMgr.translateFrame = new TranslateFrame();
        WinMgr.translateFrame.setTitle(Constant.TITLE_REMARK);
        WinMgr.translateFrame.setIconImage(IconMgr.iconHeavy);
        
        WinMgr.targetPannel = new TargetPannel();
        WinMgr.srcPannel = new SrcPanel();
        WinMgr.handlePanel = new HandlePanel();
        WinMgr.wordPanel = new WordPanel();
        
        BoxLayout boxLayout = new BoxLayout(WinMgr.translateFrame.getContentPane(), BoxLayout.X_AXIS);
        WinMgr.translateFrame.setLayout(boxLayout);
        
        WinMgr.leftPanel = new JPanel();
        BoxLayout leftBoxlLayout = new BoxLayout(WinMgr.leftPanel, BoxLayout.Y_AXIS);
        WinMgr.leftPanel.setLayout(leftBoxlLayout);
        WinMgr.leftPanel.add(WinMgr.srcPannel);
        WinMgr.leftPanel.add(Box.createVerticalStrut(10));
        WinMgr.leftPanel.add(WinMgr.handlePanel);
        WinMgr.leftPanel.add(Box.createVerticalGlue());
        WinMgr.leftPanel.add(WinMgr.targetPannel);
        
        WinMgr.translateFrame.add(WinMgr.leftPanel);
        WinMgr.translateFrame.add(Box.createHorizontalGlue());
        WinMgr.translateFrame.add(WinMgr.wordPanel);
        
        WinMgr.translateFrame.setVisible(true);
        WinMgr.translateFrame.setResizable(false);
        
        WinMgr.translateFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                WinMgr.translateFrame.setExtendedState(Frame.ICONIFIED);//最小化，并且不可见了
            }
            
            @Override
            public void windowIconified(WindowEvent e)
            {
                WinMgr.translateFrame.setVisible(false);//先隐藏图标
            }
        });
        
        WinMgr.translateFrame.pack();
        
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int left = (screenWidth - WinMgr.translateFrame.getSize().width) / 2;
        int top = (screenHeight - WinMgr.translateFrame.getSize().height) / 2;
        WinMgr.translateFrame.setLocation(left, top);//设置窗口居中显示
        
        loadSystemTray();
        
        WinMgr.imageDialog = new ImageDialog();
        
        HandlerSequence.getInstance()
            .exec(B.g(RegHotKey.class))
            .exec(B.g(LoadTemplateConfig.class))
            .exec(B.g(InitUserDir.class))
            .exec(B.g(InitGsonCfgMgrDir.class))
            .exec(B.g(MigrateUserDir.class))
            .exec(B.g(StartLocalHttpServer.class))
            .exec(B.g(InitDictMap.class))
            .exec(B.g(LoadWordBook.class))
            .exec(B.g(LoadToggleBtnStates.class))
            .exec(B.g(LoadLevel.class))
            //只会在首启时拖慢网速，因此决定弃用
            .exec(B.g(DownloadDictTask.class));
    }
    
    /**
     * 加载系统托盘图标设置
     */
    private void loadSystemTray()
    {
        try
        {
            if (SystemTray.isSupported())
            {// 判断当前平台是否支持系统托盘
                SystemTray st = SystemTray.getSystemTray();
                TrayIcon trayIcon = new TrayIcon(IconMgr.icon);
                String toolTips = Constant.TITLE_REMARK
                    + " 残障英语终结者 v1.0\n让apache.org上面的原生文档变得和蔼可亲！\n让英文文档的阅读易如反掌！\n内含“艾宾浩斯记忆曲线”引擎v1.0\n内含“反囫囵吞枣”系统v1.0\n一切皆动态调整，享受全自动化的单词学习体验！";
                trayIcon.setToolTip(toolTips);//托盘图标提示
                //左击该托盘图标，则打开窗体
                trayIcon.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        //当左击窗口时
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            WinMgr.translateFrame.setVisible(true);//设置窗口可见
                            WinMgr.translateFrame.setExtendedState(Frame.NORMAL);//正常显示窗口
                        }
                    }
                });
                PopupMenu popupMenu = new PopupMenu();
                MenuItem exitSubMenu = new MenuItem("Exit");
                exitSubMenu.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);
                    }
                });
                popupMenu.add(exitSubMenu);
                trayIcon.setPopupMenu(popupMenu); // 为托盘添加右键弹出菜单
                st.add(trayIcon);//将托盘图标加入到系统托盘中
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }
    
    //皮肤美化======================================================================
    private void loadBeautyUI()
    {
        try
        {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}