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

import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.junit.Assert.assertArrayEquals;

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
public class CollectdBinaryProtocolTest {

    @Test
    public void testHeader() throws Exception {
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("00"),
                CollectdBinaryProtocol.header((byte) 0));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("02"),
                CollectdBinaryProtocol.header((byte) 2));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("03"),
                CollectdBinaryProtocol.header((byte) 3));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("04"),
                CollectdBinaryProtocol.header((byte) 4));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("05"),
                CollectdBinaryProtocol.header((byte) 5));


        assertArrayEquals(
                DatatypeConverter.parseHexBinary("00000012"),
                CollectdBinaryProtocol.header((short) 0, (short) 18));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0002000d"),
                CollectdBinaryProtocol.header((short) 2, (short) 13));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0003000d"),
                CollectdBinaryProtocol.header((short) 3, (short) 13));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("00040014"),
                CollectdBinaryProtocol.header((short) 4, (short) 20));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0005000c"),
                CollectdBinaryProtocol.header((short) 5, (short) 12));
    }

    @Test
    public void testString() throws Exception {
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("00000012627269636b792e74656570756200"),
                CollectdBinaryProtocol.string((short) 0, "bricky.teepub"));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0000000f6465622e74656570756200"),
                CollectdBinaryProtocol.string((short) 0, "deb.teepub"));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0002000d746370636f6e6e7300"),
                CollectdBinaryProtocol.string((short) 2, "tcpconns"));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0002000863707500"),
                CollectdBinaryProtocol.string((short) 2, "cpu"));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0003000d32322d6c6f63616c00"),
                CollectdBinaryProtocol.string((short) 3, "22-local"));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("000300063000"),
                CollectdBinaryProtocol.string((short) 3, "0"));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("000400147463705f636f6e6e656374696f6e7300"),
                CollectdBinaryProtocol.string((short) 4, "tcp_connections"));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0004000863707500"),
                CollectdBinaryProtocol.string((short) 4, "cpu"));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0005000c434c4f53494e4700"),
                CollectdBinaryProtocol.string((short) 5, "CLOSING"));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("000500096e69636500"),
                CollectdBinaryProtocol.string((short) 5, "nice"));

    }

    @Test
    public void testNumeric() throws Exception {
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0001000c0000000050531d18"),
                CollectdBinaryProtocol.numeric((short) 1, 1347624216));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0001000c0000000050531d18"),
                CollectdBinaryProtocol.numeric((short) 1, 1347624216));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0001000c0000000050531d18"),
                CollectdBinaryProtocol.numeric((short) 1, 1347624216));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0001000c0000000050531d18"),
                CollectdBinaryProtocol.numeric((short) 1, 1347624216));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0007000c0000000000000005"),
                CollectdBinaryProtocol.numeric((short) 7, 5));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0007000c0000000140000000"),
                CollectdBinaryProtocol.numeric((short) 7, 5368709120L));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0008000c1414cbc85c6ae9d8"),
                CollectdBinaryProtocol.numeric((short) 8, 1447005441697180120L));
    }

    @Test
    public void testLong() throws Exception {
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("020000000000000000"),
                CollectdBinaryProtocol.value((byte) 2, 0));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0200000000000016bc"),
                CollectdBinaryProtocol.value((byte) 2, 5820));
    }

    @Test
    public void testDecimal() throws Exception {
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("01000000807fe34041"),
                CollectdBinaryProtocol.value((byte) 1, 2213631.0));

        assertArrayEquals(
                DatatypeConverter.parseHexBinary("0100000000d0d0f93f"),
                CollectdBinaryProtocol.value((byte) 1, 1.6134796142578125));
        assertArrayEquals(
                DatatypeConverter.parseHexBinary("01000000000096a640"),
                CollectdBinaryProtocol.value((byte) 1, 2891.0));

    }

}
