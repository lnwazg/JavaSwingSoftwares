package com.lnwazg.mydict.util.anticheat;

import javax.swing.BorderFactory;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.mydict.util.WinMgr;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 记忆能量的大厅
 * @author nan.li
 * @version 2014-11-28
 */
public class EnergyHall
{
    static Sentinel SEN_1_FREQ;//最常查询
    
    static Sentinel SEN_2_REVIEW;//复习
    
    static
    {
        SEN_1_FREQ = new Sentinel(WordbookHelper.WORDS_1_FREQ);
        SEN_2_REVIEW = new Sentinel(WordbookHelper.WORDS_2_REVIEW);
    }
    
    /**
     * 查询指定类别的记忆能力余额
     * @author nan.li
     * @param type
     * @return
     */
    public static int queryCurrentBalance(String type)
    {
        if (WordbookHelper.WORDS_1_FREQ.equals(type))
        {
            return SEN_1_FREQ.getCurBalance();
        }
        else if (WordbookHelper.WORDS_2_REVIEW.equals(type))
        {
            return SEN_2_REVIEW.getCurBalance();
        }
        return 0;
    }
    
    /**
     * 消费能量（每次当然是只能消费1点能力咯！）
     * @author nan.li
     * @param tag
     */
    public static void consumeEnergy(final String type)
    {
        if (WordbookHelper.WORDS_1_FREQ.equals(type))
        {
            SEN_1_FREQ.consumeEnergy();
        }
        else if (WordbookHelper.WORDS_2_REVIEW.equals(type))
        {
            SEN_2_REVIEW.consumeEnergy();
        }
        refreshStatus();
    }
    
    public static void refreshStatus()
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                String msg = String.format("念念不忘，必有回响  近访[HP:%s AP:%s] 复习[HP:%s AP:%s]",
                    SEN_1_FREQ.getCurEnergy(),
                    SEN_1_FREQ.getCurPower(),
                    SEN_2_REVIEW.getCurEnergy(),
                    SEN_2_REVIEW.getCurPower());
                WinMgr.wordPanel.setBorder(BorderFactory.createTitledBorder(msg));
            }
        });
    }
}
