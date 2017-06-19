package com.lnwazg.mydict.util.translate;

/**
 * 语言枚举类
 * @author nan.li
 * @version 2014-11-6
 */
public enum Language
{
    AUTO("自动", "auto"), TAIWAN("中文（繁体）", "zh-TW"), CHINA("中文（简体）", "zh-CN"), ENGLISH("英语", "en"), JAPAN("日语", "ja");
    
    private String name;
    
    private String value;
    
    private Language(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getValue()
    {
        return this.value;
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
}