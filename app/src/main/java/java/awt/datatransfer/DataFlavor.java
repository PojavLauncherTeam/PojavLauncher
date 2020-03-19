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
/**
 * @author Michael Danilov
 * @version $Revision$
 */
package java.awt.datatransfer;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import org.apache.harmony.awt.internal.nls.Messages;


public class DataFlavor implements Externalizable, Cloneable {

    private static final long serialVersionUID = 8367026044764648243L;

    /**
     * @deprecated
     */
    @Deprecated
    public static final DataFlavor plainTextFlavor =
	new DataFlavor("text/plain; charset=unicode; class=java.io.InputStream", //$NON-NLS-1$
				   "Plain Text"); //$NON-NLS-1$

    public static final DataFlavor stringFlavor =
	new DataFlavor("application/x-java-serialized-object; class=java.lang.String", //$NON-NLS-1$
				   "Unicode String"); //$NON-NLS-1$

	/* public static final DataFlavor imageFlavor =
	 new DataFlavor("image/x-java-image; class=java.awt.Image", //$NON-NLS-1$
	 "Image"); //$NON-NLS-1$
	 */
    public static final DataFlavor javaFileListFlavor =
	new DataFlavor("application/x-java-file-list; class=java.util.List", //$NON-NLS-1$
				   "application/x-java-file-list"); //$NON-NLS-1$

    public static final String javaJVMLocalObjectMimeType =
	"application/x-java-jvm-local-objectref"; //$NON-NLS-1$

    public static final String javaRemoteObjectMimeType =
	"application/x-java-remote-object"; //$NON-NLS-1$

    public static final String javaSerializedObjectMimeType =
	"application/x-java-serialized-object"; //$NON-NLS-1$

    private static final String sortedTextFlavors[] = {
		"text/sgml", //$NON-NLS-1$
		"text/xml", //$NON-NLS-1$
		"text/html", //$NON-NLS-1$
		"text/rtf", //$NON-NLS-1$
		"text/enriched", //$NON-NLS-1$
		"text/richtext", //$NON-NLS-1$
		"text/uri-list", //$NON-NLS-1$
		"text/tab-separated-values", //$NON-NLS-1$
		"text/t140" , //$NON-NLS-1$
		"text/rfc822-headers", //$NON-NLS-1$
		"text/parityfec", //$NON-NLS-1$
		"text/directory", //$NON-NLS-1$
		"text/css", //$NON-NLS-1$
		"text/calendar", //$NON-NLS-1$
		"application/x-java-serialized-object", //$NON-NLS-1$
		"text/plain" //$NON-NLS-1$
    };

    private static DataFlavor plainUnicodeFlavor = null;

    private String humanPresentableName;
    private Class<?> representationClass;
    private MimeTypeProcessor.MimeType mimeInfo;

    public static final DataFlavor getTextPlainUnicodeFlavor() {
        if (plainUnicodeFlavor == null) {
            plainUnicodeFlavor = new DataFlavor("text/plain" //$NON-NLS-1$
												+ "; charset=unicode"// + DTK.getDTK().getDefaultCharset() //$NON-NLS-1$
												+ "; class=java.io.InputStream", //$NON-NLS-1$
												"Plain Text"); //$NON-NLS-1$
        }

        return plainUnicodeFlavor;
    }

