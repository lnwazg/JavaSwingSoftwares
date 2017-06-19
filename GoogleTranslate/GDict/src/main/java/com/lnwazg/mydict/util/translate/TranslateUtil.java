package com.lnwazg.mydict.util.translate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.lnwazg.kit.http.apache.httpclient.HttpClientKit;
import com.lnwazg.mydict.util.Constant;
import com.lnwazg.mydict.util.translate.dictcn.DictCnTrans;

/**
 * 翻译工具
 * @author nan.li
 * @version 2014-11-6
 */
public class TranslateUtil
{
    private static Log logger = LogFactory.getLog(TranslateUtil.class);
    
    private static final String GOOGLE_TRANS_PREFIX = "http://translate.google.cn/translate_a/single?client=t";
    
    private static final String UTF8_ENCODING = "UTF-8";
    
    /**
     * 翻译的主要调用的方法
     * @author nan.li
     * @param text
     * @param target_lang 目标语言
     * @return
     */
    public static String translate(String text, String target_lang)
    {
        return translateBaidu(text, Language.AUTO.getValue(), target_lang);
    }
    
    /**
     * 百度翻译
     * @author nan.li
     * @param text
     * @param src_lang
     * @param target_lang
     * @return
     */
    public static String translateBaidu(String text, String src_lang, String target_lang)
    {
        String result = null;
        HttpClient httpClient = HttpClientKit.getDefaultHttpClient();
        try
        {
            HttpPost httppost = new HttpPost("http://fanyi.baidu.com/v2transapi");
            httppost.addHeader("User-Agent", "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
            
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (target_lang.equals(Language.ENGLISH.getValue()))
            {
                formparams.add(new BasicNameValuePair("from", "zh"));
                formparams.add(new BasicNameValuePair("to", "en"));
            }
            else
            {
                formparams.add(new BasicNameValuePair("from", "en"));
                formparams.add(new BasicNameValuePair("to", "zh"));
            }
            formparams.add(new BasicNameValuePair("query", text));
            formparams.add(new BasicNameValuePair("transtype", "realtime"));
            formparams.add(new BasicNameValuePair("simple_means_flag", "3"));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            //            httppost.setHeader("Cookie", cookie);
            HttpResponse response = httpClient.execute(httppost);
            String resultJsonStr = EntityUtils.toString(response.getEntity());
            if (StringUtils.isEmpty(resultJsonStr))
            {
                return null;
            }
            JSONObject jsonObject = new JSONObject(resultJsonStr);
            result = jsonObject.getJSONObject("trans_result").getJSONArray("data").getJSONObject(0).getString("dst");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
    
    /**
     * 谷歌翻译
     * @author nan.li
     * @param text
     * @param src_lang
     * @param target_lang
     * @return
     */
    public String translateGoogle(String text, String src_lang, String target_lang)
    {
        String result = null;
        StringBuilder sb = new StringBuilder();
        HttpClient httpClient = HttpClientKit.getDefaultHttpClient();
        try
        {
            String url = GOOGLE_TRANS_PREFIX + "&sl=" + src_lang + "&tl=" + target_lang + "&hl=" + src_lang
                + "&dt=bd&dt=ex&dt=ld&dt=md&dt=qc&dt=rw&dt=rm&dt=ss&dt=t&dt=at&dt=sw&ie=" + UTF8_ENCODING + "&oe=" + UTF8_ENCODING
                + "&prev=btn&ssel=0&tsel=0&q=" + URLEncoder.encode(text, UTF8_ENCODING);
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            String allInfo = EntityUtils.toString(response.getEntity());
            String[] resultArray = allInfo.split("]]")[0].split("]");
            for (int i = 0; i < resultArray.length - 1; i++)
            {
                sb.append(resultArray[i].split("\"")[1]);
            }
            result = sb.toString();
            result = result.replace("\\n", "\r\n");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
    
    /**
     * 获取dict.cn上面的详尽解释
     * @author nan.li
     * @param text
     * @return
     */
    public static DictCnTrans translateDictCn(String text)
    {
        //1.get方法获取页面信息。从中获取音标信息以及当前页面的key、session信息。（注意：翻译的内容不一定存在）
        //2.如果翻译的内容存在的话，则拼接参数来获取json信息，然后解析该json信息的内容
        HttpClient httpClient = HttpClientKit.getDefaultHttpClient();
        //处理请求，得到响应  
        try
        {
            HttpGet httpGet = new HttpGet(String.format("http://apii.dict.cn/mini.php?q=%s", text));
            HttpResponse response = httpClient.execute(httpGet);
            String pageReturn = EntityUtils.toString(response.getEntity(), "gb2312");
            if (StringUtils.isEmpty(pageReturn))
            {
                return null;
            }
            String pageToken = StringUtils.substringBetween(pageReturn, "dict_pagetoken=\"", "\"");//key
            String yinbiao = StringUtils.substringBetween(pageReturn, "<span class='p'> ", "</span>");//音标信息
            String audioUrl = StringUtils.substringBetween(pageReturn, "ssplay('", "')");
            //            System.out.println(pageToken);
            //            System.out.println(yinbiao);
            Header[] cookieHeaders = response.getHeaders("Set-Cookie");
            StringBuilder cookieResult = new StringBuilder();
            if (cookieHeaders != null && cookieHeaders.length > 0)
            {
                for (Header header : cookieHeaders)
                {
                    String headerValue = header.getValue();
                    cookieResult.append(headerValue.substring(0, headerValue.indexOf(";"))).append(";");
                }
            }
            if (StringUtils.isEmpty(cookieResult.toString()) || StringUtils.isEmpty(pageToken))
            {
                return null;
            }
            String json = getExplainJson(text, cookieResult.toString(), pageToken);
            //            System.out.println("the json is: " + json);
            if (StringUtils.isEmpty(json))
            {
                return null;
            }
            
            JSONObject jsonObject = new JSONObject(json);
            String fanyi = jsonObject.optString("e");//翻译
            if (StringUtils.isEmpty(fanyi))
            {
                //连翻译都没有，则的确什么也没有！
                return null;
            }
            String liju = jsonObject.optString("s");//例句
            String bianhua = jsonObject.optString("t");//变化
            //            String askingSimilar = jsonObject.optString("g");//你可能在寻找  "atop"...
            
            DictCnTrans dictCnTrans = new DictCnTrans();
            dictCnTrans.setSrc(text);
            dictCnTrans.setYinbiao(yinbiao);
            dictCnTrans.setAudioUrl(audioUrl);
            //对翻译中的尖括号作特殊处理
            fanyi = handleSpecialJiankuohao(fanyi);
            dictCnTrans.setFanyi(fanyi);
            if (StringUtils.isNotEmpty(liju))
            {
                liju = removeAllBtwn(liju, "<a", "</a>");
                dictCnTrans.setLiju(liju);
            }
            if (StringUtils.isNotEmpty(bianhua))
            {
                dictCnTrans.setBianhua(bianhua);
            }
            dictCnTrans.setNetFail(false);
            return dictCnTrans;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            DictCnTrans dictCnTrans = new DictCnTrans();
            dictCnTrans.setNetFail(true);//标记了网络请求失败了这一情况
            return dictCnTrans;
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
    }
    
    /**
     * 处理特殊的尖括号(特殊尖括号替换成转义字符)
     * @author nan.li
     * @param str
     * @return
     */
    private static String handleSpecialJiankuohao(String str)
    {
        //搜索尖括号，判定尖括号后面的字符，如果是<br      <font     </font    这样的，就不管；其余的情况，需要将其替换成&lt; &gt;
        
        if (StringUtils.indexOf(str, "<") == -1)
        {
            //不含有任何尖括号
            return str;
        }
        else
        {
            int fromIndex = 0;//从首字符开始搜索
            while (StringUtils.indexOf(str, "<", fromIndex) != -1)
            {
                int index = StringUtils.indexOf(str, "<", fromIndex);
                String houMian = str.substring(index);
                if (houMian.startsWith("<br") || houMian.startsWith("<font") || houMian.startsWith("</font"))
                {
                    //原封不动，直接搜索下一个左尖括号即可
                    fromIndex = index + 1;
                    continue;
                }
                else
                {
                    //需要进行尖括号转换
                    str = replaceFirstFromIndex(str, "<", "&lt;", fromIndex);
                    str = replaceFirstFromIndex(str, ">", "&gt;", fromIndex);
                }
            }
            return str;
        }
    }
    
    /**
     * 从某个字符开始替换首次出现的第一个字符
     * @author nan.li
     * @param str
     * @param string
     * @param string2
     * @param fromIndex
     * @return
     */
    private static String replaceFirstFromIndex(String str, String oldW, String newW, int fromIndex)
    {
        String left = str.substring(0, fromIndex);
        String right = str.substring(fromIndex);//老的右边
        right = right.replaceFirst(oldW, newW);//替换成新的右边
        return new StringBuilder(left).append(right).toString();
    }
    
    /**
     * 清空指定标记
     * @author nan.li
     * @param liju
     * @param string
     * @param string2
     * @return
     */
    private static String removeAllBtwn(String src, String fromTag, String toTag)
    {
        StringBuilder resBuilder = new StringBuilder();
        while (StringUtils.indexOf(src, fromTag) != -1 && StringUtils.indexOf(src, toTag) != -1)
        {
            resBuilder.append(StringUtils.substring(src, 0, StringUtils.indexOf(src, fromTag)));
            src = StringUtils.substring(src, (StringUtils.indexOf(src, toTag) + toTag.length()));
        }
        resBuilder.append(src);
        return resBuilder.toString();
    }
    
    public static String getExplainJson(String src, String cookie, String pageToken)
    {
        HttpClient httpClient = HttpClientKit.getDefaultHttpClient();
        String result = null;
        try
        {
            HttpPost httppost = new HttpPost("http://apii.dict.cn/ajax/dictcontent.php");
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("q", src));
            formparams.add(new BasicNameValuePair("s", "2"));
            formparams.add(new BasicNameValuePair("t", calcQueryKey(src, pageToken)));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            httppost.setHeader("Cookie", cookie);
            //            System.out.println("executing request " + httppost.getURI());
            HttpResponse response = httpClient.execute(httppost);
            result = EntityUtils.toString(response.getEntity(), UTF8_ENCODING);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }
    
    /**
     * 算出查询的秘钥
     * @author nan.li
     * @param src
     * @param pageToken
     * @return
     */
    private static String calcQueryKey(String src, String pageToken)
    {
        // 创建脚本引擎管理器
        ScriptEngineManager factory = new ScriptEngineManager();
        // 创建JavaScript引擎
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // 从字符串中赋值JavaScript脚本
        try
        {
            String js = StringUtils.replace(Constant.JS_TEMPLATE, "$$pagetoken$$", pageToken);
            js = StringUtils.replace(js, "$$src$$", src);
            return engine.eval(js).toString();
        }
        catch (ScriptException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    public static void main(String[] args)
    {
        System.out.println(TranslateUtil.translate("good", Language.CHINA.getValue()));
        System.out.println(TranslateUtil.translate("hadoop", Language.CHINA.getValue()));
        System.out.println(TranslateUtil.translate("good people think further", Language.CHINA.getValue()));
        System.out.println(TranslateUtil.translate("大力士", Language.ENGLISH.getValue()));
        System.out.println(TranslateUtil.translate("真不错哦", Language.ENGLISH.getValue()));
        System.out.println(TranslateUtil.translate("今天心情好爽！", Language.ENGLISH.getValue()));
        System.out.println(TranslateUtil.translate("中文", Language.ENGLISH.getValue()));
        System.out.println(TranslateUtil.translateDictCn("cache"));
        System.out.println(TranslateUtil.translateDictCn("hadoop"));
    }
}