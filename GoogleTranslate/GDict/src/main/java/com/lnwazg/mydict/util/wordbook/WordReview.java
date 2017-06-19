package com.lnwazg.mydict.util.wordbook;

public class WordReview
{
    private String word, translation;
    
    private String reviewStat;
    
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
    
    public String getReviewStat()
    {
        return reviewStat;
    }
    
    public void setReviewStat(String reviewStat)
    {
        this.reviewStat = reviewStat;
    }
    
    public WordReview(String word, String translation, String reviewStat)
    {
        super();
        this.word = word;
        this.translation = translation;
        this.reviewStat = reviewStat;
    }
}
