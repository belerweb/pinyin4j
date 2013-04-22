package com.hp.hpl.sparta;

/**
 * Circular character buffer used to store parsing history for debug
 * purposes.

 <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
 This file is part of Sparta, an XML Parser, DOM, and XPath library.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1 of
 the License, or (at your option) any later version.  This library
 is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.</small></blockquote>
 @see <a "href="doc-files/LGPL.txt">GNU Lesser General Public License</a>
 @version  $Date: 2003/07/17 21:36:29 $  $Revision: 1.2 $
 @author Sergio Marti

 */


class CharCircBuffer {

  CharCircBuffer(int n) {
    buf_ = new int[n];
  }

  void enable() {
    enabled_ = true;
  }

  void disable() {
    enabled_ = false;
  }

  void addInt(int i) {
    addRaw(i + 0x10000);
  }

  void addChar(char ch) {
    addRaw(ch);
  }

  private void addRaw(int v) {
    if (enabled_) {
      buf_[next_] = v;
      next_ = (next_ + 1) % buf_.length;
      ++total_;
    }
  }

  void addString(String s) {
    char[] chars = s.toCharArray();
    int slen = chars.length;
    for (int i = 0; i < slen; ++i)
      addChar(chars[i]);
  }

  public String toString() {
    StringBuffer result = new StringBuffer(11 * buf_.length / 10);
    int first_ = total_ < buf_.length ? buf_.length - total_ : 0;
    for (int i = first_; i < buf_.length; ++i) {
      int ii = (i + next_) % buf_.length;
      int v = buf_[ii];
      if (v < 0x10000)
        result.append((char) v);
      else
        result.append(Integer.toString(v - 0x10000));
    }
    return result.toString();
  }

  private final int[] buf_; //Stores either the chars or the integers+0x10000
  private int next_ = 0;
  private int total_ = 0;
  private boolean enabled_ = true;
}


// $Log: CharCircBuffer.java,v $
// Revision 1.2  2003/07/17 21:36:29  eobrain
// Use integer arithmetic instead of floating-point arithmetic which is
// not supported in the J2ME we were using on a Nokia phone.
//
// Revision 1.1.1.1  2002/08/19 05:04:02  eobrain
// import from HP Labs internal CVS
//
// Revision 1.2  2002/08/18 04:31:45  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.1  2002/08/01 23:29:17  sermarti
// Much faster Sparta parsing.
// Has debug features enabled by default. Currently toggled
// in ParseCharStream.java and recompiled.
