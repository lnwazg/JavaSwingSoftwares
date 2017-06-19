package com.lnwazg.mydict.util.anticheat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 战斗力管理器
 * @author nan.li
 * @version 2014-11-28
 */
public class PowerManager
{
    public static final int ZERO_POWER = 0;
    
    public static final int MAX_POWER = 5;//最强战力
    
    /**
     * 战斗力降档
     * 每次降一格，直到变成0
     * 2015-1-14  战力不再急剧降低，而是缓缓降低！这样才更加真实而贴切！
     * @author nan.li
     * @param curPower
     * @return
     */
    public static AtomicInteger minus(AtomicInteger curPower)
    {
        if (curPower.get() <= ZERO_POWER)
        {
            curPower.set(ZERO_POWER);
        }
        else
        {
            curPower.decrementAndGet();
        }
        return curPower;
    }
    
    /**
     * 战斗力升档
     * 每次升一格，直到变成最大
     * @author nan.li
     * @param curPower
     * @return
     */
    public static AtomicInteger add(AtomicInteger curPower)
    {
        if (curPower.get() >= MAX_POWER)
        {
            curPower.set(MAX_POWER);
        }
        else
        {
            curPower.incrementAndGet();
        }
        return curPower;
    }
}
