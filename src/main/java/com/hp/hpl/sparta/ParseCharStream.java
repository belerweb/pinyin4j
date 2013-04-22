package com.hp.hpl.sparta;

import java.io.*;
import java.util.*;

/** An XML character stream that has been parsed into a DOM tree.  This
 class encapsulates the Sparta XML parsing.

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
 @version  $Date: 2003/07/17 23:58:40 $  $Revision: 1.7 $
 @author Eamonn O'Brien-Strain
 @author Sergio Marti
 */
class ParseCharStream implements ParseSource {

  static private final boolean DEBUG = true;
  static private final boolean H_DEBUG = false;

  /** Constructor used when passing in XML stored in a string */
  public ParseCharStream(String systemId, char[] xmlData, ParseLog log, String encoding,
      ParseHandler handler) throws ParseException, EncodingMismatchException, IOException {
    this(systemId, null, xmlData, log, encoding, handler);
  }

  /** Constructor used when passing in XML from a character stream */
  public ParseCharStream(String systemId, Reader reader, ParseLog log, String encoding,
      ParseHandler handler) throws ParseException, EncodingMismatchException, IOException {
    this(systemId, reader, null, log, encoding, handler);
  }

  /** Parse XML document from characters stream according to W3C grammar.
   * [1]    document    ::=    prolog element Misc*
   *    @see <a href="http://www.w3.org/TR/2000/REC-xml-20001006">
   *      http://www.w3.org/TR/2000/REC-xml-20001006
   *    </a>
   */

  public ParseCharStream(String systemId, Reader reader, char[] xmlData, ParseLog log,
      String encoding, ParseHandler handler) throws ParseException, EncodingMismatchException,
      IOException {
    if (DEBUG) lineNumber_ = 1;
    if (H_DEBUG) {
      history_ = new CharCircBuffer(HISTORY_LENGTH);
      history_.addString("1:");
    } else
      history_ = null;

    log_ = (log == null) ? DEFAULT_LOG : log;
    encoding_ = encoding == null ? null : encoding.toLowerCase();

    //http://www.w3.org/TR/2000/REC-xml-20001006#sec-predefined-ent
    entities_.put("lt", "<");
    entities_.put("gt", ">");
    entities_.put("amp", "&");
    entities_.put("apos", "\'");
    entities_.put("quot", "\"");

    // Set input stream buffer. Either use string char array or
    // fill from character reader
    if (xmlData != null) {
      cbuf_ = xmlData;
      curPos_ = 0;
      endPos_ = cbuf_.length;
      eos_ = true;
      reader_ = null;
    } else {
      reader_ = reader;
      cbuf_ = new char[CBUF_SIZE];
      fillBuf();
    }

    systemId_ = systemId;

    // Set the ParseHandler for parsing
    handler_ = handler;
    handler_.setParseSource(this);

    /*
      try {
     */

    readProlog();

    handler_.startDocument();

    Element rootElement = readElement(/*null*/
    );

    if (docTypeName_ != null && !docTypeName_.equals(rootElement.getTagName()))
      log_.warning("DOCTYPE name \"" + docTypeName_ + "\" not same as tag name, \""
          + rootElement.getTagName() + "\" of root element", systemId_, getLineNumber());
    while (isMisc())
      readMisc();

    /*      
            } catch (ParseException e) {
            if (DEBUG)
            e.printStackTrace();
            throw e;
            } catch (IOException e) {
            if (DEBUG)
            e.printStackTrace();
            throw e;    
            }
     */

    if (reader_ != null) reader_.close();

    handler_.endDocument();
  }

  public String toString() {
    return systemId_;
  }

  public String getSystemId() {
    return systemId_;
  }

  /** Last line number read by parser. */
  public int getLineNumber() {
    return lineNumber_;
  }

  int getLastCharRead() {
    return ch_;
  }

  final String getHistory() {
    if (H_DEBUG)
      return history_.toString();
    else
      return "";
  }

  private int fillBuf() throws IOException {
    if (eos_) return -1;

    if (endPos_ == cbuf_.length) {
      //if (curPos_ != endPos_)
      //    throw new Error("Assertion failed in Sparta: curPos_ != (endPos_ == cbuf_.length)");
      curPos_ = endPos_ = 0;
    }

    int count = reader_.read(cbuf_, endPos_, cbuf_.length - endPos_);
    if (count <= 0) {
      eos_ = true;
      return -1;
    }
    endPos_ += count;
    return count;
  }

