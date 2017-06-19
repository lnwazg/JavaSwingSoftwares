package com.lnwazg.mydict.util.handler.start;

import java.io.File;
import java.io.IOException;

import com.lnwazg.httpkit.server.HttpServer;
import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.util.Constant;

/**
 * 启动一个本地的微型http服务器<br>
 * 改用httpkit，小巧且强大给力！
 * @author nan.li
 * @version 2017年4月26日
 */
public class StartLocalHttpServer implements IHandler
{
    @Override
    public void handle()
    {
        //起一个server实例
        HttpServer server;
        try
        {
            server = HttpServer.bind(Constant.IMG_SERVER_PORT);
            server.addWatchResourceDirRoute(Constant.IMG_SERVER_CONTEXT_PATH, new File(Constant.USER_DIR, Constant.IMG_FOLDER));
            //监听在这个端口处
            server.listen();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
