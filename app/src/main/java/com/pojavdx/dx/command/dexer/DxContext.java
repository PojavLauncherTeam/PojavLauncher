package com.pojavdx.dx.command.dexer;

import com.pojavdx.dx.dex.cf.CodeStatistics;
import com.pojavdx.dx.dex.cf.OptimizerOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import net.kdt.pojavlaunch.*;
import java.io.*;
import android.util.*;

/**
 * State used by a single invocation of {@link Main}.
 */
public class DxContext {
    public final CodeStatistics codeStatistics = new CodeStatistics();
    public final OptimizerOptions optimizerOptions = new OptimizerOptions();
    public final PrintStream out;
    public final PrintStream err;

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    final PrintStream noop = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // noop;
        }
    });

    public DxContext(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public DxContext() {
        this(new LogPrintStream(System.out), new LogPrintStream(System.err));
    }
	
	public static class LogPrintStream extends PrintStream {
		public LogPrintStream(PrintStream parent) {
			super(parent);
		}

		@Override
		public void println(String x) {
			super.println(x);
			PojavDXManager.call(x);
		}

		@Override
		public void println(Object x) {
			super.println(x);
			PojavDXManager.call(x.toString());
		}
	}
}