  private int fillBuf(int min) throws IOException {
    if (eos_) return -1;

    int count = 0;
    if (cbuf_.length - curPos_ < min) {
      for (int i = 0; curPos_ + i < endPos_; i++)
        cbuf_[i] = cbuf_[curPos_ + i];
      count = endPos_ - curPos_;
      endPos_ = count;
      curPos_ = 0;
    }
    int res = fillBuf();
    if (res == -1)
      if (count == 0)
        return -1;
      else
        return count;
    else
      return count + res;

  }

  /** [2]  Char ::=
      #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
   */
  private final char readChar() throws ParseException, IOException {
    //APPROXIMATION
    if (curPos_ >= endPos_)
      if (fillBuf() == -1) throw new ParseException(this, "unexpected end of expression.");
    if (DEBUG) if (cbuf_[curPos_] == '\n') lineNumber_++;
    if (H_DEBUG) {
      history_.addChar(cbuf_[curPos_]);
      if (cbuf_[curPos_] == '\n') {
        history_.addInt(lineNumber_);
        history_.addChar(':');
      }
    }

    return cbuf_[curPos_++];
  }

  private final char peekChar() throws ParseException, IOException {
    //APPROXIMATION
    if (curPos_ >= endPos_)
      if (fillBuf() == -1) throw new ParseException(this, "unexpected end of expression.");
    return cbuf_[curPos_];
  }

  private final void readChar(char expected) throws ParseException, IOException {
    final char ch = readChar();
    if (ch != expected) throw new ParseException(this, ch, expected);
  }

  private final boolean isChar(char expected) throws ParseException, IOException {
    if (curPos_ >= endPos_)
      if (fillBuf() == -1) throw new ParseException(this, "unexpected end of expression.");
    return (cbuf_[curPos_] == expected);
  }

  /*private final char readChar(char[] expected) throws ParseException, IOException{
      char ch = readChar();
      if( !isIn(ch,expected) )
          throw new ParseException(this,ch,expected);
      return ch;
  }*/

  private final char readChar(char expect0, char expect1) throws ParseException, IOException {
    final char ch = readChar();
    if (ch != expect0 && ch != expect1)
      throw new ParseException(this, ch, new char[] {expect0, expect1});
    return ch;
  }

  private final char readChar(char expect0, char expect1, char expect2, char expect3)
      throws ParseException, IOException {
    final char ch = readChar();
    if (ch != expect0 && ch != expect1 && ch != expect2 && ch != expect3)
      throw new ParseException(this, ch, new char[] {expect0, expect1, expect2, expect3});
    return ch;
  }

  private final boolean isChar(char expect0, char expect1) throws ParseException, IOException {
    if (curPos_ >= endPos_) if (fillBuf() == -1) return false;
    final char ch = cbuf_[curPos_];
    return ch == expect0 || ch == expect1;
  }

  private final boolean isChar(char expect0, char expect1, char expect2, char expect3)
      throws ParseException, IOException {
    if (curPos_ >= endPos_) if (fillBuf() == -1) return false;
    final char ch = cbuf_[curPos_];
    return ch == expect0 || ch == expect1 || ch == expect2 || ch == expect3;
  }

  /*private final boolean isChar(char[] expected) throws ParseException, IOException{
      if (curPos_ >= endPos_)
          if (fillBuf() == -1)
              return false;
      return isIn(cbuf_[curPos_],expected);
  }*/

  static private final boolean isIn(char ch, char[] expected) {
    for (int i = 0; i < expected.length; ++i)
      if (ch == expected[i]) return true;
    return false;
  }

  /** [3]    S    ::=    (#x20 | #x9 | #xD | #xA)+   */
  private final void readS() throws ParseException, IOException {
    readChar(' ', '\t', '\r', '\n');
    while (isChar(' ', '\t', '\r', '\n'))
      readChar();
  }

  private final boolean isS() throws ParseException, IOException {
    return isChar(' ', '\t', '\r', '\n');
  }

  static private final char[] NAME_PUNCT_CHARS = {'.', '-', '_', ':'};

  /** [4]  NameChar
      ::=  Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar | Extender
   */
  private boolean isNameChar() throws ParseException, IOException {
    char ch = peekChar();
    return (ch < MAX_COMMON_CHAR) ? IS_NAME_CHAR[ch] : isNameChar(ch);
  }

  static final private int MAX_COMMON_CHAR = 128;

  /**Avoid calculations for most common characters. */
  static final private boolean[] IS_NAME_CHAR = new boolean[MAX_COMMON_CHAR];
  static {
    for (char ch = 0; ch < MAX_COMMON_CHAR; ++ch)
      IS_NAME_CHAR[ch] = isNameChar(ch);
  }

  static private boolean isLetter(char ch) {
    return "abcdefghijklmnopqrstuvwxyz".indexOf(Character.toLowerCase(ch)) != -1;
  }

