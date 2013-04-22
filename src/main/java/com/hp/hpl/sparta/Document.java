package com.hp.hpl.sparta;

import java.io.*;
import java.util.*;

import com.hp.hpl.sparta.xpath.*;

/** An XML Document.

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
 @version  $Date: 2003/11/01 05:42:18 $  $Revision: 1.12 $
 @author Eamonn O'Brien-Strain


 @see org.w3c.dom.Document
 */
public class Document extends Node {

  static private final boolean DEBUG = false;

  Document(String systemId) {
    systemId_ = systemId;
  }

  /**
   * Create new empty in-memory Document with a null documentElement.
   */
  public Document() {
    systemId_ = "MEMORY";
  }

  /** Deep copy of this document. Any annotation is not copied. */
  public Object clone() {
    Document copy = new Document(systemId_);
    copy.rootElement_ = (Element) rootElement_.clone();
    return copy;
  }

  /**
   * @return the filename, URL, or other ID by which this document is known.
   * Initialized to "MEMORY" for Document created with default constructor.
   */
  public String getSystemId() {
    return systemId_;
  }

  /**
   * @param systemId the filename, URL, or other ID by which this document is known.
   */
  public void setSystemId(String systemId) {
    systemId_ = systemId;
    notifyObservers();
  }

  /** Same as {@link #getSystemId  getSystemId} */
  public String toString() {
    return systemId_;
  }

  /**
   * @return root element of this DOM tree.
   */
  public Element getDocumentElement() {
    return rootElement_;
  }

  /**
   * Set the root element of this DOM tree.
   */
  public void setDocumentElement(Element rootElement) {
    rootElement_ = rootElement;
    rootElement_.setOwnerDocument(this);
    notifyObservers();
  }

  private XPathVisitor visitor(String xpath, boolean expectStringValue) throws XPathException //, IOException
  {
    if (xpath.charAt(0) != '/') xpath = "/" + xpath;
    return visitor(XPath.get(xpath), expectStringValue);
  }

  XPathVisitor visitor(XPath parseTree, boolean expectStringValue) throws XPathException {
    if (parseTree.isStringValue() != expectStringValue) {
      String msg =
          expectStringValue ? "evaluates to element not string" : "evaluates to string not element";
      throw new XPathException(parseTree, "\"" + parseTree + "\" evaluates to " + msg);
    }
    return new XPathVisitor(this, parseTree);
  }

  /** Select all the elements that match the absolute XPath
      expression in this document. */
  public Enumeration xpathSelectElements(String xpath) throws ParseException //, IOException
  {
    try {

      if (xpath.charAt(0) != '/') xpath = "/" + xpath;
      XPath parseTree = XPath.get(xpath);
      monitor(parseTree);
      return visitor(parseTree, false).getResultEnumeration();

    } catch (XPathException e) {
      throw new ParseException("XPath problem", e);
    }
  }

  static private final Integer ONE = new Integer(1);

  void monitor(XPath parseTree) throws XPathException {
    if (DEBUG) {
      String indexingAttr = parseTree.getIndexingAttrNameOfEquals();
      if (indexingAttr != null) {
        String xpath = parseTree.toString();
        String prefix = xpath.substring(0, xpath.lastIndexOf('='));
        Integer count = (Integer) indexible_.get(prefix);
        if (count == null)
          count = ONE;
        else
          count = new Integer(count.intValue() + 1);
        indexible_.put(prefix, count);
        if (count.intValue() > 100)
          System.out.println("COULD-BE-INDEXED: " + xpath + " used " + count + " times in " + this);
      }
    }
  }

  /** Select all the strings that match the absolute XPath
      expression in this document. */
  public Enumeration xpathSelectStrings(String xpath) throws ParseException {
    try {

      return visitor(xpath, true).getResultEnumeration();

    } catch (XPathException e) {
      throw new ParseException("XPath problem", e);
    }
  }

  /** Select the first element that matches the absolute XPath
      expression in this document, or null if
      there is no match. */
  public Element xpathSelectElement(String xpath) throws ParseException {
    try {

      if (xpath.charAt(0) != '/') xpath = "/" + xpath;
      XPath parseTree = XPath.get(xpath);
      monitor(parseTree);
      return visitor(parseTree, false).getFirstResultElement();

    } catch (XPathException e) {
      throw new ParseException("XPath problem", e);
    }
  }

  /** Select the first element that matches the absolute XPath
      expression in this document, or null if
      there is no match. */
  public String xpathSelectString(String xpath) throws ParseException {
    try {

      return visitor(xpath, true).getFirstResultString();

    } catch (XPathException e) {
      throw new ParseException("XPath problem", e);
    }
  }

