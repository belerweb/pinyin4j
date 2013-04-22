package com.hp.hpl.sparta.xpath;

/**
 * A "text()" node test in a Xpath step.
 * This is part of the GoF Flyweight(195) pattern -- Only one object of
 * this class ever exists, shared amongst all clients.
 * You use INSTANCE instead of the constructor to get
 * that object.

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
 @version  $Date: 2002/12/06 23:39:35 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 */

public class TextTest extends NodeTest {

  //only need one of them => GoF Flyweight Pattern(195) 
  private TextTest() {}

  static final TextTest INSTANCE = new TextTest();

  public void accept(Visitor visitor) throws XPathException {
    visitor.visit(this);
  }

  /** Return true*/
  public boolean isStringValue() {
    return true;
  }

  public String toString() {
    return "text()";
  }
}

// $Log: TextTest.java,v $
// Revision 1.2  2002/12/06 23:39:35  eobrain
// Make objects that are always the same follow the Flyweight Pattern.
//
// Revision 1.1.1.1  2002/08/19 05:04:03  eobrain
// import from HP Labs internal CVS
//
// Revision 1.2  2002/08/18 23:39:05  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.1  2002/06/14 19:33:21  eob
// initial