    protected static final Class<?> tryToLoadClass(String className, ClassLoader fallback)
	throws ClassNotFoundException
    {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e0) {
            try {
                return ClassLoader.getSystemClassLoader().loadClass(className);
            } catch (ClassNotFoundException e1) {
                ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

                if (contextLoader != null) {
                    try {
                        return contextLoader.loadClass(className);
                    } catch (ClassNotFoundException e2) {
                    }
                }

                return fallback.loadClass(className);
            }
        }
    }

    private static boolean isCharsetSupported(String charset) {
        try {
            return Charset.isSupported(charset);
        } catch (IllegalCharsetNameException e) {
            return false;
        }
    }

    public DataFlavor() {
        mimeInfo = null;
        humanPresentableName = null;
        representationClass = null;
    }

    public DataFlavor(Class<?> representationClass, String humanPresentableName) {
        mimeInfo = new MimeTypeProcessor.MimeType("application", "x-java-serialized-object"); //$NON-NLS-1$ //$NON-NLS-2$

        if (humanPresentableName != null) {
            this.humanPresentableName = humanPresentableName;
        } else {
            this.humanPresentableName = "application/x-java-serialized-object"; //$NON-NLS-1$
        }

        mimeInfo.addParameter("class", representationClass.getName()); //$NON-NLS-1$
        this.representationClass = representationClass;
    }

    public DataFlavor(String mimeType, String humanPresentableName) {
        try {
            init(mimeType, humanPresentableName, null);
        } catch (ClassNotFoundException e) {
            // awt.16C=Can't load class: {0}
            throw new IllegalArgumentException(Messages.getString("awt.16C", mimeInfo.getParameter("class")),e);  //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public DataFlavor(String mimeType) throws ClassNotFoundException {
        init(mimeType, null, null);
    }

    public DataFlavor(String mimeType, String humanPresentableName, ClassLoader classLoader)
	throws ClassNotFoundException
    {
        init(mimeType, humanPresentableName, classLoader);
    }

    private void init(String mimeType, String humanPresentableName, ClassLoader classLoader)
	throws ClassNotFoundException
    {
        String className;

        try {
            mimeInfo = MimeTypeProcessor.parse(mimeType);
        } catch (IllegalArgumentException e) {
            // awt.16D=Can't parse MIME type: {0}
            throw new IllegalArgumentException(Messages.getString("awt.16D", mimeType)); //$NON-NLS-1$
        }

        if (humanPresentableName != null) {
            this.humanPresentableName = humanPresentableName;
        } else {
            this.humanPresentableName = mimeInfo.getPrimaryType() + '/' + mimeInfo.getSubType();
        }

        className = mimeInfo.getParameter("class"); //$NON-NLS-1$
        if (className == null) {
            className = "java.io.InputStream"; //$NON-NLS-1$
            mimeInfo.addParameter("class", className); //$NON-NLS-1$
        }
        representationClass = (classLoader == null) ?
			Class.forName(className) :
			classLoader.loadClass(className);
    }

    private String getCharset() {
        if ((mimeInfo == null) || isCharsetRedundant()) {
            return ""; //$NON-NLS-1$
        }
        String charset = mimeInfo.getParameter("charset"); //$NON-NLS-1$

        if (isCharsetRequired() && ((charset == null) || (charset.length() == 0))) {
            return "unicode";
        }
        if (charset == null) {
            return ""; //$NON-NLS-1$
        }

        return charset;
    }

    private boolean isCharsetRequired() {
        String type = mimeInfo.getFullType();

        return (type.equals("text/sgml") || //$NON-NLS-1$
			type.equals("text/xml") || //$NON-NLS-1$
			type.equals("text/html") || //$NON-NLS-1$
			type.equals("text/enriched") || //$NON-NLS-1$
			type.equals("text/richtext") || //$NON-NLS-1$
			type.equals("text/uri-list") || //$NON-NLS-1$
			type.equals("text/directory") || //$NON-NLS-1$
			type.equals("text/css") || //$NON-NLS-1$
			type.equals("text/calendar") || //$NON-NLS-1$
			type.equals("application/x-java-serialized-object") || //$NON-NLS-1$
			type.equals("text/plain")); //$NON-NLS-1$
    }

    private boolean isCharsetRedundant() {
        String type = mimeInfo.getFullType();

        return (type.equals("text/rtf") || //$NON-NLS-1$
			type.equals("text/tab-separated-values") || //$NON-NLS-1$
			type.equals("text/t140") || //$NON-NLS-1$
			type.equals("text/rfc822-headers") || //$NON-NLS-1$
			type.equals("text/parityfec")); //$NON-NLS-1$
    }

    MimeTypeProcessor.MimeType getMimeInfo() {
        return mimeInfo;
    }

    public String getPrimaryType() {
        return (mimeInfo != null) ? mimeInfo.getPrimaryType() : null;
    }

    public String getSubType() {
        return (mimeInfo != null) ? mimeInfo.getSubType() : null;
    }

    public String getMimeType() {
        return (mimeInfo != null) ? MimeTypeProcessor.assemble(mimeInfo) : null;
    }

    public String getParameter(String paramName) {
        String lowerName = paramName.toLowerCase();

        if (lowerName.equals("humanpresentablename")) { //$NON-NLS-1$
            return humanPresentableName;
        }
        return mimeInfo != null ? mimeInfo.getParameter(lowerName) : null;
    }

    public String getHumanPresentableName() {
        return humanPresentableName;
    }

    public void setHumanPresentableName(String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }

    public Class<?> getRepresentationClass() {
        return representationClass;
    }

    public final Class<?> getDefaultRepresentationClass() {
        return InputStream.class;
    }

    public final String getDefaultRepresentationClassAsString() {
        return getDefaultRepresentationClass().getName();
    }

    public boolean isRepresentationClassSerializable() {
        return Serializable.class.isAssignableFrom(representationClass);
    }

    public boolean isRepresentationClassRemote() {
        // Code should be enabled when RMI is supported
        // return java.rmi.Remote.class.isAssignableFrom(representationClass);
        return false;
    }

    public boolean isRepresentationClassReader() {
        return Reader.class.isAssignableFrom(representationClass);
    }

    public boolean isRepresentationClassInputStream() {
        return InputStream.class.isAssignableFrom(representationClass);
    }

    public boolean isRepresentationClassCharBuffer() {
        return CharBuffer.class.isAssignableFrom(representationClass);
    }

    public boolean isRepresentationClassByteBuffer() {
        return ByteBuffer.class.isAssignableFrom(representationClass);
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
        return parameterValue;
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected String normalizeMimeType(String mimeType) {
        return mimeType;
    }

    public final boolean isMimeTypeEqual(DataFlavor dataFlavor) {
        return mimeInfo != null ? mimeInfo.equals(dataFlavor.mimeInfo)
			: (dataFlavor.mimeInfo == null);
    }

    public boolean isMimeTypeEqual(String mimeType) {
        try {
            return mimeInfo.equals(MimeTypeProcessor.parse(mimeType));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public synchronized void writeExternal(ObjectOutput os) throws IOException {
        os.writeObject(humanPresentableName);
        os.writeObject(mimeInfo);
    }

    public synchronized void readExternal(ObjectInput is)
	throws IOException, ClassNotFoundException
    {
        humanPresentableName = (String) is.readObject();
        mimeInfo = (MimeTypeProcessor.MimeType) is.readObject();

        representationClass = (mimeInfo != null) ?
			Class.forName(mimeInfo.getParameter("class")) : null; //$NON-NLS-1$
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DataFlavor clone = new DataFlavor();

        clone.humanPresentableName = humanPresentableName;
        clone.representationClass = representationClass;
        clone.mimeInfo = (mimeInfo != null) ? (MimeTypeProcessor.MimeType)
			mimeInfo.clone() : null;

        return clone;
    }

    @Override
    public String toString() {
        /* The format is based on 1.5 release behavior 
         * which can be revealed by the following code:
         * 
         * System.out.println(DataFlavor.imageFlavor.toString());
         */

        return (getClass().getName()
			+ "[MimeType=(" + getMimeType() //$NON-NLS-1$
			+ ");humanPresentableName=" + humanPresentableName + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isMimeTypeSerializedObject() {
        return isMimeTypeEqual(javaSerializedObjectMimeType);
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof DataFlavor)) {
            return false;
        }
        return equals((DataFlavor) o);
    }

    public boolean equals(DataFlavor that) {
        if (that == this) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (mimeInfo == null) {
            return (that.mimeInfo == null);
        }
        if (!(mimeInfo.equals(that.mimeInfo) &&
			representationClass.equals(that.representationClass)))
        {
            return false;
        }
        if (!mimeInfo.getPrimaryType().equals("text") || isUnicodeFlavor()) { //$NON-NLS-1$
            return true;
        }

        String charset1 = getCharset();
        String charset2 = that.getCharset();

        if (!isCharsetSupported(charset1) || !isCharsetSupported(charset2)) {
            return charset1.equalsIgnoreCase(charset2);
        }
        return (Charset.forName(charset1).equals(Charset.forName(charset2)));

    }

    @Deprecated
    public boolean equals(String s) {
		if (s == null) {
			return false;
		}

		return isMimeTypeEqual(s);
    }

    public boolean match(DataFlavor that) {
        return equals(that);
    }

    @Override
    public int hashCode() {
        return getKeyInfo().hashCode();
    }

    private String getKeyInfo() {
        String key = mimeInfo.getFullType() + ";class=" + representationClass.getName(); //$NON-NLS-1$

        if (!mimeInfo.getPrimaryType().equals("text") || isUnicodeFlavor()) { //$NON-NLS-1$
            return key;
        }

        return key + ";charset=" + getCharset().toLowerCase(); //$NON-NLS-1$
    }

    public boolean isFlavorSerializedObjectType() {
        return (isMimeTypeSerializedObject()
			&& isRepresentationClassSerializable());
    }

    public boolean isFlavorRemoteObjectType() {
        return (isMimeTypeEqual(javaRemoteObjectMimeType)
			&& isRepresentationClassRemote());
    }

    public boolean isFlavorJavaFileListType() {
        return (java.util.List.class.isAssignableFrom(representationClass) &&
			isMimeTypeEqual(javaFileListFlavor));
    }

    public boolean isFlavorTextType() {
        if (equals(stringFlavor) || equals(plainTextFlavor)) {
            return true;
        }
        if ((mimeInfo != null) && !mimeInfo.getPrimaryType().equals("text")) { //$NON-NLS-1$
            return false;
        }

        String charset = getCharset();

        if (isByteCodeFlavor()) {
            if (charset.length() != 0) {
                return isCharsetSupported(charset);
            }

            return true;
        }

        return isUnicodeFlavor();
    }

    public Reader getReaderForText(Transferable transferable)
	throws UnsupportedFlavorException, IOException
    {
        Object data = transferable.getTransferData(this);

        if (data == null) {
            // awt.16E=Transferable has null data
            throw new IllegalArgumentException(Messages.getString("awt.16E")); //$NON-NLS-1$
        }

        if (data instanceof Reader) {
            Reader reader = (Reader) data;
            reader.reset();
            return reader;
        } else if (data instanceof String) {
            return new StringReader((String) data);
        } else if (data instanceof CharBuffer) {
            return new CharArrayReader(((CharBuffer) data).array());
        } else if (data instanceof char[]) {
            return new CharArrayReader((char[]) data);
        } else {
            String charset = getCharset();
            InputStream stream;

            if (data instanceof InputStream) {
                stream = (InputStream) data;
                stream.reset();
            } else if (data instanceof ByteBuffer) {
                stream = new ByteArrayInputStream((((ByteBuffer) data).array()));
            } else if (data instanceof byte[]) {
                stream = new ByteArrayInputStream((byte[]) data);
            } else {
                // awt.16F=Can't create reader for this representation class
                throw new IllegalArgumentException(Messages.getString("awt.16F")); //$NON-NLS-1$
            }

            if (charset.length() == 0) {
                return new InputStreamReader(stream);
            }
            return new InputStreamReader(stream, charset);
        }
    }

    public static final DataFlavor selectBestTextFlavor(DataFlavor[] availableFlavors) {
        if (availableFlavors == null) {
            return null;
        }

        List<List<DataFlavor>> sorted = sortTextFlavorsByType(new LinkedList<DataFlavor>(Arrays.asList(availableFlavors)));

        if (sorted.isEmpty()) {
            return null;
        }

        List<DataFlavor> bestSorted = sorted.get(0);

        if (bestSorted.size() == 1) {
            return bestSorted.get(0);
        }

        if (bestSorted.get(0).getCharset().length() == 0) {
            return selectBestFlavorWOCharset(bestSorted);
        }
        return selectBestFlavorWCharset(bestSorted);
    }

    private static DataFlavor selectBestFlavorWCharset(List<DataFlavor> list) {
        List<DataFlavor> best;

        best = getFlavors(list, Reader.class);
        if (best != null) {
            return best.get(0);
        }
        best = getFlavors(list, String.class);
        if (best != null) {
            return best.get(0);
        }
        best = getFlavors(list, CharBuffer.class);
        if (best != null) {
            return best.get(0);
        }
        best = getFlavors(list, char[].class);
        if (best != null) {
            return best.get(0);
        }

        return selectBestByCharset(list);
    }

    private static DataFlavor selectBestByCharset(List<DataFlavor> list) {
        List<DataFlavor> best;

        best = getFlavors(list, new String[] {"UTF-16", "UTF-8", "UTF-16BE", "UTF-16LE"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        if (best == null) {
            best = getFlavors(list, new String[] {"unicode"});
            if (best == null) {
                best = getFlavors(list, new String[] {"US-ASCII"}); //$NON-NLS-1$
                if (best == null) {
                    best = selectBestByAlphabet(list);
                }
            }
        }

        if (best != null) {
            if (best.size() == 1) {
                return best.get(0);
            }
            return selectBestFlavorWOCharset(best);
        }

        return null;
    }

    private static List<DataFlavor> selectBestByAlphabet(List<DataFlavor> list) {
        String charsets[] = new String[list.size()];
        LinkedList<DataFlavor> best = new LinkedList<DataFlavor>();

        for (int i = 0; i < charsets.length; i++) {
            charsets[i] = list.get(i).getCharset();
        }
        Arrays.sort(charsets, String.CASE_INSENSITIVE_ORDER);

        for (DataFlavor flavor : list) {
            if (charsets[0].equalsIgnoreCase(flavor.getCharset())) {
                best.add(flavor);
            }
        }

        return best.isEmpty() ? null : best;
    }

    private static List<DataFlavor> getFlavors(List<DataFlavor> list, String[] charset) {
        LinkedList<DataFlavor> sublist = new LinkedList<DataFlavor>();

        for (Iterator<DataFlavor> i = list.iterator(); i.hasNext();) {
            DataFlavor flavor = i.next();

            if (isCharsetSupported(flavor.getCharset())) {
                for (String element : charset) {
                    if (Charset.forName(element).equals(Charset.forName(flavor.getCharset()))) {
                        sublist.add(flavor);
                    }
                }
            } else {
                i.remove();
            }
        }

        return sublist.isEmpty() ? null : list;
    }

    private static DataFlavor selectBestFlavorWOCharset(List<DataFlavor> list) {
        List<DataFlavor> best;

        best = getFlavors(list, InputStream.class);
        if (best != null) {
            return best.get(0);
        }
        best = getFlavors(list, ByteBuffer.class);
        if (best != null) {
            return best.get(0);
        }
        best = getFlavors(list, byte[].class);
        if (best != null) {
            return best.get(0);
        }

        return list.get(0);
    }

    private static List<DataFlavor> getFlavors(List<DataFlavor> list, Class<?> klass) {
        LinkedList<DataFlavor> sublist = new LinkedList<DataFlavor>();

        for (DataFlavor flavor : list) {
            if (flavor.representationClass.equals(klass)) {
                sublist.add(flavor);
            }
        }

        return sublist.isEmpty() ? null : list;
    }

    private static List<List<DataFlavor>> sortTextFlavorsByType(List<DataFlavor> availableFlavors) {
        LinkedList<List<DataFlavor>> list = new LinkedList<List<DataFlavor>>();

        for (String element : sortedTextFlavors) {
            List<DataFlavor> subList = fetchTextFlavors(availableFlavors, element);

            if (subList != null) {
                list.addLast(subList);
            }
        }
        if (!availableFlavors.isEmpty()) {
            list.addLast(availableFlavors);
        }

        return list;
    }

    private static List<DataFlavor> fetchTextFlavors(List<DataFlavor> availableFlavors, String mimeType) {
        LinkedList<DataFlavor> list = new LinkedList<DataFlavor>();

        for (Iterator<DataFlavor> i = availableFlavors.iterator(); i.hasNext();) {
            DataFlavor flavor = i.next();

            if (flavor.isFlavorTextType()) {
                if (flavor.mimeInfo.getFullType().equals(mimeType)) {
                    if (!list.contains(flavor)) {
                        list.add(flavor);
                    }
                    i.remove();
                }
            } else {
                i.remove();
            }
        }

        return list.isEmpty() ? null : list;
    }

    private boolean isUnicodeFlavor() {
        return (representationClass != null) 
			&& (representationClass.equals(Reader.class) 
			|| representationClass.equals(String.class) 
			|| representationClass.equals(CharBuffer.class) 
			|| representationClass.equals(char[].class));
    }

    private boolean isByteCodeFlavor() {
        return (representationClass != null) 
			&& (representationClass.equals(InputStream.class)
			|| representationClass.equals(ByteBuffer.class)
			|| representationClass.equals(byte[].class));
    }

}