  /** Just like Element.xpathEnsure, but also handles case of no documentElement.
   */
  public boolean xpathEnsure(String xpath) throws ParseException {
    try {

      //Quick exit for common case
      if (xpathSelectElement(xpath) != null) return false;

      //Split XPath into dirst step and bit relative to rootElement
      final XPath parseTree = XPath.get(xpath);
      int stepCount = 0;
      for (Enumeration i = parseTree.getSteps(); i.hasMoreElements();) {
        i.nextElement();
        ++stepCount;
      }
      Enumeration i = parseTree.getSteps();
      Step firstStep = (Step) i.nextElement();
      Step[] rootElemSteps = new Step[stepCount - 1];
      for (int j = 0; j < rootElemSteps.length; ++j)
        rootElemSteps[j] = (Step) i.nextElement();

      //Create root element if necessary
      if (rootElement_ == null) {
        Element newRoot = makeMatching(null, firstStep, xpath);
        setDocumentElement(newRoot);
      } else {
        Element expectedRoot = xpathSelectElement("/" + firstStep);
        if (expectedRoot == null)
          throw new ParseException("Existing root element <" + rootElement_.getTagName()
              + "...> does not match first step \"" + firstStep + "\" of \"" + xpath);
      }

      if (rootElemSteps.length == 0)
        return true;
      else
        return rootElement_.xpathEnsure(XPath.get(false, rootElemSteps).toString());

    } catch (XPathException e) {
      throw new ParseException(xpath, e);
    }
  }

  /** @see Document#xpathGetIndex(String) */
  public class Index implements Observer {

    Index(XPath xpath) throws XPathException {
      attrName_ = xpath.getIndexingAttrName();
      xpath_ = xpath;
      addObserver(this);
    }

    /**
     * @param a value of the indexing attribute
     * @return enumeration of Elements
     * @throws ParseException when XPath that created this Index is malformed.
     */
    public synchronized Enumeration get(String attrValue) throws ParseException {
      if (dict_ == null) regenerate();
      Vector elemList = (Vector) dict_.get(attrValue);
      return elemList == null ? EMPTY : elemList.elements();
    }

    /**
     * @return number of elements returned by {@link #get(String) get}
     * @throws ParseException
     */
    public synchronized int size() throws ParseException {
      if (dict_ == null) regenerate();
      return dict_.size();
    }

    /**
     * @see com.hp.hpl.sparta.Document.Observer#update(Document)
     */
    public synchronized void update(Document doc) {
      dict_ = null; //force index to be regenerated on next get()
    }

    private void regenerate() throws ParseException {
      try {

        dict_ = Sparta.newCache();
        for (Enumeration i = visitor(xpath_, false).getResultEnumeration(); i.hasMoreElements();) {
          Element elem = (Element) i.nextElement();
          String attrValue = elem.getAttribute(attrName_);
          Vector elemList = (Vector) dict_.get(attrValue);
          if (elemList == null) {
            elemList = new Vector(1);
            dict_.put(attrValue, elemList);
          }
          elemList.addElement(elem);
        }

      } catch (XPathException e) {
        throw new ParseException("XPath problem", e);
      }
    }

    private transient Sparta.Cache dict_ = null;
    private final XPath xpath_;
    private final String attrName_;
  }

  static final Enumeration EMPTY = new EmptyEnumeration();

  /**
   * @see #xpathGetIndex
   * @return whether an index existst for this xpath
   */
  public boolean xpathHasIndex(String xpath) {
    return indices_.get(xpath) != null;
  }

  /**
   * For faster lookup by XPath return (creating if necessary) an
   * index.  The xpath should be of the form "xp[@attrName]" where
   * xp is an xpath, not ending in a "[...]" predicate, that returns
   * a list of elements.  Doing a get("foo") on the index is
   * equivalent to doing an
   * xpathSelectElement("xp[@attrName='foo']") on the document
   * except that it is faster ( O(1) as apposed to O(n) ).
   * EXAMPLE:<PRE>
   *   Enumeration leaders;
   *   if( doc.xpathHasIndex( "/Team/Members[@firstName]" ){
   *     //fast version
   *     Document.Index index = doc.xpathGetIndex( "/Team/Members[@role]" );
   *     leaders = index.get("leader");
   *   }else
   *     //slow version
   *     leaders = doc.xpathSelectElement( "/Team/Members[@role='leader']" );
   *</PRE>
   *
   * */
  public Index xpathGetIndex(String xpath) throws ParseException {
    try {

      Index index = (Index) indices_.get(xpath);
      //TODO: cacnonicalize key (use XPath object as key)
      if (index == null) {
        XPath xp = XPath.get(xpath);
        index = new Index(xp);
        indices_.put(xpath, index);
      }
      return index;

    } catch (XPathException e) {
      throw new ParseException("XPath problem", e);
    }
  }

  /*public void removeIndices() {
      indices_.clear();
  }*/

  /** Something that is informed whenever the document changes. */
  public interface Observer {
    /** Called when the document changes. */
    void update(Document doc);
  }

  public void addObserver(Observer observer) {
    observers_.addElement(observer);
  }

  public void deleteObserver(Observer observer) {
    observers_.removeElement(observer);
  }

  /** Accumulate text nodes hierarchically. */
  public void toString(Writer writer) throws IOException {
    rootElement_.toString(writer);
  }

  void notifyObservers() {
    for (Enumeration i = observers_.elements(); i.hasMoreElements();)
      ((Observer) i.nextElement()).update(this);
  }

