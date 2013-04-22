package com.hp.hpl.sparta;

/** This interface should be used by any class which returns a DOM 
 Document created by parsing XML.

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
 @author Sergio Marti

 **/

interface DocumentSource extends ParseSource {

  /** The parsed document. */
  Document getDocument();

}


// $Log: DocumentSource.java,v $
// Revision 1.1.1.1  2002/08/19 05:04:01  eobrain
// import from HP Labs internal CVS
//
// Revision 1.3  2002/08/19 00:36:58  eob
// Tweak javadoc comment.
//
// Revision 1.2  2002/08/18 04:34:45  eob
// Make interface package-private so as not to clutter up the javadoc.
//
// Revision 1.1  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
