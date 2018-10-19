/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * 
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

/**
 * 
 */
package net.sourceforge.pinyin4j;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.ParseException;

/**
 * A class contains logic that translates from Hanyu Pinyin to Phonetic
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class PhoneticTranslator {
  /**
   * @param hanyuPinyinStr
   *            Given unformatted Hanyu Pinyin with tone number
   * @return Corresponding Phonetic; null if no mapping is found.
   */
  static String convertHanyuPinyinToPhonetic(String hanyuPinyinStr) {
    String pinyinString = TextHelper.extractPinyinString(hanyuPinyinStr);
    String toneNumberStr = TextHelper.extractToneNumber(hanyuPinyinStr);

    // return value
    String phoneticStr = null;
    try {
      // find the node of source Pinyin system
      String xpathQuery1 =
          "//" + PinyinRomanizationType.HANYU_PINYIN.getTagName() + "[text()='" + pinyinString
              + "']";

      Document pinyinToPhoneticMappingDoc = PhoneticResource.getInstance().getPhoneticMappingDoc();

      Element hanyuNode = pinyinToPhoneticMappingDoc.xpathSelectElement(xpathQuery1);

      if (null != hanyuNode) {
        String xpathQuery2 = "../" + PinyinRomanizationType.PHONETIC.getTagName() + "/text()";

        phoneticStr =
            hanyuNode.xpathSelectString(xpathQuery2) + tones[Integer.parseInt(toneNumberStr) - 1];
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return phoneticStr;
  }

  /**
   * The postfixs to distinguish different tone of Phonetic
   * 
   * <i>Should be removed if new xPath parser supporting tag name with number.</i>
   */
  static private String[] tones = new String[] {"", "ˊ", "ˇ", "ˋ", "˙"};
}
