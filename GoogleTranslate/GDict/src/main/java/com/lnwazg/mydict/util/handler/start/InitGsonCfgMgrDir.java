package com.lnwazg.mydict.util.handler.start;

import com.lnwazg.kit.handlerseq.IHandler;
import com.lnwazg.kit.json.GsonCfgMgr;
import com.lnwazg.mydict.util.Constant;

public class InitGsonCfgMgrDir implements IHandler
{
    @Override
    public void handle()
    {
        GsonCfgMgr.USER_DIR = Constant.USER_DIR;
    }
}