  private static boolean isNameChar(char ch) {
    //return Unicode.isUnicodeIdentifierPart(ch)
    //    || isIn(ch,NAME_PUNCT_CHARS)
    //    || Unicode.getType(ch)==Unicode.COMBINING_SPACING_MARK
    //    || isExtender(ch);
    return Character.isDigit(ch) || isLetter(ch) || isIn(ch, NAME_PUNCT_CHARS) || isExtender(ch);
  }

  /** [89]    Extender
      ::=    #x00B7 | #x02D0 | #x02D1 | #x0387 | #x0640 | #x0E46 | #x0EC6
      | #x3005 | [#x3031-#x3035] | [#x309D-#x309E] | [#x30FC-#x30FE]
   */
  static private boolean isExtender(char ch) {
    //verbose but efficient
    switch (ch) {
      case '\u00B7':
      case '\u02D0':
      case '\u02D1':
      case '\u0387':
      case '\u0640':
      case '\u0E46':
      case '\u0EC6':
      case '\u3005':
      case '\u3031':
      case '\u3032':
      case '\u3033':
      case '\u3034':
      case '\u3035':
      case '\u309D':
      case '\u309E':
      case '\u30FC':
      case '\u30FD':
      case '\u30FE':
        return true;
      default:
        return false;
    }
  }

  /** [5]    Name    ::=    (Letter | '_' | ':') (NameChar)*
   *  [84]    Letter    ::=    BaseChar | Ideographic
   */
  private final String readName() throws ParseException, IOException {
    StringBuffer result = null;
    int i = 0;
    tmpBuf_[i++] = readNameStartChar();
    while (isNameChar()) {
      if (i >= TMP_BUF_SIZE) {
        if (result == null) {
          result = new StringBuffer(i);
          result.append(tmpBuf_, 0, i);
        } else
          result.append(tmpBuf_, 0, i);
        i = 0;
      }
      tmpBuf_[i++] = readChar();
    }
    if (result == null)
      return Sparta.intern(new String(tmpBuf_, 0, i));
    //Interning because lots of repeats.  Slower but less memory.
    else {
      result.append(tmpBuf_, 0, i);
      return result.toString();
    }
  }

  private char readNameStartChar() throws ParseException, IOException {
    char ch = readChar();
    //if (!Unicode.isUnicodeIdentifierStart(ch) && ch != '_' && ch != ':')
    if (!isLetter(ch) && ch != '_' && ch != ':')
      throw new ParseException(this, ch, "letter, underscore, colon");
    return ch;
  }

  /** [9]    EntityValue ::=
   *          '"'
   *          (
   *           [^%&"] | PEReference | Reference
   *          )*
   *          '"'
   */
  private final String readEntityValue() throws ParseException, IOException {
    //grammar allows only double quote, but many xmlconf examples
    //use single quotes
    char quote = readChar('\'', '\"');
    StringBuffer result = new StringBuffer();
    while (!isChar(quote)) {
      if (isPeReference())
        result.append(readPeReference());
      else if (isReference())
        result.append(readReference());
      else
        result.append(readChar());
    }
    readChar(quote);
    return result.toString();
  }

  private final boolean isEntityValue() throws ParseException, IOException {
    return isChar('\'', '\"');
  }

  /** [11]    SystemLiteral    ::=    ('"' [^"]* '"') | ("'" [^']* "'")  */
  private final void readSystemLiteral() throws ParseException, IOException {
    char quote = readChar();
    while (peekChar() != quote)
      readChar();
    readChar(quote);
  }

  /** [12] PubidLiteral
      ::= '"' PubidChar* '"' | "'" (PubidChar - "'")* "'"
   */
  private final void readPubidLiteral() throws ParseException, IOException {
    //APPROXIMATION
    readSystemLiteral();
  }

  private boolean isMisc() throws ParseException, IOException {
    return isComment() || isPi() || isS();
  }

  private void readMisc() throws ParseException, IOException {
    if (isComment())
      readComment();
    else if (isPi())
      readPi();
    else if (isS())
      readS();
    else
      throw new ParseException(this, "expecting comment or processing instruction or space");
  }

  static private final char[] COMMENT_BEGIN = "<!--".toCharArray();
  static private final char[] COMMENT_END = "-->".toCharArray();

  /** [15]  Comment
      ::=  '<!--' ((Char - '-') | ('-' (Char - '-')))* '-->'
   */
  private final void readComment() throws ParseException, IOException {
    //This is actually less strict than the spec because it allows
    //embedded -- and comments ending with --->
    readSymbol(COMMENT_BEGIN);
    while (!isSymbol(COMMENT_END))
      readChar();
    readSymbol(COMMENT_END);
  }

