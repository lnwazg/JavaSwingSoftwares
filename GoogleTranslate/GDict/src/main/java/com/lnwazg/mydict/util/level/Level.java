package com.lnwazg.mydict.util.level;

public class Level
{
    private String title;//头衔
    
    private int levelBegin;//开始等级
    
    private int nextLevelNeedWords;//晋级需要的单词数量
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public int getLevelBegin()
    {
        return levelBegin;
    }
    
    public void setLevelBegin(int levelBegin)
    {
        this.levelBegin = levelBegin;
    }
    
    public int getNextLevelNeedWords()
    {
        return nextLevelNeedWords;
    }
    
    public void setNextLevelNeedWords(int nextLevelNeedWords)
    {
        this.nextLevelNeedWords = nextLevelNeedWords;
    }
    
    public Level(String title, int levelBegin, int nextLevelNeedWords)
    {
        super();
        this.title = title;
        this.levelBegin = levelBegin;
        this.nextLevelNeedWords = nextLevelNeedWords;
    }
    
    @Override
    public String toString()
    {
        return "Level [title=" + title + ", levelBegin=" + levelBegin + ", nextLevelNeedWords=" + nextLevelNeedWords + "]";
    }
    
}
