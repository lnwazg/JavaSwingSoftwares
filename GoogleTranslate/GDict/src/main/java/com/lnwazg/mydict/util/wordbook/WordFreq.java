package com.lnwazg.mydict.util.wordbook;

/**
 * 词频率
 * @author nan.li
 * @version 2014-11-17
 */
public class WordFreq
{
    private String word, translation;
    
    private int freq;
    
    public WordFreq(String word, String translation, int freq)
    {
        super();
        this.word = word;
        this.translation = translation;
        this.freq = freq;
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
    
    public int getFreq()
    {
        return freq;
    }
    
    public void setFreq(int freq)
    {
        this.freq = freq;
    }
}
