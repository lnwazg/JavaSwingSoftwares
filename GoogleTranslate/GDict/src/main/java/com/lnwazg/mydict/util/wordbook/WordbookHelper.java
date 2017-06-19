package com.lnwazg.mydict.util.wordbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.mydict.bean.WordBook;
import com.lnwazg.mydict.ui.ext.CellEditorHandle;
import com.lnwazg.mydict.ui.ext.CellEditorTrans;
import com.lnwazg.mydict.ui.ext.CellEditorWord;
import com.lnwazg.mydict.ui.ext.CellRendererHandle;
import com.lnwazg.mydict.ui.ext.CellRendererTrans;
import com.lnwazg.mydict.ui.ext.CellRendererWord;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.anticheat.EnergyHall;
import com.lnwazg.mydict.util.memcycle.MemCycleHelper;

/**
 * 单词本的工具类
 * @author nan.li
 * @version 2014-11-17
 */
public class WordbookHelper
{
    /**
     * 生词
     */
    public static final String WORDS_0_UNFAMILIAR = "0";
    
    /**
     * 查询次数最多的单词
     */
    
    public static final String WORDS_1_FREQ = "1";
    
    /**
     * 复习用的单词
     */
    public static final String WORDS_2_REVIEW = "2";
    
    /**
     * 将生词添加到单词本
     * @author nan.li
     * @param word
     * @param wordTranslation
     */
    public static void addToWordbook(String word, String wordTranslation)
    {
        WordBook wordBook = GsonCfgMgr.readObject(WordBook.class);
        if (wordBook == null)
        {
            wordBook = new WordBook();
            wordBook.setWords(new ArrayList<String>());
            wordBook.setTransResults(new ArrayList<String>());
            wordBook.setWordFreq(new HashMap<String, Integer>());
        }
        List<String> words = wordBook.getWords();
        if (words == null)
        {
            words = new ArrayList<String>();
        }
        List<String> trans = wordBook.getTransResults();
        if (trans == null)
        {
            trans = new ArrayList<String>();
        }
        Map<String, Integer> wordFreq = wordBook.getWordFreq();
        if (wordFreq == null)
        {
            wordFreq = new HashMap<String, Integer>();
        }
        if (words.contains(word))
        {
            int position = words.indexOf(word);
            words.remove(position);
            trans.remove(position);
        }
        words.add(0, word);
        trans.add(0, wordTranslation);
        wordBook.setWords(words);
        wordBook.setTransResults(trans);
        Integer oldVal = wordFreq.get(word);
        if (null == oldVal)
        {
            wordFreq.put(word, 1);
        }
        else
        {
            wordFreq.put(word, oldVal + 1);
        }
        wordBook.setWordFreq(wordFreq);
        GsonCfgMgr.writeObject(wordBook);//写入完毕
        refreshPanel();//最后刷新显示器屏幕内容
    }
    
