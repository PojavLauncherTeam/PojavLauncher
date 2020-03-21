package net.kdt.pojavlaunch;
import java.io.*;

public class LoggerJava
{
	public static class LoggerDXPrintStream extends PrintStream {
		private OnStringPrintListener mListener;
        public LoggerDXPrintStream(OutputStream out, OnStringPrintListener listener)
        {
            super(out);
			mListener = listener;
        }

		@Override
		public void print(String s)
		{
			super.print(s);
			if (mListener != null) mListener.onCharPrint(s);
		}
		
		@Override
		public void println(String s)
		{
			super.println(s);
			if (mListener != null) mListener.onCharPrint(s + "\n");
		}
	}
	
	public static interface OnStringPrintListener {
		public void onCharPrint(String s);
	}
	
	public static class LoggerOutputStream extends FilterOutputStream
    {
		private OnStringPrintListener mListener;
        public LoggerOutputStream(OutputStream out, OnStringPrintListener listener)
        {
            super(out);
			mListener = listener;
        }

        @Override
        public void write(final int charCode) throws IOException
        {
            super.write(charCode);
			mListener.onCharPrint(Character.toString((char) charCode));
        }
/*
		@Override
		public void write(byte[] b) throws IOException
		{
			super.write(b);
			mListener.onCharPrint(
		}
*/
    }
	
}
