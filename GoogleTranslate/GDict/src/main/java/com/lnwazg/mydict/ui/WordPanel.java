package com.lnwazg.mydict.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class WordPanel extends JPanel
{
    private static final long serialVersionUID = -6300590826634138504L;
    
    private JTable table;
    
    public WordPanel()
    {
        setBorder(BorderFactory.createTitledBorder("念念不忘，必有回响"));
        //        this.textArea = new JTextArea();
        //        this.textArea.setColumns(34);
        //        this.textArea.setRows(10);
        //        this.textArea.setLineWrap(true);
        //        this.textArea.setWrapStyleWord(true);
        //        this.textArea.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        //        add(this.textArea);
        //        add(new JScrollPane(this.textArea));
        //        label = new JLabel();
        //        label.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        //        add(label);
        //        add(new JScrollPane(label));
        
        JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBounds(10, 10, 475, 450);
        add(scrollPane);
        table = new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(300, 357));
        //table.setBounds(10, 10, 475, 450);
        scrollPane.setViewportView(table);
    }
    
    public JTable getTable()
    {
        return table;
    }
}
