package com.lnwazg.mydict.util.memcycle;

/**
 * 记忆的单元
 * @author nan.li
 * @version 2014-11-18
 */
public class RemUnit
{
    private String word;
    
    private String translation;
    
    private long createTimestamp;//创建的时间点
    
    private int curStage;//当前的阶段。总共0-6，共7个阶段。可以提前结束
    
    private long nextReviewTimestamp;//下一次复习的时间点
    
    public RemUnit(String word, String translation)
    {
        this.word = word;
        this.translation = translation;
        createTimestamp = System.currentTimeMillis();
        curStage = 0;
        nextReviewTimestamp = createTimestamp + (MemCycleHelper.reviewIntervalDays[curStage] * 24 * 60 * 60 * 1000L);
    }
    
    public String getWord()
    {
        return word;
    }
    
    public void setWord(String word)
    {
        this.word = word;
    }
    
    public String getTranslation()
    {
        return translation;
    }
    
    public void setTranslation(String translation)
    {
        this.translation = translation;
    }
    
    public long getCreateTimestamp()
    {
        return createTimestamp;
    }
    
    public void setCreateTimestamp(long createTimestamp)
    {
        this.createTimestamp = createTimestamp;
    }
    
    public int getCurStage()
    {
        return curStage;
    }
    
    public void setCurStage(int curStage)
    {
        this.curStage = curStage;
    }
    
    public long getNextReviewTimestamp()
    {
        return nextReviewTimestamp;
    }
    
    public void setNextReviewTimestamp(long nextReviewTimestamp)
    {
        this.nextReviewTimestamp = nextReviewTimestamp;
    }
    
}
