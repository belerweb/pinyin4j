package com.hp.hpl.sparta;

import java.io.*;


/**
 * An XML byte stream that has been parsed into a DOM tree.
 * Just like ParseCharStream except handle Unicode encoding of byte stream.
 * Use rules in
 * http://www.w3.org/TR/2000/REC-xml-20001006#sec-guessing to guess
 * encoding -- if encoding declaration is different, restart parsing.

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
 @version  $Date: 2003/07/28 04:33:04 $  $Revision: 1.5 $
 @author Eamonn O'Brien-Strain
 */

class ParseByteStream implements ParseSource {

  /** Parse XML document from byte stream, converting to Unicode
   *  characters as specifed by the initial byte-order-mark.
   *  @param istream is the source of bytes and must support mark so that
   *  we can peek ahead at its first two bytes
   */
  public ParseByteStream(String systemId, InputStream istream, ParseLog log,
      String guessedEncoding, ParseHandler handler) throws ParseException, IOException {
    if (log == null) log = DEFAULT_LOG;

    //We need to be able to restart the stream if the declared encoding
    //is different than our guess, os buffer if necessary.  We also need
    //to be able to peek ahead at the first 4 bytes
    if (!istream.markSupported())
      throw new Error(
          "Precondition violation: the InputStream passed to ParseByteStream must support mark");
    istream.mark(MAXLOOKAHEAD); //mark at begining

    byte[] start = new byte[4];
    int n = istream.read(start);

    if (guessedEncoding == null) guessedEncoding = guessEncoding(systemId, start, n, log);

    try {

      //First try with guessed encoding
      istream.reset();
      InputStreamReader reader = new InputStreamReader(istream, fixEncoding(guessedEncoding));
      try {

        parseSource_ = new ParseCharStream(systemId, reader, log, guessedEncoding, handler);
        //}catch( CharConversionException e ){
      } catch (IOException e) {

        //This exception seems to be caused by reading euc-jp as utf-8
        String secondGuessEncoding = "euc-jp";
        log.note("Problem reading with assumed encoding of " + guessedEncoding
            + " so restarting with " + secondGuessEncoding, systemId, 1);
        istream.reset();
        try {
          reader = new InputStreamReader(istream, fixEncoding(secondGuessEncoding));
        } catch (UnsupportedEncodingException ee) {
          throw new ParseException(log, systemId, 1, '\0', secondGuessEncoding, "\""
              + secondGuessEncoding + "\" is not a supported encoding");
        }

        parseSource_ = new ParseCharStream(systemId, reader, log, null, handler);
      }
    } catch (EncodingMismatchException e) {
      //if that didn't work try declared encoding
      String declaredEncoding = e.getDeclaredEncoding();
      log.note("Encoding declaration of " + declaredEncoding + " is different that assumed "
          + guessedEncoding + " so restarting the parsing with the new encoding", systemId, 1);
      istream.reset();
      InputStreamReader reader;
      try {
        reader = new InputStreamReader(istream, fixEncoding(declaredEncoding));
      } catch (UnsupportedEncodingException ee) {
        throw new ParseException(log, systemId, 1, '\0', declaredEncoding, "\"" + declaredEncoding
            + "\" is not a supported encoding");
      }
      parseSource_ = new ParseCharStream(systemId, reader, log, null, handler);
    }
  }

  public String toString() {
    return parseSource_.toString();
  }

  public String getSystemId() {
    return parseSource_.getSystemId();
  }

  /** Last line number read by parser. */
  public int getLineNumber() {
    return parseSource_.getLineNumber();
  }

  /**
   * @link aggregationByValue
   */
  private ParseCharStream parseSource_;

  /////////////////////////////////////////////////////////////////////

