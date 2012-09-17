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
 * Time: 11:47 AM
 */
public class Collectd {

    public enum Part {

        HOST((short) 0x0000),
        TIME((short) 0x0001),
        PLUGIN((short) 0x0002),
        PLUGIN_INSTANCE((short) 0x0003),
        TYPE((short) 0x0004),
        TYPE_INSTANCE((short) 0x0005),
        VALUES((short) 0x0006),
        INTERVAL((short) 0x0007),
        TIME_HIRES((short) 0x0008),
        INTERVAL_HIRES((short) 0x0009),
        MESSAGE((short) 0x0100),
        SEVERITY((short) 0x0101),
        SIG((short) 0x0200),
        ENC((short) 0x0210);

        public final short ID;

        Part(short id) {
            this.ID = id;
        }

    }

    public enum Type {
        COUNTER((short) 0),
        GAUGE((short) 1),
        DERIVE((short) 2),
        ABSOLUTE((short) 3);

        public final short ID;

        Type(short id) {
            ID = id;
        }

    }


}
