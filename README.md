# JavaSwingSoftwares
以下我自己编写的一系列基于Java Swing的桌面应用，开箱即用，简单便捷。  
编写这些软件的初衷，是为了让我所掌握的Java技术更好地服务于我的工作与生活。  
当我发现，使用swing也可以绘制出漂亮且高效的桌面应用时，我开启了新世界！  
  
请注意，使用这些软件的前提是要安装JDK8运行环境。  
JDK8的下载地址为：http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html   
若有疑问或建议，请邮件我，我的联系方式如下：  

QQ：  914096874  
邮箱: lnwazg@126.com  
联系电话：18913957098


## 1. AutoShadow 基于java swing的翻墙工具
这款小工具可以帮助你有效地翻墙。软件运行截图如下:  
<img src="screenshots/1.png">

### 使用步骤：  
1. 下载并安装jdk8  
2. 下载AutoShadow文件夹内的AutoShadow-1.1.jar，双击打开  
3. 然后点击软件的“在线1”或者“在线2”或者“在线3”按钮，如果线路可用，则软件的任务栏图标将显示为绿色的<img src="screenshots/2.png">，否则为红色的。绿色状态下可以正常翻墙，红色状态下无法翻墙。  
4. 打开chrome浏览器，在地址栏输入  chrome://extensions/  
5. 将附件里的SwitchySharp.crx拖入到你的扩展程序里，并安装
6. 打开SwitchySharp的配置选项，点击“从文件恢复”，选择附件里下载的SwitchyOptions.bak，导入即可   
<img src="screenshots/3.png">   
7. 点击浏览器右上角的插件，下拉选择“自动切换模式”，然后尽情享用吧。   
 <img src="screenshots/4.png">   
8. 打开https://www.youtube.com/ 检测一下你是否可以正常翻墙。如果可以正常打开，说明已经翻墙成功了，have fun！  
 <img src="screenshots/5.png">   

## 2. GoogleTranslate  惊鸿一瞥 背单词神器
软件特色：  
1. 内置艾宾浩斯记忆曲线，高效有趣地背单词  
2. 快捷键PrintScreen一键呼出\关闭  
3. 中翻英、英翻中、独家配备精美单词插图解释，可视化的记忆，高效自然  
4. 智能发音、等级称号成就系统，越用你的英文水平就越强大

运行截图如下：  
![xxx](screenshots/22.png)
![xxx](screenshots/23.png)
![xxx](screenshots/21.png)

下载GoogleTranslate-1.3.3.jar，双击运行即可。
另附详尽的使用说明书《GoogleTranslate用户手册 v1.1.docx》

## 3. MY_ZOO_SERVER  我的zookeeper服务器
通信层基于kryonet，功能高仿zookeeper，但是更加可视化、实用化。  
提供了命名服务、服务注册、服务卸载、智能下线检测等功能。  
服务信息在服务上线时自动注册，服务下线时自动解除注册。  
也可以根据自己的需要手动调整在线服务的注册信息。  
![xxx](screenshots/41.png)

## 4. CACHE_SERVER  纯java的类redis缓存服务器  
这款缓存服务器可以让你方便地存储任意的键值对到本地磁盘中，api高仿redis，使用轻巧方便，可以自定义端口号，双击即可使用。  
可以和MY_ZOO_SERVER进行集成，通用命名服务进行访问，所有的配置信息都由MY_ZOO_SERVER统一管理，高效可视化。

提供的基本功能：  
put(Serializable key, Serializable value)     将任意键值对放入到redis缓存   
get(Serializable key, Class<T> classType)     根据key取出相对应的缓存对象，并将结果转换为指定的类型  
get(Serializable key)                         根据key取出相对应的缓存对象                
remove(Serializable key)                      移除掉某个key  
incr(Serializable key)						  将某个key对应的对象的值+1  
decr(Serializable key)                        将某个key对应的对象的值-1    

##### 脱离MY_ZOO_SERVER单独运行的截图如下：  
![xxx](screenshots/11.png)

##### 配合MY_ZOO_SERVER，运行状态如下：  
服务开启前状态：  
![xxx](screenshots/12.png)
服务开启后状态： 
![xxx](screenshots/13.png)

通过以上的状态对比，我们就能明白MY_ZOO_SERVER的设计思想及其可视化效果的优势以及特色。


CACHE_SERVER配合MY_ZOO_SERVER使用的代码示例：  

