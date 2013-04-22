package com.hp.hpl.sparta;

/** Thrown when error parsing XML or XPath.

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
 @version  $Date: 2002/08/19 05:03:58 $  $Revision: 1.1.1.1 $
 @author Eamonn O'Brien-Strain
 */
public class ParseException extends Exception {

  public ParseException(String msg) {
    super(msg);
  }

  /* For use by handlers */
  public ParseException(String msg, Throwable cause) {
    super(msg + " " + cause);
    this.cause_ = cause;
  }

  /* Using systemID */
  public ParseException(String systemId, int lineNumber, int lastCharRead, String history,
      String msg) {
    super(toMessage(systemId, lineNumber, lastCharRead, history, msg));
    lineNumber_ = lineNumber;
  }

  public ParseException(ParseLog log, String systemId, int lineNumber, int lastCharRead,
      String history, String msg) {
    this(systemId, lineNumber, lastCharRead, history, msg);
    log.error(msg, systemId, lineNumber);
  }


  public ParseException(ParseCharStream source, String msg) {
    this(source.getLog(), source.getSystemId(), source.getLineNumber(), source.getLastCharRead(),
        source.getHistory(), msg);
  }

  public ParseException(ParseCharStream source, char actual, char expected) {
    this(source, "got \'" + actual + "\' instead of expected \'" + expected + "\'");
  }

  /** Precondition: expected.length > 0 */
  public ParseException(ParseCharStream source, char actual, char[] expected) {
    this(source, "got \'" + actual + "\' instead of " + toString(expected));
  }

  public ParseException(ParseCharStream source, char actual, String expected) {
    this(source, "got \'" + actual + "\' instead of " + expected + " as expected");
  }

  public ParseException(ParseCharStream source, String actual, String expected) {
    this(source, "got \"" + actual + "\" instead of \"" + expected + "\" as expected");
  }

  static private String toString(char[] chars) {
    StringBuffer result = new StringBuffer();
    result.append(chars[0]);
    for (int i = 1; i < chars.length; ++i)
      result.append("or " + chars[i]);
    return result.toString();
  }

  public ParseException(ParseCharStream source, String actual, char[] expected) {
    this(source, actual, new String(expected));
  }

  public int getLineNumber() {
    return lineNumber_;
  }

  private int lineNumber_ = -1;

  public Throwable getCause() {
    return cause_;
  }

  //////////////////////////////////////////////////////////////////

  /*static private String toMessage(ParseCharStream source, String msg){
    return toMessage( source.getSystemId(),
    source.getLineNumber(),
    source.getLastCharRead(),
    source.getHistory(),
    msg );
    }*/

  static private String toMessage(String systemId, int lineNumber, int lastCharRead,
      String history, String msg) {
    return systemId + "(" + lineNumber + "): \n" + history + "\nLast character read was \'"
        + charRepr(lastCharRead) + "\'\n" + msg;
  }

  static String charRepr(int ch) {
    return (ch == -1) ? "EOF" : ("" + (char) ch);
  }

  private Throwable cause_ = null;

}

// $Log: ParseException.java,v $
// Revision 1.1.1.1  2002/08/19 05:03:58  eobrain
// import from HP Labs internal CVS
//
// Revision 1.13  2002/08/18 04:37:56  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.12  2002/08/09 22:36:49  sermarti
//
// Revision 1.11  2002/08/05 20:04:32  sermarti
//
// Revision 1.10  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.9  2002/05/23 21:29:32  eob
// Tweaks.
//
// Revision 1.8  2002/05/09 20:58:30  eob
// Add protected constructor to allow sub-classes without supplying all
// other info.
//
// Revision 1.7  2002/05/09 16:50:21  eob
// Add history for better error reporting.
//
// Revision 1.6  2002/01/08 19:51:02  eob
// Factored out constructors for more flexibilty.
//
// Revision 1.5  2002/01/04 00:38:40  eob
// Improve logging.
//
// Revision 1.4  2002/01/04 16:52:40  eob
// Comment change only.
//
// Revision 1.3  2002/01/04 16:52:10  eob
// Add constructors.
//
// Revision 1.2  2001/12/20 20:07:49  eob
// Added constructor that takes 2 strings.  Add getLineNumber method.
//
// Revision 1.1  2001/12/19 05:52:38  eob
// initial
