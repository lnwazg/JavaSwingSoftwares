package com.lnwazg.mydict.util.handler.start;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.kit.io.StreamUtils;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.mydict.util.Utils;

public class DownloadDictTask implements IHandler
{
    @Override
    public void handle()
    {
        Logs.i("扩充词库任务启动完毕，开始全网搜索单词...");
        ExecMgr.cachedExec.execute(new Runnable()
        {
            public void run()
            {
                InputStream inputStream = DownloadDictTask.class.getClassLoader().getResourceAsStream("words.txt");
                try
                {
                    List<String> words = IOUtils.readLines(inputStream);
                    if (words != null && words.size() > 0)
                    {
                        for (String line : words)
                        {
                            String word = StringUtils.trim(line);
                            if (StringUtils.isNotEmpty(word) && !StringUtils.startsWith(word, "#"))
                            {
                                //忽略空白行，以及注释
                                Utils.downloadWord(word);//加锁，以保证多线程情况下的数据安全同步
                            }
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    StreamUtils.close(inputStream);
                }
                Logs.i("预设词库已经全部扩充完毕！");
            }
        });
    }
}
