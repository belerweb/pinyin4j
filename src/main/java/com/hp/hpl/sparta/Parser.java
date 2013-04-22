package com.hp.hpl.sparta;

import java.io.*;

/**
 * Provides a simple interface to the XML parser.
 * This is an example of the Facade design pattern.
 * 
 * There are two types of parse methods: DOM methods which parse into a DOM tree
 * and SAX methods in which the parser calls a client-provided hander.  The XML can be proved
 * either as characters (in a String, character array, or Reader) or as bytes 
 * (in a File, byte array, or InputStream).  In the latter case the Unicode encoding is
 * first guessed by looking at the first few characters and then possibly confirmed
 * by reading an encoding declaration on the first line of the XML.  In some cases the declared
 * encoding may be different to the guessed encoding and the parser will have to re-start reading
 * the bytes.

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
 @version  $Date: 2003/11/01 05:40:15 $  $Revision: 1.5 $
 @author Eamonn O'Brien-Strain
 @author Sergio Marti
 */

public class Parser {

  /* DOM parse calls */

  /** DOM parsing of XML in a character stream, using a default log. **/
  static public Document parse(String systemId, Reader reader) throws ParseException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseCharStream(systemId, reader, null, null, bd);
    return bd.getDocument();
  }

  /** DOM parsing of XML in a character stream. **/
  static public Document parse(String systemId, Reader reader, ParseLog log) throws ParseException,
      IOException {
    BuildDocument bd = new BuildDocument();
    new ParseCharStream(systemId, reader, log, null, bd);
    return bd.getDocument();
  }

  /** DOM parsing of XML in a String. **/
  static public Document parse(String xml) throws ParseException, IOException {
    return parse(xml.toCharArray());
  }

  /** DOM parsing of XML in a character array (this is the fastest parse method). **/
  static public Document parse(char[] xml) throws ParseException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseCharStream("file:anonymous-string", xml, null, null, bd);
    return bd.getDocument();
  }

  /**
   * Parse XML to DOM, figuring out the encoding using the first few characters and possibly
   * an encoding declaration on the first line of the XML.
   * @param xml stored in an array of bytes in some Unicode encoding.
   * @return the DOM Document resulting from the parsing
   * @throws ParseException on parse error
   */
  static public Document parse(byte[] xml) throws ParseException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(xml), null, null, bd);
    return bd.getDocument();

  }

  /** DOM parsing of XML in a character stream, specifying the Unicode encoding. */
  static public Document parse(String systemId, Reader reader, ParseLog log, String encoding)
      throws ParseException, EncodingMismatchException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseCharStream(systemId, reader, log, encoding, bd);
    return bd.getDocument();
  }

  /** DOM parsing of XML encoded in a byte stream. */
  static public Document parse(String systemId, InputStream istream, ParseLog log)
      throws ParseException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseByteStream(systemId, istream, log, null, bd);
    return bd.getDocument();
  }

  /** DOM parsing of XML encoded in a byte stream, using a default log. */
  static public Document parse(String systemId, InputStream istream) throws ParseException,
      IOException {
    BuildDocument bd = new BuildDocument();
    new ParseByteStream(systemId, istream, null, null, bd);
    return bd.getDocument();
  }

  /** DOM parsing of XML encoded in a character stream, specifying the Unicode encoding. */
  static public Document parse(String systemId, InputStream istream, ParseLog log,
      String guessedEncoding) throws ParseException, IOException {
    BuildDocument bd = new BuildDocument();
    new ParseByteStream(systemId, istream, log, guessedEncoding, bd);
    return bd.getDocument();
  }

  /* SAX parse calls. Must give own ParseHandler */

  /** SAX parsing of XML in character stream, using default log. */
  static public void parse(String systemId, Reader reader, ParseHandler ph) throws ParseException,
      IOException {
    new ParseCharStream(systemId, reader, null, null, ph);
  }

  /** SAX parsing of XML in character stream. */
  static public void parse(String systemId, Reader reader, ParseLog log, ParseHandler ph)
      throws ParseException, IOException {
    new ParseCharStream(systemId, reader, log, null, ph);
  }

  /** SAX parsing of XML in a string. */
  static public void parse(String xml, ParseHandler ph) throws ParseException, IOException {
    parse(xml.toCharArray(), ph);
  }

  /** SAX parsing of XML in a character array. */
  static public void parse(char[] xml, ParseHandler ph) throws ParseException, IOException {
    new ParseCharStream("file:anonymous-string", xml, null, null, ph);
  }

  /** SAX parsing of XML encoded in a byte array. */
  static public void parse(byte[] xml, ParseHandler ph) throws ParseException, IOException {
    new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(xml), null, null, ph);
  }

  /** SAX parsing of XML encoded in a byte stream, using default log. */
  static public void parse(String systemId, InputStream istream, ParseLog log, ParseHandler ph)
      throws ParseException, IOException {
    new ParseByteStream(systemId, istream, log, null, ph);
  }

  /** SAX parsing of XML encoded in a byte stream. */
  static public void parse(String systemId, InputStream istream, ParseHandler ph)
      throws ParseException, IOException {
    new ParseByteStream(systemId, istream, null, null, ph);
  }

  /** SAX parsing of XML encoded in a byte stream, specifying the Unicode encoding. */
  static public void parse(String systemId, InputStream istream, ParseLog log,
      String guessedEncoding, ParseHandler ph) throws ParseException, IOException {
    new ParseByteStream(systemId, istream, log, guessedEncoding, ph);
  }

  /** SAX parsing of XML encoded in a character stream, specifying the Unicode encoding. */
  static public void parse(String systemId, Reader reader, ParseLog log, String encoding,
      ParseHandler ph) throws ParseException, EncodingMismatchException, IOException {
    new ParseCharStream(systemId, reader, log, encoding, ph);
  }


}

// $Log: Parser.java,v $
// Revision 1.5  2003/11/01 05:40:15  eobrain
// Reformatted.
//
// Revision 1.4  2003/07/17 23:58:40  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.3  2003/03/21 00:22:53  eobrain
// Use CharArrayReader instead of StringReader.
//
// Revision 1.2  2002/12/13 23:09:24  eobrain
// Fix javadoc.
//
// Revision 1.1.1.1  2002/08/19 05:03:57  eobrain
// import from HP Labs internal CVS
//
// Revision 1.8  2002/08/18 04:42:25  eob
// Add copyright and other formatting and commenting in preparation for
// release to SourceForge.
//
// Revision 1.7  2002/08/17 00:54:14  sermarti
//
// Revision 1.6  2002/08/15 23:40:22  sermarti
//
// Revision 1.5  2002/08/05 20:04:32  sermarti
//
// Revision 1.4  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.3  2002/07/11 21:06:41  eob
// Add parse(String) and parse(byte[])
//
// Revision 1.2  2002/06/13 19:14:44  eob
// Add a convenience constructor.
//
// Revision 1.1  2002/03/21 23:48:54  eob
// initial
