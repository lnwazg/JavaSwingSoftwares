package com.lnwazg.mydict.bean;

import java.util.List;

import com.lnwazg.mydict.util.memcycle.RemUnit;

/**
 * 记忆曲线
 * @author nan.li
 * @version 2014-11-18
 */
public class MemCycle
{
    /**
     * 记忆单元的列表
     */
    private List<RemUnit> remUnits;
    
    /**
     * 成就
     * 每杀死一个单词,则成就数目+1
     */
    private int achievement;
    
    public List<RemUnit> getRemUnits()
    {
        return remUnits;
    }
    
    public void setRemUnits(List<RemUnit> remUnits)
    {
        this.remUnits = remUnits;
    }
    
    public int getAchievement()
    {
        return achievement;
    }
    
    public void setAchievement(int achievement)
    {
        this.achievement = achievement;
    }
}
