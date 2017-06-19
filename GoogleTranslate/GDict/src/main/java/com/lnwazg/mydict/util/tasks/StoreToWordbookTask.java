package com.lnwazg.mydict.util.tasks;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.mydict.bean.SystemConfig;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.Utils;
import com.lnwazg.mydict.util.level.LevelMgr;
import com.lnwazg.mydict.util.translate.Language;
import com.lnwazg.mydict.util.translate.TranslateUtil;
import com.lnwazg.mydict.util.wordbook.WordbookHelper;

/**
 * 存储到单词本的工作任务<br>
 * 唯一的缺陷，是跟刷新显示的逻辑紧密耦合了！
 * @author nan.li
 * @version 2016年4月13日
 */
public class StoreToWordbookTask implements Runnable
{
    private String word;
    
    //    Timer timer;
    
    ScheduledFuture<?> future;
    
    public StoreToWordbookTask(String word)
    {
        this.word = word;
        //        timer = new Timer(true);
    }
    
    /**
     * 取消添加到单词本的任务
     * @author nan.li
     */
    public void cancelAdd()
    {
        //        timer.cancel();
        if (future != null)
        {
            //无条件取消，无论成败
            future.cancel(true);
        }
        TaskMap.remove(word);
        System.err.println(String.format("%s被取消!", word));
    }
    
    /**
     * 当前src的翻译结果
     */
    String result;
    
    @Override
    public void run()
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println(String.format("%s请求加入生词本，开始验证...", word));
                TaskMap.remove(word);
                //处理生词本
                if (checkUnFamiliarWord(word))
                {
                    System.out.println(String.format("%s验证成功，批准加入！", word));
                    WordbookHelper.addToWordbook(word, result);
                    Utils.getDetailedTrans(word, result);
                    //每增加一个生词，则更新一下称号！
                    LevelMgr.addWordRefreshTitle();
                }
                else
                {
                    System.err.println(String.format("%s不是生词，已智能忽略！", word));
                }
            }
            
        };
        //        timer.schedule(task, DELAY);
        //延迟一定的时间段时候再执行该任务
        if (SystemConfig.Helper.isAutoQuery())
        {
            future = ExecMgr.scheduledExec.schedule(task, Constant.QUERY_DELAY_MILLSECONDS, TimeUnit.MILLISECONDS);
        }
        else
        {
            //既然是手动查询，那么就不该有任何延迟啦！
            future = ExecMgr.scheduledExec.schedule(task, 0L, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * 检查是否是生词
     * @author nan.li
     * @param word
     * @return
     */
    private boolean checkUnFamiliarWord(String word)
    {
        result = TranslateUtil.translate(word, Language.CHINA.getValue());
        //如果翻译结果刚好包含了之前的值(忽略大小写)，那么可以认为这不是一个生词！
        if (result != null && result.length() >= word.length() && result.substring(0, word.length()).equalsIgnoreCase(word))
        {
            return false;
        }
        return true;
    }
}