  /** Convert byte stream to Unicode character stream according to
   *  http://www.w3.org/TR/2000/REC-xml-20001006#sec-guessing
   *  . */
  static private String guessEncoding(String systemId, byte[] start, int n, ParseLog log)
      throws IOException {
    //Test for UTF-16 byte-order mark
    String encoding;
    if (n != 4) {
      String msg =
          n <= 0 ? "no characters in input" : "less than 4 characters in input: \""
              + new String(start, 0, n) + "\"";
      log.error(msg, systemId, 1);
      encoding = "UTF-8";
    } else if (equals(start, 0x0000FEFF) || equals(start, 0xFFFE0000) || equals(start, 0x0000FFFE)
        || equals(start, 0xFEFF0000) || equals(start, 0x0000003C) || equals(start, 0x3C000000)
        || equals(start, 0x00003C00) || equals(start, 0x003C0000))
      encoding = "UCS-4";
    else if (equals(start, 0x003C003F))
      encoding = "UTF-16BE"; //or ISO-10646-UCS-2
    else if (equals(start, 0x3C003F00))
      encoding = "UTF-16LE"; //or ISO-10646-UCS-2
    else if (equals(start, 0x3C3F786D))
      encoding = "UTF-8";//or ISO 646, ASCII, ISO 8859, Shift-JIS, EUC
    else if (equals(start, 0x4C6FA794))
      encoding = "EBCDIC";
    else if (equals(start, (short) 0xFFFE) || equals(start, (short) 0xFEFF))
      encoding = "UTF-16";
    else
      encoding = "UTF-8";

    if (!encoding.equals("UTF-8"))
      log.note("From start " + hex(start[0]) + " " + hex(start[1]) + " " + hex(start[2]) + " "
          + hex(start[3]) + " deduced encoding = " + encoding, systemId, 1);
    return encoding;
  }

  static private String hex(byte b) {
    String s = Integer.toHexString(b);
    switch (s.length()) {
      case 1:
        return "0" + s;
      case 2:
        return s;
      default:
        return s.substring(s.length() - 2);
    }
  }

  static private boolean equals(byte[] bytes, int integer) {
    return bytes[0] == (byte) ((integer >>> 24)) && bytes[1] == (byte) ((integer >>> 16) & 0xFF)
        && bytes[2] == (byte) ((integer >>> 8) & 0xFF) && bytes[3] == (byte) ((integer) & 0xFF);
  }

  static private boolean equals(byte[] bytes, short integer) {
    return bytes[0] == (byte) ((integer >>> 8)) && bytes[1] == (byte) ((integer) & 0xFF);
  }

  static private String fixEncoding(String encoding) {
    return encoding.toLowerCase().equals("utf8") ? "UTF-8" : encoding;
  }

}


// $Log: ParseByteStream.java,v $
// Revision 1.5  2003/07/28 04:33:04  eobrain
// Fix bug that was removing dashes from unicode encoding names.  We
// should do this only for UTF-8.
//
// Revision 1.4  2003/07/17 23:55:28  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.3  2003/01/09 01:05:38  yuhongx
// added FixEncoding().
//
// Revision 1.2  2002/11/06 02:57:59  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.1.1.1  2002/08/19 05:04:00  eobrain
// import from HP Labs internal CVS
//
// Revision 1.14  2002/08/18 04:36:25  eob
// Make interface package-private so as not to clutter up the javadoc.
//
// Revision 1.13  2002/08/17 00:54:14  sermarti
//
// Revision 1.12  2002/08/05 20:04:32  sermarti
//
// Revision 1.11  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.10  2002/05/23 22:00:19  eob
// Add better error handling.
//
// Revision 1.9  2002/05/09 17:02:26  eob
// Fix NullPointerException in error reporting.
//
// Revision 1.8  2002/05/09 16:49:52  eob
// Add history for better error reporting.
//
// Revision 1.7  2002/03/21 23:50:49  eob
// Deprecate functionality moved to Parser facade class.
//
// Revision 1.6  2002/02/15 21:30:38  eob
// Comment changes only.
//
// Revision 1.5  2002/02/01 21:55:15  eob
// Comment change only.
//
// Revision 1.4  2002/01/09 00:45:58  eob
// Formatting change only.
//
// Revision 1.3  2002/01/09 00:44:57  eob
// Handle CharConversionException caused by reading euc-jp characters
// before encoding has been established.  Restart parsing.
//
// Revision 1.2  2002/01/08 19:53:43  eob
// Comment change only.
//
// Revision 1.1  2002/01/08 19:31:33  eob
// Factored out ParseSource functionality into ParseCharStream and
// ParseByteStream.
