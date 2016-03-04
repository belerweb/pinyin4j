package net.sourceforge.pinyin4j.test;

import junit.framework.TestCase;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.multipinyin.MultiPinyinConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by 刘一波 on 16/3/4.
 * E-Mail:yibo.liu@tqmall.com
 */
public class Q extends TestCase {
    public void testMulti() throws Exception {
        MultiPinyinConfig.multiPinyinPath="/Users/yiboliu/my_multi_pinyin.txt";
//        System.out.println(PinyinHelper.toHanYuPinyinString("呵呵...",new HanyuPinyinOutputFormat(),""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("吸血鬼...", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("吸血鬼日记...", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("我还要去图书馆还书...", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("一五一十", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("女医明妃传", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("一人做事一人当", new HanyuPinyinOutputFormat(), ""));
//        System.out.println(PinyinHelper.toHanYuPinyinString("梦之安魂曲", new HanyuPinyinOutputFormat(), ""));
        System.out.println(PinyinHelper.toHanYuPinyinString("长春", new HanyuPinyinOutputFormat(), ""));
        System.out.println(PinyinHelper.toHanYuPinyinString("长春不老", new HanyuPinyinOutputFormat(), ""));
        System.out.println(PinyinHelper.toHanYuPinyinString("刘一波", new HanyuPinyinOutputFormat(), ""));
    }

    public void testMore() throws Exception {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/yiboliu/pinyin.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/yiboliu/pinyin_pinyin4j.txt"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(PinyinHelper.toHanYuPinyinString(line, outputFormat, "") + "\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
