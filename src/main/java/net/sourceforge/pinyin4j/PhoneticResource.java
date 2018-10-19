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
class PhoneticResource {
  /**
   * A DOM model contains variable Phonetic presentations
   */
  private Document phoneticMappingDoc;

  /**
   * @param phoneticMappingDoc
   *            The phoneticMappingDoc to set.
   */
  private void setPhoneticMappingDoc(Document phoneticMappingDoc) {
    this.phoneticMappingDoc = phoneticMappingDoc;
  }

  /**
   * @return Returns the PhoneticMappingDoc.
   */
  Document getPhoneticMappingDoc() {
    return phoneticMappingDoc;
  }

  /**
   * Private constructor as part of the singleton pattern.
   */
  private PhoneticResource() {
    initializeResource();
  }

  /**
   * Initialiez a DOM contains variable Phonetic representations
   */
  private void initializeResource() {
    try {
      final String mappingFileName = "/pinyindb/pinyin_mapping.xml";
      final String systemId = "";

      // Parse file to DOM Document
      setPhoneticMappingDoc(Parser.parse(systemId, ResourceHelper
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
  static PhoneticResource getInstance() {
    return PhoneticSystemResourceHolder.theInstance;
  }

  /**
   * Singleton implementation helper.
   */
  private static class PhoneticSystemResourceHolder {
    static final PhoneticResource theInstance = new PhoneticResource();
  }
}
