package com.lnwazg.mydict.util.handler.start;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.mydict.util.Constant;

public class LoadTemplateConfig implements IHandler
{
    
    @Override
    public void handle()
    {
        try
        {
            Constant.JS_TEMPLATE =
                IOUtils.toString(LoadTemplateConfig.class.getClassLoader().getResourceAsStream(Constant.JS_TPL_CONFIG), Constant.UTF8_ENCODING);
            Constant.HTML_TEMPLATE =
                IOUtils.toString(LoadTemplateConfig.class.getClassLoader().getResourceAsStream(Constant.HTML_TPL_CONFIG), Constant.UTF8_ENCODING);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
}
