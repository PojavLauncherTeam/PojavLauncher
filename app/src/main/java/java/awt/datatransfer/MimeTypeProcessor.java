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

package java.awt.datatransfer;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

final class MimeTypeProcessor {

    private static MimeTypeProcessor instance;

    private MimeTypeProcessor() {
        super();
    }

    static MimeType parse(String str) {
        MimeType res;

        if (instance == null) {
            instance = new MimeTypeProcessor();
        }

        res = new MimeType();
        if (str != null) {
            StringPosition pos = new StringPosition();

            retrieveType(str, res, pos);
            retrieveParams(str, res, pos);
        }

        return res;
    }

    static String assemble(MimeType type) {
        StringBuilder buf = new StringBuilder();

        buf.append(type.getFullType());
        for (Enumeration<String> keys = type.parameters.keys(); keys.hasMoreElements();) {
            String name = keys.nextElement();
            String value = type.parameters.get(name);

            buf.append("; "); //$NON-NLS-1$
            buf.append(name);
            buf.append("=\""); //$NON-NLS-1$
            buf.append(value);
            buf.append('"');
        }

        return buf.toString();
    }

    private static void retrieveType(String str, MimeType res, StringPosition pos) {
        res.primaryType = retrieveToken(str, pos).toLowerCase();
        pos.i = getNextMeaningfulIndex(str, pos.i);
        if ((pos.i >= str.length()) || (str.charAt(pos.i) != '/')) {
            throw new IllegalArgumentException();
        }
        pos.i++;
        res.subType = retrieveToken(str, pos).toLowerCase();
    }

    private static void retrieveParams(String str, MimeType res, StringPosition pos) {
        res.parameters = new Hashtable<String, String>();
        res.systemParameters = new Hashtable<String, Object>();
        do {
            pos.i = getNextMeaningfulIndex(str, pos.i);
            if (pos.i >= str.length()) {
                return;
            }
            if (str.charAt(pos.i) != ';') {
                throw new IllegalArgumentException();
            }
            pos.i++;
            retrieveParam(str, res, pos);
        } while (true);
    }

    private static void retrieveParam(String str, MimeType res, StringPosition pos) {
        String name = retrieveToken(str, pos).toLowerCase();

        pos.i = getNextMeaningfulIndex(str, pos.i);
        if ((pos.i >= str.length()) || (str.charAt(pos.i) != '=')) {
            throw new IllegalArgumentException();
        }
        pos.i++;
        pos.i = getNextMeaningfulIndex(str, pos.i);
        if ((pos.i >= str.length())) {
            throw new IllegalArgumentException();
        }
        String value;

        if (str.charAt(pos.i) == '"') {
            value = retrieveQuoted(str, pos);
        } else {
            value = retrieveToken(str, pos);
        }
        res.parameters.put(name, value);
    }

    private static String retrieveQuoted(String str, StringPosition pos) {
        StringBuilder buf = new StringBuilder();
        boolean check = true;

        pos.i++;
        while ((str.charAt(pos.i) != '"') || !check) {
            char c = str.charAt(pos.i++);

            if (!check) {
                check = true;
            } else if (c == '\\') {
                check = false;
            }
            if (check) {
                buf.append(c);
            }
            if (pos.i == str.length()) {
                throw new IllegalArgumentException();
            }
        }
        pos.i++;

        return buf.toString();
    }

    private static String retrieveToken(String str, StringPosition pos) {
        StringBuilder buf = new StringBuilder();

        pos.i = getNextMeaningfulIndex(str, pos.i);
        if ((pos.i >= str.length()) || isTSpecialChar(str.charAt(pos.i))) {
            throw new IllegalArgumentException();
        }
        do {
            buf.append(str.charAt(pos.i++));
        } while ((pos.i < str.length())
                && isMeaningfulChar(str.charAt(pos.i))
                && !isTSpecialChar(str.charAt(pos.i)));

        return buf.toString();
    }

    private static int getNextMeaningfulIndex(String str, int i) {
        while ((i < str.length()) && !isMeaningfulChar(str.charAt(i))) {
            i++;
        }

        return i;
    }

    private static boolean isTSpecialChar(char c) {
        return ((c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '<')
                || (c == '>') || (c == '@') || (c == ',') || (c == ';') || (c == ':')
                || (c == '\\') || (c == '\"') || (c == '/') || (c == '?') || (c == '='));
    }

    private static boolean isMeaningfulChar(char c) {
        return ((c >= '!') && (c <= '~'));
    }

    private static final class StringPosition {

        int i = 0;

    }

    static final class MimeType implements Cloneable, Serializable {

        private static final long serialVersionUID = -6693571907475992044L;
        private String primaryType;
        private String subType;
        private Hashtable<String, String> parameters;
        private Hashtable<String, Object> systemParameters;

        MimeType() {
            primaryType = null;
            subType = null;
            parameters = null;
            systemParameters = null;
        }

        MimeType(String primaryType, String subType) {
            this.primaryType = primaryType;
            this.subType = subType;
            parameters = new Hashtable<String, String>();
            systemParameters = new Hashtable<String, Object>();
        }

        boolean equals(MimeType that) {
            if (that == null) {
                return false;
            }
            return getFullType().equals(that.getFullType());
        }

        String getPrimaryType() {
            return primaryType;
        }

        String getSubType() {
            return subType;
        }

        String getFullType() {
            return (primaryType + "/" + subType); //$NON-NLS-1$
        }

        String getParameter(String name) {
            return parameters.get(name);
        }

        void addParameter(String name, String value) {
            if (value == null) {
                return;
            }
            if ((value.charAt(0) == '\"') 
                    && (value.charAt(value.length() - 1) == '\"')) {
                value = value.substring(1, value.length() - 2);
            }
            if (value.length() == 0) {
                return;
            }
            parameters.put(name, value);
        }

        void removeParameter(String name) {
            parameters.remove(name);
        }

        Object getSystemParameter(String name) {
            return systemParameters.get(name);
        }

        void addSystemParameter(String name, Object value) {
            systemParameters.put(name, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object clone() {
            MimeType clone = new MimeType(primaryType, subType);
            clone.parameters = (Hashtable<String, String>)parameters.clone();
            clone.systemParameters = (Hashtable<String, Object>)systemParameters.clone();
            return clone;
        }
    }

}
