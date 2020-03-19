package net.kdt.pojavlaunch.exit;

public class ExitTrappedException extends RuntimeException
{
	public ExitTrappedException() {
		super();
	}
	
	public ExitTrappedException(String message) {
		super(message);
	}
}
