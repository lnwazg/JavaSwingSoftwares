package com.lnwazg.mydict.ui.ext;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.lnwazg.mydict.util.wordbook.WordFlag;

public class CellRendererTrans implements TableCellRenderer
{
    private JPanel panel;
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column)
    {
        panel = new JPanel();
        jLabel = new JLabel();
        WordFlag wordFlag = (WordFlag)value;
        String translation = wordFlag.getTranslation();
        //普通的颜色
        //        StringBuilder stringBuilder = new StringBuilder("<html>");
        //        stringBuilder.append("<font color=black>").append(translation).append("</font>");
        //        stringBuilder.append("</html>");
        jLabel.setText(translation);
        panel.setLayout(new BorderLayout());
        panel.add(jLabel, BorderLayout.CENTER);
        return panel;
    }
}
