package com.hp.hpl.sparta;

import com.hp.hpl.sparta.xpath.*;

import java.io.IOException;
import java.util.*;

/**
 * Visitor that evaluates an xpath expression relative to a context
 * node by walking over the parse tree of the expression.

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
 @version  $Date: 2003/08/11 18:48:39 $  $Revision: 1.9 $
 @author Eamonn O'Brien-Strain
 * @stereotype visitor */
class XPathVisitor implements Visitor {

  /** Evaluate a relative xpath expression relative to a context
   * element by walking over the parse tree of th expression. */
  private XPathVisitor(XPath xpath, Node context) throws XPathException {
    xpath_ = xpath;
    contextNode_ = context;
    nodelistFiltered_ = new Vector(1);
    nodelistFiltered_.addElement(contextNode_);

    for (Enumeration i = xpath.getSteps(); i.hasMoreElements();) {
      Step step = (Step) i.nextElement();
      multiLevel_ = step.isMultiLevel();
      nodesetIterator_ = null;
      step.getNodeTest().accept(this);
      nodesetIterator_ = nodelistRaw_.iterator();
      nodelistFiltered_.removeAllElements();
      BooleanExpr predicate = step.getPredicate();
      while (nodesetIterator_.hasMoreElements()) {
        node_ = nodesetIterator_.nextElement();
        predicate.accept(this);
        Boolean expr = exprStack_.pop();
        if (expr.booleanValue()) nodelistFiltered_.addElement(node_);
      }
    }
  }

  /** Evaluate a relative xpath expression relative to a context
   * element by walking over the parse tree of th expression. */
  public XPathVisitor(Element context, XPath xpath) throws XPathException {
    this(xpath, context);
    if (xpath.isAbsolute())
      throw new XPathException(xpath, "Cannot use element as context node for absolute xpath");
  }

  /** Evaluate an absolute xpath expression in a document by walking
      over the parse tree of th expression. */
  public XPathVisitor(Document context, XPath xpath) throws XPathException {
    this(xpath, context);
  }

  public void visit(ThisNodeTest a) {
    nodelistRaw_.removeAllElements();
    nodelistRaw_.add(contextNode_, 1);
  }

  /** @throws XPathException if ".." applied to node with no parent. */
  public void visit(ParentNodeTest a) throws XPathException {
    nodelistRaw_.removeAllElements();
    Node parent = contextNode_.getParentNode();
    if (parent == null)
      throw new XPathException(xpath_, "Illegal attempt to apply \"..\" to node with no parent.");
    nodelistRaw_.add(parent, 1);
  }

  public void visit(AllElementTest a) {
    Vector oldNodeList = nodelistFiltered_;
    nodelistRaw_.removeAllElements();
    for (Enumeration i = oldNodeList.elements(); i.hasMoreElements();) {
      Object node = i.nextElement();
      if (node instanceof Element)
        accumulateElements((Element) node);
      else if (node instanceof Document) accumulateElements((Document) node);
    }
  }

  private void accumulateElements(Document doc) {
    Element child = doc.getDocumentElement();
    nodelistRaw_.add(child, 1);
    if (multiLevel_) accumulateElements(child); //recursive call
  }

