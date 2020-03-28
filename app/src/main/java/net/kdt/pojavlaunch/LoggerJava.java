package net.kdt.pojavlaunch;
import java.io.*;

public class LoggerJava
{
	public static interface OnStringPrintListener {
		public void onCharPrint(char c);
	}
	
	public static class LoggerOutputStream extends FilterOutputStream
    {
		private OnStringPrintListener mListener;
		private PrintStream mPrinter;
        public LoggerOutputStream(PrintStream out, OnStringPrintListener listener) {
            super(out);
			mPrinter = out;
			mListener = listener;
        }

        @Override
        public void write(final int charCode) throws IOException {
            super.write(charCode);
			mListener.onCharPrint((char) charCode);
        }
		
		public PrintStream getRootStream() {
			return mPrinter;
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
