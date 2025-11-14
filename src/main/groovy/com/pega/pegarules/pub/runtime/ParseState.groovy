package com.pega.pegarules.pub.runtime

import java.io.InputStream
import java.io.Reader

interface ParseState {
    String VERSION = "8.4.0"

    String getCharacterEncoding()
    InputStream getInputStream()
    String getLastToken()
    Reader getReader()
    long getStreamOffset()
    boolean isByteStream()
    boolean isCharacterStream()
    boolean isEndOfStream()
    byte[] parseBytesRaw(long offset, int length)
    String parseBytesText(long offset, int length)
    String parseCharsText(long offset, int length)
    String parseFixedBinary(int byteCount, boolean littleEndian)
    String parsePackedDecimal(int digits, int decimals)
    void setCharacterEncoding(String value)
    void setLastToken(String value)
    long skipBytes(long count)
    long skipCharacters(long count)
}
