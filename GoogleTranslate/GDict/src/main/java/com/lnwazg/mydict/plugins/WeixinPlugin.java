package com.lnwazg.mydict.plugins;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.date.DateUtils;

/**
 * 微信处理子系统
 * @author nan.li
 * @version 2016年4月25日
 */
public class WeixinPlugin
{
    static String START_CHAR = "$";
    
    public static String process(String input)
    {
        input = StringUtils.substring(input, START_CHAR.length());
        String ret = "";
        switch (input)
        {
            case "":
                ret = "欢迎进入微信处理子系统<br>请输入" + z("help") + "来查看所有支持的指令";
                break;
            case "help":
                ret = "$time: 查看当前时间<br> $quit:退出";
                break;
            case "time":
                ret = DateUtils.getCurStandardDateTimeStr();
                break;
            case "quit":
                ret = "您已退出微信子系统";
                break;
            case "老司机带带我啊":
                ret =
                    "看你这么可怜，就捎你一程吧：<br>MIAD-530、SW-072、MIDD-944、LADY-077、SW-186、STAR444、T28-184、dvdes-635、BOD-277、ARMG-014、JUC-579、BBI-142、MILD-716、FSLV-002、CRS-S014、ODFW-006、SOE-837、Nhdta-141、NADE-783、MIDD-532、IPTD-748、IESP-144、crpd-222、GAR-280、BW248、MXGS173、MIAD-530、RCT-402。ABP-159, SIRO-1774, MIRD-134, MIDE-128, ABP-145, N0962, ABP159, ZIZG-003, CWP-107, IPZ-127, SIRO-1690, HAWA-020, SNIS-166, MIRD136, ABP-138, WANZ-201, STAR-524, SAMA-385, ABP-171, IPZ-409, ABP-108, MIDE128, N0960, JUX-357, SNIS-070, BIST-001, BBI-163, SRS-022, MIRD-102, PPPD-294, ABP-159, SIRO-1774, MIRD-134, MIDE-128, ABP-145, N0962, ABP159, ZIZG-003, CWP-107, IPZ-127, SIRO-1690, HAWA-020, SNIS-166, MIRD136, ABP-138, WANZ-201, STAR-524, SAMA-385, ABP-171, IPZ-409, ABP-108, MIDE128, N0960, JUX-357, SNIS-070, BIST-001, BBI-163, SRS-022, MIRD-102, PPPD-294";
                break;
            default:
                ret = "欢迎进入微信处理子系统<br>请输入" + z("help") + "来查看所有支持的指令";
                break;
        }
        return ret;
    }
    
    /**
     * 装配某个指令
     * @author nan.li
     * @param zhilin
     * @return
     */
    private static String z(String zhilin)
    {
        return START_CHAR + zhilin;
    }
}
