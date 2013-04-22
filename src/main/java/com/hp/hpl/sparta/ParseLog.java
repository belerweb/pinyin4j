package com.hp.hpl.sparta;

/**
 * The parser uses this to report errors.

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
 @version  $Date: 2002/08/19 05:03:57 $  $Revision: 1.1.1.1 $
 @author Eamonn O'Brien-Strain
 @author Sergio Marti
 */

public interface ParseLog {
  void error(String msg, String systemId, int lineNum);

  void warning(String msg, String systemId, int lineNum);

  void note(String msg, String systemId, int lineNum);
}

// $Log: ParseLog.java,v $
// Revision 1.1.1.1  2002/08/19 05:03:57  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/18 04:40:17  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.5  2002/08/17 00:54:14  sermarti
//
// Revision 1.4  2002/08/05 20:04:32  sermarti
//
// Revision 1.3  2002/01/09 00:53:21  eob
// Add warning.
//
// Revision 1.2  2002/01/08 19:51:31  eob
// Distinguish error from note.
//
// Revision 1.1  2002/01/04 00:40:06  eob
// initial
