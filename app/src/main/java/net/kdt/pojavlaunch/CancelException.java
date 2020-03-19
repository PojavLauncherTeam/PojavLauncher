package net.kdt.pojavlaunch;

public class CancelException extends RuntimeException
{
	@Override
	public CancelException() {
		super();
	}
	
	@Override
	public CancelException(String msg) {
		super(msg);
	}
}
