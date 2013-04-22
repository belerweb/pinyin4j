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
 * Define the output format of character 'ü'
 * 
 * <p>
 * 'ü' is a special character of Hanyu Pinyin, which can not be simply
 * represented by English letters. In Hanyu Pinyin, such characters include 'ü',
 * 'üe', 'üan', and 'ün'.
 * 
 * <p>
 * This class provides several options for output of 'ü', which are listed
 * below.
 * 
 * <table>
 * <tr>
 * <th>Options</th>
 * <th>Output</th>
 * </tr>
 * <tr>
 * <td>WITH_U_AND_COLON</td>
 * <td align = "center">u:</td>
 * </tr>
 * <tr>
 * <td>WITH_V</td>
 * <td align = "center">v</td>
 * </tr>
 * <tr>
 * <td>WITH_U_UNICODE</td>
 * <td align = "center">ü</td>
 * </tr>
 * </table>
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
public class HanyuPinyinVCharType {

  /**
   * The option indicates that the output of 'ü' is "u:"
   */
  public static final HanyuPinyinVCharType WITH_U_AND_COLON =
      new HanyuPinyinVCharType("WITH_U_AND_COLON");

  /**
   * The option indicates that the output of 'ü' is "v"
   */
  public static final HanyuPinyinVCharType WITH_V = new HanyuPinyinVCharType("WITH_V");

  /**
   * The option indicates that the output of 'ü' is "ü" in Unicode form
   */
  public static final HanyuPinyinVCharType WITH_U_UNICODE =
      new HanyuPinyinVCharType("WITH_U_UNICODE");

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

  /**
   * Constructor
   */
  protected HanyuPinyinVCharType(String name) {
    setName(name);
  }

  protected String name;
}
