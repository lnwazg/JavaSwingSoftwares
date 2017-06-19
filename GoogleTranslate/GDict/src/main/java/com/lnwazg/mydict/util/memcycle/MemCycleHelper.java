package com.lnwazg.mydict.util.memcycle;

import java.util.ArrayList;
import java.util.List;

import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.mydict.bean.MemCycle;
import com.lnwazg.mydict.util.wordbook.WordReview;

/**
 * 记忆曲线仓库
 
 
->记忆的流程：
查单词 ->  记住了 -> 消失 -> 经过记忆曲线的时间之后 -> 又出现了！->  复习！ -> 记住了！ -> 进入曲线的下一个阶段
       |                                                              |
       V                                                              V
         一直在那！                                                                                                                                                 别出现了！
                                                                      |
                                                                      V
                                                                                                                                                                                   彻底删除！
 * @author nan.li
 * @version 2014-11-18
 */
public class MemCycleHelper
{
    /**
     * 往记忆曲线中新增单词
     * @author nan.li
     * @param word
     * @param translation
     */
    public static void addWord(String word, String translation)
    {
        MemCycle memCycle = GsonCfgMgr.readObject(MemCycle.class);
        if (memCycle == null)
        {
            memCycle = new MemCycle();
            List<RemUnit> remUnits = new ArrayList<RemUnit>();
            memCycle.setRemUnits(remUnits);
        }
        List<RemUnit> remUnits = memCycle.getRemUnits();
        if (remUnits == null)
        {
            remUnits = new ArrayList<RemUnit>();
        }
        //若记忆曲线中已经包含了该单词，则无视之！（不二次添加）
        if (checkExists(remUnits, word))
        {
            return;
        }
        remUnits.add(new RemUnit(word, translation));
        memCycle.setRemUnits(remUnits);
        GsonCfgMgr.writeObject(memCycle);
    }
    
    /**
     * 检查该单词是否已存在于记忆曲线中
     * @author nan.li
     * @param remUnits 
     * @param word
     * @return
     */
    private static boolean checkExists(List<RemUnit> remUnits, String word)
    {
        for (RemUnit remUnit : remUnits)
        {
            if (remUnit.getWord().equals(word))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 和某个单词道别
     * 强行杀死一个单词！
     * @author nan.li
     * @param word
     */
    public static void byeWord(final String word)
    {
        MemCycle memCycle = GsonCfgMgr.readObject(MemCycle.class);
        if (memCycle == null)
        {
            return;
        }
        List<RemUnit> remUnits = memCycle.getRemUnits();
        int achievement = memCycle.getAchievement();
        if (remUnits == null)
        {
            return;
        }
        //从记忆曲线中搜寻待更新的单词
        for (RemUnit remUnit : remUnits)
        {
            if (remUnit.getWord().equals(word))
            {
                //找到了！
                //直接砍死！管你在哪个阶段！
                remUnits.remove(remUnit);
                achievement++;
                memCycle.setRemUnits(remUnits);
                memCycle.setAchievement(achievement);
                break;//一旦寻找到，则立即停止寻找
            }
        }
        GsonCfgMgr.writeObject(memCycle);
    }
    
    /**
     * 复习记忆曲线中的单词
     * @author nan.li
     * @param word
     */
    public static void reviewWord(final String word)
    {
        MemCycle memCycle = GsonCfgMgr.readObject(MemCycle.class);
        if (memCycle == null)
        {
            return;
        }
        List<RemUnit> remUnits = memCycle.getRemUnits();
        int achievement = memCycle.getAchievement();
        if (remUnits == null)
        {
            return;
        }
        //从记忆曲线中搜寻待更新的单词
        for (RemUnit remUnit : remUnits)
        {
            if (remUnit.getWord().equals(word))
            {
                //找到了！
                int curStage = remUnit.getCurStage();//获取当前所处的阶段 [0-6]
                if (curStage < reviewIntervalDays.length - 1)
                {
                    //尚未到达生命周期的尽头，则进入下一个记忆曲线的周期
                    curStage++;
                    remUnit.setCurStage(curStage);
                    remUnit.setNextReviewTimestamp((MemCycleHelper.reviewIntervalDays[curStage] * 24 * 60 * 60 * 1000L) + System.currentTimeMillis());//从当前时刻开始，计算下一个周期的记忆时间
                    //此处timestamp加减运算时务必要加上L，否则就会被int的上限所截断！！！！
                    //这是一个充满血泪史的bug！今日终于得以解决！
                }
                else
                {
                    //已经到达尽头，则销毁之，并将成就榜+1
                    remUnits.remove(remUnit);
                    achievement++;
                    memCycle.setAchievement(achievement);
                }
                break;//一旦寻找到，则立即停止寻找
            }
        }
        memCycle.setRemUnits(remUnits);
        GsonCfgMgr.writeObject(memCycle);
    }
    
    /**
     * 获取前N个待复习的单词
     * @author nan.li
     * @param limit
     * @return
     */
    public static List<WordReview> getTopNReviews(int limit)
    {
        List<WordReview> results = new ArrayList<WordReview>();
        MemCycle memCycle = GsonCfgMgr.readObject(MemCycle.class);
        if (memCycle == null)
        {
            return null;
        }
        List<RemUnit> remUnits = memCycle.getRemUnits();
        if (remUnits == null)
        {
            return null;
        }
        //从记忆曲线中搜寻待复习的单词
        for (RemUnit remUnit : remUnits)
        {
            long nextReviewTimestamp = remUnit.getNextReviewTimestamp();
            if (System.currentTimeMillis() >= nextReviewTimestamp)
            {
                //要复习了！
                results.add(new WordReview(remUnit.getWord(), remUnit.getTranslation(), reviewIntervalDaysStats[remUnit.getCurStage()]));
            }
        }
        //全部找完了！
        if (results.size() > limit)
        {
            results = results.subList(0, limit);
        }
        return results;
    }
    
    /**
     * 复习间隔天数
     */
    public static int[] reviewIntervalDays = {1, 2, 4, 7, 15, 60, 180};
    
    /**
     * 复习间隔天数的提示
     */
    //    public static String[] reviewIntervalDaysStats = {"趁热打铁", "二次回顾", "三次加强", "期中考试", "五步成诗", "六道轮回", "最后补刀"};
    public static String[] reviewIntervalDaysStats = {"一", "二", "三", "中", "五", "六", "终"};
}
