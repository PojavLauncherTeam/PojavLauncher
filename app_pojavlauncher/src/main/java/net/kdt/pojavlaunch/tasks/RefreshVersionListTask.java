package net.kdt.pojavlaunch.tasks;

import android.os.*;
import androidx.appcompat.widget.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;
import net.kdt.pojavlaunch.value.launcherprofiles.VersionProfileAdapter;

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
            mActivity.mVersionStringList = filter(null,new File(Tools.DIR_HOME_VERSION).listFiles());
            if(mActivity.mProfileEditView != null) {
                mActivity.mProfileEditView.refreshVersions();
            }
            mActivity.mVersionList = Tools.GLOBAL_GSON.fromJson(DownloadUtils.downloadString("https://launchermeta.mojang.com/mc/game/version_manifest.json"), JMinecraftVersionList.class);
            ArrayList<String> versionStringList = filter(mActivity.mVersionList.versions, new File(Tools.DIR_HOME_VERSION).listFiles());

            return versionStringList;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result)
    {
        super.onPostExecute(result);
        if(result != null && result.size() > 0) {
            mActivity.mVersionStringList = result;
            if(mActivity.mProfileEditView != null) {
                mActivity.mProfileEditView.refreshVersions();
            }
        }

    }
    
    private ArrayList<String> filter(JMinecraftVersionList.Version[] list1, File[] list2) {
        ArrayList<String> output = new ArrayList<String>();
        if(list1 != null) for (JMinecraftVersionList.Version value1: list1) {
            if ((value1.type.equals("release") && LauncherPreferences.PREF_VERTYPE_RELEASE) ||
                (value1.type.equals("snapshot") && LauncherPreferences.PREF_VERTYPE_SNAPSHOT) ||
                (value1.type.equals("old_alpha") && LauncherPreferences.PREF_VERTYPE_OLDALPHA) ||
                (value1.type.equals("old_beta") && LauncherPreferences.PREF_VERTYPE_OLDBETA)) {
                output.add(value1.id);
            }
        }

        if(list2 != null) for (File value2: list2) {
            if(value2.isDirectory()) if (!output.contains(value2.getName())) {
                output.add(value2.getName());
            }
        }

        return output;
    }
    
    private int selectAt(String[] strArr, String select) {
        int count = 0;
        for(String str : strArr){
            if (str.equals(select)) {
                return count;
            } else {
                count++;
            }
        }
        return -1;
	}
}
