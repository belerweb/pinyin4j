package com.hp.hpl.sparta.xpath;

/**
 * [text()='value'] expression
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
 @version  $Date: 2002/12/13 22:42:22 $  $Revision: 1.6 $
 @author Eamonn O'Brien-Strain
 */
public class TextEqualsExpr extends TextCompareExpr {
  TextEqualsExpr(String value) {
    super(value);
  }

  /**
   * @see com.hp.hpl.sparta.xpath.BooleanExpr#accept(BooleanExprVisitor)
   */
  public void accept(BooleanExprVisitor visitor) throws XPathException {
    visitor.visit(this);
  }

  public String toString() {
    return toString("=");
  }

}

// $Log: TextEqualsExpr.java,v $
// Revision 1.6  2002/12/13 22:42:22  eobrain
// Fix javadoc.
//
// Revision 1.5  2002/12/13 18:08:34  eobrain
// Factor Visitor out into separate visitors for node tests and predicates.
//
// Revision 1.4  2002/12/06 23:41:49  eobrain
// Add toString() which returns the original XPath.
//
// Revision 1.3  2002/10/30 16:47:00  eobrain
// Comment change only
//
// Revision 1.2  2002/10/30 16:46:20  eobrain
// Comment change only
//
// Revision 1.1  2002/10/30 16:23:42  eobrain
// Feature request [ 630127 ] Support /a/b[text()='foo']
// http://sourceforge.net/projects/sparta-xml/
