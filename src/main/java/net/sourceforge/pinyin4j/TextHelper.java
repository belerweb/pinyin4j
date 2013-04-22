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

/**
 * Contains the utility functions supporting text processing
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class TextHelper {

  /**
   * @param hanyuPinyinWithToneNumber
   * @return Hanyu Pinyin string without tone number
   */
  static String extractToneNumber(String hanyuPinyinWithToneNumber) {
    return hanyuPinyinWithToneNumber.substring(hanyuPinyinWithToneNumber.length() - 1);
  }

  /**
   * @param hanyuPinyinWithToneNumber
   * @return only tone number
   */
  static String extractPinyinString(String hanyuPinyinWithToneNumber) {
    return hanyuPinyinWithToneNumber.substring(0, hanyuPinyinWithToneNumber.length() - 1);
  }

}
