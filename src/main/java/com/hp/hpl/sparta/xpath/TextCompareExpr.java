package com.hp.hpl.sparta.xpath;

/**
 * Compare text value.
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
 @version  $Date: 2002/12/06 23:41:49 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 */
public abstract class TextCompareExpr extends BooleanExpr {
  TextCompareExpr(String value) {
    value_ = value;
  }

  public String getValue() {
    return value_;
  }

  protected String toString(String op) {
    return "[text()" + op + "\'" + value_ + "\']";
  }

  private final String value_;

}

// $Log: TextCompareExpr.java,v $
// Revision 1.2  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.1  2002/10/30 16:17:59  eobrain
// initial
//