  private final boolean isComment() throws ParseException, IOException {
    return isSymbol(COMMENT_BEGIN);
  }

  static private final char[] PI_BEGIN = "<?".toCharArray();
  static private final char[] QU_END = "?>".toCharArray();

  /** [16] PI  ::=  '<?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'  */
  private final void readPi() throws ParseException, IOException {
    //APPROXIMATION -- treat as comment
    readSymbol(PI_BEGIN);
    while (!isSymbol(QU_END))
      readChar();
    readSymbol(QU_END);
  }

  private final boolean isPi() throws ParseException, IOException {
    return isSymbol(PI_BEGIN);
  }

  /** www.w3.org/TR/2000/REC-xml-20001006#NT-prolog */
  private void readProlog() throws ParseException, EncodingMismatchException, IOException {
    if (isXmlDecl()) readXmlDecl();
    while (isMisc())
      readMisc();
    if (isDocTypeDecl()) {
      readDocTypeDecl();
      while (isMisc())
        readMisc();
    }
  }

  static private final char[] DOCTYPE_BEGIN = "<!DOCTYPE".toCharArray();

  private boolean isDocTypeDecl() throws ParseException, IOException {
    return isSymbol(DOCTYPE_BEGIN);
  }

  static private final char[] XML_BEGIN = "<?xml".toCharArray();

  /** [23] XMLDecl  ::=  '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>' */
  private void readXmlDecl() throws ParseException, EncodingMismatchException, IOException {
    readSymbol(XML_BEGIN);
    readVersionInfo();
    if (isS()) readS();
    if (isEncodingDecl()) {
      String encodingDeclared = readEncodingDecl();
      if (encoding_ != null && !encodingDeclared.toLowerCase().equals(encoding_))
        throw new EncodingMismatchException(systemId_, encodingDeclared, encoding_);
    }
    //APPROXIMATION:
    while (!isSymbol(QU_END))
      readChar();
    readSymbol(QU_END);
  }

  private boolean isXmlDecl() throws ParseException, IOException {
    return isSymbol(XML_BEGIN);
  }

  static private final char[] ENCODING = "encoding".toCharArray();

  private boolean isEncodingDecl() throws ParseException, IOException {
    return isSymbol(ENCODING);
  }

  /** [80] EncodingDecl ::= S 'encoding' Eq
      ('"' EncName '"' | "'" EncName "'" )
      [81] EncName      ::= [A-Za-z] ([A-Za-z0-9._] | '-')*
   */
  private String readEncodingDecl() throws ParseException, IOException {
    readSymbol(ENCODING);
    readEq();
    char quote = readChar('\'', '\"');
    StringBuffer result = new StringBuffer();
    while (!isChar(quote))
      result.append(readChar());
    readChar(quote);
    return result.toString();
  }

  static private final char[] VERSION = "version".toCharArray();

  /** [24]  VersionInfo
      ::=    S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
   */
  private void readVersionInfo() throws ParseException, IOException {
    readS();
    readSymbol(VERSION);
    readEq();
    //char quote = readChar(QUOTE_CHARS);
    char quote = readChar('\'', '\"');
    readVersionNum();
    readChar(quote);
  }

  /** [25]    Eq    ::=    S? '=' S?  */
  private final void readEq() throws ParseException, IOException {
    if (isS()) readS();
    readChar('=');
    if (isS()) readS();
  }

  static private final char[] VERSIONNUM_PUNC_CHARS = {'_', '.', ':', '-'};

  private boolean isVersionNumChar() throws ParseException, IOException {
    char ch = peekChar();
    return Character.isDigit(ch) || 'a' <= ch && ch <= 'z' || 'Z' <= ch && ch <= 'Z'
        || isIn(ch, VERSIONNUM_PUNC_CHARS);
  }

  /** [26]    VersionNum    ::=    ([a-zA-Z0-9_.:] | '-')+  */
  private void readVersionNum() throws ParseException, IOException {
    readChar();
    while (isVersionNumChar())
      readChar();
  }

  /** [28] doctypedecl ::=
   *      '<!DOCTYPE'
   *       S
   *       Name
   *(1)    ( S ExternalID )?
   *(2)    S?
   *(3)    (  '['   (markupdecl|DeclSep)*  ']'  S?  )?
   *       '>'
   */
  private void readDocTypeDecl() throws ParseException, IOException {
    readSymbol(DOCTYPE_BEGIN);
    readS();
    docTypeName_ = readName();
    if (isS()) {
      //either at (1) or (2)
      readS();
      if (!isChar('>') && !isChar('[')) {
        //was at (1)
        isExternalDtd_ = true; //less checking of entity references
        readExternalId();
        //now at (2)
        if (isS()) readS();
      }
    }
    //now at (3)
    if (isChar('[')) {
      readChar();
      while (!isChar(']')) {
        if (isDeclSep())
          readDeclSep();
        else
          readMarkupDecl();
      }
      readChar(']');
      if (isS()) readS();
    }
    readChar('>');
  }

