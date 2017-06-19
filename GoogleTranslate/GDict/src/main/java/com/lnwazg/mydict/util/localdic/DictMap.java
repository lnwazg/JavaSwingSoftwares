package com.lnwazg.mydict.util.localdic;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.dbkit.jdbc.MyJdbc;
import com.lnwazg.dbkit.utils.DbKit;
import com.lnwazg.mydict.entity.Word;
import com.lnwazg.mydict.util.Constant;

/**
 * 字典表<br>
 * 完全有必要通过db的方式进行操作了！因为用hashmap的方式，后期会占用巨量的内存，这是完全无法接受的事情！
 * 
 * @author nan.li
 * @version 2016年4月13日
 */
public class DictMap
{
    /**
     * 写入锁
     */
    private static final byte[] lock = new byte[0];
    
    static MyJdbc jdbc = DbKit.getJdbc("jdbc:sqlite://" + Constant.USER_DIR + "dict.db", "", "");
    
    static
    {
        try
        {
            jdbc.createTable(Word.class);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static String get(String key)
    {
        try
        {
            return jdbc.findValue("select value from Word where name=?", key);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void put(String key, String value)
    {
        synchronized (lock)
        {
            try
            {
                Word word = new Word().setName(key).setValue(value);
                if (StringUtils.isNotEmpty(get(key)))
                {
                    jdbc.update("update Word set value=? where name=?", value, key);
                }
                else
                {
                    jdbc.insert(word);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void remove(String key)
    {
        synchronized (lock)
        {
            try
            {
                jdbc.execute("delete from Word where name=?", key);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
