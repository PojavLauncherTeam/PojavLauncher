/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.digest;

import java.security.MessageDigest;

/**
 * Standard {@link MessageDigest} algorithm names from the <cite>Java Cryptography Architecture Standard Algorithm Name
 * Documentation</cite>.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 * <p>
 * Java 8 and up: SHA-224.
 * </p>
 * <p>
 * Java 9 and up: SHA3-224, SHA3-256, SHA3-384, SHA3-512.
 * </p>
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#MessageDigest">
 *      Java 6 Cryptography Architecture Standard Algorithm Name Documentation</a>
 * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest">
 *      Java 7 Cryptography Architecture Standard Algorithm Name Documentation</a>
 * @see <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest">
 *      Java 8 Cryptography Architecture Standard Algorithm Name Documentation</a>
 * @see <a href="http://download.java.net/java/jdk9/docs/technotes/guides/security/StandardNames.html#MessageDigest">
 *      Java 9 Cryptography Architecture Standard Algorithm Name Documentation</a>
 *
 * @see <a href="http://dx.doi.org/10.6028/NIST.FIPS.180-4">FIPS PUB 180-4</a>
 * @see <a href="http://dx.doi.org/10.6028/NIST.FIPS.202">FIPS PUB 202</a>
 * @since 1.7
 * @version $Id$
 */
public class MessageDigestAlgorithms {

    /**
     * The MD2 message digest algorithm defined in RFC 1319.
     */
    public static final String MD2 = "MD2";

    /**
     * The MD5 message digest algorithm defined in RFC 1321.
     */
    public static final String MD5 = "MD5";

    /**
     * The SHA-1 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_1 = "SHA-1";

    /**
     * The SHA-224 hash algorithm defined in the FIPS PUB 180-3.
     * <p>
     * Present in Oracle Java 8.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA_224 = "SHA-224";

    /**
     * The SHA-256 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_256 = "SHA-256";

    /**
     * The SHA-384 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_384 = "SHA-384";

    /**
     * The SHA-512 hash algorithm defined in the FIPS PUB 180-2.
     */
    public static final String SHA_512 = "SHA-512";

    /**
     * The SHA3-224 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9 GA.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_224 = "SHA3-224";

    /**
     * The SHA3-256 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9 GA.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_256 = "SHA3-256";

    /**
     * The SHA3-384 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9 GA.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_384 = "SHA3-384";

    /**
     * The SHA3-512 hash algorithm defined in the FIPS PUB 202.
     * <p>
     * Included starting in Oracle Java 9 GA.
     * </p>
     *
     * @since 1.11
     */
    public static final String SHA3_512 = "SHA3-512";

    /**
     * Gets all constant values defined in this class.
     *
     * @return all constant values defined in this class.
     * @since 1.11
     */
    public static String[] values() {
        // N.B. do not use a constant array here as that can be changed externally by accident or design
        return new String[] {
            MD2, MD5, SHA_1, SHA_224, SHA_256, SHA_384, SHA_512, SHA3_224, SHA3_256, SHA3_384, SHA3_512
        };
    }

    private MessageDigestAlgorithms() {
        // cannot be instantiated.
    }
}