  /** [28a]    DeclSep    ::=    PEReference | S  */
  private void readDeclSep() throws ParseException, IOException {
    if (isPeReference())
      readPeReference();
    else
      readS();
  }

  private boolean isDeclSep() throws ParseException, IOException {
    return isPeReference() || isS();
  }

  static private final char[] MARKUPDECL_BEGIN = "<!".toCharArray();

  /** [29]    markupdecl
      ::=  elementdecl|AttlistDecl|EntityDecl|NotationDecl|PI|Comment
   */
  private void readMarkupDecl() throws ParseException, IOException {
    if (isPi())
      readPi();
    else if (isComment())
      readComment();
    else if (isEntityDecl())
      readEntityDecl();
    else if (isSymbol(MARKUPDECL_BEGIN)) { // (element-|Attlist-|Entity-|Notation-)Decl
      while (!isChar('>')) {
        if (isChar('\'', '\"')) {
          char quote = readChar();
          while (!isChar(quote))
            readChar();
          readChar(quote);
        } else
          readChar();
      }
      readChar('>');
    } else
      throw new ParseException(this, "expecting processing instruction, comment, or \"<!\"");
  }

  static private final char[] CHARREF_BEGIN = "&#".toCharArray();

  /** [66]    CharRef    ::=    '&#'  [0-9]+       ';'
   */
  private char readCharRef() throws ParseException, IOException {
    readSymbol(CHARREF_BEGIN);
    int radix = 10;
    if (isChar('x')) {
      readChar();
      radix = 16;
    }
    int i = 0;
    while (!isChar(';')) {
      tmpBuf_[i++] = readChar();
      if (i >= TMP_BUF_SIZE) {
        log_.warning("Tmp buffer overflow on readCharRef", systemId_, getLineNumber());
        return ' ';
      }
    }
    readChar(';');
    String num = new String(tmpBuf_, 0, i);
    try {
      return (char) Integer.parseInt(num, radix);
    } catch (NumberFormatException e) {
      log_.warning("\"" + num + "\" is not a valid " + (radix == 16 ? "hexadecimal" : "decimal")
          + " number", systemId_, getLineNumber());
      return ' ';
    }
  }

  /** [67]    Reference  ::=    EntityRef | CharRef
   */
  private final char[] readReference() throws ParseException, IOException {
    if (isSymbol(CHARREF_BEGIN))
      return new char[] {readCharRef()};
    else
      return readEntityRef().toCharArray();
  }

  private final boolean isReference() throws ParseException, IOException {
    return isChar('&');
  }

  /** [68]    EntityRef  ::=    '&' Name ';'
   */
  private String readEntityRef() throws ParseException, IOException {
    readChar('&');
    String name = readName();
    String result = (String) entities_.get(name);
    //http://www.w3.org/TR/2000/REC-xml-20001006#vc-entdeclared
    if (result == null) {
      result = "";
      if (isExternalDtd_)
        log_.warning("&" + name + "; not found -- possibly defined in external DTD)", systemId_,
            getLineNumber());
      else
        log_.warning("No declaration of &" + name + ";", systemId_, getLineNumber());
    }
    readChar(';');
    return result;
  }

  /* Old methods
     private void appendText(Element element, String string) {
     handler_.characters(string);
     }
  
     private void appendText(Element element, char ch){
     handler_.character(ch);
     }
   */

  /**  [69]    PEReference    ::=    '%' Name ';'  */
  private String readPeReference() throws ParseException, IOException {
    readChar('%');
    String name = readName();
    String result = (String) pes_.get(name);
    //http://www.w3.org/TR/2000/REC-xml-20001006#vc-entdeclared
    if (result == null) {
      result = "";
      log_.warning("No declaration of %" + name + ";", systemId_, getLineNumber());
    }
    readChar(';');
    return result;
  }

  private boolean isPeReference() throws ParseException, IOException {
    return isChar('%');
  }

  static private final char[] ENTITY_BEGIN = "<!ENTITY".toCharArray();
  static private final char[] NDATA = "NDATA".toCharArray();

