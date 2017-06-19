package com.lnwazg.mydict.ui.ext;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.lnwazg.kit.swing.ImageUtil;
import com.lnwazg.kit.swing.SwingUtils;
import com.lnwazg.mydict.util.wordbook.WordFlag;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 我的单词按钮渲染器
 * @author nan.li
 * @version 2014-11-17
 */
public class CellRendererHandle implements TableCellRenderer
{
    WordFlag wordFlag;
    
    private JPanel panel;
    
    private JButton btnRem;//记住了！
    
    private JButton btnDisappear;//别出现了！
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        panel = new JPanel();
        wordFlag = (WordFlag)value;
        boolean canCycle = wordFlag.isCanCycle();//是否可以循环（显示循环按钮）
        if (!canCycle)
        {
            //不可循环，则仅仅显示一个学习标签！
            jLabel = new JLabel(ImageUtil.getIcon("icons/learn.png"));
            panel.setLayout(new BorderLayout());
            panel.add(jLabel, BorderLayout.CENTER);
            panel.setToolTipText("每消灭一个单词，就是向着美好的未来迈进一步！");
        }
        else
        {
            String tag = ((WordFlag)value).getFlag();
            if (WordbookHelper.WORDS_0_UNFAMILIAR.equals(tag) || WordbookHelper.WORDS_1_FREQ.equals(tag))
            {
                btnRem = new JButton(ImageUtil.getIcon("icons/rem.png"));
                SwingUtils.beautyBtn(btnRem);
                btnRem.setToolTipText("记住了！");
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
            }
        }
        return panel;
    }
}
