package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.util.*;
import android.view.*;
import com.kdt.mojangauth.*;
import java.io.*;

public class MCProfile
{
	private static String[] emptyBuilder = new String[]{
		"1.9", //Version
		"ProfileIDEmpty",
		"AccessToken",
		"AccessTokenEmpty",
		"Steve"
	};
	
	public static void launch(Activity ctx, Object o)
	{
		PojavProfile.setCurrentProfile(ctx, o);
		
		Intent intent = new Intent(ctx, PojavV2ActivityManager.getLauncherRemakeVer(ctx)); //MCLauncherActivity.class);
		ctx.startActivity(intent);
	}
	
	public static void updateTokens(final Activity ctx, final String pofFilePath, RefreshListener listen) throws Exception
	{
		new RefreshTokenTask(ctx, listen).execute(pofFilePath);
	}
	
	public static String build(MCProfile.Builder builder)
	{
		//System.out.println("build THE VER = " + builder.getVersion());
		
		try {
			byte[] bFull = toString(builder).getBytes("UTF-8");
			Tools.write(Tools.mpProfiles + "/" + builder.getUsername(), bFull);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Tools.mpProfiles + "/" + builder.getUsername();
	}
	
	public static MCProfile.Builder load(String pofFilePath)
	{
		try {
			//String th = new String(new byte[]{-128});
			String pofContent = Tools.read(pofFilePath);
			return parse(pofContent);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load Profile " + pofFilePath, e);
		}
	}
	
	public static MCProfile.Builder parse(String content) {
		MCProfile.Builder builder = new MCProfile.Builder();

		String[] profileInfos = content.split(":");

		String cltk = profileInfos[0];
		String prtk = profileInfos[1];
		String acct = profileInfos[2];
		String name = profileInfos[3];
		String vers = profileInfos[4];
		String isAc = profileInfos[5];
		
		//System.out.println("parse THE VER = " + vers);

		builder.setClientID(cltk);
		builder.setProfileID(prtk);
		builder.setAccessToken(acct);
		builder.setUsername(name);
		builder.setVersion(vers);
		builder.setIsMojangAccount(Boolean.parseBoolean(isAc));

		return builder;
	}
	
	public static MCProfile.Builder loadSafety(String pofFilePath) {
		try {
			return load(pofFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			
			// return new MCProfile.Builder();
			return null;
		}
	}

	public static String toString(String pofFilePath) {
		return toString(load(pofFilePath));
	}
	
	public static String toString(MCProfile.Builder builder) {
		return
			builder.getClientID() + ":" +
			builder.getProfileID() + ":" +
			builder.getAccessToken() + ":" +
			builder.getUsername() + ":" +
			builder.getVersion() + ":" +
			Boolean.toString(builder.isMojangAccount());
	}
	
	public static class Builder implements Serializable
	{
		private String[] fullArgs = new String[6];
		private boolean isMojangAccount = true;
		
		public Builder()
		{
			fullArgs = emptyBuilder;
			setClientID("0");
			setProfileID("00000000-0000-0000-0000-000000000000");
			setAccessToken("0");
		}
		
		public boolean isMojangAccount()
		{
			return isMojangAccount;
		}
		
		public String getVersion()
		{
			return fullArgs[0];
		}
		
		public String getClientID()
		{
			return fullArgs[1];
		}
		
		public String getProfileID()
		{
			return fullArgs[2];
		}
		
		public String getAccessToken()
		{
			return fullArgs[3];
		}
		
		public String getUsername()
		{
			return fullArgs[4];
		}
		
		public void setIsMojangAccount(boolean value)
		{
			isMojangAccount = value;
		}
		
		public void setVersion(String value)
		{
			fullArgs[0] = value;
		}
		
		public void setClientID(String value)
		{
			fullArgs[1] = value;
		}
		
		public void setProfileID(String value)
		{
			fullArgs[2] = value;
		}
		
		public void setAccessToken(String value)
		{
			fullArgs[3] = value;
		}
		
		public void setUsername(String value)
		{
			fullArgs[4] = value;
		}
	}
}
