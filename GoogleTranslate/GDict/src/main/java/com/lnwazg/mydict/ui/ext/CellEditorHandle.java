package com.lnwazg.mydict.ui.ext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.swing.ImageUtil;
import com.lnwazg.kit.swing.SwingUtils;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.anticheat.EnergyHall;
import com.lnwazg.mydict.util.wordbook.WordFlag;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 我的单词按钮编辑器
 * @author nan.li
 * @version 2014-11-17
 */
public class CellEditorHandle extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = -4105693920426952196L;
    
    WordFlag wordFlag;
    
    private JPanel panel;
    
    private JButton btnRem;//记住了！
    
    private JButton btnDisappear;//别出现了！
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        panel = new JPanel();
        wordFlag = (WordFlag)value;
        final String word = wordFlag.getWord();
        //优先根据canCycle判断是否显示学习按钮，然后才是各干各的！
        boolean canCycle = wordFlag.isCanCycle();//是否可以循环（显示循环按钮）
        if (!canCycle)
        {
            //不可循环，则仅仅显示一个学习标签！
            jLabel = new JLabel(ImageUtil.getIcon("icons/learn.png"));
            panel.setLayout(new BorderLayout());
            panel.add(jLabel, BorderLayout.CENTER);
            panel.setToolTipText("每消灭一个单词，就是向着美好的未来迈进一步！");
            panel.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    WinMgr.srcPannel.getSrcArea().getDocument().removeDocumentListener(WinMgr.srcPannel.getDocumentListener());
                    WinMgr.srcPannel.getSrcArea().setFont(Constant.BigFont);
                    WinMgr.srcPannel.getSrcArea().setText(wordFlag.getWord());
                    Utils.getDetailedTrans(wordFlag.getWord(), wordFlag.getTranslation());//从缓存里取出单词的翻译信息
                    WinMgr.srcPannel.lastWord = wordFlag.getWord();
                    WinMgr.srcPannel.getSrcArea().getDocument().addDocumentListener(WinMgr.srcPannel.getDocumentListener());
                    fireEditingStopped(); //stopped!!!!
                    //刷新左侧的插图
                    WinMgr.imageDialog.refreshImage(wordFlag.getWord());
                }
            });
        }
        else
        {
            final String tag = wordFlag.getFlag();//0:生词    1：查询次数最高的词    2：复习用的单词//0、1均只有1个按钮，2有2个按钮
            //删除按钮的功能地位等价于记忆，同样需要消耗记忆能量！
            //生词已经没有希望进入这里了，但是机制依然为其保留着！  所以WordbookHelper.WORDS_0_UNFAMILIAR.equals(tag)的判断其实是多余的！但是为了整体的代码一致性，为了便于理解，还是为其保留
            if (WordbookHelper.WORDS_0_UNFAMILIAR.equals(tag) || WordbookHelper.WORDS_1_FREQ.equals(tag))
            {
                btnRem = new JButton(ImageUtil.getIcon("icons/rem.png"));
                SwingUtils.beautyBtn(btnRem);
                btnRem.setToolTipText("记住了！");
                btnRem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ExecMgr.cachedExec.execute(new Runnable()
                        {
                            public void run()
                            {
                                //注意：此处的操作是有依赖关系的：先写入文件，再读文件
                                //因此，此处切不可将任务分散到几个Thread中同时执行，因为一旦那样做，这样的依赖关系便不成立了！就会导致逻辑出错！
                                //所以，有依赖关系的操作，切不可随意拆分啊！
                                WordbookHelper.rememberWord(word);//记住了某个单词
                                EnergyHall.consumeEnergy(tag);
                                WordbookHelper.refreshPanel();
                            }
                        });
                        fireEditingStopped(); //stopped!!!!
                    }
                });
                panel.setLayout(new BorderLayout());
                panel.add(btnRem, BorderLayout.CENTER);
            }
            else if (WordbookHelper.WORDS_2_REVIEW.equals(tag))
            {
                btnRem = new JButton(ImageUtil.getIcon("icons/rem.png"));
                SwingUtils.beautyBtn(btnRem);
                btnRem.setToolTipText("记住了！");
                btnDisappear = new JButton(ImageUtil.getIcon("icons/delete.png"));
                SwingUtils.beautyBtn(btnDisappear);
                btnDisappear.setToolTipText("别出现了！");
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.add(btnRem);
                panel.add(Box.createHorizontalGlue());
                panel.add(btnDisappear);
                btnRem.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ExecMgr.cachedExec.execute(new Runnable()
                        {
                            public void run()
                            {
                                WordbookHelper.reviewWord(word);//记住了某个单词 
                                EnergyHall.consumeEnergy(tag);
                                WordbookHelper.refreshPanel();
                            }
                        });
                        fireEditingStopped(); //stopped!!!!
                    }
                });
                btnDisappear.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        ExecMgr.cachedExec.execute(new Runnable()
                        {
                            public void run()
                            {
                                WordbookHelper.byeWord(word);//记住了某个单词
                                EnergyHall.consumeEnergy(tag);
                                WordbookHelper.refreshPanel();
                            }
                        });
                        fireEditingStopped();//stopped!!!!
                    }
                });
            }
        }
        return panel;
    }
    
    @Override
    public Object getCellEditorValue()
    {
        return wordFlag;
    }
}
