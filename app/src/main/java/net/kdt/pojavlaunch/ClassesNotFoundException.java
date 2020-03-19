package net.kdt.pojavlaunch;

public class ClassesNotFoundException extends ClassNotFoundException
{
	private static String mMessage = "Failed to find a class from classes list";
	public ClassesNotFoundException()
	{
		super(mMessage);
	}
	public ClassesNotFoundException(String message)
	{
		super(message + ":" + mMessage);
	}
}
