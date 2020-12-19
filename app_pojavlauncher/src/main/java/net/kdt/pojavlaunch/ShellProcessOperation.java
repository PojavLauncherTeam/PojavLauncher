package net.kdt.pojavlaunch;

import java.io.*;
import android.app.*;

public class ShellProcessOperation
{
	private OnPrintListener listener;
	private Process process;

	public ShellProcessOperation(OnPrintListener listener) throws IOException {
		this.listener = listener;
		process = Runtime.getRuntime().exec("/system/bin/sh");
	}
	
	public ShellProcessOperation(OnPrintListener listener, String command) throws IOException {
		this.listener = listener;
		process = Runtime.getRuntime().exec(
			command
		); //"/system/bin/sh -c \"" + command + "\"");
	}
	
	public void writeToProcess(String[] cmdArr) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String cmd : cmdArr) {sb.append(cmd + " ");}
		writeToProcess(sb.toString());
	}
	
	public void writeToProcess(String cmd) throws IOException {
		// listener.onPrintLine(" > " + cmd + "\n");
		
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes(cmd + "\n");
		os.flush();
	}
	
	public void initInputStream(Activity ctx) {
		ctx.runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					printStream(process.getInputStream());
					printStream(process.getErrorStream());
				}
			});
	}
	
	public int exitCode() {
		return process.exitValue();
	}
	
	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}
	
	public int exit() throws InterruptedException, IOException {
		writeToProcess("exit");
		return waitFor();
	}
	
	private void printStream(final InputStream stream) {
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try {
						BufferedReader buffStream = new BufferedReader(new InputStreamReader(stream));
						String line = null;
						while ((line = buffStream.readLine()) != null) {
							listener.onPrintLine(line + "\n");
						}
					} catch (Exception e) {
						e.printStackTrace();
						listener.onPrintLine("PrintStream error: " + e.getMessage() + "\n");
					}
				}
			}).start();
	}
	
	public static interface OnPrintListener {
		public void onPrintLine(String text);
	}
}
