package com.lnwazg.mydict.bean;

public class UserLevel
{
    private String title;//头衔
    
    private int curLevel;//当前等级
    
    private int curLevelHaveNum;//当前等级拥有的单词数量
    
    private int curLevelNeedAllNum;//当前等级需要的总单词数量
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public int getCurLevel()
    {
        return curLevel;
    }
    
    public void setCurLevel(int curLevel)
    {
        this.curLevel = curLevel;
    }
    
    public int getCurLevelHaveNum()
    {
        return curLevelHaveNum;
    }
    
    public void setCurLevelHaveNum(int curLevelHaveNum)
    {
        this.curLevelHaveNum = curLevelHaveNum;
    }
    
    public int getCurLevelNeedAllNum()
    {
        return curLevelNeedAllNum;
    }
    
    public void setCurLevelNeedAllNum(int curLevelNeedAllNum)
    {
        this.curLevelNeedAllNum = curLevelNeedAllNum;
    }
}
