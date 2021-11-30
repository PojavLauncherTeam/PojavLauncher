package net.kdt.pojavlaunch.profiles;

import java.nio.charset.StandardCharsets;

public class StringCRC64 {
    private final static long POLY = (long) 0xc96c5795d7870f42L; // ECMA-182
    private final static long[][] table;
    static
    {
        table = new long[8][256];

        for (int n = 0; n < 256; n++)
        {
            long crc = n;
            for (int k = 0; k < 8; k++)
            {
                if ((crc & 1) == 1)
                {
                    crc = (crc >>> 1) ^ POLY;
                }
                else
                {
                    crc = (crc >>> 1);
                }
            }
            table[0][n] = crc;
        }
        for (int n = 0; n < 256; n++)
        {
            long crc = table[0][n];
            for (int k = 1; k < 8; k++)
            {
                crc = table[0][(int) (crc & 0xff)] ^ (crc >>> 8);
                table[k][n] = crc;
            }
        }
    }
    public static long strhash(String str) {
        byte[] b = str.getBytes(StandardCharsets.US_ASCII);
        long value = 0;
        int idx = 0;
        int len = b.length;
        while (len >= 8)
        {
            value = table[7][(int) (value & 0xff ^ (b[idx] & 0xff))]
                    ^ table[6][(int) ((value >>> 8) & 0xff ^ (b[idx + 1] & 0xff))]
                    ^ table[5][(int) ((value >>> 16) & 0xff ^ (b[idx + 2] & 0xff))]
                    ^ table[4][(int) ((value >>> 24) & 0xff ^ (b[idx + 3] & 0xff))]
                    ^ table[3][(int) ((value >>> 32) & 0xff ^ (b[idx + 4] & 0xff))]
                    ^ table[2][(int) ((value >>> 40) & 0xff ^ (b[idx + 5] & 0xff))]
                    ^ table[1][(int) ((value >>> 48) & 0xff ^ (b[idx + 6] & 0xff))]
                    ^ table[0][(int) ((value >>> 56) ^ b[idx + 7] & 0xff)];
            idx += 8;
            len -= 8;
        }
        while (len > 0)
        {
            value = table[0][(int) ((value ^ b[idx]) & 0xff)] ^ (value >>> 8);
            idx++;
            len--;
        }
        return ~value;
    }
}
