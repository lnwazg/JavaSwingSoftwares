package com.lnwazg.mydict.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.swing.ui.comp.ImageScroller;
import com.lnwazg.kit.swing.ui.comp.SmartButton;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.WinMgr;

//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;

/**
 * 弹出图片的对话框
 * @author Administrator
 * @version 2016年4月24日
 */
public class ImageDialog extends JDialog
{
    private static final long serialVersionUID = -4065371772887090036L;
    
    JPanel corePanel, handlePanel;
    
    ImageScroller imageScroller;
    
    SmartButton close;
    
    Container c;
    
    public ImageDialog()
    {
        c = getContentPane();
        setTitle("查看图片详情");
        corePanel = new JPanel();
        corePanel.setLayout(new BoxLayout(corePanel, BoxLayout.Y_AXIS));
        corePanel.add(Box.createVerticalStrut(10));
        imageScroller = new ImageScroller();
        corePanel.add(imageScroller);
        handlePanel = new JPanel();
        handlePanel.setLayout(new BoxLayout(handlePanel, BoxLayout.X_AXIS));
        handlePanel.add(Box.createHorizontalGlue());
        close = new SmartButton("关闭");
        close.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        });
        handlePanel.add(close);
        handlePanel.add(Box.createHorizontalGlue());
        
        corePanel.add(handlePanel);
        corePanel.add(Box.createVerticalStrut(10));
        c.add(corePanel, BorderLayout.CENTER);
        
//        //使用javafx的组件
//        JFXPanel webBrowser = new JFXPanel();
//        Platform.runLater(() -> {
//            WebView webView = new WebView();
//            final WebEngine webEngine = webView.getEngine();
//            webEngine.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
//            webEngine.load("http://baoxian.haiziwang.com");
//            webBrowser.setScene(new Scene(webView));
//        });
//        c.add(webBrowser, BorderLayout.NORTH);
    }
    
    public void showImage()
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            public void run()
            {
                String srcWord = WinMgr.srcPannel.getSrcArea().getText();
                if (StringUtils.isNotBlank(srcWord))
                {
                    srcWord = srcWord.trim().toLowerCase();
                    File imageFile = new File(Constant.USER_DIR + Constant.IMG_FOLDER, srcWord + Constant.WORD_IMAGE_SUFFIX);
                    if (imageFile.exists())
                    {
                        imageScroller.setImageContent(new ImageIcon(imageFile.getAbsolutePath()));
                        ImageDialog.this.pack();
                        setTitle(String.format("%s", srcWord));
                        double parentX = WinMgr.translateFrame.getLocation().getX();
                        double parentY = WinMgr.translateFrame.getLocation().getY();
                        //出现在主窗口左侧
                        setLocation(new Point((int)(parentX - getSize().width + 40), (int)parentY));
                        setVisible(true);
                    }
                }
            }
        });
    }
    
    public void refreshImage()
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            public void run()
            {
                String srcWord = WinMgr.srcPannel.getSrcArea().getText();
                if (StringUtils.isNotBlank(srcWord))
                {
                    srcWord = srcWord.trim().toLowerCase();
                    File imageFile = new File(Constant.USER_DIR + Constant.IMG_FOLDER, srcWord + Constant.WORD_IMAGE_SUFFIX);
                    if (imageFile.exists())
                    {
                        imageScroller.setImageContent(new ImageIcon(imageFile.getAbsolutePath()));
                        setTitle(String.format("%s", srcWord));
                        ImageDialog.this.pack();
                        double parentX = WinMgr.translateFrame.getLocation().getX();
                        double parentY = WinMgr.translateFrame.getLocation().getY();
                        setLocation(new Point((int)(parentX - getSize().width + 40), (int)parentY));
                    }
                }
            }
        });
    }
    
    public void refreshImage(String word)
    {
        ExecMgr.guiExec.execute(() -> {
            String srcWord = word.trim().toLowerCase();
            File imageFile = new File(Constant.USER_DIR + Constant.IMG_FOLDER, srcWord + Constant.WORD_IMAGE_SUFFIX);
            if (imageFile.exists())
            {
                imageScroller.setImageContent(new ImageIcon(imageFile.getAbsolutePath()));
                setTitle(String.format("%s", srcWord));
                ImageDialog.this.pack();
                double parentX = WinMgr.translateFrame.getLocation().getX();
                double parentY = WinMgr.translateFrame.getLocation().getY();
                setLocation(new Point((int)(parentX - getSize().width + 40), (int)parentY));
            }
        });
    }
    
    @Override
    protected JRootPane createRootPane()
    {
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //ESC的时候，将弹出窗口设置为不可见
                setVisible(false);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}
