package com.hp.hpl.sparta;

/** This class returns the DOM Document created by parsing XML.

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
 @version  $Date: 2003/05/12 19:49:29 $  $Revision: 1.3 $
 @author Sergio Marti
 */

class BuildDocument implements DocumentSource, ParseHandler {

  public BuildDocument() {
    this(null);
  }

  public BuildDocument(ParseLog log) {
    log_ = (log == null) ? DEFAULT_LOG : log;
  }

  public void setParseSource(ParseSource ps) {
    parseSource_ = ps;
    doc_.setSystemId(ps.toString());
  }

  public ParseSource getParseSource() {
    return parseSource_;
  }

  public String toString() {
    if (parseSource_ != null)
      return "BuildDoc: " + parseSource_.toString();
    else
      return null;
  }

  public String getSystemId() {
    if (parseSource_ != null)
      return parseSource_.getSystemId();
    else
      return null;
  }

  public int getLineNumber() {
    if (parseSource_ != null)
      return parseSource_.getLineNumber();
    else
      return -1;
  }

  /** The parsed document. */
  public Document getDocument() {
    return doc_;
  }

  public void startDocument() {}

  public void endDocument() {
  /* DEBUG
     if (currentElement_ != null)
     log_.warning("EndDocument: currentElement is not null",
     getSystemId(), getLineNumber());
   */
  }

  public void startElement(Element element) {
    if (currentElement_ == null) {
      doc_.setDocumentElement(element);
    } else {
      currentElement_.appendChild(element);
    }
    currentElement_ = element;
  }

  public void endElement(Element element) {
    /* DEBUG
       if (isCENull())
       return;
       if (element != currentElement_) {
       log_.warning("EndElement (" + element.getTagName() +
       ") does not match currentElement (" +
       currentElement_.getTagName() + ")", getSystemId(),
       getLineNumber());
       return;
       }
     */

    currentElement_ = (Element) currentElement_.getParentNode();
  }

  public void characters(char[] buf, int offset, int len) {
    /* DEBUG
       if (isCENull())
       return;
     */

    Element element = currentElement_;
    if (element.getLastChild() instanceof Text) {
      Text text = (Text) element.getLastChild();
      text.appendData(buf, offset, len);
    } else {
      Text text = new Text(new String(buf, offset, len));
      element.appendChildNoChecking(text);
    }
  }


  private final ParseLog log_;

  private Element currentElement_ = null;
  private final Document doc_ = new Document();
  private ParseSource parseSource_ = null;
}

// $Log: BuildDocument.java,v $
// Revision 1.3  2003/05/12 19:49:29  eobrain
// Remove unused method.
//
// Revision 1.2  2002/10/30 16:40:27  eobrain
// appendChild no longer throws DOMException
//
// Revision 1.1.1.1  2002/08/19 05:04:02  eobrain
// import from HP Labs internal CVS
//
// Revision 1.6  2002/08/18 04:30:54  eob
// Make class package-private so as not to clutter up the javadoc.
//
// Revision 1.5  2002/08/17 19:04:36  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.4  2002/08/15 22:51:37  eob
// Sparta node constructors no longer needs document
//
// Revision 1.3  2002/08/05 20:04:31  sermarti
//
// Revision 1.2  2002/08/01 23:29:17  sermarti
// Much faster Sparta parsing.
// Has debug features enabled by default. Currently toggled
// in ParseCharStream.java and recompiled.
//
// Revision 1.1  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
