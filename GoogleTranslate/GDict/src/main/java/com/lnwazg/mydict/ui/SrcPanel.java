package com.lnwazg.mydict.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.taskman.CallableTask;
import com.lnwazg.mydict.bean.SystemConfig;
import com.lnwazg.mydict.plugins.Plugin;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.tasks.StoreToWordbookTask;
import com.lnwazg.mydict.util.tasks.TaskMap;
import com.lnwazg.mydict.util.translate.Language;
import com.lnwazg.mydict.util.translate.TranslateUtil;

public class SrcPanel extends JPanel
{
    private static final long serialVersionUID = 5825100042399728549L;
    
    private JTextArea srcArea;
    
    private JScrollPane paneScrollPane;
    
    private DocumentListener documentListener;
    
    private boolean NotAutoNeedNewQuery = false;
    
    private String NeedNewQueryOldWord = "";
    
    public SrcPanel()
    {
        setBorder(BorderFactory.createTitledBorder("原文"));
        srcArea = new JTextArea();
        srcArea.setFont(Constant.BigFont);
        srcArea.setLineWrap(true);
        srcArea.setWrapStyleWord(true);
        documentListener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                handleInput();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                handleInput();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
                handleInput();
            }
        };
        srcArea.getDocument().addDocumentListener(documentListener);
        paneScrollPane = new JScrollPane(srcArea);
        paneScrollPane.setPreferredSize(new Dimension(300, 90));
        add(paneScrollPane);
    }
    
    public JTextArea getSrcArea()
    {
        return this.srcArea;
    }
    
    /**
     * 目标语言，默认为英语
     */
    private String targetLanguage = Language.ENGLISH.getValue();
    
    public String lastLastWord = "";
    
    public String lastWord = "";
    
    private static int UNFAMILIAR_WORD_LIMIT_LENGTH = 1;//生词限定长度
    
    /**
     * 是否带查询的单词以回车键结尾
     * @author Administrator
     * @return
     */
    private boolean endsWithEnterKey()
    {
        String src = WinMgr.srcPannel.getSrcArea().getText();
        if (StringUtils.isNotEmpty(src))
        {
            if (src.endsWith("\n"))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 输入处理器
     * @author nan.li
     */
    public void handleInput()
    {
        
        //        //如果是需要新查询，则应该将老词取消掉
        //不能在回调里再次触发事件！
        //        if (!SystemConfig.Helper.isAutoQuery() && NotAutoNeedNewQuery)
        //        {
        //            String src = WinMgr.srcPannel.getSrcArea().getText();
        //            src = StringUtils.substring(src, NeedNewQueryOldWord.length());
        //            
        //            WinMgr.srcPannel.getSrcArea().setText(src);
        //            WinMgr.srcPannel.getSrcArea().setCaretPosition(src.length());
        //            
        //            NotAutoNeedNewQuery = false;
        //            NeedNewQueryOldWord = "";
        //        }
        
        //根据字数调节显示大小
        adjustFontSize();
        
        //此处分为两种情况：
        //1.如果是自动查询，则直接执行下面即可
        //2.如果是手动查询，则需要判定如果最后以\n结尾，才查询。否则，无须查询。
        if (SystemConfig.Helper.isAutoQuery())
        {
            query();
        }
        else
        {
            if (endsWithEnterKey())
            {
                query();
            }
        }
    }
    
    /**
     * 自适应字体尺寸
     * @author Administrator
     */
    private void adjustFontSize()
    {
        String src = WinMgr.srcPannel.getSrcArea().getText();
        if (StringUtils.isNotBlank(src))
        {
            String trimedSrc = StringUtils.trim(src);
            if (trimedSrc.length() > Constant.ChangeToSmallFontWordLimit)
            {
                WinMgr.srcPannel.getSrcArea().setFont(Constant.SmallFont);
            }
            else
            {
                WinMgr.srcPannel.getSrcArea().setFont(Constant.BigFont);
            }
        }
    }
    
    public void query()
    {
        //依次提交任务，所以此处用singleExec没有问题！
        ExecMgr.singleExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                //只有一个字母的时候，让百度翻译，可能会感觉会出现了一些不符实际的字符
                //因此，一个字母的时候，有必要过滤掉百度翻译！
                String src = WinMgr.srcPannel.getSrcArea().getText();
                if (StringUtils.isNotBlank(src))
                {
                    //只有当输入的文字非空，才会进行翻译处理
                    String trimedSrc = StringUtils.trim(src);
                    String trimedLastWord = StringUtils.trim(lastWord);
                    String trimedLastLastWord = StringUtils.trim(lastLastWord);
                    String wxResult = Plugin.subSystemProcessUnit(trimedSrc);
                    if (StringUtils.isNotEmpty(wxResult))
                    {
                        //也不需要记录上一个词语是什么了！
                        ExecMgr.guiExec.execute(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                WinMgr.targetPannel.getTextPane().setText(String.format("系统消息:<br>%s", wxResult));
                            }
                        });
                        return;
                    }
                    
                    if (lastWord.length() == 0)
                    {
                        WinMgr.handlePanel.audioOff();//先关闭音频（防止万一查出来的玩意不能发音呢！！）
                        //一开始没有内容的情况
                        //由空白变成了一个英文单词，支持空格输入
                        //从没内容突然变成了有内容
                        if (trimedSrc.length() > UNFAMILIAR_WORD_LIMIT_LENGTH && Utils.isAllEnglish(trimedSrc))
                        {
                            //如果认定了输入的内容是一个纯正的英文单词的话
                            //如果是不少于2个字母的纯英文，那么就查询之
                            //那么就查询这个英文单词吧
                            StartNewWordQuery(trimedSrc);
                        }
                        else if (trimedSrc.length() == 1 && Utils.isAllEnglish(trimedSrc))
                        {
                            //如果是只有一个字母的纯英文
                            //那么，原文就是一个英文字母啊，任何翻译都会是苍白无力的，并且容易被误认为是软件bug！所以，果断什么都不做！
                            //什么都不做啊！
                            //有了这个判断分支，果然可用性一下子增加了好多啊！！！真是太棒了！
                        }
                        else
                        {
                            //不是纯英文，那么会有哪些情况？
                            //干脆就直接用百度翻译吧，管它是什么呢，反正如果不是一个纯正的单词，就用默认的查询策略
                            translate(trimedSrc);
                        }
                    }
                    else
                    {
                        //一开始就有内容的情况
                        if (trimedSrc.equals(StringUtils.trim(lastWord)))
                        {
                            //假如输入前后实际上竟然是同一个单词
                            //那么就啥也别做呗！
                            //既不查生词，也不查百度快译！
                        }
                        else
                        {
                            WinMgr.handlePanel.audioOff();//先关闭音频（防止万一查出来的玩意不能发音呢！！）
                            //如果已经不是同一个单词了
                            if (trimedSrc.length() > UNFAMILIAR_WORD_LIMIT_LENGTH && Utils.isAllEnglish(trimedSrc))
                            {
                                //如果认定了输入的内容是一个纯正的英文单词的话
                                //那么就查询这个英文单词吧
                                //顺便把老的任务给取消掉（管它老的任务是否存在呢，不存在拉倒！）
                                notifyOldNewWordQueryStop(trimedLastWord);
                                StartNewWordQuery(trimedSrc);
                            }
                            else
                            {
                                //不是纯英文，那么会有哪些情况？
                                //直接调用百度翻译是不错，但是也要考虑上上个单词是纯英文，而上个单词是纯英文后补充了个空格，从而导致了上上个单词的查询依然被执行完了这个情况！
                                if (trimedLastWord.equals(trimedLastLastWord) && trimedLastLastWord.length() > UNFAMILIAR_WORD_LIMIT_LENGTH
                                    && Utils.isAllEnglish(trimedLastLastWord))
                                {
                                    //过滤掉这个没必要存在的生词查询！（因为你妨碍我的真实目的：查询整个句子，而非句子里首单词！）
                                    notifyOldNewWordQueryStop(trimedLastLastWord);
                                }
                                translate(trimedSrc);
                            }
                        }
                    }
                }
                else
                {
                    src = "";
                    //同时将src和target清空
                    //WinMgr.srcPannel.getSrcArea().setText("");
                    WinMgr.targetPannel.getTextPane().setText("");
                }
                //逐级向前覆盖
                lastLastWord = lastWord;
                lastWord = src;
                
                if (!SystemConfig.Helper.isAutoQuery())
                {
                    //如果是手动查询，那么查询完毕之后，要去除掉结尾的回车键，并将光标放置到最后
                    WinMgr.srcPannel.getSrcArea().getDocument().removeDocumentListener(WinMgr.srcPannel.getDocumentListener());
                    src = StringUtils.substring(src, 0, src.length() - 1);
                    WinMgr.srcPannel.getSrcArea().setText(src);
                    WinMgr.srcPannel.getSrcArea().setCaretPosition(src.length());
                    WinMgr.srcPannel.getSrcArea().getDocument().addDocumentListener(WinMgr.srcPannel.getDocumentListener());
                    
                    NotAutoNeedNewQuery = true;//非自动的情况下，下一次的查询需要完全查一个新单词
                    NeedNewQueryOldWord = src;
                }
            }
        });
    }
    
    /**
     * 取消某个即将执行的任务（如果该任务还存在的话）
     * 取消上次的查生词的任务（因为有更新的任务了，所以上次的任务便没意义存在了）
     * @author nan.li
     * @param oldWord
     */
    protected void notifyOldNewWordQueryStop(String oldWord)
    {
        if (StringUtils.isNotEmpty(oldWord))
        {
            StoreToWordbookTask storeToWordbookTask = TaskMap.get(oldWord);
            if (storeToWordbookTask != null)
            {
                storeToWordbookTask.cancelAdd();
            }
        }
    }
    
    /**
     * 新增一个未来执行的任务
     * 查询生词
     * @author nan.li
     * @param newWord
     */
    protected void StartNewWordQuery(String newWord)
    {
        StoreToWordbookTask newTask = new StoreToWordbookTask(newWord);
        TaskMap.put(newWord, newTask);
        ExecMgr.cachedExec.execute(newTask);
    }
    
    /**
     * 翻译 - 应该是有顺序地排队执行的才对！
     * @author nan.li
     * @param src
     */
    private void translate(final String src)
    {
        //顺序执行，并且一旦提交了新任务，便将老任务全部取消掉！
        CallableTask tranlateTask = new CallableTask()
        {
            @Override
            public void run()
            {
                if (isEnglish(src))
                {
                    //英翻中
                    targetLanguage = Language.CHINA.getValue();
                }
                else
                {
                    //否则，【任意的语言、标点、空格等等】翻英
                    targetLanguage = Language.ENGLISH.getValue();
                }
                final String googleResult = TranslateUtil.translate(src, targetLanguage);
                ExecMgr.guiExec.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        WinMgr.targetPannel.getTextPane().setText(googleResult);
                    }
                });
            }
        };
        ExecMgr.taskManager.cleanAndAdd(tranlateTask);
    }
    
    /**
     * 输入的内容是否是英文
     * @author nan.li
     * @param src
     * @return
     */
    private boolean isEnglish(String src)
    {
        char c = src.charAt(0);
        if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')))
        {
            return true;
        }
        return false;
    }
    
    public DocumentListener getDocumentListener()
    {
        return documentListener;
    }
    
}