    /**
    * 刷新右侧的表格
    * @author nan.li
    */
    public static void refreshPanel()
    {
        WordBook wordBook = GsonCfgMgr.readObject(WordBook.class);
        if (wordBook == null)
        {
            return;
        }
        List<String> words = wordBook.getWords();
        List<String> transResults = wordBook.getTransResults();
        List<WordFreq> topFreqs = getTopNFreqs(Constant.FREQ_NUM);
        List<WordReview> topReviews = getTopNReviews(Constant.REVIEW_NUM);
        int rowsWords = 0, rowsFreqs = 0;
        int rowsReviews = 0;
        if (words != null)
        {
            rowsWords = words.size();
        }
        if (topFreqs != null)
        {
            rowsFreqs = topFreqs.size();
        }
        if (topReviews != null)
        {
            rowsReviews = topReviews.size();
        }
        
        //显示机制：显示的曲线按钮的个数的依据就是当前余额的数量。
        final int rows = rowsWords + rowsFreqs + rowsReviews;
        final WordFlag[] data = new WordFlag[rows];
        if (rowsFreqs > 0)
        {
            int curBalance = EnergyHall.queryCurrentBalance(WORDS_1_FREQ);//查询当前的体力余额（最高为5）
            //查询次数最多
            for (int i = 0; i < rowsFreqs; i++)
            {
                WordFreq wordFreq = topFreqs.get(i);
                //假如余额为1，那么只有第1个单词可以记忆
                //假如余额为2，那么只有第1个和第2个单词可以记忆
                //假如余额为0，那么没有单词可以记忆
                if (i - 0 + 1 > curBalance)
                {
                    //超过了余额，那么记忆曲线按钮便不再显示
                    data[i] = new WordFlag(String.format("%s [%d]", wordFreq.getWord(), wordFreq.getFreq()), wordFreq.getWord(), wordFreq.getTranslation(),
                        WORDS_1_FREQ, false);//0:生词    1：查询次数最高的词    2：复习用的单词
                }
                else
                {
                    data[i] = new WordFlag(String.format("%s [%d]", wordFreq.getWord(), wordFreq.getFreq()), wordFreq.getWord(), wordFreq.getTranslation(),
                        WORDS_1_FREQ, true);//0:生词    1：查询次数最高的词    2：复习用的单词
                }
                
            }
        }
        if (rowsReviews > 0)
        {
            int curBalance = EnergyHall.queryCurrentBalance(WORDS_2_REVIEW);
            for (int i = rowsFreqs; i < rowsFreqs + rowsReviews; i++)
            {
                WordReview wordReview = topReviews.get(i - rowsFreqs);
                if (i - rowsFreqs + 1 > curBalance)
                {
                    data[i] = new WordFlag(String.format("%s [%s]", wordReview.getWord(), wordReview.getReviewStat()), wordReview.getWord(),
                        wordReview.getTranslation(), WORDS_2_REVIEW, false);//0:生词    1：查询次数最高的词    2：复习用的单词
                }
                else
                {
                    data[i] = new WordFlag(String.format("%s [%s]", wordReview.getWord(), wordReview.getReviewStat()), wordReview.getWord(),
                        wordReview.getTranslation(), WORDS_2_REVIEW, true);//0:生词    1：查询次数最高的词    2：复习用的单词
                }
            }
        }
        if (rowsWords > 0)
        {
            //生词
            for (int i = rowsFreqs + rowsReviews; i < rowsFreqs + rowsReviews + rowsWords; i++)
            {
                data[i] = new WordFlag(words.get(i - (rowsFreqs + rowsReviews)), words.get(i - (rowsFreqs + rowsReviews)),
                    transResults.get(i - (rowsFreqs + rowsReviews)), WORDS_0_UNFAMILIAR, false);//0:生词    1：查询次数最高的词    2：复习用的单词
            }
        }
        final int finalRowsReviews = rowsReviews;
        final int explainWidth = calcExplainWidth(data);
        ExecMgr.guiExec.execute(new Runnable()
        {
            public void run()
            {
                JTable table = WinMgr.wordPanel.getTable();
                final String[] columnNames = {"单词", "释义", "操作"};
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.setModel(new DefaultTableModel()
                {
                    private static final long serialVersionUID = 1L;
                    
                    @Override
                    public Object getValueAt(int row, int column)
                    {
                        return data[row];//每行的所有数据共用同一条“大数据”
                    }
                    
                    @Override
                    public String getColumnName(int column)
                    {
                        return columnNames[column];
                    }
                    
                    @Override
                    public int getRowCount()
                    {
                        return rows;
                    }
                    
                    @Override
                    public int getColumnCount()
                    {
                        return 3;
                    }
                    
                    //            @Override
                    //            public void setValueAt(Object aValue, int row, int column)
                    //            {
                    //                data[row] = (WordFlag)aValue;
                    //                fireTableCellUpdated(row, column);
                    //            }
                    
                    @Override
                    public boolean isCellEditable(int row, int column)
                    {
                        //                //只有最后的一列可编辑
                        //                if (column == 2)
                        //                {
                        //                    return true;
                        //                }
                        //                else
                        //                {
                        //                    return false;
                        //                }
                        return true;
                    }
                });
                
                table.getColumnModel().getColumn(2).setCellEditor(new CellEditorHandle());
                table.getColumnModel().getColumn(2).setCellRenderer(new CellRendererHandle());
                
                table.getColumnModel().getColumn(1).setCellEditor(new CellEditorTrans());
                table.getColumnModel().getColumn(1).setCellRenderer(new CellRendererTrans());
                
                table.getColumnModel().getColumn(0).setCellEditor(new CellEditorWord());
                table.getColumnModel().getColumn(0).setCellRenderer(new CellRendererWord());
                
                table.setRowSelectionAllowed(false);
                // table.setPreferredSize(new Dimension(200, 200 ));
                table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                //        table.getColumnModel().getColumn(2).setMaxWidth(80);
                //        table.getColumnModel().getColumn(2).setMinWidth(40);
                if (finalRowsReviews > 0)
                {
                    //need review,so be wider
                    table.getColumnModel().getColumn(2).setMaxWidth(80);
                    table.getColumnModel().getColumn(2).setMinWidth(80);
                    //            table.getColumnModel().getColumn(2).setPreferredWidth(80);
                }
                else
                {
                    table.getColumnModel().getColumn(2).setMaxWidth(40);
                    table.getColumnModel().getColumn(2).setMinWidth(40);
                }
                //        table.getColumnModel().getColumn(1).setMaxWidth(120);
                //        table.getColumnModel().getColumn(1).setMinWidth(90);
                //        table.getColumnModel().getColumn(1).setPreferredWidth(90);
                //设置一个确定的大小
                //                System.out.println("a".getBytes().length);
                //                System.out.println("A".getBytes().length);
                //                System.out.println("中".getBytes().length);
                //        System.out.println(",".getBytes().length);
                //        System.out.println("，".getBytes().length);
                //        FontMetrics fontMetrics =
                //            java.awt.Toolkit.getDefaultToolkit().getFontMetrics(new Font("MSGothic", Font.PLAIN, 16));
                //        System.out.println(fontMetrics.stringWidth("W"));
                
                //        System.out.println(String.format("explainWidth: %s", explainWidth));
                
                
                //                table.getColumnModel().getColumn(1).setMinWidth(36);//保证标题至少要能显示出来
                //                if (explainWidth >= 36)
                //                {
                //                    table.getColumnModel().getColumn(1).setMaxWidth(explainWidth);
                //                    table.getColumnModel().getColumn(1).setPreferredWidth(explainWidth);
                //                }
                //不再自适应，因为自适应不好看！
                
                table.getColumnModel().getColumn(0).setWidth(100);
                
                //        table.getColumnModel().getColumn(0).setMaxWidth(160);
                //        table.getColumnModel().getColumn(0).setPreferredWidth(80);
            }
        });
    }
    
