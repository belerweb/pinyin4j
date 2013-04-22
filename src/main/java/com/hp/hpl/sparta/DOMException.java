package com.hp.hpl.sparta;

/**
 * Thrown when problem constructing the DOM.

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
 @version  $Date: 2002/08/19 05:04:01 $  $Revision: 1.1.1.1 $
 @see org.w3c.dom.DOMException
 @author Eamonn O'Brien-Strain
 */
public class DOMException extends Exception {
  public DOMException(short code, String message) {
    super(message);
    this.code = code;
  }

  public short code;
  //public static final short           INDEX_SIZE_ERR       = 1;
  public static final short DOMSTRING_SIZE_ERR = 2;
  public static final short HIERARCHY_REQUEST_ERR = 3;
  //public static final short           WRONG_DOCUMENT_ERR   = 4;
  //public static final short           INVALID_CHARACTER_ERR = 5;
  //public static final short           NO_DATA_ALLOWED_ERR  = 6;
  //public static final short           NO_MODIFICATION_ALLOWED_ERR = 7;
  public static final short NOT_FOUND_ERR = 8;
  //public static final short           NOT_SUPPORTED_ERR    = 9;
  //public static final short           INUSE_ATTRIBUTE_ERR  = 10;

}

// $Log: DOMException.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:01  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/18 04:32:20  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.2  2002/05/23 21:02:12  eob
// Add DOMSTRING_SIZE_ERR code.
//
// Revision 1.1  2002/01/04 18:28:11  eob
// initial
