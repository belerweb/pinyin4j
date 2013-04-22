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

import java.io.BufferedInputStream;

/**
 * Helper class for file resources
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class ResourceHelper {
  /**
   * @param resourceName
   * @return resource (mainly file in file system or file in compressed
   *         package) as BufferedInputStream
   */
  static BufferedInputStream getResourceInputStream(String resourceName) {
    return new BufferedInputStream(ResourceHelper.class.getResourceAsStream(resourceName));
  }
}
