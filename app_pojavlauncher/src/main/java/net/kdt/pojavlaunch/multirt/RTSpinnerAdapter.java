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
    List<Runtime> mRuntimes;
    public RTSpinnerAdapter(@NonNull Context context, List<Runtime> runtimes) {
        mRuntimes = runtimes;
        Runtime runtime = new Runtime("<Default>");
        runtime.versionString = "";
        mRuntimes.add(runtime);
        mContext = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        //STUB
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        //STUB
    }

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
                LayoutInflater.from(mContext).inflate(R.layout.item_multirt_runtime,parent,false);

        Runtime runtime = mRuntimes.get(position);

        final TextView javaVersionView = view.findViewById(R.id.multirt_view_java_version);
        final TextView fullJavaVersionView = view.findViewById(R.id.multirt_view_java_version_full);
        view.findViewById(R.id.multirt_view_removebtn).setVisibility(View.GONE);
        view.findViewById(R.id.multirt_view_setdefaultbtn).setVisibility(View.GONE);

        if(runtime.versionString != null) {
            javaVersionView.setText(mContext.getString(R.string.multirt_java_ver, runtime.name, runtime.javaVersion));
            fullJavaVersionView.setText(runtime.versionString);
        }else{
            javaVersionView.setText(runtime.name);
            fullJavaVersionView.setText(R.string.multirt_runtime_corrupt);
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
