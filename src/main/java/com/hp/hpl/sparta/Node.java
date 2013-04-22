package com.hp.hpl.sparta;

import java.io.*;
import java.util.*;

import com.hp.hpl.sparta.xpath.*;

/**
 * An XML node.

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
 @version  $Date: 2003/07/28 04:30:56 $  $Revision: 1.10 $
 @author Eamonn O'Brien-Strain
 */

public abstract class Node {

  void notifyObservers() {
    hash_ = 0; //clear hash_ cache
    if (doc_ != null) doc_.notifyObservers();
  }

  void setOwnerDocument(Document doc) {
    doc_ = doc;
  }

  /** The document that contains this node.  null IFF this is a Document */
  public Document getOwnerDocument() {
    return doc_;
  }

  /** The element that contains this node or null if this is a Document
   * or the root element of a document. */
  public Element getParentNode() {
    return parentNode_;
  }

  /** Return the previous node in the parent's list of children, or null if no such node. */
  public Node getPreviousSibling() {
    return previousSibling_;
  }

  /** Return the next node in the parent's list of children, or null if no such node. */
  public Node getNextSibling() {
    return nextSibling_;
  }

  /** @see #setAnnotation */
  public Object getAnnotation() {
    return annotation_;
  }

  /** Use by client to attach arbitrary data to DOM document.
   *  Does not update indices and other observers.
   *  */
  public void setAnnotation(Object annotation) {
    annotation_ = annotation;
  }

  void setParentNode(Element parentNode) {
    parentNode_ = parentNode;
  }

  void insertAtEndOfLinkedList(Node lastChild) {
    previousSibling_ = lastChild;
    if (lastChild != null) lastChild.nextSibling_ = this;
  }

  void removeFromLinkedList() {
    if (previousSibling_ != null) previousSibling_.nextSibling_ = nextSibling_;
    if (nextSibling_ != null) nextSibling_.previousSibling_ = previousSibling_;
    nextSibling_ = null;
    previousSibling_ = null;
  }

  void replaceInLinkedList(Node replacement) {
    if (previousSibling_ != null) previousSibling_.nextSibling_ = replacement;
    if (nextSibling_ != null) nextSibling_.previousSibling_ = replacement;
    replacement.nextSibling_ = nextSibling_;
    replacement.previousSibling_ = previousSibling_;
    nextSibling_ = null;
    previousSibling_ = null;
  }

  /** XML representation of this node. */
  public String toXml() throws IOException {
    //Cannot use StringWriter because not supported by J2ME
    ByteArrayOutputStream o = new ByteArrayOutputStream();
    Writer w = new OutputStreamWriter(o);
    toXml(w);
    w.flush();
    return new String(o.toByteArray());
  }

