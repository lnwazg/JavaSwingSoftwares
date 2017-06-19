package com.lnwazg.mydict.util.tasks;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务表<br>
 * 存储的是单词，以及对应的查单词的任务对象<br>
 * 目的是：如果查某个单词的任务已经存在，那么有必要将其取消掉
 * @author nan.li
 * @version 2016年4月13日
 */
public class TaskMap
{
    private static ConcurrentHashMap<String, StoreToWordbookTask> wordQueryTaskMap = new ConcurrentHashMap<String, StoreToWordbookTask>();
    
    public static StoreToWordbookTask get(String oldWord)
    {
        return wordQueryTaskMap.get(oldWord);
    }
    
    public static void put(String word, StoreToWordbookTask task)
    {
        wordQueryTaskMap.put(word, task);
    }
    
    public static void remove(String word)
    {
        wordQueryTaskMap.remove(word);
    }
}
