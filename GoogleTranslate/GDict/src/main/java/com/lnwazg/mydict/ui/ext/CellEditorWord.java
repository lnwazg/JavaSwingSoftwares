package com.lnwazg.mydict.ui.ext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.wordbook.WordFlag;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 我的单词按钮编辑器
 * @author nan.li
 * @version 2014-11-17
 */
public class CellEditorWord extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = -4105693920426952196L;
    
    WordFlag wordFlag;
    
    private JPanel panel;
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        panel = new JPanel();
        jLabel = new JLabel();
        wordFlag = (WordFlag)value;
        String dispWord = wordFlag.getDispWord();
        String tag = wordFlag.getFlag(); //0:生词    1：查询次数最高的词    2：复习用的单词
        if (WordbookHelper.WORDS_1_FREQ.equals(tag))
        {
            //红色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=red size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        else if (WordbookHelper.WORDS_2_REVIEW.equals(tag))
        {
            //绿色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=green size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        else if (WordbookHelper.WORDS_0_UNFAMILIAR.equals(tag))
        {
            //普通的颜色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=black size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        panel.setLayout(new BorderLayout());
        panel.add(jLabel, BorderLayout.CENTER);
        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                WinMgr.srcPannel.getSrcArea().getDocument().removeDocumentListener(WinMgr.srcPannel.getDocumentListener());//临时关闭事件监听，防止又重复查一遍
                WinMgr.srcPannel.getSrcArea().setText(wordFlag.getWord());
                Utils.getDetailedTrans(wordFlag.getWord(), wordFlag.getTranslation());//从缓存里取出单词的翻译信息
                WinMgr.srcPannel.lastWord = wordFlag.getWord();
                WinMgr.srcPannel.getSrcArea().getDocument().addDocumentListener(WinMgr.srcPannel.getDocumentListener());//设置完成之后，恢复事件监听
                fireEditingStopped(); //stopped!!!!
                //刷新左侧的插图
                WinMgr.imageDialog.refreshImage(wordFlag.getWord());
            }
        });
        return panel;
    }
    
    @Override
    public Object getCellEditorValue()
    {
        return wordFlag;
    }
}