  private void accumulateElements(Element element) {
    int position = 0;
    for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n instanceof Element) {
        nodelistRaw_.add(n, ++position);
        if (multiLevel_) accumulateElements((Element) n); //recursive call
      }
    }
  }

  public void visit(TextTest a) {
    Vector oldNodeList = nodelistFiltered_;
    nodelistRaw_.removeAllElements();
    for (Enumeration i = oldNodeList.elements(); i.hasMoreElements();) {
      Object node = i.nextElement();
      if (node instanceof Element) {
        Element element = (Element) node;
        for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling())
          if (n instanceof Text) nodelistRaw_.add(((Text) n).getData());
      }
    }
  }

  public void visit(ElementTest test) {
    String tagName = test.getTagName();
    Vector oldNodeList = nodelistFiltered_;
    int n = oldNodeList.size();
    nodelistRaw_.removeAllElements();
    for (int i = 0; i < n; ++i) {
      Object node = oldNodeList.elementAt(i);
      if (node instanceof Element)
        accumulateMatchingElements((Element) node, tagName);
      else if (node instanceof Document) accumulateMatchingElements((Document) node, tagName);
    }
  }

  private void accumulateMatchingElements(Document document, String tagName) {
    Element child = document.getDocumentElement();
    if (child == null) return; //no document element
    if (child.getTagName() == tagName) //both strings interned
      nodelistRaw_.add(child, 1);
    if (multiLevel_) accumulateMatchingElements(child, tagName); //recursive call
  }

  private void accumulateMatchingElements(Element element, String tagName) {
    int position = 0;
    for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n instanceof Element) {
        Element child = (Element) n;
        if (child.getTagName() == tagName) //both strings interned
          nodelistRaw_.add(child, ++position);
        if (multiLevel_) accumulateMatchingElements(child, tagName); //recursion
      }
    }
  }

  public void visit(AttrTest test) {
    Vector oldNodeList = nodelistFiltered_;
    nodelistRaw_.removeAllElements();
    for (Enumeration i = oldNodeList.elements(); i.hasMoreElements();) {
      Node node = (Node) i.nextElement();
      if (node instanceof Element) {
        Element element = (Element) node;
        String attr = element.getAttribute(test.getAttrName());
        if (attr != null) nodelistRaw_.add(attr);
      }
    }
  }

  static private final Boolean TRUE = new Boolean(true);
  static private final Boolean FALSE = new Boolean(false);

  public void visit(TrueExpr a) {
    exprStack_.push(TRUE);
  }

  public void visit(AttrExistsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    String attrValue = element.getAttribute(a.getAttrName());
    boolean result = attrValue != null && attrValue.length() > 0;
    exprStack_.push(result ? TRUE : FALSE);
  }

  public void visit(AttrEqualsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    String attrValue = element.getAttribute(a.getAttrName());
    boolean result = a.getAttrValue().equals(attrValue);
    exprStack_.push(result ? TRUE : FALSE);
  }

  public void visit(AttrNotEqualsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    String attrValue = element.getAttribute(a.getAttrName());
    boolean result = !a.getAttrValue().equals(attrValue);
    exprStack_.push(result ? TRUE : FALSE);
  }

  public void visit(AttrLessExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    // Use jdk1.1 API to make the code work with PersonalJava.
    // double attrValue = Double.parseDouble( element.getAttribute( a.getAttrName() ) );
    long attrValue = Long.parseLong(element.getAttribute(a.getAttrName()));
    boolean result = attrValue < a.getAttrValue();
    exprStack_.push(result ? TRUE : FALSE);
  }

  public void visit(AttrGreaterExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    // Use jdk1.1 API to make the code work with PersonalJava.
    // double attrValue = Double.parseDouble( element.getAttribute( a.getAttrName() ) );
    long attrValue = Long.parseLong(element.getAttribute(a.getAttrName()));
    boolean result = attrValue > a.getAttrValue();
    exprStack_.push(result ? TRUE : FALSE);
  }

  public void visit(TextExistsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    for (Node i = element.getFirstChild(); i != null; i = i.getNextSibling()) {
      if (i instanceof Text) {
        exprStack_.push(TRUE);
        return;
      }
    }
    exprStack_.push(FALSE);
  }

  public void visit(TextEqualsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    for (Node i = element.getFirstChild(); i != null; i = i.getNextSibling()) {
      if (i instanceof Text) {
        Text text = (Text) i;
        if (text.getData().equals(a.getValue())) {
          exprStack_.push(TRUE);
          return;
        }
      }
    }
    exprStack_.push(FALSE);
  }

  public void visit(TextNotEqualsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test attribute of document");
    Element element = (Element) node_;
    for (Node i = element.getFirstChild(); i != null; i = i.getNextSibling()) {
      if (i instanceof Text) {
        Text text = (Text) i;
        if (!text.getData().equals(a.getValue())) {
          exprStack_.push(TRUE);
          return;
        }
      }
    }
    exprStack_.push(FALSE);
  }

  public void visit(PositionEqualsExpr a) throws XPathException {
    if (!(node_ instanceof Element))
      throw new XPathException(xpath_, "Cannot test position of document");
    Element element = (Element) node_;
    boolean result = (nodelistRaw_.position(element) == a.getPosition());
    exprStack_.push(result ? TRUE : FALSE);
  }

  /** Get all the elements or strings that match the xpath expression. */
  public Enumeration getResultEnumeration() {
    return nodelistFiltered_.elements();
  }

  /** Get the first element that match the xpath expression, or null. */
  public Element getFirstResultElement() {
    return nodelistFiltered_.size() == 0 ? null : (Element) nodelistFiltered_.elementAt(0);
  }

  /** Get the first string that match the xpath expression, or null. */
  public String getFirstResultString() {
    return nodelistFiltered_.size() == 0 ? null : (String) nodelistFiltered_.elementAt(0)
        .toString();
  }

  /*
  static private class BooleanStack extends LinkedList{
      void push(Boolean b){
          addLast(b);
      }
      Boolean pop(){
          return (Boolean)removeLast();
      }
  }
   */
  /** Profiling found this to be very heavily used so do not use java.util.LinkedList */
  static private class BooleanStack {
    private Item top_ = null;

    static private class Item {
      Item(Boolean b, Item p) {
        bool = b;
        prev = p;
      }

      final Boolean bool;
      final Item prev;
    }

    void push(Boolean b) {
      top_ = new Item(b, top_);
    }

    Boolean pop() {
      Boolean result = top_.bool;
      top_ = top_.prev;
      return result;
    }
  }

  /** @associates Node. */
  private final NodeListWithPosition nodelistRaw_ = new NodeListWithPosition();
  private Vector nodelistFiltered_ = new Vector();
  private Enumeration nodesetIterator_ = null;
  private Object node_ = null; //String or Element
  private final BooleanStack exprStack_ = new BooleanStack();

  /**
   * @label context
   */
  private/*final (JDK11 problems)*/
  Node contextNode_;

  private boolean multiLevel_;

  private/*final (JDK11 problems)*/
  XPath xpath_;

}


