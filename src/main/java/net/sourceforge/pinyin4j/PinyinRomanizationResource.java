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

import java.io.FileNotFoundException;
import java.io.IOException;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Parser;

/**
 * Contains the resource supporting translations among different Chinese
 * Romanization systems
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class PinyinRomanizationResource {
  /**
   * A DOM model contains variable pinyin presentations
   */
  private Document pinyinMappingDoc;

  /**
   * @param pinyinMappingDoc
   *            The pinyinMappingDoc to set.
   */
  private void setPinyinMappingDoc(Document pinyinMappingDoc) {
    this.pinyinMappingDoc = pinyinMappingDoc;
  }

  /**
   * @return Returns the pinyinMappingDoc.
   */
  Document getPinyinMappingDoc() {
    return pinyinMappingDoc;
  }

  /**
   * Private constructor as part of the singleton pattern.
   */
  private PinyinRomanizationResource() {
    initializeResource();
  }

  /**
   * Initialiez a DOM contains variable PinYin representations
   */
  private void initializeResource() {
    try {
      final String mappingFileName = "/pinyindb/pinyin_mapping.xml";
      final String systemId = "";

      // Parse file to DOM Document
      setPinyinMappingDoc(Parser.parse(systemId, ResourceHelper
          .getResourceInputStream(mappingFileName)));

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Singleton factory method.
   * 
   * @return the one and only MySingleton.
   */
  static PinyinRomanizationResource getInstance() {
    return PinyinRomanizationSystemResourceHolder.theInstance;
  }

  /**
   * Singleton implementation helper.
   */
  private static class PinyinRomanizationSystemResourceHolder {
    static final PinyinRomanizationResource theInstance = new PinyinRomanizationResource();
  }
}
