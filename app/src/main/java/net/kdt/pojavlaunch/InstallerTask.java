package net.kdt.pojavlaunch;
import android.os.*;

public class InstallerTask extends AsyncTask<String, Void, String>
{
	@Override
	protected String doInBackground(String[] p1)
	{
		try
		{
			downloadLibraries(p1[0]);
			dexMinecraftLibs();
			downloadMinecraft(p1[0]);
			dexMinecraftClient(p1[0]);
			downloadAssets(p1[0]);
		}
		catch (Exception e)
		{
			return e.getMessage();
		}
		return null;
	}
	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		
		if(result == null){
			//No errors
		}
	}
	private void downloadLibraries(String versionName) throws Exception
	{
		
	}
	private void dexMinecraftLibs() throws Exception
	{
		
	}
	private void downloadMinecraft(String versionName) throws Exception
	{
		
	}
	private void dexMinecraftClient(String version) throws Exception
	{

	}
	private void downloadAssets(String versionName) throws Exception
	{
		
	}
}
