package com.hp.hpl.sparta;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

/**
 * A section of text in an element.  In XML this can be either CDATA or not:
 * the DOM model does not distinguish between the two encodings.

 <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
 This file is part of Sparta, an XML Parser, DOM, and XPath library.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation; either version 2.1 of
 the License, or (at your option) any later version.  This library
 is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE. </small></blockquote>
 @see <a "href="doc-files/LGPL.txt">GNU Lesser General Public License</a>
 @version  $Date: 2003/07/17 23:58:40 $  $Revision: 1.5 $
 @author Eamonn O'Brien-Strain
 * @see org.w3c.dom.Text
 */

public class Text extends Node {

  /**
   * Create with an initial character as its data.  This can be added to later.
   */
  public Text(String data) {
    text_ = new StringBuffer(data);
  }

  /**
   * Create with an initial character as its data.  This can be added to later.
   */
  public Text(char ch) {
    text_ = new StringBuffer();
    text_.append(ch);
  }

  /** Deep clone: returns Text node with copy of this ones data. */
  public Object clone() {
    return new Text(text_.toString());
  }

  public void appendData(String s) {
    text_.append(s);
    notifyObservers();
  }

  public void appendData(char ch) {
    text_.append(ch);
    notifyObservers();
  }

  /**
   * @param cbuf characters, some of which are to be appended to the text data
   * @param offset the first index in cbuf to copy
   * @param len the number of characters to copy
   */
  public void appendData(char[] cbuf, int offset, int len) {
    text_.append(cbuf, offset, len);
    notifyObservers();
  }

  public String getData() {
    return text_.toString();
  }

  public void setData(String data) {
    text_ = new StringBuffer(data);
    notifyObservers();
  }

  void toXml(Writer writer) throws IOException {
    //System.out.println("Text.toXml "+text_.toString());
    String s = text_.toString();
    if (s.length() < 50)
      //short
      htmlEncode(writer, s);
    else {
      //long
      writer.write("<![CDATA[");
      writer.write(s);
      writer.write("]]>");
    }
  }

  void toString(Writer writer) throws IOException {
    writer.write(text_.toString());
  }

  /** Not implemented. */
  public Enumeration xpathSelectElements(String xpath) {
    throw new Error("Sorry, not implemented");
  }


  /** Not implemented */
  public Enumeration xpathSelectStrings(String xpath) {
    throw new Error("Sorry, not implemented");
  }

  /** Not implemented. */
  public Element xpathSelectElement(String xpath) {
    throw new Error("Sorry, not implemented");
  }

  /** Not implemented. */
  public String xpathSelectString(String xpath) {
    throw new Error("Sorry, not implemented");
  }

  /**   Text nodes can be equal even if they are in different documents,
   *    different parents, different siblings, or different annotations.
   *    They are equal IFF their string data is equal
   *     */
  public boolean equals(Object thatO) {

    //Do cheap tests first
    if (this == thatO) return true;
    if (!(thatO instanceof Text)) return false;
    Text that = (Text) thatO;
    return this.text_.toString().equals(that.text_.toString());
  }

  /** Called whenever cached version of hashCode needs to be regenerated. */
  protected int computeHashCode() {
    return text_.toString().hashCode();
  }

  private StringBuffer text_;
}

// $Log: Text.java,v $
// Revision 1.5  2003/07/17 23:58:40  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.4  2003/06/19 20:20:46  eobrain
// hash code optimization
//
// Revision 1.3  2002/12/13 23:09:24  eobrain
// Fix javadoc.
//
// Revision 1.2  2002/11/06 02:57:59  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:03:53  eobrain
// import from HP Labs internal CVS
//
// Revision 1.11  2002/08/18 04:22:24  eob
// Sparta no longer throws XPathException -- it throws ParseException
// instead.  Remove deprecated constructors.
//
// Revision 1.10  2002/08/15 21:23:00  eob
// Constructor no longer needs document.
//
// Revision 1.9  2002/08/15 05:09:22  eob
// Notify observers.  CDATA
//
// Revision 1.8  2002/08/01 23:29:17  sermarti
// Much faster Sparta parsing.
// Has debug features enabled by default. Currently toggled
// in ParseCharStream.java and recompiled.
//
// Revision 1.7  2002/06/14 19:37:51  eob
// Make toString of Node do the same as in XSLT -- recursive
// concatenation of all text in text nodes.
//
// Revision 1.6  2002/05/23 21:32:05  eob
// Add appendData method for efficiency.  This optimization was done
// because of what performance profiling showed.
//
// Revision 1.5  2002/05/11 00:10:53  eob
// Add stubs for xpathSelect* methods.
//
// Revision 1.4  2002/05/10 21:38:06  eob
// equals added
//
// Revision 1.3  2002/03/28 01:23:18  jrowson
// fixed bugs related to client side caching
//
// Revision 1.2  2002/02/23 02:07:11  eob
// Add clone method.  Tweak toXml API.
//
// Revision 1.1  2002/01/05 07:31:50  eob
// initial
