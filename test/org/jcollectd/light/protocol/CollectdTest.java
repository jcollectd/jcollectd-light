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

import java.nio.ByteBuffer;

import static org.jcollectd.light.protocol.Helper.assertArrayEquals;

public class CollectdTest {

    public void testParts() {
        ByteBuffer b;

        b = ByteBuffer.allocate(18);
        Collectd.Part.HOST.write(b, "bricky.teepub");
        assertArrayEquals("00000012627269636b792e74656570756200", b.compact());
        b.clear();

        b = ByteBuffer.allocate(15);
        Collectd.Part.HOST.write(b, "deb.teepub");
        assertArrayEquals("0000000f6465622e74656570756200", b.compact());
        b.clear();

    }

    @Test
    public void testValues() {

    }
}
