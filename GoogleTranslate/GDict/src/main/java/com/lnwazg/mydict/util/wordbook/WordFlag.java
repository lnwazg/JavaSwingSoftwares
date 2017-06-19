package com.lnwazg.mydict.util.wordbook;

public class WordFlag
{
    private String dispWord;
    
    private String word, translation, flag;
    
    /**
     * 是否显示记忆曲线按钮
     */
    private boolean canCycle;
    
    public WordFlag(String dispWord, String word, String translation, String flag, boolean canCycle)
    {
        super();
        this.dispWord = dispWord;
        this.word = word;
        this.translation = translation;
        this.flag = flag;
        this.canCycle = canCycle;
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
    
    public String getFlag()
    {
        return flag;
    }
    
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    
    public String getDispWord()
    {
        return dispWord;
    }
    
    public void setDispWord(String dispWord)
    {
        this.dispWord = dispWord;
    }
    
    public boolean isCanCycle()
    {
        return canCycle;
    }
    
    public void setCanCycle(boolean canCycle)
    {
        this.canCycle = canCycle;
    }
}