  /** For an xpath expression of the form "xpathPrefix/@attrName" set the
   *  attribute "attrName" to attrValue on all elements that match
   *  "XpathPrefix" which is an arbitrary xpath expression matching elements,
   *  or for an xpath expression of the form "xpathPrefixe/text()" set the
   *  text of all matching text nodes.
   *
   (The following doc is used in the examples below.)<pre>
  
   <a>
   <b x="ppp"/>
   <b y="qqq"/>
   <b/>
   </a>
  
   </pre>
   * <ul>
   * <li>node.xpathSetStrings( "xpathPrefix/@attrName", value )
   * is equivalent to<pre>
   foreach element in node.xpathSelectElement(xpathPrefix):
   element.setAttribute( "attrName", value );
   </pre>
   <li> doc.xpathSetStrings( "/a/b/@x", "rrr" )
   will result in<pre>
  
   <a>
   <b x="rrr"/>
   <b x="rrr" y="qqq"/>
   <b x="rrr"/>
   </a>
  
   </pre>
   (Every matching child gets its attribute set.)
  
   <li> To set only the first child you would have to do
   doc.xpathSetStrings( "/a/b[@x]/@x", "rrr" )
   which would result in:<pre>
  
   <a>
   <b x="rrr"/>
   <b y="qqq"/>
   <b/>
   </a>
  
   </pre>
  
   <li> Not matching calls silently do nothing.
  
   <li> doc.xpathSetStrings("/a/b/text()", "TTT" ) will result in:
   <pre>
  
   <a>
   <b x="ppp">TTT</b>
   <b y="qqq">TTT</b>
   <b>TTT</b>
   </a>
  
   </pre>
  
   <li> doc.xpathSetStrings("/a/text()", "TTT" ) will result in:
   <pre>
  
   <a>TTT<b x="ppp"/><b y="qqq"/><b/></a>
  
   </pre>
   </ul>
   @return true iff anything changed
   */
  public boolean xpathSetStrings(String xpath, String value) throws ParseException
  //throws XPathException, IOException
  {
    //This is currently implemented using string manipulation.  It would
    // be better to work on the XPath objects.
    boolean changed = false;
    try {
      int slash = xpath.lastIndexOf('/');
      if (!xpath.substring(slash + 1).equals("text()") && xpath.charAt(slash + 1) != '@')
        throw new ParseException("Last step of Xpath expression \"" + xpath
            + "\" is not \"text()\" and does not start with a '@'. It starts with a '"
            + xpath.charAt(slash + 1) + "'");
      String elemXPath = xpath.substring(0, slash);
      if (xpath.charAt(slash + 1) == '@') {
        String attrName = xpath.substring(slash + 2);
        if (attrName.length() == 0)
          throw new ParseException("Xpath expression \"" + xpath
              + "\" specifies zero-length attribute name\"");
        Enumeration i = xpathSelectElements(elemXPath);
        while (i.hasMoreElements()) {
          Element element = (Element) i.nextElement();
          String oldValue = element.getAttribute(attrName);
          if (!value.equals(oldValue)) {
            element.setAttribute(attrName, value);
            changed = true;
          }
        }
      } else {
        Enumeration i = xpathSelectElements(elemXPath);
        changed = i.hasMoreElements();
        while (i.hasMoreElements()) {
          Element parentOfText = (Element) i.nextElement();

          //Create a set of text nodes.  Need to do this
          //because do not want to delete from what we are
          //iterating over.

          // change LinkedList to Vector to make the code work
          // with PersonalJava.
          // List textNodes = new LinkedList();
          Vector textNodes = new Vector();
          for (Node j = parentOfText.getFirstChild(); j != null; j = j.getNextSibling())
            if (j instanceof Text) {
              // textNodes.add((Text) j);
              textNodes.addElement((Text) j);
            }

          if (textNodes.size() == 0) {

            //If no existing text node add one
            Text text = new Text(value);
            if (text.getData().length() > 0) {
              parentOfText.appendChild(text);
              changed = true;
            }

          } else {

            //Set value of first text node
            // Text first = (Text) textNodes.remove(0);
            Text first = (Text) textNodes.elementAt(0);
            if (!first.getData().equals(value)) {
              textNodes.removeElementAt(0);
              first.setData(value);
              changed = true;
            }

            //Remove all subsequent text nodes
            // for (Iterator j = textNodes.iterator(); j.hasNext();) {
            //     Text text = (Text) j.next();
            for (int j = 0; j < textNodes.size(); j++) {
              Text text = (Text) textNodes.elementAt(j);
              parentOfText.removeChild(text);
              changed = true;
            }

          }

        }
      }
      return changed;

    } catch (DOMException e) {
      throw new Error("Assertion failed " + e);
    } catch (IndexOutOfBoundsException e) {
      throw new ParseException("Xpath expression \"" + xpath
          + "\" is not in the form \"xpathExpression/@attributeName\"");
    }
  }

