package com.huawei.echannel.customactivity.weichat.sublightInternet.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	public static void main(String[] args) throws BadHanyuPinyinOutputFormatCombination {
		char chiness = '你';
		HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
		hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] temp = PinyinHelper.toHanyuPinyinStringArray(chiness, hanyuPinyinOutputFormat);
		print(temp);
		temp = PinyinHelper.toTongyongPinyinStringArray(chiness);
		print(temp);
		temp=PinyinHelper.toMPS2PinyinStringArray(chiness);
		print(temp);
		temp = PinyinHelper.toWadeGilesPinyinStringArray(chiness);
		print(temp);
		
		System.out.println("-------------------");
		
		String s = PinYinUtil.coverHanyu2pinying("世界你好");
		System.out.println(s);
		boolean a = isChinese('世');
		System.out.println(a);
		String s2 = PinYinUtil.coverHanyu2firPinying("实际9区");
		System.out.println(s2);
	}
	public static void print(String[] temp){
		String result = "";
		for (String s : temp) {
			result+=s;
		}
		System.out.println(result);
	}
	/**
	 * 将汉字转拼音 去除掉多音字内容，如果遇到非中文字符，直接返回
	 * */
	public static String coverHanyu2pinying(String hanyu) throws BadHanyuPinyinOutputFormatCombination{
		char[] hanyus = hanyu.toCharArray();
		String result = "";
		HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
		hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (char c : hanyus) {
			if(isChinese(c)){
				String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
				result+=temp[0];
				temp = null;
			}else{
				result+=c;
			}
		}
		return result;
	}
	/**
	 * 将汉字转拼音首字母 去除掉多音字内容，如果遇到非中文字符，直接返回
	 * */
	public static String coverHanyu2firPinying(String hanyu) throws BadHanyuPinyinOutputFormatCombination{
		char[] hanyus = hanyu.toCharArray();
		String result = "";
		HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();
		hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (char c : hanyus) {
			if(isChinese(c)){
				String[] temp = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
				result+=temp[0].charAt(0);
				temp = null;
			}else{
				result+=c;
			}
		}
		return result;
	}
	/**
	 * 是否为汉字
	 * */
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
}
