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
package net.sourceforge.pinyin4j.format;

/**
 * Define the output format of Hanyu Pinyin tones
 * 
 * <p>
 * Chinese has four pitched tones and a "toneless" tone. They are called Píng(平,
 * flat), Shǎng(上, rise), Qù(去, high drop), Rù(入, drop) and Qing(轻, toneless).
 * Usually, we use 1, 2, 3, 4 and 5 to represent them.
 * 
 * <p>
 * This class provides several options for output of Chinese tones, which are
 * listed below. For example, Chinese character '打'
 * 
 * <table>
 * <tr>
 * <th>Options</th>
 * <th>Output</th>
 * </tr>
 * <tr>
 * <td>WITH_TONE_NUMBER</td>
 * <td align = "center">da3</td>
 * </tr>
 * <tr>
 * <td>WITHOUT_TONE</td>
 * <td align = "center">da</td>
 * </tr>
 * <tr>
 * <td>WITH_TONE_MARK</td>
 * <td align = "center">dǎ</td>
 * </tr>
 * </table>
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
public class HanyuPinyinToneType {

  /**
   * The option indicates that hanyu pinyin is outputted with tone numbers
   */
  public static final HanyuPinyinToneType WITH_TONE_NUMBER =
      new HanyuPinyinToneType("WITH_TONE_NUMBER");

  /**
   * The option indicates that hanyu pinyin is outputted without tone numbers
   * or tone marks
   */
  public static final HanyuPinyinToneType WITHOUT_TONE = new HanyuPinyinToneType("WITHOUT_TONE");

  /**
   * The option indicates that hanyu pinyin is outputted with tone marks
   */
  public static final HanyuPinyinToneType WITH_TONE_MARK =
      new HanyuPinyinToneType("WITH_TONE_MARK");

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *            The name to set.
   */
  protected void setName(String name) {
    this.name = name;
  }

  protected HanyuPinyinToneType(String name) {
    setName(name);
  }

  protected String name;
}