  Element makeMatching(final Element parent, Step step, final String msgContext)
      throws ParseException, XPathException {
    NodeTest nodeTest = step.getNodeTest();
    if (!(nodeTest instanceof ElementTest))
      throw new ParseException("\"" + nodeTest + "\" in \"" + msgContext
          + "\" is not an element test");
    ElementTest elemTest = (ElementTest) nodeTest;
    final String tagName = elemTest.getTagName();

    final Element newChild = new Element(tagName);

    BooleanExpr predicate = step.getPredicate();

    predicate.accept(new BooleanExprVisitor() {
      public void visit(TrueExpr a) {
      //do nothing
      }

      public void visit(AttrExistsExpr a) throws XPathException {
        newChild.setAttribute(a.getAttrName(), "something");
      }

      public void visit(AttrEqualsExpr a) throws XPathException {
        newChild.setAttribute(a.getAttrName(), a.getAttrValue());
      }

      public void visit(AttrNotEqualsExpr a) throws XPathException {
        newChild.setAttribute(a.getAttrName(), "not " + a.getAttrValue());
      }

      public void visit(AttrLessExpr a) throws XPathException {
        newChild.setAttribute(a.getAttrName(), Long.toString(Long.MIN_VALUE));
      }

      public void visit(AttrGreaterExpr a) throws XPathException {
        newChild.setAttribute(a.getAttrName(), Long.toString(Long.MAX_VALUE));
      }

      public void visit(TextExistsExpr a) throws XPathException {
        newChild.appendChild(new Text("something"));
      }

      public void visit(TextEqualsExpr a) throws XPathException {
        newChild.appendChild(new Text(a.getValue()));
      }

      public void visit(TextNotEqualsExpr a) throws XPathException {
        newChild.appendChild(new Text("not " + a.getValue()));
      }

      public void visit(PositionEqualsExpr a) throws XPathException {
        int posn = a.getPosition();
        if (parent == null && posn != 1)
          throw new XPathException(XPath.get(msgContext), "Position of root node must be 1");
        int lastPosition = 1; //newChild is at position 1
        while (lastPosition < posn) {
          parent.appendChild(new Element(tagName));
          ++lastPosition;
        }

      }
    });
    return newChild;
  }

  /** Select all the elements that match the relative XPath
      expression with respect to this node. */
  public abstract Enumeration xpathSelectElements(String xpath) throws ParseException;

  //throws XPathException, IOException;

  /** Select all the strings that match the relative XPath
      expression with respect to this node. */
  public abstract Enumeration xpathSelectStrings(String xpath) throws ParseException;

  //throws XPathException, IOException;

  /** Select the first element that matches the relative XPath
      expression with respect to this node, or null if
      there is no match.*/
  public abstract Element xpathSelectElement(String xpath) throws ParseException;

  /** Select the first element that matches the relative XPath
      expression with respect to this node, or null if
      there is no match. */
  public abstract String xpathSelectString(String xpath) throws ParseException;

  abstract public Object clone();

  //public abstract Node cloneNode(Document doc) throws DOMException;

  /** Hierarchically concatenated text nodes.*/
  public String toString() {
    try {
      //Cannot use StringWriter because not supported by J2ME
      ByteArrayOutputStream o = new ByteArrayOutputStream();
      Writer w = new OutputStreamWriter(o);
      toString(w);
      w.flush();
      return new String(o.toByteArray());
    } catch (IOException e) {
      return super.toString();
    }
  }

  abstract void toString(Writer writer) throws IOException;

  abstract void toXml(Writer writer) throws IOException;

  /** Quote special XML characters '<', '>', '&', '"' if necessary,
   *  and write to character stream.  We write to a character stream
   *  rather than simply returning a stream to avoid creating
   *  unneccessary objects.*/
  static protected void htmlEncode(Writer writer, String string) throws IOException {
    int n = string.length();
    int writeNext = 0;
    for (int i = 0; i < n; ++i) {
      int ch = string.charAt(i);
      String encoded;
      if (ch >= 128)
        encoded = "&#" + ch + ";";
      else
        switch (ch) {
          case '<':
            encoded = "&lt;";
            break;
          case '>':
            encoded = "&gt;";
            break;
          case '&':
            encoded = "&amp;";
            break;
          case '\"':
            encoded = "&quot;";
            break;
          case '\'':
            encoded = "&#39;";
            break;
          default:
            encoded = null;
            break;
        }
      if (encoded != null) {
        writer.write(string, writeNext, i - writeNext);
        writer.write(encoded);
        writeNext = i + 1;
      }
    }
    if (writeNext < n) writer.write(string, writeNext, n - writeNext);
  }

