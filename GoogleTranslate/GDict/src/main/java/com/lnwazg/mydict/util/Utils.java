package com.lnwazg.mydict.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.lnwazg.kit.executor.ExecMgr;
import com.lnwazg.kit.http.DownloadKit;
import com.lnwazg.kit.http.apache.httpclient.HttpClientKit;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.swing.ImageUtil;
import com.lnwazg.mydict.util.localdic.DictMap;
import com.lnwazg.mydict.util.translate.Language;
import com.lnwazg.mydict.util.translate.TranslateUtil;
import com.lnwazg.mydict.util.translate.dictcn.DictCnTrans;

/**
 * 工具
 * @author  nan.li
 * @version 2013-12-26
 */
public class Utils
{
    /**
    * Logger for this class
    */
    public static final Log logger = LogFactory.getLog(Utils.class);
    
    /**
     * 下载单词
     * @author nan.li
     * @param word
     */
    public static void downloadWord(String word)
    {
        //        System.out.println(String.format("开始下载单词 %s", word));
        String htmlString = DictMap.get(word);
        String audioUrl = DictMap.get(String.format("%s[audio]", word));
        if (htmlString == null || StringUtils.indexOf(htmlString, Constant.IMGVISITED) == -1 || StringUtils.indexOf(htmlString, "$$google$$") != -1
            || StringUtils.indexOf(htmlString, Constant.DICTCN_VISITED) == -1)
        {
            String googleResult = TranslateUtil.translate(word, Language.CHINA.getValue());
            //            System.out.println(String.format("检测到单词 %s不存在，开始联网查询...", word));
            System.out.println(String.format("智能扩充词库: %s", word));
            htmlString = StringUtils.replace(Constant.HTML_TEMPLATE, "$$google$$", googleResult);//存储google翻译的结果
            //dictCn翻译的结果
            DictCnTrans dictCnTrans = TranslateUtil.translateDictCn(word);
            StringBuilder dictCn = new StringBuilder();
            if (dictCnTrans != null && !dictCnTrans.isNetFail())
            {
                audioUrl = dictCnTrans.getAudioUrl();
                String NEW_LINE = "\n";
                dictCn.append("<div class=\"t\">---------------------------</div>").append(NEW_LINE);
                if (dictCnTrans.getYinbiao() != null)
                {
                    dictCn.append(String.format("%s&nbsp;&nbsp;&nbsp;<span class=\"p\">%s</span><br>", word, dictCnTrans.getYinbiao())).append(NEW_LINE);
                }
                if (dictCnTrans.getFanyi() != null)
                {
                    dictCn.append(String.format("<div id=\"e\">%s</div><br>", dictCnTrans.getFanyi())).append(NEW_LINE);
                }
                if (dictCnTrans.getLiju() != null)
                {
                    dictCn.append(String.format("<div class=\"t\">------例句与用法------</div><div id=\"s\">%s</div><br>", dictCnTrans.getLiju())).append(NEW_LINE);
                }
                
                if (dictCnTrans.getBianhua() != null)
                {
                    dictCn.append(String.format("<div class=\"t\">------词形变化------</div><div id=\"t\">%s</div><br>", dictCnTrans.getBianhua()))
                        .append(NEW_LINE);
                }
            }
            //图片获取
            String imgUrl = getWordTranslateImageUrl(word);
            if (StringUtils.isEmpty(imgUrl))
            {
                htmlString = StringUtils.replace(htmlString, "$$image$$", Constant.IMGVISITED);
            }
            else
            {
                if (Constant.MSG_NET_FAIL.equals(imgUrl))
                {
                    //只是这一次网络请求图片失败了，那么下次要继续尝试哦
                    htmlString = StringUtils.replace(htmlString, "$$image$$", "");
                }
                else
                {
                    imgUrl = downloadToLocal(imgUrl, word);
                    if (StringUtils.isEmpty(imgUrl))
                    {
                        //下载到本地失败了！！！
                        //这个图片本来有问题吧！
                        htmlString = StringUtils.replace(htmlString, "$$image$$", Constant.IMGVISITED);
                    }
                    else
                    {
                        htmlString = StringUtils.replace(htmlString,
                            "$$image$$",
                            Constant.IMGVISITED + ImageUtil.getAutoZoonImageHtmlByMaxDimen(imgUrl, Constant.IMAGE_MAX_DIMEN));
                    }
                }
            }
            
            String dictCnStr = dictCn.toString();
            //可能有值，也可能没值
            //1.有值,必定请求成功了（DICTCN_VISITED）
            //2.没值，网络请求失败，或者本来就不存在这个单词（DICTCN_VISITED）
            if (StringUtils.isNotEmpty(dictCnStr))
            {
                htmlString = StringUtils.replace(htmlString, "$$dictCn$$", Constant.DICTCN_VISITED + dictCn.toString());
            }
            else
            {
                if (dictCnTrans != null && dictCnTrans.isNetFail())
                {
                    //网络请求失败了
                    htmlString = StringUtils.replace(htmlString, "$$dictCn$$", "");
                }
                else
                {
                    //返回的结果为null，也就是本来就不存在这个单词！
                    htmlString = StringUtils.replace(htmlString, "$$dictCn$$", Constant.DICTCN_VISITED);
                }
            }
            //最后，将该单词的释义记录到本地缓存起来
            DictMap.put(word, htmlString);
            if (StringUtils.isNotEmpty(audioUrl))
            {
                DictMap.put(String.format("%s[audio]", word), audioUrl);
            }
            final String finalAudioUrl = audioUrl;
            //起一个线程，优先先将音频文件下载好。等好了之后，再显示发音按钮！
            if (StringUtils.isNotEmpty(finalAudioUrl))
            {
                ExecMgr.cachedExec.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //开始检查下载文件
                        downloadAudioCheck(finalAudioUrl, word);
                    }
                });
            }
        }
        else
        {
            //            System.out.println(String.format("单词 %s已存在，忽略查询...", word));
        }
        //        System.out.println(String.format("单词 %s下载完毕！", word));
    }
    
    /**
     * 是否全部是英文单词
     * @author nan.li
     * @param src
     * @return
     */
    public static boolean isAllEnglish(String src)
    {
        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(src);
        return m.matches();
    }
    
    public static void getDetailedTrans(final String src, final String googleResult)
    {
        ExecMgr.guiExec.execute(new Runnable()
        {
            @Override
            public void run()
            {
                WinMgr.targetPannel.getTextPane().setText(
                    "<div></div><div></div><div></div><div align='center'><h1><b><font color=red>L</font><font color=green>O</font><font color=blue>A</font><font color=purple>D</font>ING ...</b></h1></div>");
                WinMgr.targetPannel.getTextPane().setCaretPosition(0);
            }
        });
        ExecMgr.cachedExec.execute(new Runnable()
        {
            public void run()
            {
                String htmlString = DictMap.get(src);
                String audioUrl = DictMap.get(String.format("%s[audio]", src));
                //充分利用了强大的缓存机制，并不失时效性
                //1.若本地缓存中没有，则重新创建一个
                //2.若从来都没查过图片(不存在IMGVISITED标记的则表示从未查过图片)，则尝试重新查询一次。如果查询过，那么无论成功或者失败了，都要设置一个flag:IMGVISITED，标记图片已经查过了
                //3.如果已经存在的解释中含有$$google$$字样，则说明曾经因为网络问题而请求失败了，那么此处需要重新请求一次
                //4.检查DICTCN_VISITED（是否查询成功过海词）标记，若不存在，则依然需要重新请求一次
                
                if (htmlString == null || StringUtils.indexOf(htmlString, Constant.IMGVISITED) == -1 || StringUtils.indexOf(htmlString, "$$google$$") != -1
                    || StringUtils.indexOf(htmlString, Constant.DICTCN_VISITED) == -1)
                {
                    System.out.println("联网查询: " + src);
                    htmlString = StringUtils.replace(Constant.HTML_TEMPLATE, "$$google$$", googleResult);//存储google翻译的结果
                    //dictCn翻译的结果
                    DictCnTrans dictCnTrans = TranslateUtil.translateDictCn(src);
                    StringBuilder dictCn = new StringBuilder();
                    if (dictCnTrans != null && !dictCnTrans.isNetFail())
                    {
                        audioUrl = dictCnTrans.getAudioUrl();
                        String NEW_LINE = "\n";
                        dictCn.append("<div class=\"t\">---------------------------</div>").append(NEW_LINE);
                        if (dictCnTrans.getYinbiao() != null)
                        {
                            dictCn.append(String.format("%s&nbsp;&nbsp;&nbsp;<span class=\"p\">%s</span><br>", src, dictCnTrans.getYinbiao())).append(NEW_LINE);
                        }
                        if (dictCnTrans.getFanyi() != null)
                        {
                            dictCn.append(String.format("<div id=\"e\">%s</div><br>", dictCnTrans.getFanyi())).append(NEW_LINE);
                        }
                        if (dictCnTrans.getLiju() != null)
                        {
                            dictCn.append(String.format("<div class=\"t\">------例句与用法------</div><div id=\"s\">%s</div><br>", dictCnTrans.getLiju()))
                                .append(NEW_LINE);
                        }
                        
                        if (dictCnTrans.getBianhua() != null)
                        {
                            dictCn.append(String.format("<div class=\"t\">------词形变化------</div><div id=\"t\">%s</div><br>", dictCnTrans.getBianhua()))
                                .append(NEW_LINE);
                        }
                    }
                    //图片获取
                    String imgUrl = getWordTranslateImageUrl(src);
                    if (StringUtils.isEmpty(imgUrl))
                    {
                        htmlString = StringUtils.replace(htmlString, "$$image$$", Constant.IMGVISITED);
                    }
                    else
                    {
                        if (Constant.MSG_NET_FAIL.equals(imgUrl))
                        {
                            //只是这一次网络请求图片失败了，那么下次要继续尝试哦
                            htmlString = StringUtils.replace(htmlString, "$$image$$", "");
                        }
                        else
                        {
                            //manual,Manual的imgUrl是一样的!
                            //manual.jpg和Manual.jpg在windows上存储的结果是一样的！也就是说，windows上只能存储一份！
                            //所以，统一存储为小写的，大写的也直接采用小写的url即可！
                            imgUrl = downloadToLocal(imgUrl, src);
                            htmlString = StringUtils.replace(htmlString,
                                "$$image$$",
                                Constant.IMGVISITED + ImageUtil.getAutoZoonImageHtmlByMaxDimen(imgUrl, Constant.IMAGE_MAX_DIMEN));
                            //                        htmlString = StringUtils.replace(htmlString, "$$image$$", ImageUtil.getAutoZoonImageHtmlByHeight(imgUrl, IMAGE_HEIGHT));
                            //                        htmlString = StringUtils.replace(htmlString, "$$image$$", ImageUtil.getAutoZoonImageHtmlByWidth(imgUrl, IMAGE_WIDTH));
                        }
                    }
                    
                    String dictCnStr = dictCn.toString();
                    //可能有值，也可能没值
                    //1.有值,必定请求成功了（DICTCN_VISITED）
                    //2.没值，网络请求失败，或者本来就不存在这个单词（DICTCN_VISITED）
                    if (StringUtils.isNotEmpty(dictCnStr))
                    {
                        htmlString = StringUtils.replace(htmlString, "$$dictCn$$", Constant.DICTCN_VISITED + dictCn.toString());
                    }
                    else
                    {
                        if (dictCnTrans != null && dictCnTrans.isNetFail())
                        {
                            //网络请求失败了
                            htmlString = StringUtils.replace(htmlString, "$$dictCn$$", "");
                        }
                        else
                        {
                            //返回的结果为null，也就是本来就不存在这个单词！
                            htmlString = StringUtils.replace(htmlString, "$$dictCn$$", Constant.DICTCN_VISITED);
                        }
                    }
                    //最后，将该单词的释义记录到本地缓存起来
                    DictMap.put(src, htmlString);
                    if (StringUtils.isNotEmpty(audioUrl))
                    {
                        DictMap.put(String.format("%s[audio]", src), audioUrl);
                    }
                }
                //                System.out.println(htmlString);
                //                System.out.println();
                //                System.out.println();
                final String finalHtmlString = htmlString;
                final String finalAudioUrl = audioUrl;
                //我擦嘞 原来guiExec并没有问题！如果慢，那么必定是其他的guiExec拖慢了速度！
                ExecMgr.guiExec.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        WinMgr.targetPannel.getTextPane().setText(finalHtmlString);
                        WinMgr.targetPannel.getTextPane().setCaretPosition(0);
                    }
                });
                //起一个线程，优先先将音频文件下载好。等好了之后，再显示发音按钮！
                if (StringUtils.isEmpty(finalAudioUrl))
                { //没有音频文件
                    WinMgr.handlePanel.audioOff();
                }
                else
                {
                    ExecMgr.cachedExec.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //有音频文件
                            //开始检查下载文件
                            downloadAudioCheck(finalAudioUrl, src);
                            //下载完成后，显示发音按钮,允许其发音
                            WinMgr.handlePanel.audioOn(src);
                        }
                    });
                }
                WinMgr.imageDialog.refreshImage();
            }
            
        });
    }
    
    private static String downloadToLocal(String imgUrl, String word)
    {
        word = word.toLowerCase();//图片统一转为小写的文件名进行存储！！！
        try
        {
            File destFile = new File(Constant.USER_DIR + Constant.IMG_SERVER_CONTEXT_PATH, word + Constant.WORD_IMAGE_SUFFIX);
            if (!destFile.exists())
            {
                Logs.i("开始下载图片: " + word + " ...");
                //只有当目标图片不存在的时候才重新下载
                DownloadKit.downloadFile(imgUrl, destFile);
            }
            return String.format("http://127.0.0.1:%d%s/%s", Constant.IMG_SERVER_PORT, Constant.IMG_SERVER_CONTEXT_PATH, word + Constant.WORD_IMAGE_SUFFIX);
        }
        catch (Exception e)
        {
            logger.error(String.format("下载【%s】图片出错！", word), e);
        }
        return null;
    }
    
    /**
     * 获取单词的插图信息<br>
     * 首先尝试从移动版获取。如果移动版没有，则从json版获取。如果json版获取到了，则取第一张作为正式的显示的图片
     * @author Administrator
     * @param src
     * @return
     */
    protected static String getWordTranslateImageUrl(String word)
    {
        if (Constant.DO_NOT_QUERY_IMG_WORDS.contains(word))
        {
            //定制化处理
            //有道词典无法查询“about”，因此也无法抓取该图片
            return "";
        }
        
        //从有道源获取
        String ret = null;
        String url = ("http://dict.youdao.com/search?le=eng&keyfrom=dict.top&q=" + word);
        org.jsoup.nodes.Document doc;
        Elements imgElement;
        try
        {
            doc = Jsoup.connect(url)
                .header("User-Agent",
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4")
                .get();//优先抓取移动版上面的图片
            imgElement = doc.select("#phrsListTab div.img-list a img");
            if (imgElement.isEmpty())
            {
                //尝试开始获取json版
                //http://dict.youdao.com/ugc/wordjson/cat
                ret = getImageFromJson(word);
            }
            else
            {
                ret = imgElement.first().attr("src");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = Constant.MSG_NET_FAIL;
        }
        return ret;
    }
    
    public static void main(String[] args)
    {
        //        getImageFromJson("about");
        System.out.println(getImageFromJson("manual"));
        System.out.println(getImageFromJson("Manual"));
    }
    
    /**
     * 尝试获取json版本的图片<br>获取不到就返回null
     * @author Administrator
     * @param word
     * @return
     */
    private static String getImageFromJson(String word)
    {
        String result = null;
        HttpClient httpClient = HttpClientKit.getDefaultHttpClient();
        try
        {
            String url = "http://dict.youdao.com/ugc/wordjson/" + word;
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            String jsonArrayStr = EntityUtils.toString(response.getEntity());
            if (StringUtils.isNotEmpty(jsonArrayStr))
            {
                if (!jsonArrayStr.startsWith("<html"))
                {
                    //如果不是404 not found的话
                    //那么就尝试解析释义
                    JSONArray jsonArray = new JSONArray(jsonArrayStr);
                    if (jsonArray.length() > 0)
                    {
                        //取出第一个元素的url属性，即可
                        //突然发现，原来写代码是这么爽的一件事！真的是比玩暗黑2还要爽啊！
                        result = jsonArray.getJSONObject(0).getString("Url");
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            result = Constant.MSG_NET_FAIL;
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
    
    /**
     * 下载音频文件(如果该文件不存在的话)
     * @author nan.li
     * @param finalAudioUrl
     * @param src
     */
    protected static void downloadAudioCheck(String audioId, String word)
    {
        File dir = new File(Constant.AUDIO_DIR);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        String targetFileName = null;
        //特殊单词特殊处理
        if (Constant.SPECIAL_WORDS.contains(word))
        {
            targetFileName = String.format("SP_%s.mp3", word);
        }
        else
        {
            targetFileName = String.format("%s.mp3", word);
        }
        File targetFile = new File(dir, targetFileName);
        if (targetFile.exists())
        {
            //该音频文件已经存在，无须再次下载！
            return;
        }
        String fromUrl = String.format("http://dict.cn/mp3.php?q=%s", audioId);
        try
        {
            IOUtils.copy(new URL(fromUrl).openStream(), new FileOutputStream(targetFile));
            System.out.println("Spell File download OK!");
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
