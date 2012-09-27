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

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.jcollectd.light.protocol.CollectdBinaryProtocol.*;
import static org.jcollectd.light.protocol.Helper.assertArrayEquals;


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

    public static Mac hmacSHA256() throws NoSuchAlgorithmException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        return hmacSHA256;
    }

    public static Cipher aes256() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("AES/OFB/NOPADDING");
    }


    @Test
    public void testHeader() throws Exception {
        assertArrayEquals("00", CollectdBinaryProtocol.header(ByteBuffer.allocate(UINT8_LEN), (byte) 0));
        assertArrayEquals("02", CollectdBinaryProtocol.header(ByteBuffer.allocate(UINT8_LEN), (byte) 2));
        assertArrayEquals("03",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(UINT8_LEN), (byte) 3));
        assertArrayEquals("04",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(UINT8_LEN), (byte) 4));
        assertArrayEquals("05",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(UINT8_LEN), (byte) 5));


        assertArrayEquals("00000012",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(HEADER_LEN), (short) 0, (short) 18));
        assertArrayEquals("0002000d",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(HEADER_LEN), (short) 2, (short) 13));
        assertArrayEquals("0003000d",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(HEADER_LEN), (short) 3, (short) 13));
        assertArrayEquals("00040014",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(HEADER_LEN), (short) 4, (short) 20));
        assertArrayEquals("0005000c",
                CollectdBinaryProtocol.header(ByteBuffer.allocate(HEADER_LEN), (short) 5, (short) 12));
    }


    @Test
    public void testString() throws Exception {
        assertArrayEquals("00000012627269636b792e74656570756200",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("bricky.teepub")), (short) 0, "bricky.teepub"))
        ;
        assertArrayEquals("0000000f6465622e74656570756200",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("deb.teepub")), (short) 0, "deb.teepub"));

        assertArrayEquals("0002000d746370636f6e6e7300",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("tcpconns")), (short) 2, "tcpconns"));
        assertArrayEquals("0002000863707500",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("cpu")), (short) 2, "cpu"));

        assertArrayEquals("0003000d32322d6c6f63616c00",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("22-local")), (short) 3, "22-local"));
        assertArrayEquals("000300063000",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("0")), (short) 3, "0"));

        assertArrayEquals("000400147463705f636f6e6e656374696f6e7300",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("tcp_connections")), (short) 4, "tcp_connect" +
                        "ions"));
        assertArrayEquals("0004000863707500",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("cpu")), (short) 4, "cpu"));

        assertArrayEquals("0005000c434c4f53494e4700",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("CLOSING")), (short) 5, "CLOSING"));
        assertArrayEquals("000500096e69636500",
                CollectdBinaryProtocol.string(ByteBuffer.allocate(length("nice")), (short) 5, "nice"));

    }

    @Test
    public void testNumeric() throws Exception {
        assertArrayEquals("0001000c0000000050531d18",
                CollectdBinaryProtocol.numeric(ByteBuffer.allocate(HEADER_LEN + UINT64_LEN), (short) 1, 1347624216));

        assertArrayEquals("0007000c0000000000000005",
                CollectdBinaryProtocol.numeric(ByteBuffer.allocate(HEADER_LEN + UINT64_LEN), (short) 7, 5));

        assertArrayEquals("0007000c0000000140000000",
                CollectdBinaryProtocol.numeric(ByteBuffer.allocate(HEADER_LEN + UINT64_LEN), (short) 7, 5368709120L));

        assertArrayEquals("0008000c1414cbc85c6ae9d8",
                CollectdBinaryProtocol.numeric(
                        ByteBuffer.allocate(HEADER_LEN + UINT64_LEN), (short) 8, 1447005441697180120L));


    }

    @Test
    public void testLong() throws Exception {
        assertArrayEquals("020000000000000000",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT8_LEN + UINT64_LEN), (byte) 2, 0));
        assertArrayEquals("0200000000000016bc",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT8_LEN + UINT64_LEN), (byte) 2, 5820));

        assertArrayEquals("0000000000000000",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT64_LEN), 0));
        assertArrayEquals("00000000000016bc",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT64_LEN), 5820));
    }

    @Test
    public void testDecimal() throws Exception {
        assertArrayEquals("01000000807fe34041",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT8_LEN + UINT64_LEN), (byte) 1, 2213631.0));
        assertArrayEquals("0100000000d0d0f93f",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT8_LEN + UINT64_LEN), (byte) 1, 1.6134796142578125))
        ;
        assertArrayEquals("01000000000096a640",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT8_LEN + UINT64_LEN), (byte) 1, 2891.0));

        assertArrayEquals("000000807fe34041",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT64_LEN), 2213631.0));
        assertArrayEquals("00000000d0d0f93f",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT64_LEN), 1.6134796142578125));
        assertArrayEquals("000000000096a640",
                CollectdBinaryProtocol.value(ByteBuffer.allocate(UINT64_LEN), 2891.0));

    }

    @Test
    public void testSign() throws Exception {
        String user = "testuser";
        String key = "testpass";
        /**/

        Mac hmacSHA256 = hmacSHA256();

        SecretKey sKey = new SecretKeySpec(key.getBytes("US-ASCII"), hmacSHA256.getAlgorithm());
        hmacSHA256.init(sKey);
        /**/
        byte[] payload = DatatypeConverter.parseHexBinary(
                "0000000974657374000008000c1416c25a85668e930009000c00000000400000000002000a7573657273000004000a7573657273000006000f00010100000000000008400008000c1416c25ac566fd3b0006000f00010100000000000008400008000c1416c25b0566ae110006000f00010100000000000008400008000c1416c25b456688110006000f00010100000000000008400008000c1416c25b8566a2ff0006000f00010100000000000008400008000c1416c25bc56683e20006000f00010100000000000008400008000c1416c25c0566b6530006000f00010100000000000008400008000c1416c25c4566917a0006000f00010100000000000008400008000c1416c25c856692d60006000f00010100000000000008400008000c1416c25cc56702bf0006000f00010100000000000008400008000c1416c25d0566cab00006000f00010100000000000008400008000c1416c25d4566ba640006000f00010100000000000008400008000c1416c25d8566f1970006000f00010100000000000008400008000c1416c25dc56686390006000f00010100000000000008400008000c1416c25e05669b6f0006000f00010100000000000008400008000c1416c25e4566c6440006000f00010100000000000008400008000c1416c25e8566ac540006000f00010100000000000008400008000c1416c25ec5667b800006000f00010100000000000008400008000c1416c25f05668aff0006000f00010100000000000008400008000c1416c25f4566c3440006000f00010100000000000008400008000c1416c25f8566bae80006000f00010100000000000008400008000c1416c25fc5669c7f0006000f00010100000000000008400008000c1416c2600566b92b0006000f00010100000000000008400008000c1416c2604566bbb20006000f00010100000000000008400008000c1416c2608566c19d0006000f00010100000000000008400008000c1416c260c56698260006000f00010100000000000008400008000c1416c2610566cd3d0006000f00010100000000000008400008000c1416c2614566bd230006000f00010100000000000008400008000c1416c26185670f0b0006000f00010100000000000008400008000c1416c261c566b4820006000f00010100000000000008400008000c1416c2620567bb9f0006000f00010100000000000008400008000c1416c262456695200006000f00010100000000000008400008000c1416c2628566cf720006000f00010100000000000008400008000c1416c262c56737700006000f00010100000000000008400008000c1416c26305667a510006000f00010100000000000008400008000c1416c2634566adcb0006000f00010100000000000008400008000c1416c2638566868f0006000f00010100000000000008400008000c1416c263c56683a20006000f00010100000000000008400008000c1416c2640566d9a30006000f00010100000000000008400008000c1416c2644566c1bf0006000f00010100000000000008400008000c1416c26485669fa10006000f00010100000000000008400008000c1416c264c566af090006000f00010100000000000008400008000c1416c2650566f41f0006000f00010100000000000008400008000c1416c26545668ea90006000f00010100000000000008400008000c1416c265856688270006000f00010100000000000008400008000c1416c265c566c1d40006000f00010100000000000008400008000c1416c2660566a55a0006000f00010100000000000008400008000c1416c2664566c5ca0006000f0001010000000000000840");
        byte[] signature = DatatypeConverter.parseHexBinary("0200" + //partId
                "002c" + //length
                "719adf9e15a4b60a658125370fdf4ec84e23e291571d705be09c10f2a1a7c4b4" + // signature
                "7465737475736572"); // username

        Helper.assertArrayEquals(signature,
                CollectdBinaryProtocol.sign(ByteBuffer.allocate(CollectdBinaryProtocol.length(key) - 1 + hmacSHA256.getMacLength()), (short) 0x0200, user, hmacSHA256.doFinal(ByteBuffer.allocate(user.length() + payload.length).put(user.getBytes()).put(payload).array())));
        /**/

        payload = DatatypeConverter.parseHexBinary(
                "0000000974657374000008000c1416c3ad23f211c60009000c00000000400000000002000a7573657273000004000a7573657273000006000f00010100000000000000400008000c1416c3ad63f21f750006000f00010100000000000000400008000c1416c3ada3f1efa90006000f00010100000000000000400008000c1416c3ade3f23ad10006000f00010100000000000000400008000c1416c3ae23f2a59f0006000f00010100000000000000400008000c1416c3ae63f228ec0006000f00010100000000000000400008000c1416c3aea3f22d160006000f00010100000000000000400008000c1416c3aee3f239000006000f00010100000000000000400008000c1416c3af23f283af0006000f00010100000000000000400008000c1416c3af63f1f1400006000f00010100000000000000400008000c1416c3afa3f28a8a0006000f00010100000000000000400008000c1416c3afe3f2711f0006000f00010100000000000000400008000c1416c3b023f247770006000f00010100000000000000400008000c1416c3b063f24b0d0006000f00010100000000000000400008000c1416c3b0a3f22c3f0006000f00010100000000000000400008000c1416c3b0e3f23fdb0006000f00010100000000000000400008000c1416c3b123f243150006000f00010100000000000000400008000c1416c3b163f22b0c0006000f00010100000000000000400008000c1416c3b1a3f23b370006000f00010100000000000000400008000c1416c3b1e3f268520006000f00010100000000000000400008000c1416c3b223f229870006000f00010100000000000000400008000c1416c3b263f21fd90006000f00010100000000000000400008000c1416c3b2a3f2380f0006000f00010100000000000000400008000c1416c3b2e3f22eae0006000f00010100000000000000400008000c1416c3b323f23b320006000f00010100000000000000400008000c1416c3b363f25d7d0006000f00010100000000000000400008000c1416c3b3a3f281500006000f00010100000000000000400008000c1416c3b3e3f22d850006000f00010100000000000000400008000c1416c3b423f1ee1a0006000f00010100000000000000400008000c1416c3b463f238090006000f00010100000000000000400008000c1416c3b4a3f23a7f0006000f00010100000000000000400008000c1416c3b4e3f237d10006000f00010100000000000000400008000c1416c3b523f23b540006000f00010100000000000000400008000c1416c3b563f205920006000f00010100000000000000400008000c1416c3b5a3f20b960006000f00010100000000000000400008000c1416c3b5e3f24b440006000f00010100000000000000400008000c1416c3b623f212320006000f00010100000000000000400008000c1416c3b663f274f30006000f00010100000000000000400008000c1416c3b6a3f208a80006000f00010100000000000000400008000c1416c3b6e3f23e660006000f00010100000000000000400008000c1416c3b723f225050006000f00010100000000000000400008000c1416c3b763f20f050006000f00010100000000000000400008000c1416c3b7a3f21c5c0006000f00010100000000000000400008000c1416c3b7e3f1f5cc0006000f00010100000000000000400008000c1416c3b823f2094d0006000f00010100000000000000400008000c1416c3b863f1f3930006000f00010100000000000000400008000c1416c3b8a3f268b00006000f00010100000000000000400008000c1416c3b8e3f1fa970006000f0001010000000000000040")
        ;
        signature = DatatypeConverter.parseHexBinary("0200" + //partid
                "002c" + //length
                "775ad11cc489edb0aec1e6e84e7e28bedf51cc2d5fb9780dd6496f6a748e0a85" + //sign
                "7465737475736572");//username

        assertArrayEquals(signature,
                CollectdBinaryProtocol.sign(
                        ByteBuffer.allocate(CollectdBinaryProtocol.length(key) - 1 + hmacSHA256.getMacLength()),
                        (short) 0x0200, user, hmacSHA256.doFinal(ByteBuffer.allocate(user.length() + payload.length).put(user.getBytes()).put(payload).array())));
        /**/

        payload = DatatypeConverter.parseHexBinary(
                "0000000974657374000008000c1416c3b923f2768f0009000c00000000400000000002000a7573657273000004000a7573657273000006000f00010100000000000000400008000c1416c3b963f293820006000f00010100000000000000400008000c1416c3b9a3f230570006000f00010100000000000000400008000c1416c3b9e3f245c60006000f00010100000000000000400008000c1416c3ba23f22c360006000f00010100000000000000400008000c1416c3ba63f21ad00006000f00010100000000000000400008000c1416c3baa3f223160006000f00010100000000000000400008000c1416c3bae3f256730006000f00010100000000000000400008000c1416c3bb23f231ca0006000f00010100000000000000400008000c1416c3bb63f1fb200006000f00010100000000000000400008000c1416c3bba3f201070006000f00010100000000000000400008000c1416c3bbe3f207080006000f00010100000000000000400008000c1416c3bc23f2d1650006000f00010100000000000000400008000c1416c3bc63f224830006000f00010100000000000000400008000c1416c3bca3f27f850006000f00010100000000000000400008000c1416c3bce3f239cb0006000f00010100000000000000400008000c1416c3bd23f290e40006000f00010100000000000000400008000c1416c3bd63f223410006000f00010100000000000000400008000c1416c3bda3f2407a0006000f00010100000000000000400008000c1416c3bde3f241cb0006000f00010100000000000000400008000c1416c3be23f25d870006000f00010100000000000000400008000c1416c3be63f22b390006000f00010100000000000000400008000c1416c3bea3f232fc0006000f00010100000000000000400008000c1416c3bee3f207d10006000f00010100000000000000400008000c1416c3bf23f23a200006000f00010100000000000000400008000c1416c3bf63f2154e0006000f00010100000000000000400008000c1416c3bfa3f20be20006000f00010100000000000000400008000c1416c3bfe3f23f5e0006000f00010100000000000000400008000c1416c3c023f21d440006000f00010100000000000000400008000c1416c3c063f237a90006000f00010100000000000000400008000c1416c3c0a3f24cde0006000f00010100000000000000400008000c1416c3c0e3f241ae0006000f00010100000000000000400008000c1416c3c123f2406a0006000f00010100000000000000400008000c1416c3c163f2363f0006000f00010100000000000000400008000c1416c3c1a3f235390006000f00010100000000000000400008000c1416c3c1e3f21a810006000f00010100000000000000400008000c1416c3c223f230970006000f00010100000000000000400008000c1416c3c263f206a00006000f00010100000000000000400008000c1416c3c2a3f255820006000f00010100000000000000400008000c1416c3c2e3f224aa0006000f00010100000000000000400008000c1416c3c323f204250006000f00010100000000000000400008000c1416c3c363f231d70006000f00010100000000000000400008000c1416c3c3a3f23b950006000f00010100000000000000400008000c1416c3c3e3f1f6dd0006000f00010100000000000000400008000c1416c3c423f223a80006000f00010100000000000000400008000c1416c3c463f22dd80006000f00010100000000000000400008000c1416c3c4a3f233c40006000f00010100000000000000400008000c1416c3c4e3f22bcc0006000f0001010000000000000040")
        ;
        signature = DatatypeConverter.parseHexBinary("0200" + //partId
                "002c" + //length
                "e2d5f30655863ec6974299dd24990a288aad4d225d116353f843722fcfa95e67" + //sign
                "7465737475736572"); //user

        assertArrayEquals(signature,
                CollectdBinaryProtocol.sign(
                        ByteBuffer.allocate(CollectdBinaryProtocol.length(key) - 1 + hmacSHA256.getMacLength()),
                        (short) 0x0200, user, hmacSHA256.doFinal(ByteBuffer.allocate(user.length() + payload.length).put(user.getBytes()).put(payload).array())));

    }


    @Test
    public void testEnc() throws Exception {

        byte[] payloadFwd = DatatypeConverter.parseHexBinary(
                "0000000974657374000008000c14170313b50636230009000c00000000400000000002000a7573657273000004000a7573657273000006000f00010100000000000008400008000c14170313f50509ae0006000f00010100000000000008400008000c141703143505fcb90006000f00010100000000000008400008000c141703147505cfbb0006000f00010100000000000008400008000c14170314b505d6590006000f00010100000000000008400008000c14170314f505cb1e0006000f00010100000000000008400008000c141703153505f0c60006000f00010100000000000008400008000c141703157505bf4b0006000f00010100000000000008400008000c14170315b5060e320006000f00010100000000000008400008000c14170315f505af510006000f00010100000000000008400008000c141703163505cade0006000f00010100000000000008400008000c141703167505ac670006000f00010100000000000008400008000c14170316b505d2050006000f00010100000000000008400008000c14170316f505659b0006000f00010100000000000008400008000c141703173505d5150006000f00010100000000000008400008000c14170317750686f90006000f00010100000000000008400008000c14170317b505f8540006000f00010100000000000008400008000c14170317f505b8810006000f00010100000000000008400008000c141703183505b38a0006000f00010100000000000008400008000c141703187505cfe60006000f00010100000000000008400008000c14170318b505f9400006000f00010100000000000008400008000c14170318f50602110006000f00010100000000000008400008000c141703193505e7fa0006000f00010100000000000008400008000c141703197505b8d90006000f00010100000000000008400008000c14170319b505c7970006000f00010100000000000008400008000c14170319f505cfc40006000f00010100000000000008400008000c1417031a3505cb090006000f00010100000000000008400008000c1417031a7505e52d0006000f00010100000000000008400008000c1417031ab505ccc50006000f00010100000000000008400008000c1417031af50600be0006000f00010100000000000008400008000c1417031b3505bcde0006000f00010100000000000008400008000c1417031b75062f720006000f00010100000000000008400008000c1417031bb505a7130006000f00010100000000000008400008000c1417031bf50601620006000f00010100000000000008400008000c1417031c3505bfc30006000f00010100000000000008400008000c1417031c7505ed230006000f00010100000000000008400008000c1417031cb505e3230006000f00010100000000000008400008000c1417031cf5058a050006000f00010100000000000008400008000c1417031d350626f00006000f00010100000000000008400008000c1417031d7504faa60006000f00010100000000000008400008000c1417031db505d1da0006000f00010100000000000008400008000c1417031df50644670006000f00010100000000000008400008000c1417031e3505dae40006000f00010100000000000008400008000c1417031e7505bc1b0006000f00010100000000000008400008000c1417031eb5060ef60006000f00010100000000000008400008000c1417031ef505d0910006000f00010100000000000008400008000c1417031f3505d5b00006000f00010100000000000008400008000c1417031f7505ec370006000f0001010000000000000840");
        byte[] payloadEnc = DatatypeConverter.parseHexBinary("0210" + //partId
                "056b" + //length
                "0008" + //uname length
                "7465737475736572" + //uname
                "ae68f203138fea59df3b9ca46fc25d68" + // iv
                "51d0fe7139ecb3938398acb186e761fed0566aa5dae7c4678ffb13d086872fdb5436abee8f525a1cf82461b3ed925a04d77d0cb59b8062aeeebadc690ec5d24b2e614baa55d66574c8ceeb150af9c068c295d009ded059fe44c3b7805da384a176145a009969bc7ea409910f00876beaab03bcd2be68e3127cf00ac458313f742f266c3629d8c6c6a4e4759c0b399faf5553433b70560233eabe16205b1a68295b692765e82725ddb1e4947fefba848ded6c1d2f7e760bcb9a6b07c9b04b0cd4729c7ddc0c6b960d99a4dc70872695be85ef77478c3b0051dc2647a523e57aa7becc5632f7fed9fef22bc3d7e6834111b8d68556d4a3328e2cfa71aa5a82a2fda8a1fab85cc22f1511f5e57c1809f2187dcbb164d50380baa88f6e69a84ca60054c6900fc187ecb42f40b0d15503a21079cd44bf48b6b5b699692775a9a86e3a05bb9e32954c197ba295016c50c0078ab3366f708f9c6e53af91170f85cf62d9b24dfc7f45995a870e6bebe84436fb7e0baea73ae92910ebbccf3b36d4503d1bec47e10ef766f4d44a902b9540fa69991da913d0c052a2531599b39a8ba15505c1ce76003b85af5c83638c9e122a37fd2299513fec6044fc870d608e33afbd946129d18efd8b3cf190987fd9fcf9b9b7b614b354d1d3bd9c55cc9381289f7ba5d66d1aeacf4c25776190c7eae5fcd4a4ff5c3f141e41195c19c36ac4348090f2aae3893c3fb37fad85ed05f719ec7094ac9396cf92f0a4c87581cd026a32942b7c76ed7c7ceda2f3332e5619fca2590945a6147a1f81101535765de1332ba760bde89e722a99c67e7de035e5bd2940eec224c5419dffa41404e57df818c39ce9e96c026a06cef93f754a74c5c73823bab9e74c9941d2968a7fbd04ac4987f9b077f607c18af293c1c8e89c7a92c1778be08613b1ad2178a48562f75e6aec7a82fa188a5171c0a44aa2c4cc2ebb40d539ba2f97f854577a79c691e3ca4d4a471bdb0c63c3ef1647952729dd372092400a285899015f84ecb9f3834ddd286365feeea04fe5370a6c80282915059fed4b8670408e1797550cf61a70ee5333bd481d03317f514285f9b04e2425d67642b146515acf18a621935f7fa397ef8941aaf581d842c653dcd9077c1db16ea9131d546e8b59287f10989e04a34464e6d2859626d43e314f6ba06fa1f004fbc9d64c4a7b4dff3f1f06331ac9d4df8df8275ddd94235c0e3e4f1856b1015f98f6f18b388095697b63c844713b4d3fb2d887502ceb43a81df1743d13b4eb15bfcc762f10057d656744840e12feb77795d6c82276a481be1b8cd94e629893ccbaf7842e8ccca53ee4736c07c4ac1012028e393f22675b3129823d299ee51b8ec21dc5a1ed55267aa5fe70f2bb641be6d63212c93d11bc6fce1f9d46fbc65f96dc9d5d339fe3a14bc0cbd54aab7c64b3cc7db32ed88b9e89f48d9b048597fc418caf0034d14298b1da747cb269aa2c4537526330beee288d2af5e8efba6e97707cec1947fce72d8886415f4ce1a15a79967d68008766e764d0b552b364c5d2a36b8971979d25580e96b23a6c813b05c031ded20848c34840c605168246d788b1bcfb5011af8aaa6d1a5234d58de138fc2de59b6d5cb0a946f2ed6d24f14748a049d899f74e71da90d45d7b5b72ad9c7f955824e3fef75bc5aefdd4f93e1c55a7ba57b07d654c8f716dd4411912cffe5032fa8d2d58ebb308471de4e301004d4e0419f92d87f9bccd79baf0bffc5ec653f7bfd9056fbfe3e97aacb817c5556a04d3119c2efd13257d96f53720d609ac592db9d51ea2341d80e7bfe509dad27c83b959bb893ccb51937c7a6cb2b1ce73a3bf35528a48c41e47247da587f61a19f6e372096315d0fab1cc2d2d0146fa871b53bd7b397761b8e4ddc41a3daa1be4ee0faa");
        /**/

        byte[] iv = DatatypeConverter.parseHexBinary("ae68f203138fea59df3b9ca46fc25d68");
        byte[] payloadEncData = DatatypeConverter.parseHexBinary("51d0fe7139ecb3938398acb186e761fed0566aa5dae7c4678ffb13d086872fdb5436abee8f525a1cf82461b3ed925a04d77d0cb59b8062aeeebadc690ec5d24b2e614baa55d66574c8ceeb150af9c068c295d009ded059fe44c3b7805da384a176145a009969bc7ea409910f00876beaab03bcd2be68e3127cf00ac458313f742f266c3629d8c6c6a4e4759c0b399faf5553433b70560233eabe16205b1a68295b692765e82725ddb1e4947fefba848ded6c1d2f7e760bcb9a6b07c9b04b0cd4729c7ddc0c6b960d99a4dc70872695be85ef77478c3b0051dc2647a523e57aa7becc5632f7fed9fef22bc3d7e6834111b8d68556d4a3328e2cfa71aa5a82a2fda8a1fab85cc22f1511f5e57c1809f2187dcbb164d50380baa88f6e69a84ca60054c6900fc187ecb42f40b0d15503a21079cd44bf48b6b5b699692775a9a86e3a05bb9e32954c197ba295016c50c0078ab3366f708f9c6e53af91170f85cf62d9b24dfc7f45995a870e6bebe84436fb7e0baea73ae92910ebbccf3b36d4503d1bec47e10ef766f4d44a902b9540fa69991da913d0c052a2531599b39a8ba15505c1ce76003b85af5c83638c9e122a37fd2299513fec6044fc870d608e33afbd946129d18efd8b3cf190987fd9fcf9b9b7b614b354d1d3bd9c55cc9381289f7ba5d66d1aeacf4c25776190c7eae5fcd4a4ff5c3f141e41195c19c36ac4348090f2aae3893c3fb37fad85ed05f719ec7094ac9396cf92f0a4c87581cd026a32942b7c76ed7c7ceda2f3332e5619fca2590945a6147a1f81101535765de1332ba760bde89e722a99c67e7de035e5bd2940eec224c5419dffa41404e57df818c39ce9e96c026a06cef93f754a74c5c73823bab9e74c9941d2968a7fbd04ac4987f9b077f607c18af293c1c8e89c7a92c1778be08613b1ad2178a48562f75e6aec7a82fa188a5171c0a44aa2c4cc2ebb40d539ba2f97f854577a79c691e3ca4d4a471bdb0c63c3ef1647952729dd372092400a285899015f84ecb9f3834ddd286365feeea04fe5370a6c80282915059fed4b8670408e1797550cf61a70ee5333bd481d03317f514285f9b04e2425d67642b146515acf18a621935f7fa397ef8941aaf581d842c653dcd9077c1db16ea9131d546e8b59287f10989e04a34464e6d2859626d43e314f6ba06fa1f004fbc9d64c4a7b4dff3f1f06331ac9d4df8df8275ddd94235c0e3e4f1856b1015f98f6f18b388095697b63c844713b4d3fb2d887502ceb43a81df1743d13b4eb15bfcc762f10057d656744840e12feb77795d6c82276a481be1b8cd94e629893ccbaf7842e8ccca53ee4736c07c4ac1012028e393f22675b3129823d299ee51b8ec21dc5a1ed55267aa5fe70f2bb641be6d63212c93d11bc6fce1f9d46fbc65f96dc9d5d339fe3a14bc0cbd54aab7c64b3cc7db32ed88b9e89f48d9b048597fc418caf0034d14298b1da747cb269aa2c4537526330beee288d2af5e8efba6e97707cec1947fce72d8886415f4ce1a15a79967d68008766e764d0b552b364c5d2a36b8971979d25580e96b23a6c813b05c031ded20848c34840c605168246d788b1bcfb5011af8aaa6d1a5234d58de138fc2de59b6d5cb0a946f2ed6d24f14748a049d899f74e71da90d45d7b5b72ad9c7f955824e3fef75bc5aefdd4f93e1c55a7ba57b07d654c8f716dd4411912cffe5032fa8d2d58ebb308471de4e301004d4e0419f92d87f9bccd79baf0bffc5ec653f7bfd9056fbfe3e97aacb817c5556a04d3119c2efd13257d96f53720d609ac592db9d51ea2341d80e7bfe509dad27c83b959bb893ccb51937c7a6cb2b1ce73a3bf35528a48c41e47247da587f61a19f6e372096315d0fab1cc2d2d0146fa871b53bd7b397761b8e4ddc41a3daa1be4ee0faa");

        String user = "testuser";
        String key = "testpass";
        /**/


        Cipher cipher = aes256();

        SecretKey secret = new SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(key.getBytes()), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

        MessageDigest cript = MessageDigest.getInstance("SHA-1");
        /**/

        byte[] unciphertext = cipher.doFinal(payloadEncData);

        byte[] payloadSHA1 = new byte[cript.getDigestLength()];
        byte[] payloadDec = new byte[unciphertext.length - payloadSHA1.length];

        ByteBuffer buff = ByteBuffer.wrap(unciphertext);

        buff.get(payloadSHA1, 0, payloadSHA1.length);
        buff.get(payloadDec);


        Assert.assertArrayEquals(payloadSHA1, cript.digest(payloadDec));
        Assert.assertArrayEquals(payloadFwd, payloadDec);


        /**/
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] b = cipher.doFinal(ByteBuffer.allocate(md.getDigestLength() + payloadFwd.length).put(md.digest(payloadFwd)).put(payloadFwd).array());

        assertArrayEquals(payloadEnc, CollectdBinaryProtocol.encrypt(ByteBuffer.allocate(length(key) - 1 + UINT16_LEN + iv.length + b.length), (short) 0x0210, user, cipher.getIV(), b));

    }

    @Test
    public void testValues() throws IOException {

        byte[] types = {(byte) 2, (byte) 2};
        ByteBuffer v = ByteBuffer.allocate(UINT64_LEN * types.length);

        v.putLong(2);
        v.putLong(1);

        assertArrayEquals(DatatypeConverter.parseHexBinary("000600180002020200000000000000020000000000000001"),
                CollectdBinaryProtocol.values(ByteBuffer.allocate(HEADER_LEN + UINT16_LEN + ((UINT8_LEN + UINT64_LEN) * types.length)), (short) 0x0006, types, v.array()))
        ;
    }

}