  /** Called whenever cached version of hashCode needs to be regenerated. */
  abstract protected int computeHashCode();

  /* 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    if (hash_ == 0) hash_ = computeHashCode();
    return hash_;
  }

  /**
   * @label ownerDocument
   */
  private Document doc_ = null;

  /**
   * @label parent
   */
  private Element parentNode_ = null;

  /**
   * @label previousSibling
   */
  private Node previousSibling_ = null;

  /**
   * @label nextSibling
   */
  private Node nextSibling_ = null;
  private Object annotation_ = null;

  private int hash_ = 0;
}

// $Log: Node.java,v $
// Revision 1.10  2003/07/28 04:30:56  eobrain
// Encode single quote character when xml encoding.
//
// Revision 1.9  2003/07/17 23:58:40  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.8  2003/06/19 20:28:20  eobrain
// Hash code optimization.
// Add monitoring (in debug mode) to detect when indexing could optimize.
//
// Revision 1.7  2003/05/12 20:55:07  eobrain
// Fix javadoc.
//
// Revision 1.6  2003/05/12 19:59:30  eobrain
// Make serializable.
//  Fix cloning to not include linked list pointers.
// Method xpathSetString returns boolean saying whether it changed anything.
//
// Revision 1.5  2003/01/09 00:59:33  yuhongx
// Use JDK1.1 API to make code work with PersonalJava.
//
// Revision 1.4  2002/12/13 23:09:24  eobrain
// Fix javadoc.
//
// Revision 1.3  2002/12/13 18:12:15  eobrain
// Fix xpathEnsure to handle case when the XPath given specifies a root node tagname that conflicts with the existing root node.  Extend xpathEnsure to work with any type of predicate.  Replace hacky string manipulation code with code that works on the XPath parse tree.
//
// Revision 1.2  2002/11/06 02:57:59  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:03:54  eobrain
// import from HP Labs internal CVS
//
// Revision 1.15  2002/08/18 04:21:39  eob
// Sparta no longer throws XPathException -- it throws ParseException
// instead.
//
// Revision 1.14  2002/08/15 21:27:48  eob
// Constructor no longer needs documenent.
//
// Revision 1.13  2002/08/15 05:08:54  eob
// Notify observers.
//
// Revision 1.12  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.11  2002/07/08 22:37:18  eob
// Add xpathEnsure
//
// Revision 1.10  2002/06/21 00:27:30  eob
// Allow setting of text()
//
// Revision 1.9  2002/06/14 19:37:34  eob
// Make toString of Node do the same as in XSLT -- recursive
// concatenation of all text in text nodes.
//
// Revision 1.8  2002/05/23 21:24:58  eob
// Change htmlEncode so that it uses charArray write instead of single
// character writes.  This optimization was done because performance
// profiling showed that this method is heavily used.
//
// Revision 1.7  2002/05/11 00:14:04  eob
// Add xpathSetAttributes
//
// Revision 1.6  2002/05/09 16:49:27  eob
// Add support for replace.
//
// Revision 1.5  2002/03/28 01:23:18  jrowson
// fixed bugs related to client side caching
//
// Revision 1.4  2002/03/26 23:11:42  eob
// Encode characters >= 128 bits.
//
// Revision 1.3  2002/02/23 02:06:19  eob
// Add clone method.  Tweak toXml API.
//
// Revision 1.2  2002/02/01 21:54:43  eob
// Move toXml up to Node from Element.
//
// Revision 1.1  2002/01/05 07:32:46  eob
// initial
