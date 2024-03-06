package net.kdt.pojavlaunch.modloaders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeoForgeVersionListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    private final List<String> mGameVersions;
    private final List<List<String>> mNeoForgeVersions;
    private final LayoutInflater mLayoutInflater;

    public NeoForgeVersionListAdapter(List<String> neoforgeVersions, LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
        //As the version list of NeoForge is in reverse order, it needs to be flipped before use.
        Collections.reverse(neoforgeVersions);
        mGameVersions = new ArrayList<>();
        mNeoForgeVersions = new ArrayList<>();
        for(String version : neoforgeVersions) {
            //For example, in the string "20.2.3-beta", only the substring "20.2" is needed.
            int dashIndex = version.indexOf(".", 3);
            String gameVersion = "1." + version.substring(0, dashIndex); // "1." + "20.2"
            List<String> versionList;
            int gameVersionIndex = mGameVersions.indexOf(gameVersion);
            if(gameVersionIndex != -1) versionList = mNeoForgeVersions.get(gameVersionIndex);
            else {
                versionList = new ArrayList<>();
                mGameVersions.add(gameVersion);
                mNeoForgeVersions.add(versionList);
            }
            versionList.add(version);
        }
    }

    @Override
    public int getGroupCount() {
        return mGameVersions.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mNeoForgeVersions.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return getGameVersion(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return getForgeVersion(i, i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        if(convertView == null)
            convertView = mLayoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, viewGroup, false);

        ((TextView) convertView).setText(getGameVersion(i));

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        if(convertView == null)
            convertView = mLayoutInflater.inflate(android.R.layout.simple_expandable_list_item_1, viewGroup, false);
        ((TextView) convertView).setText(getForgeVersion(i, i1));
        return convertView;
    }

    private String getGameVersion(int i) {
        return mGameVersions.get(i);
    }

    private String getForgeVersion(int i, int i1){
        return mNeoForgeVersions.get(i).get(i1);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
