package net.kdt.pojavlaunch.multirt;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;

import java.util.List;

public class RTSpinnerAdapter implements SpinnerAdapter {
    final Context mContext;
    final List<Runtime> mRuntimes;
    public RTSpinnerAdapter(@NonNull Context context, List<Runtime> runtimes) {
        mRuntimes = runtimes;
        Runtime runtime = new Runtime("<Default>", "", null, 0);
        mRuntimes.add(runtime);
        mContext = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public int getCount() {
        return mRuntimes.size();
    }

    @Override
    public Object getItem(int position) {
        return mRuntimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRuntimes.get(position).name.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView != null?
                convertView:
                LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent,false);

        Runtime runtime = mRuntimes.get(position);
        if(position == mRuntimes.size() - 1 ){
            ((TextView) view).setText(runtime.name);
        }else{
            ((TextView) view).setText(String.format("%s - %s",
                    runtime.name.replace(".tar.xz", ""),
                    runtime.versionString == null ? view.getResources().getString(R.string.multirt_runtime_corrupt) : runtime.versionString));
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mRuntimes.isEmpty();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }

}
