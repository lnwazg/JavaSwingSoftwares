package com.lnwazg.mydict.util.translate.dictcn;

public class DictCnTrans
{
    private String src;//单词
    
    private String yinbiao;//音标
    
    private String fanyi;//翻译
    
    private String liju;//例句
    
    private String bianhua;//变化
    
    private String audioUrl;//音频文件的资源编码
    
    /**
     * 是否网络请求失败了
     */
    private boolean netFail;
    
    public String getYinbiao()
    {
        return yinbiao;
    }
    
    public void setYinbiao(String yinbiao)
    {
        this.yinbiao = yinbiao;
    }
    
    public String getFanyi()
    {
        return fanyi;
    }
    
    public void setFanyi(String fanyi)
    {
        this.fanyi = fanyi;
    }
    
    public String getLiju()
    {
        return liju;
    }
    
    public void setLiju(String liju)
    {
        this.liju = liju;
    }
    
    public String getBianhua()
    {
        return bianhua;
    }
    
    public void setBianhua(String bianhua)
    {
        this.bianhua = bianhua;
    }
    
    public DictCnTrans()
    {
    }
    
    public String getSrc()
    {
        return src;
    }
    
    public void setSrc(String src)
    {
        this.src = src;
    }
    
    public String getAudioUrl()
    {
        return audioUrl;
    }
    
    public void setAudioUrl(String audioUrl)
    {
        this.audioUrl = audioUrl;
    }
    
    @Override
    public String toString()
    {
        return "DictCnTrans [src=" + src + ", yinbiao=" + yinbiao + ", fanyi=" + fanyi + ", liju=" + liju
            + ", bianhua=" + bianhua + ", audioUrl=" + audioUrl + "]";
    }

    public boolean isNetFail()
    {
        return netFail;
    }

    public void setNetFail(boolean netFail)
    {
        this.netFail = netFail;
    }
}
