package net.kdt.pojavlaunch;

public class AccInfo
{
	private String content;
	public static AccInfo create(String path)
	{
		return new AccInfo(path);
	}
	private AccInfo(String path)
	{
		try
		{
			content = Tools.read(path);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	public String getUuid()
	{
		return null;
	}
}
