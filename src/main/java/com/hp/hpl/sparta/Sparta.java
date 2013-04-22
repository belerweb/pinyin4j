package com.hp.hpl.sparta;

import java.util.Hashtable;

/** This utility class allows you to configure some low-level behavior
 * of the Sparta code such as caching and String interning.
 * If you do not set any of the configurations here then sparta will use
 * default implementations that will work in smaller JVMs such as J2ME.
 * However if you are running in a bigger JVM then you will get better
 * performance if you ocerride the defaults as described in the method
 * descriptions below.  Note that these static methods need to be called
 * <b>before</b> any Sparta classes are loaded, therefore it is best to
 * call them in a static block at the very beginning of your main application
 * class. 
 * 
 <blockquote><small> Copyright (C) 2003 Hewlett-Packard Company.
 This file is part of Sparta, an XML Parser, DOM, and XPath library.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1 of
 the License, or (at your option) any later version.  This library
 is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE. </small></blockquote>
 @see <a "href="doc-files/LGPL.txt">GNU Lesser General Public License</a>
 @version  $Date: 2003/07/18 01:48:41 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 * */
public class Sparta {

  /** Used internally by Sparta code to intern strings because the
   * String.intern method is not supported in older and smaller JVMs. 
   * @see String#intern */
  static public String intern(String s) {
    return internment_.intern(s);
  }


  /** Pass an object that implements this interface to setInternment. */
  static public interface Internment {
    String intern(String s);
  }

  /** Change the String intern to something custom.  For example if
   * you are running on a modern full J2EE or JDSE JVM you almost certainly
   * want to tell Sparta to use the standard String.inter method like this: <PRE>
  public class MyApplication {
  static{
      Sparta.setInternment(new Sparta.Internment(){
          public String intern(String s) {
              return s.intern();
          }
      });
  }
  public static void main(String[] args) {
    ...
  
  </PRE>
   * */
  static public void setInternment(Internment i) {
    internment_ = i;
  }

  /** The default internment used internally that does not rely on
   * String.intern being supported by the JVM. */
  static private Internment internment_ = new Internment() {
    private final Hashtable strings_ = new Hashtable();

    public String intern(String s) {
      String ss = (String) strings_.get(s);
      if (ss == null) {
        strings_.put(s, s);
        return s;
      } else
        return ss;
    }
  };

  //////////////////////////////////////////////////////////////

  /** What a CacheFactory generates. Used internally to cache collections 
   * of objects.*/
  static public interface Cache {
    Object get(Object key);

    Object put(Object key, Object value);

    int size();
  }

  /** You should pass an object that implements this interface to
   * setCacheFactory. */
  static public interface CacheFactory {
    Cache create();
  }

  /** Used internally to create a cache. */
  static Cache newCache() {
    return cacheFactory_.create();
  }

  /** Change the caching to something custom.  The default CacheFactory
   * simply creates Hashtables which grow without bound.  If you are 
   * running Sparta in a long-lived application and you want to avoid
   * memory leaks you should use caches that automatically evict using, for
   * example an LRU mechanism or soft reference.  For example if you have
   * a class called LruMap that sub-classes from hava.util.Map then you
   * can tell Sparta to use that by as follows <PRE>
   * 
  public class MyApplication {
  static private class LruCache extends LruMap implements Sparta.Cache {}
  static{
      Sparta.setCacheFactory(new Sparta.CacheFactory(){
          public Sparta.Cache create() {
              return new SoftCache();
          }
      });
  }
  public static void main(String[] args) {
    ...
  </PRE>
   * */
  static public void setCacheFactory(CacheFactory f) {
    cacheFactory_ = f;
  }

  static private class HashtableCache extends Hashtable implements Cache {}

  static private CacheFactory cacheFactory_ = new CacheFactory() {
    public Cache create() {
      return new HashtableCache();
    }
  };
}
