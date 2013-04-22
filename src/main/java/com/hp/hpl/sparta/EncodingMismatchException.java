package com.hp.hpl.sparta;

/**
 * Thrown when declared encoding does not match assumed encoding.

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
 @author Eamonn O'Brien-Strain

 */

public class EncodingMismatchException extends ParseException {

  EncodingMismatchException(String systemId, String declaredEncoding, String assumedEncoding) {
    super(systemId, 0, declaredEncoding.charAt(declaredEncoding.length() - 1), declaredEncoding,
        "encoding \'" + declaredEncoding + "\' declared instead of of " + assumedEncoding
            + " as expected");
    declaredEncoding_ = declaredEncoding;
  }

  String getDeclaredEncoding() {
    return declaredEncoding_;
  }

  private String declaredEncoding_;

}


// $Log: EncodingMismatchException.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:01  eobrain
// import from HP Labs internal CVS
//
// Revision 1.4  2002/08/18 04:35:44  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.3  2002/08/05 20:04:32  sermarti
//
// Revision 1.2  2002/05/09 16:49:09  eob
// Add history arg.
//
// Revision 1.1  2002/01/08 19:25:38  eob
// initial