  /** [70]    EntityDecl    ::=    GEDecl | PEDecl
      [71]    GEDecl        ::=    '<!ENTITY' S Name S EntityDef S? '>'
      [72]    PEDecl        ::=    '<!ENTITY' S '%' S Name S PEDef S? '>'
      [73]    EntityDef     ::=    EntityValue | (ExternalID NDataDecl?)
      [74]    PEDef         ::=    EntityValue | ExternalID
      [76]    NDataDecl     ::=    S 'NDATA' S Name
   */
  private void readEntityDecl() throws ParseException, IOException {
    readSymbol(ENTITY_BEGIN);
    readS();
    if (isChar('%')) {
      readChar('%');
      readS();
      String name = readName();
      readS();
      String value;
      if (isEntityValue())
        value = readEntityValue();
      else
        value = readExternalId();
      pes_.put(name, value);
    } else {
      String name = readName();
      readS();
      String value;
      if (isEntityValue())
        value = readEntityValue();
      else if (isExternalId()) {
        value = readExternalId();
        if (isS()) readS();
        if (isSymbol(NDATA)) {
          readSymbol(NDATA);
          readS();
          readName();
        }
      } else
        throw new ParseException(this,
            "expecting double-quote, \"PUBLIC\" or \"SYSTEM\" while reading entity declaration");
      entities_.put(name, value);
    }
    if (isS()) readS();
    readChar('>');
  }

  private boolean isEntityDecl() throws ParseException, IOException {
    return isSymbol(ENTITY_BEGIN);
  }

  static private final char[] SYSTEM = "SYSTEM".toCharArray();
  static private final char[] PUBLIC = "PUBLIC".toCharArray();

  /** [75]    ExternalID    ::=
   *  'SYSTEM' S SystemLiteral
   *  | 'PUBLIC' S PubidLiteral S SystemLiteral
   */
  private String readExternalId() throws ParseException, IOException {
    if (isSymbol(SYSTEM))
      readSymbol(SYSTEM);
    else if (isSymbol(PUBLIC)) {
      readSymbol(PUBLIC);
      readS();
      readPubidLiteral();
    } else
      throw new ParseException(this, "expecting \"SYSTEM\" or \"PUBLIC\" while reading external ID");
    readS();
    readSystemLiteral();
    return "(WARNING: external ID not read)"; //not implemented
  }

  private boolean isExternalId() throws ParseException, IOException {
    return isSymbol(SYSTEM) || isSymbol(PUBLIC);
  }

  private final void readSymbol(char[] expected) throws ParseException, IOException {
    int n = expected.length;
    if (endPos_ - curPos_ < n) {
      if (fillBuf(n) <= 0) {
        ch_ = -1;
        throw new ParseException(this, "end of XML file", expected);
      }
    }
    ch_ = cbuf_[endPos_ - 1];

    if (endPos_ - curPos_ < n) throw new ParseException(this, "end of XML file", expected);

    //compare actual with expected
    for (int i = 0; i < n; ++i) {
      if (H_DEBUG) history_.addChar(cbuf_[curPos_ + i]);

      if (cbuf_[curPos_ + i] != expected[i])
        throw new ParseException(this, new String(cbuf_, curPos_, n), expected);
    }

    curPos_ += n;
  }

  private final boolean isSymbol(char[] expected) throws ParseException, IOException {
    final int n = expected.length;
    if (endPos_ - curPos_ < n) {
      if (fillBuf(n) <= 0) {
        ch_ = -1;
        return false;
      }
    }
    ch_ = cbuf_[endPos_ - 1];

    if (endPos_ - curPos_ < n) return false;

    //compare actual with expected
    //int startPos = curPos_;
    for (int i = 0; i < n; ++i)
      if (cbuf_[curPos_ + i] != expected[i]) return false;

    return true;
  }

  //////////////////////////////////////////////////////////////
  /** [10]    AttValue    ::=     '"' ([^<&"] | Reference)* '"'
   *                           |  "'" ([^<&'] | Reference)* "'"
   */
  private String readAttValue() throws ParseException, IOException {
    char quote = readChar('\'', '\"');
    StringBuffer result = new StringBuffer();
    while (!isChar(quote)) {
      if (isReference())
        result.append(readReference());
      else
        result.append(readChar());
    }
    readChar(quote);
    return result.toString();
  }

  static private final char[] BEGIN_CDATA = "<![CDATA[".toCharArray();
  static private final char[] END_CDATA = "]]>".toCharArray();

  /** [14]    CharData    ::=    [^<&]* - (
   *                                        [^<&]*
   *                                        ']]>'
   *                                        [^<&]*
   *                                        )
   */
  private void readPossibleCharData(/*Element element*/
  ) throws ParseException, IOException {
    int i = 0;
    while (!isChar('<') && !isChar('&') && !isSymbol(END_CDATA)) {

      tmpBuf_[i] = readChar();

      //convert DOS line endings to UNIX
      if (tmpBuf_[i] == '\r' && peekChar() == '\n') tmpBuf_[i] = readChar();

      i++;
      if (i == TMP_BUF_SIZE) {
        handler_.characters(tmpBuf_, 0, TMP_BUF_SIZE);
        i = 0;
      }
    }
    if (i > 0) handler_.characters(tmpBuf_, 0, i);
  }

