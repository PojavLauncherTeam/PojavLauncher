package net.kdt.pojavlaunch;
import java.io.*;

public class LoggerJava
{
	public static class LoggerDXPrintStream extends PrintStream {
		private OnCharPrintListener mListener;
		private StringBuilder mCurrText = new StringBuilder();
        public LoggerDXPrintStream(OutputStream out, OnCharPrintListener listener)
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
	
	public static interface OnCharPrintListener {
		public void onCharPrint(String s);
	}
	
	public static class LoggerOutputStream extends FilterOutputStream
    {
		private OnCharPrintListener mListener;
        public LoggerOutputStream(OutputStream out, OnCharPrintListener listener)
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
    }
	
}
