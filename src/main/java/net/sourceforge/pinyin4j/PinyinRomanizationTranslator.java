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
 * Contains the logic translating among different Chinese Romanization systems
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class PinyinRomanizationTranslator {
  /**
   * convert the given unformatted Pinyin string from source Romanization
   * system to target Romanization system
   * 
   * @param sourcePinyinStr
   *            the given unformatted Pinyin string
   * @param sourcePinyinSystem
   *            the Romanization system which is currently used by the given
   *            unformatted Pinyin string
   * @param targetPinyinSystem
   *            the Romanization system that should be converted to
   * @return unformatted Pinyin string in target Romanization system; null if
   *         error happens
   */
  static String convertRomanizationSystem(String sourcePinyinStr,
      PinyinRomanizationType sourcePinyinSystem, PinyinRomanizationType targetPinyinSystem) {
    String pinyinString = TextHelper.extractPinyinString(sourcePinyinStr);
    String toneNumberStr = TextHelper.extractToneNumber(sourcePinyinStr);

    // return value
    String targetPinyinStr = null;
    try {
      // find the node of source Pinyin system
      String xpathQuery1 =
          "//" + sourcePinyinSystem.getTagName() + "[text()='" + pinyinString + "']";

      Document pinyinMappingDoc = PinyinRomanizationResource.getInstance().getPinyinMappingDoc();

      Element hanyuNode = pinyinMappingDoc.xpathSelectElement(xpathQuery1);

      if (null != hanyuNode) {
        // find the node of target Pinyin system
        String xpathQuery2 = "../" + targetPinyinSystem.getTagName() + "/text()";
        String targetPinyinStrWithoutToneNumber = hanyuNode.xpathSelectString(xpathQuery2);

        targetPinyinStr = targetPinyinStrWithoutToneNumber + toneNumberStr;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return targetPinyinStr;
  }
}
