package net.kdt.pojavlaunch.modloaders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ForgeVersionListAdapter extends BaseExpandableListAdapter implements ExpandableListAdapter {
    private final List<String> mGameVersions;
    private final List<List<String>> mForgeVersions;
    private final LayoutInflater mLayoutInflater;

    public ForgeVersionListAdapter(List<String> forgeVersions, LayoutInflater layoutInflater) {
        this.mLayoutInflater = layoutInflater;
        mGameVersions = new ArrayList<>();
        mForgeVersions = new ArrayList<>();
        for(String version : forgeVersions) {
            int dashIndex = version.indexOf("-");
            String gameVersion = version.substring(0, dashIndex);
            List<String> versionList;
            int gameVersionIndex = mGameVersions.indexOf(gameVersion);
            if(gameVersionIndex != -1) versionList = mForgeVersions.get(gameVersionIndex);
            else {
                versionList = new ArrayList<>();
                mGameVersions.add(gameVersion);
                mForgeVersions.add(versionList);
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
        return mForgeVersions.get(i).size();
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
        return mForgeVersions.get(i).get(i1);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
