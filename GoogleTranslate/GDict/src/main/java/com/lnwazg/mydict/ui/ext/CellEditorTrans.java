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

import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.wordbook.WordFlag;

/**
 * 我的单词按钮编辑器
 * @author nan.li
 * @version 2014-11-17
 */
public class CellEditorTrans extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = -4105693920426952196L;
    
    private WordFlag wordFlag;
    
    private JPanel panel;
    
    private JLabel jLabel;
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        panel = new JPanel();
        jLabel = new JLabel();
        wordFlag = (WordFlag)value;
        String translation = wordFlag.getTranslation();
        //普通的颜色
        //        StringBuilder stringBuilder = new StringBuilder("<html>");
        //        stringBuilder.append("<font color=black>").append(translation).append("</font>");
        //        stringBuilder.append("</html>");
        jLabel.setText(translation);
        panel.setLayout(new BorderLayout());
        panel.add(jLabel, BorderLayout.CENTER);
        panel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                WinMgr.srcPannel.getSrcArea().getDocument().removeDocumentListener(WinMgr.srcPannel.getDocumentListener());
                WinMgr.srcPannel.getSrcArea().setFont(Constant.BigFont);
                WinMgr.srcPannel.getSrcArea().setText(wordFlag.getWord());
                Utils.getDetailedTrans(wordFlag.getWord(), wordFlag.getTranslation());
                WinMgr.srcPannel.lastWord = wordFlag.getWord();
                WinMgr.srcPannel.getSrcArea().getDocument().addDocumentListener(WinMgr.srcPannel.getDocumentListener());
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
