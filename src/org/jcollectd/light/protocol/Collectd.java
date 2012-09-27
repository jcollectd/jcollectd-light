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

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

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

        public void write(ByteBuffer buffer, String string) {
            CollectdBinaryProtocol.string(buffer, ID, string);
        }
    }

    public static class Value{
        private Type type;
        private Number value;

        public Value(Type type, Number value) {
            this.type = type;
            this.value = value;
        }

        public static Value valueOf(Number value) {
            Type type = Type.DERIVE;
            if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                type = Type.GAUGE;
            }
            return valueOf(type, value);
        }

        public static Value valueOf(Type type, Number value) {
            return new Value(type, value);
        }

        public enum Type {
            COUNTER((byte) 0) {
                @Override
                public void write(ByteBuffer bytesBuffer, Number val) {
                    CollectdBinaryProtocol.value(bytesBuffer, val.longValue());
                }
            },
            GAUGE((byte) 1) {
                @Override
                public void write(ByteBuffer bytesBuffer, Number val) {
                    CollectdBinaryProtocol.value(bytesBuffer, val.doubleValue());
                }
            },
            DERIVE((byte) 2) {
                @Override
                public void write(ByteBuffer bytesBuffer, Number val) {
                    CollectdBinaryProtocol.value(bytesBuffer, val.intValue());
                }
            },
            ABSOLUTE((byte) 3) {
                @Override
                public void write(ByteBuffer bytesBuffer, Number val) {
                    CollectdBinaryProtocol.value(bytesBuffer, val.longValue());
                }
            };

            public final byte ID;

            Type(byte id) {
                ID = id;
            }

            public abstract void write(ByteBuffer bytesBuffer, Number val);

            public void header(ByteBuffer buffer) {
                CollectdBinaryProtocol.header(buffer, ID);
            }
        }

    }

    public static void values(ByteBuffer buffer, String hostname, String plugin, String pluginInstance, String type, String typeInstance, List<Value> values) {
        if (!isEmpty(hostname))
            Part.HOST.write(buffer, hostname);
        if (!isEmpty(plugin))
            Part.PLUGIN.write(buffer, plugin);
        if (!isEmpty(pluginInstance))
            Part.PLUGIN_INSTANCE.write(buffer, pluginInstance);
        if (!isEmpty(type))
            Part.TYPE.write(buffer, type);
        if (!isEmpty(typeInstance))
            Part.TYPE_INSTANCE.write(buffer, typeInstance);
        if (values != null && !values.isEmpty()) {
            byte types[] = new byte[values.size()];
            byte bytes[] = new byte[CollectdBinaryProtocol.UINT64_LEN * values.size()];
            ByteBuffer typesBuffer = ByteBuffer.wrap(types);
            ByteBuffer bytesBuffer = ByteBuffer.wrap(bytes);
            for (Value val : values) {
                val.type.header(typesBuffer);
                val.type.write(bytesBuffer, val.value);
            }
            CollectdBinaryProtocol.values(buffer, Part.VALUES.ID, types, bytes);
        }

        //TODO: timestamp and interval
    }

    private static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }


}