  /**
   * [18]    CDSect   ::=    CDStart CData CDEnd
   * [19]    CDStart  ::=    '<![CDATA['
   * [20]    CData    ::=    (Char* - (Char* ']]>' Char*))
   * [21]    CDEnd    ::=    ']]>'
   */
  private void readCdSect(/*Element element*/
  ) throws ParseException, IOException {
    StringBuffer result = null;
    readSymbol(BEGIN_CDATA);
    int i = 0;
    while (!isSymbol(END_CDATA)) {
      if (i >= TMP_BUF_SIZE) {
        if (result == null) {
          result = new StringBuffer(i);
          result.append(tmpBuf_, 0, i);
        } else
          result.append(tmpBuf_, 0, i);
        i = 0;
      }
      tmpBuf_[i++] = readChar();
    }
    readSymbol(END_CDATA);

    if (result != null) {
      result.append(tmpBuf_, 0, i);
      char[] cdSect = result.toString().toCharArray();
      handler_.characters(cdSect, 0, cdSect.length);
    } else {
      handler_.characters(tmpBuf_, 0, i);
    }

    /* Old style
       StringBuffer buf = new StringBuffer();
       readSymbol(BEGIN_CDATA);
       while( !isSymbol(END_CDATA) )
       buf.append( readChar() );
       readSymbol(END_CDATA);
       if( buf.length() > 0 ) {
       char cdSect[] = buf.toString().toCharArray();
       handler_.characters(cdSect, 0, cdSect.length);
       }
     */
  }

  private boolean isCdSect() throws ParseException, IOException {
    return isSymbol(BEGIN_CDATA);
  }

  /** Parse element using stream in document.
   *  [39]    element    ::=    EmptyElemTag
   *                          | STag content ETag
   */
  private final Element readElement(/*Element parentElement*/
  ) throws ParseException, IOException {
    final Element element = new Element();

    final boolean isSTag = readEmptyElementTagOrSTag(element);

    handler_.startElement(element);

    if (isSTag) {
      readContent(/*element*/
      );
      readETag(element);
    }

    handler_.endElement(element);

    //element.normalize();
    return element;
  }

  ParseLog getLog() {
    return log_;
  }

  static private final char[] END_EMPTYTAG = "/>".toCharArray();

  /** Return if this is a STag
   *  [40]    STag         ::=    '<' Name (S Attribute)* S? '>'
   *  [44]    EmptyElemTag ::=    '<' Name (S Attribute)* S? '/>'
   */
  private boolean readEmptyElementTagOrSTag(Element element) throws ParseException, IOException {
    readChar('<');
    element.setTagName(readName());
    while (isS()) {
      readS();
      if (!isChar('/', '>')) readAttribute(element);
    }
    if (isS()) readS();
    boolean isSTag = isChar('>');
    if (isSTag)
      readChar('>');
    else
      readSymbol(END_EMPTYTAG);
    return isSTag;

  }

  /** [41]    Attribute    ::=    Name Eq AttValue  */
  private void readAttribute(Element element) throws ParseException, IOException {
    String name = readName();
    readEq();
    String value = readAttValue();
    //http://www.w3.org/TR/2000/REC-xml-20001006#uniqattspec
    if (element.getAttribute(name) != null)
      log_.warning("Element " + this + " contains attribute " + name + "more than once", systemId_,
          getLineNumber());
    element.setAttribute(name, value);
  }

  static private final char[] BEGIN_ETAG = "</".toCharArray();

  /** [42] ETag    ::=    '</' Name S? '>' */
  private void readETag(Element element) throws ParseException, IOException {
    readSymbol(BEGIN_ETAG);
    String name = readName();
    //http://www.w3.org/TR/2000/REC-xml-20001006#GIMatch
    if (!name.equals(element.getTagName()))
      log_.warning(
          "end tag (" + name + ") does not match begin tag (" + element.getTagName() + ")",
          systemId_, getLineNumber());
    if (isS()) readS();
    readChar('>');
  }

  private boolean isETag() throws ParseException, IOException {
    return isSymbol(BEGIN_ETAG);
  }

