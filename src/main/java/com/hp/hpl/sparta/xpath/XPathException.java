package com.hp.hpl.sparta.xpath;

import java.io.*;

/** Thrown when some problem parsing or executing an XPath expression.

 <blockquote><small> Copyright (C) 2002 Hewlett-Packard Company.
 This file is part of Sparta, an XML Parser, DOM, and XPath library.
 This library is free software; you can redistribute it and/or
 modify it under the terms of the <a href="doc-files/LGPL.txt">GNU
 Lesser General Public License</a> as published by the Free Software
 Foundation; either version 2.1 of the License, or (at your option)
 any later version.  This library is distributed in the hope that it
 will be useful, but WITHOUT ANY WARRANTY; without even the implied
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE. </small></blockquote>
 @version  $Date: 2003/07/18 00:01:42 $  $Revision: 1.3 $
 @author Eamonn O'Brien-Strain
 */

public class XPathException extends Exception {

  public XPathException(XPath xpath, String msg) {
    super(xpath + " " + msg);
  }

  XPathException(XPath xpath, String where, SimpleStreamTokenizer toks, String expected) {
    this(xpath, where + " got \"" + toString(toks) + "\" instead of expected " + expected);
  }

  XPathException(XPath xpath, Exception cause) {
    super(xpath + " " + cause);
    cause_ = cause;
  }

  public Throwable getCause() {
    return cause_;
  }

  static private String toString(SimpleStreamTokenizer toks) {
    try {
      StringBuffer result = new StringBuffer();
      result.append(tokenToString(toks));
      if (toks.ttype != SimpleStreamTokenizer.TT_EOF) {
        toks.nextToken();
        result.append(tokenToString(toks));
        toks.pushBack();
      }
      return result.toString();
    } catch (IOException e) {
      return "(cannot get  info: " + e + ")";
    }
  }

  static private String tokenToString(SimpleStreamTokenizer toks) {
    switch (toks.ttype) {
      case SimpleStreamTokenizer.TT_EOF:
        return "<end of expression>";
      case SimpleStreamTokenizer.TT_NUMBER:
        return toks.nval + "";
      case SimpleStreamTokenizer.TT_WORD:
        return toks.sval;
      default:
        return (char) toks.ttype + "";
    }
  }

  private Throwable cause_ = null;
}

// $Log: XPathException.java,v $
// Revision 1.3  2003/07/18 00:01:42  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.2  2003/05/12 20:08:29  eobrain
// Inconsequential code change to avoid eclipse warning.
//
// Revision 1.1.1.1  2002/08/19 05:04:02  eobrain
// import from HP Labs internal CVS
//
// Revision 1.4  2002/08/18 23:39:32  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.3  2002/08/15 05:11:57  eob
// add cause
//
// Revision 1.2  2002/05/23 21:07:07  eob
// Better error reporting.
//
// Revision 1.1  2002/01/24 22:11:35  eob
// initial
