package com.lnwazg.mydict.ui.ext;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.lnwazg.mydict.util.wordbook.WordFlag;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

public class CellRendererWord implements TableCellRenderer
{
    private JPanel panel;
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        panel = new JPanel();
        jLabel = new JLabel();
        WordFlag wordFlag = (WordFlag)value;
        String dispWord = wordFlag.getDispWord();
        String tag = wordFlag.getFlag(); //0:生词    1：查询次数最高的词    2：复习用的单词
        if (WordbookHelper.WORDS_1_FREQ.equals(tag))
        {
            //1查询最高词，红色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=red size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        else if (WordbookHelper.WORDS_2_REVIEW.equals(tag))
        {
            //2复习单词，绿色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=green size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        else if (WordbookHelper.WORDS_0_UNFAMILIAR.equals(tag))
        {
            //0生词，普通的颜色
            StringBuilder stringBuilder = new StringBuilder("<html>");
            stringBuilder.append("<font color=black size=+0>").append(dispWord).append("</font>");
            stringBuilder.append("</html>");
            jLabel.setText(stringBuilder.toString());
        }
        panel.setLayout(new BorderLayout());
        panel.add(jLabel, BorderLayout.CENTER);
        return panel;
    }
}
