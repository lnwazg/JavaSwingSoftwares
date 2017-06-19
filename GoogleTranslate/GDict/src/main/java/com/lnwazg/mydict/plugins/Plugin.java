package com.lnwazg.mydict.plugins;

import org.apache.commons.lang3.StringUtils;

/**
 * 扩展插件
 * @author Administrator
 * @version 2016年4月24日
 */
public class Plugin
{
    /**
    * 彩蛋扩展系统！
    * 微信处理器：
    *  输入 $$help ,提示:
    $$hackThunderOfflineVip       解锁小电影高速通道
    $$hackThunderOfflineVip2     解锁所有资源高速通道
    $$带我上车                              随机推荐一个精品番号
    $$带我飙车                              随机推荐100个精品番号
    $$我要当司机                           自动导入100个最优番号，并自动开启迅雷下载
     */
    /**
     * 消息交互子系统<br>
     * 如果子系统可以处理，则返回true;否则返回false
     * @author Administrator
     * @param trimedSrc
     * @return
     */
    public static String subSystemProcessUnit(String input)
    {
        switch (input)
        {
            case "$$":
                return "$$";
            case "$$help":
                return "$$hackThunderOfflineVip       解锁小电影高速通道<br>" + "$$hackThunderOfflineVip2     解锁所有资源高速通道<br>"
                    + "$$带我上车                              随机推荐一个精品番号<br>" + "$$带我飙车                              随机推荐100个精品番号<br>"
                    + "$$我要当司机                           自动导入100个最优番号，并自动开启迅雷下载";
            case "$$hackThunderOfflineVip":
                return "小电影高速通道解锁完毕！";
        }
        if (StringUtils.startsWith(input, "$"))
        {
            return WeixinPlugin.process(input);
        }
        return null;
    }
    
}
