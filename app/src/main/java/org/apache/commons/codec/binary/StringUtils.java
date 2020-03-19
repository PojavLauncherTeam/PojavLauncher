package org.apache.commons.codec.binary;

import java.nio.charset.*;

public class StringUtils
{
	public static byte[] getBytesUtf8(String str) {
		return str.getBytes(Charset.forName("UTF-8"));
	}
}
