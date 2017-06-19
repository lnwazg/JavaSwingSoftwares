package com.lnwazg.mydict.bean;

import java.util.List;
import java.util.Map;

public class WordBook
{
    private List<String> words;
    
    private List<String> transResults;
    
    /**
     * 词频
     */
    private Map<String, Integer> wordFreq;
    
    public List<String> getWords()
    {
        return words;
    }
    
    public void setWords(List<String> words)
    {
        this.words = words;
    }
    
    public List<String> getTransResults()
    {
        return transResults;
    }
    
    public void setTransResults(List<String> transResults)
    {
        this.transResults = transResults;
    }
    
    public Map<String, Integer> getWordFreq()
    {
        return wordFreq;
    }
    
    public void setWordFreq(Map<String, Integer> wordFreq)
    {
        this.wordFreq = wordFreq;
    }
}