  /** [43]    content    ::=
   *    CharData? (
   *        (element | Reference | CDSect | PI | Comment) CharData?
   *    )*
   */
  private void readContent(/*Element element*/
  ) throws ParseException, IOException {
    readPossibleCharData(/*element*/
    );
    boolean keepGoing = true;
    while (keepGoing) {
      if (isETag())
        keepGoing = false;
      else if (isReference()) {
        // appendText( element, readReference() );
        char ref[] = readReference();
        handler_.characters(ref, 0, ref.length);
      } else if (isCdSect())
        readCdSect(
        /*element*/
        );
      else if (isPi())
        readPi();
      else if (isComment())
        readComment();
      else if (isChar('<'))
        readElement(
        /*element*/
        );
      else
        keepGoing = false;
      readPossibleCharData(/*element*/
      );
    }

  }

  //////////////////////////////////////////////////////////////

  /**
   * @link aggregationByValue
   */
  private String systemId_; // Temp not final
  private String docTypeName_ = null;

  /**
   * @link aggregationByValue
   */
  private final Reader reader_;
  //private final char[]           buf_           = new char[LOOKAHEAD];
  private final Hashtable entities_ = new Hashtable();
  private final Hashtable pes_ = new Hashtable();
  private final ParseLog log_;
  private final String encoding_;
  private int ch_ = -2; //last char read
  private boolean isExternalDtd_ = false;

  //static private final int LOOKAHEAD = 9;

  /**
   * Added by Sergio Marti.
   */

  /** Replaced PeekReader with character array.  10X speed improvement */
  private final int CBUF_SIZE = 1024;
  private final char[] cbuf_;
  private int curPos_ = 0;
  private int endPos_ = 0;
  private boolean eos_ = false; // End of stream identifier

  // Empty char buffer used to fill with char data 
  static private final int TMP_BUF_SIZE = 255;
  private final char tmpBuf_[] = new char[TMP_BUF_SIZE];

  // Debug information
  private int lineNumber_ = -1;
  private final CharCircBuffer history_;
  static public final int HISTORY_LENGTH = 100;

  // SAX Parser like handler. 
  private final ParseHandler handler_;

}

// $Log: ParseCharStream.java,v $
// Revision 1.7  2003/07/17 23:58:40  eobrain
// Make compatiblie with J2ME.  For example do not use "new"
// java.util classes.
//
// Revision 1.6  2003/05/12 20:04:47  eobrain
// Performance improvements including some interning.
//
// Revision 1.5  2003/03/21 00:22:23  eobrain
// Lots of little performance optimizations.
//
// Revision 1.4  2003/01/27 23:30:58  yuhongx
// Replaced Hashtable with HashMap.
//
// Revision 1.3  2002/11/06 02:57:59  eobrain
// Organize imputs to removed unused imports.  Remove some unused local variables.
//
// Revision 1.2  2002/08/21 20:18:12  eobrain
// Ignore case when comparing encodings.
//
// Revision 1.1.1.1  2002/08/19 05:04:00  eobrain
// import from HP Labs internal CVS
//
// Revision 1.17  2002/08/18 04:36:59  eob
// Make interface package-private so as not to clutter up the javadoc.
//
// Revision 1.16  2002/08/17 00:54:14  sermarti
//
// Revision 1.15  2002/08/15 23:40:22  sermarti
//
// Revision 1.14  2002/08/05 20:04:32  sermarti
//
// Revision 1.13  2002/08/01 23:36:52  sermarti
// Sparta minor update: Now with debug really enabled.
//
// Revision 1.12  2002/08/01 23:29:17  sermarti
// Much faster Sparta parsing.
// Has debug features enabled by default. Currently toggled
// in ParseCharStream.java and recompiled.
//
// Revision 1.11  2002/07/25 21:10:15  sermarti
// Adding files that mysteriously weren't added from Sparta before.
//
// Revision 1.10  2002/05/23 21:28:25  eob
// Make misc optimizations because performance profiling showed that this
// class is heavily used.  Avoid use char arrays instead of strings in
// symbol comparison.   Remove deprecated methods.
//
// Revision 1.9  2002/05/09 16:50:06  eob
// Add history for better error reporting.
//
// Revision 1.8  2002/03/21 23:52:21  eob
// Deprecate functionality moved to Parser facade class.
//
// Revision 1.7  2002/02/23 02:06:51  eob
// Add constructor that takes a File.
//
// Revision 1.6  2002/02/06 23:32:40  eob
// Better error message.
//
// Revision 1.5  2002/02/01 21:56:23  eob
// Tweak error messages.  Add no-log constructor.
//
// Revision 1.4  2002/01/09 00:53:02  eob
// Formatting changes only.
//
// Revision 1.3  2002/01/09 00:48:24  eob
// Replace well-formed errors with warnings.
//
// Revision 1.2  2002/01/08 19:56:51  eob
// Comment change only.
//
// Revision 1.1  2002/01/08 19:29:31  eob
// Factored out ParseSource functionality into ParseCharStream and
// ParseByteStream.
