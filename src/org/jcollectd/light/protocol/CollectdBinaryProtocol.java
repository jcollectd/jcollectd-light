/*
 * Copyright (c) 2012. Andrus Viik and other contributors
 * http://jcollectd.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jcollectd.light.protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A simple collection of methods implementing writing different parts of Collectd Binary Protocol
 *
 * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol">Collectd Binary Protocol</a>
 */
public class CollectdBinaryProtocol {

    static final short UINT8_LEN = 1;
    static final short UINT16_LEN = UINT8_LEN * 2;
    static final short UINT32_LEN = UINT16_LEN * 2;
    static final short UINT64_LEN = UINT32_LEN * 2;
    static final short HEADER_LEN = UINT16_LEN * 2;

    /**
     * Puts a value type header byte to buffer
     *
     * @param buffer ByteBuffer to write data to
     * @param typeId identifier of a value type
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Value_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void header(ByteBuffer buffer, byte typeId) {
        buffer.put(typeId); //write typeId
    }

    /**
     * Puts a part type header bits and length to ByteBuffer
     *
     * @param buffer ByteBuffer to write data to
     * @param partId identifier of a part type
     * @param length total bytes to be read for this part
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Protocol_structure">Collectd Binary Protocol - Protocol structure</a>
     */
    public static void header(ByteBuffer buffer, short partId, short length) {
        buffer.putShort(partId); //write partId
        buffer.putShort(length); //write part length
    }

    /**
     * Puts a part type header bits, length and number of values to ByteBuffer
     *
     * @param buffer      ByteBuffer to write data to
     * @param partId      identifier of a part type
     * @param length      total bytes to be read for this part
     * @param valuesCount total number of values to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Value_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void header(ByteBuffer buffer, short partId, short length, short valuesCount) {
        header(buffer, partId, length);
        buffer.putShort(valuesCount);
    }

    /**
     * Puts a numeric bytes to buffer, along with header bytes.
     *
     * @param buffer ByteBuffer to write data to
     * @param partId identifier of a part type
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Numeric_parts">Collectd Binary Protocol - Numeric parts</a>
     */
    public static void numeric(ByteBuffer buffer, short partId, long value) {
        header(buffer, partId, (short) (HEADER_LEN + UINT64_LEN)); //write header
        buffer.putLong(value); //write long
    }

    /**
     * Puts a string part bytes to buffer, along with header bytes.
     *
     * @param buffer ByteBuffer to write data to
     * @param partId identifier of a part type
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#String_parts">Collectd Binary Protocol - String parts</a>
     */
    public static void string(ByteBuffer buffer, short partId, String value) {
        if (value == null) value = "";
        header(buffer, partId, (short) (HEADER_LEN + value.length() + 1)); //write header
        buffer.put(value.getBytes()).put((byte) '\0'); //write string
    }

    /**
     * Puts a value part bytes to buffer, along with header bytes.
     *
     * @param buffer ByteBuffer to write data to
     * @param partId identifier of a part type
     * @param types  array of typeId bytes
     * @param values values to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Values_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void values(ByteBuffer buffer, short partId, byte[] types, byte[] values) {
        header(buffer, partId, (short) (HEADER_LEN + UINT16_LEN + ((UINT8_LEN + UINT64_LEN) * types.length)), (short) types.length);
        buffer.put(types);
        buffer.put(values);
    }

    /* ENC/SIGN*/
    public static void sign(ByteBuffer buffer, short partId, String username, byte[] hmac) {
        header(buffer, partId, (short) (HEADER_LEN + username.length() + hmac.length)); //write header
        buffer.put(hmac); //write signature
        buffer.put(username.getBytes()); //write username
    }

    public static void encrypt(ByteBuffer buffer, short partId, String username, byte[] iv, byte[] bytes) {
        header(buffer, partId, (short) (HEADER_LEN + username.length() + UINT16_LEN + iv.length + bytes.length));  //write header
        buffer.putShort((short) username.length()).put(username.getBytes()); //write user length and user
        buffer.put(iv); // write init vector
        buffer.put(bytes); // write payload
    }

    /**
     * Puts a long value type bytes to buffer.
     *
     * @param buffer ByteBuffer to write data to
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Values_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void value(ByteBuffer buffer, long value) {
        buffer.putLong(value); //write long
    }

    /**
     * Puts a long value type bytes to buffer with header, to use when one needs to write single value only.
     *
     * @param buffer ByteBuffer to write data to
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Values_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void value(ByteBuffer buffer, byte typeId, long value) {
        header(buffer, typeId); //write header
        value(buffer, value); //write long
    }

    /**
     * Puts a decimal value type bytes to buffer.
     *
     * @param buffer ByteBuffer to write data to
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Values_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void value(ByteBuffer buffer, double value) {
        ByteOrder order = buffer.order(); //get buffer's order
        buffer.order(ByteOrder.LITTLE_ENDIAN).putDouble(value); //switch to LITTLE_ENDIAN, write double
        buffer.order(order); //switch to original byteorder
    }

    /**
     * Puts a decimal value type bytes to buffer with header, to use when one needs to write single value only.
     *
     * @param buffer ByteBuffer to write data to
     * @param value  value to write
     * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol#Values_parts">Collectd Binary Protocol - Value parts</a>
     */
    public static void value(ByteBuffer buffer, byte typeId, double value) {
        header(buffer, typeId); //write header
        value(buffer, value); //write val
    }
}
