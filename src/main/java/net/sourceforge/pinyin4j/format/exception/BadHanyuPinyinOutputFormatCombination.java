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
package net.sourceforge.pinyin4j.format.exception;

/**
 * An exception class indicates the wrong combination of pinyin output formats
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
public class BadHanyuPinyinOutputFormatCombination extends Exception {
  /**
   * Constructor
   * 
   * @param message
   *            the exception message
   */
  public BadHanyuPinyinOutputFormatCombination(String message) {
    super(message);
  }

  /**
   * Automatically generated ID
   */
  private static final long serialVersionUID = -8500822088036526862L;
}