  /**
   * Write DOM to XML.
   */
  public void toXml(Writer writer) throws IOException {
    writer.write("<?xml version=\"1.0\" ?>\n");
    rootElement_.toXml(writer);
  }

  /** Two documents are equal IFF their document elements are equal. */
  public boolean equals(Object thatO) {

    //Do cheap tests first
    if (this == thatO) return true;
    if (!(thatO instanceof Document)) return false;
    Document that = (Document) thatO;
    return this.rootElement_.equals(that.rootElement_);
  }

  /** Called whenever cached version of hashCode needs to be regenerated. */
  protected int computeHashCode() {
    return rootElement_.hashCode();
  }

  /**
   * @link aggregation
   * @label documentElement
   */
  private Element rootElement_ = null;
  private String systemId_;
  private Sparta.Cache indices_ = Sparta.newCache();
  private Vector observers_ = new Vector();

  private final Hashtable indexible_ = DEBUG ? new Hashtable() : null;
}


class EmptyEnumeration implements Enumeration {
  public boolean hasMoreElements() {
    return false;
  }

  public Object nextElement() {
    throw new NoSuchElementException();
  }
}

// $Log: Document.java,v $
// Revision 1.12  2003/11/01 05:42:18  eobrain
// Add synchronized on some methods to make thread-safe.
//
// Revision 1.11  2003/07/17 23:52:05  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.10  2003/06/19 20:28:20  eobrain
// Hash code optimization.
// Add monitoring (in debug mode) to detect when indexing could optimize.
//
// Revision 1.9  2003/05/12 19:56:03  eobrain
// Performance improvements.
//
// Revision 1.8  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.7  2003/01/09 00:55:26  yuhongx
// Use jdk1.1 API (replaced add() with addElement()).
//
// Revision 1.6  2002/12/13 22:44:36  eobrain
// Remove redundant get/set annotation that is already in superclass.
//
// Revision 1.5  2002/12/13 18:12:16  eobrain
// Fix xpathEnsure to handle case when the XPath given specifies a root node tagname that conflicts with the existing root node.  Extend xpathEnsure to work with any type of predicate.  Replace hacky string manipulation code with code that works on the XPath parse tree.
//
// Revision 1.4  2002/11/06 02:57:59  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.3  2002/10/30 16:39:02  eobrain
// Fixed bug [ 627024 ] doc.xpathEnsure("/top") throws exception
// http://sourceforge.net/projects/sparta-xml/
//
// Revision 1.2  2002/09/12 23:00:57  eobrain
// Allow Document.xpathEnsure to work when there is no root element set.
//
// Revision 1.1.1.1  2002/08/19 05:03:55  eobrain
// import from HP Labs internal CVS
//
// Revision 1.24  2002/08/18 04:19:18  eob
// Sparta no longer throws XPathException -- it throws ParseException
// instead.
//
// Revision 1.23  2002/08/17 22:41:41  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.22  2002/08/15 22:39:53  eob
// Fix bug in which index was not getting put into index hash.
//
// Revision 1.21  2002/08/15 21:25:18  eob
// Constructor no longer needs documenent.
//
// Revision 1.20  2002/08/15 05:07:48  eob
// Add indexing for fast XPath lookup.
//
// Revision 1.19  2002/08/13 22:54:39  eob
// Added xpath indexing for faster lookup.
//
// Revision 1.18  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.17  2002/06/14 19:36:42  eob
// Make toString of Node do the same as in XSLT -- recursive
// concatenation of all text in text nodes.
//
// Revision 1.16  2002/05/23 21:04:35  eob
// Add better error reporting.
//
// Revision 1.15  2002/05/10 21:37:42  eob
// equals added
//
// Revision 1.14  2002/05/09 16:47:50  eob
// Add cloneDocument
//
// Revision 1.13  2002/03/26 01:41:11  eob
// Deprecate XPathAPI
//
// Revision 1.12  2002/02/23 01:43:26  eob
// Add clone method.  Tweak toXml API.
//
// Revision 1.11  2002/02/15 21:20:35  eob
// Rename xpath* methods to xpathSelect* to make more obvious.
//
// Revision 1.10  2002/02/15 21:05:28  eob
// Add convenient xpath* methods, allowing a more object-oriented use than
// XPathAPI.
//
// Revision 1.9  2002/02/04 22:09:04  eob
// Add defualt constructer.
//
// Revision 1.8  2002/02/01 21:49:45  eob
// Make Document inherit from Node.  Needed for XPath.
//
// Revision 1.7  2002/01/04 00:36:52  eob
// add annotation
//
// Revision 1.6  2002/01/04 16:48:44  eob
// Comment changes only.
//
// Revision 1.5  2002/01/04 14:48:56  eob
// Remove Log
//
// Revision 1.4  2002/01/04 14:46:21  eob
// comment change only
//
// Revision 1.3  2002/01/04 14:39:19  eob
// Move parse functionality functionality to ParseSource.
//
// Revision 1.2  2001/12/20 20:06:28  eob
// Fix some entity bugs.  Use UTD-8 or UTF-16 encoding as appropriate.
//
// Revision 1.1  2001/12/19 05:52:38  eob
// initial
