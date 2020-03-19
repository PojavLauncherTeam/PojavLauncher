package com.kdt.mojangauth;

import android.os.*;
import com.kdt.mojangauth.yggdrasil.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;

public class LoginTask extends AsyncTask<String, Void, String[]>
{
	private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
	//private String TAG = "MojangAuth-login";
	private LoginListener listener;
	
	public LoginTask setLoginListener(LoginListener listener)
	{
		this.listener = listener;
		return this;
	}
	private UUID getRandomUUID()
	{
		return UUID.randomUUID();
	}
	@Override
	protected void onPreExecute()
	{
		listener.onBeforeLogin();
		
		super.onPreExecute();
	}
	@Override
	protected String[] doInBackground(String[] args)
	{
		ArrayList<String> str = new ArrayList<String>();
		str.add("ERROR");
		try{
			try{
				AuthenticateResponse response = authenticator.authenticate(args[0], args[1], getRandomUUID());
				if(response.selectedProfile == null){
					str.add("Can't login a demo account!\n");
				}
				else{
					if(new File(Tools.mpProfiles + "/" + response.selectedProfile.name).exists()){
						str.add("This account already exist!\n");
					}
					else{
						str.add(response.accessToken);            // Access token
						str.add(response.clientToken.toString()); // Client token
						str.add(response.selectedProfile.id);     // Profile ID
						str.add(response.selectedProfile.name);   // Username
						str.set(0, "NORMAL");
					}
				}
			}
				//MainActivity.updateStatus(804);
			catch(Throwable e){
				str.add(e.getMessage());
			}
		}
		catch(Exception e){
			str.add(e.getMessage());
		}
		return str.toArray(new String[0]);
	}
	@Override
	protected void onPostExecute(String[] result)
	{
		listener.onLoginDone(result);
		super.onPostExecute(result);
	}
}
