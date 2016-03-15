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
    static HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();

    static {
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    public void testMulti() throws Exception {
        MultiPinyinConfig.multiPinyinPath = "/Users/yiboliu/my_multi_pinyin.txt";
        System.out.println(PinyinHelper.toHanYuPinyinString("呵呵...", outputFormat, ";", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("吸血鬼...", outputFormat, ";", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("吸血鬼日记...", outputFormat, ";", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("我还要去图书馆还书...", outputFormat, "", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("一五一十", outputFormat, "", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("女医明妃传", outputFormat, ";", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("一人做事一人当还", outputFormat, ".", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("梦之安魂曲", outputFormat, ",", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("长春..", outputFormat, ",", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("长春不老/", outputFormat, ",", true));
        System.out.println(PinyinHelper.toHanYuPinyinString("刘一波", outputFormat, ",", false));
    }

    public void testMore() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/yiboliu/pinyin.txt"));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/yiboliu/pinyin_pinyin4j.txt"));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(PinyinHelper.toHanYuPinyinString(line, outputFormat, "", true) + "\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