    /**
     * 计算释义列的真实宽度
     * @author nan.li
     * @param data
     * @return
     */
    private static int calcExplainWidth(WordFlag[] data)
    {
        if (data.length == 0)
        {
            return 90;//默认宽度
        }
        int maxBytesLength = 0;
        for (WordFlag wordFlag : data)
        {
            String translation = wordFlag.getTranslation();
            int thisTrueLengthWidth = 0;
            //此处的translation是有可能为null的，所以需要事先判断：只有当translation非空的时候才参与字符空间数目统计
            if (StringUtils.isNotBlank(translation))
            {
                for (int i = 0; i < translation.length(); i++)
                {
                    if (translation.charAt(i) == 1)
                    {
                        //英文、数字等
                        thisTrueLengthWidth += 1;//占用1个字符的空间
                    }
                    else
                    {
                        //中文、中文标点等
                        thisTrueLengthWidth += 2;//占用2个字符的空间
                    }
                }
                if (thisTrueLengthWidth > maxBytesLength)
                {
                    maxBytesLength = thisTrueLengthWidth;
                }
            }
        }
        //        System.out.println(String.format("maxBytesLength: %s", maxBytesLength));
        //        FontMetrics fontMetrics =
        //            java.awt.Toolkit.getDefaultToolkit().getFontMetrics(new Font(Font.SERIF, Font.PLAIN, 16));
        //        System.out.println(fontMetrics.stringWidth("W"));
        //        System.out.println(fontMetrics.stringWidth("中"));
        
        if (maxBytesLength > 0)
        {
            int unitWidth = 9;//单位字符的宽度
            return maxBytesLength * unitWidth;
        }
        else
        {
            return 90;//默认宽度
        }
    }
    
