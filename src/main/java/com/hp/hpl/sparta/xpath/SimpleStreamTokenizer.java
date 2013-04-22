package com.hp.hpl.sparta.xpath;

import java.io.IOException;
import java.io.Reader;

/** Simplified replacement for java.util.StreamTokenizer which is not
 avilable in J2ME.
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
 @version  $Date: 2003/07/28 04:34:31 $  $Revision: 1.2 $
 @author Eamonn O'Brien-Strain
 */
public class SimpleStreamTokenizer {
  public static final int TT_EOF = -1;
  public static final int TT_NUMBER = -2;
  public static final int TT_WORD = -3;
  public int ttype = Integer.MIN_VALUE;
  public int nval = Integer.MIN_VALUE;
  public String sval = "";

  static private final int WHITESPACE = -5;
  static private final int QUOTE = -6;
  private final StringBuffer buf_ = new StringBuffer();
  private int nextType_;
  private final Reader reader_;
  private final int[] charType_ = new int[256];
  private boolean pushedBack_ = false;
  private char inQuote_ = 0;

  public String toString() {
    switch (ttype) {
      case TT_NUMBER:
        return Integer.toString(nval);
      case TT_WORD:
      case '\"':
        return "\"" + sval + "\"";
      case '\'':
        return "\'" + sval + "\'";
      case TT_EOF:
        return "(EOF)";
      default:
        return "\'" + (char) ttype + "\'";
    }
  }

  public SimpleStreamTokenizer(Reader reader) throws IOException {
    reader_ = reader;
    for (char ch = 0; ch < charType_.length; ++ch) {
      if ('A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z' || ch == '-')
        charType_[ch] = TT_WORD;
      else if ('0' <= ch && ch <= '9')
        charType_[ch] = TT_NUMBER;
      else if ('\u0000' <= ch && ch <= '\u0020')
        charType_[ch] = WHITESPACE;
      else
        charType_[ch] = ch;
    }
    nextToken();
  }

  /** precondition 0<=ch && ch<128 */
  public void ordinaryChar(char ch) {
    charType_[ch] = ch;
  }

  /** precondition 0<=ch && ch<128 */
  public void wordChars(char from, char to) {
    for (char ch = from; ch <= to; ++ch)
      charType_[ch] = TT_WORD;
  }

  public int nextToken() throws IOException {
    if (pushedBack_) {
      pushedBack_ = false;
      return ttype;
    }
    ttype = nextType_;
    while (true) {
      int ch;
      int currentType;
      boolean transition = false;
      boolean whitespace;
      do {
        ch = reader_.read();
        if (ch == -1) {
          if (inQuote_ != 0) throw new IOException("Unterminated quote");
          currentType = TT_EOF;
        } else
          currentType = charType_[ch];
        whitespace = inQuote_ == 0 && currentType == WHITESPACE;
        transition = transition || whitespace;
      } while (whitespace);

      if (currentType == '\'' || currentType == '\"') {
        if (inQuote_ == 0)
          inQuote_ = (char) currentType;
        else {
          if (inQuote_ == currentType) inQuote_ = 0;
        }
      }
      if (inQuote_ != 0) currentType = inQuote_;
      transition =
          transition || (ttype >= TT_EOF && ttype != '\'' && ttype != '\"') || ttype != currentType;
      if (transition) {
        //transition: we have a token to emit
        switch (ttype) {
          case TT_WORD:
            sval = buf_.toString();
            buf_.setLength(0);
            break;
          case '\'':
          case '\"':
            sval = buf_.toString().substring(1, buf_.length() - 1);
            buf_.setLength(0);
            break;
          case TT_NUMBER:
            nval = Integer.parseInt(buf_.toString());
            buf_.setLength(0);
            break;
          default:
            break;
        }
        if (currentType != WHITESPACE) nextType_ = currentType == QUOTE ? ch : currentType;
      }

      switch (currentType) {
        case TT_WORD:
        case TT_NUMBER:
        case '\'':
        case '\"':
          buf_.append((char) ch);
          break;
        default:
          break;
      }
      if (transition) return ttype;
    }
  }

  public void pushBack() {
    pushedBack_ = true;
  }
}
