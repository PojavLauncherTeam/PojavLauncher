package net.kdt.pojavlaunch;
import java.io.*;

public class LoggerJava
{
	public static interface OnCharPrintListener {
		public void onCharPrint(char c);
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
			mListener.onCharPrint((char) charCode);
        }
    }
	
}
