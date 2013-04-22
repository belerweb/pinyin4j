package com.hp.hpl.sparta.xpath;

import com.hp.hpl.sparta.Sparta;

/**
 * Compare attribute to string.
 *
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
 @version  $Date: 2003/07/18 00:01:42 $  $Revision: 1.4 $
 @author Eamonn O'Brien-Strain
 */
public abstract class AttrCompareExpr extends AttrExpr {

  AttrCompareExpr(String attrName, String attrValue) {
    super(attrName);
    attrValue_ = Sparta.intern(attrValue);
  }

  /** Result is an interned string for faster comparison.*/
  public String getAttrValue() {
    return attrValue_;
  }

  protected String toString(String op) {
    return "[" + super.toString() + op + "\'" + attrValue_ + "\']";
  }

  private final String attrValue_;

}

// $Log: AttrCompareExpr.java,v $
// Revision 1.4  2003/07/18 00:01:42  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.3  2003/05/12 20:07:04  eobrain
// Performance improvement: intern strings.
//
// Revision 1.2  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.1  2002/10/30 16:16:52  eobrain
// initial
//
