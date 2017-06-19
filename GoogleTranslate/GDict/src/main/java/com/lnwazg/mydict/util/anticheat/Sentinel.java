package com.lnwazg.mydict.util.anticheat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 岗哨守卫
 * 用于加血
 * @author nan.li
 * @version 2014-11-28
 */
public class Sentinel
{
    //定义一个初始化回血速度为5（最饱满的战斗力），以及当前余额为5（最大余额，不能超过该上限）
    private AtomicInteger curEnergy = new AtomicInteger(EnergyManager.MAX_ENERGY);//当前能量，初始化为满格
    
    private AtomicInteger curPower = new AtomicInteger(PowerManager.MAX_POWER);//初始化为最强战力
    
    private long lastWordTimestamp = System.currentTimeMillis() - 1 * 60 * 1000;
    
    public Sentinel(final String type)
    {
        //巡警一旦被建造出来之后，就要开始自己的巡警生涯！
        //每次巡警执行任务时候，加血的时候如果发现当前血量为0，那么加完血之后要主动刷新一次列表，以保证界面能够正常运转下去！
        ExecMgr.scheduledExec2.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                /**
                每次到达checkpoint时候，检查一分钟之内有没有记忆单词：
                如果记忆了单词，那么当前当前战斗力减半，当前余额加上该减半的战斗力，多余的作废；
                否则，没记忆单词
                检查2分钟内是否记忆了单词，如果
                有：等够了一分钟，战斗力不变
                没有：等够了2分钟，战斗力双倍，但是不能超过5。余额加上该战斗力，但是不能超过上限。
                 */
                //战斗力可以被耗尽，直至什么都干不了！
                //到达了checkpoint
                //                System.out.println(String.format("type %s begin=======================", type));
                if (checkRecent1MiniteMem())
                {
                    //降档
                    curPower = PowerManager.minus(curPower);
                }
                else
                {
                    if (checkRecent2OrMoreMiniteMem())
                    {
                        //战斗力不变
                    }
                    else
                    {
                        //战斗力增加
                        curPower = PowerManager.add(curPower);
                    }
                }
                boolean needRefresh = (curEnergy.get() == EnergyManager.MIN_ENERGY);//当前血量为0，则需要主动刷新一次
                //溢出纠正
                //                curEnergy += curPower;
                curEnergy.addAndGet(curPower.get());
                //                System.out.println(String.format("type %s, curPower：%s", type, curPower));
                //                System.out.println(String.format("type %s, curEnergy：%s", type, curEnergy));
                //                System.out.println(String.format("type %s, EnergyManager.MAX_ENERGY：%s", type, EnergyManager.MAX_ENERGY));
                if (curEnergy.get() > EnergyManager.MAX_ENERGY)
                {
                    curEnergy.set(EnergyManager.MAX_ENERGY);
                }
                //                System.out.println(String.format("type %s, curEnergy after fix：%s", type, curEnergy));
                if (needRefresh)
                {
                    WordbookHelper.refreshPanel();
                }
                EnergyHall.refreshStatus();//主动右侧标题状态
                WordbookHelper.refreshPanel();//主动刷新右侧的内容
                //                System.out.println(String.format("type %s end=======================\n", type));
            }
        }, 0, 1, TimeUnit.MINUTES);//初始化5血，以后每隔一分钟检测一次
    }
    
    /**
     * 检查最近一分钟内是否记忆了单词
     * @author nan.li
     * @return
     */
    private boolean checkRecent1MiniteMem()
    {
        long curTime = System.currentTimeMillis();
        long before1Minute = curTime - (1 * 60 * 1000L);//1分钟前的时间点
        //从最老的往回数了
        if (lastWordTimestamp > before1Minute)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 检查最近两分钟甚至更长时间内是否记忆了单词
     * @author nan.li
     * @return
     */
    private boolean checkRecent2OrMoreMiniteMem()
    {
        long curTime = System.currentTimeMillis();
        long before = curTime - (2 * 60 * 1000L);
        if (lastWordTimestamp > before)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
    
    
    ===================
    演化：
    定义个人的战斗力只有1、2、5这三个档次！并且只有提档、降档两个选项：
    1.不停则降档
    2.停1分钟则不变
    3.停2分钟以及以上则升档
    巡警机制：
    定义一个线程，作为逻辑巡警。该巡警控制着余额的回血速度！
     */
    
    /**
     * 消费能量
     * @author nan.li
     */
    public void consumeEnergy()
    {
        //每次可以消费1点能量
        //为了保证当前余额不会出现负值！
        if (curEnergy.get() > 0)
        {
            curEnergy.decrementAndGet();
        }
        lastWordTimestamp = System.currentTimeMillis();
    }
    
    /**
     * 获取当前能量的余额
     * @author nan.li
     */
    public int getCurBalance()
    {
        return curEnergy.get();
    }
    
    public int getCurEnergy()
    {
        return curEnergy.get();
    }
    
    public int getCurPower()
    {
        return curPower.get();
    }
    
}
