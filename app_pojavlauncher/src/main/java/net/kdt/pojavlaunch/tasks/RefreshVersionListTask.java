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
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.RTSpinnerAdapter;
import net.kdt.pojavlaunch.prefs.*;
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
                    Collections.addAll(versions,list.versions);
                }
                mActivity.mVersionList = new JMinecraftVersionList();
                mActivity.mVersionList.versions = versions.toArray(new JMinecraftVersionList.Version[versions.size()]);
                Log.i("ExtVL","Final list size: " + mActivity.mVersionList.versions.length);
            }
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
        final PopupMenu popup = new PopupMenu(mActivity, mActivity.mVersionSelector);  
        popup.getMenuInflater().inflate(R.menu.menu_versionopt, popup.getMenu());  

        if(result != null && result.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, result);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            mActivity.mVersionSelector.setAdapter(adapter);
            mActivity.mVersionSelector.setSelection(selectAt(result.toArray(new String[0]), mActivity.mProfile.selectedVersion));
        } else {
            mActivity.mVersionSelector.setSelection(selectAt(mActivity.mAvailableVersions, mActivity.mProfile.selectedVersion));
        }
        PerVersionConfigDialog dialog = new PerVersionConfigDialog(this.mActivity);
        mActivity.mVersionSelector.setOnLongClickListener((v)->dialog.openConfig(mActivity.mProfile.selectedVersion));
        mActivity.mVersionSelector.setOnItemSelectedListener(new OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    String version = p1.getItemAtPosition(p3).toString();
                    mActivity.mProfile.selectedVersion = version;

                    PojavProfile.setCurrentProfile(mActivity, mActivity.mProfile);
                    if (PojavProfile.isFileType(mActivity)) {
                        try {
                            PojavProfile.setCurrentProfile(mActivity, mActivity.mProfile.save());
                        } catch (IOException e) {
                            Tools.showError(mActivity, e);
                        }
                    }

                    mActivity.mTextVersion.setText(mActivity.getString(R.string.mcl_version_msg, version));
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1)
                {
                    // TODO: Implement this method
                }
            });
        /*mActivity.mVersionSelector.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    // Implement copy, remove, reinstall,...


                    return true;
                }
            });
        */
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
                public boolean onMenuItemClick(MenuItem item) {  
                    return true;  
                }  
            });  

        mActivity.mTextVersion.setText(mActivity.getString(R.string.mcl_version_msg,mActivity.mVersionSelector.getSelectedItem()));
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
