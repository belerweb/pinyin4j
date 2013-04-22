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

/**
 * The class describes variable Chinese Pinyin Romanization System
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class PinyinRomanizationType {
  /**
   * Hanyu Pinyin system
   */
  static final PinyinRomanizationType HANYU_PINYIN = new PinyinRomanizationType("Hanyu");

  /**
   * Wade-Giles Pinyin system
   */
  static final PinyinRomanizationType WADEGILES_PINYIN = new PinyinRomanizationType("Wade");

  /**
   * Mandarin Phonetic Symbols 2 (MPS2) Pinyin system
   */
  static final PinyinRomanizationType MPS2_PINYIN = new PinyinRomanizationType("MPSII");

  /**
   * Yale Pinyin system
   */
  static final PinyinRomanizationType YALE_PINYIN = new PinyinRomanizationType("Yale");

  /**
   * Tongyong Pinyin system
   */
  static final PinyinRomanizationType TONGYONG_PINYIN = new PinyinRomanizationType("Tongyong");

  /**
   * Gwoyeu Romatzyh system
   */
  static final PinyinRomanizationType GWOYEU_ROMATZYH = new PinyinRomanizationType("Gwoyeu");

  /**
   * Constructor
   */
  protected PinyinRomanizationType(String tagName) {
    setTagName(tagName);
  }

  /**
   * @return Returns the tagName.
   */
  String getTagName() {
    return tagName;
  }

  /**
   * @param tagName
   *            The tagName to set.
   */
  protected void setTagName(String tagName) {
    this.tagName = tagName;
  }

  protected String tagName;
}
