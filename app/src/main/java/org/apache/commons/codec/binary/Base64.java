package org.apache.commons.codec.binary;

public class Base64
{
	public static byte[] decodeBase64(String string) {
        return android.util.Base64.decode(string, 0);// android.util.Base64.DEFAULT
    }
}
