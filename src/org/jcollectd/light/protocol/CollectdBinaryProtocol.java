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
 * jcollectd - org.jcollectd.light.protocol
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p/>
 * Date: 9/13/12
 * Time: 10:47 AM
 */
public class CollectdBinaryProtocol {

    static final short UINT8_LEN = 1;
    static final short UINT16_LEN = UINT8_LEN * 2;
    static final short UINT32_LEN = UINT16_LEN * 2;
    static final short UINT64_LEN = UINT32_LEN * 2;
    static final short HEADER_LEN = UINT16_LEN * 2;

    /* type header w. type only http://collectd.org/wiki/index.php/Binary_protocol#Value_parts */
    public static ByteBuffer header(ByteBuffer buffer, byte typeId) {
        buffer.put(typeId); //write typeId
        return buffer;
    }

    /* part header w. type and length */
    public static ByteBuffer header(ByteBuffer buffer, short partId, short length) {
        buffer.putShort(partId); //write partId
        buffer.putShort(length); //write part length
        return buffer;
    }

    /* values header w. type, length and number of values */
    public static ByteBuffer header(ByteBuffer buffer, short partId, short length, short valuesCount) {
        header(buffer, partId, length);
        buffer.putShort(valuesCount);
        return buffer;
    }

    /* http://collectd.org/wiki/index.php/Binary_protocol#Numeric_parts */
    public static ByteBuffer numeric(ByteBuffer buffer, short part, long val) {
        header(buffer, part, (short) (HEADER_LEN + UINT64_LEN)); //write header
        buffer.putLong(val); //write long
        return buffer;
    }

    /* http://collectd.org/wiki/index.php/Binary_protocol#String_parts */
    public static ByteBuffer string(ByteBuffer buffer, short partId, String val) {
        if (val == null) val = "";
        header(buffer, partId, (short) (HEADER_LEN + val.length() + 1)); //write header
        buffer.put(val.getBytes()).put((byte) '\0'); //write string
        return buffer;
    }

    /*http://collectd.org/wiki/index.php/Binary_protocol#Value_parts*/
    public static ByteBuffer values(ByteBuffer buffer, short partId, byte[] types, byte[] values) {
        header(buffer, partId, (short) (HEADER_LEN + UINT16_LEN + ((UINT8_LEN + UINT64_LEN) * types.length)), (short) types.length);
        buffer.put(types);
        buffer.put(values);
        return buffer;
    }

    /* ENC/SIGN*/
    public static ByteBuffer sign(ByteBuffer buffer, short partId, String username, byte[] hmac) {
        header(buffer, partId, (short) (HEADER_LEN + username.length() + hmac.length)); //write header
        buffer.put(hmac); //write signature
        buffer.put(username.getBytes()); //write username
        return buffer;
    }

    public static ByteBuffer encrypt(ByteBuffer buffer, short partId, String username, byte[] iv, byte[] bytes) {
        header(buffer, partId, (short) (HEADER_LEN + username.length() + UINT16_LEN + iv.length + bytes.length));  //write header
        buffer.putShort((short) username.length()).put(username.getBytes()); //write user length and user
        buffer.put(iv); // write init vector
        buffer.put(bytes); // write payload
        return buffer;
    }

    /* type helpers */
    public static ByteBuffer value(ByteBuffer buffer, long val) {
        buffer.putLong(val); //write long
        return buffer;
    }

    public static ByteBuffer value(ByteBuffer buffer, byte typeId, long val) {
        header(buffer, typeId); //write header
        value(buffer, val); //write long
        return buffer;
    }

    public static ByteBuffer value(ByteBuffer buffer, double val) {
        ByteOrder order = buffer.order(); //get buffer's order
        buffer.order(ByteOrder.LITTLE_ENDIAN).putDouble(val); //switch to LITTLE_ENDIAN, write double
        buffer.order(order); //switch to original byteorder
        return buffer;
    }

    public static ByteBuffer value(ByteBuffer buffer, byte typeId, double val) {
        header(buffer, typeId); //write header
        value(buffer, val); //write val
        return buffer;
    }
}
