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

package net.sourceforge.pinyin4j;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Contains logic to format given Pinyin string
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class PinyinFormatter {
  /**
   * @param pinyinStr
   *            unformatted Hanyu Pinyin string
   * @param outputFormat
   *            given format of Hanyu Pinyin
   * @return formatted Hanyu Pinyin string
   * @throws BadHanyuPinyinOutputFormatCombination
   */
  static String formatHanyuPinyin(String pinyinStr, HanyuPinyinOutputFormat outputFormat)
      throws BadHanyuPinyinOutputFormatCombination {
    if ((HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType())
        && ((HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType()) || (HanyuPinyinVCharType.WITH_U_AND_COLON == outputFormat
            .getVCharType()))) {
      throw new BadHanyuPinyinOutputFormatCombination("tone marks cannot be added to v or u:");
    }

    if (HanyuPinyinToneType.WITHOUT_TONE == outputFormat.getToneType()) {
      pinyinStr = pinyinStr.replaceAll("[1-5]", "");
    } else if (HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType()) {
      pinyinStr = pinyinStr.replaceAll("u:", "v");
      pinyinStr = convertToneNumber2ToneMark(pinyinStr);
    }

    if (HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType()) {
      pinyinStr = pinyinStr.replaceAll("u:", "v");
    } else if (HanyuPinyinVCharType.WITH_U_UNICODE == outputFormat.getVCharType()) {
      pinyinStr = pinyinStr.replaceAll("u:", "ü");
    }

    if (HanyuPinyinCaseType.UPPERCASE == outputFormat.getCaseType()) {
      pinyinStr = pinyinStr.toUpperCase();
    }
    return pinyinStr;
  }

  /**
   * Convert tone numbers to tone marks using Unicode <br/><br/>
   * 
   * <b>Algorithm for determining location of tone mark</b><br/>
   * 
   * A simple algorithm for determining the vowel on which the tone mark
   * appears is as follows:<br/>
   * 
   * <ol>
   * <li>First, look for an "a" or an "e". If either vowel appears, it takes
   * the tone mark. There are no possible pinyin syllables that contain both
   * an "a" and an "e".
   * 
   * <li>If there is no "a" or "e", look for an "ou". If "ou" appears, then
   * the "o" takes the tone mark.
   * 
   * <li>If none of the above cases hold, then the last vowel in the syllable
   * takes the tone mark.
   * 
   * </ol>
   * 
   * @param pinyinStr
   *            the ascii represention with tone numbers
   * @return the unicode represention with tone marks
   */
  private static String convertToneNumber2ToneMark(final String pinyinStr) {
    String lowerCasePinyinStr = pinyinStr.toLowerCase();

    if (lowerCasePinyinStr.matches("[a-z]*[1-5]?")) {
      final char defautlCharValue = '$';
      final int defautlIndexValue = -1;

      char unmarkedVowel = defautlCharValue;
      int indexOfUnmarkedVowel = defautlIndexValue;

      final char charA = 'a';
      final char charE = 'e';
      final String ouStr = "ou";
      final String allUnmarkedVowelStr = "aeiouv";
      final String allMarkedVowelStr = "āáăàaēéĕèeīíĭìiōóŏòoūúŭùuǖǘǚǜü";

      if (lowerCasePinyinStr.matches("[a-z]*[1-5]")) {

        int tuneNumber =
            Character.getNumericValue(lowerCasePinyinStr.charAt(lowerCasePinyinStr.length() - 1));

        int indexOfA = lowerCasePinyinStr.indexOf(charA);
        int indexOfE = lowerCasePinyinStr.indexOf(charE);
        int ouIndex = lowerCasePinyinStr.indexOf(ouStr);

        if (-1 != indexOfA) {
          indexOfUnmarkedVowel = indexOfA;
          unmarkedVowel = charA;
        } else if (-1 != indexOfE) {
          indexOfUnmarkedVowel = indexOfE;
          unmarkedVowel = charE;
        } else if (-1 != ouIndex) {
          indexOfUnmarkedVowel = ouIndex;
          unmarkedVowel = ouStr.charAt(0);
        } else {
          for (int i = lowerCasePinyinStr.length() - 1; i >= 0; i--) {
            if (String.valueOf(lowerCasePinyinStr.charAt(i)).matches(
                "[" + allUnmarkedVowelStr + "]")) {
              indexOfUnmarkedVowel = i;
              unmarkedVowel = lowerCasePinyinStr.charAt(i);
              break;
            }
          }
        }

        if ((defautlCharValue != unmarkedVowel) && (defautlIndexValue != indexOfUnmarkedVowel)) {
          int rowIndex = allUnmarkedVowelStr.indexOf(unmarkedVowel);
          int columnIndex = tuneNumber - 1;

          int vowelLocation = rowIndex * 5 + columnIndex;

          char markedVowel = allMarkedVowelStr.charAt(vowelLocation);

          StringBuffer resultBuffer = new StringBuffer();

          resultBuffer.append(lowerCasePinyinStr.substring(0, indexOfUnmarkedVowel).replaceAll("v",
              "ü"));
          resultBuffer.append(markedVowel);
          resultBuffer.append(lowerCasePinyinStr.substring(indexOfUnmarkedVowel + 1,
              lowerCasePinyinStr.length() - 1).replaceAll("v", "ü"));

          return resultBuffer.toString();

        } else
        // error happens in the procedure of locating vowel
        {
          return lowerCasePinyinStr;
        }
      } else
      // input string has no any tune number
      {
        // only replace v with ü (umlat) character
        return lowerCasePinyinStr.replaceAll("v", "ü");
      }
    } else
    // bad format
    {
      return lowerCasePinyinStr;
    }
  }
}
