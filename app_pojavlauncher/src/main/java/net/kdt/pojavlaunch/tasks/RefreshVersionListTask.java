package net.kdt.pojavlaunch.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import androidx.appcompat.widget.*;

import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.extra.ExtraListener;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.profiles.ProfileConstants;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.PerVersionConfig;

import androidx.appcompat.widget.PopupMenu;

public class RefreshVersionListTask extends AsyncTask<Void, Void, ArrayList<String>>
{
    private BaseLauncherActivity mActivity;
    public RefreshVersionListTask(BaseLauncherActivity activity) {
        mActivity = activity;
    }
    
    @Override
    protected ArrayList<String> doInBackground(Void[] p1)
    {
        try {
            //mActivity.mVersionList = Tools.GLOBAL_GSON.fromJson(DownloadUtils.downloadString(""), JMinecraftVersionList.class);
            {
                ArrayList<JMinecraftVersionList.Version> versions = new ArrayList<>();
                String[] repositories = LauncherPreferences.PREF_VERSION_REPOS.split(";");
                for (String url : repositories) {
                    JMinecraftVersionList list;
                    Log.i("ExtVL", "Syncing to external: " + url);
                    list = Tools.GLOBAL_GSON.fromJson(DownloadUtils.downloadString(url), JMinecraftVersionList.class);
                    Log.i("ExtVL","Downloaded the version list, len="+list.versions.length);
                    if(list.latest != null && ExtraCore.getValue(ProfileConstants.RELEASE_TABLE) == null)
                        ExtraCore.setValue(ProfileConstants.RELEASE_TABLE,list.latest);
                    Collections.addAll(versions,list.versions);
                }
                mActivity.mVersionList = new JMinecraftVersionList();
                mActivity.mVersionList.versions = versions.toArray(new JMinecraftVersionList.Version[versions.size()]);
                Log.i("ExtVL","Final list size: " + mActivity.mVersionList.versions.length);
            }

            return filter(mActivity.mVersionList.versions, new File(Tools.DIR_HOME_VERSION).listFiles());
        } catch (Exception e){
            System.out.println("Refreshing version list failed !");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        super.onPostExecute(result);
        ExtraCore.setValue(ProfileConstants.VERSION_LIST,result);
    }
    
    private ArrayList<String> filter(JMinecraftVersionList.Version[] list1, File[] list2) {
        ArrayList<String> output = new ArrayList<String>();

        for (JMinecraftVersionList.Version value1: list1) {
            if ((value1.type.equals("release") && LauncherPreferences.PREF_VERTYPE_RELEASE) ||
                (value1.type.equals("snapshot") && LauncherPreferences.PREF_VERTYPE_SNAPSHOT) ||
                (value1.type.equals("old_alpha") && LauncherPreferences.PREF_VERTYPE_OLDALPHA) ||
                (value1.type.equals("old_beta") && LauncherPreferences.PREF_VERTYPE_OLDBETA) ||
                (value1.type.equals("modified"))) {
                output.add(value1.id);
            }
        }

        if(list2 != null) for (File value2: list2) {
            if (!output.contains(value2.getName())) {
                output.add(value2.getName());
            }
        }

        return output;
    }
    
    public static int selectAt(String[] strArr, String select) {
        int count = 0;
        for(String str : strArr){
            if (str.equals(select)) {
                return count;
            }
            count++;
        }
        return -1;
	}
    public static int selectAt(List<String> strArr, String select) {
        int count = 0;
        for(String str : strArr){
            if (str.equals(select)) {
                return count;
            }
            count++;
        }
        return -1;
    }
}