/** A list of nodes, together with the positions in their context of
 each node. */
class NodeListWithPosition {
  Enumeration iterator() {
    return vector_.elements();
  }

  void removeAllElements() {
    vector_.removeAllElements();
    positions_.clear();
  }

  void add(String string) {
    vector_.addElement(string);
  }

  static private final Integer ONE = new Integer(1);
  static private final Integer TWO = new Integer(2);
  static private final Integer THREE = new Integer(3);
  static private final Integer FOUR = new Integer(4);
  static private final Integer FIVE = new Integer(5);
  static private final Integer SIX = new Integer(6);
  static private final Integer SEVEN = new Integer(7);
  static private final Integer EIGHT = new Integer(8);
  static private final Integer NINE = new Integer(9);
  static private final Integer TEN = new Integer(10);

  static private Integer identity(Node node) {
    return new Integer(System.identityHashCode(node));
  }

  void add(Node node, int position) {
    //Profiling shows thisto be the most heavily used method in Sparta so
    //optimize the crap out of it.
    vector_.addElement(node);
    //Avoid creating new integer objects for common cases
    Integer posn;
    switch (position) {
      case 1:
        posn = ONE;
        break;
      case 2:
        posn = TWO;
        break;
      case 3:
        posn = THREE;
        break;
      case 4:
        posn = FOUR;
        break;
      case 5:
        posn = FIVE;
        break;
      case 6:
        posn = SIX;
        break;
      case 7:
        posn = SEVEN;
        break;
      case 8:
        posn = EIGHT;
        break;
      case 9:
        posn = NINE;
        break;
      case 10:
        posn = TEN;
        break;
      default:
        posn = new Integer(position);
        break;
    }
    positions_.put(identity(node), posn);
  }

  int position(Node node) {
    return ((Integer) positions_.get(identity(node))).intValue();
  }

  public String toString() {
    try {

      StringBuffer y = new StringBuffer("{ ");
      for (Enumeration i = vector_.elements(); i.hasMoreElements();) {
        Object e = i.nextElement();
        if (e instanceof String)
          y.append("String(" + e + ") ");
        else {
          Node n = (Node) e;
          y.append("Node(" + n.toXml() + ")[" + positions_.get(identity(n)) + "] ");
        }
      }
      y.append("}");
      return y.toString();

    } catch (IOException e) {
      return e.toString();
    }
  }

  private final Vector vector_ = new Vector();

  private Hashtable positions_ = new Hashtable();
}

// $Log: XPathVisitor.java,v $
// Revision 1.9  2003/08/11 18:48:39  eobrain
// Fix bug in xpath  tag[n]  when identical elements.
//
// Revision 1.8  2003/07/17 23:58:40  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.7  2003/05/12 20:06:14  eobrain
// Performance improvements: optimize code in areas that profiling revealed to be bottlenecks.
//
// Revision 1.6  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.5  2003/01/09 01:10:50  yuhongx
// Use JDK1.1 API to make the code work with PersonalJava.
//
// Revision 1.4  2002/12/05 04:35:39  eobrain
// Add support for greater than and less than in predicates.
//
// Revision 1.3  2002/10/30 16:28:46  eobrain
// Feature request [ 630127 ] Support /a/b[text()='foo']
// http://sourceforge.net/projects/sparta-xml/
//
// Revision 1.2  2002/09/18 05:29:04  eobrain
// Support xpath predicates of the form [1], [2], ...
//
// Revision 1.1.1.1  2002/08/19 05:03:53  eobrain
// import from HP Labs internal CVS
//
// Revision 1.11  2002/08/18 04:47:47  eob
// Make class package-private so as not to clutter up the javadoc.
//
// Revision 1.10  2002/06/21 00:28:33  eob
// Make work with old JDK 1.1.*
//
// Revision 1.9  2002/06/14 19:34:50  eob
// Add handling of "text()" in XPath expressions.
//
// Revision 1.8  2002/06/04 05:29:28  eob
// Simplify use of visitor pattern to make code easier to understand.
// Fix bug when predicate in middle of XPath.
//
// Revision 1.7  2002/05/23 21:18:52  eob
// Better error reporting.
//
// Revision 1.6  2002/05/10 21:39:28  eob
// Allow documents to take relative xpaths.
//
// Revision 1.5  2002/03/26 01:49:53  eob
// Return different results depending on type.  Return first result.
//
// Revision 1.4  2002/03/21 23:54:36  eob
// Fix handling of .. when no parent.
//
// Revision 1.3  2002/02/14 02:21:45  eob
// Handle attribute XPaths.
//
// Revision 1.2  2002/02/04 22:09:44  eob
// add visit(AttrTest)
//
// Revision 1.1  2002/02/01 22:46:12  eob
// initial