    /**
     * 获取前3个待复习的单词
     * @author nan.li
     * @param i
     * @return
     */
    private static List<WordReview> getTopNReviews(int limit)
    {
        return MemCycleHelper.getTopNReviews(limit);
    }
    
    /**
     * 获取最常查询的N个单词
     * @author nan.li
     * @param i
     */
    private static List<WordFreq> getTopNFreqs(int limit)
    {
        WordBook wordBook = GsonCfgMgr.readObject(WordBook.class);
        if (wordBook == null)
        {
            return null;
        }
        List<String> words = wordBook.getWords();
        List<String> transResults = wordBook.getTransResults();
        if (words.size() == 0 || transResults.size() == 0)
        {
            return null;
        }
        Map<String, Integer> wordFreqMap = wordBook.getWordFreq();
        if (wordFreqMap == null)
        {
            return null;
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(wordFreqMap.entrySet());
        Collections.reverse(entries);//翻转整个数组(很有必要。因为默认情况下新的单元是添加到了map的后面。而实际上需要将新单元显示在前面！)
        //排序
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>()
        {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
            {
                return (o2.getValue() - o1.getValue());
            }
        });
        if (entries.size() > limit)
        {
            entries = entries.subList(0, limit);
        }
        List<String> toBeRemovedKeys = new ArrayList<String>();
        
        List<WordFreq> resultList = new ArrayList<WordFreq>();
        for (int i = 0; i < entries.size(); i++)
        {
            String word = entries.get(i).getKey();
            int freq = entries.get(i).getValue();
            int index = words.indexOf(word);
            if (index == -1)
            {
                toBeRemovedKeys.add(word);
                continue;
            }
            else
            {
                String translation = transResults.get(index);
                resultList.add(new WordFreq(word, translation, freq));
            }
        }
        
        //将所有待删除的删光
        if (toBeRemovedKeys.size() > 0)
        {
            for (int i = 0; i < toBeRemovedKeys.size(); i++)
            {
                wordFreqMap.remove(toBeRemovedKeys.get(i));
            }
            wordBook.setWordFreq(wordFreqMap);
            GsonCfgMgr.writeObject(wordBook);
        }
        return resultList;
    }
    
    /**
     * 记忆某个单词
     * @author nan.li
     * @param word
     */
    public static void rememberWord(final String word)
    {
        //将某个单词从单词本中删除，并且添加到记忆曲线仓库中
        //第一步，将某个单词从单词本中删除
        WordBook wordBook = GsonCfgMgr.readObject(WordBook.class);
        if (wordBook == null)
        {
            return;
        }
        List<String> words = wordBook.getWords();
        List<String> transResults = wordBook.getTransResults();
        if (words.size() == 0 || transResults.size() == 0)
        {
            return;
        }
        Map<String, Integer> wordFreqMap = wordBook.getWordFreq();
        if (wordFreqMap == null)
        {
            return;
        }
        
        int index = words.indexOf(word);
        if (index == -1)
        {
            return;
        }
        String translation = transResults.get(index);
        words.remove(index);
        transResults.remove(index);
        wordFreqMap.remove(word);
        
        wordBook.setWordFreq(wordFreqMap);
        wordBook.setWords(words);
        wordBook.setTransResults(transResults);
        GsonCfgMgr.writeObject(wordBook);
        
        //第二步，添加到记忆曲线仓库中
        MemCycleHelper.addWord(word, translation); // 添加到记忆曲线中
    }
    
    /**
     * 复习单词
     * @author nan.li
     * @param word
     */
    public static void reviewWord(String word)
    {
        //单词本中本来就不存在该单词了，而记忆曲线中有。因此直接更新记忆曲线即可
        MemCycleHelper.reviewWord(word); //复习单词
    }
    
    public static void byeWord(String word)
    {
        MemCycleHelper.byeWord(word);//和某个单词永别！
    }
}
