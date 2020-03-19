/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.harmony.misc;

/**
 * Some system utils
 */
public class SystemUtils {
    // Public constants
    // OSes
    public static final int OS_WINDOWS = 1;
    public static final int OS_LINUX = 2;
    public static final int OS_UNKNOWN = -1;

    // Architectures
    public static final int ARC_IA32 = 1;
    public static final int ARC_IA64 = 2;
    public static final int ARC_UNKNOWN = -1;

    // Private fields
    private static int os = 0;
    private static int arc = 0;

    /**
     * getOS method returns on of the operating system codes:
     * OS_WINDOWS, OS_LINUX or OS_UNKNOWN
     * @return Operating system code
     */
    public static int getOS() {
        if (os == 0) {
            String osname = System.getProperty("os.name").substring(0,3);
            if (osname.compareToIgnoreCase("win") == 0) {
                os = OS_WINDOWS;
            } else {
                if (osname.compareToIgnoreCase("lin") == 0) {
                    os = OS_LINUX;
                } else
                    os = OS_UNKNOWN;
            }
        }
        return os;
    }
}