```

package com.lnwazg.component.demo;

import java.util.Map;

import com.lnwazg.cache.client.RemoteCacheServer;
import com.lnwazg.cache.proxy.Cache;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.PrepareStartOnce;
import com.lnwazg.kit.testframework.anno.TestCase;
import com.lnwazg.myzoo.framework.MyZooKeeper;

/**
 * 我的缓存服务器，使用demo
 * @author nan.li
 * @version 2017年2月28日
 */
public class MyCacheServerDemo
{
    Cache cache = null;
    
    /**
     * 初始化测试环境
     * @author nan.li
     */
    @PrepareStartOnce
    void prepareGlobalOnlyOnce()
    {
        boolean success = MyZooKeeper.initDefaultConfig();
        if (success)
        {
            Map<String, String> m = MyZooKeeper.queryServiceConfigByNodeNameStartWithThenChooseOne("remoteCache");
            //初始化redis服务器
            cache = RemoteCacheServer.initConfig(m.get("server"), Integer.valueOf(m.get("port")));
        }
        else
        {
            Logs.e("MyZooKeeper集群初始化失败！");
        }
    }
    
    /**
     * 存任意对象
     * @author nan.li
     */
    //    @TestCase
    void testSave()
    {
        //存
        cache.put(5, 100.78D);
        cache.put(5, 100.746348965893486948d);
    }
    
    /**
     * 存一个整数
     * @author nan.li
     */
    //          @TestCase
    void testSaveInteger()
    {
        //存
        cache.put(5, 200);
    }
    
    /**
     * 执行“+1”操作
     * @author nan.li
     */
    @TestCase
    void testIncrement()
    {
        cache.incr(5);
    }
    
    /**
     * 获取某个key所存储的对象
     * @author nan.li
     */
    @TestCase
    void testGet()
    {
        //取
        System.out.println(cache.get(5));
    }
    
    public static void main(String[] args)
    {
        TF.l(MyCacheServerDemo.class);
    }
}


```
输出的日志如下：  

```
>>>>>>>>>>>>>>>>>>>>>  开始全局准备  >>>>>>>>>>>>>>>>>>>>>>>
>>>执行@PrepareStartOnce方法： prepareGlobalOnlyOnce  >>>
00:00  INFO: [kryonet] Connecting: myzoo.lnwazg.com/127.0.0.1:54555
00:00  INFO: [kryonet] Connection 20 connected: myzoo.lnwazg.com/127.0.0.1
[I] 已成功连接MyZooKeeper服务器！
[I] 客户端收到 msg:com.lnwazg.myzoo.bean.Msg@dd3ad49[
  token=3yrXsjD3XZEbKrqywqLQMvDujtYK1YFXBs4jTcalwmldhHve6Q9CV18TpMukHHMa
  path=/client/queryServiceConfigByNodeNameStartWithResult
  map=<null>
  list=[{node=remoteCache-4a637900d525d2e69b8c92bb6debdcfc, server=10.10.10.10, port=3333, group=remoteCache}]
  obj=<null>
]

[I] 读取到MyZooKeeper配置信息列表:[{node=remoteCache-4a637900d525d2e69b8c92bb6debdcfc, server=10.10.10.10, port=3333, group=remoteCache}]
[I] 列表总计1条数据，随机挑选出的第0条MyZooKeeper配置信息:{node=remoteCache-4a637900d525d2e69b8c92bb6debdcfc, server=10.10.10.10, port=3333, group=remoteCache}
【测试共计耗时 343 毫秒】
<<<<<<<<<<<<<<<<<<<<<  全局准备结束！   <<<<<<<<<<<<<<<<<<<<<<<

>>>>>>>>>>>>>>>>>>>>>  开始测试方法： testIncrement  >>>>>>>>>>>>>>>>>>>>>>>

【测试共计耗时 42 毫秒】
<<<<<<<<<<<<<<<<<<<<<  testIncrement 方法测试结束！   <<<<<<<<<<<<<<<<<<<<<<<

>>>>>>>>>>>>>>>>>>>>>  开始测试方法： testGet  >>>>>>>>>>>>>>>>>>>>>>>
206
【测试共计耗时 1 毫秒】
<<<<<<<<<<<<<<<<<<<<<  testGet 方法测试结束！   <<<<<<<<<<<<<<<<<<<<<<<

```



## 5.MQ_SERVER  我的消息服务器  
##### 特色：   
1. 便携性强，便于部署，端口可随便切换   
2. 与zeromq和sqlite完美集成，极速启动，高效快捷。  
3. zeromq强大的缓存能力 + sqlite数据持久化，保证了消息持久化不丢失。   
4. 客户端将消息智能合并、打包压缩，并批量发送。经过合并压缩后，大大降低了网络交互次数与通信的流量消化。  
5. 消息持久化全部异步完成，无阻塞。    
6. 消息发送与接收的运行过程可视化。  


#####与MY_ZOO_SERVER集成后，启动后的截图如下：  
![](screenshots/51.png)






